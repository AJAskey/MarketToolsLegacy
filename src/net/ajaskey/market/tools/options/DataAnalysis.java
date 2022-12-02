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
import java.util.List;

import net.ajaskey.common.DateTime;

public class DataAnalysis {

  public static void main(String[] args) throws FileNotFoundException {
    try {
      AddCboeDataFiles.main(null);
    }
    catch (final IOException e) {
      e.printStackTrace();
    }

    final DateTime buyDate = new DateTime(2020, DateTime.JANUARY, 17);
    // buyDate.add(DateTime.DATE, 1);

    final DateTime firstExpiry = new DateTime(buyDate);
    firstExpiry.add(DateTime.DATE, 10);

    final String code = "qqq";
    final String type = "put";

    int activeType = OptionsProcessor.ACALL;
    if (type.toUpperCase().contains("PUT")) {
      activeType = OptionsProcessor.APUT;
    }

    final String fname = String.format("data/options/%s-options.dat", code);
    final List<CboeOptionData> dil = CallPutList.readCboeData(fname, firstExpiry, buyDate, 10);

    double highStrike = 0.0;
    double lowStrike = 0.0;
    if (activeType == OptionsProcessor.ACALL) {
      highStrike = CallPutList.getcPrice() * 1.201;
      lowStrike = CallPutList.getcPrice() * 0.94;
      System.out.printf("%.2f\t%.2f%n", lowStrike, highStrike);
    }
    else {
      highStrike = CallPutList.getcPrice() * 1.06;
      lowStrike = CallPutList.getcPrice() * 0.799;
      System.out.printf("%.2f\t%.2f%n", highStrike, lowStrike);
    }

    final String foutname = String.format("out/options/%s-%s-options-analysis.txt", code, type);
    try (PrintWriter pw = new PrintWriter(foutname)) {
      for (final CboeOptionData cod : dil) {
        if (cod.valid) {

          CboeCallPutData option = cod.put;
          if (activeType == OptionsProcessor.ACALL) {
            option = cod.call;
          }

          if (option.vol > 999) {
            if (option.mark > 0.249) {

              if (cod.strike > lowStrike && cod.strike < highStrike) {
                pw.printf("%s, %.1f\tUnderlying --> %s%n", cod.expiry, cod.strike, CallPutList.getInfo());
                pw.printf("\tCall Data vs Calc : %s\t%d %d%n", option.id, option.vol, option.oi);
                pw.printf("\t\tIV    : %.4f, %.4f%n", option.iv, option.optionData.getIv());
                // pw.printf("\t\tDelta : %.4f, %.4f%n", option.delta,
                // option.optionData.getDelta());
                final double diff = (option.mark - option.optionData.getPrice()) / option.optionData.getPrice() * 100.0;
                pw.printf("\t\tPrice : %.4f, %.4f\t%.2f%%%n", option.mark, option.optionData.getPrice(), diff);
              }
            }
          }
        }
      }
    }

  }

}
