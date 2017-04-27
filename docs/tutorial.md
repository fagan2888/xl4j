XL4J Tutorial
=============

# Getting started
## Pre-requisites
You can build XL4J Add-ins on any operating system that supports Java: you *do not need Windows or Visual Studio* unless you
want to modify the native parts of the Add-in, which is unlikely for most users.  Being able to build from Linux or MacOS
can prove very useful later on for e.g. continuous integration, but given that you're likely to want to test and use your 
Add-in in Excel, it is probably advisable to start development there.  Windows 10 is recommended, but any version of 
Windows later than Vista (7, 8, 8.1, 10, 10 Anniversary Update) should be fine.

You will need to install a current JDK and a version of Maven after 3.1.  You should also have a git client installed.  I
would suggest using the [Chocolatey](https://www.chocolatey.org) package manager, which can install these packages for you 
using:
```
choco install jdk8 maven git
```
but it's fine to just download and install them yourself too.  You'll probably want your favorite IDE too.  If you want to 
be able to actually test your Add-ins, you will probably want to install Excel too!  Any version after (not including) 
Excel 2007 is fine.

Note that you MUST have a 32-bit Java runtime (JRE) installed for XL4J to work.  I have found Chocolatey can be a bit hit and miss
at installing this correctly, so I'd recommend downloading the i586 version of the JRE yourself and installing manually.

## Cloning the template project
We've provided a template project that you can use to get started.  It's very basic, and just consists of a Maven POM which pulls 
in the required libraries and a Maven assembly plug-in manifest which describes how to package the resulting Add-in into both a 
directory (so you can use it directly) and a zip file for distribution.

Start by cloning the project:
```
git clone https://github.com/McLeodMoores:xl4j-template.git
```
Inside you'll find a standard Maven project layout.  Now is a good time to import this project into your IDE of choice, or
fire up a text editor.  

## Adding a new custom Excel function
Try adding this class:
``` java
package com.mcleodmoores.xl4j.template;

import com.mcleodmoores.xl4j.XLFunction;

public final class MyFunctions {

  @XLFunction(name = "MyAdd", description = "A simple function", category = "XL4J")
  public static double myadd(
      @XLParameter(name = "One", description = "The first number") final double one,
      @XLParameter(name = "Two", description = "The second number") final double two) {
    return one + two;
  }
  
}
```
## Build the Add-in
Then build using:
```
mvn install
```
This will pull in the required libraries and produce the build artifact.  Have a look in `xl4j-template\target` and you should see two 
files:
 - A folder called `xl4j-template-0.0.1-SNAPSHOT-distribution`
 - A zip file containing that folder called `xl4j-template-0.0.1-SNAPSHOT-distribution.zip`
 
## Manually add the Add-in to Excel
 1. Start Excel.
 2. On the Backstage (the screen revealed by clicking on the `File` ribbon header or Office button), choose Options.
 3. Go to `Add-ins`
 4. Next to the dropdown list towards the botton called `Manage:`, select `Excel Add-ins` and click `Go`.
 5. Click `Browse`, navigate to the `xl4j-template-0.0.1-SNAPSHOT-distribution` folder, open the folder containing the appropriate distribution and select the add-in.
 6. Click `OK`. You should see a popup and a splashscreen once you've clicked `OK`.
 7. Start typing in your function name. Excel will autocomplete the name:
    ![First function image](https://github.com/McLeodMoores/xl4j/blob/master/docs/images/first-function-1.png)
 
    Or you can use the function wizard:
    ![Second function image](https://github.com/McLeodMoores/xl4j/blob/master/docs/images/first-function-2.png)
 
    ![Third function image](https://github.com/McLeodMoores/xl4j/blob/master/docs/images/first-function-3.png)
 
 ##TODO add an image of the argument wizard when it's fixed

# Using Java projects from Excel
## A simple wrapper
 
In this example, we are going to write a layer that allows the **TODO link** starling implementation of the ISDA CDS model to be used from Excel. To get pricing and risk metrics, we need 
  - A yield curve
  - A CDS curve
  - A CDS trade definition
  
Just for reference, here is some example Java code that constructs a yield curve using the starling library:
  
  ``` java
  /** The trade date */
  private static final LocalDate TRADE_DATE = LocalDate.of(2016, 10, 3);
  /** The quotes */
  private static final double[] QUOTES = new double[] {0.001, 0.0011, 0.0012, 0.002, 0.0035,
      0.006, 0.01, 0.015, 0.025, 0.04};
  /** The money market day count */
  private static final String MONEY_MARKET_DAY_COUNT = "ACT/360";
  /** The swap day count */
  private static final String SWAP_DAY_COUNT = "ACT/365";
  /** The swap interval */
  private static final String SWAP_INTERVAL = "6M";
  /** The curve day count */
  private static final String CURVE_DAY_COUNT = "ACT/365";
  /** The business day convention */
  private static final String BDC = "Modified Following";
  /** The spot date */
  private static final LocalDate SPOT_DATE = LocalDate.of(2016, 10, 5);
  /** The number of spot days */
  private static final int SPOT_DAYS = 2;
  /** The holidays */
  private static final LocalDate[] HOLIDAYS = new LocalDate[] {LocalDate.of(2016, 8, 1)};
  /** The calendar */
  private static final Calendar CALENDAR =
      new CalendarAdapter(new SimpleWorkingDayCalendar("Calendar", Arrays.asList(LocalDate.of(2016, 8, 1)), DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
  /** The yield curve */
  private static final ISDACompliantYieldCurve YIELD_CURVE;

  static {
    final ISDAInstrumentTypes[] instrumentTypes = new ISDAInstrumentTypes[] {
        ISDAInstrumentTypes.MoneyMarket, ISDAInstrumentTypes.MoneyMarket, ISDAInstrumentTypes.MoneyMarket, ISDAInstrumentTypes.Swap,
        ISDAInstrumentTypes.Swap, ISDAInstrumentTypes.Swap, ISDAInstrumentTypes.Swap, ISDAInstrumentTypes.Swap, ISDAInstrumentTypes.Swap,
        ISDAInstrumentTypes.Swap};
    final Period[] tenors = new Period[] {Period.ofMonths(3), Period.ofMonths(6), Period.ofMonths(9),
        Period.ofYears(1), Period.ofYears(2), Period.ofYears(3), Period.ofYears(4), Period.ofYears(5), Period.ofYears(7),
        Period.ofYears(10)};
    final ISDACompliantYieldCurveBuild builder = new ISDACompliantYieldCurveBuild(TRADE_DATE, SPOT_DATE, instrumentTypes, tenors,
        DayCountFactory.INSTANCE.instance(MONEY_MARKET_DAY_COUNT), DayCountFactory.INSTANCE.instance(SWAP_DAY_COUNT), Period.ofMonths(6),
        DayCountFactory.INSTANCE.instance(CURVE_DAY_COUNT), BusinessDayConventionFactory.INSTANCE.instance(BDC),
        CALENDAR);
    CURVE = builder.build(QUOTES);
    }
  ```
The curve is built from convention information (day-counts, payment intervals, etc.), a list of instruments used in the curve, the tenors of these instruments, a working day calendar, and market data. As conventions are defined on a per-currency basis, we are going to bundle all of the convention information into a class and then add functions that allow these objects to be constructed in Excel. This means that conventions can be stored in Excel tables.
 
** TODO links to all of the classes referenced **
### The conventions
We've added two POJOs, ```IdsaYieldCurveConvention``` and ```IsdaCdsConvention``` that contain all of the convention information for yield curves and CDS, and a utility class, ```ConventionFunctions```, that contains static Excel functions that build these objects.
 
The yield curve convention builder is simple: all fields are required and can be represented as ```String``` or ```int```. 
This method takes Excel types (```XLString, XLNumber```) as arguments, which means that there is no type conversion done on the objects coming from the add-in.

 ``` java
   @XLFunction(name = "ISDAYieldCurveConvention", category = "ISDA CDS model", description = "Create a yield curve convention")
  public static IsdaYieldCurveConvention buildYieldCurveConvention(
      @XLParameter(description = "Money Market Day Count", name = "Money Market Day Count") final XLString xlMoneyMarketDayCountName,
      @XLParameter(description = "Swap Day Count", name = "Swap Day Count") final XLString xlSwapDayCountName,
      @XLParameter(description = "Swap Interval", name = "Swap Interval") final XLString xlSwapIntervalName,
      @XLParameter(description = "Curve Day Count", name = "Curve Day Count") final XLString xlCurveDayCountName,
      @XLParameter(description = "Business Day Convention", name = "Business Day Convention") final XLString xlBusinessDayConventionName,
      @XLParameter(description = "Spot Days", name = "spotDays") final XLNumber xlSpotDays) {
    final DayCount moneyMarketDayCount = DayCountFactory.INSTANCE.instance(xlMoneyMarketDayCountName.getValue());
    final DayCount swapDayCount = DayCountFactory.INSTANCE.instance(xlSwapDayCountName.getValue());
    final DayCount curveDayCount = DayCountFactory.INSTANCE.instance(xlCurveDayCountName.getValue());
    final BusinessDayConvention businessDayConvention = BusinessDayConventionFactory.INSTANCE.instance(xlBusinessDayConventionName.getValue());
    final Period swapInterval = parsePeriod(xlSwapIntervalName.getValue());
    return new IsdaYieldCurveConvention(moneyMarketDayCount, swapDayCount, swapInterval, curveDayCount, businessDayConvention, xlSpotDays.getAsInt());
  }
```
The ```@XLFunction``` annotation means that this method is available from Excel. The fields are self-explanatory: the name of the function, a more detailed description and the category that the function will appear in. None of these properties are mandatory - the default value for the name is the method name, the class name for the category and an empty description if they are not filled in. There are other properties that will be discussed in more detail later. The ```@XLParameter``` properties have the same meaning as those in the function annotation.
 
After this function is built, it can be called from Excel
 
![First yield curve convention image](https://github.com/McLeodMoores/xl4j/blob/master/docs/images/yield-curve-convention-1.png)
 
![Second yield curve convention image](https://github.com/McLeodMoores/xl4j/blob/master/docs/images/yield-curve-convention-2.png)

 
The CDS convention builder is very similar to the yield curve convention builder, but some fields are optional:
 ``` java
   @XLFunction(name = "ISDACDSConvention", category = "ISDA CDS model", description = "Create a CDS convention")
  public static IsdaCdsConvention buildCdsConvention(
      @XLParameter(description = "Accrual Day Count", name = "Accrual Day Count") final XLString xlAccrualDayCountName,
      @XLParameter(description = "Curve Day Count", name = "Curve Day Count") final XLString xlCurveDayCountName,
      @XLParameter(description = "Business Day Convention", name = "Business Day Convention") final XLString xlBusinessDayConventionName,
      @XLParameter(description = "Coupon Interval", name = "Coupon Interval", optional = true) final XLString xlCouponInterval,
      @XLParameter(description = "Stub Type", name = "Stub Type", optional = true) final XLString xlStubType,
      @XLParameter(description = "Cash Settlement Days", name = "Cash Settlement Days", optional = true) final XLNumber xlCashSettlementDays,
      @XLParameter(description = "Step In Days", name = "Step In Days", optional = true) final XLNumber xlStepInDays,
      @XLParameter(description = "Pay Accrual On Default", name = "Pay Accrual On Default", optional = true) final XLBoolean xlPayAccrualOnDefault) {
    final String stubType = xlStubType == null ? null : xlStubType.getValue();
    final Integer cashSettlementDays = xlCashSettlementDays == null ? null : xlCashSettlementDays.getAsInt();
    final Integer stepInDays = xlStepInDays == null ? null : xlStepInDays.getAsInt();
    final Boolean payAccrualOnDefault = xlPayAccrualOnDefault == null ? null : xlPayAccrualOnDefault.getValue();
    return new IsdaCdsConvention(xlAccrualDayCountName.getValue(), xlCurveDayCountName.getValue(), xlBusinessDayConventionName.getValue(),
        xlCouponInterval.getValue(), stubType, cashSettlementDays, stepInDays, payAccrualOnDefault);
  }
 ```
As optional values are passed in as nulls, it's necessary to test for them and handle appropriately. Optional values are dealt with in the usual Excel way by leaving the argument blank in the formula:

![CDS convention image](https://github.com/McLeodMoores/xl4j/blob/master/docs/images/cds-convention.png)

 The next stage is to actually construct the yield and CDS curves. Again, the wrapper class has been written with various static methods that call into the starling code. For these methods, the arguments are standard Java objects (e.g. double[]) rather than ```XLValue```, which means that type conversion is performed automatically. This means that we don't have to think about how to deal with arrays (what if the data were passed in as a row? a column?), as well as cutting down on the boilerplate of unwrapping and casting these objects to the types that are needed.
 
 ``` java
   @XLFunction(name = "ISDAYieldCurve.BuildCurveFromConvention", category = "ISDA CDS model",
      description = "Build a yield curve using the ISDA methodology")
  public static ISDACompliantYieldCurve buildYieldCurve(
      @XLParameter(description = "Trade Date", name = "Trade Date") final LocalDate tradeDate,
      @XLParameter(description = "Instrument Types", name = "Instrument Types") final String[] instrumentTypeNames,
      @XLParameter(description = "Tenors", name = "Tenors") final String[] tenors,
      @XLParameter(description = "Quotes", name = "Quotes") final double[] quotes,
      @XLParameter(description = "Convention", name = "Convention") final IsdaYieldCurveConvention convention,
      @XLParameter(description = "Spot Date", name = "Spot Date", optional = true) final LocalDate spotDate,
      @XLParameter(description = "Holidays", name = "Holidays", optional = true) final LocalDate[] holidayDates) {
 ```
 This class contains two methods that construct yield curves; one that takes a convention object and another that constructs the convention itself. Note that although the methods are overloaded in the Java code, as you'd expect, the names of the functions are different. All function names in Excel must be unique; if a function has already been registered with the name, this is logged and the function ignored.
 
 
 
 ## Starting from scratch
 ## Using existing code (2)
 