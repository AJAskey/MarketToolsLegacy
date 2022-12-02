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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.common.Zip;
import net.ajaskey.market.optuma.OptumaCommon;
import net.ajaskey.market.optuma.OptumaPriceData;
import net.ajaskey.market.optuma.PriceData;

public class SipData {

  public static List<DataSet>   accRx           = new ArrayList<>();
  public static List<DataSet>   accTx           = new ArrayList<>();
  public static List<DataSet>   assets          = new ArrayList<>();
  public static List<DataSet>   bookValue       = new ArrayList<>();
  public static List<DataSet>   capEx           = new ArrayList<>();
  public static List<DataSet>   cash            = new ArrayList<>();
  public static List<DataSet>   cashFin         = new ArrayList<>();
  public static List<DataSet>   cashInv         = new ArrayList<>();
  public static List<DataSet>   cashOps         = new ArrayList<>();
  public static List<DataSet>   dividend        = new ArrayList<>();
  public static List<DataSet>   ebit            = new ArrayList<>();
  public static List<DataSet>   enterpriseValue = new ArrayList<>();
  public static List<DataSet>   eps             = new ArrayList<>();
  public static List<DataSet>   equity          = new ArrayList<>();
  public static List<DataSet>   fcf             = new ArrayList<>();
  public static List<DataSet>   goodwill        = new ArrayList<>();
  public static List<DataSet>   incomeEps       = new ArrayList<>();
  public static List<PriceData> indexPrices     = null;
  public static List<DataSet>   interest        = new ArrayList<>();
  public static List<DataSet>   inventoryValue  = new ArrayList<>();
  public static List<DataSet>   liabilities     = new ArrayList<>();
  public static List<DataSet>   ltDebt          = new ArrayList<>();
  public static List<DataSet>   prices          = new ArrayList<>();
  public static List<DataSet>   resDev          = new ArrayList<>();
  public static List<DataSet>   sales           = new ArrayList<>();

  public static List<DataSet> shares = new ArrayList<>();

  public static List<DataSet> taxes = new ArrayList<>();

  static String path = OptumaCommon.optumaPath + "SIP\\";

  private static int companyKnt = 0;

  private static SimpleDateFormat sdfOptuma = new SimpleDateFormat("yyyy-MM-dd");

  /**
   *
   * net.ajaskey.market.tools.sipro.main
   *
   * @param args
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ParseException
   */
  public static void main(final String[] args) throws FileNotFoundException, IOException, ParseException {

    SipData.readDataFile_1("sipdata/SP-STOCKS.txt");
    final DateTime latest = SipData.readDataFile_2("sipdata/SP-STOCKS-B.txt");

    System.out.println(latest);

    SipData.processData("500");
    SipData.processData("MidCap 400");
    SipData.processData("SmallCap 600");

    SipData.processSector("500", "06  - Energy");
    SipData.processSector("500", "10  - Technology");

    SipData.archiveData(latest);

  }

  /**
   *
   * net.ajaskey.market.tools.sipro.processData
   *
   * @param index
   *
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ParseException
   */
  public static void processData(final String index) throws FileNotFoundException, IOException, ParseException {

    final DataSet totSales = DataSet.sum(SipData.sales, index);
    final DataSet totEbit = DataSet.sum(SipData.ebit, index);
    final DataSet totTax = DataSet.sum(SipData.taxes, index);
    final DataSet totIncome = DataSet.sum(SipData.incomeEps, index);
    final DataSet totCops = DataSet.sum(SipData.cashOps, index);
    final DataSet totCfin = DataSet.sum(SipData.cashFin, index);
    final DataSet totCinv = DataSet.sum(SipData.cashInv, index);
    final DataSet totShr = DataSet.sum(SipData.shares, index);
    final DataSet totCash = DataSet.sum(SipData.cash, index);
    final DataSet totAssets = DataSet.sum(SipData.assets, index);
    final DataSet totLiab = DataSet.sum(SipData.liabilities, index);
    final DataSet totAccRx = DataSet.sum(SipData.accRx, index);
    final DataSet totAccTx = DataSet.sum(SipData.accTx, index);
    final DataSet totGoodwill = DataSet.sum(SipData.goodwill, index);
    final DataSet totLtDebt = DataSet.sum(SipData.ltDebt, index);
    final DataSet totCapEx = DataSet.sum(SipData.capEx, index);
    final DataSet totEquity = DataSet.sum(SipData.equity, index);
    final DataSet totInterest = DataSet.sum(SipData.interest, index);
    final DataSet totResDev = DataSet.sum(SipData.resDev, index);
    final DataSet totInventory = DataSet.sum(SipData.inventoryValue, index);
    final DataSet totEnterprise = DataSet.sum(SipData.enterpriseValue, index);
    final DataSet totFcf = DataSet.sum(SipData.fcf, index);

    String prefix = "";

    if (index.equalsIgnoreCase("500")) {
      SipData.indexPrices = OptumaPriceData.getPriceData("D:\\data2\\optuma\\WI\\S\\SPX.csv");
      prefix = "SPX";
    }
    else if (index.equalsIgnoreCase("MidCap 400")) {
      SipData.indexPrices = OptumaPriceData.getPriceData("D:\\data2\\optuma\\WI\\S\\SPX.csv");
      prefix = "SP400";
    }
    else if (index.equalsIgnoreCase("SmallCap 600")) {
      SipData.indexPrices = OptumaPriceData.getPriceData("D:\\data2\\optuma\\WI\\M\\MID.csv");
      prefix = "SP600";
    }
    else {
      return;
    }

    final DateSet dates = new DateSet(SipData.indexPrices);
    final DataSet dsPrices = new DataSet(dates, prefix);

    final List<DataSet> divDollar = new ArrayList<>();
    for (int i = 0; i < SipData.companyKnt; i++) {
      final DataSet ds = DataSet.mult(SipData.dividend.get(i), SipData.shares.get(i));
      divDollar.add(ds);
    }
    final DataSet totDivDollar = DataSet.sum(divDollar, index);

    final List<DataSet> bvDollar = new ArrayList<>();
    for (int i = 0; i < SipData.companyKnt; i++) {
      final DataSet ds = DataSet.mult(SipData.bookValue.get(i), SipData.shares.get(i));
      bvDollar.add(ds);
    }
    final DataSet totBvDollar = DataSet.sum(bvDollar, index);

    final DataSet totBVminusGW = DataSet.sub(totBvDollar, totGoodwill);

    final List<DataSet> mcap = new ArrayList<>();
    for (int i = 0; i < SipData.companyKnt; i++) {
      final DataSet ds = DataSet.mult(SipData.prices.get(i), SipData.shares.get(i));
      mcap.add(ds);
    }
    final DataSet totMktCap = DataSet.sum(mcap, index);
    totMktCap.mode = DataSet.dMode.SEQUENTIAL;

    SipData.write(totSales, prefix + " Sales", dates, false);
    SipData.write(totEbit, prefix + " EBIT", dates, false);
    SipData.write(totTax, prefix + " Taxes", dates, false);
    SipData.write(totIncome, prefix + " Income for EPS", dates, false);
    SipData.write(totCops, prefix + " Cash from Operations", dates, false);
    SipData.write(totCfin, prefix + " Cash from Financing", dates, false);
    SipData.write(totCinv, prefix + " Cash from Investing", dates, false);
    SipData.write(totDivDollar, prefix + " Dividends", dates, false);
    SipData.write(totShr, prefix + " Shares", dates, false);
    SipData.write(totCash, prefix + " Cash", dates, false);
    SipData.write(totAssets, prefix + " Assets", dates, false);
    SipData.write(totLiab, prefix + " Liabilities", dates, false);
    SipData.write(totAccRx, prefix + " Accounts Receivable", dates, false);
    SipData.write(totAccTx, prefix + " Accounts Payable", dates, false);
    SipData.write(totGoodwill, prefix + " Goodwill", dates, false);
    SipData.write(totLtDebt, prefix + " LT Debt", dates, false);
    SipData.write(totCapEx, prefix + " CapEx", dates, false);
    SipData.write(totEquity, prefix + " Common Equity", dates, false);
    SipData.write(totInterest, prefix + " Interest Paid", dates, false);
    SipData.write(totResDev, prefix + " Research and Development", dates, false);
    SipData.write(totBvDollar, prefix + " Book Value", dates, false);
    SipData.write(totBVminusGW, prefix + " Book Value less Goodwill", dates, false);
    SipData.write(totMktCap, prefix + " Market Cap", dates, false);
    SipData.write(totInventory, prefix + " Inventory", dates, false);
    SipData.write(totEnterprise, prefix + " Enterprise", dates, false);
    SipData.write(totFcf, prefix + " FCFps", dates, false);

    // -----------------------------------------

    final DataSet totMktCapShr = DataSet.ratio(totMktCap, totShr);
    SipData.write(totMktCapShr, prefix + " Market Cap to Shares", dates, false);

    final DataSet totMargin = DataSet.scale(DataSet.ratio(totIncome, totSales), 100.0);
    SipData.write(totMargin, prefix + " Margin", dates, false);

    final DataSet totROE = DataSet.scale(DataSet.ratio(totIncome, totEquity), 100.0);
    SipData.write(totROE, prefix + " ROE", dates, false);

    final DataSet totTaxMargin = DataSet.scale(DataSet.ratio(totTax, totSales), 100.0);
    SipData.write(totTaxMargin, prefix + " Tax Margin", dates, false);

    final DataSet totBVtoCap = DataSet.scale(DataSet.ratio(totBvDollar, totMktCap), 100.0);
    SipData.write(totBVtoCap, prefix + " BV over Market Cap", dates, false);

    final DataSet totBVmGWtoCap = DataSet.scale(DataSet.ratio(totBVminusGW, totMktCap), 100.0);
    SipData.write(totBVmGWtoCap, prefix + " BV Minus Goodwill over Market Cap", dates, false);

    final DataSet totGWtoAsset = DataSet.scale(DataSet.ratio(totGoodwill, totAssets), 100.0);
    SipData.write(totGWtoAsset, prefix + " Goodwill over Assets", dates, false);

    SipData.writePriceToDate(dates, totSales, prefix + " Price to Sales", 100000000000.0);
    SipData.writePriceToDate(dates, totIncome, prefix + " Price to Income", 10000000000.0);
    SipData.writePriceToDate(dates, totCash, prefix + " Price to Cash", 10000000000.0);
    SipData.writePriceToDate(dates, totBvDollar, prefix + " Price to BV", 100000000000.0);

    SipData.writeEps(totIncome, totShr, prefix + " EPS", prefix + " EPS Annual", prefix + " PE", dates, dsPrices);

  }

  /**
   * net.ajaskey.market.tools.sipro.main
   *
   * @param args
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static void readDataFile_1(final String filename) throws FileNotFoundException, IOException {

    int knt = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {

      System.out.println("Reading file : " + filename);

      String line = "";

      // line = br.readLine(); // read header
      final SipCommon sc = new SipCommon("\t", 14, 4);

      while (line != null) {
        line = br.readLine();
        if (line != null && line.length() > 0 && !line.contains("BRK.A")) {

          knt++;
          sc.reset();

          SipData.sales.add(sc.getData5("sales", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          SipData.ebit.add(sc.getData5("ebit", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          SipData.taxes.add(sc.getData5("taxes", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          SipData.incomeEps.add(sc.getData5("incomeEps", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          SipData.cashOps.add(sc.getData5("cashOps", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          SipData.cashInv.add(sc.getData5("cashInv", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          SipData.cashFin.add(sc.getData5("cashFin", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));

          SipData.dividend.add(sc.getData5("dividend", line, DataSet.dMode.ACCUMULATION, 1.0));

          SipData.capEx.add(sc.getData5("capEx", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          final DataSet s = sc.getData5("shares", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION);
          SipData.shares.add(s);

          SipData.cash.add(sc.getData5("cash", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION));
          SipData.assets.add(sc.getData5("assets", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION));
          SipData.liabilities.add(sc.getData5("liabilities", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION));
          SipData.accRx.add(sc.getData5("Accounts Receivable", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION));
          SipData.accTx.add(sc.getData5("Accounts Payable", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION));
          SipData.goodwill.add(sc.getData5("Goodwill", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION));
          SipData.ltDebt.add(sc.getData5("LT Debt", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION));
        }
      }
    }
    SipData.companyKnt = knt;
  }

  /**
   *
   * net.ajaskey.market.tools.sipro.v4.readDataFile_2
   *
   * @param filename
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static DateTime readDataFile_2(final String filename) throws FileNotFoundException, IOException {

    final DateTime latestDate = new DateTime();
    latestDate.add(Calendar.YEAR, -5);

    try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
      String line = "";

      // line = br.readLine(); // read header

      final SipCommon sc = new SipCommon("\t", 14, 4);

      while (line != null) {
        line = br.readLine();
        if (line != null && line.length() > 0 && !line.contains("BRK.A")) {

          sc.reset();

          SipData.equity.add(sc.getData5("Common Equity", line, DataSet.dMode.SEQUENTIAL, SipCommon.MILLION));
          SipData.interest.add(sc.getData5("interest", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          SipData.resDev.add(sc.getData5("R and D", line, DataSet.dMode.ACCUMULATION, SipCommon.MILLION));
          SipData.bookValue.add(sc.getData5("Book Value per Share", line, DataSet.dMode.SEQUENTIAL, 1.0));
          SipData.inventoryValue.add(sc.getData5("Inventory", line, DataSet.dMode.SEQUENTIAL, 1.0));
          SipData.enterpriseValue.add(sc.getData5("Enterprice", line, DataSet.dMode.SEQUENTIAL, 1.0));
          SipData.prices.add(sc.getData5("Prices", line, DataSet.dMode.SEQUENTIAL, 1.0));
          SipData.fcf.add(sc.getData5("FCF", line, DataSet.dMode.SEQUENTIAL, 1.0));

          try {
            final DateTime date = sc.getDate(line);
            if (date.isGreaterThan(latestDate)) {
              latestDate.set(date);
            }
          }
          catch (final ParseException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return latestDate;
  }

  /**
   *
   * net.ajaskey.market.tools.sipro.write
   *
   * @param ds
   * @param fname
   * @throws FileNotFoundException
   */
  public static void write(final DataSet ds, final String fname, final DateSet dates, final boolean printAnnual) throws FileNotFoundException {

    if (ds.mode == DataSet.dMode.SEQUENTIAL) {
      SipData.writeDataSequential(ds, fname, dates, printAnnual);
    }
    else {
      SipData.writeDataAccumulate(ds, fname, dates);
    }
  }

  /**
   * net.ajaskey.market.tools.sipro.v4.archiveData
   *
   */
  private static void archiveData(final DateTime latest) {

    final List<String> dir = new ArrayList<>();
    dir.add(SipData.path);
    final List<String> fil = new ArrayList<>();
    fil.add("sipdata\\SP-Stocks.txt");
    fil.add("sipdata\\SP-Stocks-B.txt");

    final DateTime now = new DateTime();
    now.setSdf(Utils.sdf);

    final String fname = "SIP-" + latest + "_" + now + ".zip";
    Utils.makeDirs("archive");
    Zip.create(dir, fil, "archive", fname);

  }

  /**
   * net.ajaskey.market.tools.sipro.v5.processSector
   *
   * @param string
   * @param string2
   * @throws FileNotFoundException
   */
  private static void processSector(final String index, final String sector) throws FileNotFoundException {

    final DataSet totSales = DataSet.sum(SipData.sales, index, sector);
    final DataSet totTax = DataSet.sum(SipData.taxes, index, sector);
    DataSet.sum(SipData.incomeEps, index, sector);
    final DataSet totCops = DataSet.sum(SipData.cashOps, index, sector);
    final DataSet totShr = DataSet.sum(SipData.shares, index, sector);
    final DataSet totCash = DataSet.sum(SipData.cash, index, sector);
    final DataSet totAssets = DataSet.sum(SipData.assets, index, sector);
    final DataSet totLiab = DataSet.sum(SipData.liabilities, index, sector);
    final DataSet totAccRx = DataSet.sum(SipData.accRx, index, sector);
    final DataSet totAccTx = DataSet.sum(SipData.accTx, index, sector);
    final DataSet totGoodwill = DataSet.sum(SipData.goodwill, index, sector);
    final DataSet totLtDebt = DataSet.sum(SipData.ltDebt, index, sector);
    final DataSet totCapEx = DataSet.sum(SipData.capEx, index, sector);
    final DataSet totEquity = DataSet.sum(SipData.equity, index, sector);
    final DataSet totInterest = DataSet.sum(SipData.interest, index, sector);
    final DataSet totResDev = DataSet.sum(SipData.resDev, index, sector);
    final DataSet totInventory = DataSet.sum(SipData.inventoryValue, index, sector);
    final DataSet totEnterprise = DataSet.sum(SipData.enterpriseValue, index, sector);
    final DataSet totFcf = DataSet.sum(SipData.fcf, index, sector);

    final String sec = sector.replaceAll("[^A-Za-z]", "");
    String prefix = "";
    if (index.equalsIgnoreCase("500")) {
      prefix = "SPX_" + sec;
    }
    else if (index.equalsIgnoreCase("MidCap 400")) {
      prefix = "SP400_" + sec;
    }
    else if (index.equalsIgnoreCase("SmallCap 600")) {
      prefix = "SP600_" + sec;
    }
    else {
      return;
    }

    final DateSet dates = new DateSet(SipData.indexPrices);
    // final DataSet dsPrices = new DataSet(dates, prefix);

    final List<DataSet> divDollar = new ArrayList<>();
    for (int i = 0; i < SipData.companyKnt; i++) {
      final DataSet ds = DataSet.mult(SipData.dividend.get(i), SipData.shares.get(i));
      divDollar.add(ds);
    }
    final DataSet totDivDollar = DataSet.sum(divDollar, index);

    final List<DataSet> bvDollar = new ArrayList<>();
    for (int i = 0; i < SipData.companyKnt; i++) {
      final DataSet ds = DataSet.mult(SipData.bookValue.get(i), SipData.shares.get(i));
      bvDollar.add(ds);
    }
    final DataSet totBvDollar = DataSet.sum(bvDollar, index);

    final DataSet totBVminusGW = DataSet.sub(totBvDollar, totGoodwill);

    final List<DataSet> mcap = new ArrayList<>();
    for (int i = 0; i < SipData.companyKnt; i++) {
      final DataSet ds = DataSet.mult(SipData.prices.get(i), SipData.shares.get(i));
      mcap.add(ds);
    }
    final DataSet totMktCap = DataSet.sum(mcap, index);
    totMktCap.mode = DataSet.dMode.SEQUENTIAL;

    SipData.write(totSales, prefix + " Sales", dates, false);
    SipData.write(totTax, prefix + " Taxes", dates, false);
    SipData.write(totCops, prefix + " Cash from Operations", dates, false);
    SipData.write(totDivDollar, prefix + " Dividends", dates, false);
    SipData.write(totShr, prefix + " Shares", dates, false);
    SipData.write(totCash, prefix + " Cash", dates, false);
    SipData.write(totAssets, prefix + " Assets", dates, false);
    SipData.write(totLiab, prefix + " Liabilities", dates, false);
    SipData.write(totAccRx, prefix + " Accounts Receivable", dates, false);
    SipData.write(totAccTx, prefix + " Accounts Payable", dates, false);
    SipData.write(totGoodwill, prefix + " Goodwill", dates, false);
    SipData.write(totLtDebt, prefix + " LT Debt", dates, false);
    SipData.write(totCapEx, prefix + " CapEx", dates, false);
    SipData.write(totEquity, prefix + " Common Equity", dates, false);
    SipData.write(totInterest, prefix + " Interest Paid", dates, false);
    SipData.write(totResDev, prefix + " Research and Development", dates, false);
    SipData.write(totBvDollar, prefix + " Book Value", dates, false);
    SipData.write(totBVminusGW, prefix + " Book Value less Goodwill", dates, false);
    SipData.write(totMktCap, prefix + " Market Cap", dates, false);
    SipData.write(totInventory, prefix + " Inventory", dates, false);
    SipData.write(totEnterprise, prefix + " Enterprise", dates, false);
    SipData.write(totFcf, prefix + " FCFps", dates, false);

  }

  private static double[] sumQuarters(final DataSet ds) {

    final double ret[] = new double[8];

    if (ds.mode == DataSet.dMode.SEQUENTIAL) {
      ret[7] = ds.q8;
      ret[6] = ds.q7;
      ret[5] = ds.q6;
      ret[4] = ds.q5;
      ret[3] = ds.q4;
      ret[2] = ds.q3;
      ret[1] = ds.q2;
      ret[0] = ds.q1;
    }
    else {
      ret[7] = ds.q8 * 4.0;
      ret[6] = ds.q8 * 3.0 + ds.q7;
      ret[5] = ds.q8 * 2.0 + ds.q7 + ds.q6;
      ret[4] = ds.q8 + ds.q7 + ds.q6 + ds.q5;
      ret[3] = ds.q7 + ds.q6 + ds.q5 + ds.q4;
      ret[2] = ds.q6 + ds.q5 + ds.q4 + ds.q3;
      ret[1] = ds.q5 + ds.q4 + ds.q3 + ds.q2;
      ret[0] = ds.q4 + ds.q3 + ds.q2 + ds.q1;
    }

    return ret;
  }

  /**
   *
   * net.ajaskey.market.tools.sipro.v4.write
   *
   * @param pw
   * @param q
   * @param val
   */
  private static void write(final PrintWriter pw, final DateSet.Quarters q, final double val, final double divisor) {

    final double qval = val / divisor;
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(q.q1.date.getTime()), qval);
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(q.q2.date.getTime()), qval);
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(q.q3.date.getTime()), qval);
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(q.q4.date.getTime()), qval);
  }

  /**
   *
   * net.ajaskey.market.tools.sipro.write
   *
   * @param pw
   * @param q
   * @param val
   */
  private static void writeAvgQtr(final PrintWriter pw, final DateSet.Quarters y1, final DateSet.Quarters y0, final double q8, final double q7,
      final double q6, final double q5, final double q4, final double q3, final double q2, final double q1) {

    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(y1.q1.date.getTime()), q8);

    double tmp = (q8 * 3.0 + q7) / 4.0;
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(y1.q2.date.getTime()), tmp);

    tmp = (q8 * 2.0 + q7 + q6) / 4.0;
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(y1.q3.date.getTime()), tmp);

    tmp = (q8 + q7 + q6 + q5) / 4.0;
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(y1.q4.date.getTime()), tmp);

    tmp = (q7 + q6 + q5 + q4) / 4.0;
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(y0.q1.date.getTime()), tmp);

    tmp = (q6 + q5 + q4 + q3) / 4.0;
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(y0.q2.date.getTime()), tmp);

    tmp = (q5 + q4 + q3 + q2) / 4.0;
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(y0.q3.date.getTime()), tmp);

    tmp = (q4 + q3 + q2 + q1) / 4.0;
    pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(y0.q4.date.getTime()), tmp);
  }

  /**
   *
   * net.ajaskey.market.tools.sipro.writeDataAccumulate
   *
   * @param ds
   * @param fname
   */
  private static void writeDataAccumulate(final DataSet ds, final String fname, final DateSet dates) {

    try (PrintWriter pw = new PrintWriter(SipData.path + fname + ".csv")) {

      SipData.write(pw, dates.y7, ds.y7, 4.0);
      SipData.write(pw, dates.y6, ds.y6, 4.0);
      SipData.write(pw, dates.y5, ds.y5, 4.0);
      SipData.write(pw, dates.y4, ds.y4, 4.0);
      SipData.write(pw, dates.y3, ds.y3, 4.0);
      SipData.write(pw, dates.y2, ds.y2, 4.0);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q1.date.getTime()), ds.q8);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q2.date.getTime()), ds.q7);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q3.date.getTime()), ds.q6);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q4.date.getTime()), ds.q5);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q1.date.getTime()), ds.q4);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q2.date.getTime()), ds.q3);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q3.date.getTime()), ds.q2);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q4.date.getTime()), ds.q1);

    }
    catch (final Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * net.ajaskey.market.tools.sipro.writeDataSequential
   *
   * @param ds
   * @param fname
   * @throws FileNotFoundException
   */
  private static void writeDataSequential(final DataSet ds, final String fname, final DateSet dates, final boolean printAnnual)
      throws FileNotFoundException {

    try (PrintWriter pw = new PrintWriter(SipData.path + fname + ".csv")) {

      // SipData5.write(pw, dates.y1, dates.y0, ds.q8, ds.q7, ds.q6, ds.q5, ds.q4,
      // ds.q3, ds.q2, ds.q1);

      if (printAnnual) {

        pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y7.q4.date.getTime()), ds.y7);
        pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y6.q4.date.getTime()), ds.y6);
        pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y5.q4.date.getTime()), ds.y5);
        pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y4.q4.date.getTime()), ds.y4);
        pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y3.q4.date.getTime()), ds.y3);
        pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y2.q4.date.getTime()), ds.y2);
        pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q4.date.getTime()), ds.y1);
        pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q4.date.getTime()), ds.y0);
      }
      else {

        SipData.write(pw, dates.y7, ds.y7, 1.0);
        SipData.write(pw, dates.y6, ds.y6, 1.0);
        SipData.write(pw, dates.y5, ds.y5, 1.0);
        SipData.write(pw, dates.y4, ds.y4, 1.0);
        SipData.write(pw, dates.y3, ds.y3, 1.0);
        SipData.write(pw, dates.y2, ds.y2, 1.0);
        SipData.writeAvgQtr(pw, dates.y1, dates.y0, ds.q8, ds.q7, ds.q6, ds.q5, ds.q4, ds.q3, ds.q2, ds.q1);
      }
    }
  }

  /**
   *
   * net.ajaskey.market.tools.sipro.v5.writeEps
   *
   * @param totIncome
   * @param totShr
   * @param epsStr
   * @param epsannualStr
   * @param peStr
   * @param dates
   * @param dsPrices
   * @throws FileNotFoundException
   */
  private static void writeEps(final DataSet totIncome, final DataSet totShr, final String epsStr, final String epsannualStr, final String peStr,
      final DateSet dates, final DataSet dsPrices) throws FileNotFoundException {

    final DataSet totEps = DataSet.scale(DataSet.div(totIncome, totShr), 40.0);
    SipData.write(totEps, epsStr, dates, false);

    // totEps.mode = DataSet.dMode.SEQUENTIAL;
    // SipData5.write(totEps, epsannualStr, dates, false);

    // final DataSet totPE = DataSet.scale(DataSet.div(dsPrices, totEps), 1.0);
    // totPE.mode = DataSet.dMode.SEQUENTIAL;
    // SipData5.write(totPE, peStr, dates, false);

  }

  /**
   * net.ajaskey.market.tools.sipro.v4.writePriceToDate
   *
   * @param dates
   * @param scaler
   * @param totSales
   * @throws FileNotFoundException
   */
  private static void writePriceToDate(final DateSet dates, final DataSet ds, final String fname, final double scaler) throws FileNotFoundException {

    try (PrintWriter pw = new PrintWriter(SipData.path + fname + ".csv")) {

      SipData.write(pw, dates.y7, dates.y7.q4.value / ds.y7 * scaler, 1.0);
      SipData.write(pw, dates.y6, dates.y6.q4.value / ds.y6 * scaler, 1.0);
      SipData.write(pw, dates.y5, dates.y5.q4.value / ds.y5 * scaler, 1.0);
      SipData.write(pw, dates.y4, dates.y4.q4.value / ds.y4 * scaler, 1.0);
      SipData.write(pw, dates.y3, dates.y3.q4.value / ds.y3 * scaler, 1.0);
      SipData.write(pw, dates.y2, dates.y2.q4.value / ds.y2 * scaler, 1.0);

      final double q[] = SipData.sumQuarters(ds);

      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q1.date.getTime()), dates.y1.q1.value / q[7] * scaler);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q2.date.getTime()), dates.y1.q2.value / q[6] * scaler);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q3.date.getTime()), dates.y1.q3.value / q[5] * scaler);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y1.q4.date.getTime()), dates.y1.q4.value / q[4] * scaler);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q1.date.getTime()), dates.y0.q1.value / q[3] * scaler);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q2.date.getTime()), dates.y0.q2.value / q[2] * scaler);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q3.date.getTime()), dates.y0.q3.value / q[1] * scaler);
      pw.printf("%s,%.2f%n", SipData.sdfOptuma.format(dates.y0.q4.date.getTime()), dates.y0.q4.value / q[0] * scaler);
    }
  }
}
