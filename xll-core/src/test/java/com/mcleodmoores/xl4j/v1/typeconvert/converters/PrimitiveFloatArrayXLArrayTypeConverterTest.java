/**
 * Copyright (C) 2014-Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.xl4j.v1.typeconvert.converters;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.mcleodmoores.xl4j.v1.api.typeconvert.AbstractTypeConverter;
import com.mcleodmoores.xl4j.v1.api.typeconvert.ExcelToJavaTypeMapping;
import com.mcleodmoores.xl4j.v1.api.typeconvert.JavaToExcelTypeMapping;
import com.mcleodmoores.xl4j.v1.api.values.XLArray;
import com.mcleodmoores.xl4j.v1.api.values.XLBoolean;
import com.mcleodmoores.xl4j.v1.api.values.XLNumber;
import com.mcleodmoores.xl4j.v1.api.values.XLString;
import com.mcleodmoores.xl4j.v1.api.values.XLValue;
import com.mcleodmoores.xl4j.v1.util.XL4JRuntimeException;

/**
 * Unit tests for {@link PrimitiveFloatArrayXLArrayTypeConverter}.
 */
@Test
public class PrimitiveFloatArrayXLArrayTypeConverterTest {
  /** The expected priority */
  private static final int EXPECTED_PRIORITY = 10;
  /** The converter. */
  private static final AbstractTypeConverter CONVERTER = new PrimitiveFloatArrayXLArrayTypeConverter();

  /**
   * Tests that the java type is float[].
   */
  @Test
  public void testGetExcelToJavaTypeMapping() {
    assertEquals(CONVERTER.getExcelToJavaTypeMapping(), ExcelToJavaTypeMapping.of(XLArray.class, float[].class));
  }

  /**
   * Tests that the excel type is {@link XLArray}.
   */
  @Test
  public void testGetJavaToExcelTypeMapping() {
    assertEquals(CONVERTER.getJavaToExcelTypeMapping(), JavaToExcelTypeMapping.of(float[].class, XLArray.class));
  }

  /**
   * Tests the expected priority.
   */
  @Test
  public void testPriority() {
    assertEquals(CONVERTER.getPriority(), EXPECTED_PRIORITY);
  }

  /**
   * Tests that passing in a null object gives the expected exception.
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testNullObject() {
    CONVERTER.toXLValue(null);
  }

  /**
   * Tests that passing in a null object gives the expected exception.
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testNullXLValue() {
    CONVERTER.toJavaObject(float[].class, null);
  }

  /**
   * Tests for the exception when the object to convert is the wrong type.
   */
  @Test(expectedExceptions = ClassCastException.class)
  public void testWrongTypeToJavaConversion() {
    CONVERTER.toJavaObject(float[].class, new Float[] {10f});
  }

  /**
   * Tests that passing in an object to convert that is not an array fails.
   */
  @Test(expectedExceptions = ClassCastException.class)
  public void testWrongTypeToXLConversion() {
    CONVERTER.toXLValue(true);
  }

  /**
   * Tests the behaviour when there is no available converter to a float.
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testNoConverterToFloatRow() {
    final XLArray xlArray = XLArray.of(new XLValue[][] {new XLValue[] {XLBoolean.FALSE, XLBoolean.FALSE}});
    CONVERTER.toJavaObject(float[].class, xlArray);
  }

  /**
   * Tests the behaviour when there is no available converter to a float.
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testNoConverterToFloatColumn() {
    final XLArray xlArray = XLArray.of(new XLValue[][] {new XLValue[] {XLBoolean.FALSE}, new XLValue[]{XLBoolean.FALSE}});
    CONVERTER.toJavaObject(float[].class, xlArray);
  }

  /**
   * Tests the conversion from XLArray containing a single row where the input contains only XLNumber.
   */
  @Test
  public void testToJavaConversionFromRow1() {
    final XLArray xlArray = XLArray.of(new XLValue[][] {new XLValue[] {XLNumber.of(10), XLNumber.of(20), XLNumber.of(30)}});
    final Object converted = CONVERTER.toJavaObject(float[].class, xlArray);
    final float[] array = (float[]) converted;
    assertEquals(array, new float[] {10, 20, 30});
  }

  /**
   * Tests the conversion from XLArray containing a single row where the input contains a mixture of types.
   */
  @Test
  public void testToJavaConversionFromRow2() {
    final XLArray xlArray = XLArray.of(new XLValue[][] {new XLValue[] {XLNumber.of(10), XLString.of("20"), XLNumber.of(30)}});
    final Object converted = CONVERTER.toJavaObject(float[].class, xlArray);
    final float[] array = (float[]) converted;
    assertEquals(array, new float[] {10, 20, 30});
  }

  /**
   * Tests the conversion from XLArray containing a single column where the input contains only XLNumber.
   */
  @Test
  public void testToJavaConversionFromColumn1() {
    final XLArray xlArray = XLArray.of(new XLValue[][] {new XLValue[] {XLNumber.of(10)}, new XLValue[] {XLNumber.of(20)}, new XLValue[] {XLNumber.of(30)}});
    final Object converted = CONVERTER.toJavaObject(float[].class, xlArray);
    final float[] array = (float[]) converted;
    assertEquals(array, new float[] {10, 20, 30});
  }

  /**
   * Tests the conversion from XLArray containing a single column where the input contains a mixture of types.
   */
  @Test
  public void testToJavaConversionFromColumn2() {
    final XLArray xlArray = XLArray.of(new XLValue[][] {new XLValue[] {XLNumber.of(10)}, new XLValue[] {XLString.of("20")}, new XLValue[] {XLNumber.of(30)}});
    final Object converted = CONVERTER.toJavaObject(float[].class, xlArray);
    final float[] array = (float[]) converted;
    assertEquals(array, new float[] {10, 20, 30});
  }

  /**
   * Tests the conversion from float[].
   */
  @Test
  public void testToXLConversionFrom1dPrimitiveFloatArray() {
    final float[] array = new float[] {10, 20, 30};
    final XLValue converted = (XLValue) CONVERTER.toXLValue(array);
    assertTrue(converted instanceof XLArray);
    final XLArray xlArray = (XLArray) converted;
    assertEquals(xlArray, XLArray.of(new XLValue[][] {new XLValue[] {XLNumber.of(10), XLNumber.of(20), XLNumber.of(30)}}));
  }

}
