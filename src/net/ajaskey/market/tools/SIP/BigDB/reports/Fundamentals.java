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
package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.util.List;

import net.ajaskey.market.tools.SIP.QuarterlyData;
import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class Fundamentals {

  public static final double MILLION = 1000000.0;

  static List<FieldData> fdList;

  public static void main(String[] args) throws FileNotFoundException {

    // FieldData.setQMemory(2020, 2, FiletypeEnum.TEXT);
    FieldData.setQMemory(2021, 4, FiletypeEnum.BIG_BINARY);

    Fundamentals.fdList = FieldData.getQFromMemory(2020, 2);
    FundamentalReports.write();
  }

  public QuarterlyData shares;

}
