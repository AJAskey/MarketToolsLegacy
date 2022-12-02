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

package net.ajaskey.market.tools.options.data;

import java.util.Calendar;

import net.ajaskey.common.DateTime;

public class OptionsData {

  /**
   *
   * @param args
   */
  public static void main(final String[] args) {

    final DateTime ed = new DateTime(2019, Calendar.DECEMBER, 15);
    final OptionsData od = new OptionsData(10.0, 12.0, 0.01, 0.15, ed);
    System.out.println(od);
  }

  private final double   currentPriceOfUnderlying;
  private final long     daysToExpiry;
  private double         delta;
  private final DateTime expiry;
  private double         gamma;
  private final double   interestRate;
  private double         rho;
  private final double   strikePrice;
  private double         theta;
  private double         vega;
  private final double   volatility;
  private double         yearsToExpiry;

  public OptionsData(double ul, DateTime ex, double strike, double iv, double theta, double delta, double gamma, double rho, double vega) {
    this.expiry = new DateTime(ex);
    this.currentPriceOfUnderlying = ul;
    this.strikePrice = strike;
    this.volatility = iv;
    this.theta = theta;
    this.delta = delta;
    this.gamma = gamma;
    this.rho = rho;
    this.vega = vega;

    this.interestRate = 0;
    this.yearsToExpiry = 0;
    this.daysToExpiry = 0L;
    this.yearsToExpiry = 0.0;

  }

  /**
   *
   * @param cprice
   * @param sprice
   * @param intrate
   * @param iv
   * @param exp
   */
  public OptionsData(final double cprice, final double sprice, final double intrate, final double iv, final DateTime exp) {

    this.currentPriceOfUnderlying = cprice;
    this.strikePrice = sprice;
    this.interestRate = intrate;
    this.volatility = iv;
    this.expiry = new DateTime(exp);

    DateTime today = new DateTime();
    this.daysToExpiry = this.expiry.getDeltaDays(today);
    this.yearsToExpiry = this.expiry.getDeltaYears(today);

  }

  public double getCurrentPriceOfUnderlying() {

    return this.currentPriceOfUnderlying;
  }

  public long getDaysToExpiry() {

    return this.daysToExpiry;
  }

  public double getDelta() {

    return this.delta;
  }

  public DateTime getExpiry() {

    return this.expiry;
  }

  public double getGamma() {

    return this.gamma;
  }

  public double getInterestRate() {

    return this.interestRate;
  }

  public double getRho() {

    return this.rho;
  }

  public double getStrikePrice() {

    return this.strikePrice;
  }

  public double getTheta() {

    return this.theta;
  }

  public double getTimeToExpire() {

    return this.yearsToExpiry;
  }

  public double getVega() {

    return this.vega;
  }

  public double getVolatility() {

    return this.volatility;
  }

  @Override
  public String toString() {

    final String ret = String.format("years : %.2f%ndays  : %d", this.yearsToExpiry, this.daysToExpiry);
    return ret;
  }

  private long getDeltaDays(final Calendar c1, final Calendar c2) {

    return (c2.getTime().getTime() - c1.getTime().getTime()) / 86400000;
  }

  private double getDeltaYears(final Calendar c1, final Calendar c2) {

    return this.getDeltaDays(c1, c2) / 365.0;
  }

}
