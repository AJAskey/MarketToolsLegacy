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

import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class QuarterlyDouble {

  public double[] dArr = null;

  /**
   * Constructor
   *
   * @param inArr Quarterly data
   */
  public QuarterlyDouble(double[] inArr) {
    if (inArr == null) {
      this.dArr = new double[10];
      for (double d : this.dArr) {
        d = 0.0;
      }
    }
    else {
      this.dArr = inArr;
    }
  }

  /**
   * Hidden Constructor
   */
  @SuppressWarnings("unused")
  private QuarterlyDouble() {
  }

  public int getQuarterDataKnt() {
    int ret = 0;

    int len = this.dArr.length - 1;
    for (int i = len; i > 0; i--) {
      if (this.dArr[i] != 0.0) {
        ret = i;
        break;
      }
    }

    return ret;
  }

  /**
   * Returns the difference between two quarters of data
   *
   * @param q1 Earlier quarter
   * @param q2 Later quarter
   * @return Zero if bad inputs or the delta
   */
  public double deltaQ(int q1, int q2) {

    final double ret = 0.0;
    if (q1 >= q2) {
      return ret;
    }
    if (q1 > 3 || q2 < 2) {
      return ret;
    }

    return this.dArr[q2] - this.dArr[q1];

  }

  /**
   * Formats latest quarter of data into predetermined string.
   *
   * @param desc Text to use as prefix
   * @return String
   */
  public String fmtGrowth1Q(final String desc) {

    final String ret = String.format("\t%-18s: %s M (Seq= %s%% : QoQ= %s%%)", desc, Utils.fmt(this.dArr[1], 13), Utils.fmt(this.getQseqQ(), 7),
        Utils.fmt(this.getQoQ(), 7));

    return ret;
  }

  public String fmtGrowth1QEps(final String desc) {

    final String ret = String.format("\t%-18s: %s   (Seq= %s%% : QoQ= %s%%)", desc, Utils.fmt(this.dArr[1], 13), Utils.fmt(this.getQseqQ(), 7),
        Utils.fmt(this.getQoQ(), 7));

    return ret;
  }

  /**
   * Formats latest quarter of data into predetermined string.
   *
   * @param desc Text to use as prefix
   * @return String
   */
  public String fmtGrowth1QNoUnit(final String desc) {

    final String ret = String.format("\t%-18s: %s   (Seq= %s%% : QoQ= %s%%)", desc, Utils.fmt(this.dArr[1], 13), Utils.fmt(this.getQseqQ(), 7),
        Utils.fmt(this.getQoQ(), 7));

    return ret;
  }

  /**
   * Formats trailing 12 months of data into predetermined string.
   *
   * @param desc Text to use as prefix
   * @return String
   */
  public String fmtGrowth4Q(final String desc) {

    final String ret = String.format("\t%-18s: %s M (Seq= %s%% : QoQ= %s%%)", desc, Utils.fmt(this.getTtm(), 13), Utils.fmt(this.getQseqQ(), 7),
        Utils.fmt(this.getQoQ(), 7));

    return ret;
  }

  /**
   * Returns requested quarter of data
   *
   * @param i quarter to return
   * @return double
   */
  public double get(int i) {
    double ret = 0.0;
    try {
      ret = this.dArr[i];
    }
    catch (final Exception e) {
      ret = -9999.9999;
      System.out.println(FieldData.getWarning(e));
    }
    return ret;
  }

  /**
   * Returns previous year trailing 12 months started at Q2 (used for sequential
   * comparisons)
   *
   * @return q2 + q3 + q4 + q5
   */
  public double get2QTtm() {

    double ret = 0.0;
    try {
      if (this.dArr.length > 5) {
        ret = this.dArr[2] + this.dArr[3] + this.dArr[4] + this.dArr[5];
      }
    }
    catch (final Exception e) {
      ret = 0.0;
      System.out.println(FieldData.getWarning(e));
    }
    return ret;
  }

  public double getMostRecent() {
    return this.dArr[1];
  }

  /**
   * Returns previous year trailing 12 months
   *
   * @return q5 + q6 + q7 + q8
   */
  public double getPrevTtm() {

    double ret = 0.0;
    try {
      if (this.dArr.length > 8) {
        ret = this.dArr[5] + this.dArr[6] + this.dArr[7] + this.dArr[8];
      }
    }
    catch (final Exception e) {
      ret = 0.0;
      System.out.println(FieldData.getWarning(e));
    }
    return ret;
  }

  /**
   * Returns percent change of q4 vs q1
   *
   * @return Percent change
   */
  public double getQoQ() {

    double ret = 0.0;

    try {
      if (this.dArr[5] != 0.0) {
        ret = (this.dArr[1] - this.dArr[5]) / Math.abs(this.dArr[5]) * 100.0;
        if (ret < -9999.99) {
          ret = -9999.99;
        }
        else if (ret > 9999.99) {
          ret = 9999.99;
        }
      }
    }
    catch (final Exception e) {
      ret = 0.0;
      System.out.println(FieldData.getWarning(e));
    }
    return ret;
  }

  /**
   * Returns percent change of q2 vs q1
   *
   * @return double
   */
  public double getQseqQ() {

    double ret = 0.0;
    if (this.dArr[2] != 0.0) {
      ret = (this.dArr[1] - this.dArr[2]) / Math.abs(this.dArr[2]) * 100.0;
    }
    return ret;
  }

  /**
   * Returns trailing 12 months
   *
   * @return q1 + q2 + q3 + q4
   */
  public double getTtm() {

    double ret = 0.0;
    try {
      if (this.dArr.length > 3) {
        ret = this.dArr[1] + this.dArr[2] + this.dArr[3] + this.dArr[4];
      }
    }
    catch (final Exception e) {
      ret = 0.0;
    }
    return ret;
  }

  /**
   * Returns the average of the trailing 12 months
   *
   * @return (q1 + q2 + q3 + q4) / 4
   */
  public double getTtmAvg() {
    double d = 0.0;
    try {
      d = this.getTtm();
    }
    catch (final Exception e) {
    }
    return d / 4.0;
  }

  /**
   * Returns percent change of y2 vs y1
   *
   * @return Percent change
   */
  public double getYoY() {

    double ret = 0.0;

    try {
      final double y1 = this.dArr[1] + this.dArr[2] + this.dArr[3] + this.dArr[4];
      final double y2 = this.dArr[5] + this.dArr[6] + this.dArr[7] + this.dArr[8];
      if (y2 != 0.0) {
        ret = (y1 - y2) / Math.abs(y2) * 100.0;
      }
    }
    catch (final Exception e) {
      ret = 0.0;
      System.out.println(FieldData.getWarning(e));
    }
    return ret;
  }

  @Override
  public String toString() {
    String ret = "";
    for (int i = 0; i < this.dArr.length; i++) {
      ret += String.format("\t i=%d %.4f", i, this.dArr[i]);
    }
    return ret;
  }

}
