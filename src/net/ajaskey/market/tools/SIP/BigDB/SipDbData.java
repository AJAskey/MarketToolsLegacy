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

import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

/**
 * This class serves as a test driver to check new development.
 */
public class SipDbData {

  public static void main(final String[] args) throws FileNotFoundException {

    final int year = 2020;
    final int qtr = 2;

//    for (int i = 2018; i < 2021; i++) {
//      for (int j = 1; j < 5; j++) {
//
//      }
//    }

    // FieldData.parseSipData(2020, 1, FiletypeEnum.NONE);

    // MarketTools.parseSipData(year, qtr, FiletypeEnum.BIG_BINARY);

    MarketTools.setQMemory(year, qtr, FiletypeEnum.BIG_BINARY);
    MarketTools.setQMemory(year, qtr, FiletypeEnum.BIG_BINARY);

    final FieldData fd = MarketTools.getFromMemory("MSFT", year, qtr);
    System.out.println(fd);

    // MarketTools.setMemory(2018, 2020, FiletypeEnum.BINARY);

    // System.out.println(Reports.memoryOverview());

    // FieldData.parseSipData(year, qtr, FiletypeEnum.BINARY);
    // List<FieldData> fdList = FieldData.readDbData(year, qtr, FiletypeEnum.TEXT);
    // FieldData fd = FieldData.getFromMemory("A", year, qtr);
//    FieldData fd = FieldData.getFromDb("NVAX", year, qtr, FiletypeEnum.TEXT);
//    System.out.println(fd);

//    FieldData.parseSipData(2018, 1);
//    FieldData.parseSipData(2019, 1);
//    FieldData.parseSipData(2020, 1);

//    for (int i = 2018; i < 2021; i++) {
//      for (int j = 1; j < 5; j++) {
//        FieldData.readDbData(i, j);
//      }
//    }

//    FieldData fd = CompanyData.getCompany("MSFT", 2020, 2);
//
//    List<String> tickers = CompanyData.getTickers(SnpEnum.SP500, 2020, 1);

//    for (String s : tickers) {
//      System.out.println(s);
//    }
//    try (PrintWriter pw = new PrintWriter("out/snp-earning.txt")) {
//      for (String ticker : tickers) {
//        CompanyData cd = CompanyData.getCompany(ticker);
//        double lastEps = 0.0;
//        pw.println(Utils.NL + cd.ticker);
//        for (FieldData fd : cd.fdList) {
//          double eps = fd.getEpsYr()[0];
//          double chg = 0.0;
//          if (lastEps != 0.0) {
//            chg = (eps - lastEps) / lastEps * 100.0;
//          }
//          lastEps = eps;
//
//          pw.printf(" %d Q%d -- %.3f\t%.1f%%%n", fd.getYear(), fd.getQuarter(), eps, chg);
//        }
//      }
//    }
//    try (PrintWriter pw = new PrintWriter("out/snp-earning2.txt")) {
//      List<CompanyData> cdList = CompanyData.getCompanies(tickers);
//      for (CompanyData cd : cdList) {
//        System.out.printf("Processing : %s%n", cd.getTicker());
//        double lastNet = 0.0;
//        pw.println(Utils.NL + cd.getTicker());
//        for (FieldData fd : cd.getFdList()) {
//          double net = fd.getTtm(fd.getNetIncQtr());
//          double chg = 0.0;
//          if (lastNet != 0.0) {
//            chg = (net - lastNet) / Math.abs(lastNet) * 100.0;
//          }
//          lastNet = net;
//
//          pw.printf(" %d Q%d -- %.3f\t%.1f%%%n", fd.getYear(), fd.getQuarter(), net, chg);
//        }
//      }
//    }
//    List<String> net = Reports.getCompanyNetIncome("CAT", 2018, 2020, 2);
//    for (String s : net) {
//      System.out.println(s);
//    }

    // System.out.println(Reports.getSnpIndex(2018, 2, SnpEnum.SP600, true));
    // System.out.println(Reports.getDowIndex(2019, 1, DowEnum.TRANSPORTATION,
    // false));
    // System.out.println(Reports.getExchange(2020, 2, ExchEnum.NYSE, true));

//    FieldData.parseSipData(year, qtr++);
//    FieldData.parseSipData(year, qtr++);
//    FieldData.parseSipData(year, qtr++);
//    FieldData.parseSipData(year, qtr++);
//    FieldData.parseSipData(2019, 1);
    // FieldData.parseSipData(2019, 2);
    // FieldData.parseSipData(2019, 3);
//  FieldData.parseSipData(2019, 4);
//    FieldData.parseSipData(2020, 1);
//    FieldData.parseSipData(2020, 2);

    // FieldData.readDbData(year, qtr);
    // FieldData.readDbData(2019, 3);
//    FieldData.readDbData(2019, 4);
//    FieldData.readDbData(2020, 1);
//    FieldData.readDbData(2020, 2);
    // FieldData fd = FieldData.readDbData(2020, 1, "MSFT");
    // if (fd != null) System.out.println(fd);

    // List<String> sList = Reports.outputToList(s);
    // System.out.println(Reports.getDowIndex(year, qtr, DowEnum.TRANSPORTATION,
    // false));

//    List<String> tickers = new ArrayList<>();
//    tickers.add("MSFT");
//    tickers.add("AA");

//    List<ManyCompanyData> mcdList = ManyCompanyData.createList(sList);
//    for (ManyCompanyData mcd : mcdList) {
//      System.out.println(mcd);
//
//    }
//    List<OneCompanyData> msft = OneCompanyData.getCompany("MSFT");
//    for (OneCompanyData ocd : msft) {
//      System.out.println(ocd);
//      // FieldData fd = ocd.getQ(1);
//      // System.out.println(fd);
//    }
    // System.out.println(BigLists.getReport());

//    int[] yrs = { 2019, 2020 };
//    String rpt = Reports.companySummary(yrs);
//    System.out.println(rpt);

//    for (String ss : sList) {
//      System.out.println(ss);
//    }
  }

}
