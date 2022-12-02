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

package net.ajaskey.market.tools.SIP;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.market.optuma.PriceData;

public class DateSet {

  public class QData {

    public DateTime date;
    public double   value;

    /**
     * This method serves as a constructor for the class.
     *
     */
    public QData() {

      this.date = new DateTime();
      this.value = 0.0;
    }
  }

  public class Quarters {

    public QData q1  = new QData();
    public QData q2  = new QData();
    public QData q3  = new QData();
    public QData q4  = new QData();
    public QData ttm = new QData();

    /**
     * This method serves as a constructor for the class.
     *
     */
    public Quarters(final DateTime base, final List<PriceData> prices) {

      this.q4.date = new DateTime(base);
      if (prices != null) {
        this.q4.value = PriceData.queryDate(base, prices).close;
      }

      base.add(Calendar.MONTH, -3);
      // int lday = base.getActualMaximum(Calendar.DAY_OF_MONTH);
      // base.set(Calendar.DAY_OF_MONTH, lday);
      this.q3.date = new DateTime(base);
      if (prices != null) {
        this.q3.value = PriceData.queryDate(base, prices).close;
      }

      base.add(Calendar.MONTH, -3);
      // lday = base.getActualMaximum(Calendar.DAY_OF_MONTH);
      // base.set(Calendar.DAY_OF_MONTH, lday);
      this.q2.date = new DateTime(base);
      if (prices != null) {
        this.q2.value = PriceData.queryDate(base, prices).close;
      }

      base.add(Calendar.MONTH, -3);
      // lday = base.getActualMaximum(Calendar.DAY_OF_MONTH);
      // base.set(Calendar.DAY_OF_MONTH, lday);
      this.q1.date = new DateTime(base);

      if (prices != null) {
        this.q1.value = PriceData.queryDate(base, prices).close;
      }
    }

    @Override
    public String toString() {

      String ret = "  Q1 : " + this.q1.date + "\t" + this.q1.value;
      ret += "\n  Q2 : " + this.q2.date + "\t" + this.q2.value;
      ret += "\n  Q3 : " + this.q3.date + "\t" + this.q3.value;
      ret += "\n  Q4 : " + this.q4.date + "\t" + this.q4.value;

      return ret;
    }
  }

  /**
   * net.ajaskey.market.tools.sipro.main
   *
   * @param args
   * @throws ParseException
   * @throws IOException
   */
  public static void main(final String[] args) throws IOException, ParseException {

    final List<PriceData> prices = PriceData.getData("SP500");
    final DateSet ds = new DateSet(prices);
    System.out.println(ds);

  }

  public double   latestPrice;
  public QData    ttm = new QData();
  public Quarters y0;
  public Quarters y1;
  public Quarters y2;
  public Quarters y3;
  public Quarters y4;

  public Quarters y5;

  public Quarters y6;

  public Quarters y7;

  /**
   *
   * This method serves as a constructor for the class.
   *
   * @param prices
   */
  public DateSet(final List<PriceData> prices) {

    this.latestPrice = PriceData.getLatestPrice(prices);

    this.ttm.date = new DateTime();
    final DateTime tmp = new DateTime();

    this.y0 = new Quarters(tmp, prices);

    tmp.add(Calendar.MONTH, -3);
    this.y1 = new Quarters(tmp, prices);

    tmp.add(Calendar.MONTH, -3);
    this.y2 = new Quarters(tmp, prices);

    tmp.add(Calendar.MONTH, -3);
    this.y3 = new Quarters(tmp, prices);

    tmp.add(Calendar.MONTH, -3);
    this.y4 = new Quarters(tmp, prices);

    tmp.add(Calendar.MONTH, -3);
    this.y5 = new Quarters(tmp, prices);

    tmp.add(Calendar.MONTH, -3);
    this.y6 = new Quarters(tmp, prices);

    tmp.add(Calendar.MONTH, -3);
    this.y7 = new Quarters(tmp, prices);
  }

  @Override
  public String toString() {

    String ret = "";
    ret += "Y7\n" + this.y7;
    ret += "\nY6\n" + this.y6;
    ret += "\nY5\n" + this.y5;
    ret += "\nY4\n" + this.y4;
    ret += "\nY3\n" + this.y3;
    ret += "\nY2\n" + this.y2;
    ret += "\nY1\n" + this.y1;
    ret += "\nY0\n" + this.y0;
    ret += "\nTTM  : " + this.ttm.date + "\t" + this.latestPrice;
    return ret;
  }

}
