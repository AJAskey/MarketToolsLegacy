
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
package net.ajaskey.market.tools.SIP.BigDB;

import java.io.FileNotFoundException;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.market.tools.SIP.BigDB.dataio.BalSheetFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.CashFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.CompanyFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.EstimateFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.IncSheetFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.SharesFileData;

/**
 * This class is used as the public static API into all other classes.
 */
public class MarketTools {

  public static double getChange(double mostRecent, double previous) {
    double ret = 0.0;
    try {
      if (previous != 0.0) {
        double chg = (mostRecent - previous) / Math.abs(previous) * 100.0;
        if (chg > 0.0) {
          ret = Math.min(999.99, chg);
        }
        else {
          ret = Math.max(-999.99, chg);
        }
      }
    }
    catch (Exception e) {
      ret = 0.0;
    }
    return ret;
  }

  public static double[] getAcctPayableQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getAcctPayableQtr();
  }

  public static double[] getAcctPayableYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getAcctPayableYr();
  }

  public static double[] getAcctRxQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getAcctRxQtr();
  }

  public static double[] getAcctRxYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getAcctRxYr();
  }

  public static double[] getAdjToIncQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getAdjToIncQtr();
  }

  public static double[] getAdjToIncYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getAdjToIncYr();
  }

  public static BalSheetFileData getBalSheetData(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData();
  }

  public static double getBeta(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -1.0;
    }
    return fd.getShareData().getBeta();
  }

  public static double[] getBvpsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getBvpsQtr();
  }

  public static double[] getBvpsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getBvpsYr();
  }

  public static double[] getCapExQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getCashData().getCapExQtr();
  }

  public static CashFileData getCashData(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getCashData();
  }

  public static double[] getCashFromFinQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getCashData().getCashFromFinQtr();
  }

  public static double[] getCashFromInvQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getCashData().getCashFromInvQtr();
  }

  public static double[] getCashFromOpsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getCashData().getCashFromOpsQtr();
  }

  public static double[] getCashQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getCashQtr();
  }

  public static double[] getCashYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getCashYr();
  }

  public static String getCity(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getCity();
  }

  public static double[] getCogsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getCogsQtr();
  }

  public static double[] getCogsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getCogsYr();
  }

  public static CompanyFileData getCompanyInfo(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getCompanyInfo();
  }

  public static String getCountry(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getCountry();
  }

  public static double[] getCurrAssetsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getCurrAssetsQtr();
  }

  public static double[] getCurrAssetsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getCurrAssetsYr();
  }

  public static DateTime getCurrFiscalYear(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return MarketTools.getEstimateData(fd).getCurrFiscalYear();
  }

  public static double[] getCurrLiabQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getCurrLiabQtr();
  }

  public static double[] getCurrLiabYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getCurrLiabYr();
  }

  public static double[] getDepreciationQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getDepreciationQtr();
  }

  public static double[] getDepreciationYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getDepreciationYr();
  }

  public static double[] getDividendQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getDividendQtr();
  }

  public static double[] getDividendYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getDividendYr();
  }

  public static double getDollar3m(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -2.0;
    }
    return fd.getShareData().getDollar3m();
  }

  public static DowEnum getDowIndex(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getCompanyInfo().getDowIndex();
  }

  public static String getDowIndexStr(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getDowIndexStr();
  }

  public static double[] getEpsContQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getEpsContQtr();
  }

  public static double[] getEpsContYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getEpsContYr();
  }

  public static double[] getEpsDilContQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getEpsDilContQtr();
  }

  public static double[] getEpsDilContYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getEpsDilContYr();
  }

  public static double[] getEpsDilQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getEpsDilQtr();
  }

  public static double[] getEpsDilYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getEpsDilYr();
  }

  public static double getEpsQ0(FieldData fd) {
    return fd.getEstimateData().getEpsQ0();
  }

  public static double getEpsQ1(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -1.0;
    }
    return MarketTools.getEstimateData(fd).getEpsQ1();
  }

  public static double[] getEpsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getEpsQtr();
  }

  public static double getEpsY0(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -1.0;
    }
    return fd.getEstimateData().getEpsY0();
  }

  public static double getEpsY1(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -2.0;
    }
    return fd.getEstimateData().getEpsY1();
  }

  public static double getEpsY2(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -2.0;
    }
    return fd.getEstimateData().getEpsY2();
  }

  public static double[] getEpsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getEpsYr();
  }

  public static double[] getEquityQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getEquityQtr();
  }

  public static double[] getEquityYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getEquityYr();
  }

  public static EstimateFileData getEstimateData(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getEstimateData();
  }

  public static ExchEnum getExchange(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getExchange();
  }

  public static double getFloatshr(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -2.0;
    }
    return fd.getShareData().getFloatshr();
  }

  /**
   *
   * Calls FieldData class to return FieldData for requested ticker from year and
   * quarter from DB file data.
   *
   * @param ticker The individual stock symbol
   * @param yr     year
   * @param qtr    quarter
   * @param ft     FiletypeEnum TEXT or BINARY
   * @return FieldData
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData#getFromDb
   */
  public static FieldData getFromDb(String ticker, int yr, int qtr, FiletypeEnum ft) {
    return FieldData.getFromDb(ticker, yr, qtr, ft);
  }

  /**
   * memory.
   *
   * @param tkr The individual stock symbol
   * @param yr  year
   * @param qtr quarter (1-4)
   * @return FieldData
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData#getFromMemory
   */
  public static FieldData getFromMemory(String tkr, int yr, int qtr) {
    return FieldData.getFromMemory(tkr, yr, qtr);
  }

  public static double[] getGoodwillQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getGoodwillQtr();
  }

  public static double[] getGoodwillYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getGoodwillYr();
  }

  public static double[] getGrossIncQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getGrossIncQtr();
  }

  public static double[] getGrossIncYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getGrossIncYr();
  }

  public static double[] getGrossOpIncQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getGrossOpIncQtr();
  }

  public static double[] getGrossOpIncYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getGrossOpIncYr();
  }

  public static double[] getIncAfterTaxQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIncAfterTaxQtr();
  }

  public static double[] getIncAfterTaxYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIncAfterTaxYr();
  }

  public static double[] getIncPrimaryEpsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIncPrimaryEpsQtr();
  }

  public static double[] getIncPrimaryEpsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIncPrimaryEpsYr();
  }

  public static IncSheetFileData getIncSheetData(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData();
  }

  public static double[] getIncTaxQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIncTaxQtr();
  }

  public static double[] getIncTaxYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIncTaxYr();
  }

  public static String getIndustry(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getIndustry();
  }

  public static int getInsiderBuys(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInsiderBuys();
  }

  public static int getInsiderBuyShrs(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInsiderBuyShrs();
  }

  public static double getInsiderNetPercentOutstanding(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -2.0;
    }
    return fd.getShareData().getInsiderNetPercentOutstanding();
  }

  public static int getInsiderNetTrades(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInsiderNetTrades();
  }

  public static double getInsiderOwnership(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInsiderOwnership();
  }

  public static int getInsiderSells(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInsiderSells();
  }

  public static int getInsiderSellShrs(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInsiderSellShrs();
  }

  public static int getInstBuyShrs(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInstBuyShrs();
  }

  public static double getInstOwnership(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -2.0;
    }
    return fd.getShareData().getInstOwnership();
  }

  public static int getInstSellShrs(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInstSellShrs();
  }

  public static int getInstShareholders(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getShareData().getInstShareholders();
  }

  public static double[] getIntExpNonOpQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIntExpNonOpQtr();
  }

  public static double[] getIntExpNonOpYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIntExpNonOpYr();
  }

  public static double[] getIntExpQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIntExpQtr();
  }

  public static double[] getIntExpYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getIntExpYr();
  }

  public static double[] getInventoryQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getInventoryQtr();
  }

  public static double[] getInventoryYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getInventoryYr();
  }

  public static DateTime getLatestQtrEps(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getEstimateData().getLatestQtrEps();
  }

  public static double[] getLiabEquityQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getLiabEquityQtr();
  }

  public static double[] getLiabEquityYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getLiabEquityYr();
  }

  /**
   * Calls FieldData class to return List of FieldData for requested ticker list
   * for year and quarter from internal memory.
   *
   * @param tList The list of individual stock symbols
   * @param yr    year
   * @param qtr   quarter (1-4)
   * @return List of FieldData
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData#getListFromMemory
   */
  public static List<FieldData> getListFromMemory(List<String> tList, int yr, int qtr) {
    return FieldData.getListFromMemory(tList, yr, qtr);
  }

  public static double[] getLtDebtQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getLtDebtQtr();
  }

  public static double[] getLtDebtYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getLtDebtYr();
  }

  public static double[] getLtInvestQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getLtInvestQtr();
  }

  public static double[] getLtInvestYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getLtInvestYr();
  }

  public static double getMktCap(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -2.0;
    }
    return fd.getShareData().getMktCap();
  }

  public static String getName(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getName();
  }

  public static double[] getNetFixedAssetsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getNetFixedAssetsQtr();
  }

  public static double[] getNetFixedAssetsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getNetFixedAssetsYr();
  }

  public static double[] getNetIncQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getNetIncQtr();
  }

  public static double[] getNetIncYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getNetIncYr();
  }

  public static double[] getNonrecurringItemsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getNonrecurringItemsQtr();
  }

  public static double[] getNonrecurringItemsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getNonrecurringItemsYr();
  }

  public static int getNumEmployees(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getCompanyInfo().getNumEmployees();
  }

  public static double[] getOtherCurrAssetsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getOtherCurrAssetsQtr();
  }

  public static double[] getOtherCurrAssetsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getOtherCurrAssetsYr();
  }

  public static double[] getOtherCurrLiabQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getOtherCurrLiabQtr();
  }

  public static double[] getOtherCurrLiabYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getOtherCurrLiabYr();
  }

  public static double[] getOtherIncQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getOtherIncQtr();
  }

  public static double[] getOtherIncYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getOtherIncYr();
  }

  public static double[] getOtherLtAssetsQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getOtherLtAssetsQtr();
  }

  public static double[] getOtherLtAssetsYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getOtherLtAssetsYr();
  }

  public static double[] getOtherLtLiabQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getOtherLtLiabQtr();
  }

  public static double[] getOtherLtLiabYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getOtherLtLiabYr();
  }

  public static String getPhone(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getPhone();
  }

  public static double[] getPrefStockQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getPrefStockQtr();
  }

  public static double[] getPrefStockYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getPrefStockYr();
  }

  public static double[] getPreTaxIncQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getPreTaxIncQtr();
  }

  public static double[] getPreTaxIncYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getPreTaxIncYr();
  }

  public static double getPrice(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -1.0;
    }
    return fd.getShareData().getPrice();
  }

  public static double getPrice52hi(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -1.0;
    }
    return fd.getShareData().getPrice52hi();
  }

  public static double getPrice52lo(FieldData fd) {
    if (fd == null) {
      return -1.0;
    }
    else if (!fd.isDataValid()) {
      return -1.0;
    }
    return fd.getShareData().getPrice52lo();
  }

  public static double[] getPricesQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getCompanyInfo().getPriceQtr();
  }

  public static int getQuarter(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getQuarter();
  }

  public static double[] getRdQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getRdQtr();
  }

  public static double[] getRdYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getRdYr();
  }

  public static double[] getSalesQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getSalesQtr();
  }

  public static double[] getSalesYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getSalesYr();
  }

  public static String getSector(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getSector();
  }

  public static SharesFileData getShares(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getShareData();
  }

  public static double[] getSharesQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getShareData().getSharesQtr();
  }

  public static double[] getSharesYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getShareData().getSharesYr();
  }

  public static String getSic(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getSic();
  }

  public static SnpEnum getSnpIndex(FieldData fd) {
    if (fd == null) {
      return SnpEnum.NONE;
    }
    else if (!fd.isDataValid()) {
      return SnpEnum.NONE;
    }
    return fd.getCompanyInfo().getSnpIndex();
  }

  public static String getSnpIndexStr(FieldData fd) {
    if (fd == null) {
      return "NONE";
    }
    else if (!fd.isDataValid()) {
      return "NONE";
    }
    return fd.getCompanyInfo().getSnpIndexStr();
  }

  public static String getState(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getState();
  }

  public static double[] getStDebtQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getStDebtQtr();
  }

  public static double[] getStDebtYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getStDebtYr();
  }

  public static double[] getStInvestQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getStInvestQtr();
  }

  public static double[] getStInvestYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getBalSheetData().getStInvestYr();
  }

  public static String getStreet(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getStreet();
  }

  public static String getTicker(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getTicker();
  }

  public static double[] getTotalOpExpQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getTotalOpExpQtr();
  }

  public static double[] getTotalOpExpYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getTotalOpExpYr();
  }

  public static double[] getUnusualIncQtr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getUnusualIncQtr();
  }

  public static double[] getUnusualIncYr(FieldData fd) {
    if (fd == null) {
      return null;
    }
    else if (!fd.isDataValid()) {
      return null;
    }
    return fd.getIncSheetData().getUnusualIncYr();
  }

  public static long getVolume10d(FieldData fd) {
    if (fd == null) {
      return -1L;
    }
    else if (!fd.isDataValid()) {
      return -2L;
    }
    return fd.getShareData().getVolume10d();
  }

  public static long getVolumeMonth3m(FieldData fd) {
    if (fd == null) {
      return -1L;
    }
    else if (!fd.isDataValid()) {
      return -2L;
    }
    return fd.getShareData().getVolumeMonth3m();
  }

  public static String getWeb(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getWeb();
  }

  public static int getYear(FieldData fd) {
    if (fd == null) {
      return -1;
    }
    else if (!fd.isDataValid()) {
      return -2;
    }
    return fd.getYear();
  }

  public static String getZip(FieldData fd) {
    if (fd == null) {
      return "";
    }
    else if (!fd.isDataValid()) {
      return "";
    }
    return fd.getCompanyInfo().getZip();
  }

  /**
   * Calls FieldData class to read SIP tab separated data files. Stores the data
   * in array for later use.
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   * @param ft  FiletypeEnum
   * @throws FileNotFoundException Requested file not found
   * @return FALSE if error, TRUE otherwise
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData#parseSipData
   */
  public static boolean parseSipData(int yr, int qtr, FiletypeEnum ft, boolean overwrite) {
    return FieldData.parseSipData(yr, qtr, ft, overwrite);
  }

  /**
   * Calls FieldData class to set internal memory to data from firstYear to
   * endYear.
   *
   * @param firstYear First full year of data
   * @param endYear   Last full year of data
   * @param ft        FiletypeEnum BIG_BINARY valid
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData#setMemory
   */
  public static void setMemory(int firstYear, int endYear, FiletypeEnum ft) {
    FieldData.setMemory(firstYear, endYear, ft);
  }

  /**
   * Calls FieldData class to set internal memory to request year and quarter.
   *
   * @param yr  year
   * @param qtr quarter (1-4)
   * @param ft  FiletypeEnum BINARY or TEXT valid
   *
   * @see net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData#setQMemory
   */
  public static void setQMemory(int yr, int qtr, FiletypeEnum ft) {
    FieldData.setQMemory(yr, qtr, ft);
  }

}
