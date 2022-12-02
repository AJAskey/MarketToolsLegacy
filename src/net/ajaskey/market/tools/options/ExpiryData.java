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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.DateTime;

public class ExpiryData {

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    try {
      AddCboeDataFiles.main(null);
    }
    catch (final IOException e) {
    }

    final List<ExpiryDataList> l = new ArrayList<>();

    final String code = "spy";
    final int activeType = OptionsProcessor.APUT;
    double ulMove = 0.15;

    if (activeType == OptionsProcessor.ACALL) {
      ulMove *= -1.0;
    }

    final DateTime buyDate = new DateTime();
    buyDate.add(DateTime.DATE, 1);

    final DateTime firstExpiry = new DateTime(buyDate);
    firstExpiry.add(DateTime.DATE, 10);

    final String fname = String.format("data/options/%s-options.dat", code);
    final List<CboeOptionData> dil = CallPutList.readCboeData(fname, firstExpiry, buyDate, 50);

    for (final CboeOptionData cod : dil) {
      boolean found = false;
      for (final ExpiryDataList ex : l) {
        if (cod.expiry.isEqual(ex.expiry)) {
          ExpiryData e = null;
          if (activeType == OptionsProcessor.APUT) {
            e = new ExpiryData(cod.put, ex.expiry, OptionsProcessor.APUT);
          }
          else {
            e = new ExpiryData(cod.call, ex.expiry, OptionsProcessor.ACALL);
          }
          ex.exList.add(e);
          found = true;
          break;
        }
      }
      if (!found) {
        final ExpiryDataList etmp = new ExpiryDataList(cod.expiry, CallPutList.getcPrice());
        ExpiryData e = null;
        if (activeType == OptionsProcessor.APUT) {
          e = new ExpiryData(cod.put, etmp.expiry, OptionsProcessor.APUT);
        }
        else {
          e = new ExpiryData(cod.call, etmp.expiry, OptionsProcessor.ACALL);
        }
        etmp.exList.add(e);
        l.add(etmp);
      }
    }

    final double tmpUl = CallPutList.getcPrice() * (1.0 + Math.abs(ulMove));
    try (PrintWriter pw = new PrintWriter("out/options/ExpiryData.out")) {

      for (final ExpiryDataList ex : l) {

        Collections.sort(ex.exList, new SortByStrike());

        final int ulPos = ExpiryData.getCallPos(ex.ul, ex.exList);
        double newUl = 0.0;
        if (activeType == OptionsProcessor.APUT) {
          newUl = ex.ul * (1.0 - ulMove);
        }
        else {
          newUl = ex.ul * (1.0 + ulMove);
        }

        pw.printf("%n%s\t%.2f\t%.2f\t%d\t%.2f\t\t%.2f%%%n", ex.expiry, ex.ul, tmpUl, ulPos, newUl, Math.abs(ulMove * 100.0));

        double lastPrice = 0.0;
        double buy = 0.0;
        double sell = 0.0;
        for (int i = 0; i < ex.exList.size(); i++) {

          final ExpiryData ed = ex.exList.get(i);
          if (lastPrice > 0.0) {
          }

          double posUl = 0.0;
          int pos = -1;
          if (activeType == OptionsProcessor.ACALL) {
            posUl = ed.strike * (1.0 + ulMove);
            pos = ExpiryData.getCallPos(posUl, ex.exList);
          }
          else {
            posUl = ed.strike * (1.0 - ulMove);
            pos = ExpiryData.getPutPos(posUl, ex.exList);
          }

          double profit = 0.0;
          if (pos >= 0) {

            if (ed.mark > 0.0499 && ex.exList.get(pos).mark > 0.0499) {
              if (activeType == OptionsProcessor.ACALL) {
                buy = ed.mark;
                sell = ex.exList.get(pos).mark;
              }
              else {
                buy = ex.exList.get(pos).mark;
                sell = ed.mark;
              }
              profit = (sell - buy) / buy * 100.0;
            }
          }

          pw.printf("\t%3d %15s %6.1f %7.2f %7.2f %5d %7.2f %7.2f %10.1f%% %10d%n", i, ed.id, ed.strike, posUl, ed.mark, pos, buy, sell, profit,
              ed.vol);

          lastPrice = ed.mark;

        }
        // break;
      }
    }
  }

  private static int getCallPos(double ul, List<ExpiryData> ex) {
    int ret = -1;

    for (int i = 0; i < ex.size(); i++) {
      if (ex.get(i).strike >= ul) {
        ret = i;
        break;
      }
    }

    return ret;
  }

  private static int getPosOffset(double price, double offset, List<ExpiryData> ex) {
    int ret = -1;
    final double prOff = price + offset;

    for (int i = 0; i < ex.size(); i++) {
      if (ex.get(i).strike >= prOff) {
        ret = i;
        break;
      }
    }

    return ret;
  }

  private static int getPutPos(double ul, List<ExpiryData> ex) {
    int ret = -1;

    for (int i = ex.size() - 1; i >= 0; i--) {
      if (ex.get(i).strike <= ul) {
        ret = i;
        break;
      }
    }

    return ret;
  }

  private double   ask;
  private double   bid;
  private double   delta;
  private DateTime expiry;
  private double   gamma;
  private String   id;
  private double   iv;
  private double   last;
  private double   mark;
  private double   net;

  // private List<ExpiryData> list = null;

  private int oi;

  private double strike;

  private int type;

  private int vol;

  public ExpiryData() {
    // TODO Auto-generated constructor stub
  }

  /**
   *
   * @param in
   * @param exIn
   * @param inType
   */
  public ExpiryData(CboeCallPutData in, DateTime exIn, int inType) {
    this.setExpiry(exIn);
    this.setType(inType);
    this.setId(in.id);
    this.setStrike(in.strike);
    this.setLast(in.last);
    this.setNet(in.net);
    this.setBid(in.bid);
    this.setAsk(in.ask);
    this.setMark(in.mark);
    this.setVol(in.vol);
    this.setOi(in.oi);
    this.setIv(in.iv);
    this.setDelta(in.delta);
    this.setGamma(in.gamma);
    // this.list = new ArrayList<>();
  }

  public double getAsk() {
    return this.ask;
  }

  public double getBid() {
    return this.bid;
  }

  public double getDelta() {
    return this.delta;
  }

  public DateTime getExpiry() {
    return this.expiry;
  }

  public double getGamma() {
    return this.gamma;
  }

  public String getId() {
    return this.id;
  }

  public double getIv() {
    return this.iv;
  }

  public double getLast() {
    return this.last;
  }

  public double getMark() {
    return this.mark;
  }

  public double getNet() {
    return this.net;
  }

  public int getOi() {
    return this.oi;
  }

  public double getStrike() {
    return this.strike;
  }

  public int getType() {
    return this.type;
  }

  public int getVol() {
    return this.vol;
  }

  public void setAsk(double ask) {
    this.ask = ask;
  }

  public void setBid(double bid) {
    this.bid = bid;
  }

  public void setDelta(double delta) {
    this.delta = delta;
  }

  public void setExpiry(DateTime expiry) {
    this.expiry = new DateTime(expiry);
  }

  public void setGamma(double gamma) {
    this.gamma = gamma;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setIv(double iv) {
    this.iv = iv;
  }

  public void setLast(double last) {
    this.last = last;
  }

  public void setMark(double mark) {
    this.mark = mark;
  }

  public void setNet(double net) {
    this.net = net;
  }

  public void setOi(int oi) {
    this.oi = oi;
  }

  public void setStrike(double strike) {
    this.strike = strike;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setVol(int vol) {
    this.vol = vol;
  }

  @Override
  public String toString() {
    String ret = "";

    ret += String.format("Id       : %s%n", this.id);
    ret += String.format(" Expiry  : %s%n", this.expiry);
    ret += String.format(" Strike  : %.2f%n", this.strike);
    ret += String.format(" Last    : %.2f%n", this.last);
    ret += String.format(" Bid     : %.2f%n", this.bid);
    ret += String.format(" Ask     : %.2f%n", this.ask);
    ret += String.format(" Mark    : %.2f%n", this.mark);
    ret += String.format(" Volume  : %d%n", this.vol);
    ret += String.format(" OI      : %d%n", this.oi);
    ret += String.format(" IV      : %.4f%n", this.iv);
    ret += String.format(" Delta   : %.4f%n", this.delta);
    ret += String.format(" Gamma   : %.4f%n", this.gamma);

    return ret;
  }
}
