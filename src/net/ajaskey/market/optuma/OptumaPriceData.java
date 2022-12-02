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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.ajaskey.common.DateTime;

public class OptumaPriceData {

  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

  /**
   * 
   * @param code
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static List<PriceData> getPriceData(final String code) throws FileNotFoundException, IOException {

    final List<PriceData> pd = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(code))) {

      String line;
      while ((line = reader.readLine()) != null) {
        final String str = line.trim();
        if (str.length() > 0) {
          final String fld[] = str.split(",");
          if (fld.length >= 7) {
            try {
              final Date d = OptumaPriceData.sdf.parse(fld[1].trim());
              final DateTime dt = new DateTime(d);
              final PriceData p = new PriceData(dt, Double.parseDouble(fld[2].trim()), Double.parseDouble(fld[3].trim()),
                  Double.parseDouble(fld[4].trim()), Double.parseDouble(fld[5].trim()), Long.parseLong(fld[6].trim()));
              pd.add(p);
            }
            catch (final Exception e) {
            }
          }
        }
      }
    }

    // if (pd.size() > 0) {
    // Collections.reverse(pd);
    // }
    return pd;

  }

}
