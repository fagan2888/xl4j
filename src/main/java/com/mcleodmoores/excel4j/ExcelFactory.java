/**
 * Copyright (C) 2014-Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.excel4j;

import com.mcleodmoores.excel4j.simulator.SimulatedExcel;

/**
 * Factory for getting instances of the Excel interface.  
 * This should not be called directly from application code, instances should be injected as required.
 */
public final class ExcelFactory {
  
  private static final String TEST_PROPERTY_NAME = "test.mode";

  private static boolean s_testMode;
  private ExcelFactory() {
    String property = System.getProperty(TEST_PROPERTY_NAME);
    if (property == null) {
      s_testMode = false;
    } else if (property.toUpperCase().startsWith("T")) {
      s_testMode = true;
    } else {
      s_testMode = false;
    }
  }
  
  /**
   * Bill Pugh singleton pattern helper class removes synchronization requirement.
   */
  private static class NativeExcelHelper {
    private static final Excel INSTANCE = new SimulatedExcel();
  }
  
  /**
   * Bill Pugh singleton pattern helper class removes synchronization requirement.
   */
  private static class MockExcelHelper {
    private static final Excel INSTANCE = new SimulatedExcel();
  }
  
  /**
   * Get an instance of the Excel interface.
   * Thread-safe.
   * @return an instance of the Excel interface.
   */
  public static Excel getInstance() {
    if (s_testMode) {
      return MockExcelHelper.INSTANCE; 
    } else {
      return NativeExcelHelper.INSTANCE;
    }
  }
  
}
