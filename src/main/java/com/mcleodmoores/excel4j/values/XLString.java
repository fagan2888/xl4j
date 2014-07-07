package com.mcleodmoores.excel4j.values;

import com.mcleodmoores.excel4j.util.ArgumentChecker;

/**
 * Java representation of the xloper type xltypeStr.
 * Holds an Excel String.
 */
public final class XLString implements XLValue {

  private String _value;
  
  private XLString(final String value) {
    _value = value;
  }
  
  /**
   * Static factory method to create an instance of an XLString.
   * @param value the string
   * @return an instance
   */
  public static XLString of(final String value) {
    ArgumentChecker.notNull(value, "value");
    return new XLString(value);
  }
  
  /**
   * @return the value
   */
  public String getValue() {
    return _value;
  }
  
  @Override
  public <E> E accept(final XLValueVisitor<E> visitor) {
    return visitor.visitXLString(this);
  }

  @Override
  public int hashCode() {
    return _value.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof XLString)) {
      return false;
    }
    XLString other = (XLString) obj;
    if (!_value.equals(other._value)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "XLString[value=" + _value + "]";
  }

}