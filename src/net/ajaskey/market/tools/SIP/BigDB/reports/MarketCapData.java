package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.market.tools.SIP.BigDB.SipStatistics;

public class MarketCapData {

  public static List<MarketCapData> list = new ArrayList<>();

  static SipStatistics[] mcStats;

  /*************************************************
   * 
   * @param args
   */
  public static void main(String[] args) {

    init();

    System.out.println(MarketCapData.list.size());

    for (final MarketCapData d : MarketCapData.list) {
      System.out.println(d.ticker);
    }

    final MarketCapData tst = MarketCapData.findTicker("ZG");
    if (tst != null) {
      System.out.printf("found %s%n", tst);
    }

    for (int i = 0; i < mcStats.length; i++) {
      System.out.println(mcStats[i]);
    }

  }

  /**
   * 
   */
  public static void init() {

    mcStats = new SipStatistics[6];
    mcStats[0] = new SipStatistics("Market Cap TTM");
    mcStats[1] = new SipStatistics("Market Cap Y1");
    mcStats[2] = new SipStatistics("Market Cap Y2");
    mcStats[3] = new SipStatistics("Market Cap Y3");
    mcStats[4] = new SipStatistics("Market Cap Y4");
    mcStats[5] = new SipStatistics("Market Cap Y5");

    final List<String> mcd = TextUtils.readTextFile("data/MARKETCAPDATA.TXT", true);

    for (final String s : mcd) {
      final MarketCapData d = new MarketCapData(s.replace("\"", "").trim());
      if (d.valid) {
        MarketCapData.list.add(d);
        for (int i = 0; i < mcStats.length; i++) {
          mcStats[i].addValue(d.mcap[i]);
        }
      }
    }
  }

  /**
   *
   * @param tkr
   * @return
   */
  private static MarketCapData findTicker(String tkr) {
    for (final MarketCapData m : MarketCapData.list) {
      if (m.ticker.equalsIgnoreCase(tkr)) {
        if (m.valid) {
          return m;
        }
        return null;
      }
    }
    return null;
  }

  public String   ticker;
  public String   sector;
  public String   industry;
  public double[] mcap;
  public boolean  valid;

  /**
   * 
   * @param s
   */
  public MarketCapData(String s) {
    this.mcap = new double[6];
    this.valid = false;
    final String fld[] = s.split("\t");
    this.ticker = fld[0].trim();
    this.sector = fld[1].trim();
    this.industry = fld[2].trim();
    double tmp = Double.parseDouble(fld[3].trim());
    if (tmp > 0.0) {
      this.mcap[0] = tmp;
      tmp = Double.parseDouble(fld[4].trim());
      if (tmp > 0.0) {
        this.mcap[1] = tmp;
        tmp = Double.parseDouble(fld[5].trim());
        if (tmp > 0.0) {
          this.mcap[2] = tmp;
          tmp = Double.parseDouble(fld[6].trim());
          if (tmp > 0.0) {
            this.mcap[3] = tmp;
            tmp = Double.parseDouble(fld[7].trim());
            if (tmp > 0.0) {
              this.mcap[4] = tmp;
              tmp = Double.parseDouble(fld[8].trim());
              if (tmp > 0.0) {
                this.mcap[5] = tmp;
                this.valid = true;
              }
            }
          }
        }
      }
    }
  }

  @Override
  public String toString() {
    String ret = String.format("%7s\t%25s\t%25s", this.ticker, this.sector, this.industry);
    for (int i = 0; i < this.mcap.length; i++) {
      ret += String.format("\t%f", this.mcap[i]);
    }
    return ret;
  }

}
