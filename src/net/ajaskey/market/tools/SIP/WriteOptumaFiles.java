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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.optuma.PriceData;

public class WriteOptumaFiles {

  public static List<DataSet>   accRx           = new ArrayList<>();
  public static List<DataSet>   accTx           = new ArrayList<>();
  public static List<DataSet>   assets          = new ArrayList<>();
  public static List<DataSet>   bookValue       = new ArrayList<>();
  public static List<DataSet>   capEx           = new ArrayList<>();
  public static List<DataSet>   cash            = new ArrayList<>();
  public static List<DataSet>   cashFin         = new ArrayList<>();
  public static List<DataSet>   cashInv         = new ArrayList<>();
  public static List<DataSet>   cashOps         = new ArrayList<>();
  public static List<DataSet>   dividend        = new ArrayList<>();
  public static List<DataSet>   ebit            = new ArrayList<>();
  public static List<DataSet>   enterpriseValue = new ArrayList<>();
  public static List<DataSet>   equity          = new ArrayList<>();
  public static List<DataSet>   fcf             = new ArrayList<>();
  public static List<DataSet>   goodwill        = new ArrayList<>();
  public static List<DataSet>   incomeEps       = new ArrayList<>();
  public static List<PriceData> indexPrices     = null;
  public static List<DataSet>   interest        = new ArrayList<>();
  public static List<DataSet>   inventoryValue  = new ArrayList<>();
  public static List<DataSet>   liabilities     = new ArrayList<>();
  public static List<DataSet>   ltDebt          = new ArrayList<>();
  public static List<DataSet>   prices          = new ArrayList<>();
  public static List<DataSet>   resDev          = new ArrayList<>();
  public static List<DataSet>   sales           = new ArrayList<>();
  public static List<DataSet>   shares          = new ArrayList<>();

  public static List<DataSet> taxes = new ArrayList<>();

  public static void processData(final List<CompanyData> spxList) throws IOException, ParseException {

    final String index = "500";

    final DataSet totSales = DataSet.sum(WriteOptumaFiles.sales, index);
    final DataSet totEbit = DataSet.sum(WriteOptumaFiles.ebit, index);
    final DataSet totTax = DataSet.sum(WriteOptumaFiles.taxes, index);
    final DataSet totIncome = DataSet.sum(WriteOptumaFiles.incomeEps, index);
    final DataSet totCops = DataSet.sum(WriteOptumaFiles.cashOps, index);
    final DataSet totCfin = DataSet.sum(WriteOptumaFiles.cashFin, index);
    final DataSet totCinv = DataSet.sum(WriteOptumaFiles.cashInv, index);
    final DataSet totShr = DataSet.sum(WriteOptumaFiles.shares, index);
    final DataSet totCash = DataSet.sum(WriteOptumaFiles.cash, index);
    final DataSet totAssets = DataSet.sum(WriteOptumaFiles.assets, index);
    final DataSet totLiab = DataSet.sum(WriteOptumaFiles.liabilities, index);
    final DataSet totAccRx = DataSet.sum(WriteOptumaFiles.accRx, index);
    final DataSet totAccTx = DataSet.sum(WriteOptumaFiles.accTx, index);
    final DataSet totGoodwill = DataSet.sum(WriteOptumaFiles.goodwill, index);
    final DataSet totLtDebt = DataSet.sum(WriteOptumaFiles.ltDebt, index);
    final DataSet totCapEx = DataSet.sum(WriteOptumaFiles.capEx, index);
    final DataSet totEquity = DataSet.sum(WriteOptumaFiles.equity, index);
    final DataSet totInterest = DataSet.sum(WriteOptumaFiles.interest, index);
    final DataSet totResDev = DataSet.sum(WriteOptumaFiles.resDev, index);
    final DataSet totInventory = DataSet.sum(WriteOptumaFiles.inventoryValue, index);
    final DataSet totEnterprise = DataSet.sum(WriteOptumaFiles.enterpriseValue, index);
    final DataSet totFcf = DataSet.sum(WriteOptumaFiles.fcf, index);

    String prefix = "";

    WriteOptumaFiles.indexPrices = PriceData.getData("SP500");
    prefix = "SPX";

    final int companyKnt = spxList.size();

    final DateSet dates = new DateSet(WriteOptumaFiles.indexPrices);
    final DataSet dsPrices = new DataSet(dates, prefix);

    final List<DataSet> divDollar = new ArrayList<>();
    for (int i = 0; i < companyKnt; i++) {
      final DataSet ds = DataSet.mult(WriteOptumaFiles.dividend.get(i), WriteOptumaFiles.shares.get(i));
      divDollar.add(ds);
    }
    final DataSet totDivDollar = DataSet.sum(divDollar, index);

    final List<DataSet> bvDollar = new ArrayList<>();
    for (int i = 0; i < companyKnt; i++) {
      final DataSet ds = DataSet.mult(WriteOptumaFiles.bookValue.get(i), WriteOptumaFiles.shares.get(i));
      bvDollar.add(ds);
    }
    final DataSet totBvDollar = DataSet.sum(bvDollar, index);

    final DataSet totBVminusGW = DataSet.sub(totBvDollar, totGoodwill);

    final List<DataSet> mcap = new ArrayList<>();
    for (int i = 0; i < companyKnt; i++) {
      final DataSet ds = DataSet.mult(WriteOptumaFiles.prices.get(i), WriteOptumaFiles.shares.get(i));
      mcap.add(ds);
    }
    final DataSet totMktCap = DataSet.sum(mcap, index);
    totMktCap.mode = DataSet.dMode.SEQUENTIAL;

    WriteOptumaFiles.write(totSales, prefix + " Sales v4", dates, false);
    WriteOptumaFiles.write(totEbit, prefix + " EBIT v4", dates, false);
    WriteOptumaFiles.write(totTax, prefix + " Taxes v4", dates, false);
    WriteOptumaFiles.write(totIncome, prefix + " Income for EPS v4", dates, false);
    WriteOptumaFiles.write(totCops, prefix + " Cash from Operations v4", dates, false);
    WriteOptumaFiles.write(totCfin, prefix + " Cash from Financing v4", dates, false);
    WriteOptumaFiles.write(totCinv, prefix + " Cash from Investing v4", dates, false);
    WriteOptumaFiles.write(totDivDollar, prefix + " Dividends v4", dates, false);
    WriteOptumaFiles.write(totShr, prefix + " Shares v4", dates, false);
    WriteOptumaFiles.write(totCash, prefix + " Cash v4", dates, false);
    WriteOptumaFiles.write(totAssets, prefix + " Assets v4", dates, false);
    WriteOptumaFiles.write(totLiab, prefix + " Liabilities v4", dates, false);
    WriteOptumaFiles.write(totAccRx, prefix + " Accounts Receivable v4", dates, false);
    WriteOptumaFiles.write(totAccTx, prefix + " Accounts Payable v4", dates, false);
    WriteOptumaFiles.write(totGoodwill, prefix + " Goodwill v4", dates, false);
    WriteOptumaFiles.write(totLtDebt, prefix + " LT Debt v4", dates, false);
    WriteOptumaFiles.write(totCapEx, prefix + " CapEx v4", dates, false);
    WriteOptumaFiles.write(totEquity, prefix + " Common Equity v4", dates, false);
    WriteOptumaFiles.write(totInterest, prefix + " Interest Paid v4", dates, false);
    WriteOptumaFiles.write(totResDev, prefix + " Research and Development v4", dates, false);
    WriteOptumaFiles.write(totBvDollar, prefix + " Book Value v4", dates, false);
    WriteOptumaFiles.write(totBVminusGW, prefix + " Book Value less Goodwill v4", dates, false);
    WriteOptumaFiles.write(totMktCap, prefix + " Market Cap v4", dates, false);
    WriteOptumaFiles.write(totInventory, prefix + " Inventory v4", dates, false);
    WriteOptumaFiles.write(totEnterprise, prefix + " Enterprise v4", dates, false);
    WriteOptumaFiles.write(totFcf, prefix + " FCFps v4", dates, false);

    // -----------------------------------------

    final DataSet totMktCapShr = DataSet.ratio(totMktCap, totShr);
    WriteOptumaFiles.write(totMktCapShr, prefix + " Market Cap to Shares v4", dates, false);

    final DataSet totMargin = DataSet.scale(DataSet.ratio(totIncome, totSales), 100.0);
    WriteOptumaFiles.write(totMargin, prefix + " Margin v4", dates, false);

    final DataSet totROE = DataSet.scale(DataSet.ratio(totIncome, totEquity), 100.0);
    WriteOptumaFiles.write(totROE, prefix + " ROE v4", dates, false);

    final DataSet totTaxMargin = DataSet.scale(DataSet.ratio(totTax, totSales), 100.0);
    WriteOptumaFiles.write(totTaxMargin, prefix + " Tax Margin v4", dates, false);

    final DataSet totBVtoCap = DataSet.scale(DataSet.ratio(totBvDollar, totMktCap), 100.0);
    WriteOptumaFiles.write(totBVtoCap, prefix + " BV over Market Cap v4", dates, false);

    final DataSet totBVmGWtoCap = DataSet.scale(DataSet.ratio(totBVminusGW, totMktCap), 100.0);
    WriteOptumaFiles.write(totBVmGWtoCap, prefix + " BV Minus Goodwill over Market Cap v4", dates, false);

    final DataSet totGWtoAsset = DataSet.scale(DataSet.ratio(totGoodwill, totAssets), 100.0);
    WriteOptumaFiles.write(totGWtoAsset, prefix + " Goodwill over Assets v4", dates, false);

    WriteOptumaFiles.writePriceToDate(dates, totSales, prefix + " Price to Sales v4", 100000000000.0);
    WriteOptumaFiles.writePriceToDate(dates, totIncome, prefix + " Price to Income v4", 10000000000.0);
    WriteOptumaFiles.writePriceToDate(dates, totCash, prefix + " Price to Cash v4", 10000000000.0);
    WriteOptumaFiles.writePriceToDate(dates, totBvDollar, prefix + " Price to BV v4", 100000000000.0);

    WriteOptumaFiles.writeEps(totIncome, totShr, prefix + " EPS v4", prefix + " EPS Annual v4", prefix + " PE v4", dates, dsPrices);

  }

  /**
   * net.ajaskey.market.tools.SIP.write
   *
   * @param totSales
   * @param string
   * @param dates
   * @param b
   */
  private static void write(final net.ajaskey.market.tools.SIP.DataSet totSales, final String string, final DateSet dates, final boolean b) {

    // TODO Auto-generated method stub

  }

  /**
   * net.ajaskey.market.tools.SIP.writeEps
   *
   * @param totIncome
   * @param totShr
   * @param string
   * @param string2
   * @param string3
   * @param dates
   * @param dsPrices
   */
  private static void writeEps(final net.ajaskey.market.tools.SIP.DataSet totIncome, final net.ajaskey.market.tools.SIP.DataSet totShr,
      final String string, final String string2, final String string3, final DateSet dates, final net.ajaskey.market.tools.SIP.DataSet dsPrices) {

    // TODO Auto-generated method stub

  }

  /**
   * net.ajaskey.market.tools.SIP.writePriceToDate
   *
   * @param dates
   * @param totSales
   * @param string
   * @param d
   */
  private static void writePriceToDate(final DateSet dates, final net.ajaskey.market.tools.SIP.DataSet totSales, final String string,
      final double d) {

    // TODO Auto-generated method stub

  }

}
