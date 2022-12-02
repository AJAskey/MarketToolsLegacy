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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.SipOutput;
import net.ajaskey.market.tools.SIP.SipUtils;
import net.ajaskey.market.tools.SIP.BigDB.DowEnum;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;

public class CompanyFileData implements Serializable {

  final private static int ADR = 6;

  /**
   * Stores all CompanyFileDate read in from DB.
   */
  private static List<CompanyFileData> cfdList = new ArrayList<>();

  final private static int  CITY             = 11;
  final private static int  COUNTRY          = 13;
  final private static int  DOW              = 7;
  final private static int  EMP              = 17;
  final private static int  EXCHANGE         = 2;
  private static String     fld[]            = null;
  final private static int  INDUSTRY         = 4;
  final private static int  NAME             = 0;
  final private static int  PHONE            = 15;
  final private static int  PRICE            = 18;
  final private static int  SECTOR           = 3;
  private static final long serialVersionUID = -5994427284354231386L;
  final private static int  SIC              = 5;
  final private static int  SNP              = 8;
  final private static int  STATE            = 12;
  final private static int  STREET           = 10;
  final private static int  TICKER           = 1;
  final private static int  WEB              = 16;
  final private static int  ZIP              = 14;

  public static void clearList() {
    CompanyFileData.cfdList.clear();

  }

  /**
   * Returns the CompanyFileData instance for requested ticker.
   *
   * @param ticker The name of the individual stock symbol file
   * @return CompanyFileData
   */
  public static CompanyFileData find(String ticker) {
    if (ticker != null) {
      if (ticker.trim().length() > 0) {
        for (final CompanyFileData c : CompanyFileData.cfdList) {
          if (c.getTicker().equalsIgnoreCase(ticker)) {
            return c;
          }
        }
      }
    }
    return null;
  }

  public static int getADR() {
    return CompanyFileData.ADR;
  }

  public static List<CompanyFileData> getCfdList() {
    return CompanyFileData.cfdList;
  }

  public static List<CompanyFileData> getList() {
    return CompanyFileData.cfdList;
  }

  /**
   * Returns the number of instances in the list read from the DB.
   *
   * @return count
   */
  public static int getListCount() {
    return CompanyFileData.cfdList.size();
  }

  /**
   *
   * @return
   */
  public static List<String> getTickers() {
    final List<String> tickers = new ArrayList<>();
    for (final CompanyFileData cfd : CompanyFileData.cfdList) {
      tickers.add(cfd.ticker.trim().toUpperCase());
    }
    return tickers;
  }

  /**
   * Returns a string containing text for all data in the list read from the DB.
   *
   * @return String
   */
  public static String listToString() {
    String ret = "";
    for (final CompanyFileData c : CompanyFileData.cfdList) {
      ret += c.toString();
    }
    return ret;
  }

  /**
   * Parses data and fills data structures from DB files.
   *
   * @param input List of strings to parse
   * @return CompanyFileData
   */
  public static CompanyFileData readFromDb(List<String> input) {

    final CompanyFileData cfd = new CompanyFileData();

    for (final String s : input) {

      final String[] tfld = s.split(":");

      final String fld = tfld[0].trim();

      String val = "";
      String val2 = "";
      if (tfld.length > 2) {
        val = "http:";
        val2 = tfld[2].trim();
      }
      else if (tfld.length > 1) {
        val = tfld[1].trim();
      }

      if (fld.equals("ticker")) {
        cfd.setTicker(val);
      }
      else if (fld.equals("name")) {
        cfd.setName(val);
      }
      else if (fld.equals("exchange")) {
        cfd.setExchange(val);
      }
      else if (fld.equals("sector")) {
        cfd.setSector(val);
      }
      else if (fld.equals("industry")) {
        cfd.setIndustry(val);
      }
      else if (fld.equals("sic")) {
        cfd.setSic(val);
      }
      else if (fld.equals("employees")) {
        cfd.setNumEmployees(val);
      }
      else if (fld.equals("snp index")) {
        cfd.setSnpIndex(val);
      }
      else if (fld.equals("dow index")) {
        cfd.setDowIndex(val);
      }
      else if (fld.equals("adr")) {
        cfd.setAdr(val);
      }
      else if (fld.equals("street")) {
        cfd.setStreet(val);
      }
      else if (fld.equals("city")) {
        cfd.setCity(val);
      }
      else if (fld.equals("state")) {
        cfd.setState(val);
      }
      else if (fld.equals("country")) {
        cfd.setCountry(val);
      }
      else if (fld.equals("zip")) {
        cfd.setZip(val);
      }
      else if (fld.equals("phone")) {
        cfd.setPhone(val);
      }
      else if (fld.equals("web")) {
        cfd.setWeb(val + val2);
      }
      else if (fld.equals("prices")) {
        final double[] priceQ = SipUtils.parseArrayDoubles(tfld[1], 1);
        cfd.setPriceQtr(priceQ);
      }
    }

    return cfd;
  }

  /**
   * Reads the data from SIP tab delimited files and fills data structures.
   *
   * @param filename Name of SIP file to parse
   */
  public static void readSipData(String filename) {

    final List<String> data = TextUtils.readTextFile(filename, true);

    for (final String c : data) {

      CompanyFileData.fld = c.replace("\"", "").split(Utils.TAB);

      final CompanyFileData cfd = new CompanyFileData();

      cfd.ticker = CompanyFileData.fld[CompanyFileData.TICKER].trim();
      cfd.name = CompanyFileData.fld[CompanyFileData.NAME].trim();
      cfd.exchange = FieldData.convertExchange(CompanyFileData.fld[CompanyFileData.EXCHANGE].trim());
      cfd.sector = CompanyFileData.fld[CompanyFileData.SECTOR].trim();
      cfd.industry = CompanyFileData.fld[CompanyFileData.INDUSTRY].trim();
      cfd.sic = CompanyFileData.fld[CompanyFileData.SIC].trim();

      final String tmp = CompanyFileData.fld[CompanyFileData.SNP].trim();
      if (tmp.equals("500")) {
        cfd.snpIndex = SnpEnum.SP500;
      }
      else if (tmp.equals("SmallCap 600")) {
        cfd.snpIndex = SnpEnum.SP600;
      }
      else if (tmp.equals("MidCap 400")) {
        cfd.snpIndex = SnpEnum.SP400;
      }
      else {
        cfd.snpIndex = SnpEnum.NONE;
      }

      try {
        cfd.dowIndex = DowEnum.valueOf(CompanyFileData.fld[CompanyFileData.DOW].trim().toUpperCase());
      }
      catch (final Exception e) {
        cfd.dowIndex = DowEnum.NONE;
      }

      final String tmpAdr = CompanyFileData.fld[CompanyFileData.ADR].trim();
      if (tmpAdr.trim().toUpperCase().equals("T")) {
        cfd.adr = true;
      }
      else {
        cfd.adr = false;
      }

      cfd.street = CompanyFileData.fld[CompanyFileData.STREET].trim();
      cfd.city = CompanyFileData.fld[CompanyFileData.CITY].trim();
      cfd.state = CompanyFileData.fld[CompanyFileData.STATE].trim();
      cfd.country = CompanyFileData.fld[CompanyFileData.COUNTRY].trim();
      cfd.zip = CompanyFileData.fld[CompanyFileData.ZIP].trim();
      cfd.phone = CompanyFileData.fld[CompanyFileData.PHONE].trim();
      cfd.web = CompanyFileData.fld[CompanyFileData.WEB].trim();
      cfd.numEmployees = SipUtils.parseInt(CompanyFileData.fld[CompanyFileData.EMP].trim());
      cfd.priceQtr = SipUtils.parseDoubles(CompanyFileData.fld, CompanyFileData.PRICE, 8);

      CompanyFileData.cfdList.add(cfd);

      /**
       * These important tickers are not in SIP data. Use a copy to add to this DB.
       * 
       * GOOGL exists but GOOG does not.
       * 
       * BRK.A exists but BRK.B does not.
       * 
       */
      if (cfd.ticker.equals("GOOGL")) {
        CompanyFileData googCfd = new CompanyFileData(cfd);
        googCfd.ticker = "GOOG";
        CompanyFileData.cfdList.add(googCfd);
      }
      else if (cfd.ticker.equals("BRK.A")) {
        CompanyFileData brkbCfd = new CompanyFileData(cfd);
        brkbCfd.ticker = "BRK.B";
        CompanyFileData.cfdList.add(brkbCfd);
      }
    }
  }

  private boolean  adr;
  private String   city;
  private String   country;
  private DowEnum  dowIndex;
  private ExchEnum exchange;
  private String   industry;
  private String   name;
  private int      numEmployees;
  private String   phone;
  private double[] priceQtr;
  private String   sector;
  private String   sic;
  private SnpEnum  snpIndex;
  private String   state;
  private String   street;
  private String   ticker;
  private String   web;

  private String zip;

  /**
   * Constructor
   */
  public CompanyFileData() {
    this.ticker = "";
  }

  /**
   * Copy Constructor
   *
   * @param cfd CompanyFileData to copy
   */
  public CompanyFileData(CompanyFileData cfd) {
    if (cfd != null) {
      this.adr = cfd.adr;
      this.city = cfd.city;
      this.country = cfd.country;
      this.dowIndex = cfd.dowIndex;
      this.exchange = cfd.exchange;
      this.industry = cfd.industry;
      this.name = cfd.name;
      this.numEmployees = cfd.numEmployees;
      this.phone = cfd.phone;
      this.sector = cfd.sector;
      this.sic = cfd.sic;
      this.snpIndex = cfd.snpIndex;
      this.state = cfd.state;
      this.street = cfd.street;
      this.ticker = cfd.ticker;
      this.web = cfd.web;
      this.zip = cfd.zip;
      this.priceQtr = cfd.priceQtr;
    }
    else {
      this.ticker = "";
    }
  }

  public String getCity() {
    return this.city;
  }

  public String getCountry() {
    return this.country;
  }

  public DowEnum getDowIndex() {
    return this.dowIndex;
  }

  /**
   * Returns capitalized string of DowEnum
   *
   * @return String
   */
  public String getDowIndexStr() {
    String ret = "";
    try {
      ret = this.dowIndex.toString().toUpperCase();
    }
    catch (final Exception e) {
      ret = "";
    }
    return ret;
  }

  public ExchEnum getExchange() {
    return this.exchange;
  }

  /**
   * Returns capitalized string of ExchEnum
   *
   * @return String
   */
  public String getExchangeStr() {
    String ret = "";
    try {
      ret = this.exchange.toString().toUpperCase();
    }
    catch (final Exception e) {
      FieldData.getWarning(e);
      ret = "NONE";
    }
    return ret;
  }

  public String getIndustry() {
    return this.industry;
  }

  public String getName() {
    return this.name;
  }

  public int getNumEmployees() {
    return this.numEmployees;
  }

  public String getPhone() {
    return this.phone;
  }

  public double[] getPriceQtr() {
    return this.priceQtr;
  }

  public String getSector() {
    return this.sector;
  }

  public String getSic() {
    return this.sic;
  }

  public SnpEnum getSnpIndex() {
    return this.snpIndex;
  }

  /**
   * Returns capitalized string of SnpEnum
   *
   * @return String
   */
  public String getSnpIndexStr() {
    String ret = "";
    try {
      ret = this.snpIndex.toString().toUpperCase();
    }
    catch (final Exception e) {
      ret = "";
    }
    return ret;
  }

  public String getState() {
    return this.state;
  }

  public String getStreet() {
    return this.street;
  }

  public String getTicker() {
    return this.ticker;
  }

  public String getWeb() {
    return this.web;
  }

  public String getZip() {
    return this.zip;
  }

  public boolean isAdr() {
    return this.adr;
  }

  /**
   * Parses data and fills data structures from DB files.
   *
   * @param data List of string to parse and set values
   * @return List of String
   */
  public List<String> set(List<String> data) {

    final List<String> notFound = new ArrayList<>();

    for (final String s : data) {

      final String[] tfld = s.split(":");

      final String fld = tfld[0].trim();

      String val = "";
      String val2 = "";
      if (tfld.length > 2) {
        val = "http:";
        val2 = tfld[2].trim();
      }
      else if (tfld.length > 1) {
        val = tfld[1].trim();
      }

      if (fld.equals("ticker")) {
        this.ticker = val.toUpperCase();
      }
      else if (fld.equals("name")) {
        this.name = val;
      }
      else if (fld.equals("exchange")) {
        this.exchange = ExchEnum.valueOf(val);
      }
      else if (fld.equals("sector")) {
        this.sector = val;
      }
      else if (fld.equals("industry")) {
        this.industry = val;
      }
      else if (fld.equals("sic")) {
        this.sic = val;
      }
      else if (fld.equals("employees")) {
        this.numEmployees = SipUtils.parseInt(val);
      }
      else if (fld.equals("snp index")) {
        this.snpIndex = SnpEnum.valueOf(val);
      }
      else if (fld.equals("dow index")) {
        this.dowIndex = DowEnum.valueOf(val);
      }
      else if (fld.equals("adr")) {
        this.adr = SipUtils.parseBoolean(val);
      }
      else if (fld.equals("street")) {
        this.street = val;
      }
      else if (fld.equals("city")) {
        this.city = val;
      }
      else if (fld.equals("state")) {
        this.state = val;
      }
      else if (fld.equals("country")) {
        this.country = val;
      }
      else if (fld.equals("zip")) {
        this.zip = val;
      }
      else if (fld.equals("phone")) {
        this.phone = val;
      }
      else if (fld.equals("web")) {
        this.web = val + val2;
      }
      else if (fld.equals("prices")) {
        this.priceQtr = SipUtils.parseArrayDoubles(tfld[1], 1);
      }
      else if (fld.contains("Data for ")) {
      }
      else {
        notFound.add(fld);
        // System.out.printf("Unknown tag '%s' in Company File Data%n", fld);
      }
    }
    return notFound;
  }

  public void setAdr(String adr) {
    this.adr = SipUtils.parseBoolean(adr);
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setDowIndex(String dowIdx) {
    try {
      this.dowIndex = DowEnum.valueOf(dowIdx.toUpperCase());
    }
    catch (final Exception e) {
      this.dowIndex = DowEnum.NONE;
    }
  }

  public void setExchange(String exch) {

    this.exchange = ExchEnum.valueOf(exch);
  }

  public void setIndustry(String industry) {
    this.industry = industry;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNumEmployees(String numEmployees) {
    this.numEmployees = SipUtils.parseInt(numEmployees);
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setSector(String sector) {
    this.sector = sector;
  }

  public void setSic(String sic) {
    this.sic = sic;
  }

  public void setSnpIndex(String strIdx) {
    try {
      this.snpIndex = SnpEnum.valueOf(strIdx.toUpperCase());
    }
    catch (final Exception e) {
      this.snpIndex = SnpEnum.NONE;
    }
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public void setWeb(String web) {
    this.web = web;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  /**
   * Creates string of formatted data structures.
   *
   * @return String
   */
  public String toDbOuput() {
    String ret = "";
    try {
      ret += String.format("ticker      : %s%n", this.ticker);
      ret += String.format("  name      : %s%n", this.name);
      ret += String.format("  exchange  : %s%n", this.exchange);
      ret += String.format("  sector    : %s%n", this.sector);
      ret += String.format("  industry  : %s%n", this.industry);
      ret += String.format("  sic       : %s%n", this.getSic());
      ret += String.format("  employees : %d%n", this.getNumEmployees());
      ret += String.format("  snp index : %s%n", this.getSnpIndexStr());
      ret += String.format("  dow index : %s%n", this.getDowIndexStr());
      ret += String.format("  adr       : %s%n", this.adr);
      ret += String.format("  street    : %s%n", this.street);
      ret += String.format("  city      : %s%n", this.city);
      ret += String.format("  state     : %s%n", this.state);
      ret += String.format("  country   : %s%n", this.country);
      ret += String.format("  zip       : %s%n", this.zip);
      ret += String.format("  phone     : %s%n", this.phone);
      ret += String.format("  web       : %s%n", this.getWeb());
      ret += String.format("  prices    : %s%n", SipOutput.buildArray("", this.priceQtr, 10, 4, 1));
    }
    catch (final Exception e) {
      FieldData.getWarning(e);
      ret = "";
    }
    return ret;
  }

  @Override
  public String toString() {
    String ret = "";
    try {
      ret = SipOutput.SipHeader(this.ticker, this.name, this.getExchangeStr(), this.sector, this.industry);
      ret += String.format("  SIC     : %s%n", this.getSic());
      ret += String.format("  Index   : %-10s\t%-12s\t%s%n", this.getSnpIndexStr(), this.getDowIndexStr(), this.isAdr());
      ret += String.format("  Num Emp : %d%n", this.getNumEmployees());
      ret += String.format("  Address : %s\t%s\t%s\t%s\t%s\t%s%n", this.getStreet(), this.getCity(), this.getState(), this.getCountry(),
          this.getZip(), this.getPhone());
      ret += String.format("  Web     : %s%n", this.getWeb());
      ret += String.format("  prices    : %s%n", SipOutput.buildArray("", this.priceQtr, 10, 4, 1));
    }
    catch (final Exception e) {
      ret = "";
    }
    return ret;
  }

  private void setPriceQtr(double[] priceQ) {
    this.priceQtr = priceQ;
  }

}
