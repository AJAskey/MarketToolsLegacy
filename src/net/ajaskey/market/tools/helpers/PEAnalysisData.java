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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PEAnalysisData {

  final private static SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yyyy");
  public Calendar                       date;

  public double price;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public PEAnalysisData() {

    this.date = Calendar.getInstance();
    this.price = 0;
  }

  /**
   * This method serves as a constructor for the class.
   *
   * @param trim
   * @param trim2
   * @throws ParseException
   */
  public PEAnalysisData(final String sDate, final String sPrice) throws ParseException {

    try {
      this.date = Calendar.getInstance();
      this.date.setTime(PEAnalysisData.sdf.parse(sDate));
      this.price = Double.parseDouble(sPrice);
      // System.out.println(Utils.getString(date) + " " + price);
    }
    catch (final Exception e) {
      this.price = -1.0;
    }
  }

}
