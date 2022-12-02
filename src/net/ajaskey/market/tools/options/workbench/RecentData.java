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
package net.ajaskey.market.tools.options.workbench;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;

public class RecentData {

  static List<File> codeList = new ArrayList<>();

  /**
   *
   * @param code
   * @return
   */
  public static String getMostRecent(String code) {

    final String ext[] = { "csv" };
    final List<File> files = Utils.getDirTree("D:\\dev\\Eclipse-03-2002\\workspace\\OptionsWorkbench\\data\\options", ext);
    for (final File f : files) {
      if (f.getName().startsWith(code)) {
        RecentData.codeList.add(f);
        System.out.println(f.getName());
      }
    }

    File recentF = null;
    DateTime recentD = new DateTime(1990, DateTime.JANUARY, 1);
    for (final File f : RecentData.codeList) {
      final String name = f.getName();
      final String dStr = name.replace(code, "").replace("_optiondata.csv", "").replace("_", "");
      System.out.println(dStr);
      final DateTime dt = new DateTime(dStr, "yyyyMMddHHmmss");
      if (dt.isGreaterThan(recentD)) {
        recentD = dt;
        recentF = f;
      }
    }
    String ret = "";
    if (recentF != null) {
      ret = recentF.getAbsolutePath();
    }
    return ret;
  }

  public static void main(String[] args) {

    final String code = "XLK";

    final String fname = RecentData.getMostRecent(code);
    System.out.println(fname);

  }

}
