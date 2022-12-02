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
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;

public class SharesFileData implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3414109230992409767L;

  public static void main(String[] args) {
    SharesFileData.readSipData("D:\\dev\\eclipse-markettools\\MarketTools\\data\\BigDB\\2020\\Q3\\SHARES-2020Q3.TXT");
  }

  /**
   * Stores all SharesFileDate read in from DB.
   */
  private static List<SharesFileData> sfdList = new ArrayList<>();

  public static void clearList() {
    SharesFileData.sfdList.clear();
  }

  /**
   * Returns the ShareFileData instance for requested ticker.
   *
   * @param ticker The name of the individual stock symbol file
   * @return SharesFileData
   */
  public static SharesFileData find(String ticker) {
    if (ticker != null) {
      if (ticker.trim().length() > 0) {
        for (final SharesFileData s : SharesFileData.sfdList) {
          if (s.getTicker().equalsIgnoreCase(ticker)) {
            return s;
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns the number of instances in the list read from the DB.
   *
   * @return count
   */
  public static int getListCount() {
    return SharesFileData.sfdList.size();
  }

  /**
   *
   * @return
   */
  public static List<String> getTickers() {
    final List<String> tickers = new ArrayList<>();
    for (final SharesFileData sfd : SharesFileData.sfdList) {
      tickers.add(sfd.ticker.trim().toUpperCase());
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
    for (final SharesFileData s : SharesFileData.sfdList) {
      ret += s.toString();
    }
    return ret;
  }

  /**
   * Parses data and fills data structures from DB files.
   *
   * @param data Data to parse
   * @return SharesFileData
   */
  public static SharesFileData readFromDb(List<String> data) {

    final SharesFileData sfd = new SharesFileData();

    for (final String s : data) {

      final String[] tfld = s.split(":");

      final String fld = tfld[0].trim();

      String val = "";
      if (tfld.length > 1) {
        val = tfld[1].trim();
      }

      if (fld.equals("price")) {
        sfd.setPrice(val);
      }
      else if (fld.equals("price 52w high")) {
        sfd.setPrice52hi(val);
      }
      else if (fld.equals("price 52w low")) {
        sfd.setPrice52lo(val);
      }
      else if (fld.equals("float")) {
        sfd.setFloatshr(val);
      }
      else if (fld.equals("market cap")) {
        sfd.setMktCap(val);
      }
      else if (fld.equals("volume 10d avg")) {
        sfd.setVolume10d(val);
      }
      else if (fld.equals("volume 3m avg")) {
        sfd.setVolume3m(val);
      }
      else if (fld.equals("dollars 3m avg")) {
        sfd.setDollar3m(val);
      }
      else if (fld.equals("beta")) {
        sfd.setBeta(val);
      }
      else if (fld.equals("insider ownership")) {
        sfd.setInsiderOwnership(val);
      }
      else if (fld.equals("insider buys")) {
        sfd.setInsiderBuys(val);
      }
      else if (fld.equals("insider buy shares")) {
        sfd.setInsiderBuyShrs(val);
      }
      else if (fld.equals("insider sells")) {
        sfd.setInsiderSells(val);
      }
      else if (fld.equals("insider sell shares")) {
        sfd.setInsiderSellShrs(val);
      }
      else if (fld.equals("insider net shares")) {
        sfd.setInsiderNetTrades(val);
      }
      else if (fld.equals("inst buy shares")) {
        sfd.setInstBuyShrs(val);
      }
      else if (fld.equals("inst sell shares")) {
        sfd.setInstSellShrs(val);
      }
      else if (fld.equals("inst shareholders")) {
        sfd.setInstShareholders(val);
      } //
      else if (fld.equals("inst ownership")) {
        sfd.setInstOwnership(val);
      }
      else if (fld.equals("shares quarterly")) {
        final double[] sharesQ = SipUtils.parseArrayDoubles(tfld[1], 1);
        sfd.setSharesQ(sharesQ);
      }
      else if (fld.equals("shares yearly")) {
        final double[] sharesY = SipUtils.parseArrayDoubles(tfld[1], 1);
        sfd.setSharesY(sharesY);
      }

    }
    return sfd;

  }

  /**
   * Reads the data from SIP tab delimited files and fills data structures.
   *
   * @param filename Name of SIP file to parse
   */
  public static void readSipData(String filename) {

    final List<String> shrData = TextUtils.readTextFile(filename, true);
    for (final String s : shrData) {
      final String[] fld = s.replace("\"", "").split(Utils.TAB);
      final SharesFileData sfd = new SharesFileData(fld);
      SharesFileData.sfdList.add(sfd);

      /**
       * These important tickers are not in SIP data. Use a copy to add to this DB.
       * 
       * GOOGL exists but GOOG does not.
       * 
       * BRK.A exists but BRK.B does not.
       * 
       */
      if (sfd.ticker.equals("GOOGL")) {
        SharesFileData goog = new SharesFileData(sfd);
        goog.ticker = "GOOG";
        SharesFileData.sfdList.add(goog);
      }
      else if (sfd.ticker.equals("BRK.A")) {
        SharesFileData brkb = new SharesFileData(sfd);
        brkb.ticker = "BRK.B";
        SharesFileData.sfdList.add(brkb);
      }
    }
  }

  public static void setSfdList(List<SharesFileData> sfdList) {
    SharesFileData.sfdList = sfdList;
  }

  private double   beta;
  private double   dollar3m;
  private ExchEnum exchange;
  private double   floatshr;
  private String   industry;
  private int      insiderBuys;
  private int      insiderBuyShrs;
  private double   insiderNetPercentOutstanding;
  private int      insiderNetTrades;
  private double   insiderOwnership;
  private int      insiderSells;
  private int      insiderSellShrs;
  private int      instBuyShrs;
  private double   instOwnership;
  private int      instSellShrs;
  private int      instShareholders;
  private double   mktCap;
  private String   name;
  private double   price;
  private double   price52hi;
  private double   price52lo;
  private String   sector;
  private double[] sharesQtr;
  private double[] sharesYr;
  private String   ticker;
  private long     volume10d;

  private long volumeMonth3m;

  public SharesFileData(SharesFileData sfd) {
    if (sfd != null) {
      this.beta = sfd.beta;
      this.dollar3m = sfd.dollar3m;
      this.exchange = sfd.exchange;
      this.floatshr = sfd.floatshr;
      this.industry = sfd.industry;
      this.insiderBuys = sfd.insiderBuys;
      this.insiderBuyShrs = sfd.insiderBuyShrs;
      this.insiderNetPercentOutstanding = sfd.insiderNetPercentOutstanding;
      this.insiderNetTrades = sfd.insiderNetTrades;
      this.insiderOwnership = sfd.insiderOwnership;
      this.insiderSells = sfd.insiderSells;
      this.insiderSellShrs = sfd.insiderSellShrs;
      this.instBuyShrs = sfd.instBuyShrs;
      this.instOwnership = sfd.instOwnership;
      this.instSellShrs = sfd.instSellShrs;
      this.instShareholders = sfd.instShareholders;
      this.mktCap = sfd.mktCap;
      this.name = sfd.name;
      this.price = sfd.price;
      this.price52hi = sfd.price52hi;
      this.price52lo = sfd.price52lo;
      this.sector = sfd.sector;
      this.sharesQtr = sfd.sharesQtr;
      this.sharesYr = sfd.sharesYr;
      this.ticker = sfd.ticker;
      this.volume10d = sfd.volume10d;
      this.volumeMonth3m = sfd.volumeMonth3m;
    }
    else {
      this.ticker = "";
    }

  }

  SharesFileData() {
    this.sharesQtr = new double[1];
    this.sharesQtr[0] = 0.0;
    this.sharesYr = new double[1];
    this.sharesYr[0] = 0.0;
  }

  /**
   * Constructor fills data structures.
   *
   * @param filename
   * @return
   */
  SharesFileData(String[] fld) {
    this.name = fld[0].trim();
    this.ticker = fld[1].trim();
    this.exchange = FieldData.convertExchange(fld[2].trim());
    this.sector = fld[3].trim();
    this.industry = fld[4].trim();
    this.beta = SipUtils.parseDouble(fld[5]);
    this.floatshr = SipUtils.parseDouble(fld[6]);
    this.insiderOwnership = SipUtils.parseDouble(fld[7]);
    this.insiderBuys = SipUtils.parseInt(fld[8]);
    this.insiderNetTrades = SipUtils.parseInt(fld[9]);
    this.insiderSells = SipUtils.parseInt(fld[10]);
    this.insiderBuyShrs = SipUtils.parseInt(fld[11]);
    this.insiderSellShrs = SipUtils.parseInt(fld[12]);
    this.instOwnership = SipUtils.parseDouble(fld[13]);
    this.instShareholders = SipUtils.parseInt(fld[14]);
    this.instBuyShrs = SipUtils.parseInt(fld[15]);
    this.instSellShrs = SipUtils.parseInt(fld[16]);
    this.mktCap = SipUtils.parseDouble(fld[17]);
    this.insiderNetPercentOutstanding = SipUtils.parseDouble(fld[18]);
    this.price = SipUtils.parseDouble(fld[19]);
    this.price52hi = SipUtils.parseDouble(fld[30]);
    this.price52lo = SipUtils.parseDouble(fld[31]);
    this.sharesQtr = SipUtils.parseDoubles(fld, 38, 8);
    this.sharesYr = SipUtils.parseDoubles(fld, 46, 7);
    this.volume10d = SipUtils.parseLong(fld[56]);
    this.volumeMonth3m = SipUtils.parseLong(fld[57]);
    this.dollar3m = SipUtils.parseDouble(fld[58]);
  }

  public double getBeta() {
    return this.beta;
  }

  public double getDollar3m() {
    return this.dollar3m;
  }

  public ExchEnum getExchange() {
    return this.exchange;
  }

  public double getFloatshr() {
    return this.floatshr;
  }

  public String getIndustry() {
    return this.industry;
  }

  public int getInsiderBuys() {
    return this.insiderBuys;
  }

  public int getInsiderBuyShrs() {
    return this.insiderBuyShrs;
  }

  public double getInsiderNetPercentOutstanding() {
    return this.insiderNetPercentOutstanding;
  }

  public int getInsiderNetTrades() {
    return this.insiderNetTrades;
  }

  public double getInsiderOwnership() {
    return this.insiderOwnership;
  }

  public int getInsiderSells() {
    return this.insiderSells;
  }

  public int getInsiderSellShrs() {
    return this.insiderSellShrs;
  }

  public int getInstBuyShrs() {
    return this.instBuyShrs;
  }

  public double getInstOwnership() {
    return this.instOwnership;
  }

  public int getInstSellShrs() {
    return this.instSellShrs;
  }

  public int getInstShareholders() {
    return this.instShareholders;
  }

  public double getMktCap() {
    return this.mktCap;
  }

  public String getName() {
    return this.name;
  }

  public double getPrice() {
    return this.price;
  }

  public double getPrice52hi() {
    return this.price52hi;
  }

  public double getPrice52lo() {
    return this.price52lo;
  }

  public String getSector() {
    return this.sector;
  }

  public double[] getSharesQtr() {
    return this.sharesQtr;
  }

  public double[] getSharesYr() {
    return this.sharesYr;
  }

  public String getTicker() {
    return this.ticker;
  }

  public long getVolume10d() {
    return this.volume10d;
  }

  public long getVolumeMonth3m() {
    return this.volumeMonth3m;
  }

  public void setBeta(String fld) {
    this.beta = SipUtils.parseDouble(fld);
  }

  public void setDollar3m(String fld) {
    this.dollar3m = SipUtils.parseLong(fld);
  }

  public void setFloatshr(String fld) {
    this.floatshr = SipUtils.parseDouble(fld);
  }

  public void setFromReport(int year, int quarter) {

  }

  public void setInsiderBuys(String fld) {
    this.insiderBuys = SipUtils.parseInt(fld);
  }

  public void setInsiderBuyShrs(String fld) {
    this.insiderBuyShrs = SipUtils.parseInt(fld);
  }

  public void setInsiderNetPercentOutstanding(String fld) {
    this.insiderNetPercentOutstanding = SipUtils.parseDouble(fld);
  }

  public void setInsiderNetTrades(String fld) {
    this.insiderNetTrades = SipUtils.parseInt(fld);
  }

  public void setInsiderOwnership(String fld) {
    this.insiderOwnership = SipUtils.parseDouble(fld);
  }

  public void setInsiderSells(String fld) {
    this.insiderSells = SipUtils.parseInt(fld);
  }

  public void setInsiderSellShrs(String fld) {
    this.insiderSellShrs = SipUtils.parseInt(fld);
  }

  public void setInstBuyShrs(String fld) {
    this.instBuyShrs = SipUtils.parseInt(fld);
  }

  public void setInstOwnership(String fld) {
    this.instOwnership = SipUtils.parseDouble(fld);
  }

  public void setInstSellShrs(String fld) {
    this.instSellShrs = SipUtils.parseInt(fld);
  }

  public void setInstShareholders(String fld) {
    this.instShareholders = SipUtils.parseInt(fld);
  }

  public void setMktCap(String fld) {
    this.mktCap = SipUtils.parseDouble(fld);
  }

  public void setPrice(String fld) {
    this.price = SipUtils.parseDouble(fld);
  }

  public void setSharesQ(double[] flds) {
    this.sharesQtr = flds;
  }

  public void setSharesY(double[] flds) {
    this.sharesYr = flds;
  }

  public void setVolume10d(String fld) {
    this.volume10d = SipUtils.parseLong(fld);
  }

  public void setVolume3m(String fld) {
    this.volumeMonth3m = SipUtils.parseLong(fld);
  }

  /**
   * Creates string of formatted data structures.
   *
   * @return String
   */
  public String toDbOutput() {
    String ret = "";
    ret += String.format("  price               : %f%n", this.price);
    ret += String.format("  price 52w high      : %f%n", this.price52hi);
    ret += String.format("  price 52w low       : %f%n", this.price52lo);
    ret += String.format("  float               : %f%n", this.floatshr);
    ret += String.format("  market cap          : %f%n", this.mktCap);
    ret += String.format("  volume 10d avg      : %d%n", this.volume10d);
    ret += String.format("  volume 3m avg       : %d%n", this.volumeMonth3m);
    ret += String.format("  dollars 3m avg      : %f%n", this.dollar3m);
    ret += String.format("  beta                : %f%n", this.beta);
    ret += String.format("  insider ownership   : %f%n", this.insiderOwnership);
    ret += String.format("  insider buys        : %d%n", this.insiderBuys);
    ret += String.format("  insider sells       : %d%n", this.insiderSells);
    ret += String.format("  insider buy shares  : %d%n", this.insiderBuyShrs);
    ret += String.format("  insider sell shares : %d%n", this.insiderSellShrs);
    ret += String.format("  insider net shares  : %d%n", this.insiderNetTrades);
    ret += String.format("  inst buy shares     : %d%n", this.instBuyShrs);
    ret += String.format("  inst sell shares    : %d%n", this.instSellShrs);
    ret += String.format("  inst shareholders   : %d%n", this.instShareholders);
    ret += String.format("  inst ownership      : %f%n", this.instOwnership);
    ret += String.format("  shares quarterly    : %s%n", SipOutput.buildArray("", this.sharesQtr, 10, 4, 1));
    ret += String.format("  shares yearly       : %s%n", SipOutput.buildArray("", this.sharesYr, 10, 4, 1));
    return ret;
  }

  @Override
  public String toString() {
    String ret = "";
    try {
      ret = SipOutput.SipHeader(this.ticker, this.name, FieldData.getExchangeStr(this.exchange), this.sector, this.industry);
      ret += String.format("  Price / Beta                 : %s  %s%n", SipOutput.fmt(this.price, 1, 2), SipOutput.fmt(this.beta, 1, 3));
      ret += String.format("  Volume3m / Dollars3m         : %s  %s%n", SipOutput.ifmt(this.volumeMonth3m, 1), SipOutput.fmt(this.dollar3m, 15, 2));
      ret += String.format("  Float / MCap / Insiders      : %s %s  %s%%  %s%% %n", SipOutput.fmt(this.floatshr, 1, 3),
          SipOutput.ifmt((long) this.mktCap, 1), SipOutput.fmt(this.insiderOwnership, 1, 3), SipOutput.fmt(this.insiderNetPercentOutstanding, 1, 2));
      ret += String.format("  Institutions B/S Num Percent : %s  %s  %s  %s%% %n", SipOutput.ifmt(this.instBuyShrs, 1),
          SipOutput.ifmt(this.instSellShrs, 1), SipOutput.ifmt(this.instShareholders, 1), SipOutput.fmt(this.instOwnership, 1, 1));
      ret += String.format("  Insider B / S / Net          : %d-%d  %d-%d  %d%n", this.insiderBuys, this.insiderBuyShrs, this.insiderSells,
          this.insiderSellShrs, this.insiderNetTrades);
      ret += String.format("  Shares Quarterly             : %s%n", SipOutput.buildArray("", this.sharesQtr, 10, 4));
      ret += String.format("  Shares Yearly                : %s%n", SipOutput.buildArray("", this.sharesYr, 10, 4));
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
    this.ticker = cfd.getTicker();
    this.name = cfd.getName();
    this.sector = cfd.getSector();
    this.industry = cfd.getIndustry();
    this.exchange = cfd.getExchange();
  }

  private void setPrice52hi(String fld) {
    this.price52hi = SipUtils.parseDouble(fld);
  }

  private void setPrice52lo(String fld) {
    this.price52lo = SipUtils.parseDouble(fld);
  }

}
