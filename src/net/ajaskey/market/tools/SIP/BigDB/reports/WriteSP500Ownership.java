package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;
import net.ajaskey.market.tools.SIP.BigDB.collation.CompanySummary;
import net.ajaskey.market.tools.SIP.BigDB.collation.YearQuarter;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class WriteSP500Ownership {

  private static List<YearQuarter> yqList = new ArrayList<>();

  final static FiletypeEnum ft = FiletypeEnum.BIG_BINARY;

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    int year = 2022;
    int qtr = 3;

    try (PrintWriter pw = new PrintWriter("sipout/ownership.txt")) {
      String dollars = getQuarter(year, qtr);
      String fld[] = dollars.split(",");
      pw.printf("%d:%d\t%s\t%s%n", year, qtr, fld[0], fld[1]);
      System.out.printf("%d:%d\t%s\t%s%n", year, qtr, fld[0], fld[1]);
//      for (int i = 2020; i > 2017; i--) {
//        for (int j = 4; j > 0; j--) {
//          dollars = getQuarter(i, j);
//          fld = dollars.split(",");
//          pw.printf("%d:%d\t%s\t%s%n", i, j, fld[0], fld[1]);
//        }
//      }
    }

    System.out.println("Done.");

  }

  private static String getQuarter(int yr, int qtr) {

    FieldData.setQMemory(yr, qtr, ft);

    final List<String> sp500 = CompanySummary.getSnp(SnpEnum.SP500, yr, qtr);

    final List<FieldData> fdList = FieldData.getQFromMemory(sp500, yr, qtr);

    double insidetotal = 0.0;
    double insttotal = 0.0;
    for (FieldData fd : fdList) {
      double aprice = (fd.getShareData().getPrice52hi() + fd.getShareData().getPrice52lo()) / 2.0;

      double insidebuy = fd.getShareData().getInsiderBuyShrs() * aprice;
      double insidesell = fd.getShareData().getInsiderSellShrs() * aprice;
      double insidedelta = insidebuy - insidesell;
      insidetotal += insidedelta;

      double instbuy = fd.getShareData().getInstBuyShrs() * aprice;
      double instsell = fd.getShareData().getInstSellShrs() * aprice;
      double instdelta = instbuy - instsell;
      insttotal += instdelta;

    }

    String ret = String.format("%f,%f", insidetotal, insttotal);
    return ret;

  }

}
