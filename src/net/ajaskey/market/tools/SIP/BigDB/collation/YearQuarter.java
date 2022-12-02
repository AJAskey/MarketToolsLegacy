package net.ajaskey.market.tools.SIP.BigDB.collation;

public class YearQuarter {

  private int year;
  private int quarter;

  public YearQuarter(int yr, int qtr) {
    this.year = yr;
    this.quarter = qtr;
  }

  public int getYear() {
    return year;
  }

  public int getQuarter() {
    return quarter;
  }

}
