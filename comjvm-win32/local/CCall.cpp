/*
 * Copyright 2014-present by McLeod Moores Software Limited.
 * See distribution for license.
 */

#include "stdafx.h"
#include "CCall.h"
#include "Internal.h"
#if 1
#include <xlcall.h>
#include "Framewrk.h"
#include <comdef.h>
#endif // 1

CCall::CCall(CJvm *pJvm) {
	m_pJvm = pJvm;
	// we use the TLS so we don't create blank JNI Caches if we're creating per-call CCall objects
	// e.g. for async use.
	m_pJniCache = static_cast<JniCache *>(TlsGetValue(g_dwTlsJniCacheIndex));
	if (!m_pJniCache) {
		m_pJniCache = new JniCache();
		TlsSetValue(g_dwTlsJniCacheIndex, m_pJniCache);
	} else {
		m_pJniCache->AddRef();
	}
	m_pExecutor = new CCallExecutor (this, m_pJniCache);
	m_pExecutor->AddRef (); // RC2
	IncrementActiveObjectCount ();
	m_pJvm->AddRef ();
	InitializeCriticalSection (&m_cs);
	m_lRefCount = 1;
}

CCall::~CCall () {
	assert (m_lRefCount == 0);
	DeleteCriticalSection (&m_cs);
	m_pJvm->Release ();
	m_pExecutor->Release ();
	m_pJniCache->Release ();
	m_pJniCache = NULL;
	DecrementActiveObjectCount ();
}

static HRESULT APIENTRY _call (LPVOID lpData, JNIEnv *pEnv) {
	LOGTRACE ("Entering static callback function _call");
	CCallExecutor *pExecutor = (CCallExecutor*)lpData;
	HRESULT hr = pExecutor->Run (pEnv);
	//if (SUCCEEDED (hr)) {
	//	LOGTRACE ("_call: Run returned success");
	//	hr = pExecutor->Wait ();
	//	LOGTRACE ("pExecutor->Wait() returned");
	//	Debug::print_HRESULT (hr);
	//} else {
	//	LOGTRACE ("_call: Run returned failure");
	//	Debug::print_HRESULT (hr);
	//	pExecutor->Release ();
	//}
	return hr;
}

HRESULT STDMETHODCALLTYPE CCall::Call (/* [out] */ VARIANT *result, /* [in] */ int iFunctionNum, /* [in] */ SAFEARRAY * args) {
	HRESULT hr;
	try {
#if 0
		CCallExecutor *pExecutor = new CCallExecutor (this, m_pJniCache);// RC1
#else
		CCallExecutor *pExecutor = m_pExecutor;
		pExecutor->AddRef ();
#endif
		pExecutor->AddRef ();
		pExecutor->SetArguments (result, iFunctionNum, args); //
		LOGTRACE ("call on safearray** about to call Execute on vm");
		hr = m_pJvm->Execute (_call, pExecutor);
		if (SUCCEEDED (hr)) {
			LOGTRACE ("vm execute succeeded");
			// The executor will release RC2
			#if 1
			hr = pExecutor->Wait ();
			#else
			/*  bool timedOut;
			do {
				const int MAX_WAIT_MILLIS = 100;
				hr = pExecutor->Wait(MAX_WAIT_MILLIS, &timedOut);
				if (timedOut) {
					XLOPER12 breakState;
					LOGTRACE("Calling out to xlAbort");
					Excel12f(xlAbort, &breakState, 0);
				}
			} while (timedOut);*/
			#endif
			LOGTRACE ("hr = %x after Wait()", hr);
		} else {
			LOGTRACE ("vm execute failed");
			// Release RC2
			pExecutor->Release ();
		}
		// Release RC1
		pExecutor->Release ();
	} catch (std::bad_alloc) {
		hr = E_OUTOFMEMORY;
	}
	LOGTRACE ("Returning hr = %x", hr);
	return hr;
}

static HRESULT APIENTRY _asynccall(LPVOID lpData, JNIEnv *pEnv) {
	LOGTRACE("Entering static callback function _asynccall");
	CCallExecutor *pExecutor = (CCallExecutor*)lpData;
	VARIANT vHandle = pExecutor->GetAsyncrhonousHandle();
	IAsyncCallResult *resultHandler = pExecutor->GetAsynchronousHandler();
	HRESULT hr = pExecutor->Run(pEnv);
	pExecutor->Wait(); // should just be released straight away
	VARIANT *vResult = pExecutor->GetResult();
	LOGTRACE("Got result from executor 0x%p, releasing executor", vResult);
	resultHandler->Complete(vHandle, vResult);
	pExecutor->Release(); // we've finished with the executor so release it for freeing.
	//if (SUCCEEDED (hr)) {
	//	LOGTRACE ("_call: Run returned success");
	//	hr = pExecutor->Wait ();
	//	LOGTRACE ("pExecutor->Wait() returned");
	//	Debug::print_HRESULT (hr);
	//} else {
	//	LOGTRACE ("_call: Run returned failure");
	//	Debug::print_HRESULT (hr);
	//	pExecutor->Release ();
	//}
	return hr;
}
HRESULT STDMETHODCALLTYPE CCall::AsyncCall(/* [in] */ IAsyncCallResult *pAsyncHandler, /* [in] */ VARIANT vAsyncHandle, /* [in] */ int iFunctionNum, /* [in] */ SAFEARRAY * args) {
	HRESULT hr;
	try {
		Debug::LOGTRACE_SAFEARRAY(args);
		CCallExecutor *pExecutor = m_pExecutor;
		pExecutor->AddRef();
		pExecutor->AddRef();
		// passing nullptr here makes the executor point it's result pointer to an
		// internal field we can use until we release the object.
		pExecutor->SetArguments(nullptr, iFunctionNum, args);
		pExecutor->SetAsynchronous(pAsyncHandler, vAsyncHandle);
		LOGTRACE("vAsyncHandle(%d) = %llu", vAsyncHandle.vt, vAsyncHandle.ullVal);
		LOGTRACE("In CCall::Call");
		Debug::LOGTRACE_SAFEARRAY(args);
		LOGTRACE("handle");
		Debug::LOGTRACE_VARIANT(&vAsyncHandle);
		hr = m_pJvm->ExecuteAsync(_asynccall, pExecutor);
		// we don't wait for it to finish.
		//pExecutor->Release();
		pExecutor->Release();
	} catch (std::bad_alloc) {
		hr = E_OUTOFMEMORY;
	}
	return hr;
}

HRESULT STDMETHODCALLTYPE CCall::QueryInterface (
	/* [in] */ REFIID riid,
	/* [iid_is][out] */ _COM_Outptr_ void __RPC_FAR *__RPC_FAR *ppvObject
	) {
	if (!ppvObject) return E_POINTER;
	if (riid == IID_IUnknown) {
		*ppvObject = static_cast<IUnknown*> (this);
	} else if (riid == IID_IScan) {
		*ppvObject = static_cast<ICall*> (this);
	} else {
		*ppvObject = NULL;
		return E_NOINTERFACE;
	}
	AddRef ();
	return S_OK;
}

ULONG CCall::AddRef () {
	return InterlockedIncrement (&m_lRefCount);
}

ULONG STDMETHODCALLTYPE CCall::Release () {
	ULONG lResult = InterlockedDecrement (&m_lRefCount);
	if (!lResult) delete this;
	return lResult;
}