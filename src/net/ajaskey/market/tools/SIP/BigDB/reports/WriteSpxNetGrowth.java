package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.MarketTools;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Utilities;

public class WriteSpxNetGrowth {

  public static void main(String[] args) throws FileNotFoundException {

    System.out.println("SPX Net Growth...");

    final int year = 2022;
    final int qtr = 3;

    final List<CompanyDerived> drList = Scans.findSpx(year, qtr);

    final List<CompanyDerived> cdrList = new ArrayList<>();
    for (final CompanyDerived cdr : drList) {
      if (cdr.getFd().getTicker().equalsIgnoreCase("GOOGL") || cdr.getFd().getTicker().equalsIgnoreCase("BRK.A")) {
        continue;
      }
      if (WriteSpxNetGrowth.has8qtrs(cdr)) {
        cdrList.add(cdr);
      }

    }

    final double[] epstot = new double[9];
    for (int i = 0; i < epstot.length; i++) {
      epstot[i] = 0.0;
    }

    try (PrintWriter pw = new PrintWriter("sipout/SPX-NetGrowth.csv")) {
      pw.println("Code,Name,Sector,Industry,EPS 0, EPS 1, EPS 2, EPS 3, EPS 4, EPS 5, EPS 6, EPS 7, EPS 8, Chg40, Chg51, Chg62, Chg73, Chg84");

      for (final CompanyDerived cdr : cdrList) {
        final FieldData fd = cdr.getFd();

        final double eps0scaled = WriteSpxNetGrowth.scale(fd.getTicker(), fd.getEstimateData().getEpsQ0());
        pw.printf("%s,%s,%s,%s,%f ", fd.getTicker(), Utilities.cleanForCsv(fd.getName()), Utilities.cleanSecInd(fd.getSector()),
            Utilities.cleanSecInd(fd.getIndustry()), eps0scaled);
        epstot[0] += eps0scaled;

        for (int i = 1; i < 9; i++) {
          final double eps = cdr.getNetIncQdata().get(i) / cdr.getSharesQdata().get(i);
          final double scaledEps = WriteSpxNetGrowth.scale(fd.getTicker(), eps);
          epstot[i] += scaledEps;
          pw.printf(",%f", scaledEps);
        }

        double epsPrev = cdr.getNetIncQdata().get(4) / cdr.getSharesQdata().get(4);
        double chg = MarketTools.getChange(fd.getEstimateData().getEpsQ0(), epsPrev);
        pw.printf(",%f", chg);

        for (int i = 1; i < 5; i++) {
          epsPrev = cdr.getNetIncQdata().get(i + 4) / cdr.getSharesQdata().get(i + 4);
          final double eps = cdr.getNetIncQdata().get(i) / cdr.getSharesQdata().get(i);
          chg = MarketTools.getChange(eps, epsPrev);
          pw.printf(",%f", chg);
        }

        pw.printf("%n");
      }

      // Bottom line
      pw.printf(",,,");
      for (final double element : epstot) {
        pw.printf(",%f", element);
      }

      for (int i = 0; i < 5; i++) {
        final double chg = MarketTools.getChange(epstot[i], epstot[i + 4]);
        pw.printf(",%f", chg);
      }

      pw.printf("%n");
    }

    System.out.println("Done.");
  }

  /**
   *
   * @param cdr
   * @return
   */
  private static boolean has8qtrs(CompanyDerived cdr) {
    for (int i = 1; i < 9; i++) {
      if (cdr.getSharesQdata().get(i) <= 0.0) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @param ticker
   * @param eps
   * @return
   */
  private static double scale(String ticker, double eps) {
    double scalar = 1.0;
    if (ticker.contains("BRK.")) {
      scalar = 1500.0;
    }
    else if (ticker.equalsIgnoreCase("AZO")) {
      scalar = 2.0;
    }
    else if (ticker.equalsIgnoreCase("BIO")) {
      scalar = 3.0;
    }
    else if (ticker.equalsIgnoreCase("NVR")) {
      scalar = 10.0;
    }
    else if (ticker.equalsIgnoreCase("BKNG")) {
      scalar = 10.0;
    }
    else if (ticker.equalsIgnoreCase("ALGN")) {
      scalar = 2.0;
    }
    else if (ticker.equalsIgnoreCase("GOOG")) {
      scalar = 1.5;
    }
    final double ret = eps / scalar;
    return ret;
  }

}
