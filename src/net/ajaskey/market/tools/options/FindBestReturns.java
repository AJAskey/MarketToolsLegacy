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

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;

/**
 *
 * @author Andy Askey
 *
 */
public class FindBestReturns {

  static double changes[] = { 5.0, 10.0, 15.0, 20.0 };
  // static double changes[] = { 25.0 };

  static List<String> codeList    = new ArrayList<>();
  static List<String> expiryDates = new ArrayList<>();

  final static int holdDays10 = 9;
  final static int holdDays15 = 14;

  final static int              holdDays20     = 90;
  final static int              holdDays5      = 4;
  final static double           maxOptionPrice = 10.01;
  final static int              minOi          = 500;                                              // 250;
  final static double           minOptionPrice = 0.0499;
  static int                    otypes[]       = { OptionsProcessor.ACALL, OptionsProcessor.APUT };
  final static SimpleDateFormat sdf            = new SimpleDateFormat("dd-MMM-yyyy");
  static boolean                useMarkBuy     = false;

  /**
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    AddCboeDataFiles.main(null);

    // FindBestReturns.expiryDates.add("16-Mar-2020");
    // FindBestReturns.expiryDates.add("20-Mar-2020");
    FindBestReturns.expiryDates.add("17-Apr-2020");
    // expiryDates.add("15-May-2020");
    // FindBestReturns.expiryDates.add("17-Jul-2020");
    // FindBestReturns.expiryDates.add("18-Sep-2020");
    // FindBestReturns.expiryDates.add("16-Oct-2020");
    // FindBestReturns.expiryDates.add("20-Nov-2020");
    // FindBestReturns.expiryDates.add("18-Dec-2020");
    // FindBestReturns.expiryDates.add("15-Jan-2021");

    // FindBestReturns.codeList.add("XOM");

    // FindBestReturns.codeList.add("TLT");
    FindBestReturns.codeList.add("QQQ");
    FindBestReturns.codeList.add("SPY");
    // FindBestReturns.codeList.add("DIA");
    // FindBestReturns.codeList.add("IWM");
//    FindBestReturns.codeList.add("IBB");
//    FindBestReturns.codeList.add("IYT");
//    FindBestReturns.codeList.add("XLB");
//    FindBestReturns.codeList.add("XLE");
//    FindBestReturns.codeList.add("XLF");
//    FindBestReturns.codeList.add("XLI");
//    FindBestReturns.codeList.add("XLK");
//    FindBestReturns.codeList.add("XLP");
//    FindBestReturns.codeList.add("XLRE");
//    FindBestReturns.codeList.add("XLV");
//    FindBestReturns.codeList.add("XLY");
//    FindBestReturns.codeList.add("HYG");
//    FindBestReturns.codeList.add("EEM");
//    FindBestReturns.codeList.add("EWI");
//    FindBestReturns.codeList.add("EWJ");
//    FindBestReturns.codeList.add("EWS");
//    FindBestReturns.codeList.add("EWY");
//    FindBestReturns.codeList.add("EWU");
//    FindBestReturns.codeList.add("EWW");
//    FindBestReturns.codeList.add("EWZ");
//    FindBestReturns.codeList.add("FXI");
//    FindBestReturns.codeList.add("RSX");

//    FindBestReturns.codeList.add("VIX");

    for (final String code : FindBestReturns.codeList) {
      for (final int otype : FindBestReturns.otypes) {
        for (final double minUlChg : FindBestReturns.changes) {
          int holdDays = 15;
          if (minUlChg < 5.01) {
            holdDays = FindBestReturns.holdDays5;
          }
          else if (minUlChg < 10.01) {
            holdDays = FindBestReturns.holdDays10;
          }
          else if (minUlChg < 15.00001) {
            holdDays = FindBestReturns.holdDays15;
          }
          else if (minUlChg > 15.0) {
            holdDays = FindBestReturns.holdDays15;
          }
          for (final String sExpiry : FindBestReturns.expiryDates) {

            final DateTime theExpiry = new DateTime(sExpiry, FindBestReturns.sdf);
            final DateTime buyDate = new DateTime();
            buyDate.setFirstWorkDay();
            final DateTime sellDate = new DateTime(buyDate);
            sellDate.add(DateTime.DATE, holdDays);
            sellDate.setFirstWorkDay();

            System.out.printf("%s\t%s\t%s\t%s%n", code, sExpiry, buyDate, sellDate);

            String sType = "PUT";
            if (otype == OptionsProcessor.ACALL) {
              sType = "CALL";
            }

            final String fname = String.format("data/options/%s-options.dat", code);

            final List<CboeOptionData> dil = CallPutList.readCboeData(fname, theExpiry, FindBestReturns.minOi);

            double ulMovePrice = 0.0;
            final double dilCPrice = CallPutList.getcPrice();
            if (otype == OptionsProcessor.ACALL) {
              ulMovePrice = (1.0 + minUlChg / 100.0) * dilCPrice;
            }
            else {
              ulMovePrice = (1.0 - minUlChg / 100.0) * dilCPrice;
            }

            System.out.printf("%.2f\t%.2f%n%n", dilCPrice, ulMovePrice);

            final String ofname = String.format("out/options/%s_%s_%d_%s.csv", code, sType, (int) minUlChg, theExpiry);
            try (PrintWriter pw = new PrintWriter(ofname)) {
              pw.printf("Expiry,Strike,IV,Mark,Buy,Sell,Gain,%s,%.2f,%.2f,%s,%s%n", code, dilCPrice, ulMovePrice, buyDate, sellDate);

              for (final CboeOptionData cod : dil) {

                OptionsProcessor op = null;
                if (otype == OptionsProcessor.ACALL) {
                  op = new OptionsProcessor(cod.call.optionData);
                }
                else {
                  op = new OptionsProcessor(cod.put.optionData);
                }
                op.setUlPrice(ulMovePrice);
                op.setSellDate(sellDate);
                final double iv = cod.put.iv * 1.50;
                op.setIv(iv);
                final double sellPrice = op.getPrice();

                FindBestReturns.processIt(theExpiry, cod, otype, buyDate, sellPrice, sellDate, pw);

              }
            }
          }
        }
      }
    }
  }

  /**
   *
   * @param theExpiry
   * @param cod
   * @param buyDate
   * @param sellPrice
   * @param pw
   */
  private static void processIt(DateTime theExpiry, CboeOptionData cod, int otype, DateTime buyDate, double sellPrice, DateTime sellDate,
      PrintWriter pw) {

    if (cod.expiry.isEqual(theExpiry)) {

      String pc = "";
      String.format("%s%n", cod.inputData);
      String.format("Ticker     : %s%n", cod.code);
      String.format("Expiry     : %s%n", cod.expiry);
      String.format("Strike     : %.2f%n", cod.strike);
      String.format("Sell Price : %6.2f%n", sellPrice);

      double gain = 0.0;
      double buyPrice = 0.0;
      double iv = 0.0;
      double mark = 0.0;

      OptionsProcessor opBuy = null;

      if (otype == OptionsProcessor.ACALL) {

        opBuy = new OptionsProcessor(cod.call.optionData);
        opBuy.setSellDate(buyDate);
        if (FindBestReturns.useMarkBuy) {
          buyPrice = cod.call.mark;
        }
        else {
          buyPrice = opBuy.getPrice();
        }
        if (cod.call.oi >= FindBestReturns.minOi && buyPrice > FindBestReturns.minOptionPrice && buyPrice < FindBestReturns.maxOptionPrice) {
          pc += String.format("%nCall%n%s", cod.call);
          buyPrice = cod.call.optionData.getPrice();
          iv = cod.call.optionData.getIv() * 100.0;
          mark = cod.call.mark;
        }
      }
      else if (otype == OptionsProcessor.APUT) {
        opBuy = new OptionsProcessor(cod.put.optionData);
        opBuy.setSellDate(buyDate);
        if (FindBestReturns.useMarkBuy) {
          buyPrice = cod.put.mark;
        }
        else {
          buyPrice = opBuy.getPrice();
        }
        if (cod.put.oi >= FindBestReturns.minOi && buyPrice > FindBestReturns.minOptionPrice && buyPrice < FindBestReturns.maxOptionPrice) {
          pc += String.format("%nPut%n%s%n", cod.put);
          buyPrice = cod.put.optionData.getPrice();
          iv = cod.put.optionData.getIv() * 100.0;
          mark = cod.put.mark;
        }
      }
      else {
        System.out.printf("ERROR! Bad Option Type : %d%n", otype);
      }
      if (pc.length() > 0) {
        gain = (sellPrice - buyPrice) / buyPrice * 100.0;
        String.format("Buy Price  : %6.2f%n", buyPrice);
        String.format("Gain       : %6.2f%%%n", gain);
        String.format("Sell Date  : %s%n", sellDate);

        pw.printf("%s,%f,%.2f%%,%.2f,%.2f,%.2f,%.1f%%%n", cod.expiry, cod.strike, iv, mark, buyPrice, sellPrice, gain);
      }
    }
  }

}
