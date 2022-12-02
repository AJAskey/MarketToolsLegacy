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

package net.ajaskey.market.tools.SIP.BigDB.derived;

import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.SipOutput;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class ZData {

  /**
   *
   * @param bound
   * @param val
   * @return
   */
  private static double AMAX(double bound, double val) {
    return Math.abs(Math.max(bound, val));
  }

  /**
   *
   * @param bound
   * @param val
   * @return
   */
  private static double MIN(double bound, double val) {
    return Math.min(bound, val);
  }

  /**
   *
   * @param d
   * @param e
   * @param Maxval
   * @return
   */
  private static double POW(double d, double e, double Maxval) {
    final double tmp = ZData.MIN(Math.pow(d, e), Maxval);
    return tmp;
  }

  private final CompanyDerived cdr;
  private String               dbgStr;
  private final FieldData      fd;
  private double               zCashFlow;
  private double               zDebt;
  private double               zDebtVsTa;
  private double               zInterest;
  private final boolean        zIsZombie;
  private double               zMajorScore;
  private double               zNet;
  private double               zSales;
  private double               zScore;
  private final double         zTangibleAssets;
  private double               zWorkingCapital;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public ZData(CompanyDerived cdr) {

    this.cdr = cdr;
    this.fd = cdr.getFd();
    this.zCashFlow = 0.0;
    this.zSales = 0.0;
    this.zNet = 0.0;
    this.zInterest = 0.0;
    this.zDebt = 0.0;
    this.zScore = 0.0;
    this.zMajorScore = 0.0;
    this.zIsZombie = false;
    this.zWorkingCapital = 0.0;
    this.zTangibleAssets = 0.0;
    this.zDebtVsTa = 0.0;

    this.dbgStr = this.calc();
  }

  public CompanyDerived getCd() {
    return this.cdr;
  }

  public double getzDebt() {
    return this.zDebt;
  }

  public double getzInterestRate() {
    return this.zInterest;
  }

  public double getzMajorScore() {
    return this.zMajorScore;
  }

  public double getzNet() {
    return this.zNet;
  }

  public double getzSales() {
    return this.zSales;
  }

  public double getzScore() {
    return this.zScore;
  }

  public double getzTangibleAssets() {
    return this.zTangibleAssets;
  }

  public double getzWorkingCapital() {
    return this.zWorkingCapital;
  }

  public boolean iszIsZombie() {
    return this.zIsZombie;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = "";
    ret += Utils.TAB + "Zombie Major Score  : " + SipOutput.fmt(this.zMajorScore, 13, 2) + "  (" + SipOutput.fmt(this.zScore, 1, 2) + ")" + Utils.NL;
    ret += Utils.TAB + "  *Working Capital  : " + SipOutput.fmt(this.zWorkingCapital, 13, 2) + Utils.NL;
    ret += Utils.TAB + "  *Debt             : " + SipOutput.fmt(this.zDebt, 13, 2) + Utils.NL;
    ret += Utils.TAB + "  *Interest         : " + SipOutput.fmt(this.zInterest, 13, 2) + Utils.NL;
    ret += Utils.TAB + "  *Debt to TA       : " + SipOutput.fmt(this.zDebtVsTa, 13, 2) + Utils.NL;
    ret += Utils.TAB + "   Cash Flow        : " + SipOutput.fmt(this.zCashFlow, 13, 2) + Utils.NL;
    ret += Utils.TAB + "   Sales            : " + SipOutput.fmt(this.zSales, 13, 2) + Utils.NL;
    ret += Utils.TAB + "   Net              : " + SipOutput.fmt(this.zNet, 13, 2) + Utils.NL;
    // ret += this.dbgStr + Utils.NL;

    if (this.zIsZombie) {
      ret += Utils.TAB + "Is ZOMBIE!" + Utils.NL;
    }
    return ret;
  }

  /**
   * Calculates the Zombie Score
   */
  private String calc() {

    /**
     * Debug
     */
    if (this.fd.getTicker().equalsIgnoreCase("SBUX")) {
      System.out.println();
    }

    this.dbgStr = Utils.NL + this.fd.getTicker() + Utils.NL;

    final double MAXVAL = 9999999.99;

    double qtr;
    double ttm;
    double score;
    double tmp;
    double ratio;

    final double salesTtm = this.cdr.getSalesQdata().getTtm();
    final double salesQtr = this.cdr.getSalesQdata().getMostRecent();
    final double currAssets = this.cdr.getCurrAssetsQdata().getMostRecent();
    final double currLiab = this.cdr.getCurrLiabQdata().getMostRecent();
    final double cashFromOpsTtm = this.cdr.getCashFromOpsQdata().getTtm();
    final double fcfTtm = this.cdr.getFcfQdata().getTtm();
    final double wc = currAssets - currLiab;
    final double wcfcf = wc + fcfTtm;

    this.dbgStr += String.format(" Sales Q/12       :\t%.2f\t%.2f%n", salesQtr, salesTtm);
    this.dbgStr += String.format(" Curr Assets Liab :\t%.2f\t%.2f\t%.2f%n", currAssets, currLiab, currAssets - currLiab);
    this.dbgStr += String.format(" CashFromOpsTtm   :\t%.2f%n", cashFromOpsTtm);
    this.dbgStr += String.format(" FCF TTM          :\t%.2f%n", fcfTtm);
    this.dbgStr += String.format(" WC & FCF         :\t%.2f\t%.2f%n", wc, wcfcf);

    /**
     *
     * Major Zombie factors worth 100.0 points each
     *
     */

    /**
     * Working Capital
     */
    if (currAssets > 0.0) {
      ratio = currLiab / (currAssets + fcfTtm);
      this.dbgStr += String.format("Working Capital : ratio=%.2f\tcurrLiab=%.2f\tcurrAssets=%.2f\tfcfTtm=%.2f%n", ratio, currLiab, currAssets,
          fcfTtm);
      if (ratio > 1.0) {
        tmp = ZData.POW(ratio, 9.25, MAXVAL);
        score = ZData.MIN(tmp, 150.0);
        this.zWorkingCapital = score;
        this.dbgStr += String.format(
            "\tWC neg with not enough available CashFromOps - zWorkingCapital : ratio=%.2f\tzWorkingCapital=%.2f\ttmp=%.2f\tscore=%.2f ==> POW(ratio, 9.25)\n",
            ratio, this.zWorkingCapital, tmp, score);
      }
    }
    else {
      this.zWorkingCapital = 150.0;
      this.dbgStr += String.format("\tWC with CurrAssets neg - zWorkingCapital : %.2f\t%.2f\n", this.zWorkingCapital, currAssets);
    }
    this.zMajorScore += this.zWorkingCapital;

    /**
     * Debt
     */
    final double ltDebt = this.cdr.getLtDebtQdata().getMostRecent();
    final double stDebt = this.cdr.getStDebtQdata().getMostRecent();
    final double totDebt = ltDebt + stDebt;
    final double ta = this.cdr.getTanAssetsQdata().getMostRecent();
    final double equity = this.cdr.getEquityQdata().getMostRecent();
    double debtToTa = 0.0;
    double debtToSales = 0.0;
    double debtToEquity = 0.0;
    if (ta > 0.0) {
      debtToTa = totDebt / ta;
    }
    if (salesTtm > 0.0) {
      debtToSales = totDebt / salesTtm;
    }
    if (equity > 0.0) {
      debtToEquity = totDebt / equity;
    }

    this.dbgStr += String.format("Debt ST/LT/Tot    :\t%.2f\t%.2f\t%.2f%n", stDebt, ltDebt, totDebt);
    if (totDebt > 0.0) {
      this.dbgStr += String.format(" Equity           :\t%.2f%n", equity);
      this.dbgStr += String.format(" TanAssets        :\t%.2f%n", ta);
      this.dbgStr += String.format(" D/Eq D/TA D/S    :\t%.2f\t%.2f\t%.2f%n", debtToEquity, debtToTa, debtToSales);
    }
    else {
      this.dbgStr += String.format(" No total debt%n");
    }

    if (totDebt > 0.0) {
      if (equity > 0.0) {
        tmp = 0.0;
        score = 0.0;
        if (debtToEquity > 1.0) {
          if (debtToTa > 1.0) {
            tmp += ZData.POW(5.0, debtToTa, MAXVAL);
            score = ZData.MIN(125.0, tmp);
            this.dbgStr += String.format("\tDebt To TA - zDebt : score=%.2f\ttmp=POW(5.0,debtToTa) %.2f\n", score, tmp);
          }
          else {
            tmp = ZData.POW(2.5, debtToEquity, MAXVAL);
            score = ZData.MIN(125.0, tmp);
            this.dbgStr += String.format("\tDebt To Eq - zDebt : score=%.2f\ttmp=POW(2.5,debtToEquity) %.2f\n", score, tmp);
          }
          this.zDebt = score;
        }
      }
      else if (ta > 0.0) {
        tmp = 50.0;
        this.dbgStr += String.format("\tzDebt starts at 50 because no equity%n");
        if (debtToTa > 0.99) {
          tmp += ZData.POW(5.0, debtToTa, MAXVAL);
        }
        score = ZData.MIN(125.0, tmp);
        this.zDebt = score;
        this.dbgStr += String.format("\tDebt To Tangible Assets - zDebt : %.2f\ttmp=50+POW(5.0,debtToTa) %.2f\n", this.zDebt, tmp);
      }
      else if (salesTtm > 0.0) {
        tmp = 75.0;
        this.dbgStr += String.format("\tzDebt starts at 75 because no equity or tangible assets%n");
        if (debtToSales > 0.99) {
          tmp += ZData.POW(2.0, debtToSales, MAXVAL);
        }
        score = ZData.MIN(125.0, tmp);
        this.zDebt = score;
        this.dbgStr += String.format("\tDebt To Sales - zDebt : %.2f\ttmp=75+POW(2.0,debtToSales) %.2f\n", this.zDebt, tmp);
      }
      else {
        this.zDebt = 125.0;
        this.dbgStr += String.format("\tDebt without backup - zDebt : %.2f\n", this.zDebt);
      }
    }
    this.zMajorScore += this.zDebt;

    /**
     * Debt to Tangible Assets
     */
    final double tanAssets = this.cdr.getTanAssetsQdata().get(1);
    final double tanAssetsYr = this.cdr.getTanAssetsQdata().get(5);
    final double debt = this.cdr.getStDebtQdata().get(1) + this.cdr.getLtDebtQdata().get(1);
    final double debtYr = this.cdr.getStDebtQdata().get(5) + this.cdr.getLtDebtQdata().get(5);
    final double taChg = tanAssets - tanAssetsYr;
    final double debtChg = debt - debtYr;
    ratio = debtChg / taChg;
    this.dbgStr += String.format("Debt to TanAssets%n");
    this.dbgStr += String.format("\tTA/TAYr/TAChg      \t%15.2f\t%15.2f\t%9.2f%n", tanAssets, tanAssetsYr, taChg);
    this.dbgStr += String.format("\tDebt/DebyYr/DebtChg\t%15.2f\t%15.2f\t%9.2f%n", debt, debtYr, debtChg);
    this.dbgStr += String.format("\tratio\t%.2f%n", ratio);
    if (ratio > 1.0 && debtChg > 0.0) {
      tmp = ZData.POW(4.25, ratio, MAXVAL);
      this.zDebtVsTa = ZData.MIN(tmp, 75.0);
      this.dbgStr += String.format("\tzDebtVsTa\t%.2f\ttmp=%.2f%n", this.zDebtVsTa, tmp);
    }
    else if (debtChg > 0.0 && taChg < 0.0) {
      this.zDebtVsTa = 75.0;
      this.dbgStr += String.format("\ttaChg neg while debtChg pos - zDebtVsTa\t%.2f%n", this.zDebtVsTa);
    }
    this.zMajorScore += this.zDebtVsTa;

    /**
     * Interest
     */
    final double totDebtPrev = this.cdr.getLtDebtQdata().get(2) + this.cdr.getStDebtQdata().get(2);
    final double intRate = this.cdr.getCurrInterestRate();
    final double totInterest = totDebtPrev * intRate;
    double intToSales = 0.0;
    double salesIntRatio = 0.0;
    if (salesTtm > 0.0) {
      intToSales = totInterest / salesTtm;
      salesIntRatio = salesTtm / totInterest;
    }
    this.dbgStr += String.format("Interest          :\ttotDebt=%.2f\tintRate=%.2f\ttotInterest=%.2f%n", totDebtPrev, intRate * 100.0, totInterest);
    this.dbgStr += String.format(" Int to Sales     :\t%.2f\tsalesIntRatio=%.2f%n", intToSales, salesIntRatio);
    tmp = 0.0;
    if (intRate > 0.030) {

      tmp = ZData.POW(1.8, intRate * 100.0, MAXVAL);
      this.zInterest += ZData.MIN(62.5, tmp);
      this.dbgStr += String.format("\tInterest - zInterestRate : %.2f\ttmp=POW(1.8,intRate) %.2f\n", this.zInterest, tmp);
    }
    if (intToSales > 0.0) {
      tmp = ZData.POW(5.0, intToSales * 10.0, MAXVAL);
      this.zInterest += ZData.MIN(62.5, tmp);
      this.dbgStr += String.format("\tInterest to Sales - zInterestRate : %.2f\ttmp=POW(5.0,intToSales*10.0) %.2f\n", this.zInterest, tmp);
    }
    this.zMajorScore += this.zInterest;

    this.zScore = this.zMajorScore;

    /**
     * Minor Zombie factors worth 20.0 points each
     */

    final double minorMax = -10.0;

    /**
     * Cash Flow
     */
    ttm = this.cdr.getCashflowQdata().getTtm();
    this.dbgStr += SipOutput.buildArray("Cash Flow ", this.cdr.getCashflowQdata().dArr, 10, 2) + Utils.NL;
    this.dbgStr += String.format("\tCash Flow TTM : %.2f\n", ttm);
    if (ttm <= 0.0) {
      this.zCashFlow += minorMax * -2.0;
      this.dbgStr += String.format("\tTTM CashFlow neg - zCashFlow : %.2f\n", this.zCashFlow);
    }
    this.zScore += this.zCashFlow;

    /**
     * Sales
     */
    qtr = this.cdr.getSalesQdata().getQoQ();
    ttm = this.cdr.getSalesQdata().getYoY();
    this.dbgStr += SipOutput.buildArray("Sales         ", this.cdr.getSalesQdata().dArr, 10, 2) + Utils.NL;
    this.dbgStr += String.format("\tQoQ : %.2f%%%n", qtr);
    this.dbgStr += String.format("\tYoY : %.2f%%\t%.2f\t%.2f%n", ttm, this.cdr.getSalesQdata().getTtm(), this.cdr.getSalesQdata().getPrevTtm());
    if (qtr < 0.0) {
      this.zSales += ZData.AMAX(minorMax, qtr);
      this.dbgStr += String.format("\tMost recent Sales QoQ growth neg - zSales : %.2f\t%.2f\n", this.zSales, qtr);
    }
    if (ttm < 0.0) {
      this.zSales += ZData.AMAX(minorMax, ttm);
      this.dbgStr += String.format("\tMost recent Sales YoY growth neg - zSales : %.2f\t%.2f\n", this.zSales, ttm);
    }
    this.zScore += this.zSales;

    /**
     * Net income
     */
    this.dbgStr += SipOutput.buildArray("Net Income ", this.cdr.getNetIncQdata().dArr, 10, 2) + Utils.NL;
    qtr = this.cdr.getNetIncQdata().getQoQ();
    ttm = this.cdr.getNetIncQdata().getYoY();
    this.dbgStr += String.format("\tQoQ : %.2f%%%n", qtr);
    this.dbgStr += String.format("\tYoY : %.2f%%\t%.2f\t%.2f%n", ttm, this.cdr.getNetIncQdata().getTtm(), this.cdr.getNetIncQdata().getPrevTtm());
    if (qtr < 0.0) {
      this.zNet += ZData.AMAX(minorMax, qtr);
      this.dbgStr += String.format("\tMost recent QoQ Net Income growth neg - zNet : %.2f\t%.2f\n", this.zNet, qtr);
    }
    if (ttm < 0.0) {
      this.zNet += ZData.AMAX(minorMax, ttm);
      this.dbgStr += String.format("\tMost recent YoY Net Income growth neg - zNet : %.2f\t%.2f\n", this.zNet, ttm);
    }
    this.zScore += this.zNet;

    return this.dbgStr;
  }

}
