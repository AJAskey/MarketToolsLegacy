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
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public class QuarterlyData {

  public static Map<String, Integer>  colPos               = new HashMap<>();
  private static DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
  private static DecimalFormat        df;

  private static DecimalFormat dfmt = new DecimalFormat("#,###");

  final private static String NL  = "\n";
  final private static String TAB = "\t";

  final private static String TAB2 = "\t\t";

  /**
   *
   * net.ajaskey.market.tools.SIP.fmt
   *
   * @param d
   * @return
   */
  public static String fmt(final double d) {

    return QuarterlyData.fmt(d, 15);
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.fmt
   *
   * @param d
   * @param len
   * @return
   */
  public static String fmt(final double d, final int len) {

    final String sfmt = String.format("%%%ds", len);
    return String.format(sfmt, QuarterlyData.df.format(d));
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.ifmt
   *
   * @param i
   * @param len
   * @return
   */
  public static String ifmt(final int i, final int len) {

    final String s = QuarterlyData.dfmt.format(i);
    final String sfmt = String.format("%%%ds", len);

    return String.format(sfmt, s);
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.init
   *
   */
  public static void init() {

    QuarterlyData.decimalFormatSymbols.setDecimalSeparator('.');
    QuarterlyData.decimalFormatSymbols.setGroupingSeparator(',');
    QuarterlyData.df = new DecimalFormat("#,###,##0.00", QuarterlyData.decimalFormatSymbols);

    int pos = 5;
    QuarterlyData.colPos.put("cash", pos);
    pos += 8;
    QuarterlyData.colPos.put("stInvestments", pos);
    pos += 8;
    QuarterlyData.colPos.put("acctReceiveable", pos);
    pos += 8;
    QuarterlyData.colPos.put("inventory", pos);
    pos += 8;
    QuarterlyData.colPos.put("otherAssets", pos);
    pos += 8;
    QuarterlyData.colPos.put("currentAssets", pos);
    pos += 8;
    QuarterlyData.colPos.put("fixedAssets", pos);
    pos += 8;
    QuarterlyData.colPos.put("ltInvestments", pos);
    pos += 8;
    QuarterlyData.colPos.put("goodwill", pos);
    pos += 8;
    QuarterlyData.colPos.put("otherLtAssets", pos);
    pos += 8;
    QuarterlyData.colPos.put("totalAssets", pos);
    pos += 8;
    QuarterlyData.colPos.put("acctPayable", pos);
    pos += 8;
    QuarterlyData.colPos.put("stDebt", pos);
    pos += 8;
    QuarterlyData.colPos.put("otherCurrLiab", pos);
    pos += 8;
    QuarterlyData.colPos.put("currLiab", pos);
    pos += 8;
    QuarterlyData.colPos.put("ltDebt", pos);
    pos += 8;
    QuarterlyData.colPos.put("otherLtLiab", pos);
    pos += 8;
    QuarterlyData.colPos.put("totalLiab", pos);
    pos += 8;
    QuarterlyData.colPos.put("prefStock", pos);
    pos += 8;
    QuarterlyData.colPos.put("equity", pos);
    pos += 8;
    QuarterlyData.colPos.put("liabEquity", pos);
    pos += 8;
    QuarterlyData.colPos.put("bvps", pos);

    pos = 5;
    QuarterlyData.colPos.put("sales", pos);
    pos += 8;
    QuarterlyData.colPos.put("cogs", pos);
    pos += 8;
    QuarterlyData.colPos.put("grossIncome", pos);
    pos += 8;
    QuarterlyData.colPos.put("rd", pos);
    pos += 8;
    QuarterlyData.colPos.put("depreciation", pos);
    pos += 8;
    QuarterlyData.colPos.put("interestExp", pos);
    pos += 8;
    QuarterlyData.colPos.put("unusualIncome", pos);
    pos += 8;
    QuarterlyData.colPos.put("totalOpExp", pos);
    pos += 8;
    QuarterlyData.colPos.put("grossOpIncome", pos);
    pos += 8;
    QuarterlyData.colPos.put("interestExpNonOp", pos);
    pos += 8;
    QuarterlyData.colPos.put("otherIncome", pos);
    pos += 8;
    QuarterlyData.colPos.put("pretaxIncome", pos);
    pos += 8;
    QuarterlyData.colPos.put("incomeTax", pos);
    pos += 8;
    QuarterlyData.colPos.put("incomeAfterTaxes", pos);
    pos += 8;
    QuarterlyData.colPos.put("adjustments", pos);
    pos += 8;
    QuarterlyData.colPos.put("incomeEps", pos);
    pos += 8;
    QuarterlyData.colPos.put("nonrecurring", pos);
    pos += 8;
    QuarterlyData.colPos.put("netIncome", pos);
    pos += 8;
    QuarterlyData.colPos.put("eps", pos);
    pos += 8;
    QuarterlyData.colPos.put("epsContinuing", pos);
    pos += 8;
    QuarterlyData.colPos.put("epsDiluted", pos);
    pos += 8;
    QuarterlyData.colPos.put("epsDilCont", pos);
    pos += 8;
    QuarterlyData.colPos.put("dividend", pos);

    pos = 1;
    QuarterlyData.colPos.put("capEx", pos);
    pos += 8;
    QuarterlyData.colPos.put("cashFromFin", pos);
    pos += 8;
    QuarterlyData.colPos.put("cashFromInv", pos);
    pos += 8;
    QuarterlyData.colPos.put("cashFromOps", pos);

    QuarterlyData.colPos.put("shares", 20);

    QuarterlyData.colPos.put("totalInterest", 0);
    QuarterlyData.colPos.put("assetsMinusGW", 0);
    QuarterlyData.colPos.put("cashNet", 0);
  }

  /**
   * net.ajaskey.market.tools.SIP.main
   *
   * @param args
   * @throws IOException
   */
  public static void main(final String[] args) throws IOException {

    QuarterlyData.init();

    try (BufferedReader reader = new BufferedReader(new FileReader("data/SP500-INCOMESTMTQTR.TXT"))) {

      String line = reader.readLine(); // header line

      while ((line = reader.readLine()) != null) {
        final String str = line.trim().replaceAll("\"", "").replaceAll("[MN] - ", "");
        if (str.length() > 1) {
          // System.out.println(str);
          final String fld[] = str.split(QuarterlyData.TAB);
          final QuarterlyData qd = new QuarterlyData("sales");
          qd.parse(fld);

          System.out.println(qd);
        }

      }
    }

    final QuarterlyData qd = new QuarterlyData("sales");
    System.out.println(qd);

  }

  /**
   * net.ajaskey.market.tools.SIP.parseDouble
   *
   * @param string
   * @return
   */
  public static double parseDouble(final String fld) {

    try {
      double d = Double.parseDouble(fld);
      if (d > 0.0 && d < 0.0001) {
        d = 0.0;
      }
      else if (d < -999999.0) {
        d = 0.0;
      }
      return d;
    }
    catch (final Exception e) {
    }
    return 0;
  }

  public DerivedData dd;

  public int pos;

  public double q1;

  public double q2;

  public double q3;

  public double q4;

  public double q5;

  public double q6;

  public double q7;

  public double q8;

  public String type;

  private double ttm;

  /**
   * This method serves as a constructor for the class.
   *
   * @param string
   */
  public QuarterlyData(final String t) {

    this.type = t;
    this.pos = QuarterlyData.colPos.get(t);
    this.initData();
    this.dd = new DerivedData();
  }

  public String fmtGrowth1Q(final String desc) {

    final String ret = String.format("\t%-18s: %s M (Seq= %s%% : QoQ= %s%%)", desc, QuarterlyData.fmt(this.getMostRecent(), 13),
        QuarterlyData.fmt(this.dd.seqGrowth, 7), QuarterlyData.fmt(this.dd.qoqGrowth, 7));

    return ret;
  }

  public String fmtGrowth4Q(final String desc) {

    final String ret = String.format("\t%-18s: %s M (Seq= %s%% : QoQ= %s%%)", desc, QuarterlyData.fmt(this.getTtm(), 13),
        QuarterlyData.fmt(this.dd.seqGrowth, 7), QuarterlyData.fmt(this.dd.qoqGrowth, 7));

    return ret;
  }

  /**
   * net.ajaskey.market.tools.SIP.fmtGrowthQY
   *
   * @param string
   * @return
   */
  public String fmtGrowthQY(final String desc) {

    final String ret = String.format("\t%-18s: %s M (QoQ= %s%% : YoY= %s%%)", desc, QuarterlyData.fmt(this.getTtm(), 13),
        QuarterlyData.fmt(this.dd.qoqGrowth, 7), QuarterlyData.fmt(this.dd.yoyGrowth, 7));

    return ret;
  }

  /**
   *
   * @return
   */
  public double get1QBack() {
    double d = this.q1;
    if (d == 0.0) {
      d = this.q3;
    }
    else {
      d = this.q2;
    }
    return d;
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getMostRecent
   *
   * @return
   */
  public double getMostRecent() {

    double d = this.q1;
    if (d == 0.0) {
      d = this.q2;
    }
    return d;
  }

  /**
   *
   * @return
   */
  public double getPrevTtm() {

    double ret = 0.0;
    if (Math.abs(this.q5) > 0.0 && Math.abs(this.q6) > 0.0 && Math.abs(this.q7) > 0.0 && Math.abs(this.q8) > 0.0) {
      ret = this.q5 + this.q6 + this.q7 + this.q8;
    }
    return ret;
  }

  /**
   *
   * @return
   */
  public String getQoQ() {

    return this.getQoQ(1.0);

  }

  /**
   *
   * @return
   */
  public String getQoQ(double scaler) {
    String ret = "";

    double chg = 0.0;
    if (Math.abs(this.q1) > 0.0) {
      chg = (this.q1 - this.q5) / Math.abs(this.q5) * 100.0;
    }

    final long centq1 = (long) (this.q1 * scaler);
    final long centq5 = (long) (this.q5 * scaler);

    final String c1 = String.format("%,16d", centq1);
    final String c5 = String.format("%,16d", centq5);

    ret += String.format("%s %s   --> %12.2f%%", c1, c5, chg);

    return ret;
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getTtm
   *
   * @return
   */
  public double getTtm() {

    if (this.ttm != 0.0) {
      return this.ttm;
    }

    this.ttm = this.q1 + this.q2 + this.q3 + this.q4;
    if (this.q1 == 0.0) {
      this.ttm += this.q5;
    }
    return this.ttm;
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getTtmAvg
   *
   * @return
   */
  public double getTtmAvg() {

    return this.getTtm() / 4.0;
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.parse
   *
   * @param fld
   */
  public void parse(final String fld[]) {

    try {
      int pos = QuarterlyData.colPos.get(this.type);
      this.q1 = QuarterlyData.parseDouble(fld[pos++]);
      this.q2 = QuarterlyData.parseDouble(fld[pos++]);
      this.q3 = QuarterlyData.parseDouble(fld[pos++]);
      this.q4 = QuarterlyData.parseDouble(fld[pos++]);
      this.q5 = QuarterlyData.parseDouble(fld[pos++]);
      this.q6 = QuarterlyData.parseDouble(fld[pos++]);
      this.q7 = QuarterlyData.parseDouble(fld[pos++]);
      this.q8 = QuarterlyData.parseDouble(fld[pos]);

      this.dd.calculate(this);

    }
    catch (final Exception e) {
      this.initData();
    }
  }

  /**
   * net.ajaskey.market.tools.SIP.sum
   *
   * @param cash
   */
  public void sum(final QuarterlyData qd) {

    this.q1 += qd.q1;
    this.q2 += qd.q2;
    this.q3 += qd.q3;
    this.q4 += qd.q4;
    this.q5 += qd.q5;
    this.q6 += qd.q6;
    this.q7 += qd.q7;
    this.q8 += qd.q8;

  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = this.type + QuarterlyData.NL;
    ret += QuarterlyData.TAB2 + "Q1 :" + QuarterlyData.TAB + QuarterlyData.fmt(this.q1) + QuarterlyData.NL;
    ret += QuarterlyData.TAB2 + "Q2 :" + QuarterlyData.TAB + QuarterlyData.fmt(this.q2) + QuarterlyData.NL;
    ret += QuarterlyData.TAB2 + "Q3 :" + QuarterlyData.TAB + QuarterlyData.fmt(this.q3) + QuarterlyData.NL;
    ret += QuarterlyData.TAB2 + "Q4 :" + QuarterlyData.TAB + QuarterlyData.fmt(this.q4) + QuarterlyData.NL;
    ret += QuarterlyData.TAB2 + "Q5 :" + QuarterlyData.TAB + QuarterlyData.fmt(this.q5) + QuarterlyData.NL;
    ret += QuarterlyData.TAB2 + "Q6 :" + QuarterlyData.TAB + QuarterlyData.fmt(this.q6) + QuarterlyData.NL;
    ret += QuarterlyData.TAB2 + "Q7 :" + QuarterlyData.TAB + QuarterlyData.fmt(this.q7) + QuarterlyData.NL;
    ret += QuarterlyData.TAB2 + "Q8 :" + QuarterlyData.TAB + QuarterlyData.fmt(this.q8) + QuarterlyData.NL;
    ret += this.dd;
    return ret;
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.initData
   *
   */
  private void initData() {

    this.q8 = 0.0;
    this.q7 = 0.0;
    this.q6 = 0.0;
    this.q5 = 0.0;
    this.q4 = 0.0;
    this.q3 = 0.0;
    this.q2 = 0.0;
    this.q1 = 0.0;
    this.ttm = 0.0;
  }

}
