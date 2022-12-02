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

package net.ajaskey.market.optuma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;

public class ReadStockCharts {

  /**
   *
   * net.ajaskey.market.ta.input.getData
   *
   * @param fname
   * @return
   */
  public static List<String> getData(String fname) {

    final List<String> data = TextUtils.readTextFile(fname, true);
    final List<String> subdata = new ArrayList<>();
    boolean process = false;

    for (final String s : data) {
      if (s.contains("<pre>")) {
        process = true;
      }
      else if (s.contains("</pre>")) {
        process = false;
        break;
      }

      if (process) {
        if (s.contains("Day")) {
          continue;
        }
        else if (s.contains("===")) {
          continue;
        }
        else if (s.contains("<pre>")) {
          continue;
        }
        subdata.add(s);
      }
    }

    Collections.reverse(subdata);

    return subdata;

  }

  /**
   *
   * @param data
   * @return
   */
  public static List<PriceData> getPriceData(List<String> data) {

    final List<PriceData> ret = new ArrayList<>();

    return ret;
  }

  public static void readHistories() {

  }

  /**
   * net.ajaskey.market.ta.input.main
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    final String[] ext = { "html" };

    final List<File> files = Utils.getDirTree("data/scdata", ext);
    for (final File f : files) {
      System.out.println(f.getAbsolutePath());
      String fname = Utils.getFileBaseName(f);
      fname = fname.replace("-SC", "");
      final List<String> daData = ReadStockCharts.getData(f.getAbsolutePath());

      try (PrintWriter pw = new PrintWriter("out/hist-" + fname + ".csv")) {
        for (final String s : daData) {
          // System.out.println(s);
          final String fld[] = s.split("\\s+");
          final PriceData ohlcv = new PriceData(fld, "MM-dd-yyyy", 1);
          if (ohlcv.isValid()) {
            pw.println(ohlcv.toOptumaString(fname, 1.0));
          }
        }
      }

      double scaler = 1.0;

      if (f.getName().contains("TNX")) {
        scaler = 10.0;
      }
      else if (f.getName().contains("TYX")) {
        scaler = 10.0;
      }

      try (PrintWriter pw = new PrintWriter("out/" + fname + ".csv"); PrintWriter pws = new PrintWriter("out/" + fname + "short_.csv")) {
        for (final String s : daData) {
          // System.out.println(s);
          final String fld[] = s.split("\\s+");
          final PriceData ohlcv = new PriceData(fld, "MM-dd-yyyy", 1);
          if (ohlcv.isValid()) {
            pws.println(ohlcv.toShortString(scaler));
            pw.println(ohlcv.toOptumaString(fname, scaler));
          }
        }
      }
    }
  }

}
