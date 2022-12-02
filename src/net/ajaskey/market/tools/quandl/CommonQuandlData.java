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
import java.util.Calendar;

import net.ajaskey.common.DateTime;

public class CommonQuandlData {

  public DateTime date;
  public Double[] dd;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public CommonQuandlData(final Calendar cal, final Double[] dl) {

    this.date = new DateTime(cal);
    this.dd = dl;
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

        // System.out.println(field.getType());

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
