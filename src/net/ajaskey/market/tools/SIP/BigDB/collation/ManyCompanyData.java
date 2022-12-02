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
package net.ajaskey.market.tools.SIP.BigDB.collation;

import java.util.ArrayList;
import java.util.List;

public class ManyCompanyData {

  /**
   * Create of list of company data from input list of tickers
   *
   * @param tickers The individual stock symbols
   * @return List of ManyCompanyData
   */
  public static List<ManyCompanyData> createList(List<String> tickers) {

    final List<ManyCompanyData> retList = new ArrayList<>();

    for (final String ticker : tickers) {

      final ManyCompanyData mcd = new ManyCompanyData(OneCompanyData.getCompany(ticker), ticker);
      retList.add(mcd);
    }

    return retList;
  }

  public List<OneCompanyData> ocdList = new ArrayList<>();
  public String               ticker;

  /**
   * Constructor is private for use by internal procedures.
   *
   * @param list   List of OneComanyData
   * @param ticker The individual stock symbol
   */
  private ManyCompanyData(List<OneCompanyData> list, String ticker) {
    this.ticker = ticker;
    this.ocdList = list;
  }

  @Override
  public String toString() {
    final String ret = String.format("%s : %d", this.ticker, this.ocdList.size());
    return ret;
  }
}