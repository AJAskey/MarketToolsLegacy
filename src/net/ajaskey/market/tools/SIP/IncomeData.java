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

import net.ajaskey.common.Utils;

public class IncomeData {

  final private static String NL  = Utils.NL;
  final private static String TAB = "\t";

  public static IncomeData setBalanceSheetInfo(final String[] fld) {

    final IncomeData id = new IncomeData();

    id.sales.parse(fld);
    id.cogs.parse(fld);
    id.grossIncome.parse(fld);
    id.rd.parse(fld);
    id.depreciation.parse(fld);
    id.interestExp.parse(fld);
    id.unusualIncome.parse(fld);
    id.totalOpExp.parse(fld);
    id.grossOpIncome.parse(fld);
    id.interestExpNonOp.parse(fld);
    id.otherIncome.parse(fld);
    id.pretaxIncome.parse(fld);
    id.incomeTax.parse(fld);
    id.incomeAfterTaxes.parse(fld);
    id.adjustments.parse(fld);
    id.incomeEps.parse(fld);
    id.nonrecurring.parse(fld);
    id.netIncome.parse(fld);
    id.eps.parse(fld);
    id.epsContinuing.parse(fld);
    id.epsDiluted.parse(fld);
    id.epsDilCont.parse(fld);
    id.dividend.parse(fld);

    return id;

  }

  /**
   * net.ajaskey.market.tools.SIP.setIncomeData
   *
   * @param fld
   * @return
   */
  public static IncomeData setIncomeData(final String[] fld) {

    final IncomeData id = new IncomeData();

    id.sales.parse(fld);
    id.cogs.parse(fld);
    id.grossIncome.parse(fld);
    id.rd.parse(fld);
    id.depreciation.parse(fld);
    id.interestExp.parse(fld);
    id.unusualIncome.parse(fld);
    id.totalOpExp.parse(fld);
    id.grossOpIncome.parse(fld);
    id.interestExpNonOp.parse(fld);
    id.otherIncome.parse(fld);
    id.pretaxIncome.parse(fld);
    id.incomeTax.parse(fld);
    id.incomeAfterTaxes.parse(fld);
    id.adjustments.parse(fld);
    id.incomeEps.parse(fld);
    id.nonrecurring.parse(fld);
    id.netIncome.parse(fld);
    id.eps.parse(fld);
    id.epsContinuing.parse(fld);
    id.epsDiluted.parse(fld);
    id.epsDilCont.parse(fld);
    id.dividend.parse(fld);

    id.totalInterest.sum(id.interestExp);
    id.totalInterest.sum(id.interestExpNonOp);
    id.totalInterest.dd.calculate(id.totalInterest);

    return id;
  }

  public QuarterlyData adjustments;
  public QuarterlyData cogs;
  public QuarterlyData depreciation;
  public QuarterlyData dividend;
  public QuarterlyData eps;
  public QuarterlyData epsContinuing;
  public QuarterlyData epsDilCont;
  public QuarterlyData epsDiluted;
  public QuarterlyData grossIncome;
  public QuarterlyData grossOpIncome;
  public QuarterlyData incomeAfterTaxes;
  public QuarterlyData incomeEps;
  public QuarterlyData incomeTax;
  public QuarterlyData interestExp;
  public QuarterlyData interestExpNonOp;
  public QuarterlyData netIncome;
  public QuarterlyData nonrecurring;
  public QuarterlyData otherIncome;
  public QuarterlyData pretaxIncome;
  public QuarterlyData rd;

  public QuarterlyData sales;

  public QuarterlyData totalInterest;

  public QuarterlyData totalOpExp;

  public QuarterlyData unusualIncome;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public IncomeData() {

    this.sales = new QuarterlyData("sales");
    this.cogs = new QuarterlyData("cogs");
    this.grossIncome = new QuarterlyData("grossIncome");
    this.rd = new QuarterlyData("rd");
    this.depreciation = new QuarterlyData("depreciation");
    this.interestExp = new QuarterlyData("interestExp");
    this.unusualIncome = new QuarterlyData("unusualIncome");
    this.totalOpExp = new QuarterlyData("totalOpExp");
    this.grossOpIncome = new QuarterlyData("grossOpIncome");
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
    this.totalInterest = new QuarterlyData("totalInterest");
  }

  public String getQoQ() {
    String ret = "";
    ret += String.format("Sales    -->  %s%n", this.sales.getQoQ());
    ret += String.format("COGS     -->  %s%n", this.cogs.getQoQ());
    ret += String.format("GrossOp  -->  %s%n", this.grossOpIncome.getQoQ());
    ret += String.format("Net      -->  %s%n", this.netIncome.getQoQ());
    ret += String.format("EPS      -->  %s%n", this.eps.getQoQ(100.0));
    ret += String.format("Dividend -->  %s%n", this.dividend.getQoQ(100.0));
    ret += String.format("IncTax   -->  %s%n", this.incomeTax.getQoQ());
    ret += String.format("TotInt   -->  %s%n", this.totalInterest.getQoQ());

    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = "";
    ret += IncomeData.TAB + this.sales;
    ret += IncomeData.TAB + this.cogs;
    ret += IncomeData.TAB + this.grossIncome;
    ret += IncomeData.TAB + this.rd;
    ret += IncomeData.TAB + this.depreciation;
    ret += IncomeData.TAB + this.unusualIncome;

    ret += IncomeData.TAB + this.totalOpExp;
    ret += IncomeData.TAB + this.grossOpIncome;
    ret += IncomeData.TAB + this.interestExp;
    ret += IncomeData.TAB + this.interestExpNonOp;
    ret += IncomeData.TAB + this.totalInterest;
    ret += IncomeData.TAB + this.otherIncome;
    ret += IncomeData.TAB + this.pretaxIncome;
    ret += IncomeData.TAB + this.incomeTax;
    ret += IncomeData.TAB + this.incomeAfterTaxes;

    ret += IncomeData.TAB + this.adjustments;
    ret += IncomeData.TAB + this.incomeEps;

    ret += IncomeData.TAB + this.nonrecurring;
    ret += IncomeData.TAB + this.netIncome;

    ret += IncomeData.TAB + this.eps;
    ret += IncomeData.TAB + this.epsContinuing;
    ret += IncomeData.TAB + this.epsDiluted;
    ret += IncomeData.TAB + this.epsDilCont;
    ret += IncomeData.TAB + this.dividend;
    return ret;
  }
}
