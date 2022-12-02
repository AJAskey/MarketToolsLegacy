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
package net.ajaskey.market.tools.SIP.BigDB.dataio;

public class FieldDataYear {

  private boolean          inUse;
  private FieldDataQuarter q1;
  private FieldDataQuarter q2;
  private FieldDataQuarter q3;
  private FieldDataQuarter q4;
  private final int        year;

  /**
   * Constructor
   *
   * @param yr year
   */
  public FieldDataYear(int yr) {
    this.inUse = false;
    this.year = yr;
    this.q1 = null;
    this.q2 = null;
    this.q3 = null;
    this.q4 = null;
  }

  /**
   * Returns the requested quarter of data.
   *
   * @param qtr quarter (1-4)
   * @return FieldDataQuarter
   */
  public FieldDataQuarter getQ(int qtr) {

    FieldDataQuarter ret = null;

    try {
      if (this.inUse) {
        if (qtr == 1) {
          ret = this.q1;
        }
        else if (qtr == 2) {
          ret = this.q2;
        }
        else if (qtr == 3) {
          ret = this.q3;
        }
        else if (qtr == 4) {
          ret = this.q4;
        }
      }
    }
    catch (final Exception e) {
      ret = null;
    }
    return ret;
  }

  public int getYear() {
    return this.year;
  }

  public boolean isInUse() {
    return this.inUse;
  }

  /**
   * Checks if requested data has been set.
   *
   * @param qtr quarter
   * @return TRUE if data is available. FALSE otherwise.
   */
  public boolean quarterDataAvailable(int qtr) {

    if (this.inUse) {
      if (qtr == 1) {
        if (this.q1 == null) {
          return false;
        }
        return this.q1.fieldDataList != null;
      }
      else if (qtr == 2) {
        if (this.q2 == null) {
          return false;
        }
        return this.q2.fieldDataList != null;
      }
      else if (qtr == 3) {
        if (this.q3 == null) {
          return false;
        }
        return this.q3.fieldDataList != null;
      }
      else if (qtr == 4) {
        if (this.q4 == null) {
          return false;
        }
        return this.q4.fieldDataList != null;
      }
    }
    return false;
  }

  /**
   * Sets the requested quarter with FieldDataQuarter passed in.
   *
   * @param qtr quarter
   * @param fdq FieldDataQuarter
   */
  public void setQ(int qtr, FieldDataQuarter fdq) {
    this.inUse = true;
    if (qtr == 1) {
      this.q1 = fdq;
    }
    else if (qtr == 2) {
      this.q2 = fdq;
    }
    else if (qtr == 3) {
      this.q3 = fdq;
    }
    else if (qtr == 4) {
      this.q4 = fdq;
    }
  }
}
