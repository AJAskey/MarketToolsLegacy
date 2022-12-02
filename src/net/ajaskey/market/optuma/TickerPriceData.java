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

package net.ajaskey.market.optuma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;

/**
 * This class allows reading and usage of Optuma ticker data exported during
 * price updates within Optuma. The Optuma exchange must be setup to write out
 * text files.
 *
 * @author Andy Askey
 *
 */
public class TickerPriceData {

  static private List<DateTime> holidays = new ArrayList<>();

  static final double   latest   = 3903.0;
  static final DateTime lastDate = new DateTime();

  public static void main(final String[] args) {
    final TickerPriceData code = new TickerPriceData("US", "aapl");
    System.out.println(code);
    System.out.println("Latest      : " + code.getLatest());
    System.out.println("Latest Date : " + code.getLatestDate());
    final DateTime dt = new DateTime(2019, DateTime.MARCH, 15);
    final double pd = code.getClose(dt);
    System.out.println(dt + "\t" + pd);
    PriceData tpd = code.getOffset(125);
    System.out.println(tpd);
    tpd = code.getOffset(7500);
    System.out.println(tpd);
  }

  /**
   *
   */
  private static void addHolidays() {
    TickerPriceData.holidays.add(new DateTime(2020, DateTime.APRIL, 10));
  }

  protected List<PriceData> tickerPrices = null;
  private String            id;
  final private String      NL           = Utils.NL;
  private int               numPrices    = 0;
  private String            ticker;

  public TickerPriceData(String code) {
    final String tkr[] = code.split(" ");
    this.ticker = tkr[0];
    this.id = code;
    this.numPrices = 0;
    this.id = "";
    this.tickerPrices = new ArrayList<>();
  }

  public TickerPriceData(String exch, String code) {
    this.build(exch, code, "");
  }

  public TickerPriceData(String exch, String code, String desc) {
    this.build(exch, code, desc);
  }

  public void addId(String desc) {
    this.id += String.format("\t%s", desc);
  }

  public void addPriceData(PriceData pd) {
    this.tickerPrices.add(pd);

  }

  /**
   * This method serves as a constructor for the class.
   *
   */
  private void build(String exch, String code, String desc) {
    //
    if (TickerPriceData.holidays.size() < 1) {
      TickerPriceData.addHolidays();
    }
    //
    String exchange = "";
    try {
      if (exch.toUpperCase().contains("US")) {
        exchange = "US";
      }
      else if (exch.toUpperCase().contains("NEW YORK")) {
        exchange = "NYSE";
      }
      else if (exch.toUpperCase().contains("NASDAQ")) {
        exchange = "NASDAQ";
      }
      else if (exch.toUpperCase().contains("AMEX")) {
        exchange = "AMEX";
      }
      else if (exch.equalsIgnoreCase("WI")) {
        exchange = "WI";
      }
      else if (exch.equalsIgnoreCase("BM")) {
        exchange = "BM";
      }
      else if (exch.equalsIgnoreCase("CBOE")) {
        exchange = "CBOE";
      }
      else {
        this.numPrices = 0;
        return;
      }
      final String subdir = code.trim().substring(0, 1);
      final String path = exchange + "\\" + subdir + "\\"; // "NYSE\\G\\"
      final String sid = String.format("%s - %s\t%s", exchange, code, desc);
      this.id = sid.trim();
      this.tickerPrices = OptumaPriceData.getPriceData(OptumaCommon.optumaPricePath + path + code + ".csv");
      this.numPrices = this.tickerPrices.size();
      this.ticker = code;
      System.out.printf("%s has %d prices.%n", this.id, this.numPrices);
    }
    catch (final IOException e) {
      if (this.tickerPrices != null) {
        this.tickerPrices.clear();
      }
      this.numPrices = 0;
      e.printStackTrace();
    }
  }

  /**
   *
   * @param date
   * @return
   */
  public double getActualClose(final DateTime date) {

    double ret = -1.0;

    for (final PriceData pd : this.tickerPrices) {

      if (pd.date.isEqual(date)) {
        ret = pd.close;
        break;
      }
      else if (pd.date.isGreaterThan(date)) {
        break;
      }
    }
    return ret;
  }

  /**
   * net.ajaskey.market.tools.fred.processing.getSpxClose
   *
   * @param date
   * @return
   */
  public double getClose(final DateTime date) {
    // System.out.printf("%s\t%s%n", Utils.sdf.format(date.getTime()),
    // Utils.sdf.format(spxPrices.get(0).date.getTime()));
    if (date.isLessThan(this.tickerPrices.get(0).date)) {
      return 0.0;
    }
    for (final PriceData pd : this.tickerPrices) {
      // System.out.printf("%s\t%s%n", Utils.sdf.format(date.getTime()),
      // Utils.sdf.format(pd.date.getTime()));
      if (pd.date.isGreaterThanOrEqual(date)) {
        return pd.close;
      }
    }
    return 0;
  }

  /**
   *
   * @param dt
   * @return
   */
  public PriceData getData(DateTime dt) {
    if (dt == null) {
      return null;
    }
    // System.out.printf("dt getData() : %s%n", dt);
    if (dt.isLessThan(this.tickerPrices.get(0).date)) {
      return null;
    }
    for (final PriceData pd : this.tickerPrices) {
      if (pd.date == null) {
        return null;
      }
      // System.out.printf("%s\t%s%n", Utils.sdf.format(date.getTime()),
      // Utils.sdf.format(pd.date.getTime()));
      if (pd.date.isGreaterThanOrEqual(dt)) {
        return pd;
      }
    }
    return null;
  }

  public double getFirst() {
    double ret = 0.0;
    if (this.numPrices > 0) {
      ret = this.tickerPrices.get(0).close;
    }
    return ret;
  }

  public DateTime getFirstDate() {
    return this.tickerPrices.get(0).date;
  }

  public String getId() {
    return this.id;
  }

  /**
   *
   * @return
   */
  public double getLatest() {
    double ret = latest;
//    if (this.numPrices > 0) {
//      ret = this.tickerPrices.get(this.numPrices - 1).close;
//    }
    return ret;
  }

  public DateTime getLatestDate() {
    DateTime ret = lastDate;
//    if (this.numPrices > 0) {
//      ret = this.tickerPrices.get(this.numPrices - 1).date;
//    }
    return ret;
  }

  public List<PriceData> getLatestList(int number) {
    final List<PriceData> ret = new ArrayList<>();
//      int last = numPrices - number - 1;
//      for (int i = numPrices - 1; i > last; i--) {
//         ret.add(this.tickerPrices.get(i));
//      }
    int knt = 0;
    boolean isHoliday = false;
    final int start = this.tickerPrices.size() - 1;
    for (int i = start; i > 0; i--) {
      final PriceData pd = this.tickerPrices.get(i);
      for (final DateTime dt : TickerPriceData.holidays) {
        if (pd.date.isEqual(dt)) {
          isHoliday = true;
          break;
        }
      }
      if (!isHoliday) {
        ret.add(pd);
        knt++;
        // System.out.printf("%d%n%s%n", knt, pd);
        if (knt == number) {
          break;
        }
      }
    }
    return ret;
  }

  /**
   *
   * @param days
   * @return
   */
  public double getMA(int days) {
    double ret = 0.0;
    if (days > 0 && days < this.numPrices) {
      final int first = this.numPrices - 1;
      final int last = first - days;
      double sum = 0.0;
      for (int i = first; i > last; i--) {
        sum += this.tickerPrices.get(i).close;
      }
      ret = sum / days;
    }
    return ret;
  }

  public int getNumPrices() {
    return this.numPrices;
  }

  public PriceData getOffset(int days) {
    PriceData ret = null;
    try {
      final int retday = this.numPrices - days - 1;
      if (retday > 0) {
        ret = this.tickerPrices.get(retday);
      }
      return ret;
    }
    catch (final Exception e) {
    }
    return null;
  }

  public DateTime getOffsetDate(int days) {
    final PriceData ret = this.getOffset(days);
    if (ret != null) {
      return new DateTime(ret.date);
    }
    return null;
  }

  public double getOffsetPrice(int days) {
    final PriceData ret = this.getOffset(days);
    if (ret != null) {
      return ret.close;
    }
    return 0;
  }

  public List<PriceData> getPriceData() {
    return this.tickerPrices;
  }

  public String getTicker() {
    return this.ticker;
  }

  public List<PriceData> getTickerPrices() {
    System.out.printf("%s has %d%n", this.id, this.numPrices);
    return this.tickerPrices;
  }

  public void reversePriceData() {
    Collections.reverse(this.tickerPrices);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String ret = this.ticker + this.NL;
    for (final PriceData pd : this.tickerPrices) {
      ret += String.format("%s\t%.2f, %.2f, %.2f, %.2f%n", pd.date, pd.open, pd.high, pd.low, pd.close);
    }
    return ret;
  }
}
