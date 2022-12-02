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
import java.text.ParseException;

public class ProcessVixChanges {

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
//    if (args.length < 1) {
//      System.out.println("Command line format : Type --->   PUT");
//      return;
//    }
//
//    String fname = String.format("data/options/%s-options.dat", "vix");
//    DataItemList dil = DataItemList.readOptionData(fname, 0.10, 0, 249);
//
//    if (dil == null) {
//      return;
//    }
//
//    List<DataItem> theList = null;
//    double upDown = 0.01;
//
//    String type = args[0].toUpperCase().trim();
//    if (type.contains("P")) {
//      theList = dil.putList;
//      upDown = -upDown;
//      System.out.println("Processing PUTs on : " + fname);
//      DataItem.dbgPw.println("Processing PUTs on : " + fname);
//    } else {
//      theList = dil.callList;
//      System.out.println("Processing CALLs on : " + fname);
//      DataItem.dbgPw.println("Processing CALLs on : " + fname);
//    }
//
//    double highEnd = dil.last * 1.25;
//    double lowEnd = dil.last * 0.75;
//
//    String sPrice = String.format("%d", (int) (dil.last * 100.0));
//    String outfile = String.format("out/%s-%s-%s-options-changes.csv", "vix", sPrice, type);
//    try (PrintWriter pw = new PrintWriter(outfile)) {
//
//      pw.printf("%s,, %.2f%n", "VIX", dil.last);
//
//      for (DataItem diOption : theList) {
//
//        DateTime sellDate = new DateTime();
//        DataItem di = diOption.getCallPrice(dil.last, sellDate, diOption.iv);
//
//        double newIv = diOption.iv * 1.10;
//
//        for (int j = 1; j < 8; j++) {
//
//          sellDate.add(DateTime.DATE, 14);
//
//          if (sellDate.isGreaterThan(di.expiry)) {
//            break;
//          }
//
//          String out = String.format("%s, %s, %.2f, %.2f,", di.expiry, sellDate, di.strike, di.price);
//          String out2 = "";
//
//          double ptot = 0.0;
//
//          double[] vixPrice = { 15.0, 25.0, 30.0, 35.0, 50.0 };
//
//          boolean posOpt = false;
//          for (int i = 0; i < 5; i++) {
//
//            double ul = vixPrice[i];
//
//            DataItem diToday = diOption.getCallPrice(ul, sellDate, newIv);
//            double chg = (diToday.price - diOption.last) / diOption.last;
//            ptot += chg;
//
//            // System.out.printf("%.2f\t%.2f\t%.2f%%%n", diToday.price, diOption.last, chg *
//            // 100.0);
//
//            if (chg > 0.50) {
//              posOpt = true;
//            }
//
//            out += String.format(", %f", diToday.price);
//            out2 += String.format(", %f", chg);
//          }
//          double avg = ptot / 5.0;
//          if ((posOpt) && (avg > .50)) {
//            String s = String.format("%f,, %f", avg, newIv);
//            pw.println(out + "," + out2 + ",," + s + ",," + diOption.last);
//          }
//        }
//      }
//    }
//
//    DataItem.dbgPw.close();
  }

}
