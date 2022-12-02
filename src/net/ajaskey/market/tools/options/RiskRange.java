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

import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;

public class RiskRange {

  /**
   * For testing
   *
   * @param args
   */
  public static void main(String[] args) {
    final List<String> data = TextUtils.readTextFile("data/options/DMPData.csv", true);

    for (final String s : data) {
      final RiskRange rr = new RiskRange(s);
      if (rr.isValid()) {
        System.out.println(rr);
      }
    }

  }

  public String code;
  public double dmp1l;
  public double dmp1u;
  public double dmp2l;
  public double dmp2u;
  public double dmp3l;
  public double dmp3u;
  public double dmpPoc;
  public double last;

  private boolean valid;

  public RiskRange(String s) {
    try {
      final String fld[] = s.split(",");
      this.code = fld[0].trim().toUpperCase();
      this.dmp3u = Double.parseDouble(fld[1].trim());
      this.dmp2u = Double.parseDouble(fld[2].trim());
      this.dmp1u = Double.parseDouble(fld[3].trim());
      this.dmpPoc = Double.parseDouble(fld[4].trim());
      this.last = Double.parseDouble(fld[5].trim());
      this.dmp1l = Double.parseDouble(fld[6].trim());
      this.dmp2l = Double.parseDouble(fld[7].trim());
      this.dmp3l = Double.parseDouble(fld[8].trim());
      this.setValid(true);
    }
    catch (final Exception e) {
      this.setValid(false);
    }
  }

  /**
   *
   * @return
   */
  public boolean isValid() {
    return this.valid;
  }

  @Override
  public String toString() {
    String ret = "Code : " + this.code + Utils.NL;
    ret += String.format(" DMP3U : %.2f%n", this.dmp3u);
    ret += String.format(" DMP2U : %.2f%n", this.dmp2u);
    ret += String.format(" DMP1U : %.2f%n", this.dmp1u);
    ret += String.format(" POC   : %.2f%n", this.dmpPoc);
    ret += String.format(" DMP1L : %.2f%n", this.dmp1l);
    ret += String.format(" DMP2L : %.2f%n", this.dmp2l);
    ret += String.format(" DMP3L : %.2f%n", this.dmp3l);
    ret += String.format(" Last  : %.2f", this.last);
    return ret;
  }

  /**
   *
   * @param valid
   */
  private void setValid(boolean valid) {
    this.valid = valid;
  }

}
