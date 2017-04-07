/**
 * Copyright (C) 2017 - Present McLeod Moores Software Limited.  All rights reserved.
 */
package com.mcleodmoores.xl4j.examples.timeseries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.TemporalAdjusters;

import com.mcleodmoores.xl4j.TypeConversionMode;
import com.mcleodmoores.xl4j.XLFunctions;
import com.mcleodmoores.xl4j.XLNamespace;
import com.mcleodmoores.xl4j.util.ArgumentChecker;

/**
 * Generates a start-of-month schedule of dates from the start date to end date inclusive.
 */
@XLNamespace("Schedule.")
@XLFunctions(prefix = "StartOfMonth", 
  typeConversionMode=TypeConversionMode.OBJECT_RESULT,
  description = "Generates a start-of-month schedule", 
  category = "Schedule")
public class StartOfMonthScheduleFunction implements BiFunction<LocalDate, LocalDate, List<LocalDate>> {

  @Override
  public List<LocalDate> apply(final LocalDate start, final LocalDate endInclusive) {
    ArgumentChecker.notNull(start, "start");
    ArgumentChecker.notNull(endInclusive, "endInclusive");
    final List<LocalDate> result = new ArrayList<>();
    LocalDate date = start.getDayOfMonth() == 1 ? start : start.with(TemporalAdjusters.firstDayOfNextMonth());
    while (!date.isAfter(endInclusive)) {
      result.add(date);
      date = date.plusMonths(1);
    }
    return result;
  }

}
