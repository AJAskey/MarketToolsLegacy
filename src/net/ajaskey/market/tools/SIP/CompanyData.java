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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.WordUtils;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.misc.Debug;
import net.ajaskey.market.optuma.TickerPriceData;

public class CompanyData {

  public static List<CompanyData> buybackList = new ArrayList<>();
  public static List<CompanyData> companyList = new ArrayList<>();

  public static List<String> sectorList;

//  protected final static String balsheetFile     = "sipdata/US-STOCKS-BALANCESHEETQTR.TXT";
//  protected final static String cashFile         = "sipdata/US-STOCKS-CASH.TXT";
//  protected final static String incstatementFile = "sipdata/US-STOCKS-INCOMESTMTQTR.TXT";
//  protected final static String miscFile         = "sipdata/US-STOCKS-MISC.TXT";

  protected final static String balsheetFile     = "data/BigDB/2021/Q4/BALSHEET-QTR-2021Q4.TXT";
  protected final static String cashFile         = "data/BigDB/2021/Q4/CASH-2021Q4.TXT";
  protected final static String incstatementFile = "data/BigDB/2021/Q4/INCOME-QTR-2021Q4.TXT";
  protected final static String miscFile         = "data/BigDB/2021/Q4/US-STOCKS-MISC.TXT";

  private final static String           NL  = Utils.NL;
  private final static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
  private final static String           TAB = Utils.TAB;

  private static Set<String> theGoodList = new HashSet<>();

  private static double totalBuyBacks  = 0.0;
  private static double totalEps       = 0.0;
  private static double totalMarketCap = 0.0;
  private static double totalNewShares = 0.0;

  /**
   *
   * @param cd
   */
  public static void addToGoodList(CompanyData cd) {
    String exch = "";
    if (cd.exchange.contains("New York")) {
      exch = "NYSE";
    }
    else if (cd.exchange.contains("Nasdaq")) {
      exch = "NASDAQ";
    }
    final String str = String.format("%s:%s", cd.ticker.toUpperCase(), exch);
    CompanyData.theGoodList.add(str);
    // System.out.println("adding to theGoodList : " + str);
  }

  /**
   * net.ajaskey.market.tools.SIP.main
   *
   * @param args
   * @throws IOException
   */
  public static void main(final String[] args) throws IOException {

    final String totIndex = "SP500";

    Utils.makeDir("sipout");

    if (!CompanyData.validateInputFiles()) {
      return;
    }

    final DateTime yesterday = new DateTime();
    yesterday.add(DateTime.DATE, -1);

    final TickerPriceData spxData = new TickerPriceData("WI", "SPX");
    final double day0 = spxData.getLatest();
    final double day65 = spxData.getOffsetPrice(65);
    final double spxChg13wk = (day0 - day65) / day65 * 100.0;

    final double spxPrice = day0;

    Utils.makeDir("debug");
    Debug.init("debug/CompanyData.log");

    QuarterlyData.init();

    CompanyData.readBsdData(CompanyData.balsheetFile);
    CompanyData.readIdData(CompanyData.incstatementFile);
    CompanyData.readCashData(CompanyData.cashFile);
    CompanyData.readMiscData(CompanyData.miscFile, spxChg13wk);

    final List<String> latestQDate = new ArrayList<>();

    for (final CompanyData cd : CompanyData.companyList) {

      cd.partOfTotalCap = cd.marketCap / CompanyData.totalMarketCap;
      final double te = cd.id.epsDilCont.getMostRecent() * Math.min(0.01, cd.partOfTotalCap) * 100.0;
      CompanyData.totalEps += te;
    }

    Collections.sort(latestQDate, Collections.reverseOrder());
    try (PrintWriter pw = new PrintWriter("sipout/LatestQDate.txt")) {
      for (final String s : latestQDate) {
        pw.println(s);
      }
    }

    final double spxPE = spxPrice / CompanyData.totalEps;

    // Calculate statistics
    final Statistics bvpsStats = new Statistics("Book Value per Share");
    final Statistics salesStats = new Statistics("Sales");
    final Statistics epsStats = new Statistics("Earnings per Share");
    final Statistics netIncomeStats = new Statistics("Net Income");
    final Statistics inventoryStats = new Statistics("Inventory");
    final Statistics peStats = new Statistics("Price / Earnings");
    final Statistics psStats = new Statistics("Price / Sales");
    final Statistics opMarginStats = new Statistics("Operations Margin");
    final Statistics taxRateStats = new Statistics("Tax Rate");
    final Statistics interestRateStats = new Statistics("Interest Rate");
    final Statistics divYldStats = new Statistics("Dividend Yield");
    final Statistics epsYldStats = new Statistics("Earnings Yield");

    List<Statistics> statList = new ArrayList<>();
    statList.add(bvpsStats);
    statList.add(salesStats);
    statList.add(epsStats);
    statList.add(netIncomeStats);
    statList.add(inventoryStats);
    statList.add(peStats);
    statList.add(psStats);
    statList.add(opMarginStats);
    statList.add(taxRateStats);
    statList.add(interestRateStats);
    statList.add(divYldStats);
    statList.add(epsYldStats);

    try (PrintWriter pw = new PrintWriter("sipdata/spx-stocks.txt")) {
      for (final CompanyData cd : CompanyData.companyList) {
        pw.println(cd.ticker);
        bvpsStats.addValues(cd.bsd.bvps);
        salesStats.addValues(cd.id.sales);
        epsStats.addValues(cd.id.eps);
        netIncomeStats.addValues(cd.id.netIncome);
        inventoryStats.addValues(cd.bsd.inventory);
        peStats.addValue(cd.pe);
        psStats.addValue(cd.psales);
        opMarginStats.addValue(cd.opMargin);
        taxRateStats.addValue(cd.taxRate);
        interestRateStats.addValue(cd.interestRate);
        divYldStats.addValue(cd.divYld);
        epsYldStats.addValue(cd.epsYld);
      }
    }

    // output data
    try (PrintWriter pw = new PrintWriter("debug/companystats.dbg")) {

      for (final CompanyData cd : CompanyData.companyList) {
        pw.println(cd);
      }

      pw.printf("%n%n-----------------------------------------------------------------%n%n");

      pw.println(bvpsStats);
      pw.println(salesStats);
      pw.println(epsStats);
      pw.println(netIncomeStats);
      pw.println(inventoryStats);
      pw.println(peStats);
      pw.println(psStats);
      pw.println(opMarginStats);
      pw.println(taxRateStats);
      pw.println(interestRateStats);
      pw.println(divYldStats);
      pw.println(epsYldStats);
      pw.println("Total Market Cap :" + QuarterlyData.fmt(CompanyData.totalMarketCap, 20));
      pw.println("Total EPS        :" + QuarterlyData.fmt(CompanyData.totalEps, 20));
      pw.println("SPX PE           :" + QuarterlyData.fmt(spxPE, 20));

    }

    List<CompanyData> filteredList = new ArrayList<>();
    final DateTime endDt = new DateTime(2020, DateTime.JULY, 31);
    for (final CompanyData cd : CompanyData.companyList) {
      final DateTime dt = new DateTime(cd.eoq);
      if (dt.isGreaterThanOrEqual(endDt)) {
        filteredList.add(cd);
      }
    }

    List<CompanyData> iList = new ArrayList<>();
    for (CompanyData cd : filteredList) {
      if (cd.lastPrice >= 5.0) {
        iList.add(cd);
      }
    }
    final Reports iReports = new Reports(iList);
    iReports.writeInsiders();
    iReports.writeInstitutions();

    final Reports reports = new Reports(filteredList);
    reports.writeBestFinancial();
    reports.writeGoodFinancial();
    reports.writeZombies("");
    reports.writeDividendCutters();
    reports.writeSpreadsheetData();

//    List<CompanyData> zListcd = createZlist();
//    Reports zListReport = new Reports(zListcd);
//    zListReport.writeZombies("twitter");

    List<CompanyData> noBrainZombies = new ArrayList<>();
    for (final CompanyData cd : filteredList) {
      if (CompanyData.isNoBrainZombie(cd)) {
        noBrainZombies.add(cd);
      }
    }
    final Reports nbzReport = new Reports(noBrainZombies);
    nbzReport.writeZombies("noBrain-");
    noBrainZombies = null;

    final Reports tdReport = new Reports(CompanyData.companyList);
    tdReport.writeCompanyReports();

    System.out.printf("Total Buyback Estimate   :  $%sB%n", QuarterlyData.fmt(CompanyData.totalBuyBacks / 1000.0, 2));
    System.out.printf("Total New Share Estimate :  $%sB%n", QuarterlyData.fmt(CompanyData.totalNewShares / 1000.0, 2));

    try (PrintWriter pw = new PrintWriter("sipout/buybacks.txt")) {
      pw.println("Ticker\tShares(M)\tEst Cost(M)");
      for (final CompanyData cd : CompanyData.buybackList) {
        final double sc = DerivedData.calcShareChange(cd);
        final double bbest = Math.abs(sc) * cd.avgPrice;
        pw.printf("%s\t%.2f\t%.2f%n", cd.ticker, sc, bbest);
      }
    }

    final List<CompanyData> spxList = new ArrayList<>();
    for (final CompanyData cd : CompanyData.companyList) {
      if (cd.spIndex.equals(totIndex)) {
        spxList.add(cd);
      }
    }
    System.out.println(spxList.size());

    List<String> tgl = new ArrayList<>(CompanyData.theGoodList);
    Collections.sort(tgl);
    try (PrintWriter pw = new PrintWriter("sipout/good-list.csv"); PrintWriter pwSc = new PrintWriter("sipout/good-list-sc.csv")) {
      pw.println("code");
      for (final String s : tgl) {
        pw.println(s);
        final int idx = s.indexOf(":");
        pwSc.println(s.substring(0, idx));
      }
    }
    tgl = null;

    // Cleanup - not necessary but stops memory leaks
    CompanyData.companyList = null;
    CompanyData.theGoodList = null;
    CompanyData.sectorList = null;
    CompanyData.buybackList = null;
    statList = null;
    filteredList = null;
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.readMiscData
   *
   * @param fname
   * @param spxChg13wk
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void readMiscData(final String fname, double spxChg13wk) throws FileNotFoundException, IOException {

    try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {

      String line = "";
      while ((line = reader.readLine()) != null) {
        final String str = line.replaceAll("\"", "").trim();
        if (str.length() > 1) {

          final String fld[] = str.split(CompanyData.TAB);
          final String ticker = fld[0].trim();
          final CompanyData cd = CompanyData.getCompany(ticker);
          if (cd != null) {

            cd.numEmp = (int) Double.parseDouble(fld[2].trim());
            cd.eoq = new DateTime(fld[3].trim(), "MM/dd/yyyy");
            cd.eoq.setSdf(Utils.sdf);
            final String s = fld[1].trim();
            if (s.length() > 0) {
              if (s.contains("500")) {
                cd.spIndex = "SP500";
              }
              else if (s.contains("400")) {
                cd.spIndex = "SP400";
              }
              else if (s.contains("600")) {
                cd.spIndex = "SP600";
              }
            }
            cd.lastPrice = QuarterlyData.parseDouble(fld[4].trim());
            cd.avgPrice = QuarterlyData.parseDouble(fld[5].trim());
            cd.high52wk = QuarterlyData.parseDouble(fld[6].trim());
            cd.insiders = QuarterlyData.parseDouble(fld[7].trim());
            cd.inst = QuarterlyData.parseDouble(fld[8].trim());
            cd.adv = (int) QuarterlyData.parseDouble(fld[9].trim()) * 1000;
            cd.floatShares = QuarterlyData.parseDouble(fld[10].trim());
            cd.cashFlow = QuarterlyData.parseDouble(fld[12].trim());
            cd.q0EstGrowth = QuarterlyData.parseDouble(fld[13].trim());
            cd.y1EstGrowth = QuarterlyData.parseDouble(fld[14].trim());
            cd.opInc3yrGrowth = QuarterlyData.parseDouble(fld[16].trim());
            cd.city = fld[17].trim();
            cd.state = fld[18].trim();
            final double rs = QuarterlyData.parseDouble(fld[28].trim());
            cd.rs = rs - spxChg13wk;

            cd.shares.parse(fld);

            if (cd.adv > 0.0) {
              cd.turnover = cd.floatShares * 1000000.0 / cd.adv;
            }

            final double shr = cd.shares.getTtm();
            if (shr == 0.0 && cd.id.eps.getMostRecent() != 0.0) {
              cd.shares.q1 = Math.abs(cd.id.netIncome.getMostRecent() / cd.id.eps.getMostRecent());
            }

            cd.pe = DerivedData.calcPE(cd.id, cd.lastPrice);
            cd.psales = DerivedData.calcPSales(cd);
            cd.opMargin = DerivedData.calcOpMargin(cd.id);
            cd.netMargin = DerivedData.calcNetMargin(cd.id);
            cd.roe = DerivedData.calcRoe(cd);
            cd.taxRate = DerivedData.calcTaxRate(cd.id);
            cd.interestRate = DerivedData.calcInterestRate(cd.id);
            cd.divYld = DerivedData.calcDividendYield(cd);
            cd.epsYld = DerivedData.calcEarningsYield(cd);
            cd.ltDebtEquity = DerivedData.calcDebtToEquity(cd.bsd);
            cd.stDebtOpIncome = DerivedData.calcStDebtToOpIncome(cd);
            cd.debtCash = DerivedData.calcDebtToCash(cd.bsd);
            cd.marketCap = DerivedData.calcMarketCap(cd);
            cd.freeCashFlow = DerivedData.calcFreeCashFlow(cd);
            cd.workingCashFlow = DerivedData.calcWorkingCashFlow(cd);
            cd.sumCurrAssets = DerivedData.calcCurrAssets(cd);
            cd.sumCurrLiab = DerivedData.calcCurrLiabilities(cd);
            cd.currentRatio = DerivedData.calcCurrentRatio(cd);
            cd.workingCapital = DerivedData.calcWorkingCapital(cd);
            cd.netCashFlow = DerivedData.calcNetCashFlow(cd);
            cd.totalCashFlow = DerivedData.calcTotalCashFlow(cd);

            cd.pricePercOff52High = DerivedData.calc52weekHighPercent(cd);

            if (cd.spIndex.equals("SP500")) {
              final double sc = DerivedData.calcShareChange(cd);
              final double bbest = Math.abs(sc) * cd.avgPrice;
              if (sc < 0.0) {
                CompanyData.totalBuyBacks += bbest;
                CompanyData.buybackList.add(cd);
              }
              else {
                CompanyData.totalNewShares += bbest;
              }
            }

            CompanyData.totalMarketCap += cd.marketCap;
          }
        }
      }
    }

  }

  /**
   *
   * @return
   */
  protected static boolean validateInputFiles() {
    boolean ret = true;
    if (!CompanyData.validateInputFile(CompanyData.balsheetFile)) {
      System.out.println("Invalid input file : " + CompanyData.balsheetFile);
      ret = false;
    }
    final List<String> baseTickers = CompanyData.getTickers(CompanyData.balsheetFile);
    final int btCount = baseTickers.size();

    if (!CompanyData.validateInputFile(CompanyData.incstatementFile)) {
      System.out.println("Invalid input file : " + CompanyData.incstatementFile);
      ret = false;
    }
    List<String> tickers = CompanyData.getTickers(CompanyData.incstatementFile);
    int tCount = tickers.size();
    if (btCount != tCount) {
      System.out.println("Ticker mismatch between " + CompanyData.balsheetFile + " and " + CompanyData.incstatementFile);
      ret = false;
    }
    else {
      for (int i = 0; i < btCount; i++) {
        if (!baseTickers.get(i).equals(tickers.get(i))) {
          ret = false;
          System.out.println("Ticker mismatch between " + CompanyData.balsheetFile + " and " + CompanyData.incstatementFile);
          break;
        }
      }
    }

    if (!CompanyData.validateInputFile(CompanyData.cashFile)) {
      System.out.println("Invalid input file : " + CompanyData.cashFile);
      ret = false;
    }
    tickers.clear();
    tickers = CompanyData.getTickers(CompanyData.cashFile);
    tCount = tickers.size();
    if (btCount != tCount) {
      System.out.println("Ticker mismatch between " + CompanyData.balsheetFile + " and " + CompanyData.cashFile);
      ret = false;
    }

    if (!CompanyData.validateInputFile(CompanyData.miscFile)) {
      System.out.println("Invalid input file : " + CompanyData.miscFile);
      ret = false;
    }
    return ret;
  }

  /**
   * net.ajaskey.market.tools.SIP.getCompany
   *
   * @param ticker
   * @return
   */
  static CompanyData getCompany(final String ticker) {

    for (final CompanyData cd : CompanyData.companyList) {
      if (cd.ticker.equalsIgnoreCase(ticker)) {
        return cd;
      }
    }
    return null;
  }

  /**
   * net.ajaskey.market.tools.SIP.readBsdData
   *
   * @param string
   * @throws IOException
   * @throws FileNotFoundException
   */
  static void readBsdData(final String fname) throws FileNotFoundException, IOException {

    try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {

      String line = "";
      while ((line = reader.readLine()) != null) {
        final String str = line.trim().replaceAll("\"", "").replaceAll("[MN] - ", "");
        if (str.length() > 1) {
          // System.out.println(str);
          final String fld[] = str.split(CompanyData.TAB);
          final CompanyData cd = CompanyData.setCompanyInfo(fld);
          cd.bsd = BalanceSheetData.setBalanceSheetInfo(fld);
          // System.out.println(cd.bsd);
          CompanyData.companyList.add(cd);
        }
      }
    }

  }

  /**
   * net.ajaskey.market.tools.SIP.readCashData
   *
   * @param string
   * @throws IOException
   * @throws FileNotFoundException
   */
  static void readCashData(final String fname) throws FileNotFoundException, IOException {

    try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {

      String line = "";
      while ((line = reader.readLine()) != null) {
        final String str = line.replaceAll("\"", "").trim();
        if (str.length() > 1) {

          final String fld[] = str.split(CompanyData.TAB);
          final String ticker = fld[0].trim();
          final CompanyData cd = CompanyData.getCompany(ticker);
          if (cd != null) {
            cd.cashData = CashData.setCashDataInfo(fld);
          }
        }
      }
    }

  }

  /**
   * net.ajaskey.market.tools.SIP.readIdData
   *
   * @param string
   * @throws IOException
   */
  static void readIdData(final String fname) throws IOException {

    final Set<String> sectors = new HashSet<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {

      String line = "";
      while ((line = reader.readLine()) != null) {
        final String str = line.trim().replaceAll("\"", "").replaceAll("[MN] - ", "");
        if (str.length() > 1) {

          final String fld[] = str.split(CompanyData.TAB);
          final String ticker = fld[1].trim();
          final CompanyData cd = CompanyData.getCompany(ticker);
          if (cd != null) {
            sectors.add(cd.sector);
            cd.id = IncomeData.setIncomeData(fld);
          }
        }
      }
    }

    CompanyData.sectorList = new ArrayList<>(sectors);
    Collections.sort(CompanyData.sectorList);

  }

  private static List<CompanyData> createZlist() {
    final List<String> zList = new ArrayList<>();
    zList.add("OSTK");
    zList.add("W");
    zList.add("FLSY");
    zList.add("PENN");
    zList.add("CVNA");
    zList.add("NIO");
    zList.add("LL");
    zList.add("SQ");
    zList.add("ETSY");
    zList.add("SE");
    zList.add("WIX");
    zList.add("PTON");
    zList.add("MRNA");
    zList.add("WING");
    zList.add("SHOP");
    zList.add("PINS");
    zList.add("Z");

    final List<CompanyData> cdLst = new ArrayList<>();

    for (final CompanyData cd : CompanyData.companyList) {
      for (final String s : zList) {
        if (s.equalsIgnoreCase(cd.ticker)) {
          cdLst.add(cd);
          break;
        }
      }
    }

    return cdLst;

  }

  /**
   *
   * @param fname
   * @return
   */
  private static List<String> getTickers(String fname) {
    final List<String> data = TextUtils.readTextFile(fname, false);
    final List<String> tickers = new ArrayList<>();
    for (final String s : data) {
      final String fld[] = s.split(CompanyData.TAB);
      tickers.add(fld[1].trim().toUpperCase());
    }
    return tickers;
  }

  /**
   * net.ajaskey.market.tools.SIP.isNoBrainZombie
   *
   * @param cd
   * @return
   */
  private static boolean isNoBrainZombie(CompanyData cd) {

    if (cd.sector.contains("Utilities")) {
      return false;
    }
//    if (cd.lastPrice >= 20.0) {
//
//      final double wcfcf = cd.workingCapital + cd.freeCashFlow;
//      cd.zscore = ZombieScore.calculate(cd);
//
//      if (cd.zscore.score > 79.99) {
//
//        final double cf = cd.cashData.cashFromOps.getTtm() + cd.cashData.cashFromFin.getTtm() + cd.cashData.cashFromInv.getTtm();
//        if (wcfcf < 0.0 && cf < 0.0) {
//          if (cd.bsd.equity.q1 < 0.0) {
//            return true;
//          }
//        }
//      }
//    }

    if (cd.bsd.equity.q1 <= 0.0) {
      if (cd.bsd.ltDebt.q1 > cd.bsd.assetsMinusGW.q1) {
        return true;
      }
    }
    return false;
  }

  /**
   * net.ajaskey.market.tools.SIP.setCompanyInfo
   *
   * @param fld
   */
  private static CompanyData setCompanyInfo(final String[] fld) {

    final CompanyData cd = new CompanyData();
    cd.name = fld[0].trim();
    cd.ticker = fld[1].trim();
    cd.exchange = fld[2].trim();
    cd.sector = CompanyData.setSecInd(fld[3].trim());
    final String s = CompanyData.setSecInd(fld[4].trim());
    cd.industry = WordUtils.capitalizeFully(s);
    return cd;

  }

  /**
   * net.ajaskey.market.tools.SIP.setSecInd
   *
   * @param string
   * @return
   */
  private static String setSecInd(final String secind) {

    String ret = "";
    final String str = secind.trim();
    final int pos = secind.indexOf(" - ");
    if (pos > 0) {
      ret = str.substring(pos + 3, str.length()).trim();
    }
    else {
      ret = str;
    }
    return ret;
  }

  /**
   *
   * @param fname
   * @return
   */
  private static boolean validateInputFile(String fname) {
    boolean ret = true;
    final List<String> data = TextUtils.readTextFile(fname, false);
    for (final String s : data) {
      final String fld[] = s.split(CompanyData.TAB);
      if (fld.length < 10) {
        ret = false;
        break;
      }
    }
    return ret;
  }

  public int              adv;
  public double           avgPrice;
  public BalanceSheetData bsd;
  public CashData         cashData;
  public double           cashFlow;
  public String           city;
  public double           currentRatio;
  public double           debtCash;
  public double           divYld;
  public DateTime         eoq;
  public double           epsYld;
  public String           exchange;
  public double           floatShares;
  public double           freeCashFlow;
  public double           gscore;
  public double           high52wk;
  public IncomeData       id;
  public String           industry;
  public double           insiders;
  public double           inst;
  public double           interestRate;
  public double           lastPrice;
  public double           ltDebtEquity;
  public double           marketCap;
  public String           name;
  public double           netCashFlow;
  public double           netMargin;
  public int              numEmp;
  public double           opInc3yrGrowth;
  public double           opMargin;
  public double           partOfTotalCap;
  public double           pe;
  public double           pricePercOff52High;
  public double           psales;
  public double           q0EstGrowth;
  public double           roe;
  public double           rs;
  public String           sector;
  public QuarterlyData    shares;
  public String           spIndex;
  public String           state;
  public double           stDebtOpIncome;
  public double           sumCurrAssets;
  public double           sumCurrLiab;
  public double           taxRate;
  public String           ticker;
  public double           totalCashFlow;
  public double           turnover;
  public double           workingCapital;
  public double           workingCashFlow;
  public double           y1EstGrowth;
  public ZombieScore      zscore;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public CompanyData() {

    this.name = "";
    this.city = "";
    this.state = "";
    this.ticker = "";
    this.exchange = "";
    this.rs = 0.0;
    this.sector = "";
    this.industry = "";
    this.spIndex = "";
    this.numEmp = 0;
    this.insiders = 0.0;
    this.inst = 0.0;
    this.turnover = 0.0;
    this.adv = 0;
    this.eoq = null;
    this.floatShares = 0.0;
    this.opInc3yrGrowth = 0.0;
    this.cashFlow = 0.0;
    this.q0EstGrowth = 0.0;
    this.y1EstGrowth = 0.0;
    this.shares = new QuarterlyData("shares");
    this.gscore = 0.0;
    this.lastPrice = 0.0;
    this.avgPrice = 0.0;
    this.pricePercOff52High = 0.0;
    this.pe = 0.0;
    this.psales = 0.0;
    this.opMargin = 0.0;
    this.netMargin = 0.0;
    this.roe = 0.0;
    this.taxRate = 0.0;
    this.interestRate = 0.0;
    this.divYld = 0.0;
    this.epsYld = 0.0;
    this.freeCashFlow = 0.0;
    this.workingCashFlow = 0.0;
    this.ltDebtEquity = 0.0;
    this.stDebtOpIncome = 0.0;
    this.debtCash = 0.0;
    this.marketCap = 0.0;
    this.partOfTotalCap = 0.0;
    this.sumCurrAssets = 0.0;
    this.sumCurrLiab = 0.0;
    this.currentRatio = 0.0;
    this.workingCapital = 0.0;
    this.netCashFlow = 0.0;
    this.totalCashFlow = 0.0;
    this.high52wk = 0.0;
  }

  /**
   * This method serves as a constructor for the class. Used for test setup.
   *
   * @param string
   */
  public CompanyData(final String code) {

    this.ticker = code;
    this.shares = new QuarterlyData("shares");
  }

  /**
   *
   * @param cd
   * @return
   */
  public String getExchange() {
    String ret = this.exchange.toUpperCase().trim();
    if (this.exchange.equalsIgnoreCase("New York")) {
      ret = "NYSE";
    }
    return ret;
  }

  /**
   *
   * @param ret
   * @return
   */
  public String printMisc(String ret) {

    ret += CompanyData.TAB + this.name + CompanyData.NL;
    ret += CompanyData.TAB + this.exchange + CompanyData.NL;
    ret += CompanyData.TAB + this.sector + CompanyData.NL;
    ret += CompanyData.TAB + this.industry + CompanyData.NL;
    ret += CompanyData.TAB + "Number Employees  : " + String.format("%15d", this.numEmp) + CompanyData.NL;
    try {
      ret += CompanyData.TAB + "End of Quarter    : " + String.format("%15s", CompanyData.sdf.format(this.eoq)) + CompanyData.NL;
    }
    catch (final Exception e) {
      ret += CompanyData.TAB + "End of Quarter    : ERROR" + CompanyData.NL;
    }
    ret += CompanyData.TAB + "Insiders Own      : " + QuarterlyData.fmt(this.insiders) + CompanyData.NL;
    ret += CompanyData.TAB + "Float             : " + QuarterlyData.fmt(this.floatShares) + CompanyData.NL;
    ret += CompanyData.TAB + "Outstanding       : " + QuarterlyData.fmt(this.shares.getMostRecent()) + CompanyData.NL;
    ret += CompanyData.TAB + "Market Cap        : " + QuarterlyData.fmt(this.marketCap)
        + String.format("  %s", String.format("(%3.5f%%)", this.partOfTotalCap * 100.0)) + CompanyData.NL;

    ret += CompanyData.TAB + "Last Price        : " + QuarterlyData.fmt(this.lastPrice) + CompanyData.NL;
    ret += CompanyData.TAB + "Average Price     : " + QuarterlyData.fmt(this.avgPrice) + CompanyData.NL;
    ret += CompanyData.TAB + "RS vs SPX         : " + this.rs + CompanyData.NL;
    ret += CompanyData.TAB + "PE                : " + QuarterlyData.fmt(this.pe) + CompanyData.NL;
    ret += CompanyData.TAB + "Price/Sales       : " + QuarterlyData.fmt(this.psales) + CompanyData.NL;
    ret += CompanyData.TAB + "Op Margin         : " + QuarterlyData.fmt(this.opMargin) + CompanyData.NL;
    ret += CompanyData.TAB + "Net Margin        : " + QuarterlyData.fmt(this.netMargin) + CompanyData.NL;
    ret += CompanyData.TAB + "ROE               : " + QuarterlyData.fmt(this.roe) + CompanyData.NL;
    ret += CompanyData.TAB + "Tax Rate          : " + QuarterlyData.fmt(this.taxRate) + CompanyData.NL;
    ret += CompanyData.TAB + "Interest Rate     : " + QuarterlyData.fmt(this.interestRate) + CompanyData.NL;
    ret += CompanyData.TAB + "Dividend Yield    : " + QuarterlyData.fmt(this.divYld) + CompanyData.NL;
    ret += CompanyData.TAB + "Earnings Yield    : " + QuarterlyData.fmt(this.epsYld) + CompanyData.NL;
    ret += CompanyData.TAB + "LT Debt to Equity : " + QuarterlyData.fmt(this.ltDebtEquity) + CompanyData.NL;
    ret += CompanyData.TAB + "ST Debt to OpInc  : " + QuarterlyData.fmt(this.stDebtOpIncome) + CompanyData.NL;
    ret += CompanyData.TAB + "Debt to Cash      : " + QuarterlyData.fmt(this.debtCash) + CompanyData.NL;
    ret += CompanyData.TAB + "Q0 Est Growth     : " + QuarterlyData.fmt(this.q0EstGrowth, 15) + CompanyData.NL;
    ret += CompanyData.TAB + "Y1 Est Growth     : " + QuarterlyData.fmt(this.y1EstGrowth, 15) + CompanyData.NL;
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = this.ticker + CompanyData.NL;
    ret += this.printMisc(ret);
    ret += this.bsd;
    ret += this.id;

    return ret;
  }

}
