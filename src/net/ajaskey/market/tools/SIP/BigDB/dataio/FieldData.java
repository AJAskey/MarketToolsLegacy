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
package net.ajaskey.market.tools.SIP.BigDB.dataio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.Globals;
import net.ajaskey.market.tools.SIP.BigDB.collation.CompanySummary;
import net.ajaskey.market.tools.SIP.BigDB.collation.FieldDataBinary;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Utilities;

/**
 * This class provides most of the interfaces needed for reading SIP data and
 * writing DB data.
 */
public class FieldData implements Serializable {

  /**
   * Set this to the directories you store you SIP data (inbasedir) and where you
   * want your DB output to be stored (outbasedir).
   *
   * You do not need the SIP data as I have uploaded the database data to the DB
   * folder.
   */
  final public static String inbasedir        = String.format("data/BigDB/");
  final public static String outbasedir       = String.format("out/BigDB/");
  /**
   *
   */
  private static final long  serialVersionUID = 2772336615089382916L;

  /**
   * Returns formatted constructor error message
   *
   * @param e Exception thrown
   * @return String
   */
  public static String getConstructorError(Throwable e) {
    String ret = "";
    final String m = String.format("%s.%s", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName());
    ret += String.format("%nError. Unexpected exception in Constructor : %s%n", m);
    ret += String.format("  Line number = %d%n%n", e.getStackTrace()[0].getLineNumber());
    return ret;
  }

  /**
   * Returns formatted error message
   *
   * @param e Exception thrown
   * @return String
   */
  public static String getError(Throwable e) {
    String ret = "";
    final String m = String.format("%s.%s", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName());
    ret += String.format("%nError. Unexpected exception in method : %s%n", m);
    ret += String.format("  Line number = %d%n%n", e.getStackTrace()[0].getLineNumber());
    return ret;
  }

  /**
   * Returns a capitalized string
   *
   * @param enm ExchEnum
   * @return String
   */
  public static String getExchangeStr(ExchEnum enm) {

    String ret = "NONE";
    if (enm != null) {
      ret = enm.toString().toUpperCase();
    }
    return ret;
  }

  /**
   * Returns FieldData for requested ticker from year and quarter from DB file
   * data.
   *
   * @param ticker The individual stock symbol
   * @param yr     year
   * @param qtr    quarter
   * @param ft     FiletypeEnum TEXT or BINARY
   * @return FieldData or NULL is error
   */
  public static FieldData getFromDb(String ticker, int yr, int qtr, FiletypeEnum ft) {

    FieldData fd = null;
    try {
      if (ft == FiletypeEnum.TEXT) {
        fd = FieldData.parseFromDbData(yr, qtr, ticker);
      }
      else if (ft == FiletypeEnum.BINARY) {
        fd = FieldData.parseFromDbBinData(yr, qtr, ticker);
      }
      else {
        System.out.printf("Waring. Invalid Filetype in getFromDb : %s%n", ft.toString());
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fd = null;
    }

    return fd;
  }

  /**
   * Returns FieldData for requested ticker for year and quarter from internal
   * memory.
   *
   * @param tkr The individual stock symbol
   * @param yr  year
   * @param qtr quarter (1-4)
   * @return FieldData
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.Globals#getQFromMemory(String, int,
   *      int)
   */
  public static FieldData getFromMemory(String tkr, int yr, int qtr) {
    return Globals.getQFromMemory(tkr, yr, qtr);
  }

  /**
   * Returns List of FieldData for requested ticker list for year and quarter from
   * internal memory.
   *
   * @param tList The list of individual stock symbols
   * @param yr    year
   * @param qtr   quarter (1-4)
   * @return List of FieldData
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.Globals#getListFromMemory(List, int,
   *      int)
   */
  public static List<FieldData> getListFromMemory(List<String> tList, int yr, int qtr) {
    return Globals.getQFromMemory(tList, yr, qtr);
  }

  /**
   * Creates a full filename based on the input parameters.
   *
   * @param yr     year
   * @param qtr    quarter
   * @param ticker The individual stock symbol
   * @param ext    The desired extension of the returned file.
   * @return String
   */
  public static String getOutfileName(int yr, int qtr, String ticker, String ext) {

    String fname = "";
    try {
      final String yearDir = String.format("%s%s", FieldData.outbasedir, yr);
      final String qtrDir = String.format("%s/Q%s", yearDir, qtr);

      final String outdir = qtrDir;

      Utils.makeDir(String.format("%s", FieldData.outbasedir));
      Utils.makeDir(yearDir);
      Utils.makeDir(qtrDir);

      fname = String.format("%s/%s-fundamental-data-%dQ%d.%s", outdir, ticker, yr, qtr, ext);
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fname = "";
    }
    return fname;
  }

  /**
   *
   * Returns a list of FieldData for the requested year and quarter from internal
   * files.
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   * @param ft  FiletypeEnum TEXT, BINARY, or BIG_BINARY
   * @return List of FieldData or empty list if error
   */
  public static List<FieldData> getQFromDb(int yr, int qtr, FiletypeEnum ft) {

    List<FieldData> fdList = new ArrayList<>();
    try {
      if (ft == FiletypeEnum.TEXT) {
        fdList = FieldData.parseFromDbData(yr, qtr);
      }
      else if (ft == FiletypeEnum.BINARY) {
        fdList = FieldData.parseFromDbBinData(yr, qtr);
      }
      else if (ft == FiletypeEnum.BIG_BINARY) {
        fdList = FieldData.parseFromDbBigBinData(yr, qtr);
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fdList.clear();
    }

    return fdList;
  }

  /**
   *
   * Returns a list of FieldData for the requested year and quarter from internal
   * files.
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   * @return List of FieldData
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.Globals#getQFromMemory(int, int)
   */
  public static List<FieldData> getQFromMemory(int yr, int qtr) {
    return Globals.getQFromMemory(yr, qtr);
  }

  public static List<FieldData> getQFromMemory(List<String> list, int yr, int qtr) {
    return Globals.getQFromMemory(list, yr, qtr);
  }

  /**
   * Returns formatted warning message
   *
   * @param e Exception thrown
   * @return String
   */
  public static String getWarning(Throwable e) {
    String ret = "";
    final String m = String.format("%s.%s", e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName());
    ret += String.format("%nWarning. Unexpected exception in method : %s%n", m);
    ret += String.format("  Line number = %d%n%n", e.getStackTrace()[0].getLineNumber());
    return ret;
  }

  /**
   * Reads SIP tab separated data files. Stores the data in array for later use.
   *
   * @param yr        year
   * @param qtr       quarter (1-4)
   * @param ft        FiletypeEnum
   * @param overwrite
   * @return FALSE on any error, TRUE is successful processing
   */
  public static boolean parseSipData(int yr, int qtr, FiletypeEnum ft, boolean overwrite) {

    try {

      if (ft == FiletypeEnum.BIG_BINARY) {
        if (!overwrite) {
          final String outfname = String.format("%s%d/all-companies-%dQ%d.bin", FieldData.outbasedir, yr, yr, qtr);
          final File f = new File(outfname);
          if (f.exists()) {
            System.out.printf("File %s already exists.%n", outfname);
            return true;
          }
        }
      }

      System.out.printf("Updating Sip Data for %dQ%d%n", yr, qtr);

      CompanyFileData.clearList();
      CashFileData.clearList();
      EstimateFileData.clearList();
      SharesFileData.clearList();
      IncSheetFileData.clearList();
      BalSheetFileData.clearList();
      FieldDataBinary.clearList();

      Utils.makeDirs(String.format("out/BigDB/%d/Q%d", yr, qtr));

      final String yearDir = String.format("%s%s", FieldData.inbasedir, yr);
      final String dir = String.format("%s/Q%d/", yearDir, qtr);
      final String tail = String.format("%dQ%d.txt", yr, qtr);

      File dirCk = new File(dir);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested SIP Data Directory does not exist! %s%n", dir);
        return false;
      }

      System.out.printf("Processing SIP Year %d Quarter %d data.%n", yr, qtr);

      String head = String.format("CompanyInfo-");
      String ffname = String.format("%s%s%s", dir, head, tail);
      dirCk = new File(ffname);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested File does not exist! %s%n", ffname);
        return false;
      }
      CompanyFileData.readSipData(ffname);

      head = "Shares-";
      ffname = String.format("%s%s%s", dir, head, tail);
      dirCk = new File(ffname);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested File does not exist! %s%n", ffname);
        return false;
      }
      SharesFileData.readSipData(ffname);

      // System.out.println(SharesFileData.listToString());

      head = "Estimates-";
      ffname = String.format("%s%s%s", dir, head, tail);
      dirCk = new File(ffname);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested File does not exist! %s%n", ffname);
        return false;
      }
      EstimateFileData.readSipData(ffname);
      // System.out.println(EstimateFileData.listToString());

      head = "Balsheet-";
      String ffname1 = dir + head + "QTR-" + tail;
      String ffname2 = dir + head + "ANN-" + tail;
      dirCk = new File(ffname1);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested File does not exist! %s%n", ffname1);
        return false;
      }
      dirCk = new File(ffname2);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested File does not exist! %s%n", ffname2);
        return false;
      }
      BalSheetFileData.readSipData(ffname1, ffname2);
      // System.out.println(BalSheetFileData.listToString());

      head = "Income-";
      ffname1 = dir + head + "QTR-" + tail;
      ffname2 = dir + head + "ANN-" + tail;
      dirCk = new File(ffname1);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested File does not exist! %s%n", ffname1);
        return false;
      }
      dirCk = new File(ffname2);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested File does not exist! %s%n", ffname2);
        return false;
      }
      IncSheetFileData.readSipData(ffname1, ffname2);
      // System.out.println(IncSheetFileData.listToString());

      head = "Cash-";
      ffname = String.format("%s%s%s", dir, head, tail);
      dirCk = new File(ffname);
      if (!dirCk.exists()) {
        System.out.printf("Warning ... Requested File does not exist! %s%n", ffname);
        return false;
      }
      CashFileData.readSipData(ffname);

      if (!FieldData.validateData()) {
        return false;
      }

      /**
       * Write Company list for creating ticker lists
       */
      CompanySummary.write(yr, qtr);

      for (final CompanyFileData cfd : CompanyFileData.getList()) {

        final String ticker = cfd.getTicker();

        final SharesFileData sfd = SharesFileData.find(ticker);
        final EstimateFileData efd = EstimateFileData.find(ticker);
        final IncSheetFileData ifd = IncSheetFileData.find(ticker);
        final BalSheetFileData bfd = BalSheetFileData.find(ticker);
        final CashFileData cashfd = CashFileData.find(ticker);

        final FieldData fd = new FieldData(cfd, efd, sfd, ifd, bfd, cashfd, yr, qtr);

        if (ft == FiletypeEnum.BINARY) {
          final String fname = FieldData.getOutfileName(yr, qtr, ticker, "bin");
          FieldData.writeDbBinary(fname, fd);
        }
        else if (ft == FiletypeEnum.TEXT) {
          FieldData.writeDbOutput(cfd, efd, sfd, ifd, bfd, cashfd, yr, qtr);
        }
        else if (ft == FiletypeEnum.BIG_BINARY) {
          FieldDataBinary.add(cfd, efd, sfd, ifd, bfd, cashfd, yr, qtr);
        }
      }

      /**
       * Write big binary file for year and quarter data
       */
      if (ft == FiletypeEnum.BIG_BINARY) {
        FieldDataBinary.writeBinaryFile(yr, qtr);
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      return false;
    }
    return true;
  }

  /**
   * Set internal memory to data from firstYear to endYear.
   *
   * @param firstYear First full year of data
   * @param endYear   Last full year of data
   * @param ft        FiletypeEnum BIG_BINARY valid
   */
  public static void setMemory(int firstYear, int endYear, FiletypeEnum ft) {

    try {
      if (ft == FiletypeEnum.BIG_BINARY) {
        for (int yr = firstYear; yr <= endYear; yr++) {
          for (int qtr = 1; qtr <= 4; qtr++) {
            FieldData.readDbBigBinData(yr, qtr);
          }
        }
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
    }
  }

  public static void setAllMemory(FiletypeEnum ft) {
    try {
      if (ft == FiletypeEnum.BIG_BINARY) {
        for (int yr = Globals.startYear; yr <= Globals.endYear; yr++) {
          for (int qtr = 1; qtr <= 4; qtr++) {
            FieldData.readDbBigBinData(yr, qtr);
          }
        }
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
    }
  }

  /**
   * Sets internal memory to request year and quarter.
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   * @param ft  FiletypeEnum BINARY or TEXT valid
   */
  public static void setQMemory(int yr, int qtr, FiletypeEnum ft) {

    try {

      FieldData.checkQMemory(yr, qtr);

      System.out.printf("Setting internal memory : %d Q%d%n", yr, qtr);

      if (ft == FiletypeEnum.BINARY) {

        FieldData.readDbData(yr, qtr, ft);
      }
      else if (ft == FiletypeEnum.TEXT) {

        FieldData.readDbData(yr, qtr, ft);
      }
      else if (ft == FiletypeEnum.BIG_BINARY) {

        FieldData.readDbBigBinData(yr, qtr);
      }

      else {
        System.out.printf("Waring. Invalid Filetype in setQMemory : %s%n", ft.toString());
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
    }
  }

  /**
   * Used when reading SIP exchange SIP data.
   *
   * @param enumStr Exchange in string format
   * @return ExchEnum
   */
  static ExchEnum convertExchange(String enumStr) {
    return Utilities.convertExchange(enumStr);
  }

  private static boolean checkQMemory(int yr, int qtr) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * Reads individual company binary DB file.
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   * @return List of FieldData or empty list if error
   */
  private static List<FieldData> parseFromDbBigBinData(int yr, int qtr) {

    List<FieldData> fdList = new ArrayList<>();
    try {
      final String fname = String.format("%s%d/all-companies-%dQ%d.bin", FieldData.outbasedir, yr, yr, qtr);

      final File f = new File(fname);
      if (f.exists()) {
        fdList = FieldDataBinary.readBinaryFile(fname);
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fdList.clear();
    }
    return fdList;
  }

  /**
   * Read the company binary DB file corresponding to year and quarter
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   * @return List of FieldData or empty list if error
   */
  private static List<FieldData> parseFromDbBinData(int yr, int qtr) {

    final List<FieldData> fdList = new ArrayList<>();

    try {
      final String indir = String.format("%s%s/Q%d/", FieldData.outbasedir, yr, qtr);

      final File indirCk = new File(indir);
      if (!indirCk.exists()) {
        System.out.printf("Warning... DB directory does not exists. %s%n", indir);
        return null;
      }

      final String[] ext = { "bin" };
      final List<File> fList = Utils.getDirTree(indir, ext);
      for (final File f : fList) {
        final FieldData fd = FieldData.readBinaryFieldData(f.getAbsoluteFile());
        if (fd != null && fd.companyInfo.getTicker() != null) {
          fdList.add(fd);
        }
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fdList.clear();
    }

    return fdList;

  }

  /**
   *
   * @param yr     year
   * @param qtr    quarter
   * @param ticker The individual stock symbol
   * @return FieldData or NULL if error
   */
  private static FieldData parseFromDbBinData(int yr, int qtr, String ticker) {

    FieldData fd = null;
    try {
      final String fname = FieldData.getOutfileName(yr, qtr, ticker, "bin");
      final File f = new File(fname);

      if (f.exists()) {
        fd = FieldData.readBinaryFieldData(f);
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fd = null;
    }
    return fd;

  }

  /**
   * Reads data from DB into global memory. Calls to this for various years and
   * quarters "may" make processing faster for large comprehensive reports.
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   * @return List of FieldData for year and quarter or empty list if error
   */
  private static List<FieldData> parseFromDbData(int yr, int qtr) {

    final List<FieldData> fdList = new ArrayList<>();

    try {

      final String indir = String.format("%s%s/Q%d/", FieldData.outbasedir, yr, qtr);

      final File indirCk = new File(indir);
      if (!indirCk.exists()) {
        System.out.printf("Warning... DB directory does not exists. %s%n", indir);
        return null;
      }

      final String[] ext = { "txt", "gz" };
      final List<File> fList = Utils.getDirTree(indir, ext);
      for (final File f : fList) {

        List<String> data = null;
        if (f.getName().endsWith(".gz")) {
          data = TextUtils.readGzipFile(f, true);
        }
        else {
          data = TextUtils.readTextFile(f, true);
        }

        final FieldData fd = new FieldData(yr, qtr);

        fd.companyInfo = CompanyFileData.readFromDb(data);
        fd.shareData = SharesFileData.readFromDb(data);
        fd.estimateData = EstimateFileData.readFromDb(data);
        fd.incSheetData = IncSheetFileData.readFromDb(data);
        fd.balSheetData = BalSheetFileData.readFromDb(data);
        fd.cashData = CashFileData.readFromDb(data);
        fd.setNameFields(fd.companyInfo);
        fd.shareData.setNameFields(fd.companyInfo);
        fd.estimateData.setNameFields(fd.companyInfo);
        fd.incSheetData.setNameFields(fd.companyInfo);
        fd.balSheetData.setNameFields(fd.companyInfo);

        if (fd.companyInfo.getTicker() != null) {
          fdList.add(fd);
        }
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fdList.clear();
    }
    return fdList;
  }

  /**
   * Reads one file from DB based on year, quarter, and ticker.
   *
   * @param yr     year
   * @param qtr    quarter
   * @param ticker The individual stock symbol
   * @return FieldData for year, quarter, and ticker or NULL if error.
   */
  private static FieldData parseFromDbData(int yr, int qtr, String ticker) {

    FieldData fd = null;
    try {
      final String fname = FieldData.getOutfileName(yr, qtr, ticker, "txt");

      List<String> data = null;

      data = TextUtils.readTextFile(fname, true);
      if (data == null) {
        data = TextUtils.readGzipFile(fname + ".gz", true);
      }
      if (data == null) {
        System.out.printf("Warning... File not found %s", fname);
        return null;
      }

      fd = new FieldData(yr, qtr);

      fd.companyInfo = CompanyFileData.readFromDb(data);
      fd.shareData = SharesFileData.readFromDb(data);
      fd.estimateData = EstimateFileData.readFromDb(data);
      fd.incSheetData = IncSheetFileData.readFromDb(data);
      fd.balSheetData = BalSheetFileData.readFromDb(data);
      fd.setNameFields(fd.companyInfo);
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fd = null;
    }
    return fd;
  }

  /**
   * Perform physical read on binary data file containing FieldData.
   *
   * @param fname Full path of binary file to read.
   * @return FieldData
   */
  private static FieldData readBinaryFieldData(File fname) {

    FieldData fd;
    try {
      final ObjectInputStream objBinFile = new ObjectInputStream(new FileInputStream(fname));

      fd = (FieldData) objBinFile.readObject();
      objBinFile.close();

      return fd;
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
    }
    return null;
  }

  /**
   * Reads the DB for specific year and quarter into memory for future use. All
   * tickers are returned.
   *
   * @param yr     year
   * @param qtr    quarter 1-4
   * @param binary False for text output and TRUE for binary output
   * @return A list of FieldData for each ticket in the DB for year and quarter or
   *         empty List when error
   */
  private static void readDbBigBinData(int yr, int qtr) {

    List<FieldData> fdList = new ArrayList<>();

    try {

      if (Globals.checkLists(yr, qtr)) {
        System.out.printf(" DB data %d Q%d already in memory.%n", yr, qtr);
        return;
      }

      System.out.printf(" Reading BigBinary DB for %d Q%d%n", yr, qtr);

      fdList = FieldData.parseFromDbBigBinData(yr, qtr);

      Globals.setLists(yr, qtr, fdList);

    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fdList.clear();
    }
  }

  /**
   * Reads the DB for specific year and quarter into memory for future use. All
   * tickers are returned.
   *
   * @param yr  year
   * @param qtr quarter (1-4) 1-4
   * @param ft  FiletypeEnum
   * @return A list of FieldData for each ticket in the DB for year and quarter or
   *         empty List when error
   */
  private static List<FieldData> readDbData(int yr, int qtr, FiletypeEnum ft) {

    List<FieldData> fdList = new ArrayList<>();

    try {
      System.out.printf("Processing DB %d %d%n", yr, qtr);

      if (ft == FiletypeEnum.BINARY) {
        fdList = FieldData.parseFromDbBinData(yr, qtr);
      }
      else if (ft == FiletypeEnum.TEXT) {
        fdList = FieldData.parseFromDbData(yr, qtr);
      }
      else {
        System.out.printf("Waring. Invalid Filetype in readDbData : %s%n", ft.toString());
        return null;
      }

      Globals.setLists(yr, qtr, fdList);
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      fdList.clear();
    }
    return fdList;
  }

  /**
   * Checks for ticker mismatches between SIP data lists
   *
   * @return True if all match, False otherwise
   */
  private static boolean validateData() {

    final int sizeCompanyData = CompanyFileData.getListCount();
    final List<String> companyTickers = CompanyFileData.getTickers();

    if (CashFileData.getListCount() != sizeCompanyData) {
      System.out.println("Data Validation Error : CashFileData size != CompanyFileData size!");
      return false;
    }
    else if (!FieldData.validateDataTickers(CashFileData.getTickers(), companyTickers)) {
      System.out.println("Data Validation Error : Ticker mismatch in CashFileData!");
      return false;
    }

    if (EstimateFileData.getListCount() != sizeCompanyData) {
      System.out.println("Data Validation Error : EstimateFileData size != CompanyFileData size!");
      return false;
    }
    else if (!FieldData.validateDataTickers(EstimateFileData.getTickers(), companyTickers)) {
      System.out.println("Data Validation Error : Ticker mismatch in EstimateFileData!");
      return false;
    }

    if (SharesFileData.getListCount() != sizeCompanyData) {
      System.out.println("Data Validation Error : SharesFileData size != CompanyFileData size!");
      return false;
    }
    else if (!FieldData.validateDataTickers(SharesFileData.getTickers(), companyTickers)) {
      System.out.println("Data Validation Error : Ticker mismatch in SharesFileData!");
      return false;
    }

    if (BalSheetFileData.getListCount() != sizeCompanyData) {
      System.out.println("Data Validation Error : BalFileData size != CompanyFileData size!");
      return false;
    }
    else if (!FieldData.validateDataTickers(BalSheetFileData.getTickers(), companyTickers)) {
      System.out.println("Data Validation Error : Ticker mismatch in BalSheetFileData!");
      return false;
    }

    if (IncSheetFileData.getListCount() != sizeCompanyData) {
      System.out.println("Data Validation Error : IncSheetFileData size != CompanyFileData size!");
      return false;
    }
    else if (!FieldData.validateDataTickers(IncSheetFileData.getTickers(), companyTickers)) {
      System.out.println("Data Validation Error : Ticker mismatch in IncSheetFileData!");
      return false;
    }

    return true;
  }

  /**
   * Checks for ticker mismatches between SIP data lists
   *
   * @param tickers        List of tickers to check
   * @param companyTickers List of CompanyFileData tickers to use as baseline
   * @return True if all match, False otherwise
   */
  private static boolean validateDataTickers(List<String> tickers, List<String> companyTickers) {
    for (int i = 0; i < tickers.size(); i++) {
      if (!tickers.get(i).equals(companyTickers.get(i))) {
        System.out.printf("%s  :  %s%n", tickers.get(i), companyTickers.get(i));
        return false;
      }
    }
    return true;
  }

  /**
   * Writes each ticker data in binary format
   *
   * @param fname File name to write
   * @param fd    FieldData to write
   */
  private static void writeDbBinary(String fname, FieldData fd) {
    try {
      final ObjectOutputStream binObjFile = new ObjectOutputStream(new FileOutputStream(fname));

      binObjFile.writeObject(fd);
      binObjFile.close();
    }
    catch (final IOException e) {
      System.out.printf("Warning. Problems writing binary file %s", fname);
    }

  }

  /**
   * Sets up file names and writes data to DB files. Calls genOutput to create
   * data to be written.
   *
   * @param cfd  CompanyFileData
   * @param efd  EstimateFileData
   * @param sfd  SharesFileData
   * @param ifd  IncSheetFileData
   * @param bfd  BalSheetFileData
   * @param cash CashFileData
   * @param yr   year
   * @param qtr  quarter
   */
  private static void writeDbOutput(CompanyFileData cfd, EstimateFileData efd, SharesFileData sfd, IncSheetFileData ifd, BalSheetFileData bfd,
      CashFileData cash, int yr, int qtr) throws FileNotFoundException {

    try {
      final String fname = FieldData.getOutfileName(yr, qtr, cfd.getTicker(), "txt");

      final FieldData fd = new FieldData(cfd, efd, sfd, ifd, bfd, cash, yr, qtr);

      final String rpt = fd.genOutput();

      if (rpt != null && rpt.length() > 0) {
        try (PrintWriter pw = new PrintWriter(fname)) {
          pw.println(rpt);
        }
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getError(e));
    }
  }

  private BalSheetFileData balSheetData;
  private CashFileData     cashData;
  private CompanyFileData  companyInfo;
  private EstimateFileData estimateData;
  private ExchEnum         exchange;
  private IncSheetFileData incSheetData;
  private String           industry;
  private String           name;
  private int              quarter;
  private String           sector;
  private SharesFileData   shareData;
  private String           ticker;
  private int              year;

  /**
   * Constructor
   *
   * @param cfd  CompanyFileData
   * @param efd  EstimateFileData
   * @param sfd  SharesFileData
   * @param ifd  IncSheetFileData
   * @param bfd  BalSheetFileData
   * @param cash CashFileData
   * @param yr   year
   * @param qtr  quarter
   */
  public FieldData(CompanyFileData cfd, EstimateFileData efd, SharesFileData sfd, IncSheetFileData ifd, BalSheetFileData bfd, CashFileData cash,
      int yr, int qtr) {
    try {
      this.companyInfo = cfd;
      this.cashData = cash;
      this.estimateData = efd;
      this.shareData = sfd;
      this.incSheetData = ifd;
      this.balSheetData = bfd;
      if (cfd != null) {
        this.ticker = cfd.getTicker();
        this.name = cfd.getName();
        this.exchange = cfd.getExchange();
        this.sector = cfd.getSector();
        this.industry = cfd.getIndustry();
      }
      else {
        this.ticker = "";
        this.name = "";
        this.exchange = ExchEnum.NONE;
        this.sector = "";
        this.industry = "";
      }
      this.year = yr;
      this.quarter = qtr;
    }
    catch (final Exception e) {
      System.out.println(FieldData.getConstructorError(e));
      this.ticker = "";
    }
  }

  /**
   * Constructor
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   */
  public FieldData(int yr, int qtr) {
    try {
      this.year = yr;
      this.quarter = qtr;
      this.ticker = "";
      this.name = "";
      this.sector = "";
      this.industry = "";
      this.cashData = new CashFileData();
      this.companyInfo = new CompanyFileData();
      this.estimateData = new EstimateFileData();
      this.shareData = new SharesFileData();
      this.incSheetData = new IncSheetFileData();
      this.balSheetData = new BalSheetFileData();
    }
    catch (final Exception e) {
      System.out.println(FieldData.getConstructorError(e));
      this.ticker = "";
    }
  }

  /**
   * Creates String containing data to write to DB files.
   *
   * @return String
   */
  public String genOutput() {
    String ret = "";
    try {
      ret = String.format("Data for %s from %d Q%d%n", this.companyInfo.getTicker(), this.year, this.quarter);
      ret += this.companyInfo.toDbOuput();
      ret += this.shareData.toDbOutput();
      ret += this.estimateData.toDbOutput();
      ret += this.incSheetData.toDbOutput();
      ret += this.balSheetData.toDbOutput();
      ret += this.cashData.toDbOutput();
    }
    catch (final Exception e) {
      System.out.println(FieldData.getError(e));
      ret = "";
    }
    return ret;

  }

  public BalSheetFileData getBalSheetData() {
    return this.balSheetData;
  }

  public CashFileData getCashData() {
    return this.cashData;
  }

  public CompanyFileData getCompanyInfo() {
    return this.companyInfo;
  }

  public EstimateFileData getEstimateData() {
    return this.estimateData;
  }

  public ExchEnum getExchange() {
    return this.exchange;
  }

  public IncSheetFileData getIncSheetData() {
    return this.incSheetData;
  }

  public String getIndustry() {
    return this.industry;
  }

  public String getName() {
    return this.name;
  }

  public int getQuarter() {
    return this.quarter;
  }

  public String getSector() {
    return this.sector;
  }

  public SharesFileData getShareData() {
    return this.shareData;
  }

  public String getTicker() {
    return this.ticker;
  }

  public void overrideTicker(String t) {
    this.ticker = t;
  }

  public int getYear() {
    return this.year;
  }

  /**
   * Any valid ticker String created by a Constructor will have a length of 1 or
   * more
   *
   * @return TRUE if any ticker String length greater than 0, FALSE otherwise
   */
  public boolean isDataValid() {
    return this.ticker.trim().length() > 0;
  }

  public void setQuarter(int quarter) {
    this.quarter = quarter;
  }

  public void setYear(int year) {
    this.year = year;
  }

  @Override
  public String toString() {
    String ret = "";
    try {
      if (this.companyInfo.getTicker() == null) {
        ret = "";
      }
      else {
        ret = String.format("%d Q%d%n", this.year, this.quarter);
        ret += this.companyInfo.toDbOuput();
        ret += this.estimateData.toDbOutput();
        ret += this.shareData.toDbOutput();
        ret += this.incSheetData.toDbOutput();
        ret += this.balSheetData.toDbOutput();
        ret += this.cashData.toDbOutput();
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      ret = "";
    }
    return ret;
  }

  /**
   * Sets local "name" fields from CompanyFileData
   *
   * @param cfd CompanyFileData
   */
  private void setNameFields(CompanyFileData cfd) {
    try {
      this.ticker = cfd.getTicker();
      this.name = cfd.getName();
      this.sector = cfd.getSector();
      this.industry = cfd.getIndustry();
      this.exchange = cfd.getExchange();
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      this.ticker = "";
    }
  }

}
