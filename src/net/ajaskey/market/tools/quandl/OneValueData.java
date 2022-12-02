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

import net.ajaskey.common.DateTime;

public class OneValueData {

  public DateTime date;
  public double   value;

  /**
   * This method serves as a constructor for the class.
   *
   * @param date
   * @param double1
   */
  public OneValueData(final DateTime dt, final Double v) {

    this.date = dt;
    this.value = v;
  }

  @Override
  public String toString() {

    final String s = String.format("%s\t%f", this.date, this.value);
    return s;
  }
}
