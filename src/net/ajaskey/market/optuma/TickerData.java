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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.ajaskey.common.DateTime;

public class TickerData {

  private final static String DELIMITER = ",";

  private final static int FIELDS = 7;

  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
  public double[]                       close;
  public DateTime[]                     date;
  public int                            days;
  public double[]                       high;
  public double[]                       low;
  public double[]                       oi;

  public double[] open;

  public String ticker;

  public double[] volume;

  /**
   * This method serves as a constructor for the class.
   *
   * @param tkr
   * @param data
   */
  public TickerData(final String tkr, final List<String> data) {

    this.ticker = tkr;
    this.days = data.size();
    this.date = new DateTime[this.days];
    this.open = new double[this.days];
    this.high = new double[this.days];
    this.low = new double[this.days];
    this.close = new double[this.days];
    this.volume = new double[this.days];
    this.oi = new double[this.days];
    int knt = 0;
    for (final String s : data) {
      try {
        if (this.checkLine(s)) {
          final String fld[] = s.split(TickerData.DELIMITER);
          final Date d = TickerData.sdf.parse(fld[1].trim());
          this.date[knt] = new DateTime(d);

          this.open[knt] = Double.parseDouble(fld[2].trim());
          this.high[knt] = Double.parseDouble(fld[3].trim());
          this.low[knt] = Double.parseDouble(fld[4].trim());
          this.close[knt] = Double.parseDouble(fld[5].trim());
          this.volume[knt] = Double.parseDouble(fld[6].trim());
          this.oi[knt] = Double.parseDouble(fld[7].trim());

          knt++;
        }
      }
      catch (final Exception e) {
        System.out.println("Failed : " + s);
      }
    }
  }

  public String getTicker() {
    return this.ticker;
  }

  /**
   *
   * net.ajaskey.market.optuma.checkLine
   *
   * @param line
   * @return
   */
  private boolean checkLine(final String line) {

    boolean ret = true;

    if (line == null) {
      ret = false;
    }
    else {
      final String fld[] = line.trim().split(TickerData.DELIMITER);
      if (fld.length != TickerData.FIELDS) {
        ret = false;
      }
      else {
        Date d;
        try {
          d = TickerData.sdf.parse(fld[1].trim());
          new DateTime(d);
          Double.parseDouble(fld[2].trim());

        }
        catch (final ParseException e) {
          ret = false;
        }
      }
    }

    return ret;
  }

}
