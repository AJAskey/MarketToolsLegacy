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
package net.ajaskey.market.tools.quandl;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.market.optuma.PriceData;
import net.ajaskey.market.optuma.TickerPriceData;

public class ProcessQuandl {

  public static List<LastDataPoint> lastDataPoint = new ArrayList<>();

  public final static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * net.ajaskey.market.tools.quandl.main
   *
   * @param args
   */
  public static void main(final String[] args) {

    final TickerPriceData spxData = new TickerPriceData("WI", "SPX");

    final double lastSpxPrice = spxData.getLatest();
    final DateTime lastSpxDate = spxData.getLatestDate();

    System.out.println("SPX latest date  : " + lastSpxDate);
    System.out.println("SPX latest price : " + lastSpxPrice);

    final ShillerData sd = new ShillerData();

    final double shillerpe = sd.ShillerPE;
    ProcessQuandl.lastDataPoint.add(new LastDataPoint("SHILLER_PE_RATIO", shillerpe));
    ProcessQuandl.lastDataPoint.add(new LastDataPoint("SP500_DIV_MONTH", sd.Sp500Dividends));
    ProcessQuandl.lastDataPoint.add(new LastDataPoint("SP500_BVPS_YEAR", sd.Sp500BVPS));
    ProcessQuandl.lastDataPoint.add(new LastDataPoint("SP500_SALES", sd.Sp500Sales));
    final double spxearn = sd.ShillerEarnings;
    final double spxyield = spxearn / lastSpxPrice * 100.0;
    ProcessQuandl.lastDataPoint.add(new LastDataPoint("SP500_EARNINGS_YIELD_MONTH", spxyield));
    ProcessQuandl.lastDataPoint.add(new LastDataPoint("SP500_EARNINGS", spxearn));

    final String sp500URL = "https://www.quandl.com/api/v3/datasets/MULTPL/SP500_REAL_PRICE_MONTH.xml?api_key=" + QuandlApi.key;
    final String sp500EarnYldURL = "https://www.quandl.com/api/v3/datasets/MULTPL/SP500_EARNINGS_YIELD_MONTH.xml?api_key=" + QuandlApi.key;
    final String sp500DivURL = "https://www.quandl.com/api/v3/datasets/MULTPL/SP500_DIV_MONTH.xml?api_key=" + QuandlApi.key;
    final String bookValueURL = "https://www.quandl.com/api/v3/datasets/MULTPL/SP500_BVPS_YEAR.xml?api_key=" + QuandlApi.key;
    final String shillerPeURL = "https://www.quandl.com/api/v3/datasets/MULTPL/SHILLER_PE_RATIO_MONTH.xml?api_key=" + QuandlApi.key;
    final String sp500SalesURL = "https://www.quandl.com/api/v3/datasets/MULTPL/SP500_SALES_QUARTER.xml?api_key=" + QuandlApi.key;

    final String naaimURL = "https://www.quandl.com/api/v3/datasets/NAAIM/NAAIM.xml?api_key=" + QuandlApi.key;

    final OneValueData odv = new OneValueData(lastSpxDate, lastSpxPrice);
    final List<OneValueData> price = ProcessQuandl.getOneDataPoint(sp500URL);
    price.add(0, odv);

    final List<OneValueData> earnYld = ProcessQuandl.getOneDataPoint(sp500EarnYldURL);
    final List<OneValueData> scaledEarnings = ProcessQuandl.scaleEarnings(earnYld, price);
    ProcessQuandl.writeOneList(scaledEarnings, "SP500_Earnings");

    Collections.reverse(scaledEarnings);
    final List<OneValueData> sp500pe = ProcessQuandl.scalePE(price, scaledEarnings);
    ProcessQuandl.writeOneList(sp500pe, "SP500_PE");

    ProcessQuandl.writeOneList(earnYld, "SP500_EarningsYield");

    final List<OneValueData> div = ProcessQuandl.getOneDataPoint(sp500DivURL);
    ProcessQuandl.writeOneList(div, "SP500_Dividend");

    final List<OneValueData> sdiv = ProcessQuandl.scaleYield(div, price);
    ProcessQuandl.writeOneList(sdiv, "SP500_DividendYield");

    final List<NaaimData> naaim = ProcessQuandl.getNaaimData(naaimURL);
    ProcessQuandl.writeNaaimList(naaim, "NAAIM");

    final List<OneValueData> bv = ProcessQuandl.getOneDataPoint(bookValueURL);
    ProcessQuandl.writeOneList(bv, "SP500_BookValuePS");

    final List<OneValueData> sPE = ProcessQuandl.getOneDataPoint(shillerPeURL);
    ProcessQuandl.writeOneList(sPE, "Shiller_PE");

    final List<OneValueData> sales = ProcessQuandl.getOneDataPoint(sp500SalesURL);
    ProcessQuandl.writeOneList(sales, "SP500_Sales");

//    final List<PriceData> epc = ProcessQuandl.getPutCallData(epcURL, 0, 1, 2, 3);
//    ProcessQuandl.writePcList(epc, "EquityPC");
//
//    final List<PriceData> ipc = ProcessQuandl.getPutCallData(ipcURL, 0, 1, 2, 3);
//    ProcessQuandl.writePcList(ipc, "IndexPC");
//
//    final List<PriceData> tpc = ProcessQuandl.getPutCallData(tpcURL, 0, 1, 2, 3);
//    ProcessQuandl.writePcList(tpc, "TotalPC");
//
//    final List<PriceData> spxpc = ProcessQuandl.getPutCallData(spxpcURL, 1, 2, 3, 0);
//    ProcessQuandl.writePcList(spxpc, "SPX PC");
//
//    final List<PriceData> vixpc = ProcessQuandl.getPutCallData(vixpcURL, 1, 2, 3, 0);
//    ProcessQuandl.writePcList(vixpc, "VIX PC");

    System.out.println("Done.");
  }

  /**
   *
   * net.ajaskey.market.tools.quandl.getLastDataPoint
   *
   * @param name
   * @return
   */
  private static double getLastDataPoint(final String name) {

    for (final LastDataPoint ldp : ProcessQuandl.lastDataPoint) {
      if (name.contains(ldp.name)) {
        return ldp.value;
      }
    }
    return -9999999999.999;
  }

  /**
   * net.ajaskey.market.tools.quandl.getNaaimData
   *
   * @param naaimURL
   * @return
   */
  private static List<NaaimData> getNaaimData(final String url) {

    final List<NaaimData> ret = new ArrayList<>();
    final List<CommonQuandlData> ddList = Qcommon.getData(url, 9);
    for (final CommonQuandlData cqd : ddList) {
      final NaaimData na = new NaaimData(cqd.date, cqd.dd[0], cqd.dd[1], cqd.dd[2], cqd.dd[3], cqd.dd[4], cqd.dd[5], cqd.dd[6], cqd.dd[7], cqd.dd[8]);
      ret.add(na);
      // System.out.println(na);
    }

    return ret;
  }

  private static List<OneValueData> getOneDataPoint(final String url) {

    final List<OneValueData> ret = new ArrayList<>();
    final List<CommonQuandlData> ddList = Qcommon.getData(url, 1);
    for (final CommonQuandlData cqd : ddList) {
      final OneValueData dp = new OneValueData(cqd.date, cqd.dd[0]);
      ret.add(dp);
    }

    final double dv = ProcessQuandl.getLastDataPoint(url);
    if (dv > -9999999999.999) {
      final OneValueData dp = new OneValueData(new DateTime(), dv);
      ret.add(0, dp);
    }

    return ret;
  }

  /**
   * net.ajaskey.market.tools.quandl.getPutCallData
   *
   * @param epcURL
   * @return
   */
  private static List<PriceData> getPutCallData(final String url, final int callIdx, final int putIdx, final int totIdx, final int ratioIdx) {

    final List<PriceData> ret = new ArrayList<>();

    final List<CommonQuandlData> ddList = Qcommon.getData(url, 4);
    for (final CommonQuandlData cqd : ddList) {
      final PriceData pc = new PriceData(cqd.date, cqd.dd[callIdx], cqd.dd[putIdx], cqd.dd[totIdx], cqd.dd[ratioIdx], 0);
      ret.add(pc);
    }

    return ret;
  }

  /**
   * net.ajaskey.market.tools.quandl.scaleEarnings
   *
   * @param earn
   * @param d
   * @return
   */
  private static List<OneValueData> scaleEarnings(final List<OneValueData> earn, final List<OneValueData> price) {

    final List<OneValueData> ret = new ArrayList<>();
    int knt = 0;
    final int tot = price.size() - 1;
    System.out.println(tot);
    for (final OneValueData data : earn) {
      if (knt > tot) {
        break;
      }
      final OneValueData nd = new OneValueData(data.date, data.value * price.get(knt++).value / 100.0);
      ret.add(nd);
    }
    final double dv = ProcessQuandl.getLastDataPoint("SP500_EARNINGS");
    ret.get(0).value = dv;
    return ret;
  }

  private static List<OneValueData> scalePE(final List<OneValueData> price, final List<OneValueData> earn) {

    final List<OneValueData> ret = new ArrayList<>();
    int knt = 0;
    for (final OneValueData data : earn) {
      final double pe = Math.min(price.get(knt++).value / data.value, 35.0);
      final OneValueData nd = new OneValueData(data.date, pe);
      ret.add(nd);
    }
    return ret;
  }

  private static List<OneValueData> scaleYield(final List<OneValueData> div, final List<OneValueData> price) {

    final List<OneValueData> ret = new ArrayList<>();
    int knt = price.size() - 1;
    for (final OneValueData data : div) {
      if (knt >= 0) {
        // System.out.println(data + "\t" + price.get(knt));
        final OneValueData nd = new OneValueData(data.date, data.value / price.get(knt--).value * 100.0);
        // System.out.println(nd);
        ret.add(nd);
      }
    }
    return ret;
  }

  /**
   * net.ajaskey.market.tools.quandl.writeNaaimList
   *
   * @param naaim
   * @param string
   */
  private static void writeNaaimList(final List<NaaimData> list, final String fname) {

    Collections.reverse(list);
    try (PrintWriter pwMean = new PrintWriter(Qcommon.outpath + "\\" + fname + "_Mean.csv");
        PrintWriter pwBear = new PrintWriter(Qcommon.outpath + "\\" + fname + "_Bear.csv");
        PrintWriter pwq1 = new PrintWriter(Qcommon.outpath + "\\" + fname + "_q1.csv");
        PrintWriter pwMed = new PrintWriter(Qcommon.outpath + "\\" + fname + "_Median.csv");
        PrintWriter pwq3 = new PrintWriter(Qcommon.outpath + "\\" + fname + "_q3.csv");
        PrintWriter pwBull = new PrintWriter(Qcommon.outpath + "\\" + fname + "_Bull.csv");
        PrintWriter pwStddev = new PrintWriter(Qcommon.outpath + "\\" + fname + "_StdDev.csv");
        PrintWriter pwbbdiff = new PrintWriter(Qcommon.outpath + "\\" + fname + "_BullBear.csv");
        PrintWriter pwsp500 = new PrintWriter(Qcommon.outpath + "\\" + fname + "_SP500.csv")) {

      for (final NaaimData naaim : list) {

        pwMean.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.mean);
        pwBear.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.mostBearish);
        pwq1.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.q1);
        pwMed.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.median);
        pwq3.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.q3);
        pwBull.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.mostBullish);
        pwStddev.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.stdDev);
        pwsp500.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.sp500);
        pwbbdiff.printf("%s,%.2f%n", naaim.date.format("yyyy-MM-dd"), naaim.bbDiff);
      }
      // System.out.println(Utils.getString(list.get(list.size() - 1).date));

    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * net.ajaskey.market.tools.quandl.writeBalticDryList
   *
   * @param balDry
   * @param string
   */
  private static void writeOneList(final List<OneValueData> list, final String fname) {

    if (!fname.equalsIgnoreCase("Shiller_SP500_Earnings") && !fname.equalsIgnoreCase("SP500_DividendYield")) {
      Collections.reverse(list);
    }

    try (PrintWriter pw = new PrintWriter(Qcommon.outpath + "\\" + fname + ".csv")) {
      for (final OneValueData one : list) {

        pw.printf("%s,%.2f%n", one.date.format("yyyy-MM-dd"), one.value);
      }
      // System.out.println(Utils.getString(list.get(list.size() - 1).date));

    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * net.ajaskey.market.tools.quandl.writePcList
   *
   * @param epc
   * @param string
   */
  private static void writePcList(final List<PriceData> list, final String fname) {

    Collections.reverse(list);
    try (PrintWriter pwCall = new PrintWriter(Qcommon.outpath + "\\" + fname + "-CallVol.csv");
        PrintWriter pwPut = new PrintWriter(Qcommon.outpath + "\\" + fname + "-PutVol.csv");
        PrintWriter pwTot = new PrintWriter(Qcommon.outpath + "\\" + fname + "_TotalVol.csv");
        PrintWriter pwDiff = new PrintWriter(Qcommon.outpath + "\\" + fname + "-DiffVol.csv");
        PrintWriter pwRatio = new PrintWriter(Qcommon.outpath + "\\" + fname + "_Ratio.csv")) {

      for (final PriceData price : list) {

        pwCall.printf("%s,%d%n", price.date.format("yyyy-MM-dd"), (int) price.open);
        pwPut.printf("%s,%d%n", price.date.format("yyyy-MM-dd"), (int) price.high);
        pwTot.printf("%s,%d%n", price.date.format("yyyy-MM-dd"), (int) price.low);
        pwRatio.printf("%s,%.2f%n", price.date.format("yyyy-MM-dd"), price.close);

        final int diff = (int) (price.open - price.high);
        pwDiff.printf("%s,%d%n", price.date.format("yyyy-MM-dd"), diff);

      }
      // System.out.println(Utils.getString(list.get(list.size() - 1).date));

    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

  }
}
