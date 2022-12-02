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
package net.ajaskey.market.tools.SIP.BigDB.derived;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.DowEnum;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.Globals;
import net.ajaskey.market.tools.SIP.BigDB.MarketTools;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;
import net.ajaskey.market.tools.SIP.BigDB.collation.CompanySummary;
import net.ajaskey.market.tools.SIP.BigDB.collation.QuarterlyDouble;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.reports.WriteCompanyData;

/**
 * This class contains data and methods used to aggregate financial and price
 * data.
 */
public class CompanyDerived {

  public static final double MILLION = 1000000.0;

  /**
   *
   * @param yr
   * @param qtr
   * @return
   */
  public static List<FieldData> getFieldData(int yr, int qtr) {
    return Globals.getQFromMemory(yr, qtr);
  }

  /**
   * @param tickers
   * @param yr
   * @param qtr
   * @return
   */
  public static List<FieldData> getFieldData(List<String> tickers, int yr, int qtr) {
    return Globals.getQFromMemory(tickers, yr, qtr);
  }

  /**
   *
   * @param dList
   * @return
   */
  public static Double[] getTotalMarketCap(List<CompanyDerived> dList) {
    final Double[] ret = new Double[9];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = 0.0;
    }
    for (final CompanyDerived cdr : dList) {
      for (int i = 0; i < ret.length; i++) {
        ret[i] += cdr.getMarketCapQdata().get(i);
      }
    }
    return ret;
  }

  /**
   *
   * @param dList
   * @return
   */
  public static Double[] getTotalShares(List<CompanyDerived> dList) {
    final Double[] ret = new Double[9];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = 0.0;
    }
    for (final CompanyDerived cdr : dList) {
      for (int i = 0; i < ret.length; i++) {
        ret[i] += cdr.getSharesQdata().get(i);
      }
    }
    return ret;
  }

  /**
   *
   * @param yr  year
   * @param qtr quarter
   * @param ft  TEXT, BINARY, BIG_BINARY
   */
  public static void loadDb(int yr, int qtr, FiletypeEnum ft) {
    FieldData.setQMemory(yr, qtr, FiletypeEnum.BIG_BINARY);
  }

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(final String[] args) throws FileNotFoundException {

    System.out.println("CompanyDerived...");

    final int year = 2021;
    final int qtr = 4;
    final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;

    MarketTools.parseSipData(year, qtr, ft, false);
    CompanyDerived.loadDb(year, qtr, ft);

    final List<String> spxList = CompanySummary.getSnp(SnpEnum.SP500, year, qtr);
    List<FieldData> fdList = CompanyDerived.getFieldData(spxList, year, qtr);

    Utils.makeDir("sipout");
//    final String fname = String.format("sipout/Companies-%dQ%d", year, qtr);
//
//    final List<CompanyDerived> agList = CompanyDerived.processList(fdList);
//    CompanyDerived.write(fname, agList, false);

    final List<String> over10List = CompanySummary.get(year, qtr, SnpEnum.NONE, DowEnum.NONE, ExchEnum.MAJOR, 10.0, 100000L);

    // TextUtils.print(over10List);
    // System.out.println(over10List.size());

    fdList = CompanyDerived.getFieldData(over10List, year, qtr);
    final List<CompanyDerived> ag10List = CompanyDerived.processList(fdList);
    CompanyDerived.write("sipout/over10stocks", ag10List, false);

//    WriteCompanyData.writeEstimates("sipout/estimates.csv", agList);

//    final List<String> zList = CompanySummary.get(year, qtr, SnpEnum.NONE, DowEnum.NONE, ExchEnum.NONE, 20.0, 100000);
//    fdList = CompanyDerived.getFieldData(zList, year, qtr);
//
//    final List<CompanyDerived> zombieList = CompanyDerived.processZombies(fdList);
//    CompanyDerived.write("sipout/zombies-new", zombieList, true);

    System.out.println("Done.");

  }

  /**
   * Converts all in FieldData list to a CompanyDerived list
   *
   * @param fdList List of FieldData
   * @return List of CompanyDerived
   */
  public static List<CompanyDerived> processList(List<FieldData> fdList) {

    final List<CompanyDerived> agList = new ArrayList<>();

    for (final FieldData fd : fdList) {

      final CompanyDerived ca = new CompanyDerived(fd);
      if (ca.valid) {
        agList.add(ca);
      }
    }
    return agList;
  }

  /**
   * Outputs formatted values for all entries in the static agList
   *
   * @param fname Name of output file
   * @throws FileNotFoundException
   */
  public static void write(String fname, List<CompanyDerived> agList, boolean zombie) throws FileNotFoundException {

    System.out.println("Writing Fundamental Report to : " + fname);

    Utils.makeDir("out");

    final String fname1 = String.format("%s.txt", fname);
    final String fnameSnap = String.format("%s-snap.txt", fname);

    try (PrintWriter pw = new PrintWriter(fname1); PrintWriter pwSnap = new PrintWriter(fnameSnap)) {

      WriteCompanyData.writeHeader(pw);

      for (final CompanyDerived cdr : agList) {

        WriteCompanyData.writeCompanyInfo(pw, cdr);
        WriteCompanyData.writeCompanyInfo(pwSnap, cdr);

        WriteCompanyData.write(pw, cdr);

        WriteCompanyData.writeQuarterly(pw, cdr);
        WriteCompanyData.writeQuarterly(pwSnap, cdr);

        pw.println();
        pwSnap.println();

      }

    }
  }

  @Override
  public String toString() {
    String ret = String.format("%s", this.getFd().getTicker());
    return ret;
  }

  private QuarterlyDouble acctPayableQdata;
  private QuarterlyDouble acctRxQdata;
  private QuarterlyDouble adjToIncQdata;
  private QuarterlyDouble bvpsQdata;
  private QuarterlyDouble capExQdata;
  private QuarterlyDouble cashflowQdata;
  private QuarterlyDouble cashFromFinQdata;
  private QuarterlyDouble cashFromInvQdata;
  private QuarterlyDouble cashFromOpsQdata;
  private QuarterlyDouble cashQdata;
  private QuarterlyDouble cogsQdata;
  private QuarterlyDouble currAssetsQdata;
  private QuarterlyDouble currentRatioQdata;
  private double          currInterestRate;
  private QuarterlyDouble currLiabQdata;
  private QuarterlyDouble depreciationQdata;
  private QuarterlyDouble divCostQdata;
  private QuarterlyDouble dividendQdata;
  private QuarterlyDouble epsContQdata;
  private QuarterlyDouble epsDilContQdata;
  private QuarterlyDouble epsDilQdata;
  private double          epsEstQ0;
  private double          epsEstQ0Growth;
  private double          epsEstY1;
  private double          epsEstY2;
  private QuarterlyDouble epsQdata;
  private QuarterlyDouble equityQdata;
  private QuarterlyDouble fcfQdata;
  private FieldData       fd;
  private QuarterlyDouble goodwillQdata;
  private QuarterlyDouble grossIncQdata;
  private QuarterlyDouble grossOpIncQdata;
  private QuarterlyDouble incAfterTaxQdata;
  private QuarterlyDouble incPrimaryEpsQdata;
  private QuarterlyDouble incTaxQdata;
  private QuarterlyDouble intExpNonOpQdata;
  private QuarterlyDouble intExpQdata;
  private QuarterlyDouble intTotQdata;
  private QuarterlyDouble inventoryQdata;
  private QuarterlyDouble liabEquityQdata;
  private QuarterlyDouble ltDebtQdata;
  private QuarterlyDouble ltInvestQdata;
  private QuarterlyDouble marketCapQdata;
  private QuarterlyDouble netcashflowQdata;
  private QuarterlyDouble netFixedAssetsQdata;
  private QuarterlyDouble netIncQdata;
  private QuarterlyDouble netMarginQdata;
  private QuarterlyDouble nonrecurringItemsQdata;
  private QuarterlyDouble opMarginQdata;
  private QuarterlyDouble otherCurrAssetsQdata;
  private QuarterlyDouble otherCurrLiabQdata;
  private QuarterlyDouble otherIncQdata;
  private QuarterlyDouble otherLtAssetsQdata;
  private QuarterlyDouble otherLtLiabQdata;
  private QuarterlyDouble peQdata;
  private QuarterlyDouble prefStockQdata;
  private QuarterlyDouble preTaxIncQdata;
  private double          priceChg3;
  private double          priceChg7;
  private QuarterlyDouble pricesQdata;
  private double          q0EstGrowth;
  private int             quarter;
  private QuarterlyDouble rdQdata;
  private QuarterlyDouble roeQdata;
  private double          rs;
  private QuarterlyDouble salesQdata;
  private QuarterlyDouble sharesQdata;
  private QuarterlyDouble stDebtQdata;
  private QuarterlyDouble stInvestQdata;
  private QuarterlyDouble tanAssetsQdata;
  private QuarterlyDouble totalAssetsQdata;
  private QuarterlyDouble totalLiabQdata;
  private QuarterlyDouble totalOpExpQdata;
  private QuarterlyDouble unusualIncQdata;
  private boolean         valid;
  private QuarterlyDouble wcfcfQdata;
  private QuarterlyDouble workingCapitalQdata;
  private double          y1EstGrowth;
  private double          y2EstGrowth;
  private int             year;
  private ZData           zdata;
  private String          zState;

  /**
   * Constructor
   *
   * @param fd FieldData
   */
  public CompanyDerived(FieldData fd) {

    try {
      if (fd != null) {

        // System.out.println("Setting fd : " + fd.getTicker());
        this.fd = fd;

        this.year = fd.getYear();
        this.quarter = fd.getQuarter();

        this.sharesQdata = new QuarterlyDouble(MarketTools.getSharesQtr(fd));
        this.pricesQdata = new QuarterlyDouble(MarketTools.getPricesQtr(fd));

        this.adjToIncQdata = new QuarterlyDouble(MarketTools.getAdjToIncQtr(fd));
        this.cogsQdata = new QuarterlyDouble(MarketTools.getCogsQtr(fd));
        this.depreciationQdata = new QuarterlyDouble(MarketTools.getDepreciationQtr(fd));
        this.dividendQdata = new QuarterlyDouble(MarketTools.getDividendQtr(fd));
        this.epsEstQ0 = fd.getEstimateData().getEpsQ0();
        this.epsEstY1 = fd.getEstimateData().getEpsY1();
        this.epsEstY2 = fd.getEstimateData().getEpsY2();

        this.epsContQdata = new QuarterlyDouble(MarketTools.getEpsContQtr(fd));
        this.epsDilContQdata = new QuarterlyDouble(MarketTools.getEpsDilContQtr(fd));
        this.epsDilQdata = new QuarterlyDouble(MarketTools.getEpsDilQtr(fd));
        this.epsQdata = new QuarterlyDouble(MarketTools.getEpsQtr(fd));
        this.grossIncQdata = new QuarterlyDouble(MarketTools.getGrossIncQtr(fd));
        this.grossOpIncQdata = new QuarterlyDouble(MarketTools.getGrossOpIncQtr(fd));
        this.incAfterTaxQdata = new QuarterlyDouble(MarketTools.getIncAfterTaxQtr(fd));
        this.incPrimaryEpsQdata = new QuarterlyDouble(MarketTools.getIncPrimaryEpsQtr(fd));
        this.incTaxQdata = new QuarterlyDouble(MarketTools.getIncTaxQtr(fd));
        this.intExpNonOpQdata = new QuarterlyDouble(MarketTools.getIntExpNonOpQtr(fd));
        this.intExpQdata = new QuarterlyDouble(MarketTools.getIntExpQtr(fd));
        this.netIncQdata = new QuarterlyDouble(MarketTools.getNetIncQtr(fd));
        this.nonrecurringItemsQdata = new QuarterlyDouble(MarketTools.getNonrecurringItemsQtr(fd));
        this.otherIncQdata = new QuarterlyDouble(MarketTools.getOtherIncQtr(fd));
        this.preTaxIncQdata = new QuarterlyDouble(MarketTools.getPreTaxIncQtr(fd));
        this.rdQdata = new QuarterlyDouble(MarketTools.getRdQtr(fd));
        this.salesQdata = new QuarterlyDouble(MarketTools.getSalesQtr(fd));
        this.totalOpExpQdata = new QuarterlyDouble(MarketTools.getTotalOpExpQtr(fd));
        this.unusualIncQdata = new QuarterlyDouble(MarketTools.getUnusualIncQtr(fd));

        this.acctPayableQdata = new QuarterlyDouble(MarketTools.getAcctPayableQtr(fd));
        this.acctRxQdata = new QuarterlyDouble(MarketTools.getAcctRxQtr(fd));
        this.bvpsQdata = new QuarterlyDouble(MarketTools.getBvpsQtr(fd));
        this.cashQdata = new QuarterlyDouble(MarketTools.getCashQtr(fd));
        this.currAssetsQdata = new QuarterlyDouble(MarketTools.getCurrAssetsQtr(fd));
        this.currLiabQdata = new QuarterlyDouble(MarketTools.getCurrLiabQtr(fd));
        this.equityQdata = new QuarterlyDouble(MarketTools.getEquityQtr(fd));
        this.goodwillQdata = new QuarterlyDouble(MarketTools.getGoodwillQtr(fd));
        this.inventoryQdata = new QuarterlyDouble(MarketTools.getInventoryQtr(fd));
        this.liabEquityQdata = new QuarterlyDouble(MarketTools.getLiabEquityQtr(fd));
        this.ltDebtQdata = new QuarterlyDouble(MarketTools.getLtDebtQtr(fd));
        this.ltInvestQdata = new QuarterlyDouble(MarketTools.getLtInvestQtr(fd));
        this.netFixedAssetsQdata = new QuarterlyDouble(MarketTools.getNetFixedAssetsQtr(fd));
        this.otherCurrAssetsQdata = new QuarterlyDouble(MarketTools.getOtherCurrAssetsQtr(fd));
        this.otherCurrLiabQdata = new QuarterlyDouble(MarketTools.getOtherCurrLiabQtr(fd));
        this.otherLtAssetsQdata = new QuarterlyDouble(MarketTools.getOtherLtAssetsQtr(fd));
        this.otherLtLiabQdata = new QuarterlyDouble(MarketTools.getOtherLtLiabQtr(fd));
        this.prefStockQdata = new QuarterlyDouble(MarketTools.getPrefStockQtr(fd));
        this.stDebtQdata = new QuarterlyDouble(MarketTools.getStDebtQtr(fd));
        this.stInvestQdata = new QuarterlyDouble(MarketTools.getStInvestQtr(fd));

        this.capExQdata = new QuarterlyDouble(MarketTools.getCapExQtr(fd));
        this.cashFromFinQdata = new QuarterlyDouble(MarketTools.getCashFromFinQtr(fd));
        this.cashFromInvQdata = new QuarterlyDouble(MarketTools.getCashFromInvQtr(fd));
        this.cashFromOpsQdata = new QuarterlyDouble(MarketTools.getCashFromOpsQtr(fd));

        this.derived();

        this.valid = true;
      }
    }
    catch (final Exception e) {
      this.valid = false;
      e.printStackTrace();
    }

  }

  public QuarterlyDouble getAcctPayableQdata() {
    return this.acctPayableQdata;
  }

  public QuarterlyDouble getAcctRxQdata() {
    return this.acctRxQdata;
  }

  public QuarterlyDouble getAdjToIncQdata() {
    return this.adjToIncQdata;
  }

  public QuarterlyDouble getBvpsQdata() {
    return this.bvpsQdata;
  }

  public QuarterlyDouble getCapExQdata() {
    return this.capExQdata;
  }

  public QuarterlyDouble getCashflowQdata() {
    return this.cashflowQdata;
  }

  public QuarterlyDouble getCashFromFinQdata() {
    return this.cashFromFinQdata;
  }

  public QuarterlyDouble getCashFromInvQdata() {
    return this.cashFromInvQdata;
  }

  public QuarterlyDouble getCashFromOpsQdata() {
    return this.cashFromOpsQdata;
  }

  public QuarterlyDouble getCashQdata() {
    return this.cashQdata;
  }

  public QuarterlyDouble getCogsQdata() {
    return this.cogsQdata;
  }

  public QuarterlyDouble getCurrAssetsQdata() {
    return this.currAssetsQdata;
  }

  public QuarterlyDouble getCurrentRatioQdata() {
    return this.currentRatioQdata;
  }

  public double getCurrInterestRate() {
    return this.currInterestRate;
  }

  public QuarterlyDouble getCurrLiabQdata() {
    return this.currLiabQdata;
  }

  public QuarterlyDouble getDepreciationQdata() {
    return this.depreciationQdata;
  }

  public QuarterlyDouble getDivCostQdata() {
    return this.divCostQdata;
  }

  public QuarterlyDouble getDividendQdata() {
    return this.dividendQdata;
  }

  public QuarterlyDouble getEpsContQdata() {
    return this.epsContQdata;
  }

  public QuarterlyDouble getEpsDilContQdata() {
    return this.epsDilContQdata;
  }

  public QuarterlyDouble getEpsDilQdata() {
    return this.epsDilQdata;
  }

  public double getEpsEstQ0() {
    return this.epsEstQ0;
  }

  public double getEpsEstQ0Growth() {
    return this.epsEstQ0Growth;
  }

  public double getEpsEstY1() {
    return this.epsEstY1;
  }

  public double getEpsEstY2() {
    return this.epsEstY2;
  }

  public QuarterlyDouble getEpsQdata() {
    return this.epsQdata;
  }

  public Object getEpsY1() {
    return this.y1EstGrowth;
  }

  public QuarterlyDouble getEquityQdata() {
    return this.equityQdata;
  }

  public QuarterlyDouble getFcfQdata() {
    return this.fcfQdata;
  }

  public FieldData getFd() {
    return this.fd;
  }

  public QuarterlyDouble getGoodwillQdata() {
    return this.goodwillQdata;
  }

  public QuarterlyDouble getGrossIncQdata() {
    return this.grossIncQdata;
  }

  public QuarterlyDouble getGrossOpIncQdata() {
    return this.grossOpIncQdata;
  }

  public QuarterlyDouble getIncAfterTaxQdata() {
    return this.incAfterTaxQdata;
  }

  public QuarterlyDouble getIncPrimaryEpsQdata() {
    return this.incPrimaryEpsQdata;
  }

  public QuarterlyDouble getIncTaxQdata() {
    return this.incTaxQdata;
  }

  /**
   *
   * @return
   */
  public double getInsiderDollarChg() {
    double ret = 0.0;

    try {
      double price = this.fd.getShareData().getPrice();
      if (this.fd.getCompanyInfo().getPriceQtr()[1] > 0.0 && this.fd.getCompanyInfo().getPriceQtr()[2] > 0.0
          && this.fd.getCompanyInfo().getPriceQtr()[3] > 0.0) {
        price = (this.fd.getCompanyInfo().getPriceQtr()[1] + this.fd.getCompanyInfo().getPriceQtr()[2] + this.fd.getCompanyInfo().getPriceQtr()[3])
            / 3.0;
      }
      else if (this.fd.getShareData().getPrice52lo() > 0.0 && this.fd.getShareData().getPrice52hi() > 0.0) {
        price = (this.fd.getShareData().getPrice52lo() + this.fd.getShareData().getPrice52hi()) / 2.0;
      }

      final double ibuyShr = this.fd.getShareData().getInsiderBuyShrs();
      final double isellShr = this.fd.getShareData().getInsiderSellShrs();
      final double inetShr = ibuyShr - isellShr;

      ret = inetShr * price;
    }
    catch (final Exception e) {
      System.out.println(FieldData.getError(e));
      ret = 0.0;
    }
    return ret;

  }

  /**
   *
   * @return
   */
  public double getInstDollarChg() {
    double ret = 0.0;

    try {
      double price = this.fd.getShareData().getPrice();
      if (this.fd.getCompanyInfo().getPriceQtr()[1] > 0.0 && this.fd.getCompanyInfo().getPriceQtr()[2] > 0.0
          && this.fd.getCompanyInfo().getPriceQtr()[3] > 0.0) {
        price = (this.fd.getCompanyInfo().getPriceQtr()[1] + this.fd.getCompanyInfo().getPriceQtr()[2] + this.fd.getCompanyInfo().getPriceQtr()[3])
            / 3.0;
      }
      else if (this.fd.getShareData().getPrice52lo() > 0.0 && this.fd.getShareData().getPrice52hi() > 0.0) {
        price = (this.fd.getShareData().getPrice52lo() + this.fd.getShareData().getPrice52hi()) / 2.0;
      }

      final double ibuyShr = this.fd.getShareData().getInstBuyShrs();
      final double isellShr = this.fd.getShareData().getInstSellShrs();
      final double inetShr = ibuyShr - isellShr;

      ret = inetShr * price;
    }
    catch (final Exception e) {
      System.out.println(FieldData.getError(e));
      ret = 0.0;
    }
    return ret;

  }

  public QuarterlyDouble getIntExpNonOpQdata() {
    return this.intExpNonOpQdata;
  }

  public QuarterlyDouble getIntExpQdata() {
    return this.intExpQdata;
  }

  public QuarterlyDouble getIntTotQdata() {
    return this.intTotQdata;
  }

  public QuarterlyDouble getInventoryQdata() {
    return this.inventoryQdata;
  }

  public QuarterlyDouble getLiabEquityQdata() {
    return this.liabEquityQdata;
  }

  public QuarterlyDouble getLtDebtQdata() {
    return this.ltDebtQdata;
  }

  public QuarterlyDouble getLtInvestQdata() {
    return this.ltInvestQdata;
  }

  public QuarterlyDouble getMarketCapQdata() {
    return this.marketCapQdata;
  }

  public QuarterlyDouble getNetcashflowQdata() {
    return this.netcashflowQdata;
  }

  public QuarterlyDouble getNetFixedAssetsQdata() {
    return this.netFixedAssetsQdata;
  }

  public QuarterlyDouble getNetIncQdata() {
    return this.netIncQdata;
  }

  public QuarterlyDouble getNetMarginQdata() {
    return this.netMarginQdata;
  }

  public QuarterlyDouble getNonrecurringItemsQdata() {
    return this.nonrecurringItemsQdata;
  }

  public QuarterlyDouble getOpMarginQdata() {
    return this.opMarginQdata;
  }

  public QuarterlyDouble getOtherCurrAssetsQdata() {
    return this.otherCurrAssetsQdata;
  }

  public QuarterlyDouble getOtherCurrLiabQdata() {
    return this.otherCurrLiabQdata;
  }

  public QuarterlyDouble getOtherIncQdata() {
    return this.otherIncQdata;
  }

  public QuarterlyDouble getOtherLtAssetsQdata() {
    return this.otherLtAssetsQdata;
  }

  public QuarterlyDouble getOtherLtLiabQdata() {
    return this.otherLtLiabQdata;
  }

  public QuarterlyDouble getPeQdata() {
    return this.peQdata;
  }

  public QuarterlyDouble getPrefStockQdata() {
    return this.prefStockQdata;
  }

  public QuarterlyDouble getPreTaxIncQdata() {
    return this.preTaxIncQdata;
  }

  public double getPriceChg3() {
    return this.priceChg3;
  }

  public double getPriceChg7() {
    return this.priceChg7;
  }

  public QuarterlyDouble getPricesQdata() {
    return this.pricesQdata;
  }

  public double getQ0EstGrowth() {
    return this.q0EstGrowth;
  }

  public int getQuarter() {
    return this.quarter;
  }

  public QuarterlyDouble getRdQdata() {
    return this.rdQdata;
  }

  public QuarterlyDouble getRoeQdata() {
    return this.roeQdata;
  }

  public double getRs() {
    return this.rs;
  }

  public QuarterlyDouble getSalesQdata() {
    return this.salesQdata;
  }

  public QuarterlyDouble getSharesQdata() {
    return this.sharesQdata;
  }

  public QuarterlyDouble getStDebtQdata() {
    return this.stDebtQdata;
  }

  public QuarterlyDouble getStInvestQdata() {
    return this.stInvestQdata;
  }

  public QuarterlyDouble getTanAssetsQdata() {
    return this.tanAssetsQdata;
  }

  public QuarterlyDouble getTotalAssetsQdata() {
    return this.totalAssetsQdata;
  }

  public QuarterlyDouble getTotalLiabQdata() {
    return this.totalLiabQdata;
  }

  public QuarterlyDouble getTotalOpExpQdata() {
    return this.totalOpExpQdata;
  }

  /**
   *
   * @return
   */
  public double getTurnover() {
    double turnover = 0.0;
    try {
      if (MarketTools.getVolume10d(this.fd) > 0.0) {
        turnover = MarketTools.getFloatshr(this.fd) / (MarketTools.getVolume10d(this.fd) / 1000.0);
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getError(e));
      turnover = 0.0;
    }
    return turnover;
  }

  public double getTotalDebt() {
    double ret = this.getLtDebtQdata().getMostRecent() + this.getStDebtQdata().getMostRecent();
    return ret;
  }

  public double getNetTangible() {
    double ret = this.getTanAssetsQdata().getMostRecent() - this.getTotalDebt();
    return ret;
  }

  public double getNetNavps() {
    double ret = 0.0;
    if (this.getSharesQdata().getMostRecent() > 0.0) {
      ret = this.getNetTangible() / this.getSharesQdata().getMostRecent();
    }
    return ret;
  }

  /**
   * Calculate the Zombie'ness' of the company
   *
   * @param fd FieldData
   * @return Zombie score value
   */
//  private void calcZombieScore() {
//    final ZData zd = new ZData(this);
//    this.zdata = zd;
//    // System.out.printf("%s%n%s%n%n", this.fd.getTicker(), zd);
//  }

  public QuarterlyDouble getUnusualIncQdata() {
    return this.unusualIncQdata;
  }

  public QuarterlyDouble getWcFcfQdata() {
    return this.wcfcfQdata;
  }

  public QuarterlyDouble getWorkingCapitalQdata() {
    return this.workingCapitalQdata;
  }

  public Object getY1EstGrowth() {
    return this.epsEstY1;
  }

  public double getY2EstGrowth() {
    return this.y2EstGrowth;
  }

  public int getYear() {
    return this.year;
  }

  public ZData getZdata() {
    return this.zdata;
  }

  public String getTicker() {
    return this.getFd().getTicker();
  }

  public boolean isValid() {
    return this.valid;
  }

  /**
   * Calculates Derived Data
   */
  private void derived() {

    this.epsEstQ0Growth = MarketTools.getChange(this.epsEstQ0, this.getEpsDilContQdata().get(4));

    // Today vs average of Q1 and Q2
    final double p1 = this.fd.getShareData().getPrice();
    final double p2 = (this.pricesQdata.get(1) + this.pricesQdata.get(2)) / 2.0;
    this.rs = MarketTools.getChange(p1, p2);

    this.q0EstGrowth = MarketTools.getChange(this.fd.getEstimateData().getEpsQ0(), this.fd.getIncSheetData().getEpsDilContQtr()[4]);

    double epsY1 = this.epsDilContQdata.getPrevTtm();
    final double tmp = this.fd.getEstimateData().getEpsY2();
    this.y1EstGrowth = MarketTools.getChange(tmp, epsY1);

    epsY1 = this.fd.getEstimateData().getEpsY1();
    final double epsY2 = this.fd.getEstimateData().getEpsY2();
    this.y2EstGrowth = MarketTools.getChange(epsY1, epsY2);

    final double ncf[] = new double[9];
    for (int i = 0; i < ncf.length; i++) {
      ncf[i] = this.cashFromOpsQdata.get(i) + this.cashFromFinQdata.get(i);
    }
    this.netcashflowQdata = new QuarterlyDouble(ncf);

    final double cf[] = new double[9];
    for (int i = 0; i < cf.length; i++) {
      cf[i] = this.cashFromOpsQdata.get(i) + this.cashFromInvQdata.get(i) + this.cashFromFinQdata.get(i);
    }
    this.cashflowQdata = new QuarterlyDouble(cf);

    final double cAssets[] = new double[9];
    for (int i = 0; i < cAssets.length; i++) {
      cAssets[i] = this.acctRxQdata.get(i) + this.stInvestQdata.get(i) + this.inventoryQdata.get(i) + this.otherCurrAssetsQdata.get(i)
          + this.cashQdata.get(i);
    }
    this.currAssetsQdata = new QuarterlyDouble(cAssets);

    final double cliab[] = new double[9];
    for (int i = 0; i < cliab.length; i++) {
      cliab[i] = this.acctPayableQdata.get(i) + this.stDebtQdata.get(i) + this.otherCurrLiabQdata.get(i);
    }
    this.currLiabQdata = new QuarterlyDouble(cliab);

    final double totAssetsArr[] = new double[9];
    final double tanArr[] = new double[9];
    for (int i = 0; i < totAssetsArr.length; i++) {
      tanArr[i] = this.currAssetsQdata.get(i) + this.netFixedAssetsQdata.get(i) + this.ltInvestQdata.get(i) + this.otherLtAssetsQdata.get(i);
      totAssetsArr[i] = tanArr[i] + this.goodwillQdata.get(i);
    }
    this.totalAssetsQdata = new QuarterlyDouble(totAssetsArr);
    this.tanAssetsQdata = new QuarterlyDouble(tanArr);

    final double intArr[] = new double[9];
    for (int i = 0; i < intArr.length; i++) {
      intArr[i] = MarketTools.getIntExpQtr(this.fd)[i] + MarketTools.getIntExpNonOpQtr(this.fd)[i];
    }
    this.intTotQdata = new QuarterlyDouble(intArr);
    final double totDebt = this.stDebtQdata.get(2) + this.ltDebtQdata.get(2);
    if (totDebt > 0.0) {
      this.currInterestRate = this.intTotQdata.getTtm() / totDebt;
    }

    final double wcArr[] = new double[9];
    for (int i = 0; i < wcArr.length; i++) {
      wcArr[i] = this.currAssetsQdata.get(i) - this.currLiabQdata.get(i);
    }
    this.workingCapitalQdata = new QuarterlyDouble(wcArr);

    final double crArr[] = new double[9];
    for (int i = 0; i < crArr.length; i++) {
      crArr[i] = this.currAssetsQdata.get(i) / this.currLiabQdata.get(i);
    }
    this.currentRatioQdata = new QuarterlyDouble(crArr);

    final double fcfArr[] = new double[6];
    for (int i = 0; i < fcfArr.length; i++) {
      fcfArr[i] = this.cashFromOpsQdata.get(i) - this.capExQdata.get(i) - this.dividendQdata.get(i) * this.sharesQdata.get(i);
    }
    this.fcfQdata = new QuarterlyDouble(fcfArr);

    final double wcfcfArr[] = new double[6];
    for (int i = 0; i < wcfcfArr.length; i++) {
      wcfcfArr[i] = this.workingCapitalQdata.getMostRecent() + this.fcfQdata.getTtm();
    }
    this.wcfcfQdata = new QuarterlyDouble(wcfcfArr);

    final double divArr[] = new double[6];
    for (int i = 0; i < divArr.length; i++) {
      divArr[i] = this.dividendQdata.get(i) * this.sharesQdata.get(i);
    }
    this.divCostQdata = new QuarterlyDouble(divArr);

    final double nMarArr[] = new double[6];
    for (int i = 0; i < nMarArr.length; i++) {
      if (this.salesQdata.get(i) != 0.0) {
        nMarArr[i] = this.netIncQdata.get(i) / this.salesQdata.get(i) * 100.0;
      }
      else {
        nMarArr[i] = 0.0;
      }
    }
    this.netMarginQdata = new QuarterlyDouble(nMarArr);

    final double oMarArr[] = new double[6];
    for (int i = 0; i < oMarArr.length; i++) {
      if (this.salesQdata.get(i) != 0.0) {
        oMarArr[i] = this.grossOpIncQdata.get(i) / this.salesQdata.get(i) * 100.0;
      }
      else {
        oMarArr[i] = 0.0;
      }
    }
    this.opMarginQdata = new QuarterlyDouble(oMarArr);

    final double peArr[] = new double[6];
    double eps = this.epsContQdata.getTtm();
    for (int i = 0; i < peArr.length; i++) {
      peArr[i] = 0.0;
    }
    if (eps > 0.0) {
      peArr[1] = this.pricesQdata.get(1) / eps;
    }
    eps = this.epsContQdata.get2QTtm();
    if (eps > 0.0) {
      peArr[2] = this.pricesQdata.get(2) / eps;
    }
    eps = this.epsContQdata.getPrevTtm();
    if (eps > 0.0) {
      peArr[5] = this.pricesQdata.get(5) / eps;
    }
    this.peQdata = new QuarterlyDouble(peArr);

    final double roeArr[] = new double[6];
    double net = this.netIncQdata.getTtm();
    double eq = this.equityQdata.getTtm();
    for (int i = 0; i < roeArr.length; i++) {
      roeArr[i] = 0.0;
    }
    if (net > 0.0 && eq > 0.0) {
      roeArr[1] = net / eq * 100.0;
    }
    net = this.netIncQdata.get2QTtm();
    eq = this.equityQdata.get2QTtm();
    if (net > 0.0 && eq > 0.0) {
      roeArr[2] = net / eq * 100.0;
    }
    net = this.netIncQdata.getPrevTtm();
    eq = this.equityQdata.getPrevTtm();
    if (net > 0.0 && eq > 0.0) {
      roeArr[5] = net / eq * 100.0;
    }
    this.roeQdata = new QuarterlyDouble(roeArr);

    final double mcArr[] = new double[9];
    for (int i = 0; i < mcArr.length; i++) {
      mcArr[i] = this.sharesQdata.get(i) * this.pricesQdata.get(i);
    }
    this.marketCapQdata = new QuarterlyDouble(mcArr);

    this.priceChg3 = MarketTools.getChange(this.getPricesQdata().getMostRecent(), this.getPricesQdata().get(3));
    this.priceChg7 = MarketTools.getChange(this.getPricesQdata().getMostRecent(), this.getPricesQdata().get(7));

    this.zdata = new ZData(this);

  }

  public String getzState() {
    return zState;
  }

  public void setzState(String z) {
    this.zState = z;
  }

}
