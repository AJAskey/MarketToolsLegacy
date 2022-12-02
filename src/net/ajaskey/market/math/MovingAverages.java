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

package net.ajaskey.market.math;

public class MovingAverages {

  private double       ema;
  private final double emaMultipler;
  private final int    emaWindow;
  private int          knt;
  private double       sma;
  private double       smaTot;

  // private List<Double> values = new ArrayList<>();;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public MovingAverages(final int emaWin) {

    this.knt = 0;
    this.sma = 0;
    this.ema = 0;
    this.smaTot = 0;
    this.emaWindow = emaWin;
    this.emaMultipler = 2.0 / (1.0 + this.emaWindow);
  }

  /**
   *
   * net.ajaskey.market.ta.addValue
   *
   * @param val
   * @return
   */
  public double addValue(final double val) {

    this.knt++;

    // values.add(val);

    if (this.knt <= this.emaWindow) {
      this.smaTot += val;
      this.sma = this.smaTot / this.knt;
      this.ema = this.sma;
    }
    else {
      final double pEma = this.ema;
      this.ema = (val - pEma) * this.emaMultipler + pEma;
    }

    return this.ema;
  }

  /**
   * @return the ema
   */
  public double getEma() {

    return this.ema;
  }

  /**
   * @return the emaWindow
   */
  public int getEmaWindow() {

    return this.emaWindow;
  }

  /**
   * @return the knt
   */
  public int getKnt() {

    return this.knt;
  }

  /**
   *
   * @param days
   * @return
   */
//  public double getSma(int days) {
//    double tot = 0.0;
//    int last = knt - days - 1;
//    for (int i = knt - 1; i >= last; i--) {
//      tot += values.get(i);
//    }
//    double ret = tot / (double) days;
//    return ret;
//  }

}
