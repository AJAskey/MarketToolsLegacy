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
package net.ajaskey.market.tools.SIP;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.market.optuma.PriceData;
import net.ajaskey.market.optuma.TickerPriceData;

public class CompanyBuyBacks {

  public static void main(String[] args) throws FileNotFoundException {

    final List<String> data = TextUtils.readTextFile("data/SP500 SHARES VS PRICE.TXT", true);

    try (PrintWriter pw = new PrintWriter("out/buybacks.txt")) {

      pw.println("Code\tName\tExchange\tShares 5to4\tShares 4to3\tShares 3to2\tShares2to1\tTotal Cost\tCurrent Value\tDelta");

      for (final String s : data) {

        final String fld[] = s.split("\t");
        final String code = fld[0].replace("\"", "").trim();
        final String name = fld[1].replace("\"", "").trim();
        final String exch = fld[2].replace("\"", "").trim();

        if (code.equalsIgnoreCase("DXC")) {
          System.out.println(code);
        }

        if (exch.length() > 0) {
          final TickerPriceData priceData = new TickerPriceData(exch, code);

          if (priceData.getNumPrices() > 0) {

            // double avg2015 = getYearlyAverage(priceData, 2015);
            final double avg2016 = CompanyBuyBacks.getYearlyAverage(priceData, 2016); // 4
            final double avg2017 = CompanyBuyBacks.getYearlyAverage(priceData, 2017); // 3
            final double avg2018 = CompanyBuyBacks.getYearlyAverage(priceData, 2018); // 2
            final double avg2019 = CompanyBuyBacks.getYearlyAverage(priceData, 2019); // 1
            final double lastPrice = priceData.getLatest();

            if (avg2016 > 0.0 && avg2017 > 0.0 && avg2018 > 0.0 && avg2019 > 0.0 && lastPrice > 0.0) {
              final double shr2to1 = Double.parseDouble(fld[4].trim()); // 1
              final double shr3to2 = Double.parseDouble(fld[5].trim()); // 2
              final double shr4to3 = Double.parseDouble(fld[6].trim()); // 3
              final double shr5to4 = Double.parseDouble(fld[7].trim()); // 4

              final double buyCost5to4 = shr5to4 * avg2016;
              final double buyCost4to3 = shr4to3 * avg2017;
              final double buyCost3to2 = shr3to2 * avg2018;
              final double buyCost2to1 = shr2to1 * avg2019;

              final double totShares = shr5to4 + shr4to3 + shr3to2 + shr2to1;
              final double totCost = buyCost5to4 + buyCost4to3 + buyCost3to2 + buyCost2to1;

              final double currentValue = totShares * lastPrice;
              final double delta = currentValue - totCost;

              pw.printf("%s\t%s\t%s\t%f\t%f\t%f\t%f\t%f\t%f\t%f%n", code, name, exch, buyCost5to4, buyCost4to3, buyCost3to2, buyCost2to1, totCost,
                  currentValue, delta);
            }
          }
          else {
            System.out.println("ERROR : No price data for : " + code);
          }
        }
      }
    }
  }

  private static double getYearlyAverage(TickerPriceData pd, int yr) {

    double ret = 0.0;
    int knt = 0;
    double sum = 0.0;
    for (final PriceData td : pd.getPriceData()) {
      final int year = td.date.getYear();
      if (year == yr) {
        // System.out.println(td);
        sum += td.close;
        knt++;
      }
      else if (year > yr) {
        break;
      }
    }
    if (knt > 0) {
      ret = sum / knt;
    }
    return ret;
  }

  int years[] = { 2015, 2016, 2017, 2018, 2019 };

}
