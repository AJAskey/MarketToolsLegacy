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

package net.ajaskey.market.tools.helpers;

import net.ajaskey.common.DateTime;

public class OhlcvData {

  public enum FormType {
    FULL, SHORT
  }

  public double close;

  public DateTime  date;
  public double    high;
  public double    low;
  public double    open;
  public long      volume;
  private FormType form;

  private boolean valid;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public OhlcvData() {

    // TODO Auto-generated constructor stub
  }

  /**
   *
   * This method serves as a constructor for the class.
   *
   * @param dt
   * @param o
   * @param h
   * @param l
   * @param c
   * @param vol
   */
  public OhlcvData(final DateTime dt, final double o, final double h, final double l, final double c, final long vol) {

    this.date = dt;
    this.open = o;
    this.high = h;
    this.low = l;
    this.close = c;
    this.volume = vol;
    this.setForm();
    this.valid = true;
  }

  /**
   *
   * This method serves as a constructor for the class.
   *
   * @param fld
   * @param fmt
   */
  public OhlcvData(String fld[], String fmt, int startIdx) {

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

    final String ret = String.format("%s\t%10.2f", this.date, this.close);
    return ret;
  }

  @Override
  public String toString() {

    final String dt = this.date.format("dd-MMM-yyyy");
    final String ret = String.format("%s\t%10.2f\t%10.2f\t%10.2f\t%10.2f\t%10d\t%s", dt, this.open, this.high, this.low, this.close, this.volume,
        this.form);
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
