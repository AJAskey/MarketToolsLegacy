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

package net.ajaskey.market.tools.quandl;

import java.lang.reflect.Field;

import net.ajaskey.common.DateTime;

public class NaaimData {

  public double   bbDiff;
  public DateTime date;
  public double   mean;
  public double   median;
  public double   mostBearish;
  public double   mostBullish;
  public double   number;
  public double   q1;
  public double   q3;
  public double   sp500;
  public double   stdDev;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public NaaimData(final DateTime dt, final double mn, final double mbear, final double qq1, final double med, final double qq3, final double mbull,
      final double s, final double n, final double sp) {

    this.date = dt;
    this.mean = mn;
    this.mostBearish = mbear;
    if (mbear == 0.0) {
      this.mostBearish = 0.01;
    }
    this.q1 = qq1;
    this.median = med;
    this.q3 = qq3;
    this.mostBullish = mbull;
    if (this.mostBullish == 0.0) {
      this.mostBullish = 0.01;
    }
    this.bbDiff = mbull + mbear;
    if (this.bbDiff == 0.0) {
      this.bbDiff = 0.01;
    }

    this.stdDev = s;
    this.number = n;
    this.sp500 = sp;

  }

  @Override
  public String toString() {

    final StringBuilder result = new StringBuilder();
    final String newLine = System.getProperty("line.separator");
    result.append(this.getClass().getName());
    result.append(" Object {");
    result.append(newLine);
    final Field[] fields = this.getClass().getDeclaredFields();
    for (final Field field : fields) {
      result.append("  ");
      try {
        result.append(field.getName());
        result.append(": ");
        final String ft = field.getType().toString();
        if (ft.equals("class java.util.Calendar")) {
          result.append(this.toString());
        }
        else {
          result.append(field.get(this));
        }
      }
      catch (final IllegalAccessException ex) {
        System.out.println(ex);
      }
      result.append(newLine);
    }
    result.append("}");
    return result.toString();
  }

}
