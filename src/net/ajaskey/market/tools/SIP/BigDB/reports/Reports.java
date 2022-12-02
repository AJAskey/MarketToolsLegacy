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

import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.DowEnum;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.Globals;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;
import net.ajaskey.market.tools.SIP.BigDB.collation.OneCompanyData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldDataQuarter;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldDataYear;

public class Reports {

  /**
   *
   * @param yr
   * @param qtr
   * @return
   */
  public static String companySummary(int yr, int qtr) {

    String ret = Reports.header(yr, qtr);

    for (final FieldDataYear fdy : Globals.allDataList) {

      if (yr == fdy.getYear()) {

        if (fdy.isInUse()) {

          if (fdy.quarterDataAvailable(qtr)) {

            final FieldDataQuarter fdq = fdy.getQ(qtr);

            for (final FieldData fd : fdq.fieldDataList) {
              final String s = Reports.companyLine(fd);
              ret += s;
            }
          }
        }
        break;
      }
    }
    return ret;
  }

  /**
   *
   * @param yrs
   * @return
   */
  public static String companySummary(int[] yrs) {

    String ret = "";

    for (final int year : yrs) {

      ret += String.format("%n----------%d Q1----------------%n%n", year);
      ret += Reports.companySummary(year, 1);
      ret += String.format("%n----------%d Q2----------------%n%n", year);
      ret += Reports.companySummary(year, 2);
      ret += String.format("%n----------%d Q3----------------%n%n", year);
      ret += Reports.companySummary(year, 3);
      ret += String.format("%n----------%d Q4----------------%n%n", year);
      ret += Reports.companySummary(year, 4);
    }
    return ret;
  }

  /**
   *
   * @param ticker
   * @return
   */
  public static List<String> getCompanyNetIncome(String ticker) {

    final List<String> ret = new ArrayList<>();

    final List<OneCompanyData> ocdList = OneCompanyData.getCompany(ticker);

    for (final OneCompanyData ocd : ocdList) {
      final double net = ocd.getQ2().getIncSheetData().getNetIncYr()[0];
      final String s = String.format("%d : %f", ocd.getYear(), net);
      ret.add(s);
    }

    return ret;
  }

  /**
   *
   * @param ticker
   * @param startYr
   * @param endYr
   * @param quarter
   * @return
   */
  public static List<String> getCompanyNetIncome(String ticker, int startYr, int endYr, int quarter) {

    final List<String> ret = new ArrayList<>();

    for (int yr = startYr; yr <= endYr; yr++) {
      final FieldData fd = FieldData.getFromDb(ticker, yr, quarter, FiletypeEnum.BINARY);
      final String s = String.format("%d : %f", yr, fd.getIncSheetData().getNetIncYr()[0]);
      ret.add(s);
    }

    return ret;

  }

  /**
   * Returns a list of String for all tickers found matching the input index
   * value.
   *
   * @param yr
   * @param qtr
   * @param index       - INDUSTRIAL, TRANPORTATION, UTILITY
   * @param tickersOnly True output is only tickers. False if a summary of company
   *                    is returned.
   * @return A NL delimited string of data
   */
  public static String getDowIndex(int yr, int qtr, DowEnum index, boolean tickersOnly) {

    String ret = "";
    if (!tickersOnly) {
      ret += Reports.header(yr, qtr);
    }

    for (final FieldDataYear fdy : Globals.allDataList) {

      if (yr == fdy.getYear()) {

        if (fdy.isInUse()) {

          if (fdy.quarterDataAvailable(qtr)) {

            final FieldDataQuarter fdq = fdy.getQ(qtr);

            for (final FieldData fd : fdq.fieldDataList) {
              if (fd.getCompanyInfo().getDowIndex().equals(index)) {
                String s = "";
                if (tickersOnly) {
                  s = String.format("%s%n", fd.getTicker());
                }
                else {
                  s = Reports.companyLine(fd);
                }
                ret += s;
              }
            }
          }
        }
        break;
      }
    }
    return ret;
  }

  /**
   * Returns a list of String for all tickers found matching the input exch value.
   *
   * @param yr
   * @param qtr
   * @param exch        - NYSE, NASDAQ, AMEX, OTC
   * @param tickersOnly True output is only tickers. False if a summary of company
   *                    is returned.
   * @return A NL delimited string of data
   */
  public static String getExchange(int yr, int qtr, ExchEnum exch, boolean tickersOnly) {

    String ret = "";
    if (!tickersOnly) {
      ret += Reports.header(yr, qtr);
    }

    for (final FieldDataYear fdy : Globals.allDataList) {

      if (yr == fdy.getYear()) {

        if (fdy.isInUse()) {

          if (fdy.quarterDataAvailable(qtr)) {

            final FieldDataQuarter fdq = fdy.getQ(qtr);

            for (final FieldData fd : fdq.fieldDataList) {
              if (fd.getCompanyInfo().getExchange().equals(exch)) {
                String s = "";
                if (tickersOnly) {
                  s = String.format("%s%n", fd.getTicker());
                }
                else {
                  s = Reports.companyLine(fd);
                }
                ret += s;
              }
            }
          }
        }
        break;
      }
    }
    return ret;
  }

  /**
   * Returns a list of String for all tickers found matching the input index
   * value.
   *
   * @param yr
   * @param qtr
   * @param index       - SP500, SP400, SP600
   * @param tickersOnly True output is only tickers. False if a summary of company
   *                    is returned.
   * @return A NL delimited string of data
   */
  public static String getSnpIndex(int yr, int qtr, SnpEnum index, boolean tickersOnly) {

    String ret = "";
    if (!tickersOnly) {
      ret += Reports.header(yr, qtr);
    }
    for (final FieldDataYear fdy : Globals.allDataList) {

      if (yr == fdy.getYear()) {

        if (fdy.isInUse()) {

          if (fdy.quarterDataAvailable(qtr)) {

            final FieldDataQuarter fdq = fdy.getQ(qtr);

            for (final FieldData fd : fdq.fieldDataList) {
              if (fd.getCompanyInfo().getSnpIndex().equals(index)) {
                String s = "";
                if (tickersOnly) {
                  s = String.format("%s%n", fd.getTicker());
                }
                else {
                  s = Reports.companyLine(fd);
                }
                ret += s;
              }
            }
          }
        }
        break;
      }
    }
    return ret;
  }

  /**
   *
   * @return
   */
  public static String memoryOverview() {

    String ret = "";

    for (int year = Globals.startYear; year <= Globals.endYear; year++) {

      final FieldDataYear fdy = Globals.getYear(year);
      if (fdy != null) {

        ret += String.format("%n----------%d Q1----------------%n", year);
        int qtr = 1;
        int knt = 0;
        if (fdy.quarterDataAvailable(qtr)) {
          knt = fdy.getQ(qtr).fieldDataList.size();
        }
        ret += String.format("  Companies : %d%n", knt);

        ret += String.format("%n----------%d Q2----------------%n", year);
        qtr = 2;
        knt = 0;
        if (fdy.quarterDataAvailable(qtr)) {
          knt = fdy.getQ(qtr).fieldDataList.size();
        }
        ret += String.format("  Companies : %d%n", knt);

        ret += String.format("%n----------%d Q3----------------%n", year);
        qtr = 3;
        knt = 0;
        if (fdy.quarterDataAvailable(qtr)) {
          knt = fdy.getQ(qtr).fieldDataList.size();
        }
        ret += String.format("  Companies : %d%n", knt);

        ret += String.format("%n----------%d Q4----------------%n", year);
        qtr = 4;
        knt = 0;
        if (fdy.quarterDataAvailable(qtr)) {
          knt = fdy.getQ(qtr).fieldDataList.size();
        }
        ret += String.format("  Companies : %d%n", knt);
      }
    }
    return ret;
  }

  /**
   * Procedure takes a NL delimited string and returns a list of strings.
   *
   * @param str NL delimited string from previous report
   * @return List of String
   */
  public static List<String> outputToList(String str) {
    final List<String> ret = new ArrayList<>();
    final String[] fld = str.split(Utils.NL);

    for (final String s : fld) {
      ret.add(s);
    }

    return ret;
  }

  /**
   *
   * @param fd
   * @return
   */
  private static String companyLine(FieldData fd) {
    return String.format("%-10s\t%-50s\t%-15s\t%-8s\t%-1s%n", fd.getTicker(), fd.getCompanyInfo().getName(), fd.getCompanyInfo().getExchange(),
        fd.getCompanyInfo().getSnpIndexStr(), fd.getCompanyInfo().getDowIndexStr(), fd.getCompanyInfo().getExchange());
  }

  /**
   *
   * @param yr
   * @param qtr
   * @return
   */
  private static String header(int yr, int qtr) {
    return String.format("Summary of requested Company Ticker, Name, Exchange SnP, Dow for year %d quarter %d%n", yr, qtr);
  }

}
