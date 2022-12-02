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
package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.market.misc.Debug;
import net.ajaskey.market.tools.SIP.BigDB.MarketTools;
import net.ajaskey.market.tools.SIP.BigDB.collation.QuarterlyDouble;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class FundamentalReports extends Fundamentals {

  public static void write() throws FileNotFoundException {

    System.out.println("Writing Fundamental Report");

    Utils.makeDir("out/CompanyReports");

    new ArrayList<>();

    try (PrintWriter pw = new PrintWriter("out/Companies.txt")) {

      final DateTime now = new DateTime();
      now.setSdf(Utils.sdfFull);

      pw.printf("Created : %s\t%s%n", now, "This file is subject to change without notice.");
      pw.println("Pre-filtered for US companies over $12 and average trading volume of at least 100K." + Utils.NL);

      pw.println("Seq : this quarter versus last quarter.");
      pw.println("QoQ : this quarter versus same quarter a year ago.");
      pw.println("YoY : last 12m versus 12m a year ago.\n\n--------------------------");

      for (final FieldData fd : Fundamentals.fdList) {

        final String lq = MarketTools.getLatestQtrEps(fd).toString().trim();
        if (lq.length() < 8) {
          continue;
        }

        final boolean earnEst = MarketTools.getEpsQ1(fd) < 0.0 && MarketTools.getEpsQ0(fd) < 0.0;
        if (earnEst) {
          final String s = fd.getTicker() + " Negative earings estimates";
          Debug.LOGGER.info(String.format("  %-15s%n", s));
          continue;
        }

        FundamentalReports.printHeaderData(pw, fd);

        pw.printf("%n\tFloat             : %s M%n", Utils.fmt(MarketTools.getFloatshr(fd), 13));
        double d = MarketTools.getInsiderOwnership(fd);
        pw.printf("\tInsiders          : %s %%%n", Utils.fmt(d, 13));
        d = MarketTools.getInstOwnership(fd);
        pw.printf("\tInstitutions      : %s %% (Number %s)%n", Utils.fmt(d, 13), Utils.ifmt(MarketTools.getInstShareholders(fd), 5));

        if (MarketTools.getVolume10d(fd) > 999) {
          pw.printf("\tAvg Daily Vol     : %s M%n", Utils.fmt(MarketTools.getVolume10d(fd) / 1000.0, 13));
        }
        else {
          pw.printf("\tAvg Daily Vol     : %s K%n", Utils.lfmt(MarketTools.getVolume10d(fd), 13));
        }

        double turnover = 0.0;
        if (MarketTools.getVolumeMonth3m(fd) > 0.0) {
          turnover = MarketTools.getFloatshr(fd) / (MarketTools.getVolume10d(fd) / 1000.0);
        }
        pw.printf("\tTurnover Float    : %s days%n", Utils.fmt(turnover, 13));

//        this.WriteShareData(pw, cd);
        pw.println();

//        pw.println(fd.id.sales.fmtGrowthQY("Sales 12m"));
//        pw.println(fd.id.incomeEps.fmtGrowthQY("Income EPS 12m"));
//        pw.printf("\tOpInc Growth 3Y   : %13.2f%%%n", fd.opInc3yrGrowth);
//        pw.println();
//
//        bestList.add(cd);

      }
    }
//    System.out.printf("Total Best Companies found : %d%n", knt);
//
//    try (PrintWriter pw = new PrintWriter("out/best-list.txt")) {
//      for (final CompanyData cd : bestList) {
//        pw.printf(" $%s", fd.getTicker());
//        // System.out.println("*adding to goodlist :" + fd.getTicker());
//        CompanyData.addToGoodList(cd);
//      }
//    }

  }

  private static boolean checkMaxValue(final String ticker, final String desc, final double val, final double max) {

    boolean ret = true;

    if (val > max) {
      final String s = ticker + desc;
      Debug.LOGGER.info(String.format("  %-15s Value : %.2f is greater than Max : %.2f%n", s, val, max));
      ret = false;
    }

    return ret;
  }

  private static boolean checkMinValue(final String ticker, final String desc, final double val, final double min) {

    boolean ret = true;

    if (val < min) {
      final String s = ticker + desc;
      Debug.LOGGER.info(String.format("  %-15s Value : %.2f is less than Min : %.2f%n", s, val, min));
      ret = false;
    }

    return ret;
  }

  private static void printHeaderData(final PrintWriter pw, final FieldData fd) {

    pw.println(" " + fd.getTicker());
    pw.printf("\t%s : %s, %s%n", fd.getName(), MarketTools.getCity(fd), MarketTools.getState(fd));

    final String index = ", " + MarketTools.getSnpIndexStr(fd);
    final String exch = ", " + fd.getExchange().toString();

    pw.printf("\t%s, %s%s%s%n", fd.getSector(), fd.getIndustry(), index, exch);
    // pw.printf("\t%s%n", fd.industry);
    String sNumEmp = "?";
    if (MarketTools.getNumEmployees(fd) > 0) {
      sNumEmp = Utils.ifmt(MarketTools.getNumEmployees(fd), 12);
    }
    pw.printf("\tEmployees     : %s%n", sNumEmp);
    if (MarketTools.getNumEmployees(fd) > 0) {
      final QuarterlyDouble grossIncQdata = new QuarterlyDouble(MarketTools.getGrossIncQtr(fd));
      final double d = grossIncQdata.getTtm() / MarketTools.getNumEmployees(fd) * Fundamentals.MILLION;
      final int i = (int) d;
      pw.printf("\tOpInc per Emp : $%s%n", Utils.ifmt(i, 11));
    }

    final String dat = MarketTools.getLatestQtrEps(fd).format("yyyy-MMM-dd"); // Utils.stringDate(fd.eoq);
    pw.printf("\t10Q Date      :  %s%n", dat);

    pw.printf("\tPrice         :  %11.2f%n", MarketTools.getPrice(fd));
    pw.printf("%n\tMarket Cap        : %s M%n", Utils.fmt(MarketTools.getMktCap(fd), 13));

    final QuarterlyDouble shares = new QuarterlyDouble(MarketTools.getSharesQtr(fd));
    pw.println(shares.fmtGrowth1Q("Shares"));
    final double sc = shares.deltaQ(1, 2);
    if (sc < -0.250) {
      final double bbest = Math.abs(sc) * ((MarketTools.getPrice52hi(fd) + MarketTools.getPrice52lo(fd)) / 2.0);
      pw.printf("\tShare Change 12m  : %s M (Buyback Est= $%sM)%n", Utils.fmt(sc, 13), Utils.fmt(bbest, 1));
    }

    final QuarterlyDouble sales = new QuarterlyDouble(MarketTools.getSalesQtr(fd));
    final QuarterlyDouble cogs = new QuarterlyDouble(MarketTools.getCogsQtr(fd));
    final QuarterlyDouble grossOpInc = new QuarterlyDouble(MarketTools.getGrossIncQtr(fd));
    final QuarterlyDouble net = new QuarterlyDouble(MarketTools.getNetIncQtr(fd));

    final double tArr[] = new double[9];
    for (int i = 0; i < tArr.length; i++) {
      tArr[i] = MarketTools.getIntExpQtr(fd)[i] + MarketTools.getIntExpNonOpQtr(fd)[i];
    }
    final QuarterlyDouble intTot = new QuarterlyDouble(tArr);

    pw.println("\n" + sales.fmtGrowth4Q("Sales 12m"));
    pw.println(cogs.fmtGrowth4Q("COGS 12m"));
    pw.println(grossOpInc.fmtGrowth4Q("Ops Income 12m"));
    pw.println(net.fmtGrowth4Q("Net Income 12m"));
    pw.println(intTot.fmtGrowth4Q("Interest 12m"));
//
    final double totdebt = MarketTools.getStDebtQtr(fd)[1] + MarketTools.getLtDebtQtr(fd)[1];
    double intrate = 0.0;
    if (totdebt > 0.0) {
      intrate = intTot.getTtm() / totdebt * 100.0;
    }
    pw.printf("\tInterest Rate     :%14.2f%%%n", intrate);
//
    final QuarterlyDouble cash = new QuarterlyDouble(MarketTools.getCashQtr(fd));
    final QuarterlyDouble capex = new QuarterlyDouble(fd.getCashData().getCapExQtr());
    final QuarterlyDouble cashops = new QuarterlyDouble(fd.getCashData().getCashFromOpsQtr());
    final QuarterlyDouble dividend = new QuarterlyDouble(MarketTools.getDividendQtr(fd));

    pw.println("\n" + cashops.fmtGrowth4Q("Cash <- Ops 12m"));
    pw.println(capex.fmtGrowth4Q("  CapEx 12m"));
    if (MarketTools.getDividendQtr(fd)[1] > 0.0) {
      final double div = dividend.getTtm() * shares.getTtmAvg();
      final double dyld = dividend.getTtm() / MarketTools.getPrice(fd) * 100.0;
      pw.printf("\t  Dividends 12m   : %s M (Yield=%.2f%%)%n", Utils.fmt(div, 13), dyld);

    }
    else {
      final double d = dividend.getTtm() * shares.getTtmAvg();
      pw.printf("\t  Dividends 12m   : %s M%n", Utils.fmt(d, 13));
    }

    final double fcf = cashops.getTtm() - capex.getTtm() - dividend.getTtm() * shares.getTtmAvg();

    final QuarterlyDouble cashfin = new QuarterlyDouble(MarketTools.getCashFromFinQtr(fd));
    final QuarterlyDouble cashinv = new QuarterlyDouble(MarketTools.getCashFromInvQtr(fd));

    final double netcashflow = cashops.getTtm() + cashfin.getTtm();
    final double cashflow = netcashflow + cashinv.getTtm();

    pw.printf("\t    FCF 12m       : %s M %s%n", Utils.fmt(fcf, 13), "[Cash from Operations - CapEx - Dividends]");

    pw.printf("\tCash <- Fin 12m   : %s M %s%n", Utils.fmt(cashfin.getTtm(), 13),
        "[Movement of cash between a firm and its owners/creditors : borrowing, debt repayment, dividend paid, equity financing.]");

    pw.printf("\t  Net Cash 12m    : %s M %s%n", Utils.fmt(netcashflow, 13), "[Cash from Ops + Cash from Financing]");

    pw.printf("\tCash <- Inv 12m   : %s M %s%n", Utils.fmt(cashinv.getTtm(), 13),
        "[Purchases and sales of long-term assets such as plant and machinery - assumed infrequent.]");

    pw.printf("\t  Cash Flow 12m   : %s M %s%n", Utils.fmt(cashflow, 13), "[Cash from Ops + Cash from Financing + Cash from Investing]");

    //

    final QuarterlyDouble acctRx = new QuarterlyDouble(MarketTools.getAcctRxQtr(fd));
    final QuarterlyDouble stInvest = new QuarterlyDouble(MarketTools.getStInvestQtr(fd));
    final QuarterlyDouble inventory = new QuarterlyDouble(MarketTools.getInventoryQtr(fd));
    final QuarterlyDouble otherAssets = new QuarterlyDouble(MarketTools.getOtherCurrAssetsQtr(fd));

    final double aArr[] = new double[9];
    for (int i = 0; i < aArr.length; i++) {
      aArr[i] = acctRx.get(i) + stInvest.get(i) + inventory.get(i) + otherAssets.get(i) + cash.get(i);
    }
    final QuarterlyDouble currAssets = new QuarterlyDouble(aArr);

    pw.println(Utils.NL + currAssets.fmtGrowth1Q("Current Assets"));
    pw.println(cash.fmtGrowth1Q("  Cash"));
    pw.println(acctRx.fmtGrowth1Q("  Acct Rx"));
    pw.println(stInvest.fmtGrowth1Q("  ST Invest"));
    pw.println(inventory.fmtGrowth1Q("  Inventory"));
    pw.println(otherAssets.fmtGrowth1Q("  Other"));

    final QuarterlyDouble acctPay = new QuarterlyDouble(MarketTools.getAcctPayableQtr(fd));
    final QuarterlyDouble stDebt = new QuarterlyDouble(MarketTools.getStDebtQtr(fd));
    final QuarterlyDouble otherLiab = new QuarterlyDouble(MarketTools.getOtherCurrLiabQtr(fd));

    final double cArr[] = new double[9];
    for (int i = 0; i < cArr.length; i++) {
      cArr[i] = acctPay.get(i) + stDebt.get(i) + otherLiab.get(i);
    }
    final QuarterlyDouble currLiab = new QuarterlyDouble(cArr);

    pw.println(currLiab.fmtGrowth1Q("Current Liabs"));
    pw.println(acctPay.fmtGrowth1Q("  Acct Pay"));
    pw.println(stDebt.fmtGrowth1Q("  ST Debt"));
    pw.println(otherLiab.fmtGrowth1Q("  Other"));

    final double wc = currAssets.getMostRecent() - currLiab.getMostRecent();
    final double cr = currAssets.getMostRecent() / currLiab.getMostRecent();
    final double wcfcf = wc + fcf;

    pw.printf("\tWorking Capital   : %s M (Ratio=%.2f)%n", Utils.fmt(wc, 13), cr);
    pw.printf("\tWC + FCF          : %s M%n", Utils.fmt(wcfcf, 13));

//

    final QuarterlyDouble equity = new QuarterlyDouble(MarketTools.getEquityQtr(fd));
    final QuarterlyDouble ltDebt = new QuarterlyDouble(MarketTools.getLtDebtQtr(fd));
    final QuarterlyDouble goodwill = new QuarterlyDouble(MarketTools.getGoodwillQtr(fd));

    final double gArr[] = new double[9];
    for (int i = 0; i < gArr.length; i++) {
      gArr[i] = currAssets.get(i) - goodwill.get(i);
    }
    final QuarterlyDouble tanAssets = new QuarterlyDouble(gArr);

    pw.println(Utils.NL + equity.fmtGrowth1Q("Sharehldr Equity"));
    pw.println(goodwill.fmtGrowth1Q("Goodwill"));
    pw.println(tanAssets.fmtGrowth1Q("Tangible Assets"));
    pw.println(ltDebt.fmtGrowth1Q("LT Debt"));
    if (equity.getMostRecent() > 0.0) {
      pw.printf("\tLT Debt to Equity : %s%n", Utils.fmt(ltDebt.getMostRecent() / equity.getMostRecent(), 13));
    }
    else {
      pw.printf("\tLT Debt Tan Asset : %s%n", Utils.fmt(ltDebt.getMostRecent() / tanAssets.getMostRecent(), 13));
    }

  }

}
