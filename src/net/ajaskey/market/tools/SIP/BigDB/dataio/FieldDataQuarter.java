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
package net.ajaskey.market.tools.SIP.BigDB.dataio;

import java.util.ArrayList;
import java.util.List;

public class FieldDataQuarter {

  public List<FieldData> fieldDataList = null;
  private final int      quarter;
  private final int      year;

  /**
   * Constructor
   *
   * @param yr  year
   * @param qtr quarter
   * @param fdl LIst of FieldData
   */
  public FieldDataQuarter(int yr, int qtr, List<FieldData> fdl) {
    this.year = yr;
    this.quarter = qtr;
    if (fdl == null) {
      this.fieldDataList = new ArrayList<>();
    }
    else {
      this.fieldDataList = fdl;
    }
  }

  public int getQuarter() {
    return this.quarter;
  }

  public int getYear() {
    return this.year;
  }

}
