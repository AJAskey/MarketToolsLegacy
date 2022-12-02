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

import java.util.Comparator;

public class SortOhlcv implements Comparator<OhlcvData> {

  @Override
  public int compare(final OhlcvData d1, final OhlcvData d2) {

    int ret = 0;
    if (d1.date.isLessThan(d2.date)) {
      ret = -1;
    }
    else if (d1.date.isGreaterThan(d2.date)) {
      ret = 1;
    }
    return ret;
  }
}
