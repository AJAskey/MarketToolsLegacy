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
import java.text.SimpleDateFormat;
import java.util.List;

import net.ajaskey.common.DateTime;

public class OptionPrice {

  public static void main(String[] args) throws IOException, ParseException {

    final DateTime firstExpiry = new DateTime();

    // System.out.println(args.length);

    if (args.length < 4) {
      System.out.println("Command line format : Code Type Expiry Strike [Chg] --->  SPY PUT 12Jun2020 300.0 [0.05]");
      return;
    }

    double chg = 0.01;
    if (args.length == 5) {
      chg = Double.parseDouble(args[4].trim());
    }

    final String code = args[0].trim();

    final String fname = String.format("data/options/%s-options.dat", code);

    final List<CboeOptionData> dil = CallPutList.readCboeData(fname, firstExpiry, new DateTime(), 1);
    final double dilCPrice = CallPutList.getcPrice();

    final SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");

    final String type = args[1].trim();
    int typeInt = OptionsProcessor.ACALL;
    if (type.toUpperCase().contains("P")) {
      typeInt = OptionsProcessor.APUT;
    }

    final DateTime expiry = new DateTime(args[2].trim(), sdf);
    final double strike = Double.parseDouble(args[3].trim());

    System.out.printf("%n%s  %s  %s  %.2f%n%n", code.toUpperCase(), type.toUpperCase(), expiry, strike);

    new DateTime();
    final DateTime inTwoWeeks = new DateTime();
    inTwoWeeks.add(DateTime.DATE, 7);

    final CboeCallPutData odFound = CallPutList.findData(expiry, strike, typeInt, dil);

    if (odFound == null) {
      System.out.println("No match found in CBOE data!");
      return;
    }

    System.out.println(odFound);

    final double newIv = odFound.optionData.findIv(odFound.mark, odFound.iv);

    final double price = odFound.getPrice();
    final double premium = (odFound.mark - price) / price * 100.0;

    System.out.printf("%.2f on %s when underlying is at %.2f - Price Now  NewIV:%.4f on Mark:%.2f with premium of %.1f%%%n", price,
        odFound.getSellDate(), dilCPrice, newIv, odFound.mark, premium);

    if (typeInt == OptionsProcessor.APUT) {
      chg *= -1.0;
    }

    double ul = dilCPrice;
    odFound.optionData.setIv(newIv);
    for (int i = 1; i < 25; i++) {

      if (ul < 1.0) {
        break;
      }

      odFound.optionData.setSellDate(inTwoWeeks);
      odFound.optionData.setUlPrice(ul);

      System.out.printf("%.2f on %s when underlying is at %.2f%n", odFound.optionData.getPrice(), odFound.optionData.getSellDate(),
          odFound.optionData.getUlPrice());

      // 1% change in underlying each 2-week period
      ul = dilCPrice * (1.0 + i * chg);

      inTwoWeeks.add(DateTime.DATE, 14);
      if (inTwoWeeks.isGreaterThan(expiry)) {
        break;
      }
    }
  }

}
