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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;

public class Option {

  public DateTime         lastTrade = null;
  public List<OptionData> optList   = new ArrayList<>();
  public String           response;
  public String           sCode;
  public String           sExch;

  public String sLastTradeDate;

  public Double sUlLastTradePrice;

  /**
   *
   */
  public Option() {
    // TODO Auto-generated constructor stub
  }

  public void addOption(OptionData od) {
    if (od.valid) {
      if (od.lastPrice > 0.049) {
        this.optList.add(od);
      }
    }

  }

  public void filter(double low, double high, long minVol, long minOi, double minIv, double maxIv) {
    final List<OptionData> newOd = new ArrayList<>();

    for (final OptionData od : this.optList) {
      if (od.lastPrice >= low && od.lastPrice <= high) {
        if (od.volume >= minVol && od.openInterest >= minOi) {
          if (od.impliedVolatility >= minIv && od.impliedVolatility <= maxIv) {
            newOd.add(od);
          }
        }
      }
    }

    this.optList.clear();
    this.optList = newOd;
  }

  /**
   * Queries from option data from internet and places in standard data structure
   * from OptionData
   *
   * @param code
   * @return
   * @throws FileNotFoundException
   */
  public String processJson(String code) throws FileNotFoundException {

    new OptionJson(code, this);
//    String fname = oj.writeFile();
    return "";

  }

  /**
   * Reads file data into standard data structure from OptionData. Stores valid
   * records into optList.
   *
   * @param filename
   */
  public void readBasefile(String filename) {

    final String ffname = String.format("%s", filename);
    // System.out.println(ffname);
    final List<String> fileData = TextUtils.readTextFile(new File(ffname), true);

    final String s = fileData.get(0);
    final int idx = s.indexOf(':');
    final String fld[] = s.substring(idx + 2).split(",");
    this.sCode = fld[0].trim();
    this.sExch = "";
    this.sUlLastTradePrice = Double.parseDouble(fld[1].trim());
    this.sLastTradeDate = fld[2].trim();
    this.lastTrade = new DateTime(this.sLastTradeDate, "yyyy-MM-dd");

    for (int i = 1; i < fileData.size(); i++) {
      final OptionData od = new OptionData();
      od.parseInputData(fileData.get(i));
      if (od.valid) {
        this.addOption(od);
      }
    }
  }

  @Override
  public String toString() {
    String ret = String.format("%s\t%s\t%s\t%s%n", this.sCode, this.sExch, this.sUlLastTradePrice, this.lastTrade);
    for (final OptionData od : this.optList) {
      ret += od.toString() + Utils.NL;
    }
    return ret;
  }

  /**
   * Writes data into file with standard data structure from OptionData
   *
   * @return
   * @throws FileNotFoundException
   */
  public String writeBasefile() throws FileNotFoundException {
    return this.writeBasefile(null);
  }

  /**
   * Writes data into file with standard data structure from OptionData
   *
   * @param fname Name for output file or NULL for auto-created
   * @throws FileNotFoundException
   */
  public String writeBasefile(String fname) throws FileNotFoundException {

    String filename = "";

    DateTime recent = new DateTime(1990, DateTime.JANUARY, 1);
    for (final OptionData opt : this.optList) {
      if (opt.lastTradeDateTime.isGreaterThan(recent)) {
        recent = opt.lastTradeDateTime;
      }
    }
    recent.setSdf(OptionData.sdfOutTime);
    if (fname == null) {
      filename = String.format("data/options/%s_%s_optiondata.csv", this.sCode, recent);
    }
    else {
      filename = fname;
    }
    try (PrintWriter pw = new PrintWriter(filename)) {
      final String h = String.format("%s,:,%s,%.2f,%s", OptionData.header, this.sCode, this.sUlLastTradePrice, this.sLastTradeDate);
      pw.println(h);
      for (final OptionData opt : this.optList) {
        pw.println(opt);
      }
    }

    return filename;
  }

  /**
   *
   * @param scaler
   */
  protected void scaleData(double scaler) {

    for (final OptionData od : this.optList) {
      od.scaleData(scaler);
    }

  }

  public void writeChangeFile(String type, double chg) {
    for (final OptionData opt : this.optList) {
      if (type.equalsIgnoreCase("PUT")) {
        System.out.println(opt);
      }
    }

  }

}
