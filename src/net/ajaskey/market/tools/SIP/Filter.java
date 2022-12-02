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

package net.ajaskey.market.tools.SIP;

import java.util.ArrayList;
import java.util.List;

public class Filter {

  public static void main(final String[] args) {

  }

  public FilterData epsQoQ = null;

  private List<CompanyData> cList = null;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public Filter(final List<CompanyData> cdList) {

    this.cList = cdList;
    this.epsQoQ = new FilterData(0.0);
  }

  public void addBound(final double val) {

  }

  public List<CompanyData> execute() {

    final List<CompanyData> retList = new ArrayList<>();
    for (final CompanyData cd : this.cList) {
      if (this.epsQoQ.check(cd.id.epsDilCont.dd.qoqGrowth)) {

        retList.add(cd);
      }
    }

    return retList;

  }

}
