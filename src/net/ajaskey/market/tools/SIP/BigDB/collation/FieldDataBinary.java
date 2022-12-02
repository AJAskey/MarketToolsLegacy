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
package net.ajaskey.market.tools.SIP.BigDB.collation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.dataio.BalSheetFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.CashFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.CompanyFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.EstimateFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.IncSheetFileData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.SharesFileData;

public class FieldDataBinary implements Serializable {

  public static final long serialVersionUID = 2600255321596652398L;

  /**
   * Internal storage for all data records to be written
   */
  private static List<FieldDataBinary> bigList = new ArrayList<>();

  /**
   * Adds an entry to <b>bigList</b>
   *
   * @param cfd  CompanyFileData
   * @param efd  EstimateFileData
   * @param sfd  SharesFileData
   * @param ifd  IncSheetFileData
   * @param bfd  BalSheetFileData
   * @param cash CashFileData
   * @param yr   year
   * @param qtr  quarter
   */
  public static void add(CompanyFileData cfd, EstimateFileData efd, SharesFileData sfd, IncSheetFileData ifd, BalSheetFileData bfd, CashFileData cash,
      int yr, int qtr) {

    new FieldDataBinary(cfd, efd, sfd, ifd, bfd, cash, yr, qtr);
  }

  public static void clearList() {
    FieldDataBinary.bigList.clear();
  }

  /**
   * Reads the big binary file containing ome quarter of data
   *
   * @param fname File name to read
   * @return List of FieldData
   */
  public static List<FieldData> readBinaryFile(String fname) {

    final List<FieldData> fdList = new ArrayList<>();

    FieldDataBinaryFile fdbf = null;
    try {

      final File f = new File(fname);

      if (f.exists()) {
        final ObjectInputStream objBinFile = new ObjectInputStream(new FileInputStream(fname));

        fdbf = (FieldDataBinaryFile) objBinFile.readObject();
        objBinFile.close();
      }
    }
    catch (final Exception e) {
      fdbf = null;
      e.printStackTrace();
    }

    if (fdbf != null) {
      for (final FieldDataBinary f : fdbf.bigList) {
        final FieldData fd = new FieldData(f.companyInfo, f.estimateData, f.shareData, f.incSheetData, f.balSheetData, f.cashData, f.year, f.quarter);
        fdList.add(fd);
      }
    }

    return fdList;
  }

  /**
   *
   * Writes the big binary file containing one quarter of data
   *
   * @param yr  year
   * @param qtr quarter
   */
  public static void writeBinaryFile(int yr, int qtr) {

    final FieldDataBinaryFile fdbf = new FieldDataBinaryFile(yr, qtr, FieldDataBinary.bigList);

    try {
      final String fname = String.format("%s%s/all-companies-%dQ%d.bin", FieldData.outbasedir, yr, yr, qtr);
      final ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(fname));
      o.writeObject(fdbf);
      o.close();
    }
    catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public BalSheetFileData balSheetData;
  public CashFileData     cashData;
  public CompanyFileData  companyInfo;
  public EstimateFileData estimateData;
  public ExchEnum         exchange;
  public IncSheetFileData incSheetData;
  public String           industry;
  public String           name;
  public int              quarter;
  public String           sector;
  public SharesFileData   shareData;
  public String           ticker;
  public boolean          valid;
  public int              year;

  /**
   * Constructor
   *
   * @param cfd  CompanyFileData
   * @param efd  EstimateFileData
   * @param sfd  SharesFileData
   * @param ifd  IncSheetFileData
   * @param bfd  BalSheetFileData
   * @param cash CashFileData
   * @param yr   year
   * @param qtr  quarter
   */

  private FieldDataBinary(CompanyFileData cfd, EstimateFileData efd, SharesFileData sfd, IncSheetFileData ifd, BalSheetFileData bfd,
      CashFileData cashfd, int yr, int qtr) {

    if (cfd == null || efd == null || sfd == null || ifd == null || bfd == null) {
      this.companyInfo = new CompanyFileData();
      this.valid = false;
    }
    else if (cfd.getTicker().trim().length() < 1) {
      this.companyInfo = new CompanyFileData();
      this.valid = false;
    }
    else {
      this.year = yr;
      this.quarter = qtr;
      this.balSheetData = new BalSheetFileData(bfd);
      this.cashData = new CashFileData(cashfd);
      this.companyInfo = new CompanyFileData(cfd);
      this.estimateData = new EstimateFileData(efd);
      this.exchange = cfd.getExchange();
      this.incSheetData = new IncSheetFileData(ifd);
      this.industry = cfd.getIndustry();
      this.name = cfd.getName();
      this.sector = cfd.getSector();
      this.shareData = new SharesFileData(sfd);
      this.valid = true;
    }

    if (this.valid) {
      FieldDataBinary.bigList.add(this);
    }
  }

  /**
   * @return Reference to <b>bigList</b>
   */
  public List<FieldDataBinary> getBigList() {
    return FieldDataBinary.bigList;
  }

}
