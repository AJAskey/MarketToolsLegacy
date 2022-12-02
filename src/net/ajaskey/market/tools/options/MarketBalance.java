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

public class MarketBalance {

  public static void main(String[] args) throws FileNotFoundException, IOException {

//    DataItem.dbgPw = new PrintWriter("out/dataItem.dbg");
//
//    String fname = String.format("data/options/%s-options.dat", args[0].trim());
//    DataItemList dil = DataItemList.readOptionData(fname, 0.10, 1, 10000000);
//
//    long totOi = 0;
//
//    try (PrintWriter pw = new PrintWriter("out/test.csv")) {
//
//      DateTime today = new DateTime();
//      pw.println("Expiry,Strike,OI,Vol,Last,Value,Premium");
//
//      for (DataItem di : dil.putList) {
//        DataItem diToday = di.getPutPrice(dil.last, today, di.iv);
//        // System.out.println(di.toCsv());
//        double premium = (diToday.price - di.last) / di.last;
//        pw.printf("%s,%f,%d,%d,%f,%f,%f%n", di.expiry, di.strike, di.oi, di.volume, di.last, diToday.price, premium);
//        totOi += di.oi;
//      }
//    }
    System.out.println("Done.");
  }

}
