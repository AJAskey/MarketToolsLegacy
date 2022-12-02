/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Original author : Andy Askey (ajaskey34@gmail.com)
 */

package net.ajaskey.market.tools.options.workbench;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;

import net.ajaskey.common.DateTime;

/**
 *
 * <p>
 * This software was derived from a github project from M. Bret Blackford.
 *
 * https://github.com/bret-blackford/black-scholes/tree/master/...
 * OptionValuation/src/mBret/options
 * </p>
 *
 * <p>
 * The Black/Scholes formula, which gives a theoretical estimate of the price of
 * European-style options.
 *
 * The model was first articulated by Fischer Black and Myron Scholes in their
 * 1973 paper, "The Pricing of Options and Corporate Liabilities", published in
 * the Journal of Political Economy. They derived a stochastic partial
 * differential equation, now called the Black?Scholes equation, which governs
 * the price of the option over time.
 *
 * Delta - measures the rate of change of option value with respect to changes
 * in the underlying asset's price.
 *
 * Gamma - measures the rate of change in the delta with respect to changes in
 * the underlying price.
 *
 * Vega - measures sensitivity to volatility. Vega is typically expressed as the
 * amount of money per underlying share that the option's value will gain or
 * lose as volatility rises or falls by 1%.
 *
 * Theta - measures the sensitivity of the value of the derivative to the
 * passage of time: the "time decay."
 *
 * Rho - measures sensitivity to the interest rate.
 *
 * mblackford - M. Bret Blackford (credit to Dhruba Bandopadhyay)
 * </p>
 *
 */
public class OptionsProcessor {

  public final static int ACALL = 2;
  public final static int APUT  = 1;
  //
  static String gDbgStr;
  //
  final static double         ONEDAY = 1.0 / 365.0;
  private static final double B1     = 0.319381530;
  private static final double B2     = -0.356563782;
  private static final double B3     = 1.781477937;
  private static final double B4     = -1.821255978;
  private static final double B5     = 1.330274429;
  private static final double opRate = 0.000001;
  // private static final double skew = 0.0295;
  /**
   * @author M. Bret Blackford
   *
   *         For Black-Scholes calculations.
   */
  private static final double P         = 0.2316419;
  private final static double p2        = Math.sqrt(2.0 * Math.PI);
  /**
   * Within one cent.
   */
  private static final double precision = 0.0149;

  /**
   *
   * Used as a simple example
   *
   * @param args
   * @throws ParseException
   * @throws FileNotFoundException
   */
  public static void main(final String[] args) throws ParseException, FileNotFoundException {
    new DateTime();
    final DateTime expiry = new DateTime(2020, DateTime.JUNE, 20);
    // final OptionsProcessor op = new OptionsProcessor(OptionsProcessor.ACALL,
    // "TestOpt", "Test CALL", 300.0, 301.00, expiry, 0.01,
    // true);
    // op.CloseDebug();
//      expiry.add(DateTime.DATE, 25);
    final double iv = 0.3474 * 1.029;
    final OptionsProcessor op2 = new OptionsProcessor(OptionsProcessor.APUT, "TestOpt", "Test PUT", 267.0, 297.12, expiry, iv, true);
    op2.CloseDebug();
  }

  /**
   *
   * @param op
   * @param type
   * @param id
   * @param strike
   * @param ulPrice
   * @param expiry
   * @param bDate
   * @param iv
   * @return
   */
  private static OptionsProcessor build(OptionsProcessor op, int type, String ticker, String id, double strike, double ulPrice, DateTime expiry,
      DateTime bDate, double iv, boolean dbg) {
    try {
      op.code = ticker;
      op.id = id;
      op.dataType = type;
      op.strike = strike;
      op.ulPrice = ulPrice;
      op.expiry = new DateTime(expiry);
      op.iv = iv;
      op.delta = 0.0;
      op.gamma = 0.0;
      op.theta = 0.0;
      op.rho = 0.0;
      op.vega = 0.0;
      op.price = 0.0;
      op.rate = OptionsProcessor.opRate;
      op.setSellDate(bDate);
      op.years = op.expiry.getDeltaYears(op.getSellDate());
      if (op.years <= 0.0) {
        op.years = OptionsProcessor.ONEDAY / 2.0;
      }
      op.days = (int) (op.years * 365.0) + 1;
      //
      op.DEBUG = dbg;
      if (dbg) {
        op.OpenDebug();
      }
      //
      if (type == OptionsProcessor.APUT || type == OptionsProcessor.ACALL) {
        op.valid = true;
        //
        final String s = String.format("%d\t%s\t%s\t%.2f\t%.2f\t%s\t%s\t%.4f%n", type, ticker, id, strike, ulPrice, expiry, bDate, iv);
        op.printDbg(s);
        //
        op.setGreeks();
        op.printDbg(String.format("%nNew instance created%n%s%n", op));
      }
      else {
        op.valid = false;
      }
    }
    catch (final Exception e) {
      op.valid = false;
    }
//      if (op.valid) {
//         final String ex = op.expiry.format("dd-MMM-yyyy");
//         final long s = (long) (op.strike * 100.0);
//         final long ul = (long) (op.ulPrice * 100.0);
//         op.dbgFilename = String.format("%s_%s_%d_%s.dbg", op.code, ex, s, ul);
//         System.out.println(op.dbgFilename);
//      }
    return op;
  }

  /**
   * @author M. Bret Blackford
   *
   * @param x
   * @return
   */
  private static double cumulativeDistribution(double x) {
    return OptionsProcessor.cumulativeDistribution(x, OptionsProcessor.standardNormalDistribution(x));
  }

  /**
   * @author M. Bret Blackford
   *
   * @param num
   * @return
   */
  private static double cumulativeDistribution(final double num, double sdx) {
    final double t = 1 / (1 + OptionsProcessor.P * Math.abs(num));
    final double t1 = OptionsProcessor.B1 * Math.pow(t, 1);
    final double t2 = OptionsProcessor.B2 * Math.pow(t, 2);
    final double t3 = OptionsProcessor.B3 * Math.pow(t, 3);
    final double t4 = OptionsProcessor.B4 * Math.pow(t, 4);
    final double t5 = OptionsProcessor.B5 * Math.pow(t, 5);
    final double b = t1 + t2 + t3 + t4 + t5;
    final double cd = 1 - sdx * b;
    return num < 0 ? 1 - cd : cd;
  }

  /**
   * @author M. Bret Blackford
   *
   * @param num
   * @return
   */
  private static double standardNormalDistribution(final double num) {
    final double p1 = Math.exp(-0.5 * Math.pow(num, 2.0));
    return p1 / OptionsProcessor.p2;
  }

  String       code;
  int          dataType;
  String       id;
  boolean      valid;
  private long days;

  private String dbgFilename;

  private PrintWriter dbgPw;

  private boolean DEBUG;

  private double delta;

  private DateTime expiry;

  private double gamma;

  private boolean isPwDbgOpen = false;

  private double iv;

  private double price;

  private PrintWriter pwDbg = null;

  private double rate;

  private double rho;

  private DateTime sellDate;

  private double strike;

  private double theta;

  private double ulPrice;

  private double vega;

  private double years;

  /**
   *
   * @param type
   * @param id
   * @param strike
   * @param ulPrice
   * @param expiry
   * @param bDate
   * @param iv
   */
  public OptionsProcessor(int type, String ticker, String id, double strike, double ulPrice, DateTime expiry, DateTime bDate, double iv,
      boolean dbg) {
    OptionsProcessor.build(this, type, ticker, id, strike, ulPrice, expiry, bDate, iv, dbg);
  }

  /**
   * Creates a working object to process the option.
   *
   * @param type
   * @param id
   * @param strike
   * @param ulPrice
   * @param expiry
   * @param iv
   * @throws FileNotFoundException
   */
  public OptionsProcessor(int type, String ticker, String id, double strike, double ulPrice, DateTime expiry, double iv, boolean dbg) {
    final DateTime now = new DateTime();
    if (expiry.isGreaterThanOrEqual(now)) {
      OptionsProcessor.build(this, type, ticker, id, strike, ulPrice, expiry, now, iv, dbg);
    }
    else {
      this.valid = false;
    }
  }

  /**
   * Copy Constructor
   *
   * @param op
   */
  public OptionsProcessor(OptionsProcessor op) {
    this.valid = op.valid;
    if (op.valid) {
      this.id = op.id;
      this.dataType = op.dataType;
      this.delta = op.delta;
      this.gamma = op.gamma;
      this.theta = op.theta;
      this.rho = op.rho;
      this.vega = op.vega;
      this.iv = op.iv;
      this.strike = op.strike;
      this.ulPrice = op.ulPrice;
      this.price = op.price;
      if (op.expiry != null) {
        this.expiry = new DateTime(op.expiry);
      }
      if (op.sellDate != null) {
        this.sellDate = new DateTime(op.sellDate);
      }
      this.rate = op.rate;
      this.years = op.years;
      this.days = op.days;
      this.DEBUG = op.DEBUG;
    }
  }

  public OptionsProcessor(String id, TestOptionTruthdata tset) {
    int type = OptionsProcessor.ACALL;
    if (tset.type > 10) {
      type = OptionsProcessor.APUT;
    }
    final DateTime today = new DateTime();
    final DateTime ex = new DateTime();
    ex.add(DateTime.DATE, (int) (tset.years * 365.0));
    OptionsProcessor.build(this, type, "TESTING", id, tset.strike, tset.ul, ex, today, tset.iv, true);
  }

  /**
   * Must be called by main program
   *
   * @throws FileNotFoundException
   */
  public void CloseDebug() {
    if (this.isPwDbgOpen) {
      this.pwDbg.close();
    }
  }

  /**
   * Calculates IV from price of underlying
   *
   * @param cPrice - current price of the underlying
   * @param baseIv - a startingPoint
   * @return
   */
  public double findIv(double cPrice, double baseIv) {
    double impVol = baseIv;
//		if (OptionsProcessor.DEBUG) {
//			System.out.printf("\ncPrice : %.2f%n", cPrice);
//		}
    // Create a copy and process the copy
    final OptionsProcessor op = new OptionsProcessor(this);
    op.setId("Temp findIv instance");
    double oldDiff = 0.0;
    for (int i = 0; i < 10000; i++) {
//			if (OptionsProcessor.DEBUG) {
//				System.out.printf("IV %.4f\tIteration %d%n", impVol, i);
//			}
      op.setIv(impVol);
      final double pr = op.getPrice();
      final double diff = cPrice - pr;
// Price can get "stuck" outside the precision value,
// so stop if the diff does not significantly change.
      final double diffdiff = Math.abs(diff - oldDiff);
//			if (OptionsProcessor.DEBUG) {
//				System.out.printf("  Price     %.2f%n", pr);
//				System.out.printf("  Diff      %.3f%n", diff);
//				System.out.printf("  DiffDiff  %.3f%n", diffdiff);
//			}
      if (Math.abs(diff) < OptionsProcessor.precision) {
        System.out.println("Iterations : " + i);
        return impVol;
      }
      else if (diffdiff < OptionsProcessor.precision) {
        System.out.println("Iterations : " + i);
        System.out.println("DiffDiff exit.");
        return impVol;
      }
      if (diff > 0.0) {
        impVol *= 1.11;
      }
      else {
        impVol /= 1.07;
      }
      oldDiff = diff;
    }
    System.out.println("\nERROR - findIv did not work!\n");
    return impVol;
  }

  public int getDataType() {
    return this.dataType;
  }

  public double getDelta() {
    return this.delta;
  }

  public DateTime getExpiry() {
    return this.expiry;
  }

  public double getGamma() {
    return this.gamma;
  }

  public String getId() {
    return this.id;
  }

  public double getIv() {
    return this.iv;
  }

  /**
   * Recalculates the Greeks and returns updated price
   *
   * @return option price
   */
  public double getPrice() {
    Double pr = 0.0;
    // Double skewy = 1.0 + skew;
    if (this.valid && this.sellDate.isLessThanOrEqual(this.expiry)) {
      final double d1 = this.calcD1();
      final double d2 = this.calcD2(d1);
      double ccd1 = 0.0;
      double ccd2 = 0.0;
      double pcd1 = 0.0;
      double pcd2 = 0.0;
      if (this.dataType == OptionsProcessor.ACALL) {
        ccd1 = OptionsProcessor.cumulativeDistribution(d1);
        ccd2 = OptionsProcessor.cumulativeDistribution(d2);
        this.price = this.ulPrice * ccd1 - this.strike * Math.exp(-this.rate * this.years) * ccd2;
      }
      else if (this.dataType == OptionsProcessor.APUT) {
        pcd1 = OptionsProcessor.cumulativeDistribution(-d1);
        pcd2 = OptionsProcessor.cumulativeDistribution(-d2);
        this.price = this.strike * Math.exp(-this.rate * this.years) * pcd2 - this.ulPrice * pcd1;
      }
      pr = this.price;
      if (this.isDebugOn()) {
        String sDbg = "";
        sDbg += String.format("getPrice() : %s%n", this.id);
        sDbg += String.format("\td1         : %f%n", d1);
        sDbg += String.format("\td2         : %f%n", d2);
        if (this.dataType == OptionsProcessor.ACALL) {
          sDbg += String.format("\tccd1       : %f%n", ccd1);
          sDbg += String.format("\tccd2       : %f%n", ccd2);
        }
        if (this.dataType == OptionsProcessor.APUT) {
          sDbg += String.format("\tpcd1       : %f%n", pcd1);
          sDbg += String.format("\tpcd2       : %f%n", pcd2);
        }
        sDbg += String.format("\tprice      : %f%n", pr);
        this.printDbg(sDbg);
      }
    }
    else {
      this.printDbg(String.format("getPrice() :: Sell date : %s is later than expiry :%s.%n", this.sellDate, this.expiry));
    }

    return this.price;
  }

  public double getRate() {
    return this.rate;
  }

  public double getRho() {
    return this.rho;
  }

  public DateTime getSellDate() {
    return this.sellDate;
  }

  public double getStrike() {
    return this.strike;
  }

  public double getTheta() {
    return this.theta;
  }

  public String getType() {
    String ret = "APUT";
    if (this.dataType == OptionsProcessor.ACALL) {
      ret = "ACALL";
    }
    return ret;
  }

  public double getUlPrice() {
    return this.ulPrice;
  }

  public double getVega() {
    return this.vega;
  }

  public double getYears() {
    return this.years;
  }

  /**
   * Must be called by main program
   *
   * @throws FileNotFoundException
   */
  public void OpenDebug() throws FileNotFoundException {
    this.DEBUG = true;
    final DateTime now = new DateTime();
    now.getMs();
    this.dbgFilename = String.format("%s_%s_%d_%d_%d%d.txt", this.expiry, this.getType(), (int) this.strike, (int) (this.ulPrice * 100.0),
        now.getSecond(), now.getMs());
    this.pwDbg = new PrintWriter("dbg/" + this.dbgFilename);
    this.isPwDbgOpen = true;
    System.out.println(this.dbgFilename);
  }

  /**
   *
   * @param s
   */
  public void printDbg(String s) {
    if (this.isDebugOn()) {
      this.pwDbg.println(s);
      // this.dbgPw.flush();
    }
  }

  public void setIv(double inIv) {
    this.iv = inIv;
  }

  public void setRate(double inRate) {
    this.rate = inRate;
  }

  /**
   * Updates the sellDate and the years values.
   *
   * @param sellDate
   */
  public void setSellDate(DateTime sellDate) {
    this.sellDate = new DateTime(sellDate);
    this.years = this.expiry.getDeltaYears(sellDate);
    this.days = this.expiry.getDeltaDays(sellDate) + 1;
  }

  public void setUlPrice(double ulPrice) {
    this.ulPrice = ulPrice;
  }

  /**
   *
   */
  @Override
  public String toString() {
    String ret = "";
    if (this.valid) {
      if (this.dataType == OptionsProcessor.ACALL) {
        ret = String.format("%s\t:\t%s%n", "CALL", this.id);
      }
      else {
        ret = String.format("%s\t:\t%s%n", "PUT", this.id);
      }
      ret += String.format("  Expiry    : %s\t(Years:%.3f\tDays:%d)%n", this.expiry, this.years, this.days);
      ret += String.format("  Strike    : %.2f%n", this.strike);
      ret += String.format("  ulPrice   : %.2f%n", this.ulPrice);
      ret += String.format("  Price     : %.2f%n", this.price);
      ret += String.format("  IV        : %.4f%n", this.iv);
      ret += String.format("  Theta     : %.4f%n", this.theta);
      ret += String.format("  Delta     : %.4f%n", this.delta);
      ret += String.format("  Vega      : %.4f%n", this.vega);
      ret += String.format("  Gamma     : %.4f%n", this.gamma);
      ret += String.format("  Rho       : %.4f%n", this.rho);
      ret += String.format("  Rate      : %.4f%n", this.rate);
      ret += String.format("  Sell Date : %s", this.sellDate);
    }
    else {
      ret = "NOT VALID";
    }
    return ret;
  }

  /**
   * @author M. Bret Blackford
   *
   * @return d1 from Black-Scholes equation
   */
  private double calcD1() {
    double d1 = 0.0;
    if (this.valid) {
      final double p1 = Math.log(this.ulPrice / this.strike) + (this.rate + Math.pow(this.iv, 2.0) / 2.0) * this.years;
      final double p2 = this.iv * Math.sqrt(this.years);
      OptionsProcessor.gDbgStr = String.format(" calcD1() --> p1=%f\tp2=%f", p1, p2);
      OptionsProcessor.gDbgStr += "\n\tp1 = Math.log(this.ulPrice / this.strike) + (this.rate + Math.pow(this.iv, 2.0) / 2.0) * this.years";
      OptionsProcessor.gDbgStr += "\n\tp2 = this.iv * Math.sqrt(this.years)";
      if (p2 != 0.0) {
        d1 = p1 / p2;
        OptionsProcessor.gDbgStr += String.format("%n\td1=%f\t\td1 =  p1 / p2", d1);
      }
      else {
        System.out.println("Error : CalcD1 p2 is zero!");
      }
    }
    return d1;
  }

  /**
   * @author M. Bret Blackford
   *
   * @return d2 from Black-Scholes equation
   */
  private double calcD2(double d1) {
    final double sqrtYears = Math.sqrt(this.years);
    final double d2 = d1 - this.iv * sqrtYears;
    OptionsProcessor.gDbgStr = String.format(" calcD2() --> iv=%f\tyears=%f\tsqrtYears=%f%n\td2=%f", this.iv, this.years, sqrtYears, d2);
    OptionsProcessor.gDbgStr += "\t\td2 = d1 - (this.iv * sqrtYears)";
    return d2;
  }

  private boolean isDebugOn() {
    return this.isPwDbgOpen && this.valid && this.DEBUG;
  }

  private void printDbgStr() {
    this.printDbg(OptionsProcessor.gDbgStr);
    OptionsProcessor.gDbgStr = "";
  }

  /**
   * @author M. Bret Blackford
   *
   *         Calculates the greeks and the price.
   *
   */
  private void setGreeks() {
    try {
      this.printDbg("Entering setGreeks()");
      //
      final double d1 = this.calcD1();
      this.printDbgStr();
      final double d2 = this.calcD2(d1);
      this.printDbgStr();
      //
      final double sd1 = OptionsProcessor.standardNormalDistribution(d1);
      final double cd1 = OptionsProcessor.cumulativeDistribution(d1, sd1);
      final double thetaLeft = -(this.ulPrice * sd1 * this.iv) / (2.0 * Math.sqrt(this.years));
      if (this.isDebugOn()) {
        String sDbg = "";
        sDbg += String.format(" %s%n", this.id);
        sDbg += String.format("\td1         : %f%n", d1);
        sDbg += String.format("\td2         : %f%n", d2);
        sDbg += String.format("\tsd1        : %f\t\t--\tstandardNormalDistribution(d1)%n", sd1);
        sDbg += String.format("\tcd1        : %f\t\t--\tcumulativeDistribution(d1, sd1)%n", cd1);
        this.printDbg(sDbg);
      }
      if (this.dataType == OptionsProcessor.ACALL) {
        final double cd2 = OptionsProcessor.cumulativeDistribution(d2);
        this.delta = cd1;
        final double thetaRight = this.rate * this.strike * Math.exp(-this.rate * this.years) * cd2;
        this.theta = (thetaLeft - thetaRight) / 100.0;
        this.rho = this.strike * this.years * Math.exp(-this.rate * this.years) * cd2 / 100.0;
        if (this.isDebugOn()) {
          String sDbg = "";
          sDbg += String.format("\tcd2        : %f\t\t--\tcd2 = cumulativeDistribution(d2)%n", cd2);
          sDbg += String.format("\tthetaLeft  : %f\t\t--\tthetaLeft = (this.ulPrice * sd1 * this.iv) / (2.0 * Math.sqrt(this.years))%n", thetaLeft);
          sDbg += String.format("\tthetaRight : %f\t\t--\tthetaRight = this.rate * this.strike * Math.exp(-this.rate * this.years) * cd2%n",
              thetaRight);
          sDbg += String.format("%n\ttheta      : %f\t\t--\ttheta = (thetaLeft - thetaRight) / 100.0%n", this.theta);
          sDbg += String.format("\trho        : %f\t\t--\trho = this.strike * this.years * Math.exp(-this.rate * this.years) * cd2 / 100.0",
              this.rho);
          this.printDbg(sDbg);
        }
      }
      else if (this.dataType == OptionsProcessor.APUT) {
        final double pcd1 = OptionsProcessor.cumulativeDistribution(-d1);
        final double pcd2 = OptionsProcessor.cumulativeDistribution(-d2);
        this.delta = pcd1 - 1.0;
        final double thetaRight = this.rate * this.strike * Math.exp(-this.rate * this.years) * pcd2;
        this.theta = (thetaLeft + thetaRight) / 100.0;
        this.rho = -this.strike * this.years * Math.exp(-this.rate * this.years) * pcd2 / 100.0;
        if (this.isDebugOn()) {
          String sDbg = "";
          sDbg += String.format("\tpcd1       : %f\t\t--\tpcd1 = cumulativeDistribution(-d1)%n", pcd1);
          sDbg += String.format("\tpcd2       : %f\t\t--\tpcd2 = cumulativeDistribution(-d2)%n", pcd2);
          sDbg += String.format("\tthetaLeft  : %f\t\t--\tthetaLeft = -(this.ulPrice * sd1 * this.iv) / (2.0 * Math.sqrt(this.years))%n", thetaLeft);
          sDbg += String.format("\tthetaRight : %f\t\t--\tthetaRight = this.rate * this.strike * Math.exp(-this.rate * this.years) * pcd2%n",
              thetaRight);
          sDbg += String.format("%n\ttheta      : %f\t\t--\ttheta = (thetaLeft + thetaRight) / 100.0%n", this.theta);
          sDbg += String.format("\trho        : %f\t\t--\trho = -this.strike * this.years * Math.exp(-this.rate * this.years) * pcd2 / 100.0",
              this.rho);
          this.printDbg(sDbg);
        }
      }
      this.gamma = sd1 / (this.ulPrice * this.iv * Math.sqrt(this.years));
      this.vega = this.ulPrice * sd1 * Math.sqrt(this.years) / 100.0;
      //
      String sDbg = "";
      sDbg += String.format("\tgamma      : %f\t\t--\tgamma = sd1 / (this.ulPrice * this.iv * Math.sqrt(this.years))%n", this.gamma);
      sDbg += String.format("\tvega       : %f\t\t--\tvega = this.ulPrice * sd1 * Math.sqrt(this.years) / 100.0%n", this.vega);
      // sDbg += String.format("%nreturn setGreeks() :: Updated instance%n%s%n",
      // this);
      //
      this.printDbg(sDbg);
      //
      this.price = this.getPrice();
      //
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * The Id is only for commenting purposes. It is not required.
   *
   * @param inId
   */
  private void setId(String inId) {
    this.id = inId;
  }
}
