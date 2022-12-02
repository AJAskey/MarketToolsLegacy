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

import java.io.IOException;

public class OptionStatus {

  public static void main(String[] args) throws IOException {

    OptionUtils.getDownloads();

    final OptionStatistics osAll = new OptionStatistics();

    System.out.printf("%n -----------------SPX----------------------- %n%n");

    final OptionCboe spx = new OptionCboe("SPX");
    final OptionCboe spy = new OptionCboe("SPY");
    final OptionCboe spyg = new OptionCboe("SPYG");
    final OptionCboe spyv = new OptionCboe("SPYV");
    final OptionCboe rsp = new OptionCboe("RSP");

    final OptionStatistics osSPX = new OptionStatistics();

    osSPX.addToStats(spx.getOpt());
    osSPX.addToStats(spy.getOpt());
    osSPX.addToStats(spyg.getOpt());
    osSPX.addToStats(spyv.getOpt());
    osSPX.addToStats(rsp.getOpt());

    osAll.addToStats(spx.getOpt());
    osAll.addToStats(spy.getOpt());
    osAll.addToStats(spyg.getOpt());
    osAll.addToStats(spyv.getOpt());
    osAll.addToStats(rsp.getOpt());

    System.out.println(osSPX);

    System.out.printf("%n -----------------NDX----------------------- %n%n");

    final OptionCboe ndx = new OptionCboe("NDX");
    final OptionCboe qqq = new OptionCboe("QQQ");

    final OptionStatistics osNDX = new OptionStatistics();
    osNDX.addToStats(ndx.getOpt());
    osNDX.addToStats(qqq.getOpt());

    osAll.addToStats(ndx.getOpt());
    osAll.addToStats(qqq.getOpt());

    System.out.println(osNDX);

    System.out.printf("%n -----------------RUT----------------------- %n%n");

    final OptionCboe rut = new OptionCboe("RUT");
    final OptionCboe iwm = new OptionCboe("IWM");
    final OptionCboe iwn = new OptionCboe("IWN");
    final OptionCboe iwo = new OptionCboe("IWO");

    final OptionStatistics osRUT = new OptionStatistics();
    osRUT.addToStats(rut.getOpt());
    osRUT.addToStats(iwm.getOpt());
    osRUT.addToStats(iwn.getOpt());
    osRUT.addToStats(iwo.getOpt());

    osAll.addToStats(rut.getOpt());
    osAll.addToStats(iwm.getOpt());
    osAll.addToStats(iwn.getOpt());
    osAll.addToStats(iwo.getOpt());

    System.out.println(osRUT);

    System.out.printf("%n -----------------ALL----------------------- %n%n");

    final OptionCboe oex = new OptionCboe("OEX");
    final OptionCboe dia = new OptionCboe("DIA");
    osAll.addToStats(oex.getOpt());
    osAll.addToStats(dia.getOpt());

    System.out.println(osAll);

  }

}
