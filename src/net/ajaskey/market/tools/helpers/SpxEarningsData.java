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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SpxEarningsData {

  private static final double NA_VALUE = -99999999.99;

  public static List<SpxEarningsData> readData(final String fname) throws IOException {

    final List<SpxEarningsData> data = new ArrayList<>();
    final Charset charset = Charset.forName("UTF-8");

    final File file = new File("data\\" + fname);
    final Path path = file.toPath();

    String line;
    try (BufferedReader reader = Files.newBufferedReader(path, charset)) {

      while ((line = reader.readLine()) != null) {
        if (line.length() > 10) {
          try {
            final SpxEarningsData d = new SpxEarningsData();

            final String fld[] = line.split("\\t");

            // for (String s : fld) {
            // System.out.printf("%12s", s);
            // }
            // System.out.println();

            d.ticker = fld[0].trim().replaceAll("\"", "");

            if (!d.ticker.contains("BRK.A")) {

              if (fld[1].trim().length() > 0.0) {
                d.mktcap = Double.parseDouble(fld[1].trim());
              }

              if (fld[2].trim().length() > 0.0) {
                d.shares = Double.parseDouble(fld[2].trim());
                if (d.shares == SpxEarningsData.NA_VALUE) {
                  d.shares = 0.0;
                }
              }

              if (fld[3].trim().length() > 0.0) {
                d.netIncAfterTax = Double.parseDouble(fld[3].trim());
                if (d.netIncAfterTax == SpxEarningsData.NA_VALUE) {
                  d.netIncAfterTax = 0.0;
                }
              }

              if (fld[4].trim().length() > 0.0) {
                d.eps = Double.parseDouble(fld[4].trim());
                if (d.eps == SpxEarningsData.NA_VALUE) {
                  d.eps = 0.0;
                }
              }

              if (fld[5].trim().length() > 0.0) {
                d.eps1y = Double.parseDouble(fld[5].trim());
                if (d.eps1y == SpxEarningsData.NA_VALUE) {
                  d.eps1y = 0.0;
                }
              }

              if (fld[6].trim().length() > 0.0) {
                d.div = Double.parseDouble(fld[6].trim());
              }

              System.out.print(d);

              data.add(d);
            }
          }
          catch (final Exception e) {
            System.out.println("Error : " + line);
          }
        }
      }
    }

    return data;
  }

  public double div;
  public double eps;
  public double eps1y;
  public double mktcap;
  public double netIncAfterTax;

  public double shares;

  public String ticker;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public SpxEarningsData() {

    this.ticker = null;
    this.mktcap = 0;
    this.shares = 0;
    this.netIncAfterTax = 0;
    this.eps = 0;
    this.eps1y = 0;
    this.div = 0;
  }

  @Override
  public String toString() {

    final String str = String.format("%-9s %10.1f %11.3f %10.1f %10.3f %10.3f %n", this.ticker, this.mktcap, this.shares, this.netIncAfterTax,
        this.eps, this.eps1y);
    return str;

  }

}
