package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.dataio.Options;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;

public class WriteUsefulStocks {

  final static String listDir = "D:/dev/MarketTools - dev/lists/";

  static PrintWriter bigListPw = null;

  public static void main(String[] args) {

    System.out.println("Writing Useful Stocks List...");

    final int year = 2022;
    final int quarter = 3;

    final List<CompanyDerived> cdrList = new ArrayList<>();

    final List<CompanyDerived> dRList = Scans.findMajor(year, quarter, 30.0, 500000L);

    Options.readOptionData(year, quarter);
    for (final CompanyDerived cdr : dRList) {
      if (Options.isOptionable(cdr.getTicker())) {
        double mc = cdr.getMarketCapQdata().getMostRecent();
        if (cdr.getMarketCapQdata().getMostRecent() > 1000.0) {
          cdrList.add(cdr);
        }
      }
    }

    try (PrintWriter bigListPw = new PrintWriter(listDir + "useful_stocks.csv")) {
      bigListPw.println("Code,Exchange");
      for (final CompanyDerived cdr : cdrList) {
        String str = String.format("%S,US", cdr.getTicker());
        bigListPw.printf("%s%n", str);
      }
    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println("Done.");
  }
}
