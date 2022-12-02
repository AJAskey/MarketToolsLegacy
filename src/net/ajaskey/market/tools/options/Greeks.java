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

public class Greeks {

  // 04/17/2020,
  // SPY200417C00300000,18.9,0,21.04,21.16,0,0.1747,0.6816,0.0106,491,300.000,
  // SPY200417P00300000,6.93,-0.83,6.92,6.95,48,0.1729,-0.3097,0.0107,4056

  public static void main(String[] args) {

    /**
     *
     * @param type
     * @param id
     * @param strike
     * @param ulPrice
     * @param expiry
     * @param iv
     */
    final OptionsProcessor op = new OptionsProcessor(OptionsProcessor.ACALL, "SPY 300", 300.0, 314.0, new DateTime(2020, DateTime.APRIL, 17), 0.1747);

    System.out.println(op);
    System.out.printf("Delta %.3f\tTheta %.3f%n", 0.70, 0.04);

  }

}
