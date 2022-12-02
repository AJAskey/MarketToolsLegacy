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

package net.ajaskey.market.tools.SIP;

public class ZombieData {

  public static double arKnob = 0.95;
  public static double gwKnob = 0.10;

  public static double inventoryKnob = 0.95;
  public static double ltAssetsKnob  = 0.15;

  public static double        ltInvestmentsKnob = 0.85;
  public static double        stAssetsKnob      = 0.98;
  public static double        stInvestmentsKnob = 0.99;
  public static int           zKnt              = 0;
  public static String        zStr              = "";
  final private static String NL                = "\n";
  final private static String TAB               = "\t";

  // private static void print(String desc, double val) {
  // System.out.printf("%-10s : %f%n", desc, val);
  // }

  public double unusualExpenses;

  public double zAdjInc;

  public double zAdjScr;

  public double zCash;

  public double zDebt;

  public double zDividend;

  public double zIncome;

  public boolean zIsZombie;

  public double zKeepItRunning;

  public double zNet;

  public double       zScore;
  public ZombieStates zState;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public ZombieData() {

    this.zIncome = 0.0;
    this.unusualExpenses = 0.0;
    this.zAdjInc = 0.0;
    this.zDividend = 0.0;
    this.zCash = 0.0;
    this.zDebt = 0.0;
    this.zNet = 0.0;
    this.zScore = 0.0;
    this.zAdjScr = 0.0;
    this.zKeepItRunning = 0.0;
    this.zIsZombie = false;
    this.zState = ZombieStates.UNKNOWN;
  }

  /**
   * net.ajaskey.market.tools.SIP.calc
   *
   */
  public void calc(final CompanyData cd) {

    if (cd.ticker.equalsIgnoreCase("QTNT")) {
      // System.out.println(this);
    }

    this.zCash = this.calcZCash(cd);
    this.zDebt = this.calcZDebt(cd);
    this.zNet = this.zCash - this.zDebt;
    this.zIncome = this.calcZIncome(cd);
    this.calcZScore(cd);

    if (this.zIsZombie && this.zAdjScr < 4.0) {
      // zStr += " $" + cd.ticker;
      ZombieData.zStr += " " + cd.ticker;
      final int len = ZombieData.zStr.length();
      final int m = len % 100;
      if (len > 100 && m < 6) {
        ZombieData.zStr += ZombieData.NL;
      }
      ZombieData.zKnt++;
      System.out.printf("%-8s\t%25s\t%d%n", cd.ticker, cd.sector, cd.numEmp);
    }

  }

  /**
   * net.ajaskey.market.tools.SIP.calcZCash
   *
   * @param cd
   * @return
   */
  public double calcZCash(final CompanyData cd) {

    final double ret = cd.bsd.cash.getMostRecent() + cd.bsd.acctReceiveable.getMostRecent() * ZombieData.arKnob
        + cd.bsd.stInvestments.getMostRecent() * ZombieData.stInvestmentsKnob + cd.bsd.otherAssets.getMostRecent() * ZombieData.stAssetsKnob
        + cd.bsd.inventory.getMostRecent() * ZombieData.inventoryKnob + cd.bsd.ltInvestments.getMostRecent() * ZombieData.ltInvestmentsKnob
        + cd.bsd.otherLtAssets.getMostRecent() * ZombieData.ltAssetsKnob + cd.bsd.goodwill.getMostRecent() * ZombieData.gwKnob;
    return ret;
  }

  /**
   * net.ajaskey.market.tools.SIP.calcZDebt
   *
   * @param cd
   * @return
   */
  public double calcZDebt(final CompanyData cd) {

    final double ret = cd.bsd.acctPayable.getMostRecent() + cd.bsd.stDebt.getMostRecent() + cd.bsd.otherCurrLiab.getMostRecent();
    return ret;
  }

  /**
   * net.ajaskey.market.tools.SIP.calcZIncome
   *
   * @param cd
   * @return
   */
  public double calcZIncome(final CompanyData cd) {

    if (cd.ticker.equalsIgnoreCase("NI")) {
      System.out.println(this);
    }

    double ret = cd.id.pretaxIncome.q1 + cd.id.pretaxIncome.q2 + cd.id.pretaxIncome.q3 + cd.id.pretaxIncome.q4 + cd.id.unusualIncome.q1
        + cd.id.unusualIncome.q2 + cd.id.unusualIncome.q3 + cd.id.unusualIncome.q4;

    if (cd.id.pretaxIncome.q1 == 0.0) {
      ret += cd.id.pretaxIncome.q5 + cd.id.unusualIncome.q5;
    }
    final double avg = ret / 4.0;
    this.zDividend = cd.id.dividend.q1 * cd.shares.getMostRecent();
    ret = avg; // - this.zDividend;
    return ret;
  }

  /**
   * net.ajaskey.market.tools.SIP.calcZScore
   *
   * @param cd
   * @return
   */
  public void calcZScore(final CompanyData cd) {

    if (cd.ticker.equalsIgnoreCase("CAKE")) {
      System.out.println(this);
    }

    if (cd.currentRatio > 0.95) {
      this.zScore = 55.0;
      this.zAdjScr = this.zScore;
      return;
    }

    this.zAdjInc = this.zIncome + this.zDividend;

    if (cd.sector.equalsIgnoreCase("Financials")) {
      this.zScore = 123.456;
      return;
    }

    // Case both net and income positive
    if (this.zNet > 0.0 && this.zIncome > 0.0) {
      this.zState = ZombieStates.PNET_PINC;
      this.zIsZombie = false;
      this.zKeepItRunning = this.zIncome + this.zDebt / 8.0;
      this.zScore = 100.0;
      this.zAdjScr = 100.0;
    }

    // Case both net and income are negative
    else if (this.zNet < 0.0 && this.zIncome < 0.0) {

      this.zKeepItRunning = Math.abs(this.zAdjInc) + this.zDebt / 8.0;
      if (this.zKeepItRunning != 0.0) {
        this.zAdjScr = Math.abs(this.zCash / this.zKeepItRunning);
      }

      this.zState = ZombieStates.NNET_NINC;
      this.zScore = this.zAdjScr;
      this.zIsZombie = true;

      if (this.zAdjInc > 0.0) {
        if (this.zAdjScr > 8.0) {
          this.zState = ZombieStates.NNET_NINC_DIVCUT;
          this.zIsZombie = false;
        }
      }
      else {
        // this.zAdjScr = 0.0;
      }
    }

    // Case net is positive and income is negative
    else if (this.zNet > 0.0 && this.zIncome < 0.0) {

      this.zKeepItRunning = Math.abs(this.zIncome) + this.zDebt / 8.0;
      if (this.zKeepItRunning != 0.0) {
        this.zScore = Math.abs(this.zCash / this.zKeepItRunning);
      }

      this.zState = ZombieStates.PNET_NINC;
      // System.out.printf("%s\t%s%n%s%n", cd.ticker, cd.sector, this.toString());

      // Cash funds at least 8 quarters
      if (this.zScore >= 8.0) {
        this.zState = ZombieStates.PNET_NINC_ENUFCASH;
        this.zIsZombie = false;
        this.zAdjScr = this.zScore;
      }

      // Cash funds less than 8 quarters but can use dividend
      else if (this.zScore < 8.0 && this.zDividend > 0.0) {

        // System.out.printf("%s\t%s%n%s%n", cd.ticker, cd.sector, this.toString());

        this.zIsZombie = true;

        this.zKeepItRunning = Math.abs(this.zAdjInc) + this.zDebt / 8.0;
        if (this.zKeepItRunning != 0.0) {
          this.zAdjScr = Math.abs(this.zCash / this.zKeepItRunning);
        }

        if (this.zAdjScr > 8.0) {
          this.zState = ZombieStates.PNET_NINC_DIVCUT;
          this.zIsZombie = false;
        }
      }

      // Cash funds less than 8 quarters with no dividend
      else {
        this.zIsZombie = true;
        this.zAdjScr = this.zScore;
        // System.out.printf("%s\t%s%n%s%n", cd.ticker, cd.sector, this.toString());
      }
    }

    // Case net is negative and income is positive
    else if (this.zNet < 0.0 && this.zIncome > 0.0) {

      this.zKeepItRunning = this.zDebt / 8.0;
      // zScore = number of Qs zcash plus half income to pay off St debt
      this.zScore = (this.zCash + this.zIncome * 0.5) / this.zKeepItRunning;

      if (this.zScore <= 8.0) {
        this.zState = ZombieStates.NNET_PINC_ENUFINCOME;
        this.zIsZombie = false;
        this.zAdjScr = this.zScore;

      }
      else {

        this.zAdjScr = (this.zCash + this.zAdjInc * 0.25) / this.zKeepItRunning;

        if (this.zAdjScr > 8.0) {
          this.zState = ZombieStates.NNET_PINC;
          this.zIsZombie = true;
        }
        else {
          this.zState = ZombieStates.NNET_PINC_DIVCUT;
          this.zIsZombie = false;
        }
      }
    }
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.report
   *
   * @param ticker
   * @param sector
   * @return
   */
  public String report(final String ticker, final String sector) {

    String ret = "";

    if (sector.equalsIgnoreCase("Financials")) {
      return ret;
    }

    if (!this.zIsZombie) {
      return ret;
    }

    System.out.printf("%-6s\t%s%n", ticker, sector);
    // zStr += " $" + ticker;
    // int len = zStr.length();
    // int m = len % 200;
    // if ((len > 100) && (m < 6)) {
    // zStr += NL;
    // }
    // zKnt++;

    switch (this.zState) {
      case PNET_PINC:
        break;
      default:
        ret = ticker + ZombieData.NL;
        ret += this.toString();
        break;
    }

    ret += this.zStatus();

    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = "";
    ret += ZombieData.TAB + "Zombie Cash       : " + QuarterlyData.fmt(this.zCash) + ZombieData.NL;
    ret += ZombieData.TAB + "Zombie Debt       : " + QuarterlyData.fmt(this.zDebt) + ZombieData.NL;
    ret += ZombieData.TAB + "Zombie Net        : " + QuarterlyData.fmt(this.zNet) + ZombieData.NL;
    ret += ZombieData.TAB + "Zombie Income     : " + QuarterlyData.fmt(this.zIncome) + ZombieData.NL;
    ret += ZombieData.TAB + "Zombie Score      : " + QuarterlyData.fmt(this.zScore) + ZombieData.NL;
    ret += ZombieData.TAB + "Zombie Adj Income : " + QuarterlyData.fmt(this.zAdjInc) + ZombieData.NL;
    ret += ZombieData.TAB + "Zombie Dividend   : " + QuarterlyData.fmt(this.zDividend) + ZombieData.NL;
    ret += ZombieData.TAB + "Zombie Adj Score  : " + QuarterlyData.fmt(this.zAdjScr) + ZombieData.NL;
    ret += ZombieData.TAB + "Operations Cost   : " + QuarterlyData.fmt(this.zKeepItRunning) + ZombieData.NL;
    ret += ZombieData.TAB + "Zombie State      : " + this.zState + ZombieData.NL;
    if (this.zIsZombie) {
      ret += ZombieData.TAB + "Is ZOMBIE!" + ZombieData.NL;
    }
    return ret;
  }

  public String zStatus() {

    String ret = "";

    if (this.zScore == 123.456) {
      return ret;
    }

    switch (this.zState) {
      case NNET_PINC_DIVCUT:
        ret += ZombieData.TAB
            + String.format("Can pay off debt in %.2f quarters by reducing the dividend with existing cash and quarterly income.", this.zAdjScr);
        break;
      case NNET_NINC:
        ret += ZombieData.TAB + String.format("Can only survive for %.2f quarters with existing cash reserves.", this.zAdjScr);
        break;
      case NNET_NINC_DIVCUT:
        ret += ZombieData.TAB + String.format("Can pay off debt in %.2f quarters by reducing the dividend.", this.zAdjScr);
        break;
      case NNET_PINC:
        if (!this.zIsZombie) {
          final String s = String.format("\tCan pay of debt in %.2f quarters.", Math.abs(this.zAdjScr));
          ret += s;
        }
        else {
          final String s = String.format("\tWill take %.2f quarters to pay off current debt with quarterly income.", Math.abs(this.zAdjScr));
          ret += s;
        }
        break;
      case PNET_NINC:
        final String s = String.format("\tOnly enough cash to continue operations for %.2f quarters.", Math.abs(this.zAdjScr));
        ret += s;
        break;
      case PNET_NINC_DIVCUT:
        ret += ZombieData.TAB + String.format("Can survive for %.2f quarters by reducing the dividend.", this.zAdjScr);
        break;
      case PNET_NINC_ENUFCASH:
        ret += ZombieData.TAB + String.format("Can survive for %.2f quarters using cash reserves.", this.zScore);
        break;
      case PNET_PINC:
        break;
      case UNKNOWN:
        break;
      case NNET_PINC_ENUFINCOME:
        ret += ZombieData.TAB + String.format("Will payoff existing ST debt in %.2f quarters using existing cash and quarterly income.", this.zScore);
        break;
      default:
        break;

    }
    return ret;
  }
}
