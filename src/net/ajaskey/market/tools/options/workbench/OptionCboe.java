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
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;

public class OptionCboe {

  static List<String> icodes = new ArrayList<>();

  // private static final double CboeSkewIv = 1.029;
  private static final double CboeSkewIv = 1.0;

  private static final SimpleDateFormat sdfin = new SimpleDateFormat("MM/dd/yyyy");

  private static final DateTime today = new DateTime();

  private static DateTime tomorrow = null;

  /**
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    OptionUtils.getDownloads();

    final List<String> codes = new ArrayList<>();
    OptionCboe.icodes.add("SPY");
    OptionCboe.icodes.add("SPX");
    OptionCboe.icodes.add("QQQ");
    OptionCboe.icodes.add("NDX");
    OptionCboe.icodes.add("IWO");
    OptionCboe.icodes.add("RUT");
    OptionCboe.icodes.add("DIA");
    OptionCboe.icodes.add("OEX");

    final String ext[] = { "dat" };
    final List<File> files = Utils.getDirTree("data/options", ext);

    for (final File f : files) {

      if (f.getName().contains("-options.dat")) {
        final String c = f.getName().replace("-options.dat", "");
        codes.add(c);
      }
    }

    final OptionStatistics osIndex = new OptionStatistics();

    try (PrintWriter pw = new PrintWriter("out/cboe.txt")) {

      for (final String s : OptionCboe.icodes) {
        pw.printf("%s ", s.toUpperCase());
        System.out.printf("%s ", s.toUpperCase());
      }
      pw.printf("%n%n");
      System.out.printf("%n%n");

      for (final String code : codes) {

        final Option opt = new Option();

        new OptionCboe(code, opt);

        if (OptionCboe.isIcode(code)) {
          // System.out.println("Found icode : " + code);
          osIndex.addToStats(opt);
        }

        pw.printf("%n%s\t%s%n", code.toUpperCase(), opt.lastTrade);
        System.out.printf("%n%s\t%s%n", code.toUpperCase(), opt.lastTrade);

        // System.out.println(opt);

        final OptionStatistics os = new OptionStatistics(opt);
        pw.println(os);
        System.out.println(os);

        opt.writeBasefile();

        List<OptionCollection> ocList = OptionCollection.collate(opt);

      }
    }

    // OptionCollection oc = OptionCollection.get("OEX", new DateTime("2020-Nov-20",
    // "yyyy-MMM-dd"), "PUT");
    // System.out.println(oc);

    System.out.println(osIndex.toSumString());

  }

  /**
   *
   * @param code
   * @return
   */
  private static boolean isIcode(String code) {
    for (final String ic : OptionCboe.icodes) {
      if (ic.equalsIgnoreCase(code)) {
        return true;
      }
    }
    return false;
  }

  private Option opt = null;

  /**
   *
   * @param code
   */
  public OptionCboe(String code) {
    try {
      this.opt = new Option();
      String fname = "";
      final File f = new File(code);
      if (f.exists()) {
        fname = code;
      }
      else {
        fname = String.format("data/options/%s-options.dat", code);
      }
      this.process(code, fname);
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
   * @param option
   */
  public OptionCboe(String code, Option option) {
    try {
      this.opt = option;
      String fname = "";
      final File f = new File(code);
      if (f.exists()) {
        fname = code;
      }
      else {
        fname = String.format("data/options/%s-options.dat", code);
      }
      this.process(code, fname);
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

  public Option getOpt() {
    return this.opt;
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

    OptionCboe.tomorrow = new DateTime(OptionCboe.today);

    final String ffname = String.format("%s", fname);
    System.out.println(ffname);
    final List<String> fileData = TextUtils.readTextFile(new File(ffname), true);

    this.opt.sCode = code;
    this.opt.sExch = "US";

    String fld[] = fileData.get(0).split(",");
    this.opt.sUlLastTradePrice = Double.parseDouble(fld[1].trim());

    fld = fileData.get(1).split(",");
    final String dStr = fld[0].trim().replace("@", "").replace("ET", "").replaceAll("\\s+", "");
    this.opt.lastTrade = new DateTime(dStr, "MMMddyyyyHH:mm");
    this.opt.lastTrade.setSdf(OptionData.sdfOut);
    System.out.println(this.opt.lastTrade);

    for (final String s : fileData) {
      this.setOptionCboe(s);
    }

    // DIA (SPDR Dow Jones Industrial Average ETF),243.45,-2.48
    // May 19 2020 @ 16:01 ET,Bid,243.44,Ask,243.48,Size,11x4,Vol,3701408

  }

  /**
   *
   * @param jopt
   * @param od
   */
  public void setOptionCboe(String data) {

    try {

      // System.out.println(data);

      final OptionData odPut = new OptionData();
      this.processPut(data, odPut);
      if (odPut.valid) {
        this.opt.addOption(odPut);
      }

      final OptionData odCall = new OptionData();
      this.processCall(data, odCall);
      if (odCall.valid) {
        this.opt.addOption(odCall);
      }

    }
    catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return this.opt.toString();
  }

  /**
   *
   * @param data
   * @param od
   */
  private void processCall(String data, OptionData od) {

    // System.out.println(data);

    od.valid = true;
    try {
      final String fld[] = data.split(",");
      od.type = "CALL";

      final String s = fld[0].trim();
      od.expirationDate = new DateTime(s, OptionCboe.sdfin, OptionData.sdfOut);
      if (od.expirationDate.isLessThan(OptionCboe.tomorrow)) {
        od.valid = false;
        return;
      }

      od.lastTradeDateTime = new DateTime();
      od.strike = Double.parseDouble(fld[11].trim());
      od.contractName = fld[1].trim();
      od.lastPrice = Double.parseDouble(fld[2].trim());
      od.bid = Double.parseDouble(fld[4].trim());
      od.ask = Double.parseDouble(fld[5].trim());
      od.volume = Long.parseLong(fld[6].trim());
      od.impliedVolatility = Double.parseDouble(fld[7].trim()) * OptionCboe.CboeSkewIv;
      od.delta = Double.parseDouble(fld[8].trim());
      od.gamma = Double.parseDouble(fld[9].trim());
      od.openInterest = Long.parseLong(fld[10].trim());
      od.daysBeforeExpiration = od.expirationDate.getDeltaDays(OptionCboe.today) + 1;

    }
    catch (final Exception e) {
      od.valid = false;
    }
  }

  /**
   *
   * @param data
   * @param od
   */
  private void processPut(String data, OptionData od) {

    // System.out.println(data);

    od.valid = true;
    try {
      final String fld[] = data.split(",");
      od.type = "PUT";

      final String s = fld[0].trim();
      od.expirationDate = new DateTime(s, OptionCboe.sdfin, OptionData.sdfOut);
      if (od.expirationDate.isLessThan(OptionCboe.tomorrow)) {
        od.valid = false;
        return;
      }

      od.lastTradeDateTime = new DateTime();
      od.strike = Double.parseDouble(fld[11].trim());
      od.contractName = fld[12].trim();
      od.lastPrice = Double.parseDouble(fld[13].trim());
      od.bid = Double.parseDouble(fld[15].trim());
      od.ask = Double.parseDouble(fld[16].trim());
      od.volume = Long.parseLong(fld[17].trim());
      od.impliedVolatility = Double.parseDouble(fld[18].trim()) * OptionCboe.CboeSkewIv;
      od.delta = Double.parseDouble(fld[19].trim());
      od.gamma = Double.parseDouble(fld[20].trim());
      od.openInterest = Long.parseLong(fld[21].trim());
      od.daysBeforeExpiration = od.expirationDate.getDeltaDays(OptionCboe.today) + 1;

//      OptionsProcessor op = new OptionsProcessor(OptionsProcessor.APUT, "SPY", data, od.strike, opt.sUlLastTradePrice,
//          od.expirationDate, new DateTime(), od.impliedVolatility / 100.0, false);
//      op.CloseDebug();
//
//      od.theoretical = op.getPrice();

      // System.out.printf("%.2f\t%.2f", od.lastPrice, op.getPrice());
      // System.out.println("");

    }
    catch (final Exception e) {
      od.valid = false;
    }
  }
}
