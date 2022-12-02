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
package net.ajaskey.market.tools.options.workbench;

import java.text.SimpleDateFormat;

import net.ajaskey.common.DateTime;

public class OptionData {

  final protected static String           header     = "Name,Type,LastTrade,Expiry,Strike,Last,Bid,Ask,Vol,OI,IV,Theta,Delta,Gamma,Vega,Rho,Theory,Intrins,TimeValue";
  protected final static SimpleDateFormat sdf        = new SimpleDateFormat("yyyy-MM-dd");
  protected final static SimpleDateFormat sdfOut     = new SimpleDateFormat("dd-MMM-yyyy");
  protected final static SimpleDateFormat sdfOutTime = new SimpleDateFormat("yyyyMMdd_HHmmss");

  protected final static SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

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
  public boolean  valid;
  public Double   vega;
  public Long     volume;

  public OptionData() {
    this.strike = 0.0;
    this.lastPrice = 0.0;
    this.bid = 0.0;
    this.ask = 0.0;
    this.change = 0.0;
    this.changePercent = 0.0;
    this.impliedVolatility = 0.0;
    this.delta = 0.0;
    this.gamma = 0.0;
    this.theta = 0.0;
    this.vega = 0.0;
    this.rho = 0.0;
    this.theoretical = 0.0;
    this.intrinsicValue = 0.0;
    this.timeValue = 0.0;
    this.volume = 0L;
    this.openInterest = 0L;
  }

  /**
   * Write to standard options file
   */
  @Override
  public String toString() {

    String ret = String.format("%s,", this.contractName);
    ret += String.format("%s,", this.type);
    ret += String.format("%s,", this.lastTradeDateTime);
    ret += String.format("%s,", this.expirationDate);
    ret += String.format("%.2f,", this.strike);
    ret += String.format("%.2f,", this.lastPrice);
    ret += String.format("%.2f,", this.bid);
    ret += String.format("%.2f,", this.ask);
    ret += String.format("%d,", this.volume);
    ret += String.format("%d,", this.openInterest);
    ret += String.format("%.2f,", this.impliedVolatility);
    ret += String.format("%.4f,", this.theta);
    ret += String.format("%.4f,", this.delta);
    ret += String.format("%.4f,", this.gamma);
    ret += String.format("%.4f,", this.vega);
    ret += String.format("%.4f,", this.rho);
    ret += String.format("%.2f,", this.theoretical);
    ret += String.format("%.2f,", this.intrinsicValue);
    ret += String.format("%.2f", this.timeValue);

    return ret;
  }

  protected void parseInputData(String input) {
    final String fld[] = input.split(",");
    this.contractName = fld[0].trim();
    this.type = fld[1].trim();
    String s = fld[2].trim();
    this.lastTradeDateTime = new DateTime(s, OptionData.sdftime);
    s = fld[3].trim();
    this.expirationDate = new DateTime(s, OptionData.sdf, OptionData.sdfOut);
    this.strike = Double.parseDouble(fld[4].trim());
    this.lastPrice = Double.parseDouble(fld[5].trim());
    this.bid = Double.parseDouble(fld[6].trim());
    this.ask = Double.parseDouble(fld[7].trim());
    this.volume = Long.parseLong(fld[8].trim());
    this.openInterest = Long.parseLong(fld[9].trim());
    this.impliedVolatility = Double.parseDouble(fld[10].trim());
    this.theta = Double.parseDouble(fld[11].trim());
    this.delta = Double.parseDouble(fld[12].trim());
    this.gamma = Double.parseDouble(fld[13].trim());
    this.vega = Double.parseDouble(fld[14].trim());
    this.rho = Double.parseDouble(fld[15].trim());
    this.theoretical = Double.parseDouble(fld[16].trim());
    this.intrinsicValue = Double.parseDouble(fld[17].trim());
    this.timeValue = Double.parseDouble(fld[18].trim());
    this.valid = true;
  }

  protected void scaleData(double scaler) {
    this.strike *= scaler;
  }

}
