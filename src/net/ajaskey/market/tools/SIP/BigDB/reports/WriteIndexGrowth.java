package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.MarketTools;
import net.ajaskey.market.tools.SIP.BigDB.collation.QuarterlyDouble;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Utilities;

public class WriteIndexGrowth {

  double sales;
  double salesTtm;
  double net;
  double netTtm;
  double debt;
  double debtYr;
  double tanAssets;
  double tanAssetsYr;

  public WriteIndexGrowth() {
    sales = 0.0;
    salesTtm = 0.0;
    net = 0.0;
    netTtm = 0.0;
    debt = 0.0;
    debtYr = 0.0;
    tanAssets = 0.0;
    tanAssetsYr = 0.0;
  }

  public static void main(String[] args) throws FileNotFoundException {

    final int year = 2022;
    final int qtr = 3;

    final List<CompanyDerived> drList = Scans.findSpx(year, qtr);
    final List<CompanyDerived> cdrList = new ArrayList<>();
    for (final CompanyDerived cdr : drList) {
      if (cdr.getFd().getTicker().equalsIgnoreCase("GOOGL") || cdr.getFd().getTicker().equalsIgnoreCase("BRK.A")) {
        continue;
      }
      if (WriteIndexGrowth.has8qtrs(cdr)) {
        cdrList.add(cdr);
      }
    }

    WriteIndexGrowth wgi = new WriteIndexGrowth();
    wgi.process(cdrList);
    wgi.write();

    write(cdrList, "Sales");
    write(cdrList, "NetInc");
    write(cdrList, "BVPS");
    write(cdrList, "Debt");
    write(cdrList, "TanAssets");

  }

  public void write() {

    System.out.printf("Sales : %s\t%s\t%9.2f%% %n", Utils.fmt(sales, 15), Utils.fmt(salesTtm, 15), MarketTools.getChange(sales, salesTtm));
    System.out.printf("Net   : %s\t%s\t%9.2f%% %n", Utils.fmt(net, 15), Utils.fmt(netTtm, 15), MarketTools.getChange(net, netTtm));
    System.out.printf("Debt  : %s\t%s\t%9.2f%% %n", Utils.fmt(debt, 15), Utils.fmt(debtYr, 15), MarketTools.getChange(debt, debtYr));
    System.out.printf("TA    : %s\t%s\t%9.2f%% %n", Utils.fmt(tanAssets, 15), Utils.fmt(tanAssetsYr, 15),
        MarketTools.getChange(tanAssets, tanAssetsYr));

    System.out.println();
  }

  private void process(List<CompanyDerived> cdrList) {

    for (CompanyDerived cdr : cdrList) {
      sales += cdr.getSalesQdata().getTtm();
      salesTtm += cdr.getSalesQdata().getPrevTtm();
      net += cdr.getNetIncQdata().getTtm();
      netTtm += cdr.getNetIncQdata().getPrevTtm();

      debt += cdr.getStDebtQdata().get(1) + cdr.getLtDebtQdata().get(1);
      debtYr += cdr.getStDebtQdata().get(5) + cdr.getLtDebtQdata().get(5);

      tanAssets += cdr.getTanAssetsQdata().get(1);
      tanAssetsYr += cdr.getTanAssetsQdata().get(5);

    }

  }

  public static void write(List<CompanyDerived> cdrList, String valname) throws FileNotFoundException {

    String fname = String.format("sipout/%s_Growth.csv", valname);
    System.out.printf("Processing %s...%n", fname);

    final double[] total = new double[9];
    for (int i = 0; i < total.length; i++) {
      total[i] = 0.0;
    }

    QuarterlyDouble qd = null;

    try (PrintWriter pw = new PrintWriter(fname)) {
      pw.println("Code,Name,Sector,Industry, Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Chg51, Chg62, Chg73, Chg84");

      for (final CompanyDerived cdr : cdrList) {

        if (valname.equalsIgnoreCase("NetInc")) {
          qd = cdr.getNetIncQdata();
        }
        else if (valname.equalsIgnoreCase("Sales")) {
          qd = cdr.getSalesQdata();
        }
        else if (valname.equalsIgnoreCase("BVPS")) {
          qd = cdr.getEquityQdata();
        }
        else if (valname.equalsIgnoreCase("Debt")) {
          final double debtArr[] = new double[9];
          for (int i = 0; i < debtArr.length; i++) {
            debtArr[i] = cdr.getStDebtQdata().get(i) + cdr.getLtDebtQdata().get(i);
          }
          qd = new QuarterlyDouble(debtArr);
        }
        else if (valname.equalsIgnoreCase("TanAssets")) {
          final double taArr[] = new double[9];
          for (int i = 0; i < taArr.length; i++) {
            taArr[i] = cdr.getTanAssetsQdata().get(i) - (cdr.getStDebtQdata().get(i) + cdr.getLtDebtQdata().get(i));
          }
          qd = new QuarterlyDouble(taArr);
        }
        else {
          return;
        }

        QuarterlyDouble shares = cdr.getSharesQdata();
        final FieldData fd = cdr.getFd();

        pw.printf("%s,%s,%s,%s ", fd.getTicker(), Utilities.cleanForCsv(fd.getName()), Utilities.cleanSecInd(fd.getSector()),
            Utilities.cleanSecInd(fd.getIndustry()));

        for (int i = 1; i < 9; i++) {
          final double vps = qd.get(i) / shares.get(i);
          final double scaledVps = WriteIndexGrowth.scale(fd.getTicker(), vps);
          total[i] += scaledVps;
          pw.printf(",%f", scaledVps);
        }

        for (int i = 1; i < 5; i++) {
          double vpsPrev = qd.get(i + 4) / shares.get(i + 4);
          final double vps = qd.get(i) / shares.get(i);
          double chg = MarketTools.getChange(vps, vpsPrev);
          pw.printf(",%f", chg);
        }

        pw.printf("%n");
      }

      // Bottom line
      pw.printf(",,,");
      for (int i = 1; i < 9; i++) {
        pw.printf(",%f", total[i]);
      }

      for (int i = 1; i < 5; i++) {
        final double chg = MarketTools.getChange(total[i], total[i + 4]);
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
    else if (ticker.equalsIgnoreCase("MCK")) {
      scalar = 2.75;
    }
    else if (ticker.equalsIgnoreCase("HUM")) {
      scalar = 1.5;
    }
    else if (ticker.equalsIgnoreCase("CAH")) {
      scalar = 1.25;
    }
    else if (ticker.equalsIgnoreCase("AMZN")) {
      scalar = 1.75;
    }
    else if (ticker.equalsIgnoreCase("ABC")) {
      scalar = 2.3;
    }
    final double ret = eps / scalar;
    return ret;
  }

}
