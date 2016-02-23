/**
 * Copyright (C) 2014-Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.excel4j.typeconvert.converters;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;

import org.testng.annotations.Test;

import com.mcleodmoores.excel4j.typeconvert.AbstractTypeConverter;
import com.mcleodmoores.excel4j.typeconvert.ExcelToJavaTypeMapping;
import com.mcleodmoores.excel4j.typeconvert.JavaToExcelTypeMapping;
import com.mcleodmoores.excel4j.util.Excel4JRuntimeException;
import com.mcleodmoores.excel4j.values.XLBoolean;
import com.mcleodmoores.excel4j.values.XLError;
import com.mcleodmoores.excel4j.values.XLInteger;
import com.mcleodmoores.excel4j.values.XLNumber;
import com.mcleodmoores.excel4j.values.XLObject;
import com.mcleodmoores.excel4j.values.XLString;
import com.mcleodmoores.excel4j.values.XLValue;

/**
 * Unit tests for {@link BigDecimalXLNumberTypeConverter}.
 */
@Test
public class BigDecimalXLNumberTypeConverterTest extends TypeConverterTests {
  /** XLNumber holding a double */
  private static final XLNumber XL_NUMBER_DOUBLE = XLNumber.of(10d);
  /** XLNumber holding a long */
  private static final XLNumber XL_NUMBER_LONG = XLNumber.of(10L);
  /** XLNumber holding an int */
  private static final XLNumber XL_NUMBER_INT = XLNumber.of(10);
  /** BigDecimal */
  private static final BigDecimal BIG_DECIMAL = BigDecimal.valueOf(10d);
  /** The converter */
  private static final AbstractTypeConverter CONVERTER = new BigDecimalXLNumberTypeConverter();
  /** The class name */
  private static final String CLASSNAME = "java.math.BigDecimal";
  //  /** Test function processor */
  //  private MockFunctionProcessor _processor;
  //  /** The heap */
  //  private Heap _heap;
  //
  //  /**
  //   * Initialization.
  //   */
  //  @BeforeTest
  //  public void init() {
  //    _processor = new MockFunctionProcessor();
  //    _heap = ExcelFactory.getInstance().getHeap();
  //  }

  /**
   * Tests that the java type is {@link BigDecimal}.
   */
  @Test
  public void testGetExcelToJavaTypeMapping() {
    assertEquals(CONVERTER.getExcelToJavaTypeMapping(), ExcelToJavaTypeMapping.of(XLNumber.class, BigDecimal.class));
  }

  /**
   * Tests that the excel type is {@link XLNumber}.
   */
  @Test
  public void testGetJavaToExcelTypeMapping() {
    assertEquals(CONVERTER.getJavaToExcelTypeMapping(), JavaToExcelTypeMapping.of(BigDecimal.class, XLNumber.class));
  }

  /**
   * Tests the expected priority.
   */
  @Test
  public void testPriority() {
    assertEquals(CONVERTER.getPriority(), 10);
  }

  /**
   * Tests that passing in a null expected {@link XLValue} is successful.
   */
  @Test
  public void testNullExpectedXLValueClass() {
    CONVERTER.toXLValue(null, BIG_DECIMAL);
  }

  /**
   * Tests that passing in a null object gives the expected exception.
   */
  @Test(expectedExceptions = Excel4JRuntimeException.class)
  public void testNullObject() {
    CONVERTER.toXLValue(XLNumber.class, null);
  }

  /**
   * Tests that passing in a null expected Java class is successful.
   */
  @Test
  public void testNullExpectedClass() {
    CONVERTER.toJavaObject(null, XL_NUMBER_DOUBLE);
  }

  /**
   * Tests that passing in a null object gives the expected exception.
   */
  @Test(expectedExceptions = Excel4JRuntimeException.class)
  public void testNullXLValue() {
    CONVERTER.toJavaObject(BigDecimal.class, null);
  }

  /**
   * Tests for the exception when the object to convert is the wrong type.
   */
  @Test(expectedExceptions = ClassCastException.class)
  public void testWrongTypeToJavaConversion() {
    CONVERTER.toJavaObject(BigDecimal.class, XLInteger.of((int) 10.));
  }

  /**
   * Tests that the expected type is ignored during conversions to Java.
   */
  @Test
  public void testWrongExpectedClassToJavaConversion() {
    assertEquals(CONVERTER.toJavaObject(Boolean.class, XL_NUMBER_DOUBLE), BIG_DECIMAL);
  }

  /**
   * Tests for the exception when the {@link XLValue} to convert is the wrong type.
   */
  @Test(expectedExceptions = ClassCastException.class)
  public void testWrongTypeToXLConversion() {
    CONVERTER.toXLValue(XLNumber.class, Double.valueOf(10));
  }

  /**
   * Tests that the expected type is ignored during conversion to a XL class.
   */
  @Test
  public void testWrongExpectedClassToXLConversion() {
    assertEquals(CONVERTER.toXLValue(XLBoolean.class, BigDecimal.ONE), XLNumber.of(1));
  }

  /**
   * Tests the conversion from a {@link BigDecimal}.
   */
  @Test
  public void testConversionFromBigDecimal() {
    final XLValue converted = (XLValue) CONVERTER.toXLValue(XL_NUMBER_DOUBLE.getClass(), BIG_DECIMAL);
    assertTrue(converted instanceof XLNumber);
    final XLNumber xlNumber = (XLNumber) converted;
    assertEquals(xlNumber.getValue(), 10, 0);
  }

  /**
   * Tests the conversion from a {@link XLNumber}.
   */
  @Test
  public void testConversionFromXLNumber() {
    Object converted = CONVERTER.toJavaObject(BigDecimal.class, XL_NUMBER_INT);
    assertTrue(converted instanceof BigDecimal);
    BigDecimal bigDecimal = (BigDecimal) converted;
    assertEquals(bigDecimal, BIG_DECIMAL);
    converted = CONVERTER.toJavaObject(BigDecimal.class, XL_NUMBER_LONG);
    assertTrue(converted instanceof BigDecimal);
    bigDecimal = (BigDecimal) converted;
    assertEquals(bigDecimal, BIG_DECIMAL);
    converted = CONVERTER.toJavaObject(BigDecimal.class, XL_NUMBER_DOUBLE);
    assertTrue(converted instanceof BigDecimal);
    bigDecimal = (BigDecimal) converted;
    assertEquals(bigDecimal, BIG_DECIMAL);
  }

  /**
   * Tests creation of BigDecimals using its constructors.
   */
  @Test
  public void testJConstruct() {
    // no no-args constructor for BigDecimal
    XLValue xlValue = PROCESSOR.invoke("JConstruct", XLString.of(CLASSNAME));
    assertTrue(xlValue instanceof XLError);
    // double constructor
    xlValue = PROCESSOR.invoke("JConstruct", XLString.of(CLASSNAME), XL_NUMBER_DOUBLE);
    assertTrue(xlValue instanceof XLObject);
    Object bigDecimalObject = HEAP.getObject(((XLObject) xlValue).getHandle());
    assertTrue(bigDecimalObject instanceof BigDecimal);
    BigDecimal bigDecimal = (BigDecimal) bigDecimalObject;
    assertEquals(bigDecimal.doubleValue(), BIG_DECIMAL.doubleValue(), 0);
    // long constructor
    xlValue = PROCESSOR.invoke("JConstruct", XLString.of(CLASSNAME), XL_NUMBER_LONG);
    assertTrue(xlValue instanceof XLObject);
    bigDecimalObject = HEAP.getObject(((XLObject) xlValue).getHandle());
    assertTrue(bigDecimalObject instanceof BigDecimal);
    bigDecimal = (BigDecimal) bigDecimalObject;
    assertEquals(bigDecimal.longValue(), BIG_DECIMAL.longValue());
    // int constructor
    xlValue = PROCESSOR.invoke("JConstruct", XLString.of(CLASSNAME), XL_NUMBER_INT);
    assertTrue(xlValue instanceof XLObject);
    bigDecimalObject = HEAP.getObject(((XLObject) xlValue).getHandle());
    assertTrue(bigDecimalObject instanceof BigDecimal);
    bigDecimal = (BigDecimal) bigDecimalObject;
    assertEquals(bigDecimal.intValue(), BIG_DECIMAL.intValue());
  }

  /**
   * Tests creation of BigDecimals using its static constructors.
   */
  @Test
  public void testJMethod() {
    // no BigDecimal.valueOf(String)
    XLValue xlValue = PROCESSOR.invoke("JStaticMethodX", XLString.of(CLASSNAME), XLString.of("valueOf"), XLString.of("10"));
    assertTrue(xlValue instanceof XLError);
    // BigDecimal.valueOf(double)
    xlValue = PROCESSOR.invoke("JStaticMethodX", XLString.of(CLASSNAME), XLString.of("valueOf"), XL_NUMBER_DOUBLE);
    assertTrue(xlValue instanceof XLObject);
    Object bigDecimalObject = HEAP.getObject(((XLObject) xlValue).getHandle());
    assertTrue(bigDecimalObject instanceof BigDecimal);
    BigDecimal bigDecimal = (BigDecimal) bigDecimalObject;
    assertEquals(bigDecimal.doubleValue(), BIG_DECIMAL.doubleValue(), 0);
    // BigDecimal.valueOf(long)
    xlValue = PROCESSOR.invoke("JStaticMethodX", XLString.of(CLASSNAME), XLString.of("valueOf"), XL_NUMBER_LONG);
    assertTrue(xlValue instanceof XLObject);
    bigDecimalObject = HEAP.getObject(((XLObject) xlValue).getHandle());
    assertTrue(bigDecimalObject instanceof BigDecimal);
    bigDecimal = (BigDecimal) bigDecimalObject;
    assertEquals(bigDecimal.longValue(), BIG_DECIMAL.longValue());
    // BigDecimal.valueOf(long, int)
    xlValue = PROCESSOR.invoke("JStaticMethodX", XLString.of(CLASSNAME), XLString.of("valueOf"), XL_NUMBER_INT);
    assertTrue(xlValue instanceof XLObject);
    bigDecimalObject = HEAP.getObject(((XLObject) xlValue).getHandle());
    assertTrue(bigDecimalObject instanceof BigDecimal);
    bigDecimal = (BigDecimal) bigDecimalObject;
    assertEquals(bigDecimal.intValue(), BIG_DECIMAL.intValue());
  }
}
