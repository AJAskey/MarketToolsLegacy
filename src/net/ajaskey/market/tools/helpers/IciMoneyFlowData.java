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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.ajaskey.common.Utils;

public class IciMoneyFlowData {

  private static SimpleDateFormat sdf       = new SimpleDateFormat("MM/dd/yyyy");
  private static SimpleDateFormat sdfOptuma = new SimpleDateFormat("yyyy-MM-dd");
  public Calendar                 date;
  public long                     domestic;
  public long                     equity;
  public long                     lcap;
  public long                     mcap;
  public long                     scap;

  public long total;

  public boolean valid;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public IciMoneyFlowData() {

    this.valid = false;
    this.total = 0;
  }

  public void build(final String line) {

    new IciMoneyFlowData();
    try {
      final String str = line.replaceAll(",", "").replaceAll("\"", "").trim();
      final String fld[] = str.split("\\s+");
      final Date date = IciMoneyFlowData.sdf.parse(fld[0].trim());
      this.date = Calendar.getInstance();
      this.date.setTime(date);
      this.total = Long.parseLong(fld[1].trim());
      this.equity = Long.parseLong(fld[2].trim());
      this.domestic = Long.parseLong(fld[3].trim());
      this.lcap = Long.parseLong(fld[4].trim());
      this.mcap = Long.parseLong(fld[5].trim());
      this.scap = Long.parseLong(fld[6].trim());
      this.valid = true;
    }
    catch (final Exception e) {
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = IciMoneyFlowData.sdfOptuma.format(this.date.getTime());
    ret += Utils.TAB + this.total;
    ret += Utils.TAB + this.equity;
    ret += Utils.TAB + this.domestic;
    ret += Utils.TAB + this.lcap;
    ret += Utils.TAB + this.mcap;
    ret += Utils.TAB + this.scap;
    return ret;
  }

}
