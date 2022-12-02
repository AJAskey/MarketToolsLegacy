package net.ajaskey.market.tools.SIP.BigDB.reports.utils;

import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.MarketTools;

public class RefreshAllSip {

  public static void main(String[] args) {

    for (int year = 2018; year < 2021; year++) {
      for (int qtr = 1; qtr < 5; qtr++) {
        final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;
        MarketTools.parseSipData(year, qtr, ft, true);
      }
    }
    System.out.println("Done.");
  }
}
