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

import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.Globals;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldDataYear;

public class OneCompanyData {

  /**
   * Returns all requested data for ticker supplied
   *
   * @param ticker The individual stock symbol
   * @return One instance in list for each year of data
   */
  public static List<OneCompanyData> getCompany(String ticker) {

    final List<OneCompanyData> ret = new ArrayList<>();

    for (final FieldDataYear fdy : Globals.allDataList) {
      if (fdy.isInUse()) {
        final OneCompanyData icd = new OneCompanyData(ticker, fdy);
        ret.add(icd);
      }
    }
    return ret;
  }

  /**
   * Returns FieldData for requested parameters
   *
   * @param fdList List of FieldData
   * @param yr     year
   * @param qtr    quarter
   * @return FieldData
   */
  public static FieldData getFieldData(List<FieldData> fdList, int yr, int qtr) {
    for (final FieldData fd : fdList) {
      if (fd.getYear() == yr && fd.getQuarter() == qtr) {
        return fd;
      }
    }
    return null;
  }

  /**
   * Returns quarter
   *
   * @param name The name of the individual stock symbol file
   * @return Numeric value of quarter
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
   * Returns year
   *
   * @param name The name of the individual stock symbol file
   * @return Numeric value of year
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

  private FieldData    q1;
  private FieldData    q2;
  private FieldData    q3;
  private FieldData    q4;
  private final String ticker;
  private final int    year;

  /**
   * Constructor is private for use by internal procedures
   *
   * @param t   The individual stock symbol
   * @param fdy Year of FieldData
   */
  private OneCompanyData(String t, FieldDataYear fdy) {

    this.q1 = null;
    this.q2 = null;
    this.q3 = null;
    this.q4 = null;

    this.year = fdy.getYear();
    this.ticker = t;
    if (fdy.quarterDataAvailable(1)) {
      for (final FieldData fd : fdy.getQ(1).fieldDataList) {
        if (fd.getTicker().equalsIgnoreCase(t)) {
          this.q1 = fd;
          break;
        }
      }
    }
    if (fdy.quarterDataAvailable(2)) {
      for (final FieldData fd : fdy.getQ(2).fieldDataList) {
        if (fd.getTicker().equalsIgnoreCase(t)) {
          this.q2 = fd;
          break;
        }
      }
    }
    if (fdy.quarterDataAvailable(3)) {
      for (final FieldData fd : fdy.getQ(3).fieldDataList) {
        if (fd.getTicker().equalsIgnoreCase(t)) {
          this.q3 = fd;
          break;
        }
      }
    }
    if (fdy.quarterDataAvailable(4)) {
      for (final FieldData fd : fdy.getQ(4).fieldDataList) {
        if (fd.getTicker().equalsIgnoreCase(t)) {
          this.q4 = fd;
          break;
        }
      }
    }

  }

  /**
   * getter allowing for external loops to retrieve quarter data
   *
   * @param qtr quarter
   * @return corresponding quarter data
   */
  public FieldData getQ(int qtr) {
    FieldData ret = null;
    if (qtr == 1) {
      ret = this.q1;
    }
    else if (qtr == 2) {
      ret = this.q2;
    }
    else if (qtr == 3) {
      ret = this.q3;
    }
    else if (qtr == 4) {
      ret = this.q4;
    }

    return ret;
  }

  public FieldData getQ1() {
    return this.q1;
  }

  public FieldData getQ2() {
    return this.q2;
  }

  public FieldData getQ3() {
    return this.q3;
  }

  public FieldData getQ4() {
    return this.q4;
  }

  public String getTicker() {
    return this.ticker;
  }

  public int getYear() {
    return this.year;
  }

  @Override
  public String toString() {
    String ret = String.format("%s %s%n", this.ticker, this.year);
    if (this.q1 != null) {
      ret += String.format(" %d Q1  : %s%n", this.year, this.q1.getCompanyInfo().toString());
    }
    if (this.q2 != null) {
      ret += String.format(" %d Q2  : %s%n", this.year, this.q2.getCompanyInfo().toString());
    }
    if (this.q3 != null) {
      ret += String.format(" %d Q3  : %s%n", this.year, this.q3.getCompanyInfo().toString());
    }
    if (this.q4 != null) {
      ret += String.format(" %d Q4  : %s%n", this.year, this.q4.getCompanyInfo().toString());
    }
    return ret;
  }

}
