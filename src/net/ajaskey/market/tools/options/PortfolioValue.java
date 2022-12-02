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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;

public class PortfolioValue {

  final static List<PortfolioValue> pvData = new ArrayList<>();

  public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

    final Object obj = new JSONParser().parse(new FileReader("data/AAPL.json"));

    final JSONObject jo = (JSONObject) obj;
    final JSONArray jaData = (JSONArray) jo.get("data");

    final Iterator<?> itrData = jaData.iterator();

    while (itrData.hasNext()) {
      final JSONObject joData = (JSONObject) itrData.next();
      joData.get("expirationDate");

      final JSONObject joOptExpiry = (JSONObject) joData.get("options");
      final JSONArray jaOptPut = (JSONArray) joOptExpiry.get("PUT");
      joOptExpiry.get("CALL");

      final Iterator<?> itrPut = jaOptPut.iterator();

      final OptionFromJson aput = new OptionFromJson(itrPut);
      aput.get();

      System.out.println(aput);

    }

//    String sData = (String) jaData.get("expirationDate");

    final List<String> data = TextUtils.readTextFile("data/positiondata.txt", true);

    String optName = "";
    int pos = 0;
    for (int i = 0; i < data.size(); i++) {
      final String s = data.get(i);
      final String fld[] = s.split("\t");
      if (fld[0].contains("----")) {
        pos = i;
        break;
      }
      optName = fld[0].replaceFirst("\\.", "").trim();
      // System.out.println(s + "\t" + optName);
      final PortfolioValue pv = new PortfolioValue();
      pv.optName = optName;
      final String sLast = fld[1].trim();
      pv.lPrice = Double.parseDouble(sLast);
      final String sIv = fld[2].replace("%", "").trim();
      pv.iv = Double.parseDouble(sIv);
      pv.calcBs();
      PortfolioValue.pvData.add(pv);
    }

    for (final PortfolioValue pv : PortfolioValue.pvData) {
      if (pv.quantity > 0) {
        for (int i = pos + 1; i < data.size(); i++) {
          final String s = data.get(i);
          if (s.contains(pv.optName)) {
            final String fld[] = s.split("\t");
            pv.quantity = Integer.parseInt(fld[5].trim());
            pv.tPrice = Double.parseDouble(fld[6].trim());
            // System.out.println(s);
            break;
          }
        }
      }
    }

//    for (PortfolioValue pv : pvData) {
//      System.out.println(pv);
//    }

  }

  double bsPrice;
  double iv;
  double lPrice;
  String optName;

  int quantity;

  double tPrice;

  public PortfolioValue() {
    this.optName = "";
    this.lPrice = 0.0;
    this.tPrice = 0.0;
    this.bsPrice = 0.0;
    this.quantity = 0;
    this.iv = 0.0;
  }

  @Override
  public String toString() {
    String ret = this.optName + Utils.NL;
    final double cost = this.tPrice * this.quantity;
    final double iv = this.iv / 100.0;
    ret += String.format("\t%.2f\t%d\t$%.2f%n\t%.4f\t$%.2f%n\t%.2f%n", this.tPrice, this.quantity, cost, iv, this.lPrice, this.bsPrice);
    return ret;
  }

  private void calcBs() {

    final String back = this.optName.substring(5).toUpperCase();
    if (back.contains("P")) {
    }
    this.getDt(this.optName);
    this.getStrike(this.optName);

    // OptionsProcessor op = new OptionsProcessor(type, this.optName, strike, ul,
    // dt, this.iv);
    // this.bsPrice = op.getPrice();
  }

  /**
   *
   * @param optN
   * @return
   */
  private DateTime getDt(String optN) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   *
   * @param optN
   * @return
   */
  private double getStrike(String optN) {
    // TODO Auto-generated method stub
    return 0;
  }

}
