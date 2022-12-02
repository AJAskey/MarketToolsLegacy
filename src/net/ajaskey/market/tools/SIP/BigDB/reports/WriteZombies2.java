package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.dataio.Options;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;

public class WriteZombies2 {

  public class ZSorter implements Comparator<WriteZombies2> {

    @Override
    public int compare(WriteZombies2 z1, WriteZombies2 z2) {
      if (z1.zombieness > z2.zombieness) {
        return -1;
      }
      else if (z1.zombieness < z2.zombieness) {
        return 1;
      }
      return 0;
    }

  }

  static WriteZombies2 WZ2 = new WriteZombies2();

  /**
   *
   * @param dList
   * @return
   */
  public static List<WriteZombies2> findZombies(int year, int quarter) {

    final List<CompanyDerived> dRList = Scans.findMajor(year, quarter, 25.0, 100000L);

    Options.readOptionData(year, quarter);

    final List<WriteZombies2> zList = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {

      if (!cdr.getFd().getSector().contains("Financials")) {
        if (Options.isOptionable(cdr.getFd().getTicker())) {
          final int qtrs = cdr.getSalesQdata().getQuarterDataKnt();
          if (qtrs > 4) {

            double opInc12m = cdr.getGrossOpIncQdata().getTtm();

            final WriteZombies2 wzLocal = new WriteZombies2();
            wzLocal.opIndTtm = opInc12m;
            wzLocal.totalCash = cdr.getCashQdata().getMostRecent() + opInc12m + cdr.getEquityQdata().getMostRecent();

            wzLocal.debtToCashAvailable = 99.9;
            wzLocal.equity = cdr.getEquityQdata().getMostRecent();
            wzLocal.cash = cdr.getCashQdata().getMostRecent();
            wzLocal.interestOwned12m = cdr.getIntTotQdata().getTtm();
            wzLocal.interestRate = 0.0;

            if (cdr.getTotalDebt() > 0.0) {
              wzLocal.interestRate = cdr.getIntTotQdata().getTtm() / cdr.getTotalDebt() * 100.0;
            }
            // Assume same interest payment for three more years
            wzLocal.totalDebt = cdr.getTotalDebt() + cdr.getIntTotQdata().getTtm() * 3.0;

            // No debt
            if (wzLocal.totalDebt <= 0.0) {
              wzLocal.debtToCashAvailable = 0.0;
              wzLocal.sPath = "No debt";
            }
            // Weird data like BUD
            else if (opInc12m == 0.0) {
              wzLocal.debtToCashAvailable = 0.0;
              wzLocal.sPath = "Positive Cash Flow";
            }
            // More cash than debt
            else if (cdr.getCashQdata().getMostRecent() >= wzLocal.totalDebt) {
              wzLocal.debtToCashAvailable = 0.0;
              wzLocal.sPath = "More cash than debt";
            }
            // Less cash than debt with CashOps positive
            else if (opInc12m > 0.0) {
              wzLocal.dtcNumerator = wzLocal.totalDebt - cdr.getCashQdata().getMostRecent() - cdr.getEquityQdata().getMostRecent();
              wzLocal.dtcDemoninator = opInc12m;
              wzLocal.debtToCashAvailable = wzLocal.dtcNumerator / wzLocal.dtcDemoninator;
              wzLocal.sPath = "Less cash than debt with CashOps positive";
            }
            // CashOps negative
            else if (opInc12m <= 0.0) {
              wzLocal.dtcNumerator = wzLocal.totalDebt - cdr.getCashQdata().getMostRecent() - cdr.getEquityQdata().getMostRecent();
              // Assume 1% gross margin
              wzLocal.dtcDemoninator = cdr.getSalesQdata().getTtm() * 0.01;
              wzLocal.debtToCashAvailable = wzLocal.dtcNumerator / wzLocal.dtcDemoninator;

              wzLocal.sPath = "CashOps negative";
            }

            if (wzLocal.debtToCashAvailable > 20.0) {
              wzLocal.zombieness = wzLocal.debtToCashAvailable * 0.10;
              wzLocal.cdr = cdr;
              wzLocal.path = 1;
              zList.add(wzLocal);
            }
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

    System.out.println("WriteZombies2...");

    final int year = 2022;
    final int qtr = 4;

    final List<WriteZombies2> dList = WriteZombies2.findZombies(year, qtr);

    Collections.sort(dList, WriteZombies2.WZ2.new ZSorter());

    try (PrintWriter pw = new PrintWriter("sipout/Zombies22.txt"); PrintWriter pwcsv = new PrintWriter("sipout/Zombies22.csv")) {

      final DateTime now = new DateTime();
      now.setSdf(Utils.sdfFull);

      int rank = 1;
      for (final WriteZombies2 wz : dList) {
        pwcsv.println(wz.cdr.getFd().getTicker());

        pw.printf("%n--------------------------------------------------------------------------------%nRank: %d%n%s%n", rank++, wz);
        WriteCompanyData.writeCompanyInfo(pw, wz.cdr);
        WriteCompanyData.write(pw, wz.cdr);
        WriteCompanyData.writeQuarterly(pw, wz.cdr);

        if (rank > 100) {
          break;
        }

      }
    }

    System.out.printf("Zombies : %d%n%n", dList.size());

    System.out.println("Done.");
  }

  double opIndTtm;
  double cash;
  double totalCash;
  double totalDebt;
  double debtToCashAvailable;
  double equity;
  double interestRate;
  double interestOwned12m;
  double zombieness;
  int    path;
  String sPath;
  double dtcNumerator;
  double dtcDemoninator;

  CompanyDerived cdr = null;

  /**
   * Constructor
   */
  public WriteZombies2() {
    this.cdr = null;
    this.path = 0;
    this.sPath = "";
    this.zombieness = 0.0;
    this.dtcNumerator = 0.0;
    this.dtcDemoninator = 0.0;
  }

  @Override
  public String toString() {
    String ret = ""; // String.format("Path: %d%n", this.path);
    ret += String.format("Total Cash     : %15.2f%n", this.totalCash);
    ret += String.format("  Equity       : %15.2f%n", this.equity);
    ret += String.format("  Cash         : %15.2f%n", this.cash);
    ret += String.format("  CashFrom Ops : %15.2f%n", this.opIndTtm);
    ret += String.format("TotalDebt      : %15.2f%n", this.totalDebt);
    ret += String.format(" Interest x 3  : %15.2f%n", this.interestOwned12m * 3.0);
    ret += String.format("Debt After Y1  : %15.2f   [TotalDebt - Cash - Equity]%n", this.totalDebt - this.cash - this.equity);

    ret += String.format("%nDebtToCash     : %15.2f %15.2f   %.2f %n%n", this.debtToCashAvailable, this.dtcNumerator, this.dtcDemoninator);

    // ret += String.format("Equity : %.2f%n", this.equity);
    // ret += String.format("Debt Interest : %.2f %.2f%%%n", this.interestOwned12m,
    // this.interestRate);
    ret += String.format("Zombieness     : %.2f%n", this.zombieness);
    ret += String.format("sPath          : %s%n", this.sPath);

    return ret;
  }

}
