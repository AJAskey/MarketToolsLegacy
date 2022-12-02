package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.dataio.Options;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.SortZScore;

public class WriteZombies {

  /**
   *
   * @param dList
   * @return
   */
  public static List<CompanyDerived> findZombies(List<CompanyDerived> dRList, int year, int quarter) {

    // final String filename = String.format("%s2021/Q1/OPTIONABLE-2021Q1.TXT",
    // FieldData.inbasedir);
    Options.readOptionData(year, quarter);

    final List<CompanyDerived> zList = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {

      if (!cdr.getFd().getSector().contains("Financials")) {
        if (cdr.getZdata().getzMajorScore() > 124.99) {
          if (Options.isOptionable(cdr.getFd().getTicker())) {
            int qtrs = cdr.getSalesQdata().getQuarterDataKnt();
            if (qtrs > 4) {
              cdr.setzState(zombieness(cdr));
              boolean case1 = getZombieStatus(cdr.getzState(), 1);
              boolean case2 = getZombieStatus(cdr.getzState(), 2);
              boolean case3 = getZombieStatus(cdr.getzState(), 3);

              if (case1 || case2 || case3) {
                zList.add(cdr);
              }
            }
          }
        }
      }
    }

    return zList;
  }

  /**
   * 
   * @param zbness
   * @param i
   * @return
   */
  private static boolean getZombieStatus(String zbness, int i) {
    boolean ret = false;
    String fld[] = zbness.split(" ");
    if (fld[i - 1].equalsIgnoreCase("true")) {
      ret = true;
    }
    return ret;
  }

  /**
   * Needs at least one negative financial Zombie characteristic.
   * 
   * @param cdr Company data
   * @return True of Zombie characteristic found, False otherwise
   */
  private static String zombieness(CompanyDerived cdr) {
    String ret = "";
    final double lowval = 0.0001;
    boolean case1 = false;
    boolean case2 = false;
    boolean case3 = false;

    if (cdr.getNetIncQdata().getTtm() < lowval) {
      if (cdr.getGrossOpIncQdata().getTtm() < lowval) {
        case1 = true;
      }
    }

    if ((cdr.getWcFcfQdata().getTtm() < lowval) && (cdr.getEquityQdata().getMostRecent() < lowval)) {
      case2 = true;
    }

    if (cdr.getNetTangible() < lowval) {
      case3 = true;
    }

    ret = String.format("%s %s %s", case1, case2, case3);
    return ret;
  }

  /**
   * 
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    System.out.println("WriteZombies...");

    int year = 2022;
    int qtr = 4;

    List<CompanyDerived> dRList = Scans.findMajor(year, qtr, 25.0, 500000L);

    final List<CompanyDerived> dList = WriteZombies.findZombies(dRList, year, qtr);

    Collections.sort(dList, new SortZScore());

    try (PrintWriter pw = new PrintWriter("sipout/Zombies2.txt");
        PrintWriter pwSnap = new PrintWriter("sipout/Zombies2-snap.txt");
        PrintWriter pwCsv = new PrintWriter("sipout/Zombies2.csv");
        PrintWriter pwTxt = new PrintWriter("sipout/Zombies2-sc.txt")) {
      pwCsv.println("Code,Exchange");

      final DateTime now = new DateTime();
      now.setSdf(Utils.sdfFull);

      pw.printf("Created : %s\t%s%n", now, "This file is subject to change without notice.");
      pw.println("Pre-filtered for AMEX, NASDAQ, NYSE companies over $20 and average trading volume of at least 500K.");
      pw.printf("Using the most recent %d Q%d data.%n", year, qtr);
      pw.println("\nSeq : this quarter versus last quarter.");
      pw.println("QoQ : this quarter versus same quarter a year ago.");
      pw.println("YoY : last 12m versus 12m a year ago.\n\n--------------------------");

      pw.printf("%nScoring%n");
      pw.printf("\tRequires at least one of these cases to be True.%n");
      pw.printf("\t\tCase 1: Gross Operating Income and Net Income to be negative%n");
      pw.printf("\t\tCase 2: Shareholder Equity and Working Capital plus FCF to be negative%n");
      pw.printf("\t\tCase 3: Net Tangible Assets minus Total Debt to be negative%n");
      pw.printf("\tWorking Capital%n\t\tratio = Current Liabilities / (Current Assets + FCF12m) -- Assume FCF level will continue going forward.%n");
      pw.printf("\t\tif (ratio > 1)%n\t\t\tzWorkingCapital = POW(ratio, 9.25) -- Max 150%n");

      pw.printf("\tDebt -- Try Debt vs TanAssets for common comparison as many companies do not have positive Equity.%n");
      pw.printf("\t\tTanAssets = All Assets - Goodwill%n");
      pw.printf("\t\tif (Equity > 0) AND (Debt to Equity > 1)%n");
      pw.printf("\t\t\tif (Debt to TanAssets > 1)%n");
      pw.printf("\t\t\t\tzDebt = POW(5.0, Debt to TanAssets) -- MAX 125%n");
      pw.printf("\t\t\telse%n\t\t\t\tzDebt = POW(2.5, Debt to Equity) -- MAX 125%n");
      pw.printf("\t\telse if (TanAssets > 0) AND (Debt To TanAssets > 1)%n");
      pw.printf("\t\t\tzDebt = 50 + POW(5.0, Debt to TanAssets) -- Start at 50 becaue negative Equity ; MAX 125%n");
      pw.printf("\t\telse if (Sales12m > 0) AND (Debt To Sales > 1)%n");
      pw.printf("\t\t\tzDebt = 75 + POW(2.0, Debt to Sales) -- Start at 75 because negative Equity and TanAssets ; MAX 125%n");
      pw.printf("\t\telse if (Debt > 0)%n\t\t\tzDebt = 125%n");

      pw.printf("\tDebt vs TanAssets -- Debt flows into TanAssets = GOOD ; Debt up and TanAssets down = BAD%n");
      pw.printf("\t\tTanAssets = All Assets - Goodwill%n");
      pw.printf("\t\ttaChg = TanAssets - TanAssets Year Ago%n");
      pw.printf("\t\tdebtChg = Total Debt - Total Debt Year Ago%n");
      pw.printf("\t\tratio = debtChg / taChg%n");
      pw.printf("\t\tif (ratio > 1) AND (debtChg > 0)%n");
      pw.printf("\t\t\tzDebtVsTa = POW(4.25, ratio) -- MAX 75%n");
      pw.printf("\t\telse if (debtChg > 0) AND (taChg < 0)%n");
      pw.printf("\t\t\tzDebtVsTa = 75%n");

      pw.printf("\tInterest rates and payments%n");
      pw.printf("\t\tIntRate = Interest Paid / Total Debt%n");
      pw.printf("\t\tif (IntRate > 0.30) -- Arbitrarily say rates less than 3.0%% are not a problem.%n");
      pw.printf("\t\t\tzInterest = POW(1.80, IntRate*100.0) -- MAX 62.5%n");
      pw.printf("\t\tIntToSales = Interest Paid / Sales12m%n");
      pw.printf("\t\tif (IntToSales > 0)%n");
      pw.printf("\t\t\tzInterest += POW(5.0, IntToSales*10.0) -- MAX 62.5%n");

      pw.printf("%n---------------------------------------------------------------------------------------------------%n%n");

      int rank = 1;
      for (final CompanyDerived cdr : dList) {
        pw.printf(" Rank:%s Zscore:%3d%n", rank++, (int) cdr.getZdata().getzScore());
        WriteCompanyData.writeCompanyInfo(pw, cdr);
        WriteCompanyData.write(pw, cdr);
        WriteCompanyData.writeQuarterly(pw, cdr);

        // Snapshot
        WriteCompanyData.writeSnapshot(pwSnap, cdr);

        pw.println(Utils.NL + cdr.getZdata());

        pwCsv.printf("%s,US%n", cdr.getFd().getTicker());
        pwTxt.printf("%s%n", cdr.getFd().getTicker());
        if (rank > 100) {
          break;
        }

      }
    }
    WriteZombies.findZombies(dList, year, qtr);

    try (PrintWriter pwPy = new PrintWriter("out/zombie-python-list.txt"); PrintWriter pw = new PrintWriter("sipout/zombies-all.txt")) {
      pwPy.printf("%s", "zombie_codes = [");
      for (final CompanyDerived cdr : dList) {
        pwPy.printf(",'%s'", cdr.getTicker());
        pw.println(cdr.getTicker());
        System.out.println(cdr.getTicker());
      }
      pwPy.println("]");
    }
    System.out.println(dList.size());
    System.out.println("Done.");
  }

}
