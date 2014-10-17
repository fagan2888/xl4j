#include "stdafx.h"
#include "Jvm.h"
#include "helper/ClasspathUtils.h"

COMJVM_EXCEL_API Jvm::Jvm () {
	HRESULT hr = ComJvmCreateLocalConnector (&m_pConnector);
	if (FAILED (hr)) {
		TRACE ("CreateLocalConnector failed");
		_com_raise_error (hr);
	}
	hr = m_pConnector->Lock ();
	if (FAILED (hr)) {
		TRACE ("connector Lock could not be aquired");
		_com_raise_error (hr);
	}

	IJvmTemplate *pTemplate;

	hr = ComJvmCreateTemplate (NULL, &pTemplate);
	if (FAILED (hr)) {
		TRACE ("could not create template");
		_com_raise_error (hr);
	}
	IClasspathEntries *entries;
	hr = pTemplate->get_Classpath (&entries);
	if (FAILED (hr)) {
		TRACE ("could not get template classpath");
		_com_raise_error (hr);
	}

	ClasspathUtils::AddEntries (entries, TEXT ("..\\lib\\"));
	hr = m_pConnector->CreateJvm (pTemplate, NULL, &m_pJvm);
	if (FAILED (hr)) {
		_com_error err (hr);
		LPCTSTR errMsg = err.ErrorMessage ();
		TRACE ("could not create JVM: %s", errMsg);
		_com_raise_error (hr);
	}
	TRACE ("Created JVM!");
	m_pConnector->Unlock ();
	TRACE ("Unlocked connector");

	hr = pTemplate->Release ();
	if (FAILED (hr)) {
		TRACE ("Could not release template");
		_com_raise_error (hr);
	}
}

COMJVM_EXCEL_API Jvm::~Jvm () {
	m_pJvm->Release ();
	m_pConnector->Release ();
}

COMJVM_EXCEL_API ULONG Jvm::AddRef () {
	return InterlockedIncrement (&m_lRefCount);
}

COMJVM_EXCEL_API ULONG Jvm::Release () {
	ULONG lResult = InterlockedDecrement (&m_lRefCount);
	if (!lResult) delete this;
	return lResult;
}