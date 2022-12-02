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

public class BalanceSheetData {

  final private static String TAB = "\t";

  public static BalanceSheetData setBalanceSheetInfo(final String[] fld) {

    final BalanceSheetData bsd = new BalanceSheetData();

    bsd.cash.parse(fld);
    bsd.stInvestments.parse(fld);
    bsd.acctReceiveable.parse(fld);
    bsd.inventory.parse(fld);
    bsd.otherAssets.parse(fld);
    bsd.currentAssets.parse(fld);
    bsd.fixedAssets.parse(fld);
    bsd.ltInvestments.parse(fld);
    bsd.goodwill.parse(fld);
    bsd.otherLtAssets.parse(fld);
    bsd.totalAssets.parse(fld);
    bsd.acctPayable.parse(fld);
    bsd.stDebt.parse(fld);
    bsd.otherCurrLiab.parse(fld);
    bsd.currLiab.parse(fld);
    bsd.ltDebt.parse(fld);
    bsd.otherLtLiab.parse(fld);
    bsd.totalLiab.parse(fld);
    bsd.prefStock.parse(fld);
    bsd.equity.parse(fld);
    bsd.liabEquity.parse(fld);
    bsd.bvps.parse(fld);

    // Derived
    BalanceSheetData.calcAssetsMinusGW(bsd);

    return bsd;

  }

  /**
   * net.ajaskey.market.tools.SIP.calcAssetsMinusGW
   *
   * @param bsd
   * @return
   */
  private static void calcAssetsMinusGW(BalanceSheetData bsd) {

    bsd.assetsMinusGW.q1 = bsd.totalAssets.q1 - bsd.goodwill.q1;
    bsd.assetsMinusGW.q2 = bsd.totalAssets.q2 - bsd.goodwill.q2;
    bsd.assetsMinusGW.q3 = bsd.totalAssets.q3 - bsd.goodwill.q3;
    bsd.assetsMinusGW.q4 = bsd.totalAssets.q4 - bsd.goodwill.q4;
    bsd.assetsMinusGW.q5 = bsd.totalAssets.q5 - bsd.goodwill.q5;
    bsd.assetsMinusGW.q6 = bsd.totalAssets.q6 - bsd.goodwill.q6;
    bsd.assetsMinusGW.q7 = bsd.totalAssets.q7 - bsd.goodwill.q7;
    bsd.assetsMinusGW.q8 = bsd.totalAssets.q8 - bsd.goodwill.q8;

    bsd.assetsMinusGW.dd.calculate(bsd.assetsMinusGW);

  }

  public QuarterlyData acctPayable;
  public QuarterlyData acctReceiveable;
  // Derived
  public QuarterlyData assetsMinusGW;
  public QuarterlyData bvps;
  public QuarterlyData cash;
  public QuarterlyData currentAssets;
  public QuarterlyData currLiab;
  public QuarterlyData equity;
  public QuarterlyData fixedAssets;
  public QuarterlyData goodwill;
  public QuarterlyData inventory;
  public QuarterlyData liabEquity;
  public QuarterlyData ltDebt;
  public QuarterlyData ltInvestments;
  public QuarterlyData otherAssets;
  public QuarterlyData otherCurrLiab;
  public QuarterlyData otherLtAssets;
  public QuarterlyData otherLtLiab;
  public QuarterlyData prefStock;
  public QuarterlyData stDebt;

  public QuarterlyData stInvestments;

  public QuarterlyData totalAssets;

  public QuarterlyData totalLiab;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public BalanceSheetData() {

    this.cash = new QuarterlyData("cash");
    this.stInvestments = new QuarterlyData("stInvestments");
    this.acctReceiveable = new QuarterlyData("acctReceiveable");
    this.inventory = new QuarterlyData("inventory");
    this.otherAssets = new QuarterlyData("otherAssets");
    this.currentAssets = new QuarterlyData("currentAssets");
    this.fixedAssets = new QuarterlyData("fixedAssets");
    this.ltInvestments = new QuarterlyData("ltInvestments");
    this.goodwill = new QuarterlyData("goodwill");
    this.otherLtAssets = new QuarterlyData("otherLtAssets");
    this.totalAssets = new QuarterlyData("totalAssets");
    this.acctPayable = new QuarterlyData("acctPayable");
    this.stDebt = new QuarterlyData("stDebt");
    this.otherCurrLiab = new QuarterlyData("otherCurrLiab");
    this.currLiab = new QuarterlyData("currLiab");
    this.ltDebt = new QuarterlyData("ltDebt");
    this.otherLtLiab = new QuarterlyData("otherLtLiab");
    this.totalLiab = new QuarterlyData("totalLiab");
    this.prefStock = new QuarterlyData("prefStock");
    this.equity = new QuarterlyData("equity");
    this.liabEquity = new QuarterlyData("liabEquity");
    this.bvps = new QuarterlyData("bvps");

    this.assetsMinusGW = new QuarterlyData("assetsMinusGW");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = "";
    ret += BalanceSheetData.TAB + this.acctPayable;
    ret += BalanceSheetData.TAB + this.acctReceiveable;
    ret += BalanceSheetData.TAB + this.bvps;
    ret += BalanceSheetData.TAB + this.cash;
    ret += BalanceSheetData.TAB + this.equity;
    ret += BalanceSheetData.TAB + this.inventory;
    ret += BalanceSheetData.TAB + this.prefStock;

    ret += BalanceSheetData.TAB + this.currentAssets;
    ret += BalanceSheetData.TAB + this.fixedAssets;
    ret += BalanceSheetData.TAB + this.otherLtAssets;
    ret += BalanceSheetData.TAB + this.otherAssets;
    ret += BalanceSheetData.TAB + this.goodwill;
    ret += BalanceSheetData.TAB + this.totalAssets;

    ret += BalanceSheetData.TAB + this.stInvestments;
    ret += BalanceSheetData.TAB + this.ltInvestments;

    ret += BalanceSheetData.TAB + this.stDebt;
    ret += BalanceSheetData.TAB + this.ltDebt;

    ret += BalanceSheetData.TAB + this.currLiab;
    ret += BalanceSheetData.TAB + this.liabEquity;
    ret += BalanceSheetData.TAB + this.otherCurrLiab;
    ret += BalanceSheetData.TAB + this.otherLtLiab;
    ret += BalanceSheetData.TAB + this.totalLiab;
    return ret;
  }
}
