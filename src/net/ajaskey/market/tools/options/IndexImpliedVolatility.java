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

import net.ajaskey.common.DateTime;
import net.ajaskey.market.optuma.TickerPriceData;

public class IndexImpliedVolatility {

  public static void main(String[] args) {

    final TickerPriceData spxData = new TickerPriceData("WI", "SPX");
    final double spxClose = spxData.getLatest();

    final double spx30dma = spxData.getMA(30);

    System.out.println(spxClose);
    System.out.println(spx30dma);

    final OptionsProcessor spxOp = new OptionsProcessor(OptionsProcessor.APUT, "SPX", spx30dma, spxClose, new DateTime(2019, DateTime.DECEMBER, 10),
        10.0);

    final double price = spxOp.getPrice();
    System.out.println(price);

    final double newIv = spxOp.findIv(price, 0.5);
    System.out.println(newIv);

  }

}
