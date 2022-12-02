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
package net.ajaskey.market.tools.options;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;

public class CallPutList {

  // private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  private static String code   = "";
  private static double cPrice = 0.0;

  private static String               info;
  private static List<CboeOptionData> odList = new ArrayList<>();

  /**
   *
   * @param x
   * @param s
   * @param type
   * @param dil
   * @return
   */
  public static CboeCallPutData findData(DateTime x, double s, int type, List<CboeOptionData> dil) {

    for (final CboeOptionData od : dil) {
      if (od.strike == s) {
        if (od.expiry.sameDate(x)) {
          if (type == OptionsProcessor.ACALL) {
            return od.call;
          }
          else {
            return od.put;
          }
        }
      }
    }

    return null;

  }

  public static String getCode() {
    return CallPutList.code;
  }

  public static double getcPrice() {
    return CallPutList.cPrice;
  }

  public static String getInfo() {
    return CallPutList.info;
  }

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    CallPutList.odList.clear();
    CallPutList.odList = CallPutList.readCboeData("data/options/spy-options.dat", new DateTime(), new DateTime(), 0);

    for (final CboeOptionData od : CallPutList.odList) {
      System.out.println(od);
    }

  }

  /**
   * Find data for all strikes on-or-after firstExpiry.
   *
   * @param fname
   * @param firstExpiry
   * @param bDate
   * @return
   */
  public static List<CboeOptionData> readCboeData(String fname, DateTime firstExpiry, DateTime bDate, int minOi) {

    CallPutList.odList.clear();

    final List<String> data = TextUtils.readTextFile(fname, true);

    final List<CboeOptionData> retList = new ArrayList<>();

    final String fld[] = data.get(0).split(",");
    CallPutList.code = fld[0].trim();
    CallPutList.cPrice = Double.parseDouble(fld[1].trim());
    final String tmp = data.get(1).trim();
    final int idx = tmp.indexOf(",Ask,");
    CallPutList.info = tmp.substring(0, idx).trim().replace(",Bid,", " Bid=");

    for (int i = 3; i < data.size(); i++) {

      final CboeOptionData od = new CboeOptionData(data.get(i), CallPutList.cPrice, CallPutList.code, firstExpiry, bDate);

      final int oi = od.call.oi + od.put.oi;
      if (od.valid && oi >= minOi) {
        retList.add(od);
      }
    }

    return retList;
  }

  /**
   * Find data for one strike.
   *
   * @param fname
   * @param theExpiry
   * @param minOi
   * @return
   */
  public static List<CboeOptionData> readCboeData(String fname, DateTime theExpiry, int minOi) {
    CallPutList.odList.clear();

    final List<String> data = TextUtils.readTextFile(fname, true);

    final List<CboeOptionData> retList = new ArrayList<>();

    final String fld[] = data.get(0).split(",");
    CallPutList.code = fld[0].trim();
    CallPutList.cPrice = Double.parseDouble(fld[1].trim());
    final String tmp = data.get(1).trim();
    final int idx = tmp.indexOf(",Ask,");
    CallPutList.info = tmp.substring(0, idx).trim().replace(",Bid,", " Bid=");

    for (int i = 3; i < data.size(); i++) {

      final CboeOptionData od = new CboeOptionData(data.get(i), CallPutList.cPrice, CallPutList.code, theExpiry);

      final int oi = od.call.oi + od.put.oi;
      if (od.valid && oi >= minOi) {
        retList.add(od);
      }
    }

    return retList;
  }

}
