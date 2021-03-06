/**
 * Copyright (C) 2017 - Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.xl4j.examples.timeseries;

import static org.testng.Assert.assertEquals;

import java.util.function.BiFunction;
import java.util.stream.IntStream;

import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;

/**
 * Unit tests for {@link Scale}.
 */
public class ScaleTest {
  private static final BiFunction<TimeSeries, Double, TimeSeries> CALCULATOR = new Scale();
  private static final double EPS = 1e-15;

  /**
   * Tests the function when there are no nulls in the series.
   */
  @Test
  public void noNullValues() {
    final int n = 100;
    final Double scale = 0.4;
    final TimeSeries ts = TimeSeries.newTimeSeries();
    IntStream.range(0, n).forEach(i -> {
      ts.put(LocalDate.now().plusDays(i), Double.valueOf(i));
    });
    final TimeSeries result = CALCULATOR.apply(ts, scale);
    assertEquals(result.size(), ts.size());
    assertEquals(result.keySet(), ts.keySet());
    IntStream.range(0, n).forEach(i -> assertEquals(result.get(LocalDate.now().plusDays(i)), i * scale, EPS));
  }

  /**
   * Tests the function when there are nulls in the series.
   */
  @Test
  public void testNullsInSeries() {
    final int n = 100;
    final Double scale = 0.4;
    final TimeSeries ts = TimeSeries.newTimeSeries();
    IntStream.range(0, n).forEach(i -> {
      final LocalDate date = LocalDate.now().plusDays(i);
      ts.put(date, i % 2 == 0 ? null : Double.valueOf(i));
    });
    final TimeSeries result = CALCULATOR.apply(ts, scale);
    assertEquals(result.size(), ts.size());
    assertEquals(result.keySet(), ts.keySet());
    IntStream.range(0, n).forEach(i -> assertEquals(result.get(LocalDate.now().plusDays(i)), i % 2 == 0 ? 0 : i * scale, EPS));
  }

}
