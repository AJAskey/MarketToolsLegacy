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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;

public class PriceData {

  public enum FormType {
    FULL, SHORT
  }

  /**
   *
   * @param index
   * @return
   * @throws IOException
   */
  public static List<PriceData> getData(String index) throws IOException {
    final List<PriceData> ret = new ArrayList<>();

    final String fname = PriceData.getFilename(index);

    try (BufferedReader br = new BufferedReader(new FileReader(new File(fname)))) {
      String line = "";

      line = br.readLine(); // read header

      while (line != null) {
        line = br.readLine();
        if (line != null && line.length() > 0) {
          final String fld[] = line.split(",");
          // final PriceData dd = new PriceData();
          DateTime dt = new DateTime();
          dt = dt.parse(fld[0].trim(), "yyyy-MM-dd");
          final double open = Double.parseDouble(fld[1].trim());
          final double high = Double.parseDouble(fld[2].trim());
          final double low = Double.parseDouble(fld[3].trim());
          final double close = Double.parseDouble(fld[5].trim());
          final long volume = Long.parseLong(fld[6].trim());

          final PriceData dd = new PriceData(dt, open, high, low, close, volume);

          ret.add(dd);
        }
      }
    }

    return ret;
  }

  /**
   *
   * @param prices
   * @return
   */
  public static double getLatestPrice(List<PriceData> prices) {
    try {
      return prices.get(prices.size() - 1).close;
    }
    catch (final Exception e) {
      return 0.0;
    }
  }

  public static PriceData queryDate(DateTime dt, List<PriceData> prices) {
    for (final PriceData d : prices) {
      if (d.date.isEqual(dt)) {
        return d;
      }
      else if (d.date.isGreaterThan(dt)) {
        return d;
      }
    }
    // Return last data point
    return prices.get(prices.size() - 1);
  }

  private static String getFilename(String index) {
    // TODO Auto-generated method stub
    return null;
  }

  public double    close;
  public DateTime  date;
  public double    high;
  public double    low;
  public double    open;
  public long      volume;
  private FormType form;
  private boolean  valid;

  /**
   * This method serves as a constructor for the class.
   * 
   * @param dt DateTime of data
   * @param o  Open price
   * @param h  High price
   * @param l  Low price
   * @param c  Close price
   * @param v  Volume
   */
  public PriceData(final DateTime dt, final double o, final double h, final double l, final double c, final long v) {

    this.date = new DateTime(dt);
    this.open = o;
    this.high = h;
    this.low = l;
    this.close = c;
    this.volume = v;
    this.valid = true;
    this.form = FormType.FULL;
  }

  public PriceData(String[] fld, String fmt, int startIdx) {
    try {
      this.date = new DateTime(fld[startIdx].trim(), fmt);
      if (this.date.isNull()) {
        this.valid = false;
        return;
      }
      this.open = Double.parseDouble(fld[startIdx + 1].trim());
      this.high = Double.parseDouble(fld[startIdx + 2].trim());
      this.low = Double.parseDouble(fld[startIdx + 3].trim());
      this.close = Double.parseDouble(fld[startIdx + 4].trim());
      this.volume = Long.parseLong(fld[startIdx + 5].trim());
      this.setForm();

      this.valid = true;

    }
    catch (final Exception e) {
      e.printStackTrace();
      this.valid = false;
    }
  }

  public PriceData(DateTime dt, double clse) {
    this.date = new DateTime(dt);
    this.close = clse;
    this.open = clse;
    this.high = clse;
    this.low = clse;
    this.volume = 0L;
    this.form = FormType.SHORT;
    this.valid = true;
  }

  /**
   * @return the form
   */
  public FormType getForm() {

    return this.form;
  }

  public boolean isValid() {

    // valid = this.date.isValid();

    return this.valid;
  }

  public String toShortString() {

    final String ret = String.format("%s, %.2f", this.date.format("yyyy-MM-dd"), this.close);
    return ret;
  }

  public String toShortString(double scaler) {

    final String ret = String.format("%s, %.2f", this.date.format("yyyy-MM-dd"), this.close / scaler);
    return ret;
  }

  public String toOptumaString(String desc, double scaler) {

    final String ret = String.format("%s, %s, %.2f, %.2f, %.2f, %.2f, %d, 0", desc, this.date.format("yyyyMMdd"), this.open / scaler,
        this.high / scaler, this.low / scaler, this.close / scaler, this.volume);
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    final String ret = String.format("%s, %.2f, %.2f, %.2f, %.2f, %10d", this.date.format("yyyy-MM-dd"), this.open, this.high, this.low, this.close,
        this.volume);
    return ret;
  }

  /**
   *
   * net.ajaskey.market.tools.helpers.setForm
   *
   */
  private void setForm() {

    if (this.open == this.high && this.high == this.low && this.low == this.close) {
      this.setForm(FormType.SHORT);
    }
    else {
      this.setForm(FormType.FULL);
    }
  }

  /**
   * @param form the form to set
   */
  private void setForm(final FormType form) {

    this.form = form;
  }

}
