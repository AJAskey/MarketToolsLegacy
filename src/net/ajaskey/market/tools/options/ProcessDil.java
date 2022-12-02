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
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;

public class ProcessDil {

  final static int MinOI = 50;

  public static void main(String[] args) {
    try {
      AddCboeDataFiles.main(null);
    }
    catch (final IOException e) {
    }

    final DateTime buyDate = new DateTime();
    buyDate.add(DateTime.DATE, 1);

    final DateTime firstExpiry = new DateTime(buyDate);
    firstExpiry.add(DateTime.DATE, 10);

    final String code = "qqq";
    final String type = "CALL";

    int activeType = OptionsProcessor.ACALL;
    if (type.toUpperCase().contains("PUT")) {
      activeType = OptionsProcessor.APUT;
    }

    final String fname = String.format("data/options/%s-options.dat", code);
    final List<CboeOptionData> dil = CallPutList.readCboeData(fname, firstExpiry, buyDate, ProcessDil.MinOI);

    double highStrike = 0.0;
    double lowStrike = 0.0;
    if (activeType == OptionsProcessor.ACALL) {
      highStrike = CallPutList.getcPrice() * 1.121;
      lowStrike = CallPutList.getcPrice() * 0.90;
      System.out.printf("%.2f\t%.2f\t%.2f%n", lowStrike, CallPutList.getcPrice(), highStrike);
    }
    else {
      highStrike = CallPutList.getcPrice() * 1.021;
      lowStrike = CallPutList.getcPrice() * 0.899;
      System.out.printf("%.2f\t%.2f%n", highStrike, lowStrike);
    }

    final List<CboeCallPutData> options = new ArrayList<>();
    for (final CboeOptionData cod : dil) {

      if (cod.strike < highStrike && cod.strike > lowStrike) {

        if (cod.call.vol > 0 && cod.call.oi >= ProcessDil.MinOI) {
          if (cod.call.mark > 0.099) {
            if (activeType == OptionsProcessor.ACALL) {
              options.add(cod.call);
            }
            else {
              options.add(cod.put);
            }
          }
        }
      }
    }

    final double processStrike = CallPutList.getcPrice() * 0.99;
    System.out.printf("processStrike %.2f%n", processStrike);
    for (final CboeCallPutData opt : options) {

      if (opt.getStrike() > processStrike) {

        System.out.printf("Calling findProfitableCallStrikes for %.1f%n", opt.getStrike());

        final List<String> strikes = ProcessDil.findProfitableCallStrikes(opt, options, CallPutList.getcPrice());

        if (strikes.size() > 0) {
          System.out.println(opt);
          System.out.printf("%s,%.1f, %.1f%n", opt.id, opt.optionData.getStrike(), opt.mark);
          for (final String s : strikes) {
            System.out.println(s);
          }
          System.out.println("");
        }
      }
    }
  }

  /**
   *
   * @param optList
   * @param option
   * @param offset
   * @return
   */
  private static double findOffsetStrike(List<CboeCallPutData> optList, CboeCallPutData option, double offset) {

    double profit = 0.0;
    final double oStrike = option.strike + offset;
    CboeCallPutData activeOpt = null;

    System.out.printf("findOffsetStrike : Strike %.1f\t Offset %.2f\t%.2f%n", option.strike, offset, option.getPrice());

    final int pos = (int) Math.abs(offset);
    for (int i = pos; i < optList.size(); i++) {
      if (optList.get(i).getStrike() == oStrike) {
        activeOpt = optList.get(i);
        System.out.printf("  Found == Strike %.1f%n", activeOpt.strike);
        break;
      }
      else if (optList.get(i).getStrike() > oStrike) {
        activeOpt = optList.get(i);
        System.out.printf("  Found == Strike %.1f%n", activeOpt.strike);
        break;
      }
      optList.get(i);
    }

    if (activeOpt != null) {
      if (activeOpt.getPrice() > 0.0) {
        profit = (option.getPrice() - activeOpt.getPrice()) / activeOpt.getPrice() * 100.0;
        System.out.printf("%.1f%%\t%.2f\t%.2f%n", profit, option.getPrice(), activeOpt.getPrice());
      }
    }
    else {
      System.out.println("Found no offset strike.");
    }

    return profit;
  }

  /**
   *
   * @param opt
   * @param calls
   * @param cPrice
   * @return
   */
  private static List<String> findProfitableCallStrikes(CboeCallPutData opt, List<CboeCallPutData> calls, double cPrice) {

    final List<String> ret = new ArrayList<>();
    final List<CboeCallPutData> tmpList = new ArrayList<>();

    for (final CboeCallPutData call : calls) {

      if (opt.optionData.getExpiry().isEqual(call.optionData.getExpiry())) {
        if (opt.optionData.getStrike() != call.optionData.getStrike()) {
          if (call.oi >= ProcessDil.MinOI) {

            System.out.printf("Adding %.1f to tmpList%n", call.strike);
            tmpList.add(call);

          }
        }
      }
    }

    for (final CboeCallPutData call : tmpList) {

      final double profit = ProcessDil.findOffsetStrike(tmpList, call, -10.0);

      ret.add(String.format("\t%.1f, %.2f, %.4f, %d, %d, %.1f%%", call.getStrike(), call.mark, call.iv, call.vol, call.oi, profit));

    }

    return ret;
  }

}
