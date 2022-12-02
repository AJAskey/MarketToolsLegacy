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
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.optuma.TickerPriceData;

public class SpxEarnings {

  final private static int         QoD           = 8;
  private static SimpleDateFormat  sdf           = new SimpleDateFormat("MM/dd/yyyy");
  private static SimpleDateFormat  sdfout        = new SimpleDateFormat("yyyy-MMM-dd");
  private static List<SpxEarnings> spxList       = new ArrayList<>();
  private static Double            spxTe[]       = null;
  private static Double            totalMCap[]   = new Double[8];
  private static Double            totalShares[] = new Double[8];

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    final List<String> input = TextUtils.readTextFile("data/SPX-Earnings.txt", true);

    for (final String s : input) {
      final SpxEarnings data = new SpxEarnings(s);
      if (data.valid) {
        SpxEarnings.spxList.add(data);
      }
    }

    // final List<String> allSectors = SpxEarnings.findSectors(SpxEarnings.spxList);
    // final List<String> allIndustries =
    // SpxEarnings.findIndustries(SpxEarnings.spxList);

    final TickerPriceData spxData = new TickerPriceData("WI", "SPX");
    final double spxPrice = spxData.getLatest();

    String dir = "sipout/earnings";
    Utils.makeDirs(dir);
    final DateTime today = new DateTime();
    today.setSdf(new SimpleDateFormat("yyyyMMdd"));
    String fname = String.format("%s/SPX_%s_Historic_Earnings.txt", dir, today);
    SpxEarnings.spxTe = SpxEarnings.process(SpxEarnings.spxList, spxPrice, fname);

//    for (final String sec : allSectors) {
//      final List<SpxEarnings> seList = SpxEarnings.findSectors(SpxEarnings.spxList, sec);
//      SpxEarnings.process(seList, 0.0, String.format("%s/%s_%s_Historic-Earnings.txt", dir, sec, today));
//    }
//
//    dir += "/industry";
//    Utils.makeDir(dir);
//
//    for (final String ind : allIndustries) {
//      final List<SpxEarnings> seList = SpxEarnings.findIndustries(SpxEarnings.spxList, ind);
//      SpxEarnings.process(seList, 0.0, String.format("%s/%s_%s_Historic-Earnings.txt", dir, ind, today));
//    }
//
//    /**
//     * Estimate 50 decline in earnings
//     */
//    final double earningsDrop = 0.65;
//    final List<SpxEarnings> estList = new ArrayList<>();
//    for (final SpxEarnings se : SpxEarnings.spxList) {
//      final SpxEarnings tmp = new SpxEarnings(se);
//      tmp.est1 = tmp.incEps[1] * earningsDrop;
//      tmp.estNet1 = tmp.est1 * tmp.shares[1];
//      tmp.incEps[0] = tmp.est1;
//      estList.add(tmp);
//    }
//    dir = "sipout/earnings";
//    fname = String.format("%s/SPX_%s_Estimated_Earnings.txt", dir, today);
//    SpxEarnings.process(estList, 0.0, fname);

  }

  /**
   *
   * @param i
   * @return
   */
  private static double calcEarnings(int i, List<SpxEarnings> seList) {
    double totearn = 0.0;
    for (final SpxEarnings se : seList) {
      final double earn = SpxEarnings.calcEarnings(se, i);
      totearn += earn;
    }
    return totearn;
  }

  /**
   *
   * @param se
   * @param i
   * @return
   */
  private static double calcEarnings(SpxEarnings se, int i) {
    final double capRatio = se.mktCap[i] / SpxEarnings.totalMCap[i];
    final double shrRatio = se.shares[i] / SpxEarnings.totalShares[i];
    final double ret = se.incEps[i] * capRatio * shrRatio;
    return ret;
  }

  private static void calcTotals(List<SpxEarnings> seList) {

    for (int i = 0; i < SpxEarnings.QoD; i++) {
      SpxEarnings.totalShares[i] = 0.0;
      SpxEarnings.totalMCap[i] = 0.0;
    }
    for (final SpxEarnings se : seList) {
      SpxEarnings.totalShares[1] += se.shares[1];
      SpxEarnings.totalShares[2] += se.shares[2];
      SpxEarnings.totalShares[3] += se.shares[3];
      SpxEarnings.totalShares[4] += se.shares[4];
      SpxEarnings.totalShares[5] += se.shares[5];
      SpxEarnings.totalShares[6] += se.shares[6];
      SpxEarnings.totalShares[7] += se.shares[7];
      SpxEarnings.totalShares[0] += se.shares[1];

      SpxEarnings.totalMCap[1] += se.mktCap[1];
      SpxEarnings.totalMCap[2] += se.mktCap[2];
      SpxEarnings.totalMCap[3] += se.mktCap[3];
      SpxEarnings.totalMCap[4] += se.mktCap[4];
      SpxEarnings.totalMCap[5] += se.mktCap[5];
      SpxEarnings.totalMCap[6] += se.mktCap[6];
      SpxEarnings.totalMCap[7] += se.mktCap[7];
      SpxEarnings.totalMCap[0] += se.mktCap[1];
    }

    for (int i = 0; i < SpxEarnings.QoD; i++) {
      System.out.printf("%15.2f", SpxEarnings.totalShares[i]);
    }
    System.out.println();
    for (int i = 0; i < SpxEarnings.QoD; i++) {
      System.out.printf("%15.2f", SpxEarnings.totalMCap[i]);
    }
    System.out.println();
  }

  /**
   *
   * @param seList
   * @return
   */
  private static List<String> findIndustries(List<SpxEarnings> seList) {
    final Set<String> ind = new HashSet<>();
    for (final SpxEarnings cd : seList) {
      final String i = cd.industry.toUpperCase().trim();
      ind.add(i);
    }

    final List<String> ret = new ArrayList<>(ind);
    return ret;
  }

  /**
   *
   * @param seList
   * @param industry
   * @return
   */
  private static List<SpxEarnings> findIndustries(List<SpxEarnings> seList, String industry) {
    final List<SpxEarnings> retList = new ArrayList<>();

    for (final SpxEarnings se : seList) {
      if (se.industry.equalsIgnoreCase(industry)) {
        retList.add(se);
      }
    }

    return retList;
  }

  /**
   *
   * @param cdList
   * @return
   */
  private static List<String> findSectors(List<SpxEarnings> seList) {
    final Set<String> sec = new HashSet<>();
    for (final SpxEarnings se : seList) {
      final String s = se.sector.toUpperCase().trim();
      sec.add(s);
    }

    final List<String> ret = new ArrayList<>(sec);
    return ret;
  }

  /**
   *
   * @param seList
   * @param sector
   * @return
   */
  private static List<SpxEarnings> findSectors(List<SpxEarnings> seList, String sector) {
    final List<SpxEarnings> retList = new ArrayList<>();

    for (final SpxEarnings se : seList) {
      if (se.sector.equalsIgnoreCase(sector)) {
        retList.add(se);
      }
    }

    return retList;
  }

  /**
   *
   * @param seList
   * @throws FileNotFoundException
   */
  private static Double[] process(List<SpxEarnings> seList, double indexPrice, String filename) throws FileNotFoundException {

    System.out.println(filename);

    final Double[] te = new Double[SpxEarnings.QoD];
    final Double[] tePct = new Double[SpxEarnings.QoD];
    for (int i = 0; i < SpxEarnings.QoD; i++) {
      tePct[i] = 100.0;
    }

    SpxEarnings.calcTotals(SpxEarnings.spxList);

    for (int i = 0; i < SpxEarnings.QoD; i++) {
      te[i] = SpxEarnings.calcEarnings(i, seList);
      if (SpxEarnings.spxTe != null) {
        tePct[i] = te[i] / SpxEarnings.spxTe[i] * 100.0;
      }
    }

    final double pe0 = indexPrice / te[0];
    final double pe1 = indexPrice / te[1];
    try (PrintWriter pw = new PrintWriter(filename)) {
      pw.println(filename);
      for (int i = 0; i < te.length; i++) {

        double chg = 0.0;
        if (i < te.length - 1) {
          final double delta = te[i] - te[i + 1];
          chg = delta / te[i + 1] * 100.0;
          if (indexPrice > 0.0 && i == 1) {
            pw.printf("  Year %d : %12.4f%10.1f%%\t--\t%7.1f%% of SPX\t\tPE: %.2f%n", i, te[i], chg, tePct[i], pe1);
          }
          else if (indexPrice > 0.0 && i == 0) {
            pw.printf("  Year %d : %12.4f%10.1f%%\t--\t%7.1f%% of SPX\t\tPE: %.2f%n", i, te[i], chg, tePct[i], pe0);
          }
          else {
            pw.printf("  Year %d : %12.4f%10.1f%%\t--\t%7.1f%% of SPX%n", i, te[i], chg, tePct[i]);
          }
        }
        else {
          pw.printf("  Year %d : %12.4f%n", i, te[i]);
        }
      }
    }
    return te;
  }

  public DateTime currentFiscalYear;
  public double   est1;
  public double   estNet1;
  public String   exchange;
  public Double[] incEps = new Double[SpxEarnings.QoD];
  public String   industry;
  public DateTime lastQtrEps;
  public Double[] mktCap = new Double[SpxEarnings.QoD];
  public String   name;
  public String   sector;
  public Double[] shares = new Double[SpxEarnings.QoD];
  public String   ticker;

  private boolean valid;

  /**
   * Copy Constructor
   *
   * @param se
   */
  public SpxEarnings(SpxEarnings se) {
    this.ticker = se.ticker;
    this.name = se.name;
    this.exchange = se.exchange;
    this.sector = se.sector;
    this.industry = se.industry;
    this.currentFiscalYear = se.currentFiscalYear;
    this.lastQtrEps = se.lastQtrEps;
    for (int i = 0; i < SpxEarnings.QoD; i++) {
      this.mktCap[i] = se.mktCap[i];
      this.shares[i] = se.shares[i];
      this.incEps[i] = se.incEps[i];
    }
    this.est1 = se.est1;
    this.estNet1 = se.estNet1;
    this.valid = se.valid;
  }

  /**
   *
   * @param s
   */
  public SpxEarnings(String s) {

    try {

      final String ss = s.replace("\"", "");
      final String fld[] = ss.split("\t");

      this.ticker = fld[0].trim();
      this.name = fld[1].trim();
      this.exchange = fld[2].trim();
      this.sector = fld[3].trim();
      this.industry = fld[4].trim();

      this.currentFiscalYear = new DateTime(fld[5].trim(), SpxEarnings.sdf);
      this.lastQtrEps = new DateTime(fld[6].trim(), SpxEarnings.sdf);

      this.mktCap[1] = SipUtils.parseDouble(fld[7].trim());
      this.mktCap[2] = SipUtils.parseDouble(fld[8].trim());
      this.mktCap[3] = SipUtils.parseDouble(fld[9].trim());
      this.mktCap[4] = SipUtils.parseDouble(fld[10].trim());
      this.mktCap[5] = SipUtils.parseDouble(fld[11].trim());
      this.mktCap[6] = SipUtils.parseDouble(fld[12].trim());
      this.mktCap[7] = SipUtils.parseDouble(fld[13].trim());

      this.incEps[1] = SipUtils.parseDouble(fld[14].trim());
      this.incEps[2] = SipUtils.parseDouble(fld[15].trim());
      this.incEps[3] = SipUtils.parseDouble(fld[16].trim());
      this.incEps[4] = SipUtils.parseDouble(fld[17].trim());
      this.incEps[5] = SipUtils.parseDouble(fld[18].trim());
      this.incEps[6] = SipUtils.parseDouble(fld[19].trim());
      this.incEps[7] = SipUtils.parseDouble(fld[20].trim());

      this.shares[1] = SipUtils.parseDouble(fld[21].trim());
      this.shares[2] = SipUtils.parseDouble(fld[22].trim());
      this.shares[3] = SipUtils.parseDouble(fld[23].trim());
      this.shares[4] = SipUtils.parseDouble(fld[24].trim());
      this.shares[5] = SipUtils.parseDouble(fld[25].trim());
      this.shares[6] = SipUtils.parseDouble(fld[26].trim());
      this.shares[7] = SipUtils.parseDouble(fld[27].trim());

      this.est1 = SipUtils.parseDouble(fld[28].trim());

      this.shares[0] = this.shares[1];
      this.mktCap[0] = this.mktCap[1];
      this.incEps[0] = this.est1 * this.shares[0];

      this.currentFiscalYear.setSdf(SpxEarnings.sdfout);
      this.lastQtrEps.setSdf(SpxEarnings.sdfout);

      this.valid = true;

    }
    catch (final Exception e) {
      e.printStackTrace();
      this.valid = false;
    }
  }

  @Override
  public String toString() {
    String ret = String.format("%s : %s : %s : %s : %s\t", this.ticker, this.name, this.exchange, this.sector, this.industry);
    ret += String.format("  %s : %s%n", this.currentFiscalYear, this.lastQtrEps);
    ret += String.format("  %12.2f%12.2f%12.2f%12.2f%12.2f%12.2f%12.2f%n", this.mktCap[1], this.mktCap[2], this.mktCap[3], this.mktCap[4],
        this.mktCap[5], this.mktCap[6], this.mktCap[7]);
    ret += String.format("  %12.2f%12.2f%12.2f%12.2f%12.2f%12.2f%12.2f\t[%.2f -- %.2f]%n", this.incEps[1], this.incEps[2], this.incEps[3],
        this.incEps[4], this.incEps[5], this.incEps[6], this.incEps[7], this.est1, this.estNet1);
    ret += String.format("  %12.2f%12.2f%12.2f%12.2f%12.2f%12.2f%12.2f", this.shares[1], this.shares[2], this.shares[3], this.shares[4],
        this.shares[5], this.shares[6], this.shares[7]);
    return ret;
  }

}
