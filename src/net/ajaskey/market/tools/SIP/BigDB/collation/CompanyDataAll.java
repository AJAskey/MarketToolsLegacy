package net.ajaskey.market.tools.SIP.BigDB.collation;

import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.Globals;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;

public class CompanyDataAll {

  /**
   * 
   * @param list
   * @param yr
   * @param qtr
   * @return
   */
  public static FieldData get(List<CompanyDataAll> list, int yr, int qtr) {
    for (final CompanyDataAll cda : list) {
      if (cda.year == yr) {
        if (qtr == 1) {
          return cda.q1;
        }
        else if (qtr == 2) {
          return cda.q2;
        }
        else if (qtr == 3) {
          return cda.q3;
        }
        else if (qtr == 4) {
          return cda.q4;
        }
      }
    }
    return null;
  }

  /**
   * 
   * @param ticker
   * @return
   */
  public static List<CompanyDataAll> get(String ticker) {
    final List<CompanyDataAll> retList = new ArrayList<>();

    for (int yr = Globals.startYear; yr <= Globals.endYear; yr++) {
      final CompanyDataAll cda = new CompanyDataAll(ticker);
      cda.q1 = Globals.getQFromMemory(ticker, yr, 1);
      cda.q2 = Globals.getQFromMemory(ticker, yr, 2);
      cda.q3 = Globals.getQFromMemory(ticker, yr, 3);
      cda.q4 = Globals.getQFromMemory(ticker, yr, 4);
      cda.year = yr;
      if (cda.q1 != null || cda.q2 != null || cda.q3 != null || cda.q4 != null) {
        retList.add(cda);
      }
    }

    return retList;
  }

  public boolean checkQ(int q) {
    boolean ret = false;

    if (q == 1) {
      if (this.q1 != null) {
        ret = true;
      }
    }
    else if (q == 2) {
      if (this.q2 != null) {
        ret = true;
      }
    }
    else if (q == 3) {
      if (this.q3 != null) {
        ret = true;
      }
    }
    else if (q == 4) {
      if (this.q2 != null) {
        ret = true;
      }
    }

    return ret;
  }

  public FieldData getQ1() {
    return q1;
  }

  public FieldData getQ2() {
    return q2;
  }

  public FieldData getQ3() {
    return q3;
  }

  public FieldData getQ4() {
    return q4;
  }

  public String getTicker() {
    return ticker;
  }

  public int getYear() {
    return year;
  }

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {

    final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;

    CompanyDerived.loadDb(2020, 1, ft);
    CompanyDerived.loadDb(2020, 2, ft);
    CompanyDerived.loadDb(2020, 3, ft);

    final List<CompanyDataAll> cdaList = CompanyDataAll.get("MSFT");
    for (final CompanyDataAll cda : cdaList) {
      System.out.println(cda);
    }
  }

  private FieldData    q1;
  private FieldData    q2;
  private FieldData    q3;
  private FieldData    q4;
  private final String ticker;
  private int          year;

  public CompanyDataAll(String ticker) {
    this.ticker = ticker;
  }

  @Override
  public String toString() {
    String ret = String.format("%s\t%d%n", this.ticker, this.year);
    if (this.q1 != null) {
      ret += String.format("%s%n", this.q1);
    }
    if (this.q2 != null) {
      ret += String.format("%s%n", this.q2);
    }
    if (this.q3 != null) {
      ret += String.format("%s%n", this.q3);
    }
    if (this.q4 != null) {
      ret += String.format("%s%n", this.q4);
    }
    return ret;
  }

}
