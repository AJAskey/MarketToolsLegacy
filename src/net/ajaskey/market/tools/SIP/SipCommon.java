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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.ajaskey.common.DateTime;

public class SipCommon {

  public final static double BILLION  = 1e9;
  public final static double MILLION  = 1e6;
  public final static double THOUSAND = 1e3;

  private final static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  private final int INC;

  private int ptr;

  private final String splitChar;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public SipCommon(final String ch, final int inc, final int ver) {

    this.reset();
    this.INC = inc;
    this.splitChar = ch;
  }

  public DataSet getData5(final String name, final String line, final DataSet.dMode mode, final double scaler) {

    final String fld[] = line.replace("\"", "").split(this.splitChar);

    final DataSet ds = new DataSet(fld[1], name, fld[0], fld[2], fld, this.ptr, mode);
    final DataSet dsRet = DataSet.scale(ds, scaler);
    // System.out.println(dsRet);
    this.ptr += this.INC;
    return dsRet;
  }

  /**
   * net.ajaskey.market.tools.sipro.v4.getDate
   *
   * @return
   * @throws ParseException
   */
  public DateTime getDate(final String line) throws ParseException {

    final String fld[] = line.replace("\"", "").split(this.splitChar);
    final int len = fld.length - 1;
    final String sdate = fld[len].trim();
    final Date d = SipCommon.sdf.parse(sdate);
    final DateTime ret = new DateTime();
    ret.set(d);
    return ret;
  }

  public DataSet getUsData5(final String name, final String line, final DataSet.dMode mode, final double scaler) {

    final String fld[] = line.replace("\"", "").split(this.splitChar);

    final DataSet ds = new DataSet("US", name, fld[0], fld[2], fld, this.ptr, mode);
    final DataSet dsRet = DataSet.scale(ds, scaler);
    // System.out.println(dsRet);
    this.ptr += this.INC;
    return dsRet;
  }

  public void reset() {

    this.ptr = 2;
  }

}
