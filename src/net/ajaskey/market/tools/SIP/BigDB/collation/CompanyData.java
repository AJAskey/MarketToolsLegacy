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
package net.ajaskey.market.tools.SIP.BigDB.collation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.DowEnum;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;
import net.ajaskey.market.tools.SIP.BigDB.dataio.CompanyFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class CompanyData {

  /**
   * Returns a list of companies with FieldData for each quarter of requested
   * ticker list.
   *
   * @param tickers The individual stock symbols
   * @return List of CompanyData
   */
  public static List<CompanyData> getCompanies(List<String> tickers) {
    final List<CompanyData> retList = new ArrayList<>();
    if (tickers != null) {
      for (final String ticker : tickers) {
        final CompanyData cd = CompanyData.getCompany(ticker);
        retList.add(cd);
      }
    }
    return retList;
  }

  /**
   * Returns a list of CompanyData for tickers, the year, and quarter requested.
   *
   * @param tickers List of string ticker values,
   * @param yr      year
   * @param qtr     quarter
   * @return List of CompanyData
   */
  public static List<CompanyData> getCompanies(List<String> tickers, int yr, int qtr) {

    final List<CompanyData> ret = new ArrayList<>();

    for (final String ticker : tickers) {

      final CompanyData cd = new CompanyData(ticker);
      final FieldData fd = CompanyData.getCompany(ticker, yr, qtr);
      cd.fdList.add(fd);
      ret.add(cd);
    }
    return ret;
  }

  /**
   * Returns a CompanyData for each quarter of requested ticker
   *
   * @param ticker The individual stock symbol
   * @return CompanyData
   */
  public static CompanyData getCompany(String ticker) {

    if (ticker != null) {

      try {
        final CompanyData cd = new CompanyData(ticker);

        final String[] ext = { "txt", "gz" };
        final List<File> fList = Utils.getDirTree(FieldData.outbasedir, ext);
        for (final File f : fList) {
          if (f.getName().startsWith(ticker.toUpperCase() + "-")) {
            // System.out.println(f.getAbsolutePath());
            final int yr = CompanyData.parseYear(f.getName());
            final int qtr = CompanyData.parseQuarter(f.getName());
            final FieldData fd = FieldData.getFromDb(ticker, yr, qtr, FiletypeEnum.TEXT);
            fd.setYear(yr);
            fd.setQuarter(qtr);
            cd.fdList.add(fd);
          }

        }
        return cd;
      }
      catch (final Exception e) {
      }
    }
    return null;
  }

  /**
   * Returns FieldData for ticker, year, and quarter of requested
   *
   * @param ticker The individual stock symbol
   * @param yr     year
   * @param qtr    quarter
   * @return FieldData
   */
  public static FieldData getCompany(String ticker, int yr, int qtr) {

    final FieldData ret = FieldData.getFromDb(ticker, yr, qtr, FiletypeEnum.BINARY);

    return ret;
  }

  /**
   * Returns a list of tickers from the requesting Dow index
   *
   * @param index DowEnum
   * @param yr    year
   * @param qtr   quarter
   * @return List of String
   */
  public static List<String> getTickers(DowEnum index, int yr, int qtr) {
    final List<String> ret = new ArrayList<>();

    try {
      final List<File> files = CompanyData.getFiles(yr, qtr);

      List<String> input = null;
      for (final File f : files) {
        if (f.getName().endsWith(".gz")) {
          input = TextUtils.readGzipFile(f, true);
        }
        else {
          input = TextUtils.readTextFile(f, true);
        }

        final CompanyFileData cfd = CompanyFileData.readFromDb(input);
        if (cfd.getDowIndex() == index) {
          ret.add(cfd.getTicker());
        }
      }
    }
    catch (final Exception e) {
      FieldData.getWarning(e);
    }
    return ret;
  }

  /**
   * Returns a list of tickers from the requested market exchange.
   *
   * @param index ExchEnum
   * @param yr    year
   * @param qtr   quarter
   * @return List of String
   */
  public static List<String> getTickers(ExchEnum index, int yr, int qtr) {

    final List<String> ret = new ArrayList<>();

    try {
      final List<File> files = CompanyData.getFiles(yr, qtr);

      List<String> input = null;

      if (files != null) {
        for (final File f : files) {
          if (f.getName().endsWith(".gz")) {
            input = TextUtils.readGzipFile(f, true);
          }
          else {
            input = TextUtils.readTextFile(f, true);
          }

          final CompanyFileData cfd = CompanyFileData.readFromDb(input);
          if (cfd.getExchange() == index) {
            ret.add(cfd.getTicker());
          }
        }
      }
    }
    catch (final Exception e) {
      FieldData.getWarning(e);
    }
    return ret;
  }

  /**
   * Returns a list of tickers for the year and quarter requested.
   *
   * @param yr  year
   * @param qtr quarter
   * @return List of String
   */
  public static List<String> getTickers(int yr, int qtr) {
    final List<String> ret = new ArrayList<>();
    try {
      final String[] ext = { "txt", "gz" };
      final String dir = String.format("%s%d/Q%d", FieldData.outbasedir, yr, qtr);
      final List<File> fList = Utils.getDirTree(dir, ext);
      if (fList != null) {
        for (final File f : fList) {
          final String name = f.getName();
          final int idx = name.indexOf("-fundamental");
          if (idx > 0) {
            final String s = name.substring(0, idx);
            ret.add(s);
          }
        }
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
      ret.clear();
    }
    return ret;
  }

  /**
   * Returns of list of tickers from the requested SnP index
   *
   * @param index SnpEnum
   * @param yr    year
   * @param qtr   quarter
   * @return List of String
   */
  public static List<String> getTickers(SnpEnum index, int yr, int qtr) {

    final List<String> ret = new ArrayList<>();

    try {

      final List<File> files = CompanyData.getFiles(yr, qtr);

      List<String> input = null;
      for (final File f : files) {
        if (f.getName().endsWith(".gz")) {
          input = TextUtils.readGzipFile(f, true);
        }
        else {
          input = TextUtils.readTextFile(f, true);
        }

        final CompanyFileData cfd = CompanyFileData.readFromDb(input);
        if (cfd.getSnpIndex() == index) {
          ret.add(cfd.getTicker());
        }
      }
    }
    catch (final Exception e) {
      FieldData.getWarning(e);
    }
    return ret;
  }

  /**
   * Private procedure to get all files for a year and quarter.
   *
   * @param year
   * @param quarter
   * @return List of File
   */
  private static List<File> getFiles(int year, int quarter) {
    final List<File> ret = new ArrayList<>();
    final String[] ext = { "txt", "gz" };
    final String dir = String.format("%s%d/Q%d", FieldData.outbasedir, year, quarter);
    final List<File> fList = Utils.getDirTree(dir, ext);
    for (final File f : fList) {
      ret.add(f);
    }
    return ret;
  }

  /**
   *
   * @param ticker
   * @param tickers The individual stock symbols
   * @return TRUE is ticker found. False if not.
   */
  private static boolean isTickerInList(String ticker, List<String> tickers) {
    for (final String s : tickers) {
      if (s.equalsIgnoreCase(ticker)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Private procedure to parse the ticker out of the file name.
   *
   * @param name
   * @return
   */
  private static String parseName(String name) {
    String ret = "";
    try {
      final int idx = name.indexOf("-fundamental-data-");
      ret = name.substring(0, idx);
    }
    catch (final Exception e) {
      ret = "";
    }
    return ret;
  }

  /**
   * Private procedure to parse the quarter out of the file name.
   *
   * @param name
   * @return
   */
  private static int parseQuarter(String name) {
    int ret = 0;
    try {
      final int idx = name.indexOf("-data-");
      final String sQtr = name.substring(idx + 11, idx + 12);
      ret = Integer.parseInt(sQtr);
    }
    catch (final Exception e) {
      ret = 0;
    }
    return ret;
  }

  /**
   * Private procedure to parse the year out of the file name.
   *
   * @param name
   * @return
   */
  private static int parseYear(String name) {
    int ret = 0;
    try {
      final int idx = name.indexOf("-data-");
      final String sYr = name.substring(idx + 6, idx + 10);
      ret = Integer.parseInt(sYr);
    }
    catch (final Exception e) {
      ret = 0;
    }
    return ret;
  }

  private final List<FieldData> fdList = new ArrayList<>();
  private final String          ticker;

  /**
   * Constructor
   *
   * @param ticker The name of the individual stock symbol file
   */
  public CompanyData(String ticker) {
    this.ticker = ticker;
  }

  public List<FieldData> getFdList() {
    return this.fdList;
  }

  public String getTicker() {
    return this.ticker;
  }

}
