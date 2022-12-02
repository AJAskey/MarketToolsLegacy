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
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.market.optuma.TickerPriceData;

public class IndexGrowth extends CompanyData {

  public final static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  static double day0   = 0.0;
  static double day365 = 0.0;

  static double   day65  = 0.0;
  static double   day730 = 0.0;
  static DateTime qDate  = null;
  // static final DateTime recentQtr = new DateTime(2020, DateTime.JANUARY, 31);
  static final DateTime  recentQtr = new DateTime(2020, DateTime.SEPTEMBER, 14);
  static TickerPriceData spxData   = null;

  static DateTime yesterday = new DateTime();

  // public static List<String> sectorList;

  /**
   * net.ajaskey.market.tools.SIP.main
   *
   * @param args
   * @throws IOException
   */
  public static void main(final String[] args) throws IOException {

    if (!CompanyData.validateInputFiles()) {
      return;
    }

    Utils.makeDir("sipout");

    IndexGrowth.yesterday.add(DateTime.DATE, -1);

    final TickerPriceData spxData = new TickerPriceData("WI", "SPX");
    IndexGrowth.day0 = spxData.getLatest();
    IndexGrowth.day65 = spxData.getOffsetPrice(65);
    final double spxChg13wk = (IndexGrowth.day0 - IndexGrowth.day65) / IndexGrowth.day65 * 100.0;

    QuarterlyData.init();

    CompanyData.readBsdData(CompanyData.balsheetFile);
    CompanyData.readIdData(CompanyData.incstatementFile);
    CompanyData.readCashData(CompanyData.cashFile);
    CompanyData.readMiscData(CompanyData.miscFile, spxChg13wk);

    System.out.println(CompanyData.companyList.size());

    IndexGrowth.writeCompanyInfo();

    final List<String> criteriaList = new ArrayList<>();

    criteriaList.add("SP500,ALL,ALL");
    criteriaList.add("!SP500,ALL,ALL");

    criteriaList.add("ALL,ALL,REALESTATE,Group");
    criteriaList.add("ALL,ALL,SEMICONDUCTOR,Group");
    criteriaList.add("ALL,ALL,RETAILERS,Group");
    criteriaList.add("ALL,ALL,TRANSPORTS,Group");

    for (final String criteria : criteriaList) {

      final List<String> latestQDate = new ArrayList<>();

      final List<CompanyData> indexList = new ArrayList<>();
      // Filter For Index
      for (final CompanyData cd : CompanyData.companyList) {
        if (IndexGrowth.isMatch(cd, IndexGrowth.recentQtr, criteria)) {
          // System.out.println(cd.ticker);
          indexList.add(cd);
        }
      }
      System.out.println("indexList   : " + indexList.size());
      System.out.println("companyList : " + companyList.size());

      final List<CompanyData> notIndexList = new ArrayList<>();
      // Filter For Index
      for (final CompanyData cd : CompanyData.companyList) {
        if (IndexGrowth.recentQtr.isGreaterThan(cd.eoq)) {
          notIndexList.add(cd);
        }
      }
      System.out.println("notIndexList : " + notIndexList.size());

      try (PrintWriter pw = new PrintWriter("sipout/NoEarningsReported.txt")) {
        for (final CompanyData cd : notIndexList) {
          pw.printf("%-10s %s %10s %50s %55s%n", cd.ticker, cd.eoq, cd.spIndex, cd.sector, cd.industry);
        }
      }

      // Latest Quarter List
      for (final CompanyData cd : indexList) {
        String q1Str = cd.eoq.format("MMM-yyyy");
        q1Str = cd.eoq.format("yyyy-MM-dd");
        latestQDate.add(String.format("%s_%s\t%s\t%s\t%s", q1Str, cd.ticker, cd.sector, cd.industry, cd.spIndex));
      }
      Collections.sort(latestQDate, Collections.reverseOrder());

      // Write Total Data
      IndexGrowth.qDate = new DateTime();
      IndexGrowth.qDate.add(DateTime.YEAR, -1);
      IndexGrowth.day365 = spxData.getClose(IndexGrowth.qDate);
      IndexGrowth.qDate.add(DateTime.YEAR, -1);
      IndexGrowth.day730 = spxData.getClose(IndexGrowth.qDate);

      TotalData td = new TotalData(IndexGrowth.day0, IndexGrowth.day365, IndexGrowth.day730);
      for (final CompanyData cd : indexList) {
        if (!cd.ticker.equals("BRK.A")) {
          td.add(cd);
          System.out.println("Adding : " + cd.ticker);
        }
      }

      td.sum();
      TotalData.setLatestQDate(latestQDate);

      final String s = String.format("%s", criteria.replace(",", "_").replace("!", "Not_"));
      final String sData = td.process(s, IndexGrowth.yesterday);

      String dir = "sipout/Fundies";
      final String fld[] = criteria.split(",");
      if (fld.length > 3 && fld[3].length() > 0) {
        dir += "/" + fld[3];
      }
      Utils.makeDir(dir);
      final String tdfn = String.format("%s/%s_TotalData_%s.txt", dir, criteria.replace(",", "_"), IndexGrowth.yesterday.format("yyyy-MM-dd"));
      try (PrintWriter pw = new PrintWriter(tdfn)) {
        pw.printf("%s%n%n", sData);
        for (final String sDate : latestQDate) {
          pw.println(sDate);
        }
      }

      td = null;
    }

    IndexGrowth.writeIndustryData(true);
    IndexGrowth.writeIndustryData(false);

    IndexGrowth.writeSectorData(true);
    IndexGrowth.writeSectorData(false);

  }

  /**
   *
   * @param dir
   * @param Filename
   * @param cdList
   */
  public static void writeExcelFile(String dir, String Filename, List<CompanyData> cdList) {

  }

  /**
   *
   * @param cd
   * @param recentQtr
   * @param criteria
   * @return
   */
  private static boolean isMatch(CompanyData cd, DateTime recentQtr, String criteria) {
    final String fld[] = criteria.split(",");
    final String index = fld[0].trim();
    final String sector = fld[1].trim();
    final String industry = fld[2].trim();

    if (cd.eoq.isGreaterThan(recentQtr)) {

      // Check RealEstate
      if (industry.equalsIgnoreCase("REALESTATE")) {
        // System.out.printf("%s\t--\t%s%n", industry, cd.industry);
        if (cd.industry.equalsIgnoreCase("HOMEBUILDING")) {
          return true;
        }
        else if (cd.industry.equalsIgnoreCase("REAL ESTATE SERVICES")) {
          return true;
          // } else if (cd.industry.contains("Reits")) {
          // return true;
        }
        return false;
      }

      // Check Semiconductor
      if (industry.equalsIgnoreCase("SEMICONDUCTOR")) {
        // System.out.printf("%s\t--\t%s%n", industry, cd.industry);
        if (cd.industry.toUpperCase().contains("SEMICONDUCTOR")) {
          return true;
        }
        return false;
      }

      // Check Retailers
      if (industry.equalsIgnoreCase("RETAILERS")) {
        // System.out.printf("%s\t--\t%s%n", industry, cd.industry);
        if (cd.industry.toUpperCase().contains("RETAILERS")) {
          return true;
        }
        return false;
      }

      // Check TRANSPORTS
      if (industry.equalsIgnoreCase("TRANSPORTS")) {
        // System.out.printf("%s\t--\t%s%n", industry, cd.industry);
        if (cd.industry.toUpperCase().equalsIgnoreCase("AIRLINES")) {
          return true;
        }
        else if (cd.industry.toUpperCase().contains("COURIER, POSTAL")) {
          return true;
        }
        else if (cd.industry.toUpperCase().contains("FREIGHT & LOGISTICS")) {
          return true;
        }
        else if (cd.industry.toUpperCase().contains("PASSENGER")) {
          return true;
        }
        else if (cd.industry.toUpperCase().contains("HIGHWAYS & RAIL")) {
          return true;
        }
        return false;
      }

      // Check for Industry
      if (!industry.equalsIgnoreCase("ALL")) {
        if (cd.industry.equalsIgnoreCase(industry)) {
          return true;
        }
        return false;
      }

      if (index.equalsIgnoreCase("!SP500")) {
        if (!cd.spIndex.equalsIgnoreCase("SP500")) {
          if (sector.equalsIgnoreCase("ALL")) {
            return true;
          }
          else if (cd.sector.equalsIgnoreCase(sector)) {
            return true;
          }
        }
      }
      else if (index.equalsIgnoreCase("ALL")) {
        if (sector.equalsIgnoreCase("ALL")) {
          return true;
        }
        else if (cd.sector.equalsIgnoreCase(sector)) {
          if (industry.equalsIgnoreCase("ALL")) {
            return true;
          }
        }
      }
      else if (cd.spIndex.equalsIgnoreCase(index)) {
        if (sector.equalsIgnoreCase("ALL")) {
          return true;
        }
        else if (cd.sector.equalsIgnoreCase(sector)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   *
   * @param val
   * @param lim
   * @return
   */
  private static double limit(double val, double lim) {
    double ret = 0.0;
    if (val > 0.0) {
      ret = Math.min(val, lim);
    }
    else {
      ret = Math.max(val, -lim);
    }
    return ret;
  }

  /***
   *
   * @param td
   * @param list
   * @return
   */
  private static List<CompanyData> updateScore(TotalData td, List<CompanyData> list) {

    final List<CompanyData> retList = new ArrayList<>();

    for (final CompanyData cd : list) {

      double scr = 0.0;

      if (cd.cashData.cashFromOps.q1 <= 0.0) {
        scr -= 100.0;
      }

      if (cd.workingCapital < 0.0) {
        scr -= 50.0;
      }

      if (cd.bsd.equity.q1 < 0.0) {
        scr -= 100.0;
      }

      scr += IndexGrowth.limit(cd.cashData.cashFromOps.dd.seqGrowth, 100.0);

      scr += IndexGrowth.limit(cd.id.netIncome.dd.qoqGrowth, 100.0);
      scr += IndexGrowth.limit(cd.id.sales.dd.qoqGrowth, 100.0);
      scr += IndexGrowth.limit(cd.q0EstGrowth, 100.0);

      scr += IndexGrowth.limit(cd.bsd.equity.dd.seqGrowth, 100.0);
      scr += IndexGrowth.limit(cd.bsd.assetsMinusGW.dd.seqGrowth, 100.0);
      scr += IndexGrowth.limit(cd.bsd.currentAssets.dd.seqGrowth, 100.0);
      scr += IndexGrowth.limit(cd.roe * 5.0, 100.0);

      scr -= IndexGrowth.limit(cd.bsd.ltDebt.dd.seqGrowth, 100.0);
      scr -= IndexGrowth.limit(cd.bsd.currLiab.dd.seqGrowth, 100.0);

      cd.gscore = scr;

      retList.add(cd);
      // System.out.println(scr);
    }
    Collections.sort(retList, new SortGScore());
    return retList;

  }

  /**
   *
   */
  private static void writeCompanyInfo() {
    try (PrintWriter pw = new PrintWriter("sipout/CompanyInfo.csv")) {
      pw.println("Ticker,Sector,Industry,Index,EOQ");
      for (final CompanyData cd : CompanyData.companyList) {
        final String ind = cd.industry.replace(",", "");
        pw.printf("%s,%s,%s,%s,%s%n", cd.ticker, cd.sector, ind, cd.spIndex, cd.eoq);
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   *
   * @param cd
   * @return
   * @throws FileNotFoundException
   */
  private static String writeIndividualTd(CompanyData cd) throws FileNotFoundException {
    final TotalData td = new TotalData(IndexGrowth.day0, IndexGrowth.day365, IndexGrowth.day730);
    td.add(cd);
    td.setPrintFormat(TotalData.printFormatShort);
    final String s = String.format("%s:%s", cd.ticker, cd.name);
    return td.process(s, IndexGrowth.yesterday);
  }

  /**
   *
   * @param now
   * @param recentQtr
   * @throws FileNotFoundException
   */
  private static void writeIndustryData(Boolean spxOnly) throws FileNotFoundException {

    final List<String> allIndustries = SipUtils.findIndustries(CompanyData.companyList);
    Collections.sort(allIndustries);

    //
    // All industries stats
    //
    for (final String ind : allIndustries) {
      // System.out.println(ind);

      final List<CompanyData> cFromInd = new ArrayList<>();

      final TotalData td = new TotalData(IndexGrowth.day0, IndexGrowth.day365, IndexGrowth.day730);
      final List<String> latestQDate = new ArrayList<>();

      String c = String.format("ALL,ALL,%s", ind);
      for (final CompanyData cd : CompanyData.companyList) {

        if (IndexGrowth.isMatch(cd, IndexGrowth.recentQtr, c)) {

          Boolean process = true;
          if (spxOnly) {
            if (!cd.spIndex.equals("SP500")) {
              process = false;
            }
          }

          if (process) {

            cFromInd.add(cd);
            // System.out.printf("Adding : %s\t--\t%s, %s, %s%n", ind, cd.ticker, cd.sector,
            // cd.industry);
            td.add(cd);

            String q1Str = cd.eoq.format("MMM-yyyy");
            q1Str = cd.eoq.format("yyyy-MM-dd");
            latestQDate.add(String.format("%s_%s\t%s\t%s\t%s", q1Str, cd.ticker, cd.sector, cd.industry, cd.spIndex));
          }
        }
      }
      td.sum();
      Collections.sort(latestQDate, Collections.reverseOrder());
      TotalData.setLatestQDate(latestQDate);

      if (spxOnly) {
        c = String.format("SP500,ALL,%s", ind);
      }
      final String s = String.format("%s", c.replace(",", "_"));
      final String sData = td.process(s, IndexGrowth.yesterday);

      final String dir = "sipout/Fundies/Industries";
      Utils.makeDir(dir);
      final String tdfn = String.format("%s/%s_TotalData_%s.txt", dir, s, IndexGrowth.yesterday.format("yyyy-MM-dd"));

      try (PrintWriter pw = new PrintWriter(tdfn)) {

        pw.println(sData);

        final List<CompanyData> cdSorted = IndexGrowth.updateScore(td, cFromInd);

        // System.out.println(td.getKnt());
        // System.out.println(cFromInd.size());
        // System.out.println(cdSorted.size());

        for (final CompanyData cd : cdSorted) {
          pw.printf("  %7s : %10.1f%n", cd.ticker, cd.gscore);
        }

        for (final CompanyData cd : cdSorted) {
          final String sCd = IndexGrowth.writeIndividualTd(cd);
          pw.printf("%s%n", sCd);
        }

      }
    }
  }

  /**
   *
   * @param spxOnly
   * @throws FileNotFoundException
   */
  private static void writeSectorData(boolean spxOnly) throws FileNotFoundException {

    final List<String> allSectors = SipUtils.findSectors(CompanyData.companyList);
    Collections.sort(allSectors);

    //
    // All industries stats
    //
    for (final String sec : allSectors) {
      // System.out.println(sec);

      final List<CompanyData> cFromSec = new ArrayList<>();

      final TotalData td = new TotalData(IndexGrowth.day0, IndexGrowth.day365, IndexGrowth.day730);
      final List<String> latestQDate = new ArrayList<>();

      String c = String.format("ALL,%s,ALL", sec);
      for (final CompanyData cd : CompanyData.companyList) {

        if (IndexGrowth.isMatch(cd, IndexGrowth.recentQtr, c)) {

          Boolean doProcess = true;
          if (spxOnly) {
            if (!cd.spIndex.equals("SP500")) {
              doProcess = false;
            }
          }

          if (doProcess) {
            cFromSec.add(cd);
            // System.out.printf("Adding : %s\t--\t%s, %s, %s%n", sec, cd.ticker, cd.sector,
            // cd.industry);
            td.add(cd);

            String q1Str = cd.eoq.format("MMM-yyyy");
            q1Str = cd.eoq.format("yyyy-MM-dd");
            latestQDate.add(String.format("%s_%s\t%s\t%s\t%s", q1Str, cd.ticker, cd.sector, cd.industry, cd.spIndex));
          }
        }
      }
      td.sum();
      Collections.sort(latestQDate, Collections.reverseOrder());
      TotalData.setLatestQDate(latestQDate);

      if (spxOnly) {
        c = String.format("SP500,%s,ALL", sec);
      }
      final String s = String.format("%s", c.replace(",", "_"));
      final String sData = td.process(s, IndexGrowth.yesterday);

      final String dir = "sipout/Fundies/Sectors";
      Utils.makeDir(dir);
      final String tdfn = String.format("%s/%s_TotalData_%s.txt", dir, s, IndexGrowth.yesterday.format("yyyy-MM-dd"));

      try (PrintWriter pw = new PrintWriter(tdfn)) {

        pw.println(sData);

        final List<CompanyData> cdSorted = IndexGrowth.updateScore(td, cFromSec);

//        System.out.println(td.getKnt());
//        System.out.println(cFromSec.size());
//        System.out.println(cdSorted.size());

        for (final CompanyData cd : cdSorted) {
          pw.printf("  %7s : %10.1f%n", cd.ticker, cd.gscore);
        }
      }
    }
  }

  /**
   * This method serves as a constructor for the class.
   *
   */
  public IndexGrowth() {

    super();

  }

}
