package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Utilities;

public class WeightedEarnings {

  static Double[] wtEarningsTotal = new Double[9];

  public static void main(String[] args) throws FileNotFoundException {

    System.out.println("WeightedEarnings...");

    for (int i = 0; i < wtEarningsTotal.length; i++) {
      wtEarningsTotal[i] = 0.0;
    }

    int year = 2020;
    int qtr = 3;

    List<CompanyDerived> drList = Scans.findSpx(year, qtr);

    List<CompanyDerived> cdrList = new ArrayList<>();
    for (CompanyDerived cdr : drList) {
      FieldData fd = cdr.getFd();
      if (fd.getTicker().equalsIgnoreCase("GOOG") || fd.getTicker().equalsIgnoreCase("BRK.A") || fd.getTicker().equalsIgnoreCase("BRK.B")) {
        continue;
      }
      cdrList.add(cdr);
    }

    Double[] sharesTotal = CompanyDerived.getTotalShares(cdrList);
    Double[] marketcapTotal = CompanyDerived.getTotalMarketCap(cdrList);

    try (PrintWriter pw = new PrintWriter("sipout/SpxWtEarnings.csv")) {

      pw.printf("Ticker,Sector,Industry,");
      pw.printf(",%s", getHeader(0));
      pw.printf(",%s", getHeader(1));
      pw.printf(",%s", getHeader(2));
      pw.printf(",%s", getHeader(3));
      pw.printf(",%s", getHeader(4));
      pw.printf(",%s", getHeader(5));
      pw.printf(",%s", getHeader(6));
      pw.printf(",%s", getHeader(7));
      pw.printf(",%s", getHeader(8));
      pw.printf("%n");

      for (CompanyDerived cdr : cdrList) {

        FieldData fd = cdr.getFd();

        pw.printf("%s,", fd.getTicker());
        pw.printf("%s,", Utilities.cleanSecInd(fd.getSector()));
        pw.printf("%s,,", Utilities.cleanSecInd(fd.getIndustry()));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 0));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 1));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 2));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 3));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 4));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 5));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 6));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 7));
        pw.printf("%s,", getData(cdr, sharesTotal, marketcapTotal, 8));

        pw.printf("%n");
      }

      pw.printf(",,");
      for (int i = 0; i < wtEarningsTotal.length; i++) {
        pw.printf(",,,,,%f", wtEarningsTotal[i]);
      }
    }

    System.out.println("Done.");

  }

  private static String getHeader(int i) {
    // String ret = String.format("Shares Q%d,sRatio Q%d, MCap Q%d, mcRatio Q%d,
    // Earn Q%d, WtEarn Q%d,", i, i, i, i, i, i);
    String ret = String.format("sRatio Q%d, mcRatio Q%d, Earn Q%d, WtEarn Q%d,", i, i, i, i, i);
    return ret;
  }

  private static String getData(CompanyDerived cdr, Double[] sharesTotal, Double[] marketcapTotal, int ii) {

    String ret = ",,,,,,";

    int j = ii;
    if (ii == 0) {
      j = 1;
    }

    if (sharesTotal[j] > 0.0 && marketcapTotal[j] > 0.0) {

      double shareRatio = 1.0;
      if (sharesTotal[j] > 0.0) {
        shareRatio = cdr.getSharesQdata().get(j) / sharesTotal[j];
      }
      double mcRatio = 1.0;
      if (marketcapTotal[j] > 0.0) {
        mcRatio = cdr.getMarketCapQdata().get(j) / marketcapTotal[j];
      }
      double eps = 0.0;
      if (ii > 0) {
        if (cdr.getSharesQdata().get(j) > 0.0) {
          eps = cdr.getIncPrimaryEpsQdata().get(j) / cdr.getSharesQdata().get(j);
        }
      }
      else {
        eps = cdr.getEpsEstQ0();
      }
      double wtEarn = eps * mcRatio;

      wtEarningsTotal[ii] += wtEarn;

//      ret = String.format("%f,%f,%f,%f,%f,%f,", cdr.getSharesQdata().get(i), shareRatio, cdr.getMarketCapQdata().get(i), mcRatio,
//          cdr.getIncPrimaryEpsQdata().get(i), wtEarn);
      ret = String.format("%f,%f,%f,%f,", shareRatio, mcRatio, eps, wtEarn);
    }
    return ret;
  }

}
