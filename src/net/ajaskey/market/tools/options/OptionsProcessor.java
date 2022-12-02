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

package net.ajaskey.market.tools.options;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;

import net.ajaskey.common.DateTime;

/**
 *
 * @author Andy Askey
 *         <p>
 *         Copyright (c) 2019, Andy Askey. All rights reserved.
 *         </p>
 *
 *         <p>
 *         Permission is hereby granted, free of charge, to any person obtaining
 *         a copy of this software and associated documentation files (the
 *         "Software"), to deal in the Software without restriction, including
 *         without limitation the rights to use, copy, modify, merge, publish,
 *         distribute, sublicense, and/or sell copies of the Software, and to
 *         permit persons to whom the Software is furnished to do so, subject to
 *         the following conditions:
 *
 *         The above copyright notice and this permission notice shall be
 *         included in all copies or substantial portions of the Software.
 *         </p>
 *
 *         <p>
 *         THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *         EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *         MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *         NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *         BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 *         ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *         CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *         SOFTWARE.
 *         </p>
 *
 *         <p>
 *         This software was derived from a github project from M. Bret
 *         Blackford.
 *
 *         https://github.com/bret-blackford/black-scholes/tree/master/...
 *         OptionValuation/src/mBret/options
 *         </p>
 *
 *         <p>
 *         The Black?Scholes formula, which gives a theoretical estimate of the
 *         price of European-style options.
 *
 *         The model was first articulated by Fischer Black and Myron Scholes in
 *         their 1973 paper, "The Pricing of Options and Corporate Liabilities",
 *         published in the Journal of Political Economy. They derived a
 *         stochastic partial differential equation, now called the
 *         Black?Scholes equation, which governs the price of the option over
 *         time.
 *
 *         Delta - measures the rate of change of option value with respect to
 *         changes in the underlying asset's price.
 *
 *         Gamma - measures the rate of change in the delta with respect to
 *         changes in the underlying price.
 *
 *         Vega - measures sensitivity to volatility. Vega is typically
 *         expressed as the amount of money per underlying share that the
 *         option's value will gain or lose as volatility rises or falls by 1%.
 *
 *         Theta - measures the sensitivity of the value of the derivative to
 *         the passage of time: the "time decay."
 *
 *         Rho - measures sensitivity to the interest rate.
 *
 *         mblackford - M. Bret Blackford (credit to Dhruba Bandopadhyay)
 *         </p>
 *
 */
public class OptionsProcessor {

  public final static int     ACALL = 2;
  public final static int     APUT  = 1;
  private static final double B1    = 0.319381530;

  private static final double  B2          = -0.356563782;
  private static final double  B3          = 1.781477937;
  private static final double  B4          = -1.821255978;
  private static final double  B5          = 1.330274429;
  private static final boolean DEBUG       = true;
  private static boolean       isPwDbgOpen = false;

  private static final double opRate = 0.000001;

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
  private static PrintWriter  pwDbg     = null;

  /**
   * Must be called by main program
   *
   * @throws FileNotFoundException
   */
  public static void CloseDebug() {
    if (OptionsProcessor.isPwDbgOpen) {
      OptionsProcessor.pwDbg.close();
    }
  }

  /**
   *
   * Used as a simple example
   *
   * @param args
   * @throws ParseException
   * @throws FileNotFoundException
   */
  public static void main(final String[] args) throws ParseException, FileNotFoundException {

    OptionsProcessor.OpenDebug();

    final DateTime expiry = new DateTime(2020, DateTime.JUNE, 21);

    final OptionsProcessor op = new OptionsProcessor(OptionsProcessor.ACALL, "SPY CALL", 300.0, 314.31, expiry, 0.1045);

    OptionsProcessor.printDbg("\nmain() :: getting price");
    final double price = op.getPrice();

    System.out.println(price);
    System.out.println(op.iv);

    final double iv = op.findIv(350.0, 1.0);
    System.out.println(iv);

    // printDbg("\nmain() :: finding IV");
    // final double impVol = op.findIv(price, op.iv);
    // System.out.println(impVol);
//
//    System.out.println(op);

    OptionsProcessor.CloseDebug();

  }

  /**
   * Must be called by main program
   *
   * @throws FileNotFoundException
   */
  public static void OpenDebug() throws FileNotFoundException {
    if (!OptionsProcessor.isPwDbgOpen) {
      OptionsProcessor.pwDbg = new PrintWriter("OptionsProcess.dbg");
      OptionsProcessor.isPwDbgOpen = true;
    }
  }

  /**
   *
   * @param s
   */
  public static void printDbg(String s) {
    if (OptionsProcessor.isPwDbgOpen) {
      OptionsProcessor.pwDbg.println(s);
    }
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
  private static OptionsProcessor build(OptionsProcessor op, int type, String id, double strike, double ulPrice, DateTime expiry, DateTime bDate,
      double iv) {

    try {

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
      op.days = op.expiry.getDeltaDays(op.getSellDate()) + 1;

      op.setGreeks();

      if (type == OptionsProcessor.APUT || type == OptionsProcessor.ACALL) {
        op.valid = true;
        if (OptionsProcessor.isPwDbgOpen) {
          OptionsProcessor.pwDbg.printf("%nNew instance created%n%s%n", op);
        }
      }
      else {
        op.valid = false;
      }
    }
    catch (final Exception e) {
      op.valid = false;
    }

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

  int dataType;

  String id;

  boolean valid;

  private long days;

  private double delta;

  private DateTime expiry;

  private double gamma;

  private double iv;

  private double price;

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
  public OptionsProcessor(int type, String id, double strike, double ulPrice, DateTime expiry, DateTime bDate, double iv) {

    OptionsProcessor.build(this, type, id, strike, ulPrice, expiry, bDate, iv);

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
  public OptionsProcessor(int type, String id, double strike, double ulPrice, DateTime expiry, double iv) {

    final DateTime now = new DateTime();
    now.add(DateTime.DATE, 5);
    if (expiry.isGreaterThanOrEqual(now)) {
      OptionsProcessor.build(this, type, id, strike, ulPrice, expiry, new DateTime(), iv);
      this.price = this.getPrice();
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

    if (OptionsProcessor.DEBUG) {
      System.out.printf("\ncPrice : %.2f%n", cPrice);
    }

    // Create a copy and process the copy
    final OptionsProcessor op = new OptionsProcessor(this);
    op.setId("Temp findIv instance");

    double oldDiff = 0.0;
    for (int i = 0; i < 10000; i++) {

      if (OptionsProcessor.DEBUG) {
        System.out.printf("IV %.4f\tIteration %d%n", impVol, i);
      }
      op.setIv(impVol);

      final double pr = op.getPrice();
      final double diff = cPrice - pr;

      // Price can get "stuck" outside the precision value,
      // so stop if the diff does not significantly change.
      final double diffdiff = Math.abs(diff - oldDiff);

      if (OptionsProcessor.DEBUG) {
        System.out.printf("  Price     %.2f%n", pr);
        System.out.printf("  Diff      %.3f%n", diff);
        System.out.printf("  DiffDiff  %.3f%n", diffdiff);
      }

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

    if (this.valid && this.sellDate.isLessThanOrEqual(this.expiry)) {

      final double d1 = this.calcD1();
      final double d2 = this.calcD2();

      final double ccd1 = OptionsProcessor.cumulativeDistribution(d1);
      final double ccd2 = OptionsProcessor.cumulativeDistribution(d2);

      final double pcd1 = OptionsProcessor.cumulativeDistribution(-d1);
      final double pcd2 = OptionsProcessor.cumulativeDistribution(-d2);

      if (this.dataType == OptionsProcessor.ACALL) {

        this.price = this.ulPrice * ccd1 - this.strike * Math.exp(-this.rate * this.years) * ccd2;

      }
      else if (this.dataType == OptionsProcessor.APUT) {

        this.price = this.strike * Math.exp(-this.rate * this.years) * pcd2 - this.ulPrice * pcd1;

      }
      pr = this.price;

      if (OptionsProcessor.isPwDbgOpen && this.valid) {
        OptionsProcessor.pwDbg.printf("getPrice() : %s%n", this.id);
        OptionsProcessor.pwDbg.printf(" d1         : %f%n", d1);
        OptionsProcessor.pwDbg.printf(" d2         : %f%n", d2);
        OptionsProcessor.pwDbg.printf(" ccd1       : %f%n", ccd1);
        OptionsProcessor.pwDbg.printf(" ccd2       : %f%n", ccd2);
        OptionsProcessor.pwDbg.printf(" pcd1       : %f%n", pcd1);
        OptionsProcessor.pwDbg.printf(" pcd2       : %f%n", pcd2);
        OptionsProcessor.pwDbg.printf(" price      : %f%n", pr);
      }

    }
    else {
      if (OptionsProcessor.isPwDbgOpen) {
        OptionsProcessor.pwDbg.printf("getPrice() :: Sell date : %s is later than expiry :%s.%n", this.sellDate, this.expiry);
      }
    }

    return pr;
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
   * @author M. Bret Blackford
   *
   *         Calculates the greeks and the price.
   *
   */
  public void setGreeks() {

    try {
      final double d1 = this.calcD1();
      final double d2 = this.calcD2();

      final double sd1 = OptionsProcessor.standardNormalDistribution(d1);
      final double cd1 = OptionsProcessor.cumulativeDistribution(d1, sd1);

      final double thetaLeft = -(this.ulPrice * sd1 * this.iv) / (2.0 * Math.sqrt(this.years));

      if (OptionsProcessor.isPwDbgOpen && this.valid) {
        OptionsProcessor.pwDbg.printf("Setting Greeks : %s%n", this.id);
        OptionsProcessor.pwDbg.printf(" d1         : %f%n", d1);
        OptionsProcessor.pwDbg.printf(" d2         : %f%n", d2);
        OptionsProcessor.pwDbg.printf(" sd1        : %f%n", sd1);
        OptionsProcessor.pwDbg.printf(" cd1        : %f%n", cd1);
      }

      if (this.dataType == OptionsProcessor.ACALL) {

        final double cd2 = OptionsProcessor.cumulativeDistribution(d2);

        this.price = this.getPrice();

        this.delta = cd1;

        final double thetaRight = this.rate * this.strike * Math.exp(-this.rate * this.years) * cd2;
        this.theta = (thetaLeft - thetaRight) / 100.0;

        this.rho = this.strike * this.years * Math.exp(-this.rate * this.years) * cd2 / 100.0;

        if (OptionsProcessor.isPwDbgOpen && this.valid) {
          OptionsProcessor.pwDbg.printf(" cd2        : %f%n", cd2);
          OptionsProcessor.pwDbg.printf(" thetaLeft  : %f%n", thetaLeft);
          OptionsProcessor.pwDbg.printf(" thetaRight : %f%n", thetaRight);
          OptionsProcessor.pwDbg.printf(" theta      : %f%n", this.theta);
        }

      }
      else if (this.dataType == OptionsProcessor.APUT) {

        final double pcd1 = OptionsProcessor.cumulativeDistribution(-d1);
        final double pcd2 = OptionsProcessor.cumulativeDistribution(-d2);

        this.price = this.getPrice();

        this.delta = pcd1 - 1.0;

        final double thetaRight = this.rate * this.strike * Math.exp(-this.rate * this.years) * pcd2;
        this.theta = (thetaLeft + thetaRight) / 100.0;

        this.rho = -this.strike * this.years * Math.exp(-this.rate * this.years) * pcd2 / 100.0;

        if (OptionsProcessor.isPwDbgOpen && this.valid) {
          OptionsProcessor.pwDbg.printf(" pcd1       : %f%n", pcd1);
          OptionsProcessor.pwDbg.printf(" pcd2       : %f%n", pcd2);
          OptionsProcessor.pwDbg.printf(" thetaLeft  : %f%n", thetaLeft);
          OptionsProcessor.pwDbg.printf(" thetaRight : %f%n", thetaRight);
          OptionsProcessor.pwDbg.printf(" theta      : %f%n", this.theta);
        }

      }

      this.gamma = sd1 / (this.ulPrice * this.iv * Math.sqrt(this.years));

      this.vega = this.ulPrice * sd1 * Math.sqrt(this.years) / 100.0;

      if (OptionsProcessor.isPwDbgOpen && this.valid) {
        OptionsProcessor.pwDbg.printf("setGreeks() :: Updated instance%n%s%n", this);
      }

    }
    catch (final Exception e) {
      e.printStackTrace();
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

    double ret = 0.0;

    if (this.valid) {
      final double p1 = Math.log(this.ulPrice / this.strike) + (this.rate + Math.pow(this.iv, 2) / 2) * this.years;
      final double p2 = this.iv * Math.sqrt(this.years);

      if (p2 != 0.0) {
        ret = p1 / p2;
      }
      else {
        System.out.println("Error : CalcD1 p2 is zero!");
      }
    }
    return ret;
  }

  /**
   * @author M. Bret Blackford
   *
   * @return d2 from Black-Scholes equation
   */
  private double calcD2() {

    return this.calcD1() - this.iv * Math.sqrt(this.years);
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
