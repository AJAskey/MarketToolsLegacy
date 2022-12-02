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

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.SipOutput;
import net.ajaskey.market.tools.SIP.SipUtils;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;

public class EstimateFileData implements Serializable {

  /**
   * Stores all EstimateFileDate read in from DB.
   */
  private static List<EstimateFileData> efdList = new ArrayList<>();

  public static void clearList() {
    EstimateFileData.efdList.clear();
  }

  /**
   * Returns the EstimateFileData instance for requested ticker.
   *
   * @param ticker The individual stock symbol
   * @return EstimateFileData
   */
  public static EstimateFileData find(String ticker) {
    if (ticker != null) {
      if (ticker.trim().length() > 0) {
        for (final EstimateFileData e : EstimateFileData.efdList) {
          if (e.getTicker().equalsIgnoreCase(ticker)) {
            return e;
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns the number of instances in the list read from the DB.
   *
   * @return Number of entries in internal memory
   */
  public static int getListCount() {
    return EstimateFileData.efdList.size();
  }

  /**
   *
   * @return
   */
  public static List<String> getTickers() {
    final List<String> tickers = new ArrayList<>();
    for (final EstimateFileData efd : EstimateFileData.efdList) {
      tickers.add(efd.ticker.trim().toUpperCase());
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
    for (final EstimateFileData e : EstimateFileData.efdList) {
      ret += e.toString();
    }
    return ret;
  }

  /**
   * Parses data and fills data structures from DB files.
   *
   * @param data List of strings to parse
   * @return EstimateFileData
   */
  public static EstimateFileData readFromDb(List<String> data) {

    final EstimateFileData efd = new EstimateFileData();

    for (final String s : data) {

      final String[] tfld = s.split(":");

      final String fld = tfld[0].trim();

      String val = "";
      if (tfld.length > 1) {
        val = tfld[1].trim();
      }

      if (fld.equals("eps Q0")) {
        efd.epsQ0 = SipUtils.parseDouble(val);
      }
      else if (fld.equals("eqp Q1")) {
        efd.epsQ1 = SipUtils.parseDouble(val);
      }
      else if (fld.equals("eps Y0")) {
        efd.epsY0 = SipUtils.parseDouble(val);
      }
      else if (fld.equals("eps Y1")) {
        efd.epsY1 = SipUtils.parseDouble(val);
      }
      else if (fld.equals("eps Y2")) {
        efd.epsY2 = SipUtils.parseDouble(val);
      }
      else if (fld.equals("current fiscal year")) {
        efd.currFiscalYear = new DateTime(val, "yyyy-MM-dd");
      }
      else if (fld.equals("latest quarter eps")) {
        efd.latestQtrEps = new DateTime(val, "yyyy-MM-dd");
      }
    }
    return efd;

  }

  /**
   * Reads the data from SIP tab delimited files and fills data structures.
   *
   * @param filename Name of SIP file to parse
   */
  public static void readSipData(String filename) {

    final List<String> estData = TextUtils.readTextFile(filename, true);
    for (final String s : estData) {
      final String[] fld = s.replace("\"", "").split(Utils.TAB);
      final EstimateFileData efd = new EstimateFileData(fld);
      EstimateFileData.efdList.add(efd);

      /**
       * These important tickers are not in SIP data. Use a copy to add to this DB.
       * 
       * GOOGL exists but GOOG does not.
       * 
       * BRK.A exists but BRK.B does not.
       * 
       */
      if (efd.ticker.equals("GOOGL")) {
        EstimateFileData goog = new EstimateFileData(efd);
        goog.ticker = "GOOG";
        EstimateFileData.efdList.add(goog);
      }
      else if (efd.ticker.equals("BRK.A")) {
        EstimateFileData brkb = new EstimateFileData(efd);
        brkb.ticker = "BRK.B";
        EstimateFileData.efdList.add(brkb);
      }
    }
  }

  private DateTime currFiscalYear;
  private double   epsQ0;
  private double   epsQ1;
  private double   epsY0;
  private double   epsY1;
  private double   epsY2;
  private ExchEnum exchange;
  private String   industry;
  private DateTime latestQtrEps;
  private String   name;

  private String sector;

  private String ticker;

  public EstimateFileData(EstimateFileData efd) {
    if (efd != null) {
      this.currFiscalYear = efd.currFiscalYear;
      this.epsQ0 = efd.epsQ0;
      this.epsQ1 = efd.epsQ1;
      this.epsY0 = efd.epsY0;
      this.epsY1 = efd.epsY1;
      this.epsY2 = efd.epsY2;
      this.exchange = efd.exchange;
      this.industry = efd.industry;
      this.latestQtrEps = efd.latestQtrEps;
      this.name = efd.name;
      this.sector = efd.sector;
      this.ticker = efd.ticker;
    }
  }

  /**
   * Constructor - package level.
   */
  EstimateFileData() {
  }

  /**
   * Constructor fills data structures.
   *
   * @param fld List of strings to parse
   */
  EstimateFileData(String[] fld) {

    this.name = fld[0].trim();
    this.ticker = fld[1].trim();
    this.exchange = FieldData.convertExchange(fld[2].trim());
    this.sector = fld[3].trim();
    this.industry = fld[4].trim();
    this.currFiscalYear = new DateTime(fld[5].trim(), "MM/dd/yyyy");
    this.latestQtrEps = new DateTime(fld[6].trim(), "MM/dd/yyyy");
    this.epsQ0 = SipUtils.parseDouble(fld[7]);
    this.epsQ1 = SipUtils.parseDouble(fld[16]);
    this.epsY0 = SipUtils.parseDouble(fld[25]);
    this.epsY1 = SipUtils.parseDouble(fld[34]);
    this.epsY2 = SipUtils.parseDouble(fld[43]);
  }

  public DateTime getCurrFiscalYear() {
    return this.currFiscalYear;
  }

  public double getEpsQ0() {
    return this.epsQ0;
  }

  public double getEpsQ1() {
    return this.epsQ1;
  }

  public double getEpsY0() {
    return this.epsY0;
  }

  public double getEpsY1() {
    return this.epsY1;
  }

  public double getEpsY2() {
    return this.epsY2;
  }

  public ExchEnum getExchange() {
    return this.exchange;
  }

  public String getIndustry() {
    return this.industry;
  }

  public DateTime getLatestQtrEps() {
    return this.latestQtrEps;
  }

  public String getName() {
    return this.name;
  }

  public String getSector() {
    return this.sector;
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
      String tmp = "";
      if (!this.currFiscalYear.isNull()) {
        tmp = this.currFiscalYear.format("yyyy-MM-dd");
      }
      ret += String.format("  current fiscal year : %s%n", tmp);

      tmp = "";
      if (!this.latestQtrEps.isNull()) {
        tmp = this.latestQtrEps.format("yyyy-MM-dd");
      }
      ret += String.format("  latest quarter eps  : %s%n", tmp);

      ret += String.format("  eps Q0              : %f%n", this.epsQ0);
      ret += String.format("  eps Q1              : %f%n", this.epsQ1);
      ret += String.format("  eps Y0              : %f%n", this.epsY0);
      ret += String.format("  eps Y1              : %f%n", this.epsY1);
      ret += String.format("  eps Y2              : %f%n", this.epsY2);
    }
    catch (final Exception e) {
      ret = "";
    }
    return ret;
  }

  @Override
  public String toString() {
    String ret = "";
    try {
      ret = SipOutput.SipHeader(this.ticker, this.name, FieldData.getExchangeStr(this.exchange), this.sector, this.industry);
      ret += String.format("  %s  %s%n", this.currFiscalYear, this.latestQtrEps);
      ret += String.format("  Est Q0 Q1    : %10.3f %10.3f%n", this.epsQ0, this.epsQ1);
      ret += String.format("  Est Y0 Y1 Y2 : %10.3f %10.2f %10.3f", this.epsY0, this.epsY1, this.epsY2);
    }
    catch (final Exception e) {
      ret = "";
    }
    return ret;
  }

  /**
   * Sets name fields
   *
   * @param cfd CompanyFileData instance
   */
  void setNameFields(CompanyFileData cfd) {
    if (cfd != null) {
      this.ticker = cfd.getTicker();
      this.name = cfd.getName();
      this.sector = cfd.getSector();
      this.industry = cfd.getIndustry();
      this.exchange = cfd.getExchange();
    }
  }
}
