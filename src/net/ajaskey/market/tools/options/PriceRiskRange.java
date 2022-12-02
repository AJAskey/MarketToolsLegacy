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
import java.io.PrintWriter;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;

public class PriceRiskRange {

  public static void main(String[] args) throws FileNotFoundException {

    final double minBuyPrice = 0.10;
    final int minOI = 1;
    final double sellExtension = 0.075;

    final DateTime buyDate = new DateTime();
    buyDate.add(DateTime.DATE, 1);

    final DateTime sellDate = new DateTime(buyDate);
    sellDate.add(DateTime.DATE, 15);

    final DateTime firstExpiry = new DateTime(sellDate);
    firstExpiry.add(DateTime.DATE, 1);

    final List<String> data = TextUtils.readTextFile("data/options/DMPData.csv", true);
    for (final String s : data) {

      for (int activeType = 1; activeType < 3; activeType++) {

        String activeTrade = "";
        if (activeType == OptionsProcessor.ACALL) {
          activeTrade = "CALL";
        }
        else {
          activeTrade = "PUT";
        }

        final RiskRange rr = new RiskRange(s);
        if (rr.isValid()) {
          final String activeCode = rr.code;

          double ulBuy = 0.0;
          double ulSell = 0.0;
          if (activeType == OptionsProcessor.ACALL) {
            ulBuy = rr.dmp2l;
            ulSell = rr.dmp2u + rr.dmp2u * sellExtension;
          }
          else {
            ulBuy = rr.dmp2u;
            ulSell = rr.dmp2l - rr.dmp2l * sellExtension;
          }

          final String dbgFname = String.format("out/options/%s.dbg", activeCode);
          try (PrintWriter pwDbg = new PrintWriter(dbgFname)) {

            pwDbg.printf("%s%n", rr);
            pwDbg.printf("%nType        : %s%n", activeTrade);

            final String fname = String.format("data/options/%s-options.dat", activeCode);
            final List<CboeOptionData> dil = CallPutList.readCboeData(fname, firstExpiry, buyDate, minOI);

            for (final CboeOptionData cd : dil) {
              System.out.printf("%s\t%.2f\t%d\t%d%n", activeCode, cd.strike, cd.call.oi, cd.put.oi);
            }

            final String outfile = String.format("out/options/%s-%s-%d.csv", activeCode, activeTrade, (int) ulBuy);
            try (PrintWriter pw = new PrintWriter(outfile)) {

              pw.printf("Id,Expiry,Strike,Opt Buy,Opt Sell,Profit,IV,%s,%s,%.2f,%.2f%n", buyDate, sellDate, ulBuy, ulSell);
              for (final CboeOptionData cod : dil) {
                CboeCallPutData option = null;
                if (activeType == OptionsProcessor.ACALL) {
                  option = cod.call;
                }
                else {
                  option = cod.put;
                }
                final String id = option.id;
                final OptionsProcessor op = new OptionsProcessor(option.optionData);
                op.setUlPrice(ulBuy);
                op.setSellDate(buyDate);
                if (activeCode.equalsIgnoreCase("VIX")) {
                  final double newIv = option.iv * 1.75;
                  op.setIv(newIv);
                }
                final double buyPrice = op.getPrice();
                pwDbg.printf("%nInitial buy : %s\t%.2f\t%.2f\tStrike : %.2f\tIV : %.4f\tDIL IV : %.4f%n", buyDate, buyPrice, ulBuy, op.getStrike(),
                    op.getIv(), op.getIv());

                if (buyPrice >= minBuyPrice && option.oi >= minOI) {
                  op.setSellDate(sellDate);
                  op.setUlPrice(ulSell);
                  final double sellPrice = op.getPrice();

                  final double chg = (sellPrice - buyPrice) / buyPrice * 100.0;
                  pwDbg.printf("Sell        : %s\t%.2f\t%.2f%nProfit      : %.2f%%%n%s%n  OI        : %d%n", sellDate, sellPrice, ulSell, chg, op,
                      option.oi);

                  pw.printf("%s,%s,%.2f,%.2f,%.2f,%.2f%%,%.4f%n", id, op.getExpiry(), op.getStrike(), buyPrice, sellPrice, chg, option.iv);
                }
              }
            }
          }
        }
      }
    }
  }

}
