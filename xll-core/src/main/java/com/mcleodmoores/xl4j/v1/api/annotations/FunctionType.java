/**
 * Copyright (C) 2014 - Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.xl4j.v1.api.annotations;

/**
 * Enum representing the type of Excel function to be registered.
 */
public enum FunctionType {
  /**
   * Function is a worksheet function.
   */
  FUNCTION(1),
  /**
   * Function is actually a command.
   */
  COMMAND(2);

  private int _xlValue;

  /**
   * @param xlValue  the value
   */
  FunctionType(final int xlValue) {
    _xlValue = xlValue;
  }

  /**
   * @return the integer value expected by Excel to represent this enum value
   */
  public int getExcelValue() {
    return _xlValue;
  }
}
