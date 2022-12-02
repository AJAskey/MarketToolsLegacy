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

package net.ajaskey.market.tools.SIP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CashData {

  final private static String TAB = "\t";

  /**
   * net.ajaskey.market.tools.SIP.main
   *
   * @param args
   * @throws IOException
   */
  public static void main(final String[] args) throws IOException {

    QuarterlyData.init();

    try (BufferedReader reader = new BufferedReader(new FileReader("data/US-STOCKS-CASH.TXT"))) {
      String line = "";
      while ((line = reader.readLine()) != null) {
        final String str = line.replaceAll("\"", "").trim();
        if (str.length() > 1) {

          // System.out.println(str);
          final String fld[] = str.split(CashData.TAB);

          final CashData cashData = CashData.setCashDataInfo(fld);
          System.out.println(fld[0]);
          System.out.println(cashData);
        }
      }
    }

  }

  /**
   *
   * net.ajaskey.market.tools.SIP.setCashDataInfo
   *
   * @param fld
   * @return
   */
  public static CashData setCashDataInfo(final String[] fld) {

    final CashData cashData = new CashData();

    cashData.capEx.parse(fld);
    cashData.cashFromFin.parse(fld);
    cashData.cashFromInv.parse(fld);
    cashData.cashFromOps.parse(fld);

    return cashData;

  }

  public QuarterlyData capEx;

  public QuarterlyData cashFromFin;

  public QuarterlyData cashFromInv;

  public QuarterlyData cashFromOps;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public CashData() {

    this.capEx = new QuarterlyData("capEx");
    this.cashFromOps = new QuarterlyData("cashFromOps");
    this.cashFromFin = new QuarterlyData("cashFromFin");
    this.cashFromInv = new QuarterlyData("cashFromInv");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = "";
    ret += CashData.TAB + this.capEx;
    ret += CashData.TAB + this.cashFromOps;
    ret += CashData.TAB + this.cashFromFin;
    ret += CashData.TAB + this.cashFromInv;
    return ret;
  }

}
