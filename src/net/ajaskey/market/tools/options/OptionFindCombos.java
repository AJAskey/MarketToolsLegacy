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
package net.ajaskey.market.tools.options;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;

public class OptionFindCombos {

  static List<OptionFindCombos> c1List = new ArrayList<>();

  static List<OptionFindCombos> c2List = new ArrayList<>();
  static List<OptionFindCombos> c3List = new ArrayList<>();
  static List<OptionFindCombos> c4List = new ArrayList<>();
  static double                 chg[]  = { 0.05, 0.10, 0.15, 0.20 };

  final private static double MinOptionPrice = 0.10;
  private static double       ulPrice        = 0.0;

  /**
   *
   * @param args
   * @throws IOException
   * @throws ParseException
   */
  public static void main(String[] args) throws IOException, ParseException {

    AddCboeDataFiles.main(null);

    OptionFindCombos.c1List.clear();
    OptionFindCombos.c2List.clear();
    OptionFindCombos.c3List.clear();
    OptionFindCombos.c4List.clear();

    // System.out.println(args.length);

    if (args.length < 2) {
      System.out.println("Command line format : Code Type --->  SPY PUT ");
      return;
    }

    String tf = "S";
    if (args.length == 3) {
      tf = args[2].trim().toUpperCase();
    }

    if (args.length == 4) {
      OptionFindCombos.ulPrice = Double.parseDouble(args[3].trim());
    }

    final String propFile = "";
    OptionFindCombos.processProperties(propFile);

    OptionFindCombos.process(args[0].trim(), args[1].trim(), tf);
  }

  /**
   *
   * @param code
   * @param type
   * @param timeFrame
   * @throws FileNotFoundException
   */
  public static void process(String code, String type, String timeFrame) throws FileNotFoundException {

    Utils.makeDir("out");
    Utils.makeDir("out/options");

    int sdOffset = 0;
    int feOffset = 0;

    if (timeFrame.equalsIgnoreCase("S")) {
      sdOffset = 10;
      feOffset = 10;
    }
    else if (timeFrame.equalsIgnoreCase("M")) {
      sdOffset = 45;
      feOffset = 20;
    }
    else {
      sdOffset = 90;
      feOffset = 45;
    }

    // OptionsProcessor.OpenDebug();

    if (code.contains("qqq")) {
      System.out.println(code);
    }

    final double MinProfit = 1.0;

    // DateTime buyDate = new DateTime(2019, DateTime.DECEMBER, 23);
    final DateTime buyDate = new DateTime();

    final DateTime sellDate = new DateTime(buyDate);

    sellDate.add(DateTime.DATE, sdOffset);

    final DateTime firstExpiry = new DateTime(sellDate);
    firstExpiry.add(DateTime.DATE, feOffset);

    final String fname = String.format("data/options/%s-options.dat", code);

    final List<CboeOptionData> dil = CallPutList.readCboeData(fname, firstExpiry, buyDate, 1);

    /**
     * Debug of DIL
     */
    final String dbgName = String.format("out/dil-%s-%s.dbg", code, type);
    try (PrintWriter pw = new PrintWriter(dbgName)) {
      pw.println("Dil Count : " + dil.size());
      for (final CboeOptionData cod : dil) {
        pw.println(cod);
      }
    }

    final double dilCPrice = CallPutList.getcPrice();
    if (OptionFindCombos.ulPrice == 0.0) {
      OptionFindCombos.ulPrice = dilCPrice;
    }
    int typeInt = OptionsProcessor.ACALL;

    /**
     * Name the output file
     */
    String sCombo = "";
    for (final double d : OptionFindCombos.chg) {
      sCombo += String.format("_%d", (int) Math.round(d * 100.0));
    }
    final String outfile = String.format("out/options/%s-%d-%s%s-options-analysis.csv", code.toUpperCase(), (int) OptionFindCombos.ulPrice,
        type.toUpperCase(), sCombo);

    if (type.toUpperCase().contains("P")) {
      typeInt = OptionsProcessor.APUT;
      for (int i = 0; i < OptionFindCombos.chg.length; i++) {
        OptionFindCombos.chg[i] = -OptionFindCombos.chg[i];
      }
    }

    /**
     * Loop over the price changes
     */
    for (final double d : OptionFindCombos.chg) {

      final double ul = OptionFindCombos.ulPrice * (1.0 + d);

      for (final CboeOptionData cod : dil) {

        if (cod.expiry.isGreaterThan(sellDate) && cod.expiry.isGreaterThan(firstExpiry)) {

          CboeCallPutData option = null;
          if (typeInt == OptionsProcessor.ACALL) {
            option = cod.call;
          }
          else {
            option = cod.put;
          }

          final CboeCallPutData opt = new CboeCallPutData(option);

          final double p = opt.getPrice();

          final double price = option.getPrice();

          // System.out.printf("%.2f\t%.2f%n", price, option.mark);
          if (price > 0.0 && opt.mark > OptionFindCombos.MinOptionPrice) {

            System.out.printf("%.2f%n", opt.optionData.getStrike());

            System.out.printf("  0 : %.2f\t%.2f\t%.2f\t%.2f%n", opt.mark, p, p, opt.premium);

            opt.setSellDate(sellDate);
            opt.setUlPrice(ul);

            final double sp = opt.getPrice();
            final double sellPrice = sp;
            // final double sellPrice = sp * (1.0 + option.premium);
            System.out.printf("  1 : %.2f\t%.2f\t%.2f\t%.2f%n", opt.mark, sp, sellPrice, opt.premium);

            final double buyPrice = opt.mark;

            final double profit = (sellPrice - buyPrice) / buyPrice * 100.0;

            if (profit > MinProfit && buyPrice > 0.0) {

              final List<OptionFindCombos> cList = OptionFindCombos.findChgList(d);

              final OptionFindCombos ofc = new OptionFindCombos(cod.strike, OptionFindCombos.ulPrice, ul, opt.premium, d, profit, opt, buyDate);

              cList.add(ofc);
              System.out.printf("  2 : %.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f%n", ofc.optBuyPrice, ofc.optSellPrice, sellPrice, ofc.premium, p, sp);
            }
          }
        }
      }
    } // End loop over price changes

    Collections.sort(OptionFindCombos.c2List, new OptionListSorter());

    /**
     * Write output file. Write debug file.
     */
    try (PrintWriter pw = new PrintWriter(outfile); PrintWriter pwDbg = new PrintWriter("out/options/OPC.dbg")) {

      final String sRepeat = ",,SellPriceUL,SellPriceOpt,Profit";

      pw.printf("Expiry,Strike,,BuyPriceUL,BuyPriceOpt,Premium%s%s%s%s,,AvgProfit,,ToStrike,%s,%s,%s%n", sRepeat, sRepeat, sRepeat, sRepeat,
          code.toUpperCase(), buyDate, sellDate);

      for (final OptionFindCombos ofc2 : OptionFindCombos.c2List) {

        final OptionFindCombos ofc1 = OptionFindCombos.findFromList(ofc2, OptionFindCombos.c1List);
        if (ofc1 != null) {

          final OptionFindCombos ofc3 = OptionFindCombos.findFromList(ofc2, OptionFindCombos.c3List);
          if (ofc2 != null) {

            final OptionFindCombos ofc4 = OptionFindCombos.findFromList(ofc2, OptionFindCombos.c4List);
            if (ofc4 != null) {
              // System.out.println(ofc2);
              // System.out.println(ofc1);
              // System.out.println(ofc3);
              // System.out.println(ofc4);
              // System.out.println("\n--------------------------------------------------------\n");

              final double avgProfit = (ofc1.profit + ofc2.profit + ofc3.profit + ofc4.profit) / 4.0;

              if (avgProfit > 99.99) {

                final double toStrike = Math.abs((ofc1.strike - ofc1.ulBuyPrice) / ofc1.strike);

                final String s = String.format(
                    "%s,%.2f,,%.2f,%.2f,%.2f%%,,%.2f,%.2f,%.2f%%,,%.2f,%.2f,%.2f%%,,%.2f,%.2f,%.2f%%,,%.2f,%.2f,%.2f%%,,%.2f%%,,%.4f", ofc1.expiry,
                    ofc1.strike, ofc1.ulBuyPrice, ofc1.optBuyPrice, ofc1.premium * 100.0, ofc1.ulSellPrice, ofc1.optSellPrice, ofc1.profit,
                    ofc2.ulSellPrice, ofc2.optSellPrice, ofc2.profit, ofc3.ulSellPrice, ofc3.optSellPrice, ofc3.profit, ofc4.ulSellPrice,
                    ofc4.optSellPrice, ofc4.profit, avgProfit, toStrike);
                pw.println(s);
              }
            }
          }
        }
      }
    }
    // OptionsProcessor.CloseDebug();

  }

  /**
   *
   * @param d
   * @return
   */
  private static List<OptionFindCombos> findChgList(double d) {
    for (int i = 0; i < OptionFindCombos.chg.length; i++) {
      if (OptionFindCombos.chg[i] == d) {
        if (i == 0) {
          return OptionFindCombos.c1List;
        }
        if (i == 1) {
          return OptionFindCombos.c2List;
        }
        if (i == 2) {
          return OptionFindCombos.c3List;
        }
        if (i == 3) {
          return OptionFindCombos.c4List;
        }
      }
    }
    return null;
  }

  /**
   *
   * @param ofc
   * @param cList
   * @return
   */
  private static OptionFindCombos findFromList(OptionFindCombos ofc, List<OptionFindCombos> cList) {
    for (final OptionFindCombos o : cList) {
      if (o.expiry.sameDate(ofc.expiry)) {
        if (o.strike == ofc.strike) {
          return o;
        }
      }
    }
    return null;
  }

  /**
   *
   * @param propFile
   */
  private static void processProperties(String propFile) {
    // TODO Auto-generated method stub

  }

  public double bsBuyPrice;

  public DateTime buyDate;

  public double          change;
  public CboeCallPutData data;
  public DateTime        expiry;
  public double          optBuyPrice;
  public double          optSellPrice;

  public double premium;

  public double profit;

  public double strike;

  public double ulBuyPrice;

  public double ulSellPrice;

  public OptionFindCombos(double sk, double ul, double ulSell, double prem, double c, double prof, CboeCallPutData d, DateTime bdate) {

    this.strike = sk;
    this.bsBuyPrice = d.getPrice();
    this.optBuyPrice = d.mark;
    this.ulBuyPrice = ul;
    this.ulSellPrice = ulSell;
    this.premium = prem;
    this.change = c;
    this.profit = prof;
    this.data = d;
    this.buyDate = new DateTime(bdate);
    this.expiry = new DateTime(d.optionData.getExpiry());
    final double p = d.getPrice();
    this.optSellPrice = p; // * (1.0 + prem);
  }

  @Override
  public String toString() {
    String ret = String.format("Change          : %.2f%n", this.change);
    ret += String.format("Expiry          : %s%n", this.expiry);
    ret += String.format("Strike          : %.2f%n", this.strike);
    ret += String.format("Buy Date        : %s%n", this.buyDate);
    ret += String.format("Buy UL Price    : %.2f%n", this.ulBuyPrice);
    ret += String.format("Sell UL Price   : %.2f%n", this.ulSellPrice);
    ret += String.format("BS Buy Opt Price : %.2f%n", this.optBuyPrice);
    ret += String.format("Buy Opt Price   : %.2f%n", this.optBuyPrice);
    ret += String.format("Sell Opt Price  : %.2f%n", this.optSellPrice);
    ret += String.format("Premium         : %.2f%%%n", this.premium);
    ret += String.format("Profit          : %.2f%%%n", this.profit);
    ret += String.format("Option Data     : %s%n", this.data);
    return ret;
  }

}
