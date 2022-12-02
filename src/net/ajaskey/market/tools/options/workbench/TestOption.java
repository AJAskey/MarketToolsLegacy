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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TestOption {

  public static void main(String[] args) throws FileNotFoundException {

    final List<String> codes = new ArrayList<>();
//    codes.add("SPY");
//    codes.add("OEF");
//    codes.add("QQQ");
//    codes.add("IWM");
//    codes.add("XLB");
//    codes.add("XLE");
//    codes.add("XLK");
//    codes.add("XLU");
//    codes.add("XHB");
//    codes.add("VNQ");
//    codes.add("XRT");
//    codes.add("GLD");

    codes.add("GSPC.indx");

    // codes.add("OEX.indx");

    try (PrintWriter pw = new PrintWriter("out/eod.txt")) {
      for (final String c : codes) {

        final Option opt = new Option();

        opt.processJson(c);

        // String fname = opt.writeBasefile();
        // pw.println(fname);

//        OptionStatistics os = new OptionStatistics(opt);
//        pw.println(os);

      }
    }
  }
}
