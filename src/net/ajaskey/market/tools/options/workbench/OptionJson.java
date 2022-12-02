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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;

public class OptionJson {

  private final static String apitoken = "5e6d19e325dbf1.63911667";

  private static String response = "";

  private final static String url1 = "https://eodhistoricaldata.com/api/options/";

  private final static String url2 = "?api_token=" + OptionJson.apitoken;

  /**
   *
   * @param filename
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ParseException
   */
  public static Object getFromFile(String filename) throws FileNotFoundException, IOException, ParseException {
    final Object obj = new JSONParser().parse(new FileReader(filename));
    return obj;
  }

  /**
   *
   * @param code
   * @return
   * @throws ParseException
   */
  public static Object getFromWeb(String code) throws ParseException {
    final String url = String.format("%s%s%s", OptionJson.url1, code, OptionJson.url2);
    System.out.println(url);
    OptionJson.response = Utils.getFromUrl(url);
//    try {
//      System.out.println(OptionJson.response.substring(0, 100));
//    } catch (final Exception e) {
//      e.printStackTrace();
//    }
    // System.out.println(resp);
    final Object obj = new JSONParser().parse(OptionJson.response);
    return obj;
  }

  /**
   *
   * @param jo
   * @param key
   * @return
   */
  private static Double getDouble(JSONObject jo, String key) {
    Double ret = 0.0;
    try {
      return ((Number) jo.get(key)).doubleValue();
    }
    catch (final Exception e) {
      ret = 0.0;
    }
    return ret;
  }

  /**
   *
   * @param jo
   * @param key
   * @return
   */
  private static Long getLong(JSONObject jo, String key) {
    Long ret = 0L;
    try {
      return ((Number) jo.get(key)).longValue();
    }
    catch (final Exception e) {
      ret = 0L;
    }
    return ret;
  }

  private Option opt = null;

  public OptionJson(String code, Option option) {
    try {
      this.opt = option;
      this.process(code);
      this.opt.response = OptionJson.response;
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (final IOException e) {
      e.printStackTrace();
    }
    catch (final ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param code
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ParseException
   */
  public void process(String code) throws FileNotFoundException, IOException, ParseException {
    this.process(code, null);
  }

  /**
   *
   * @param code
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ParseException
   */
  public void process(String code, String fname) throws FileNotFoundException, IOException, ParseException {

    Object obj = null;
    if (fname == null) {
      obj = OptionJson.getFromWeb(code);
    }
    else {
      obj = OptionJson.getFromFile("data/json-data.json");
    }

    final JSONObject jo = (JSONObject) obj;
    final JSONArray jaData = (JSONArray) jo.get("data");

    this.opt.sCode = (String) jo.get("code");
    this.opt.sExch = (String) jo.get("exchange");
    this.opt.sLastTradeDate = (String) jo.get("lastTradeDate");
    this.opt.sUlLastTradePrice = OptionJson.getDouble(jo, "lastTradePrice");
    this.opt.lastTrade = new DateTime(this.opt.sLastTradeDate, "yyyy-MM-dd");

    final Iterator<?> itrData = jaData.iterator();

    while (itrData.hasNext()) {

      final JSONObject joData = (JSONObject) itrData.next();
      joData.get("expirationDate");

      final JSONObject joOptExpiry = (JSONObject) joData.get("options");
      final JSONArray jaOptPut = (JSONArray) joOptExpiry.get("PUT");
      final JSONArray jaOptCall = (JSONArray) joOptExpiry.get("CALL");

      if (jaOptCall != null) {

        final Iterator<?> itrCall = jaOptCall.iterator();

        while (itrCall.hasNext()) {

          final JSONObject callObj = (JSONObject) itrCall.next();
          final OptionData od = new OptionData();
          this.setOptionJson(callObj, od);
          this.opt.addOption(od);
        }
      }

      if (jaOptPut != null) {

        final Iterator<?> itrPut = jaOptPut.iterator();

        while (itrPut.hasNext()) {

          final JSONObject putObj = (JSONObject) itrPut.next();
          final OptionData od = new OptionData();
          this.setOptionJson(putObj, od);
          this.opt.addOption(od);
        }
      }
    }
  }

  /**
   *
   * @param jopt
   * @param od
   */
  public void setOptionJson(JSONObject jopt, OptionData od) {
    od.valid = false;
    try {

      String s = (String) jopt.get("lastTradeDateTime");
      if (s.equalsIgnoreCase("0000-00-00 00:00:00")) {
        od.valid = false;
        return;
      }
      od.lastTradeDateTime = new DateTime(s.trim(), OptionData.sdftime);

      od.contractName = (String) jopt.get("contractName");
      od.contractSize = (String) jopt.get("contractSize");
      od.currency = (String) jopt.get("currency");
      od.type = (String) jopt.get("type");
      od.itm = Boolean.parseBoolean((String) jopt.get("inTheMoney"));
      s = (String) jopt.get("expirationDate");
      od.expirationDate = new DateTime(s.trim(), OptionData.sdf, OptionData.sdfOut);
      od.strike = OptionJson.getDouble(jopt, "strike");
      od.lastPrice = OptionJson.getDouble(jopt, "lastPrice");
      od.bid = OptionJson.getDouble(jopt, "bid");
      od.ask = OptionJson.getDouble(jopt, "ask");
      od.change = OptionJson.getDouble(jopt, "change");
      od.volume = OptionJson.getLong(jopt, "volume");
      od.openInterest = OptionJson.getLong(jopt, "openInterest");
      od.impliedVolatility = OptionJson.getDouble(jopt, "impliedVolatility");
      od.delta = OptionJson.getDouble(jopt, "delta");
      od.gamma = OptionJson.getDouble(jopt, "gamma");
      od.theta = OptionJson.getDouble(jopt, "theta");
      od.vega = OptionJson.getDouble(jopt, "vega");
      od.rho = OptionJson.getDouble(jopt, "rho");
      od.theoretical = OptionJson.getDouble(jopt, "theoretical");
      od.intrinsicValue = OptionJson.getDouble(jopt, "intrinsicValue");
      od.timeValue = OptionJson.getDouble(jopt, "timeValue");
      s = (String) jopt.get("updatedAt");
      od.updatedAt = new DateTime(s.trim(), OptionData.sdftime, OptionData.sdfOut);
      od.daysBeforeExpiration = OptionJson.getLong(jopt, "daysBeforeExpiration");
      od.valid = true;
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
  }

}
