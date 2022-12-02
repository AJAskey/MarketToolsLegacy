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

import java.text.SimpleDateFormat;
import java.util.List;

import net.ajaskey.common.DateTime;

public class CboeOptionData {

  private static final SimpleDateFormat inSdf  = new SimpleDateFormat("MM/dd/yyyy");
  private static final SimpleDateFormat outSdf = new SimpleDateFormat("dd-MMM-yyyy");

  public static CboeOptionData findStrike(List<CboeOptionData> list, int s, DateTime expiry) {
    for (final CboeOptionData cod : list) {
      if ((int) cod.strike == s && cod.expiry.isLessThanOrEqual(expiry)) {
        return cod;
      }
    }
    return null;
  }

  public CboeCallPutData call;
  public String          code;

  public DateTime expiry;
  public String   inputData;

  public CboeCallPutData put;

  public double strike;

  public boolean valid;

  public CboeOptionData(String data, double ulPrice, String c, DateTime theExpiry) {

    try {

      this.valid = false;

      this.inputData = data;
      this.code = c;

      final String[] fld = data.split(",");

      this.call = new CboeCallPutData();
      this.put = new CboeCallPutData();

      this.expiry = new DateTime(fld[0].trim(), CboeOptionData.inSdf);
      this.expiry.setSdf(CboeOptionData.outSdf);

      if (this.expiry.isEqual(theExpiry)) {

        this.valid = true;

        this.strike = Double.parseDouble(fld[11].trim());
        this.call.strike = this.strike;
        this.put.strike = this.strike;

        // Call data
        this.call.id = fld[1].trim();
        this.call.last = Double.parseDouble(fld[2].trim());
        this.call.net = Double.parseDouble(fld[3].trim());
        this.call.bid = Double.parseDouble(fld[4].trim());
        this.call.ask = Double.parseDouble(fld[5].trim());
        this.call.vol = Integer.parseInt(fld[6].trim());
        this.call.iv = Double.parseDouble(fld[7].trim());
        this.call.delta = Double.parseDouble(fld[8].trim());
        this.call.gamma = Double.parseDouble(fld[9].trim());
        this.call.oi = Integer.parseInt(fld[10].trim());

        this.call.premium = 0.0;

        if (this.call.iv > 0.0) {
          final double iv = this.call.iv * 1.04;
          this.call.optionData = new OptionsProcessor(OptionsProcessor.ACALL, this.call.id, this.strike, ulPrice, this.expiry, iv);
        }

        // Put data
        this.put.id = fld[12].trim();
        this.put.last = Double.parseDouble(fld[13].trim());
        this.put.net = Double.parseDouble(fld[14].trim());
        this.put.bid = Double.parseDouble(fld[15].trim());
        this.put.ask = Double.parseDouble(fld[16].trim());
        this.put.vol = Integer.parseInt(fld[17].trim());
        this.put.iv = Double.parseDouble(fld[18].trim());
        this.put.delta = Double.parseDouble(fld[19].trim());
        this.put.gamma = Double.parseDouble(fld[20].trim());
        this.put.oi = Integer.parseInt(fld[21].trim());
        if (this.put.iv > 0.0) {
          final double iv = this.put.iv * 1.00;
          this.put.optionData = new OptionsProcessor(OptionsProcessor.APUT, this.put.id, this.strike, ulPrice, this.expiry, iv);
        }
        this.call.mark = (this.call.bid + this.call.ask) / 2.0;
        this.put.mark = (this.put.bid + this.put.ask) / 2.0;

        double price = this.call.getPrice();
        this.call.premium = (this.call.mark - price) / price;
        price = this.put.getPrice();
        this.put.premium = (this.put.mark - price) / price;

        if (this.call != null && this.inputData != null) {
          this.valid = this.call.optionData.valid && this.put.optionData.valid;
        }
        else {
          this.valid = false;
        }
      }
    }
    catch (final Exception e) {
      this.valid = false;
      // e.printStackTrace();
    }

  }

  public CboeOptionData(String data, double ulPrice, String c, DateTime firstExpiry, DateTime bDate) {

    try {

//      if ((data.length() < 25) || (ulPrice < 0.10)) {
//        this.valid = false;
//        return;
//      }

      this.valid = true;

      this.inputData = data;
      this.code = c;

      final String[] fld = data.split(",");
//      if (fld.length < 20) {
//        this.valid = false;
//        return;
//      }

      this.call = new CboeCallPutData();
      this.put = new CboeCallPutData();

      this.expiry = new DateTime(fld[0].trim(), CboeOptionData.inSdf);
      this.expiry.setSdf(CboeOptionData.outSdf);

      if (this.expiry.isLessThanOrEqual(firstExpiry)) {
        this.valid = false;
        return;
      }

      this.strike = Double.parseDouble(fld[11].trim());
      this.call.strike = this.strike;
      this.put.strike = this.strike;

      // Call data
      this.call.id = fld[1].trim();
      this.call.last = Double.parseDouble(fld[2].trim());
      this.call.net = Double.parseDouble(fld[3].trim());
      this.call.bid = Double.parseDouble(fld[4].trim());
      this.call.ask = Double.parseDouble(fld[5].trim());
      this.call.vol = Integer.parseInt(fld[6].trim());
      this.call.iv = Double.parseDouble(fld[7].trim());
      this.call.delta = Double.parseDouble(fld[8].trim());
      this.call.gamma = Double.parseDouble(fld[9].trim());
      this.call.oi = Integer.parseInt(fld[10].trim());

      this.call.premium = 0.0;

      if (this.call.iv > 0.0) {
        final double iv = this.call.iv * 1.04;
        this.call.optionData = new OptionsProcessor(OptionsProcessor.ACALL, this.call.id, this.strike, ulPrice, this.expiry, iv);
      }

      // Put data
      this.put.id = fld[12].trim();
      this.put.last = Double.parseDouble(fld[13].trim());
      this.put.net = Double.parseDouble(fld[14].trim());
      this.put.bid = Double.parseDouble(fld[15].trim());
      this.put.ask = Double.parseDouble(fld[16].trim());
      this.put.vol = Integer.parseInt(fld[17].trim());
      this.put.iv = Double.parseDouble(fld[18].trim());
      this.put.delta = Double.parseDouble(fld[19].trim());
      this.put.gamma = Double.parseDouble(fld[20].trim());
      this.put.oi = Integer.parseInt(fld[21].trim());
      if (this.put.iv > 0.0) {
        final double iv = this.put.iv * 1.00;
        this.put.optionData = new OptionsProcessor(OptionsProcessor.APUT, this.put.id, this.strike, ulPrice, this.expiry, iv);
      }
      this.call.mark = (this.call.bid + this.call.ask) / 2.0;
      this.put.mark = (this.put.bid + this.put.ask) / 2.0;

      double price = this.call.getPrice();
      this.call.premium = (this.call.mark - price) / price;
      price = this.put.getPrice();
      this.put.premium = (this.put.mark - price) / price;

      if (this.call != null && this.inputData != null) {
        this.valid = this.call.optionData.valid && this.put.optionData.valid;
      }
      else {
        this.valid = false;
      }

    }
    catch (final Exception e) {
      this.valid = false;
      // e.printStackTrace();
    }

  }

  @Override
  public String toString() {
    String ret = "";
    if (this.valid) {

      ret += String.format("%s%n", this.inputData);
      ret += String.format("Ticker    : %s%n", this.code);
      ret += String.format("Expiry    : %s%n", this.expiry);
      ret += String.format("Strike    : %.2f%n", this.strike);
      ret += String.format("%nCall%n%s%n", this.call);
      ret += String.format("Put%n%s%n", this.put);
    }
    return ret;
  }

}
