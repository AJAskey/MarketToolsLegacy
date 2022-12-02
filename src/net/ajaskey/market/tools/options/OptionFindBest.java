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

public class OptionFindBest {

  public static void main(String[] args) throws IOException, ParseException {

    OptionsProcessor.OpenDebug();

    final DateTime sellDate = new DateTime();
    sellDate.add(DateTime.DATE, 10);

    final DateTime firstExpiry = new DateTime(sellDate);
    firstExpiry.add(DateTime.DATE, 10);

    // System.out.println(args.length);

    if (args.length < 2) {
      System.out.println("Command line format : Code Type --->  SPY PUT ");
      return;
    }

    double chg = 0.10;
    if (args.length == 3) {
      chg = Double.parseDouble(args[2].trim());
    }

    final String code = args[0].trim();

    final String fname = String.format("data/options/%s-options.dat", code);

    final List<CboeOptionData> dil = CallPutList.readCboeData(fname, firstExpiry, new DateTime(), 1);

    final double dilCPrice = CallPutList.getcPrice();

    final String type = args[1].trim();
    int typeInt = OptionsProcessor.ACALL;

    final String outfile = String.format("out/%s-%d-%s-%d-options-analysis.csv", code.toUpperCase(), (int) dilCPrice, type.toUpperCase(),
        (int) Math.round(chg * 100.0));

    if (type.toUpperCase().contains("P")) {
      typeInt = OptionsProcessor.APUT;
      chg = -chg;
    }

    final double ul = dilCPrice * (1.0 + chg);

    try (PrintWriter pw = new PrintWriter(outfile)) {

      // final double y = firstExpiry.getDeltaYears(today);

      pw.printf("Expiry,Strike,,Price,Mark,Premium,,Delta,IV,,SellPrice,Profit,,LossPrice,Loss,,Id,,%s,%s,%.2f,%.2f%n", CallPutList.getCode(),
          sellDate, dilCPrice, ul);

      for (final CboeOptionData cod : dil) {

        if (cod.expiry.isGreaterThan(sellDate) && cod.expiry.isGreaterThan(firstExpiry)) {

          CboeCallPutData option = null;
          if (typeInt == OptionsProcessor.ACALL) {
            option = cod.call;
          }
          else {
            option = cod.put;
          }

          final double price = option.getPrice();

          if (price > 0.0499) {

            final double premium = (option.mark - price) / price * 100.0;

            OptionsProcessor.printDbg("\nmain() :: Creating new instance.");
            final OptionsProcessor op = new OptionsProcessor(option.optionData);

            op.setSellDate(sellDate);
            op.setUlPrice(ul);

            final double sellPrice = op.getPrice();

            final double buyPrice = option.mark;

            final double profit = (sellPrice - buyPrice) / buyPrice * 100.0;

            if (profit > 50.0 && buyPrice > 0.0) {

              option.optionData.setSellDate(sellDate);
              final double lossPrice = option.optionData.getPrice();
              final double loss = (lossPrice - buyPrice) / buyPrice * 100.0;

              op.setGreeks();

              pw.printf("%s,%.2f,,%.2f,%.2f,%.2f%%,,%.4f,%.4f,,%.2f,%.2f%%,,%.2f,%.2f%%,,%s%n", cod.expiry, cod.strike, price, option.mark, premium,
                  option.delta, option.iv, sellPrice, profit, lossPrice, loss, option.id);

              final String s = String.format("main() :: Writing instance data%n%s%n", op.toString());
              OptionsProcessor.printDbg(s);

            }
          }
        }
      }
    }
    OptionsProcessor.CloseDebug();

  }

}
