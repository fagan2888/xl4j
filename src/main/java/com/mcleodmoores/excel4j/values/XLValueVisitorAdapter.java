package com.mcleodmoores.excel4j.values;

import com.mcleodmoores.excel4j.util.Excel4JRuntimeException;

/**
 * Adapter containing method to throw Excel4JRuntimeException if a visitor is not implemented, but called.
 * @param <T> the type of data returned by the visitor
 */
public class XLValueVisitorAdapter<T> implements XLValueVisitor<T> {
  @Override
  public T visitXLString(final XLString value) {
    throw new Excel4JRuntimeException("XLString visitor not implemented");
  }

  @Override
  public T visitXLBoolean(final XLBoolean value) {
    throw new Excel4JRuntimeException("XLBoolean visitor not implemented");
  }

  @Override
  public T visitXLBigData(final XLBigData value) {
    throw new Excel4JRuntimeException("XLBigData visitor not implemented");
  }

  @Override
  public T visitXLError(final XLError value) {
    throw new Excel4JRuntimeException("XLError visitor not implemented");
  }

  @Override
  public T visitXLInteger(final XLInteger value) {
    throw new Excel4JRuntimeException("XLInteger visitor not implemented");
  }

  @Override
  public T visitXLLocalReference(final XLLocalReference value) {
    throw new Excel4JRuntimeException("XLLocalReference visitor not implemented");
  }

  @Override
  public T visitXLMissing(final XLMissing value) {
    throw new Excel4JRuntimeException("XLMissing visitor not implemented");
  }

  @Override
  public T visitXLNil(final XLNil value) {
    throw new Excel4JRuntimeException("XLNil visitor not implemented");
  }

  @Override
  public T visitXLNumber(final XLNumber value) {
    throw new Excel4JRuntimeException("XLNumber visitor not implemented");
  }

  @Override
  public T visitXLMultiReference(final XLMultiReference value) {
    throw new Excel4JRuntimeException("XLMultiReference visitor not implemented");
  }

  @Override
  public T visitXLValueRange(final XLValueRange value) {
    throw new Excel4JRuntimeException("XLValueRange visitor not implemented");
  }

}
