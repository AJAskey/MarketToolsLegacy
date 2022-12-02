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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.market.misc.Debug;
import net.ajaskey.market.optuma.TickerPriceData;

public class Reports {

  private static final double cratioLWM       = 0.75;
  private static final double intToSalesHWM   = 15.0;
  private static final double lteHWM          = 5.0;
  private static final double MILLION         = 1000000.0;
  private static final String NL              = Utils.NL;
  private static final double supplyDemandLWM = 350.0;

  /**
   *
   * @param ticker
   * @param desc
   * @param val
   * @param max
   * @return
   */
  private static boolean checkMaxValue(final String ticker, final String desc, final double val, final double max) {

    boolean ret = true;

    if (val > max) {
      final String s = ticker + desc;
      Debug.LOGGER.info(String.format("  %-15s Value : %.2f is greater than Max : %.2f%n", s, val, max));
      ret = false;
    }

    return ret;
  }

  /**
   *
   * @param ticker
   * @param desc
   * @param val
   * @param min
   * @return
   */
  private static boolean checkMinValue(final String ticker, final String desc, final double val, final double min) {

    boolean ret = true;

    if (val < min) {
      final String s = ticker + desc;
      Debug.LOGGER.info(String.format("  %-15s Value : %.2f is less than Min : %.2f%n", s, val, min));
      ret = false;
    }

    return ret;
  }

  /**
   * Main list
   */
  private List<CompanyData> companyList = null;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public Reports(final List<CompanyData> list) {

    this.companyList = list;
  }

  /**
   *
   * @throws FileNotFoundException
   */
  public void writeBestFinancial() throws FileNotFoundException {

    Utils.makeDirs("sipout/CompanyReports");

    final List<CompanyData> bestList = new ArrayList<>();

    /**
     *
     */
    int knt = 0;
    try (PrintWriter pw = new PrintWriter("sipout/BestCompanies.txt")) {

      final DateTime now = new DateTime();
      now.setSdf(Utils.sdfFull);

      pw.printf("Created : %s\t%s%n", now, "This file is subject to change without notice.");
      pw.println("Pre-filtered for US companies over $12 and average trading volume of at least 100K." + Reports.NL);

      pw.println("Seq : this quarter versus last quarter.");
      pw.println("QoQ : this quarter versus same quarter a year ago.");
      pw.println("YoY : last 12m versus 12m a year ago.\n\n--------------------------");

      for (final CompanyData cd : this.companyList) {

        if (!Reports.checkMinValue(cd.ticker, " Sales QoQ", cd.id.sales.dd.qoqGrowth, 10.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " Sales YoY", cd.id.sales.dd.yoyGrowth, 10.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " GrossOpIncome", cd.id.grossOpIncome.getMostRecent(), 0.01)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " OpMargin", cd.opMargin, 10.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " NetMargin", cd.netMargin, 10.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " Cash from Operations", cd.cashData.cashFromOps.getTtm(), 0.01)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " Equity", cd.bsd.equity.getMostRecent(), 0.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " ROE", cd.roe, 10.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " NetIncome", cd.id.netIncome.getMostRecent(), 0.01)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " NetIncome QoQ", cd.id.netIncome.dd.qoqGrowth, 25.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " IncomeEPS QoQ", cd.id.incomeEps.dd.qoqGrowth, 25.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " IncomeEPS YoY", cd.id.incomeEps.dd.yoyGrowth, 25.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " Sharehold Equity", cd.bsd.equity.getMostRecent(), 1.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " Sharehold Equity Growth", cd.bsd.equity.dd.qoqGrowth, 5.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " Insiders", cd.insiders, 1.0)) {
          continue;
        }

        if (!Reports.checkMaxValue(cd.ticker, " Interest Paid", cd.interestRate, 5.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " OpInc Growth 3Yr", cd.opInc3yrGrowth, 0.0)) {
          continue;
        }

        final double fcfwc = cd.freeCashFlow + cd.workingCapital;
        if (!Reports.checkMinValue(cd.ticker, " FCFWS", fcfwc, 0.01)) {
          continue;
        }

        if (!Reports.checkMaxValue(cd.ticker, " SupplyDemand", cd.turnover, Reports.supplyDemandLWM)) {
          continue;
        }

        if (!Reports.checkMaxValue(cd.ticker, " Price vs 52 Week High", cd.pricePercOff52High, 50.0)) {
          continue;
        }

        final boolean earnEst = cd.q0EstGrowth < 0.0 && cd.y1EstGrowth < 0.0;
        if (earnEst) {
          final String s = cd.ticker + " Negative earings estimates";
          Debug.LOGGER.info(String.format("  %-15s%n", s));
          continue;
        }

        this.printHeaderData(pw, cd);
        this.writeShareData(pw, cd);
        pw.println();

        pw.println(cd.id.sales.fmtGrowthQY("Sales 12m"));
        pw.println(cd.id.incomeEps.fmtGrowthQY("Income EPS 12m"));
        pw.printf("\tOpInc Growth 3Y   : %13.2f%%%n", cd.opInc3yrGrowth);
        pw.println();

        bestList.add(cd);

        knt++;

      }
    }
    System.out.printf("Total Best Companies found : %d%n", knt);

    try (PrintWriter pw = new PrintWriter("sipout/best-list.txt")) {
      for (final CompanyData cd : bestList) {
        pw.printf(" $%s", cd.ticker);
        // System.out.println("*adding to goodlist :" + cd.ticker);
        CompanyData.addToGoodList(cd);
      }
    }

  }

  /**
   *
   * @throws FileNotFoundException
   */
  public void writeCompanyReports() throws FileNotFoundException {

    Utils.makeDir("sipout/CompanyReports");

    final PrintWriter pwAll = new PrintWriter("sipout/CompanyReports.txt");

    for (final CompanyData cd : this.companyList) {
      try (PrintWriter pw = new PrintWriter("sipout/CompanyReports/" + cd.ticker + ".txt")) {
        this.printHeaderData(pw, cd);
        this.writeShareData(pw, cd);

        this.printHeaderData(pwAll, cd);

        this.writeShareData(pwAll, cd);
        pw.println();

        pwAll.println(cd.id.sales.fmtGrowthQY("Sales 12m"));
        pwAll.println(cd.id.incomeEps.fmtGrowthQY("Income EPS 12m"));
        pwAll.printf("\tOpInc Growth 3Y   : %13.2f%%%n", cd.opInc3yrGrowth);
        pwAll.println();
      }
      catch (final FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    pwAll.close();
  }

  /**
   *
   * @param pw
   * @param cd
   */
  public void writeCsvLine(PrintWriter pw, CompanyData cd) {

    final String exch = ":US";
//    if (cd.exchange.contains("New York")) {
//      exch = ":NYSE";
//    } else if (cd.exchange.contains("Nasdaq")) {
//      exch = ":NASDAQ";
//    }
    final String str = String.format("%s%s,", cd.ticker, exch);
    pw.printf("%s%n", str);
    // System.out.printf("Adding Zombie to list : %s%n", str);

  }

  /**
   *
   * @throws FileNotFoundException
   */
  public void writeDividendCutters() throws FileNotFoundException {

    Utils.makeDir("sipout/CompanyReports");

    final List<CompanyData> noDivList = new ArrayList<>();
    for (final CompanyData cd : this.companyList) {
      if (!cd.sector.equalsIgnoreCase("Financials")) {
        // if (cd.ticker.equalsIgnoreCase("yum")) {
        // System.out.println(cd);
        // }
        if (cd.id.dividend.getTtm() > 0.0) {
          final double wcfcf = cd.workingCapital + cd.freeCashFlow;
          final double div = cd.id.dividend.getTtm() * cd.shares.getTtmAvg();
          if (wcfcf < div) {
            cd.zscore = ZombieScore.calculate(cd);
            noDivList.add(cd);
          }
        }
      }
    }

    Collections.sort(noDivList, new SortScore());

    final List<String> divcutList = new ArrayList<>();

    try (PrintWriter pw = new PrintWriter("sipout/DividendCutters.txt")) {

      final DateTime now = new DateTime();
      now.setSdf(Utils.sdfFull);

      pw.printf("Created : %s\t%s%n", now, "This file is subject to change without notice.");
      pw.println("Pre-filtered for US companies over $12 and average trading volume of at least 100K.");
      pw.println("These companies will probably need to cut their dividend because of lack of money to pay it." + Reports.NL);

      int knt = 0;
      for (final CompanyData cd : noDivList) {
        this.printHeaderData(pw, cd);
        pw.printf("\tZombie Score        : %11.2f%n", cd.zscore.score);
        pw.println();
        if (knt < 10) {
          divcutList.add(cd.ticker);
          knt++;
        }
      }
    }
    try (PrintWriter pw = new PrintWriter("sipout/divcutlist.txt")) {
      for (final String s : divcutList) {
        pw.printf(" $%s", s);
      }
      pw.println("");
    }
  }

  /**
   *
   * @throws FileNotFoundException
   */
  public void writeGoodFinancial() throws FileNotFoundException {

    Utils.makeDir("sipout/CompanyReports");

    // final List<CompanyData> goodList = new ArrayList<>();

    final TickerPriceData spxData = new TickerPriceData("WI", "SPX");
    final double spxP1 = spxData.getOffsetPrice(125);
    final double spxP0 = spxData.getLatest();
    final double spxChg = (spxP0 - spxP1) / spxP1;

    try (PrintWriter pw = new PrintWriter("sipout/GoodCompanies.txt")) {

      final DateTime now = new DateTime();
      now.setSdf(Utils.sdfFull);

      pw.printf("Created : %s\t%s%n", now, "This file is subject to change without notice.");
      pw.println("Pre-filtered for US companies over $12 and average trading volume of at least 100K." + Reports.NL);

      pw.println("Seq : this quarter versus last quarter.");
      pw.println("QoQ : this quarter versus same quarter a year ago.");
      pw.println("YoY : last 12m versus 12m a year ago.\n\n--------------------------");

      for (final CompanyData cd : this.companyList) {

        if (!Reports.checkMinValue(cd.ticker, " Sales QoQ", cd.id.sales.dd.qoqGrowth, 1.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " Sales YoY", cd.id.sales.dd.yoyGrowth, 1.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " GrossOpIncome", cd.id.grossOpIncome.getMostRecent(), 0.01)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " OpMargin", cd.opMargin, 1.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " NetMargin", cd.netMargin, 1.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " Cash from Operations", cd.cashData.cashFromOps.getTtm(), 0.01)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " Equity", cd.bsd.equity.getMostRecent(), 0.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " ROE", cd.roe, 1.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " NetIncome", cd.id.netIncome.getMostRecent(), 0.01)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " NetIncome QoQ", cd.id.netIncome.dd.qoqGrowth, 1.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " IncomeEPS QoQ", cd.id.incomeEps.dd.qoqGrowth, 1.0)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " IncomeEPS YoY", cd.id.incomeEps.dd.yoyGrowth, 1.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " Sharehold Equity", cd.bsd.equity.getMostRecent(), 0.1)) {
          continue;
        }
        if (!Reports.checkMinValue(cd.ticker, " Sharehold Equity Growth", cd.bsd.equity.dd.qoqGrowth, 0.01)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " Insiders", cd.insiders, 1.0)) {
          continue;
        }

        if (!Reports.checkMaxValue(cd.ticker, " Interest Paid", cd.interestRate, 5.0)) {
          continue;
        }

        if (!Reports.checkMinValue(cd.ticker, " OpInc Growth 3Yr", cd.opInc3yrGrowth, 0.0)) {
          continue;
        }

        final double fcfwc = cd.freeCashFlow + cd.workingCapital;
        if (!Reports.checkMinValue(cd.ticker, " FCFWS", fcfwc, 0.01)) {
          continue;
        }

        final TickerPriceData tpd = new TickerPriceData(cd.exchange, cd.ticker);

        final double p1 = tpd.getOffsetPrice(125);
        if (p1 <= 0.0) {
          continue;
        }
        final double p0 = tpd.getLatest();
        final double chg = (p0 - p1) / p1;
        if (spxChg > chg) {
          continue;
        }

        final boolean earnEst = cd.q0EstGrowth < 0.0 && cd.y1EstGrowth < 0.0;
        if (earnEst) {
          final String s = cd.ticker + " Negative earings estimates";
          Debug.LOGGER.info(String.format("  %-15s%n", s));
          continue;
        }

        this.printHeaderData(pw, cd);
        this.writeShareData(pw, cd);
        pw.println();

        pw.println(cd.id.sales.fmtGrowthQY("Sales 12m"));
        pw.println(cd.id.incomeEps.fmtGrowthQY("Income EPS 12m"));
        pw.printf("\tOpInc Growth 3Y   : %13.2f%%%n", cd.opInc3yrGrowth);
        pw.println();

        CompanyData.addToGoodList(cd);
      }
    }

  }

  /**
   * Prints to comma separated file
   */
  public void writeSpreadsheetData() {

    try (PrintWriter pw = new PrintWriter("sipout/spx-fundies.csv")) {

      pw.println("Ticker,Sector,Industry,Index,Exch,Last Q,Sales Q5,Sales Q1,Sales Chg,Net Q5,Net Q1,Net Chg,"
          + "CashOps pTTM,CashOps TTM,CashOps Chg,PE,Q0 Est,Y1 Est");

      for (final CompanyData cd : this.companyList) {
        double pe = -0.001;
        if (cd.id.netIncome.dd.ttm > 0.0) {
          final double petmp = cd.lastPrice / (cd.id.netIncome.dd.ttm / cd.shares.q1);
          pe = Math.min(petmp, 300.0);
        }
        final String sector = cd.sector.replace(",", " ");
        final String industry = cd.industry.replace(",", " ");
        final double cops = cd.cashData.cashFromOps.getTtm();
        final double cops58 = cd.cashData.cashFromOps.getPrevTtm();
        double chg = 0.0;
        if (cops58 != 0.0) {
          chg = (cops - cops58) / Math.abs(cops58) * 100.0;
        }
        final String s = String.format("%s,%s,%s,%s,%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f", cd.ticker, sector, industry, cd.spIndex,
            cd.getExchange(), cd.eoq, cd.id.sales.q1, cd.id.sales.q5, cd.id.sales.dd.qoqGrowth, cd.id.netIncome.q5, cd.id.netIncome.q1,
            cd.id.netIncome.dd.qoqGrowth, cops58, cops, chg, pe, cd.q0EstGrowth, cd.y1EstGrowth);
        pw.println(s);
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.WriteZombies
   *
   * @throws FileNotFoundException
   */
  public void writeZombies(String prefix) throws FileNotFoundException {

    Utils.makeDir("sipout/CompanyReports");

    final List<CompanyData> zombieList = new ArrayList<>();
    for (final CompanyData cd : this.companyList) {

      if (!cd.sector.equalsIgnoreCase("Financials")) {
        if (cd.lastPrice >= 20.0) {
          if (cd.adv > 250000) {
            final String state = this.getState(cd);
            if (!state.contains("Good shape - no red flags")) {
              cd.zscore = ZombieScore.calculate(cd);
              if (cd.zscore.score > 50.0) {
                zombieList.add(cd);
              }
              else {
                Debug.LOGGER.info(String.format("ZOMBIE Check Fail : %s zscore=%.2f  rs=%.2f%n", cd.ticker, cd.zscore.score, cd.rs));
              }
            }
            else {
              Debug.LOGGER.info(String.format("ZOMBIE Check Fail : %s%n", state.replace(Utils.NL, "")));
            }
          }
          else {
            Debug.LOGGER.info(String.format("ZOMBIE Check Fail : %s volume=%d%n", cd.ticker, cd.adv));
          }
        }
        else {
          Debug.LOGGER.info(String.format("ZOMBIE Check Fail : %s price=%.2f%n", cd.ticker, cd.lastPrice));
        }
      }
      else {
        Debug.LOGGER.info(String.format("ZOMBIE Check Fail : %s sector=%s%n", cd.ticker, cd.sector));
      }

    }

    Collections.sort(zombieList, new SortScore());

    final List<CompanyData> finalZombieList = new ArrayList<>();
    int knt = 0;
    for (final CompanyData cd : zombieList) {
      if (cd.exchange.equalsIgnoreCase("NEW YORK") || cd.exchange.equalsIgnoreCase("NASDAQ") || cd.exchange.contains("American")) {
        if (knt++ < 150) {
          System.out.printf("Zombie adding : %10s : %s%n", cd.ticker, cd.exchange);
          finalZombieList.add(cd);
        }
        else {
          break;
        }
      }
      else {
        System.out.printf("Zombie NOT adding : %10s : %s%n", cd.ticker, cd.exchange);
      }
    }

    try (PrintWriter pw = new PrintWriter("sipout/" + prefix + "Zombie-Sectors.txt")) {

      final DateTime now = new DateTime();
      now.setSdf(Utils.sdfFull);

      pw.printf("Created : %s\t%s%n", now, "This file is subject to change without notice.");
      pw.println("Pre-filtered for AMEX, NASDAQ, NYSE companies over $20 and average trading volume of at least 100K.");

      for (final String sec : CompanyData.sectorList) {
        if (sec.equalsIgnoreCase("financials") || sec.equalsIgnoreCase("utilities")) {
          continue;
        }
        knt = 1;
        pw.printf("%n%n====================================%nSector : %s%n====================================%n", sec);
        for (final CompanyData cd : finalZombieList) {
          if (sec.equalsIgnoreCase(cd.sector)) {
            final String state = this.getState(cd);
            pw.printf("%nRank : %d%n", knt++);
            this.printHeaderData(pw, cd);
            pw.printf("%s", state);
            pw.printf("%s%n", cd.zscore);
          }
        }
      }
    }

    try (PrintWriter pw = new PrintWriter("sipout/" + prefix + "Zombies.txt")) {

      final DateTime now = new DateTime();
      now.setSdf(Utils.sdfFull);

      pw.printf("Created : %s\t%s%n", now, "This file is subject to change without notice.");
      pw.println("Pre-filtered for AMEX, NASDAQ, NYSE companies over $20 and average trading volume of at least 100K.");

      pw.printf("%nList of tickers with Cash from Operations less than Working Capital deficit.%n");
      String str = "";
      for (final CompanyData cd : finalZombieList) {
        // System.out.println(cd.ticker);
        if (cd.cashData.cashFromOps.getTtm() < cd.workingCapital && cd.workingCapital < 0.0) {
          str = this.addStr(cd.ticker, str);
        }
      }
      pw.println(str);

      pw.printf("%nList of tickers with Current Ratio < %.2f and paying more than %.1f%% of Sales to Interest.%n", 1.0, Reports.intToSalesHWM);
      str = "";
      for (final CompanyData cd : finalZombieList) {
        // System.out.println(cd.ticker);
        if (cd.currentRatio < 1.0 && cd.interestRate > Reports.intToSalesHWM) {
          str = this.addStr(cd.ticker, str);
        }
      }
      pw.println(str);

      pw.printf("%nList of tickers with FCF + Working Capital less than 0.%n*Paid a dividend -- may need to cut dividend.%n");
      str = "";
      for (final CompanyData cd : finalZombieList) {
        String div = "";
        if (cd.freeCashFlow + cd.workingCapital < 0.0) {
          if (cd.id.dividend.getTtm() > 0.0) {
            div += "*";
          }
          str = this.addStr(cd.ticker + div, str);
        }
      }
      pw.println(str);

      pw.printf("%nList of tickers with negative Cash from Ops and Cash Flow with no Shareholder Equity.%n");
      str = "";
      for (final CompanyData cd : finalZombieList) {
        if (cd.cashData.cashFromOps.getTtm() < 0.0 && cd.bsd.equity.getMostRecent() < 0.0 && cd.cashFlow < 0.0) {
          str = this.addStr(cd.ticker, str);
        }
      }
      pw.println(str);

      pw.printf("%nList of tickers with big buybacks and an available cash flow deficit.%n");
      str = "";
      for (final CompanyData cd : finalZombieList) {

        final double sc = DerivedData.calcShareChange(cd);
        if (sc < -0.25) {
          final double fcfwc = cd.freeCashFlow + cd.workingCapital;
          if (fcfwc < 0.0 && Math.abs(sc) * cd.avgPrice > Math.abs(fcfwc)) {
            str = this.addStr(cd.ticker, str);
          }
        }
      }
      pw.println(str);

      pw.println("\nSeq : this quarter versus last quarter.");
      pw.println("QoQ : this quarter versus same quarter a year ago.");
      pw.println("YoY : last 12m versus 12m a year ago.\n\n--------------------------");

      try (PrintWriter pwPz = new PrintWriter("sipout/" + prefix + "Pre-Zombies.txt")) {
        for (final CompanyData cd : finalZombieList) {

          if (cd.zscore.score < 80.0) {
            final double fcfwc = cd.freeCashFlow + cd.workingCapital;
            if (fcfwc < 0.0) {
              this.printHeaderData(pwPz, cd);
              pwPz.printf("%s", this.getState(cd));
              pwPz.printf("%s%n", cd.zscore);
            }
          }
        }
      }

      knt = 1;
      int rank = 1;
      try (PrintWriter pwCode = new PrintWriter("sipout/" + prefix + "zombie-list.txt");
          PrintWriter pwCsv = new PrintWriter("sipout/" + prefix + "Zombies.csv");
          PrintWriter pwSc = new PrintWriter("sipout/" + prefix + "Zombies-sc.txt")) {

        pwCsv.println("Ticker,");
        pwCsv.println("SPY:US,");
        pwCsv.println("QQQ:US,");
        pwCsv.println("IWM:US,");
        pwCsv.println("DIA:US,");

        for (final CompanyData cd : finalZombieList) {

          final String state = this.getState(cd);
          pw.printf("%nRank : %d%n", rank++);
          this.printHeaderData(pw, cd);
          pw.printf("%s", state);
          pw.printf("%s%n", cd.zscore);

          if (cd.zscore.score >= 80.0 || cd.zscore.score >= 70.0 && cd.marketCap < 2000.0) {

            pwCode.printf(" $%s", cd.ticker);
            if (cd.lastPrice > 12.0) {
              this.writeCsvLine(pwCsv, cd);
              pwSc.println(cd.ticker);
              knt++;
            }
          }

          if (knt > 100) {
            break;
          }
        }
      }
    }
  }

  /**
   * net.ajaskey.market.tools.SIP.addStr
   *
   * @param ticker
   * @param str
   */
  private String addStr(final String ticker, String str) {

    str += " " + ticker;
    final int len = str.length() - str.lastIndexOf(Reports.NL);
    if (len > 85) {
      str += Reports.NL;
    }
    return str;
  }

  /**
   * net.ajaskey.market.tools.SIP.printState
   *
   * @param pw
   * @param cd
   */
  private String getState(final CompanyData cd) {

    String ret = String.format("%n\t%s State -->%n", cd.ticker);

    boolean goodState = true;

    final double fcfwc = cd.freeCashFlow + cd.workingCapital;

    if (cd.cashData.cashFromOps.getTtm() < cd.workingCapital && cd.workingCapital < 0.0) {
      ret += String.format("\t  Cash from Operations less than Working Capital deficit%n");
      goodState = false;
    }

    if (fcfwc < 0.0 && cd.id.dividend.getTtm() <= 0.0) {
      ret += String.format("\t  Free Cash Flow plus Working Capital is negative (%.2f)%n", cd.freeCashFlow + cd.workingCapital);
      goodState = false;
    }

    if (fcfwc < 0.0 && cd.id.dividend.getTtm() > 0.0) {
      ret += String.format("\t  Free Cash Flow plus Working Capital is negative (%.2f) with dividend paid %.2f.%n", fcfwc,
          cd.id.dividend.getTtm() * cd.shares.getTtmAvg());
      goodState = false;
    }

    if (cd.currentRatio < Reports.cratioLWM && cd.totalCashFlow < 0.0) {
      ret += String.format("\t  Current Ratio less than %.2f and Cash Flow less than 0.%n", Reports.cratioLWM);
      goodState = false;
    }

    if (fcfwc < 0.0 && cd.cashData.cashFromFin.getTtm() > 0.0 && cd.cashData.cashFromFin.getTtm() > Math.abs(fcfwc)) {
      ret += String.format("\t  Running company with Financing. Cash from Financing greater than FCF plus Working Capital.%n", Reports.cratioLWM);
      goodState = false;
    }

    if (cd.id.interestExpNonOp.getTtm() > 0.0 && cd.interestRate > Reports.intToSalesHWM) {
      ret += String.format("\t  Interest payments to sales is high at %.2f%%.%n", cd.interestRate);
      goodState = false;
    }

    if (cd.bsd.equity.getMostRecent() <= 0.0) {
      final double tmp = cd.bsd.assetsMinusGW.getMostRecent() + cd.bsd.equity.getMostRecent();
      if (tmp < 0.0) {
        ret += String.format("\t  Shareholders have no equity.%n");
        goodState = false;
      }
    }
    else {
      final double dte = cd.bsd.ltDebt.getMostRecent() / cd.bsd.equity.getMostRecent();
      if (dte > Reports.lteHWM) {
        ret += String.format("\t  LT debt to equity of %.2f is high.%n", dte);
        goodState = false;
      }
    }

    // if (cd.zd.zIsZombie) {
    // pw.println("\t Is a Zombie by algorithm.");
    // goodState = false;
    // }

    if (goodState) {
      ret += String.format("\t  Good shape - no red flags.%n");
    }

    return ret;
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.printHeaderData
   *
   * @param pw
   * @param cd
   */
  private void printHeaderData(final PrintWriter pw, final CompanyData cd) {

    pw.println(" " + cd.ticker);
    pw.printf("\t%s : %s, %s%n", cd.name, cd.city, cd.state);

    String index = "";
    if (cd.spIndex.length() > 0) {
      index = ", " + cd.spIndex;
    }

    pw.printf("\t%s, %s, %s%s%n", cd.sector, cd.industry, cd.exchange.replace("A - ", ""), index);
    // pw.printf("\t%s%n", cd.industry);
    String sNumEmp = "?";
    if (cd.numEmp > 0) {
      sNumEmp = QuarterlyData.ifmt(cd.numEmp, 12);
    }
    pw.printf("\tEmployees     : %s%n", sNumEmp);
    if (cd.numEmp > 0) {
      final double d = cd.id.grossOpIncome.getTtm() / cd.numEmp * Reports.MILLION;
      final int i = (int) d;
      pw.printf("\tOpInc per Emp : $%s%n", QuarterlyData.ifmt(i, 11));
    }

    final String dat = cd.eoq.toString(); // Utils.stringDate(cd.eoq);
    pw.printf("\t10Q Date      :  %s%n", dat);

    // this.printState(pw, cd);

    // if (cd.ticker.equalsIgnoreCase("MAXR")) {
    // System.out.println(cd);
    // }
    pw.printf("%n\tMarket Cap        : %s M%n", QuarterlyData.fmt(cd.marketCap, 13));
    pw.println(cd.shares.fmtGrowth1Q("Shares"));

    final double sc = DerivedData.calcShareChange(cd);
    if (sc < -0.250) {
      final double bbest = Math.abs(sc) * cd.avgPrice;
      pw.printf("\tShare Change 12m  : %s M (Buyback Est= $%sM)%n", QuarterlyData.fmt(sc, 13), QuarterlyData.fmt(bbest, 1));
    }

    double salesps = cd.id.sales.getTtm() / cd.shares.getTtmAvg();
    double pSales = cd.lastPrice / salesps;
    pw.printf("%n%s :: SalesPS=%.2f --> pSales=%.2f%n", cd.id.sales.fmtGrowth4Q("Sales 12m"), salesps, pSales);
    pw.println(cd.id.cogs.fmtGrowth4Q("COGS 12m"));
    pw.println(cd.id.grossOpIncome.fmtGrowth4Q("Ops Income 12m"));
    pw.println(cd.id.netIncome.fmtGrowth4Q("Net Income 12m"));
    pw.println(cd.id.totalInterest.fmtGrowth4Q("Interest Paid 12m"));

    final double totdebt = cd.bsd.stDebt.getMostRecent() + cd.bsd.ltDebt.getMostRecent();
    double intrate = 0.0;
    if (totdebt > 0.0) {
      intrate = cd.id.totalInterest.getTtm() / totdebt * 100.0;
    }
    pw.printf("\tInterest Rate     :%14.2f%%%n", intrate);

    pw.println("\n" + cd.cashData.cashFromOps.fmtGrowth4Q("Cash <- Ops 12m"));
    pw.println(cd.cashData.capEx.fmtGrowth4Q("  CapEx 12m"));
    if (cd.id.dividend.getTtm() > 0.0) {
      pw.printf("\t  Dividends 12m   : %s M (Yield=%.2f%%)%n", QuarterlyData.fmt(cd.id.dividend.getTtm() * cd.shares.getTtmAvg(), 13), cd.divYld);

    }
    else {
      pw.printf("\t  Dividends 12m   : %s M%n", QuarterlyData.fmt(cd.id.dividend.getTtm() * cd.shares.getTtmAvg(), 13));
    }
    pw.printf("\t    FCF 12m       : %s M %s%n", QuarterlyData.fmt(cd.freeCashFlow, 13), "[Cash from Operations - CapEx - Dividends]");
    pw.printf("\tCash <- Fin 12m   : %s M %s%n", QuarterlyData.fmt(cd.cashData.cashFromFin.getTtm(), 13),
        "[Movement of cash between a firm and its owners/creditors : borrowing, debt repayment, dividend paid, equity financing.]");
    pw.printf("\t  Net Cash 12m    : %s M %s%n", QuarterlyData.fmt(cd.netCashFlow, 13), "[Cash from Ops + Cash from Financing]");
    pw.printf("\tCash <- Inv 12m   : %s M %s%n", QuarterlyData.fmt(cd.cashData.cashFromInv.getTtm(), 13),
        "[Purchases and sales of long-term assets such as plant and machinery - assumed infrequent.]");
    pw.printf("\t  Cash Flow 12m   : %s M %s%n", QuarterlyData.fmt(cd.netCashFlow + cd.cashData.cashFromInv.getTtm(), 13),
        "[Cash from Ops + Cash from Financing + Cash from Investing]");

    // pw.println("\n" + cd.bsd.cash.fmtGrowth1Q("Cash Available"));

    this.printWorkingCapital(pw, cd, true);

    pw.println("\n" + cd.bsd.equity.fmtGrowth1Q("Sharehldr Equity"));
    pw.println(cd.bsd.assetsMinusGW.fmtGrowth1Q("Tangible Assets"));
    pw.println(cd.bsd.ltDebt.fmtGrowth1Q("LT Debt"));

    if (cd.bsd.equity.getMostRecent() > 0.0) {
      pw.printf("\tLT Debt to Equity : %s%n", QuarterlyData.fmt(cd.bsd.ltDebt.getMostRecent() / cd.bsd.equity.getMostRecent(), 13));
    }
    else {
      pw.printf("\tLT Debt Tan Asset : %s%n", QuarterlyData.fmt(cd.bsd.ltDebt.getMostRecent() / cd.bsd.assetsMinusGW.getMostRecent(), 13));
    }

    pw.printf("%n\tLast Price          : %s : (52wkHi= %.2f %%offHigh=%d%%) :: RS= %.2f%n", QuarterlyData.fmt(cd.lastPrice, 11), cd.high52wk,
        (int) cd.pricePercOff52High, cd.rs);
    pw.printf("\tPE                  : %s%n", QuarterlyData.fmt(cd.pe, 11));
    pw.printf("\tOp Margin           : %s%%%n", QuarterlyData.fmt(cd.opMargin, 11));
    pw.printf("\tNet Margin          : %s%%%n", QuarterlyData.fmt(cd.netMargin, 11));
    pw.printf("\tROE                 : %s%%%n", QuarterlyData.fmt(cd.roe, 11));
    pw.printf("\tInterest %% of sales : %s%%%n", QuarterlyData.fmt(cd.interestRate, 11));
    if (cd.id.epsDilCont.getTtm() > 0.0) {
      pw.printf("\tEPS Yield           : %s%% ($%.2f)%n", QuarterlyData.fmt(cd.epsYld, 11), cd.id.epsDilCont.getTtm(), 2);
    }
    pw.printf("\tQ0 Est Growth       : %11.2f%%%n", cd.q0EstGrowth);
    pw.printf("\tY1 Est Growth       : %11.2f%%%n", cd.y1EstGrowth);

  }

  private void printWorkingCapital(final PrintWriter pw, final CompanyData cd, boolean expanded) {

    if (cd.bsd.currentAssets.getMostRecent() > 0.0) {
      pw.println(Reports.NL + cd.bsd.currentAssets.fmtGrowth1Q("Current Assets"));
      // final double ca = cd.bsd.cash.getMostRecent() +
      // cd.bsd.acctReceiveable.getMostRecent() + cd.bsd.stInvestments.getMostRecent()
      // + cd.bsd.inventory.getMostRecent() + cd.bsd.otherAssets.getMostRecent();
      if (expanded) {
        pw.println(cd.bsd.cash.fmtGrowth1Q("  Cash"));
        pw.println(cd.bsd.acctReceiveable.fmtGrowth1Q("  Acct Rx"));
        pw.println(cd.bsd.stInvestments.fmtGrowth1Q("  ST Invest"));
        pw.println(cd.bsd.inventory.fmtGrowth1Q("  Inventory"));
        pw.println(cd.bsd.otherAssets.fmtGrowth1Q("  Other"));
      } // final double cl = cd.bsd.acctPayable.getMostRecent() +
        // cd.bsd.stDebt.getMostRecent() + cd.bsd.otherCurrLiab.getMostRecent();
      pw.println(cd.bsd.currLiab.fmtGrowth1Q("Current Liabs"));
      if (expanded) {
        pw.println(cd.bsd.acctPayable.fmtGrowth1Q("  Acct Pay"));
        pw.println(cd.bsd.stDebt.fmtGrowth1Q("  ST Debt"));
        pw.println(cd.bsd.otherCurrLiab.fmtGrowth1Q("  Other"));
      }
    }
    else {
      pw.printf("%n\tCurrent Assets    : %s M%n", QuarterlyData.fmt(cd.sumCurrAssets, 13));
      pw.printf("\tCurrent Liabs     : %s M%n", QuarterlyData.fmt(cd.sumCurrLiab, 13));
    }
    pw.printf("\tWorking Capital   : %s M (Ratio=%.2f)%n", QuarterlyData.fmt(cd.workingCapital, 13), cd.currentRatio);
    pw.printf("\tWC + FCF          : %s M%n", QuarterlyData.fmt(cd.workingCapital + cd.freeCashFlow, 13));
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.WriteShareData
   *
   * @param pw
   * @param cd
   */
  private void writeShareData(final PrintWriter pw, final CompanyData cd) {

    pw.printf("%n\tFloat             : %s M%n", QuarterlyData.fmt(cd.floatShares, 13));
    double d = cd.insiders * cd.floatShares / 100.0;
    pw.printf("\tInsiders          : %s M (%s%%)%n", QuarterlyData.fmt(d, 13), QuarterlyData.fmt(cd.insiders, 5));
    d = cd.inst * cd.floatShares / 100.0;
    pw.printf("\tInstitutions      : %s M (%s%%)%n", QuarterlyData.fmt(d, 13), QuarterlyData.fmt(cd.inst, 5));
    pw.printf("\tAvg Daily Vol     : %s%n", QuarterlyData.ifmt(cd.adv, 13));
    pw.printf("\tTurnover Float    : %s days%n", QuarterlyData.fmt(cd.turnover, 13));

  }

  public void writeZombieList(List<String> zList) {

  }

  public void writeInsiders() {
    // TODO Auto-generated method stub

  }

  public void writeInstitutions() {
    // TODO Auto-generated method stub

  }

}
