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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;

public class SipUtils {

  /**
   *
   * @param cdList
   * @return
   */
  public static List<String> findIndustries(List<CompanyData> cdList) {
    final Set<String> ind = new HashSet<>();
    for (final CompanyData cd : cdList) {
      final String i = cd.industry.toUpperCase().trim();
      // System.out.println(i);
      ind.add(i);
    }

    final List<String> ret = new ArrayList<>(ind);
    return ret;
  }

  /**
   *
   * @param cdList
   * @return
   */
  public static List<String> findSectors(List<CompanyData> cdList) {
    final Set<String> sec = new HashSet<>();
    for (final CompanyData cd : cdList) {
      final String s = cd.sector.toUpperCase().trim();
      sec.add(s);
    }

    final List<String> ret = new ArrayList<>(sec);
    return ret;
  }

  /**
   *
   * @param ind
   * @param lst
   * @return
   */
  public static List<CompanyDerived> getIndustryCompaniesFromList(String ind, List<CompanyDerived> lst) {
    final List<CompanyDerived> retList = new ArrayList<>();
    for (final CompanyDerived cdr : lst) {
      if (cdr.getFd().getIndustry().equalsIgnoreCase(ind)) {
        retList.add(cdr);
      }
    }
    return retList;
  }

  /**
   * 
   * @param dRList
   * @return
   */
  public static List<String> getIndustryList(List<CompanyDerived> dRList) {
    final Set<String> indSet = new HashSet<>();
    for (final CompanyDerived cdr : dRList) {
      indSet.add(cdr.getFd().getIndustry());
    }

    final List<String> retList = new ArrayList<>(indSet);
    Collections.sort(retList);
    return new ArrayList<>(retList);
  }

  /**
   *
   * @param sec
   * @param lst
   * @return
   */
  public static List<CompanyDerived> getSectorCompaniesFromList(String sec, List<CompanyDerived> lst) {
    final List<CompanyDerived> retList = new ArrayList<>();
    for (final CompanyDerived cdr : lst) {
      if (cdr.getFd().getSector().equalsIgnoreCase(sec)) {
        retList.add(cdr);
      }
    }
    return retList;
  }

  public static List<String> getSectorList(List<CompanyDerived> dRList) {
    final Set<String> secSet = new HashSet<>();
    for (final CompanyDerived cdr : dRList) {
      secSet.add(cdr.getFd().getSector());
    }

    final List<String> retList = new ArrayList<>(secSet);
    Collections.sort(retList);
    return new ArrayList<>(retList);
  }

  public static double[] parseArrayDoubles(String flds, int slide) {

    final String fld[] = flds.trim().split("\\s+");

    final int len = fld.length + slide;

    final double[] ret = new double[len];

    int iret = slide;
    for (final String element : fld) {
      ret[iret++] = SipUtils.parseDouble(element);
    }

    return ret;
  }

  public static boolean parseBoolean(String fld) {
    boolean ret = false;
    try {
      ret = Boolean.parseBoolean(fld.trim());
    }
    catch (final Exception e) {
      ret = false;
    }
    return ret;
  }

  /**
   * Returns a double value represent by fld.
   *
   * @param fld Text from which to pull value.
   * @return Val or 0 if error.
   */
  public static double parseDouble(final String fld) {

    try {
      double d = Double.parseDouble(fld.trim().replace(",", ""));
      if (d > 0.0 && d < 0.0001) {
        d = 0.0;
      }
      else if (d < -999999.0) {
        d = 0.0;
      }
      return d;
    }
    catch (final Exception e) {
    }
    return 0.0;
  }

  /**
   *
   * @param fld
   * @param start
   * @param len
   * @return
   */
  public static double[] parseDoubles(String[] fld, int start, int len) {

    final double[] ret = new double[len + 1];
    int knt = 1;
    for (int i = start; i < start + len; i++) {
      ret[knt++] = SipUtils.parseDouble(fld[i].trim());
    }

    return ret;
  }

  public static int parseInt(String fld) {
    return (int) SipUtils.parseLong(fld);
  }

  /**
   * Returns a long value represent by fld.
   *
   * @param fld Text from which to pull value.
   * @return Val or 0 if error.
   */
  public static long parseLong(final String fld) {

    long ret = 0L;
    try {
      long tmp = 0L;
      final String f[] = fld.split("\\.");
      tmp = Long.parseLong(f[0].trim());
      if (tmp < 0L) {
        ret = 0L;
      }
      else {
        ret = tmp;
      }
    }
    catch (final Exception e) {
      ret = 0L;
    }

    return ret;
  }
}
