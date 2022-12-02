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
import java.text.DecimalFormatSymbols;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;

public class TotalData {

  public final static int printFormatLong  = 2;
  public final static int printFormatShort = 1;

  private static List<String> latestQDate = null;
  final private static String NL          = "\n";

  final private static String TAB = "\t";

  // public String name;
  // public String sector;
  // public String industry;

  /**
   *
   * @param lqd
   */
  public static void setLatestQDate(List<String> lqd) {
    TotalData.latestQDate = lqd;
  }

  public QuarterlyData acctPayable;
  public QuarterlyData acctReceiveable;
  public QuarterlyData adjustments;
  public QuarterlyData bvps;
  public QuarterlyData capex;
  // Balance Sheet
  public QuarterlyData cash;
  public QuarterlyData cashFromFin;
  public QuarterlyData cashFromInv;
  public QuarterlyData cashFromOps;
  public QuarterlyData cashNet;
  public QuarterlyData cogs;
  public QuarterlyData currentAssets;
  public QuarterlyData currLiab;
  public QuarterlyData depreciation;
  public QuarterlyData dividend;
  public QuarterlyData eps;
  public QuarterlyData epsContinuing;
  public QuarterlyData epsDilCont;
  public QuarterlyData epsDiluted;
  public QuarterlyData equity;
  public QuarterlyData fixedAssets;

  public QuarterlyData goodwill;
  public QuarterlyData grossIncome;
  public QuarterlyData grossOpIncome;
  public QuarterlyData incomeAfterTaxes;
  public QuarterlyData incomeEps;
  public QuarterlyData incomeTax;
  public QuarterlyData interestExp;
  public QuarterlyData interestExpNonOp;
  public QuarterlyData inventory;
  public QuarterlyData liabEquity;
  public QuarterlyData ltDebt;
  public QuarterlyData ltInvestments;
  public QuarterlyData netIncome;
  public QuarterlyData nonrecurring;
  public QuarterlyData otherAssets;
  public QuarterlyData otherCurrLiab;
  public QuarterlyData otherIncome;
  public QuarterlyData otherLtAssets;
  public QuarterlyData otherLtLiab;
  public double        p0;
  public double        p365;
  public double        p730;
  public QuarterlyData prefStock;
  public QuarterlyData pretaxIncome;
  public QuarterlyData rd;
  // Income Statement
  public QuarterlyData sales;

  public QuarterlyData shares;
  public QuarterlyData stDebt;
  public QuarterlyData stInvestments;

  public QuarterlyData totalAssets;

  public QuarterlyData totalLiab;

  public QuarterlyData totalOpExp;

  public QuarterlyData unusualIncome;

  DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();

  private int knt;

  private String outdir;

  private int printFormat;

  /**
   * This method serves as a constructor for the class.
   *
   * @param day730
   * @param day365
   * @param day0
   *
   */
  public TotalData(double day0, double day365, double day730) {

    this.knt = 0;

    this.p0 = day0;
    this.p365 = day365;
    this.p730 = day730;

    // this.name = "";
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

    this.sales = new QuarterlyData("sales");
    this.cogs = new QuarterlyData("cogs");
    this.grossIncome = new QuarterlyData("grossIncome");
    this.rd = new QuarterlyData("rd");
    this.capex = new QuarterlyData("capEx");
    this.depreciation = new QuarterlyData("depreciation");
    this.interestExp = new QuarterlyData("interestExp");
    this.grossOpIncome = new QuarterlyData("grossOpIncome");
    this.unusualIncome = new QuarterlyData("unusualIncome");
    this.totalOpExp = new QuarterlyData("totalOpExp");
    this.interestExpNonOp = new QuarterlyData("interestExpNonOp");
    this.otherIncome = new QuarterlyData("otherIncome");
    this.pretaxIncome = new QuarterlyData("pretaxIncome");
    this.incomeTax = new QuarterlyData("incomeTax");
    this.incomeAfterTaxes = new QuarterlyData("incomeAfterTaxes");
    this.adjustments = new QuarterlyData("adjustments");
    this.incomeEps = new QuarterlyData("incomeEps");
    this.nonrecurring = new QuarterlyData("nonrecurring");
    this.netIncome = new QuarterlyData("netIncome");
    this.eps = new QuarterlyData("eps");
    this.epsContinuing = new QuarterlyData("epsContinuing");
    this.epsDiluted = new QuarterlyData("epsDiluted");
    this.epsDilCont = new QuarterlyData("epsDilCont");
    this.dividend = new QuarterlyData("dividend");
    this.shares = new QuarterlyData("shares");

    this.cashFromOps = new QuarterlyData("cashFromOps");
    this.cashFromFin = new QuarterlyData("cashFromFin");
    this.cashFromInv = new QuarterlyData("cashFromInv");
    this.cashNet = new QuarterlyData("cashNet");

    this.setOutdir("out/SPX-Fundies");
    this.setPrintFormat(TotalData.printFormatLong);

  }

  /**
   * net.ajaskey.market.tools.SIP.add
   *
   * @param bsd
   */
  public void add(final CompanyData cd) {

    this.knt++;

    this.acctReceiveable.sum(cd.bsd.acctReceiveable);
    this.acctPayable.sum(cd.bsd.acctPayable);
    this.bvps.sum(cd.bsd.bvps);
    this.cash.sum(cd.bsd.cash);
    this.currentAssets.sum(cd.bsd.currentAssets);
    this.currLiab.sum(cd.bsd.currLiab);
    this.equity.sum(cd.bsd.equity);
    this.fixedAssets.sum(cd.bsd.fixedAssets);
    this.goodwill.sum(cd.bsd.goodwill);
    this.inventory.sum(cd.bsd.inventory);
    this.liabEquity.sum(cd.bsd.liabEquity);
    this.ltDebt.sum(cd.bsd.ltDebt);
    this.ltInvestments.sum(cd.bsd.ltInvestments);
    this.otherAssets.sum(cd.bsd.otherAssets);
    this.otherCurrLiab.sum(cd.bsd.otherCurrLiab);
    this.otherLtAssets.sum(cd.bsd.otherLtAssets);
    this.otherLtLiab.sum(cd.bsd.otherLtLiab);
    this.prefStock.sum(cd.bsd.prefStock);
    this.stDebt.sum(cd.bsd.stDebt);
    this.stInvestments.sum(cd.bsd.stInvestments);
    this.totalAssets.sum(cd.bsd.totalAssets);
    this.totalLiab.sum(cd.bsd.totalLiab);

    this.sales.sum(cd.id.sales);
    this.cogs.sum(cd.id.cogs);
    this.grossIncome.sum(cd.id.grossIncome);
    this.rd.sum(cd.id.rd);
    this.capex.sum(cd.cashData.capEx);
    this.depreciation.sum(cd.id.depreciation);
    this.interestExp.sum(cd.id.interestExp);
    this.unusualIncome.sum(cd.id.unusualIncome);
    this.totalOpExp.sum(cd.id.totalOpExp);
    this.grossOpIncome.sum(cd.id.grossOpIncome);
    this.interestExpNonOp.sum(cd.id.interestExpNonOp);
    this.otherIncome.sum(cd.id.otherIncome);
    this.pretaxIncome.sum(cd.id.pretaxIncome);
    this.incomeTax.sum(cd.id.incomeTax);
    this.incomeAfterTaxes.sum(cd.id.incomeAfterTaxes);
    this.adjustments.sum(cd.id.adjustments);
    this.incomeEps.sum(cd.id.incomeEps);
    this.nonrecurring.sum(cd.id.nonrecurring);
    this.netIncome.sum(cd.id.netIncome);
    this.eps.sum(cd.id.eps);
    this.epsContinuing.sum(cd.id.epsContinuing);
    this.epsDiluted.sum(cd.id.epsDiluted);
    this.epsDilCont.sum(cd.id.epsDilCont);
    this.dividend.sum(cd.id.dividend);
    this.shares.sum(cd.shares);

    this.cashFromOps.sum(cd.cashData.cashFromOps);
    this.cashFromFin.sum(cd.cashData.cashFromFin);
    this.cashFromInv.sum(cd.cashData.cashFromInv);

    this.cashNet.sum(cd.cashData.cashFromOps);
    this.cashNet.sum(cd.cashData.cashFromFin);
    this.cashNet.sum(cd.cashData.cashFromInv);

  }

  public int getKnt() {
    return this.knt;
  }

  public String getOutdir() {
    return this.outdir;
  }

  /**
   *
   * @param index
   * @param kntRecent
   * @param outdir
   * @throws FileNotFoundException
   */
//  public String process(String index, List<CompanyData> cdList, DateTime now) throws FileNotFoundException {
//    this.setOutdir(outdir);
//    Utils.makeDir(outdir);
//    return process(index, cdList, now);
//  }

  public int getPrintFormat() {
    return this.printFormat;
  }

  /**
   *
   * @param index
   * @param kntRecent
   * @throws FileNotFoundException
   */
  public String process(String index, DateTime now) throws FileNotFoundException

  {
    final String totstasset = this.calcTotalStAssets();
    final String totstliab = this.calcTotalStLiab();
    final String totwc = this.calcTotalWC(totstasset, totstliab);

    // final DateTime now = new DateTime();
    // now.add(DateTime.DATE, -1);
    // final String tdfn = String.format("%s/%s_TotalData_%s.txt", this.getOutdir(),
    // index, now.format("yyyy-MM-dd"));
    // try (PrintWriter pw = new PrintWriter(tdfn)) {

    String ret = String.format(
        "%n From AAII Stock Investor Pro -- Thomson/Reuters -- %s :: %s data.%n                               Q1               Q5%n", index,
        now.format("dd-MMM-yyyy"));

    ret += String.format(" Sales         -->  %s%n", this.sales.getQoQ());
    ret += String.format(" COGS          -->  %s%n", this.cogs.getQoQ());
    ret += String.format(" GrossOpInc    -->  %s%n", this.grossOpIncome.getQoQ());
    ret += String.format(" Net           -->  %s%n", this.netIncome.getQoQ());
    ret += String.format(" EPS           -->  %s%n", this.eps.getQoQ());
    ret += String.format(" Dividend      -->  %s%n", this.dividend.getQoQ());
    ret += String.format(" IncTax        -->  %s%n", this.incomeTax.getQoQ());
    ret += String.format(" IntExp        -->  %s%n", this.interestExp.getQoQ());
    ret += String.format(" IntExp NonOp  -->  %s%n", this.interestExpNonOp.getQoQ());
    ret += Utils.NL;
    ret += String.format(" LT Debt       -->  %s%n", this.ltDebt.getQoQ());
    ret += String.format(" Equity        -->  %s%n", this.equity.getQoQ());
    ret += String.format(" Goodwill      -->  %s%n", this.goodwill.getQoQ());
    ret += Utils.NL;
    ret += String.format(" Curr Assets   -->  %s%n", totstasset);
    ret += String.format("   Cash        -->  %s%n", this.cash.getQoQ());
    ret += String.format("   ST Inv      -->  %s%n", this.stInvestments.getQoQ());
    ret += String.format("   Acct Rec    -->  %s%n", this.acctReceiveable.getQoQ());
    ret += String.format("   Inventory   -->  %s%n", this.inventory.getQoQ());
    ret += String.format("   Other       -->  %s%n", this.otherAssets.getQoQ());
    ret += String.format(" Curr Liab     -->  %s%n", totstliab);
    ret += String.format("   Acct Pay    -->  %s%n", this.acctPayable.getQoQ());
    ret += String.format("   ST Debt     -->  %s%n", this.stDebt.getQoQ());
    ret += String.format("   Other       -->  %s%n", this.otherCurrLiab.getQoQ());
    ret += String.format(" WC            -->  %s%n", totwc);
    ret += Utils.NL;
    ret += String.format(" CapEx         -->  %s%n", this.capex.getQoQ());
    ret += String.format(" R&D           -->  %s%n", this.rd.getQoQ());
    ret += Utils.NL;
    ret += String.format(" Cash Net      -->  %s%n", this.cashNet.getQoQ());
    ret += String.format("   CashFromOps -->  %s%n", this.cashFromOps.getQoQ());
    ret += String.format("   CashFromFin -->  %s%n", this.cashFromFin.getQoQ());
    ret += String.format("   CashFromInv -->  %s%n", this.cashFromInv.getQoQ());
    ret += Utils.NL;
    ret += String.format(" BVPS          -->  %s%n", this.calcTotalBvps());
    ret += String.format(" Shares        -->  %s%n", this.shares.getQoQ());

    if (this.getPrintFormat() == TotalData.printFormatLong) {
      double chg = 0.0;

//      double t1 = 11.0;
//      double t5 = 10.0;
//      chg = calcChg(t1, t5);
//      ret += "10 to 11 : " + chg;

      double pps1 = this.p0 / (this.netIncome.q1 / this.shares.q1);
      double pps5 = this.p365 / (this.netIncome.q5 / this.shares.q5);
      chg = this.calcChg(pps1, pps5);

      ret += String.format("%n Pps/Net       -->  %16.2f%17.2f   -->%13.2f%%%n", pps1, pps5, chg);

      pps1 = this.p0 / (this.cashFromOps.q1 / this.shares.q1);
      pps5 = this.p365 / (this.cashFromOps.q5 / this.shares.q5);

      chg = this.calcChg(pps1, pps5);
      ret += String.format(" Pps/CashOps   -->  %16.2f%17.2f   -->%13.2f%%%n", pps1, pps5, chg);

      chg = this.calcChg(this.p0, this.p365);
      ret += String.format("%n SPX Price     -->  %16.2f%17.2f   -->%13.2f%%%n", this.p0, this.p365, chg);

      ret += String.format("%n Knt : %d%n", this.knt);
    }

    return ret;
  }

  public void setOutdir(String outdir) {
    this.outdir = outdir;
  }

  public void setPrintFormat(int printFormat) {
    this.printFormat = printFormat;
  }

  /**
   * net.ajaskey.market.tools.SIP.sum
   *
   */
  public void sum() {

    this.acctReceiveable.dd.calculate(this.acctReceiveable);
    this.acctPayable.dd.calculate(this.acctPayable);
    this.bvps.dd.calculate(this.bvps);
    this.cash.dd.calculate(this.cash);
    this.currentAssets.dd.calculate(this.currentAssets);
    this.currLiab.dd.calculate(this.currLiab);
    this.equity.dd.calculate(this.equity);
    this.fixedAssets.dd.calculate(this.fixedAssets);
    this.goodwill.dd.calculate(this.goodwill);
    this.inventory.dd.calculate(this.inventory);
    this.liabEquity.dd.calculate(this.liabEquity);
    this.ltDebt.dd.calculate(this.ltDebt);
    this.ltInvestments.dd.calculate(this.ltInvestments);
    this.otherAssets.dd.calculate(this.otherAssets);
    this.otherCurrLiab.dd.calculate(this.otherCurrLiab);
    this.otherLtAssets.dd.calculate(this.otherLtAssets);
    this.otherLtLiab.dd.calculate(this.otherLtLiab);
    this.prefStock.dd.calculate(this.prefStock);
    this.stDebt.dd.calculate(this.stDebt);
    this.stInvestments.dd.calculate(this.stInvestments);
    this.totalAssets.dd.calculate(this.totalAssets);
    this.totalLiab.dd.calculate(this.totalLiab);

    this.sales.dd.calculate(this.sales);
    this.cogs.dd.calculate(this.cogs);
    this.grossIncome.dd.calculate(this.grossIncome);
    this.rd.dd.calculate(this.rd);
    this.depreciation.dd.calculate(this.depreciation);
    this.interestExp.dd.calculate(this.interestExp);
    this.unusualIncome.dd.calculate(this.unusualIncome);
    this.totalOpExp.dd.calculate(this.totalOpExp);
    this.interestExpNonOp.dd.calculate(this.interestExpNonOp);
    this.otherIncome.dd.calculate(this.otherIncome);
    this.pretaxIncome.dd.calculate(this.pretaxIncome);
    this.incomeTax.dd.calculate(this.incomeTax);
    this.incomeAfterTaxes.dd.calculate(this.incomeAfterTaxes);
    this.adjustments.dd.calculate(this.adjustments);
    this.incomeEps.dd.calculate(this.incomeEps);
    this.nonrecurring.dd.calculate(this.nonrecurring);
    this.netIncome.dd.calculate(this.netIncome);
    this.eps.dd.calculate(this.eps);
    this.epsContinuing.dd.calculate(this.epsContinuing);
    this.epsDiluted.dd.calculate(this.epsDiluted);
    this.epsDilCont.dd.calculate(this.epsDilCont);
    this.dividend.dd.calculate(this.dividend);

    this.cashFromOps.dd.calculate(this.cashFromOps);
    this.cashFromFin.dd.calculate(this.cashFromFin);
    this.cashFromInv.dd.calculate(this.cashFromInv);

    this.shares.dd.calculate(this.shares);

  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = "Totals ==>" + TotalData.NL;
    ret += TotalData.TAB + this.acctPayable;
    ret += TotalData.TAB + this.acctReceiveable;
    ret += TotalData.TAB + this.bvps;
    ret += TotalData.TAB + this.cash;
    ret += TotalData.TAB + this.equity;
    ret += TotalData.TAB + this.inventory;
    ret += TotalData.TAB + this.prefStock;

    ret += TotalData.TAB + this.currentAssets;
    ret += TotalData.TAB + this.fixedAssets;
    ret += TotalData.TAB + this.otherLtAssets;
    ret += TotalData.TAB + this.otherAssets;
    ret += TotalData.TAB + this.goodwill;
    ret += TotalData.TAB + this.totalAssets;

    ret += TotalData.TAB + this.stInvestments;
    ret += TotalData.TAB + this.ltInvestments;

    ret += TotalData.TAB + this.stDebt;
    ret += TotalData.TAB + this.ltDebt;

    ret += TotalData.TAB + this.currLiab;
    ret += TotalData.TAB + this.liabEquity;
    ret += TotalData.TAB + this.otherCurrLiab;
    ret += TotalData.TAB + this.otherLtLiab;
    ret += TotalData.TAB + this.totalLiab;

    ret += TotalData.TAB + this.sales;
    ret += TotalData.TAB + this.cogs;
    ret += TotalData.TAB + this.grossIncome;
    ret += TotalData.TAB + this.rd;
    ret += TotalData.TAB + this.depreciation;
    ret += TotalData.TAB + this.interestExp;
    ret += TotalData.TAB + this.otherIncome;
    ret += TotalData.TAB + this.pretaxIncome;
    ret += TotalData.TAB + this.incomeTax;
    ret += TotalData.TAB + this.incomeAfterTaxes;
    ret += TotalData.TAB + this.adjustments;
    ret += TotalData.TAB + this.incomeEps;
    ret += TotalData.TAB + this.nonrecurring;
    ret += TotalData.TAB + this.netIncome;
    ret += TotalData.TAB + this.eps;
    ret += TotalData.TAB + this.epsContinuing;
    ret += TotalData.TAB + this.epsDiluted;
    ret += TotalData.TAB + this.epsDilCont;
    ret += TotalData.TAB + this.dividend;

    ret += TotalData.TAB + this.shares;

    ret += String.format("%n%n%.2f\t%.2f\t%.2f\t--->", this.p0, this.p365, this.p730);

    return ret;
  }

  private double calcChg(double p1, double p5) {
    double chg = 0.0;
    final double ap5 = Math.abs(p5);
    if (Math.abs(ap5) > 0.0) {
      chg = (p1 - p5) / ap5 * 100.0;
    }
    return chg;
  }

  /**
   *
   * @return
   */
  private String calcTotalBvps() {

    final String fldEquity[] = this.equity.getQoQ(100.0).replace(",", "").split("\\s+");
    final String fldShares[] = this.shares.getQoQ().replace(",", "").split("\\s+");
    final String fldGoodwill[] = this.goodwill.getQoQ().replace(",", "").split("\\s+");

    final long q1e = Long.parseLong(fldEquity[1].trim());
    final long q5e = Long.parseLong(fldEquity[2].trim());

    final long q1s = Long.parseLong(fldShares[1].trim());
    final long q5s = Long.parseLong(fldShares[2].trim());

    final long q1g = Long.parseLong(fldGoodwill[1].trim()) / 2;
    final long q5g = Long.parseLong(fldGoodwill[2].trim()) / 2;

    // Subtract half of goodwill as useless equity
    long q1bvps = 0L;
    if (Math.abs(q1s) > 0) {
      q1bvps = (q1e - q1g) / q1s;
    }
    long q5bvps = 0L;
    if (Math.abs(q5s) > 0) {
      q5bvps = (q5e - q5g) / q5s;
    }
    double chg = 0.0;
    if (Math.abs(q5bvps) > 0) {
      final double r1 = q1bvps;
      final double r5 = q5bvps;
      chg = this.calcChg(r1, r5);
    }

    final double dq1 = q1bvps / 100.0;
    final double dq5 = q5bvps / 100.0;

    final String q1 = String.format("%,16.2f", dq1);
    final String q5 = String.format("%,16.2f", dq5);
    final String sRet = String.format("%s %s   --> %12.2f%%", q1, q5, chg);

    return sRet;
  }

  /**
   *
   * @return
   */
  private String calcTotalStAssets() {

    final long[] ret = { 0, 0 };

    String fld[] = this.cash.getQoQ().replace(",", "").split("\\s+");
    long q1 = Long.parseLong(fld[1].trim());
    long q5 = Long.parseLong(fld[2].trim());
    ret[0] += q1;
    ret[1] += q5;

    fld = this.stInvestments.getQoQ().replace(",", "").split("\\s+");
    q1 = Long.parseLong(fld[1].trim());
    q5 = Long.parseLong(fld[2].trim());
    ret[0] += q1;
    ret[1] += q5;

    fld = this.acctReceiveable.getQoQ().replace(",", "").split("\\s+");
    q1 = Long.parseLong(fld[1].trim());
    q5 = Long.parseLong(fld[2].trim());
    ret[0] += q1;
    ret[1] += q5;

    fld = this.inventory.getQoQ().replace(",", "").split("\\s+");
    q1 = Long.parseLong(fld[1].trim());
    q5 = Long.parseLong(fld[2].trim());
    ret[0] += q1;
    ret[1] += q5;

    fld = this.otherAssets.getQoQ().replace(",", "").split("\\s+");
    q1 = Long.parseLong(fld[1].trim());
    q5 = Long.parseLong(fld[2].trim());
    ret[0] += q1;
    ret[1] += q5;

    double chg = 0.0;
    if (Math.abs(ret[1]) > 0) {
      final double r1 = ret[0];
      final double r5 = ret[1];
      chg = this.calcChg(r1, r5);
    }
    final String q1s = String.format("%,16d", ret[0]);
    final String q5s = String.format("%,16d", ret[1]);
    final String sRet = String.format("%s %s   --> %12.2f%%", q1s, q5s, chg);

    return sRet;
  }

  /**
   *
   * @return
   */
  private String calcTotalStLiab() {

    final long[] ret = { 0, 0 };

    String fld[] = this.stDebt.getQoQ().replace(",", "").split("\\s+");
    long q1 = Long.parseLong(fld[1].trim());
    long q5 = Long.parseLong(fld[2].trim());
    ret[0] += q1;
    ret[1] += q5;

    fld = this.acctPayable.getQoQ().replace(",", "").split("\\s+");
    q1 = Long.parseLong(fld[1].trim());
    q5 = Long.parseLong(fld[2].trim());
    ret[0] += q1;
    ret[1] += q5;

    fld = this.otherCurrLiab.getQoQ().replace(",", "").split("\\s+");
    q1 = Long.parseLong(fld[1].trim());
    q5 = Long.parseLong(fld[2].trim());
    ret[0] += q1;
    ret[1] += q5;

    double chg = 0.0;
    if (Math.abs(ret[1]) > 0) {
      final double r1 = ret[0];
      final double r5 = ret[1];
      chg = this.calcChg(r1, r5);
    }
    final String q1s = String.format("%,16d", ret[0]);
    final String q5s = String.format("%,16d", ret[1]);
    final String sRet = String.format("%s %s   --> %12.2f%%", q1s, q5s, chg);

    return sRet;
  }

  /**
   *
   * @param totstasset
   * @param totstliab
   * @return
   */
  private String calcTotalWC(String totstasset, String totstliab) {

    final String fldAsset[] = totstasset.replace(",", "").split("\\s+");
    final long q1a = Long.parseLong(fldAsset[1].trim());
    final long q5a = Long.parseLong(fldAsset[2].trim());

    final String fldLiab[] = totstliab.replace(",", "").split("\\s+");
    final long q1l = Long.parseLong(fldLiab[1].trim());
    final long q5l = Long.parseLong(fldLiab[2].trim());

    final long q1diff = q1a - q1l;
    final long q5diff = q5a - q5l;

    double chg = 0.0;
    if (Math.abs(q5diff) > 0) {
      final double r1 = q1diff;
      final double r5 = q5diff;
      chg = this.calcChg(r1, r5);
    }
    final String q1s = String.format("%,16d", q1diff);
    final String q5s = String.format("%,16d", q5diff);
    final String sRet = String.format("%s %s   --> %12.2f%%", q1s, q5s, chg);

    return sRet;
  }

}
