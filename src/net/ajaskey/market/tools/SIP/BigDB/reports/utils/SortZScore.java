package net.ajaskey.market.tools.SIP.BigDB.reports.utils;

import java.util.Comparator;

import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;

public class SortZScore implements Comparator<CompanyDerived> {

  @Override
  public int compare(CompanyDerived cdr1, CompanyDerived cdr2) {
    int ret = 0;
    if (cdr1.getZdata().getzScore() < cdr2.getZdata().getzScore()) {
      ret = 1;
    }
    else if (cdr1.getZdata().getzScore() > cdr2.getZdata().getzScore()) {
      ret = -1;
    }

    return ret;
  }

}
