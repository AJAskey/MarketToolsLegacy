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
import java.text.ParseException;
import java.util.List;

import net.ajaskey.common.DateTime;

public class ProcessOptions {

  static double maxPremium = 10000.0;
  static double minIv      = 0.0;
  static double minLast    = 0.0499;
  static int    minOi      = 0;

  /**
   * net.ajaskey.market.tools.options.main
   *
   * @param args
   * @throws IOException
   */
  public static void main(final String[] args) throws ParseException, IOException {

//    DataItem.dbgPw = new PrintWriter("out/dataItem.dbg");
//
    if (args.length < 2) {
      System.out.println("Command line format : Type Filename-Prefix [Position Size]  --->   PUT spy [1000.0]");
      return;
    }

    final String code = args[1].trim();
    final String fname = String.format("data/options/%s-options.dat", code);

    final String type = args[0].toUpperCase().trim();
    int typeInt = OptionsProcessor.ACALL;
    if (type.contains("P")) {
      typeInt = OptionsProcessor.APUT;
      System.out.println("Processing PUTs on : " + fname);
      // DataItem.dbgPw.println("Processing PUTs on : " + fname);
    }
    else {
      System.out.println("Processing CALLs on : " + fname);
      // DataItem.dbgPw.println("Processing CALLs on : " + fname);
    }

    double positionSize = 1000.0;

    if (args.length > 2) {
      positionSize = Double.parseDouble(args[2]);
    }
//
//    DataItem.dbgPw.println("Reading : " + fname);
//
//    DataItemList dil = DataItemList.readOptionData(fname, 0.10, 0, 249);
    final List<CboeOptionData> dil = CallPutList.readCboeData(fname, new DateTime(), new DateTime(), 0);

//
//    DataItem.dbgPw.println("Finished Reading : " + fname);
//
    final String commas = ",,,,,,,,,,,New Price/Contract,,,,";
//
    if (dil == null) {
      return;
    }
//
    final double cPrice = CallPutList.getcPrice();
//
    final double priceChg = 0.10;
    final int daysToSell = 45;
//
    final String outfile = String.format("out/%s-options-analysis.csv", args[1].trim());
    try (PrintWriter pw = new PrintWriter(outfile)) {
//
      final String note = String.format("Assume %d%% price change in %d days", (int) (priceChg * 100.0), daysToSell);

      pw.printf("%s %.2f,Created:,%s,%sGain/Loss on $%d Position,,,,,,%s%n", code, cPrice, new DateTime(), commas, (int) positionSize, note);
//
      pw.println(
          "Type,Expiry,Id,Vol,OI,IV,Delta,Theta,Gamma,Strike,Current,Calculated,Premium,,IV*1.0,IV*1.5,IV*2.0,,IV*1.0,IV*1.5,IV*2.0,,Contracts,Price,Date Of Sale");
//
      final DateTime closeDate = new DateTime();
      closeDate.add(DateTime.DATE, daysToSell);
      System.out.println("Closing position on : " + closeDate);
      if (typeInt == OptionsProcessor.ACALL) {
      }
      else {
      }

      for (final CboeOptionData od : dil) {
        CboeCallPutData cpd = null;
        if (typeInt == OptionsProcessor.ACALL) {
          cpd = od.call;
        }
        else {
          cpd = od.put;
        }
        if (cpd.mark > ProcessOptions.minLast && cpd.iv > ProcessOptions.minIv && cpd.oi > ProcessOptions.minOi) {

        }
      }

//
//      for (DataItem di : theList) {
//
//        if ((di.last > minLast) && (di.iv > minIv) && (di.oi > minOi)) {
//
//          // if ((di.strike <= cPrice) && (di.premium < maxPremium)) {
//          if (di.premium < maxPremium) {
//            double low = cPrice * 0.70;
//            double high = cPrice * 1.30;
//            if ((di.strike >= low) && (di.strike <= high)) {
//
//              String str1 = di.toCsv();
//
//              String str2 = estimateProfit(di, cPrice * priceMove, positionSize, closeDate);
//
//              String str = str1 + str2;
//
//              pw.println(str);
//            }
//          }
//        }
//      }
    }
//
//    DataItem.dbgPw.close();
  }

  /**
   *
   * @param di
   * @param cp
   * @param posSize
   * @param closeDate
   * @return
   */
//  }

}
