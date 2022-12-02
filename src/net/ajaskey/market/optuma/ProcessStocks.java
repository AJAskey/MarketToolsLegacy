package net.ajaskey.market.optuma;

import net.ajaskey.common.DateTime;

public class ProcessStocks {

  public static void main(String[] args) {
    final PriceDerived code = new PriceDerived("US", "aapl");

    // System.out.println(code);
    System.out.println("Latest      : " + code.getLatest());
    System.out.println("Latest Date : " + code.getLatestDate());
    final DateTime dt = new DateTime(2019, DateTime.MARCH, 15);
    final double pd = code.getClose(dt);
    System.out.println(dt + "\t" + pd);
    PriceData tpd = code.getOffset(125);
    System.out.println(tpd);
    tpd = code.getOffset(7500);
    System.out.println(tpd);

  }

}
