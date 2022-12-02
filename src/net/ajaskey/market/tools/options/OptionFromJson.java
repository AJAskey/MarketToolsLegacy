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
import java.util.Iterator;

import org.json.simple.JSONObject;

import net.ajaskey.common.DateTime;

public class OptionFromJson {

  private static Double getDouble(JSONObject jo, String key) {
    final Double ret = 0.0;
    try {
      return ((Number) jo.get(key)).doubleValue();
    }
    catch (final Exception e) {
    }
    return ret;
  }

  private static Long getLong(JSONObject jo, String key) {
    final Long ret = 0L;
    try {
      return ((Number) jo.get(key)).longValue();
    }
    catch (final Exception e) {
    }
    return ret;
  }

  public Double   ask;
  public Double   bid;
  public Double   change;
  public Double   changePercent;
  public String   contractName;
  public String   contractSize;
  public String   currency;
  public Long     daysBeforeExpiration;
  public Double   delta;
  public DateTime expirationDate;
  public Double   gamma;
  public Double   impliedVolatility;
  public Double   intrinsicValue;
  public Boolean  itm;
  public Double   lastPrice;
  public DateTime lastTradeDateTime;
  public Long     openInterest;
  public Double   rho;
  public Double   strike;
  public Double   theoretical;
  public Double   theta;
  public Double   timeValue;
  public String   type;
  public DateTime updatedAt;
  public Double   vega;

  public Long volume;

  SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
  SimpleDateFormat sdfOut = new SimpleDateFormat("dd-MMM-yyyy");

  SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  private final Iterator<?> itr;

  public OptionFromJson(Iterator<?> inItr) {
    this.itr = inItr;
  }

  public void get() {
    String s = "";
    while (this.itr.hasNext()) {
      final JSONObject jopt = (JSONObject) this.itr.next();
      this.contractName = (String) jopt.get("contractName");
      this.contractSize = (String) jopt.get("contractSize");
      this.currency = (String) jopt.get("currency");
      this.itm = Boolean.parseBoolean((String) jopt.get("inTheMoney"));

      s = (String) jopt.get("lastTradeDateTime");
      this.lastTradeDateTime = new DateTime(s, this.sdftime);
      s = (String) jopt.get("expirationDate");
      this.expirationDate = new DateTime(s, this.sdf, this.sdfOut);

      this.strike = OptionFromJson.getDouble(jopt, "strike");
      this.lastPrice = OptionFromJson.getDouble(jopt, "lastPrice");
      this.bid = OptionFromJson.getDouble(jopt, "bid");
      this.ask = OptionFromJson.getDouble(jopt, "ask");
      this.change = OptionFromJson.getDouble(jopt, "change");
      this.volume = OptionFromJson.getLong(jopt, "volume");
      this.openInterest = OptionFromJson.getLong(jopt, "openInterest");

      this.impliedVolatility = OptionFromJson.getDouble(jopt, "impliedVolatility");
      this.delta = OptionFromJson.getDouble(jopt, "delta");
      this.gamma = OptionFromJson.getDouble(jopt, "gamma");
      this.theta = OptionFromJson.getDouble(jopt, "theta");
      this.vega = OptionFromJson.getDouble(jopt, "vega");
      this.rho = OptionFromJson.getDouble(jopt, "rho");
      this.theoretical = OptionFromJson.getDouble(jopt, "theoretical");
      this.intrinsicValue = OptionFromJson.getDouble(jopt, "intrinsicValue");
      this.timeValue = OptionFromJson.getDouble(jopt, "timeValue");

      s = (String) jopt.get("updatedAt");
      this.updatedAt = new DateTime(s, this.sdftime, this.sdfOut);
      this.daysBeforeExpiration = OptionFromJson.getLong(jopt, "daysBeforeExpiration");

    }
  }

//  "contractName": "AAPL220121C00110000",
//  "contractSize": "REGULAR",
//  "currency": "USD",
//  "type": "CALL",
//  "inTheMoney": "TRUE",
//  "lastTradeDateTime": "2020-03-09 11:16:42",
//  "expirationDate": "2022-01-21",
//  "strike": 110,
//  "lastPrice": 165.95,
//  "bid": 139,
//  "ask": 144,
//  "change": -24.55,
//  "changePercent": -0.1289,
//  "volume": 1,
//  "openInterest": 369,
//  "impliedVolatility": 52.2919,
//  "delta": 0.9329,
//  "gamma": 0.0008,
//  "theta": -0.0199,
//  "vega": 0.4284,
//  "rho": 1.5616,
//  "theoretical": 141.5,
//  "intrinsicValue": 0,
//  "timeValue": 0,
//  "updatedAt": "2020-03-13 23:45:39",
//  "daysBeforeExpiration": 678

  @Override
  public String toString() {
    String ret = this.contractName;
    ret += String.format("  %s  %s  %s  %s%n  %s  %.2f  %.2f  %.2f  %d  %d%n", this.contractSize, this.currency, this.itm, this.lastTradeDateTime,
        this.expirationDate, this.strike, this.bid, this.ask, this.volume, this.openInterest);
    return ret;
  }

}
