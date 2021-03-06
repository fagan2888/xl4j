/**
 * Copyright (C) 2014-Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.xl4j.v1;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.mcleodmoores.xl4j.v1.api.values.XLRange;
import com.mcleodmoores.xl4j.v1.util.XL4JRuntimeException;

/**
 * Unit tests for XLRange.
 */
public class XLRangeTests {

  /**
   *
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testFirstArgNegative() {
    XLRange.of(-1, 100, 2, 3);
  }

  /**
   *
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testSecondArgNegative() {
    XLRange.of(1, -100, 2, 3);
  }

  /**
   *
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testThirdArgNegative() {
    XLRange.of(1, 100, -2, 3);
  }

  /**
   *
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testForthArgNegative() {
    XLRange.of(1, 100, 2, -3);
  }

  /**
   *
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testOfCellFirstNegative() {
    XLRange.ofCell(-100, 3);
  }

  /**
   *
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testOfCellSecondNegative() {
    XLRange.ofCell(100, -3);
  }

  /**
   *
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testFirstGtSecond() {
    XLRange.of(101, 100, 2, 3);
  }

  /**
   *
   */
  @Test(expectedExceptions = XL4JRuntimeException.class)
  public void testThirdGtForth() {
    XLRange.of(100, 101, 5, 3);
  }

  /**
   *
   */
  @Test
  public void testGetters() {
    final XLRange range = XLRange.of(1, 2, 3, 4);
    Assert.assertEquals(range.getRowFirst(), 1);
    Assert.assertEquals(range.getRowLast(), 2);
    Assert.assertEquals(range.getColumnFirst(), 3);
    Assert.assertEquals(range.getColumnLast(), 4);
  }

  /**
   *
   */
  @Test
  public void testSingleCol() {
    final XLRange range = XLRange.of(100, 100, 3, 5);
    Assert.assertFalse(range.isSingleColumn());
    Assert.assertTrue(range.isSingleRow());
    Assert.assertFalse(range.isSingleCell());
  }

  /**
   *
   */
  @Test
  public void testSingleRow() {
    final XLRange range = XLRange.of(100, 104, 3, 3);
    Assert.assertFalse(range.isSingleRow());
    Assert.assertTrue(range.isSingleColumn());
    Assert.assertFalse(range.isSingleCell());
  }

  /**
   *
   */
  @Test
  public void testSingleCell() {
    final XLRange range = XLRange.of(100, 100, 3, 3);
    Assert.assertTrue(range.isSingleRow());
    Assert.assertTrue(range.isSingleColumn());
    Assert.assertTrue(range.isSingleCell());
  }

  /**
   *
   */
  @Test
  public void testSingleCellOfCell() {
    final XLRange range = XLRange.ofCell(100, 3);
    Assert.assertTrue(range.isSingleRow());
    Assert.assertTrue(range.isSingleColumn());
    Assert.assertTrue(range.isSingleCell());
  }

  // CHECKSTYLE:OFF
  /**
   *
   */
  @Test
  public void testEqualsAndHashCode() {
    final XLRange range = XLRange.of(0, 15, 0, 15);
    final XLRange range_1 = XLRange.of(1, 15, 0, 15);
    final XLRange range_2 = XLRange.of(0, 16, 0, 15);
    final XLRange range_3 = XLRange.of(0, 15, 1, 15);
    final XLRange range_4 = XLRange.of(0, 15, 0, 16);
    Assert.assertNotEquals(range, range_1);
    Assert.assertNotEquals(range.hashCode(), range_1.hashCode());
    Assert.assertNotEquals(range, range_2);
    Assert.assertNotEquals(range.hashCode(), range_2.hashCode());
    Assert.assertNotEquals(range, range_3);
    Assert.assertNotEquals(range.hashCode(), range_3.hashCode());
    Assert.assertNotEquals(range, range_4);
    Assert.assertNotEquals(range.hashCode(), range_4.hashCode());
    // identity
    Assert.assertEquals(range, range);
    Assert.assertEquals(range.hashCode(), range.hashCode());
    final XLRange rangeClone = XLRange.of(0, 15, 0, 15);
    Assert.assertEquals(range, rangeClone);
    Assert.assertEquals(range.hashCode(), rangeClone.hashCode());
    // null, other
    Assert.assertNotEquals(null, range);
    Assert.assertNotEquals("Hello World", range);
  }

  /**
   *
   */
  @Test
  public void testOfCellEquivalent() {
    final XLRange range = XLRange.of(15, 15, 20, 20);
    final XLRange rangeOfCell = XLRange.ofCell(15, 20);
    Assert.assertEquals(range, rangeOfCell);
  }

  private static final String SINGLE_ROW = "XLRange[Single Row row=100, columns=3 to 5]";
  private static final String SINGLE_COL = "XLRange[Single Column rows=100 to 104, column=3]";
  private static final String SINGLE_CELL = "XLRange[Single Cell row=100, column=3]";
  private static final String FULL_RANGE = "XLRange[Range rows=10 to 45, columns=0 to 30]";

  /**
   *
   */
  @Test
  public void testXLRangeToString() {
    final XLRange singleRow = XLRange.of(100, 100, 3, 5);
    Assert.assertEquals(singleRow.toString(), SINGLE_ROW);
    final XLRange singleCol = XLRange.of(100, 104, 3, 3);
    Assert.assertEquals(singleCol.toString(), SINGLE_COL);
    final XLRange singleCell = XLRange.of(100, 100, 3, 3);
    Assert.assertEquals(singleCell.toString(), SINGLE_CELL);
    final XLRange singleCell2 = XLRange.ofCell(100, 3);
    Assert.assertEquals(singleCell2.toString(), SINGLE_CELL);
    final XLRange fullRange = XLRange.of(10, 45, 0, 30);
    Assert.assertEquals(fullRange.toString(), FULL_RANGE);
  }
}
