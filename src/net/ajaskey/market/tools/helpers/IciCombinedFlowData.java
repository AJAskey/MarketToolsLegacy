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

public class IciCombinedFlowData {

  private static SimpleDateFormat sdf       = new SimpleDateFormat("MM/dd/yyyy");
  private static SimpleDateFormat sdfOptuma = new SimpleDateFormat("yyyy-MM-dd");
  public long                     bondMuni;
  public long                     bondTaxable;
  public long                     commodity;
  public Calendar                 date;
  public long                     equityDomestic;

  public long equityWorld;

  public boolean valid;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public IciCombinedFlowData() {

    this.valid = false;
    this.equityDomestic = 0;
    this.equityWorld = 0;
    this.bondTaxable = 0;
    this.bondMuni = 0;
    this.commodity = 0;
  }

  public void build(final String line) {

    new IciCombinedFlowData();
    try {
      final String str = line.replaceAll(",", "").replaceAll("\"", "").trim();
      final String fld[] = str.split("\\s+");
      final Date date = IciCombinedFlowData.sdf.parse(fld[0].trim());
      this.date = Calendar.getInstance();
      this.date.setTime(date);
      this.equityDomestic = Long.parseLong(fld[3].trim());
      this.equityWorld = Long.parseLong(fld[4].trim());
      this.bondTaxable = Long.parseLong(fld[7].trim());
      this.bondMuni = Long.parseLong(fld[8].trim());
      this.commodity = Long.parseLong(fld[9].trim());
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

    String ret = IciCombinedFlowData.sdfOptuma.format(this.date.getTime());
    ret += Utils.TAB + this.equityDomestic;
    ret += Utils.TAB + this.equityWorld;
    ret += Utils.TAB + this.bondTaxable;
    ret += Utils.TAB + this.bondMuni;
    ret += Utils.TAB + this.commodity;
    return ret;
  }

}
