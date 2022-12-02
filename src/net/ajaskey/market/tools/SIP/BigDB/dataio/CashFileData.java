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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.SipOutput;
import net.ajaskey.market.tools.SIP.SipUtils;

public class CashFileData implements Serializable {

  /**
   * Stores all EstimateFileDate read in from DB.
   */
  private static List<CashFileData> cashfdList       = new ArrayList<>();
  /**
   *
   */
  private static final long         serialVersionUID = 1L;

  public static void clearList() {
    CashFileData.cashfdList.clear();
  }

  /**
   * Returns the EstimateFileData instance for requested ticker.
   *
   * @param ticker The individual stock symbol
   * @return CahsFileData
   */
  public static CashFileData find(String ticker) {
    if (ticker != null) {
      if (ticker.trim().length() > 0) {
        for (final CashFileData c : CashFileData.cashfdList) {
          if (c.getTicker().equalsIgnoreCase(ticker)) {
            return c;
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns internal list of CashFileData
   *
   * @return CashFileData
   */
  public static List<CashFileData> getCashfdList() {
    return CashFileData.cashfdList;
  }

  /**
   * Returns the number of instances in the list read from the DB.
   *
   * @return Number in list
   */
  public static int getListCount() {
    return CashFileData.cashfdList.size();
  }

  public static List<String> getTickers() {
    final List<String> tickers = new ArrayList<>();
    for (final CashFileData cfd : CashFileData.cashfdList) {
      tickers.add(cfd.ticker.trim().toUpperCase());
    }
    return tickers;
  }

  /**
   * Returns a string containing text for all data in the list read from the DB.
   *
   * @return String
   */
  public static String listToString() {
    String ret = "";
    for (final CashFileData c : CashFileData.cashfdList) {
      ret += c.toString();
    }
    return ret;
  }

  /**
   * Parses data and fills data structures from DB files.
   *
   * @param data List of strings to parse
   * @return CashFileData
   */
  public static CashFileData readFromDb(List<String> data) {

    final CashFileData cfd = new CashFileData();

    for (final String s : data) {

      final String[] tfld = s.split(":");

      final String fld = tfld[0].trim();

      String val = "";
      if (tfld.length > 1) {
        val = tfld[1].trim();
      }

      if (fld.equals("ticker")) {
        cfd.ticker = val;
      }
      else if (fld.equals("capex")) {
        final double[] dArr = SipUtils.parseArrayDoubles(tfld[1], 1);
        cfd.capExQtr = dArr;
      }
      else if (fld.equals("cash from financing")) {
        final double[] dArr = SipUtils.parseArrayDoubles(tfld[1], 1);
        cfd.cashFromFinQtr = dArr;
      }
      else if (fld.equals("cash from investing")) {
        final double[] dArr = SipUtils.parseArrayDoubles(tfld[1], 1);
        cfd.cashFromInvQtr = dArr;
      }
      else if (fld.equals("cash from operations")) {
        final double[] dArr = SipUtils.parseArrayDoubles(tfld[1], 1);
        cfd.cashFromOpsQtr = dArr;
      }
    }
    return cfd;

  }

  /**
   * Reads the data from SIP tab delimited files and fills data structures.
   *
   * @param filename SIP data file name
   *
   */
  public static void readSipData(String filename) {

    final List<String> cashData = TextUtils.readTextFile(filename, true);
    for (final String s : cashData) {
      final String[] fld = s.replace("\"", "").split(Utils.TAB);
      final CashFileData cashfd = new CashFileData(fld);
      CashFileData.cashfdList.add(cashfd);

      /**
       * These important tickers are not in SIP data. Use a copy to add to this DB.
       * 
       * GOOGL exists but GOOG does not.
       * 
       * BRK.A exists but BRK.B does not.
       * 
       */
      if (cashfd.ticker.equals("GOOGL")) {
        CashFileData goog = new CashFileData(cashfd);
        goog.ticker = "GOOG";
        CashFileData.cashfdList.add(goog);
      }
      else if (cashfd.ticker.equals("BRK.A")) {
        CashFileData brkb = new CashFileData(cashfd);
        brkb.ticker = "BRK.B";
        CashFileData.cashfdList.add(brkb);
      }

    }

  }

  private double[] capExQtr;
  private double[] cashFromFinQtr;
  private double[] cashFromInvQtr;
  private double[] cashFromOpsQtr;

  private String ticker;

  /**
   * Copy Constructor
   *
   * @param cfd CashFileData to copy
   */
  public CashFileData(CashFileData cfd) {
    if (cfd != null) {
      this.capExQtr = cfd.capExQtr;
      this.cashFromOpsQtr = cfd.cashFromOpsQtr;
      this.cashFromInvQtr = cfd.cashFromInvQtr;
      this.cashFromFinQtr = cfd.cashFromFinQtr;
      this.ticker = cfd.ticker;
    }
    else {
      this.ticker = "";
    }
  }

  /**
   * Constructor - package level.
   */
  CashFileData() {
    this.ticker = "";
  }

  /**
   * Constructor fills data structures.
   *
   * @param fld String data to parse
   *
   */
  CashFileData(String[] fld) {

    this.ticker = fld[0].trim();
    int ptr = 1;
    this.capExQtr = SipUtils.parseDoubles(fld, ptr, 8);
    ptr += 8;
    this.cashFromFinQtr = SipUtils.parseDoubles(fld, ptr, 8);
    ptr += 8;
    this.cashFromInvQtr = SipUtils.parseDoubles(fld, ptr, 8);
    ptr += 8;
    this.cashFromOpsQtr = SipUtils.parseDoubles(fld, ptr, 8);

  }

  public double[] getCapExQtr() {
    return this.capExQtr;
  }

  public double[] getCashFromFinQtr() {
    return this.cashFromFinQtr;
  }

  public double[] getCashFromInvQtr() {
    return this.cashFromInvQtr;
  }

  public double[] getCashFromOpsQtr() {
    return this.cashFromOpsQtr;
  }

  public String getTicker() {
    return this.ticker;
  }

  /**
   * Returns string of output to write to DB file
   *
   * @return String
   */
  public String toDbOutput() {
    String ret = "";
    try {
      ret += String.format("  capex                 : %s%n", SipOutput.buildArray("", this.capExQtr, 10, 3, 1));
      ret += String.format("  cash from financing   : %s%n", SipOutput.buildArray("", this.cashFromFinQtr, 10, 3, 1));
      ret += String.format("  cash from investing   : %s%n", SipOutput.buildArray("", this.cashFromInvQtr, 10, 3, 1));
      ret += String.format("  cash from operations  : %s%n", SipOutput.buildArray("", this.cashFromOpsQtr, 10, 3, 1));
    }
    catch (final Exception e) {
      ret = "";
    }
    return ret;
  }

  @Override
  public String toString() {
    String ret = "Cash File Data";
    ret += this.toDbOutput();
    return ret;
  }
}
