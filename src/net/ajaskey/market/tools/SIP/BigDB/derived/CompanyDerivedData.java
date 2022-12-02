package net.ajaskey.market.tools.SIP.BigDB.derived;

import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.Utils;

public class CompanyDerivedData {

  public List<CompanyDerived> dList;

  public int quarter;
  public int year;

  public CompanyDerivedData(int yr, int qtr) {
    this.year = yr;
    this.quarter = qtr;
    this.dList = new ArrayList<>();
  }

  @Override
  public String toString() {
    String ret = String.format("%dQ%d%n", this.year, this.quarter);
    for (final CompanyDerived cd : this.dList) {
      ret += " " + cd.getFd().getTicker() + Utils.NL;
    }
    return ret;
  }

}
