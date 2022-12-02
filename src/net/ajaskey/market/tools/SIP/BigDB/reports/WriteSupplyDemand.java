package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.dataio.Options;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;

public class WriteSupplyDemand {

  /**
   *
   * @param dList
   * @return
   */
  public static List<CompanyDerived> findSupplyDemand(List<CompanyDerived> dRList, int yr, int qtr) {

    Options.readOptionData(yr, qtr);

    final List<CompanyDerived> zList = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {

//      if (cdr.getFd().getTicker().equalsIgnoreCase("BHVN")) {
//        System.out.println("BVHN");
//      }

      double turnover = cdr.getTurnover();
      if (turnover < 15.0) {
        if (Options.isOptionable(cdr.getFd().getTicker())) {
          final int qtrs = cdr.getSalesQdata().getQuarterDataKnt();
          if (qtrs > 4) {
            zList.add(cdr);
          }
        }
      }
    }

    return zList;
  }

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    System.out.println("SuppyDemand...");

    int year = 2022;
    int quarter = 3;

    List<CompanyDerived> dRList = Scans.findMajor(year, quarter, 20.0, 500000L);

    final List<CompanyDerived> dList = WriteSupplyDemand.findSupplyDemand(dRList, year, quarter);

    try (PrintWriter pw = new PrintWriter("sipout/SupplyDemand.csv")) {

      for (final CompanyDerived cdr : dList) {
        pw.printf("%s,%n", cdr.getFd().getTicker());
      }
    }

    System.out.println("Done.");
  }

}
