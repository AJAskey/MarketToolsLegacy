package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;

public class WriteSmlEstimates {

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    System.out.println("SML Estimates...");

    int year = 2022;
    int qtr = 3;

    List<CompanyDerived> drList = Scans.findSml(year, qtr);

    WriteCompanyData.writeEstimates("sipout/SmlEstimates.csv", drList);

    System.out.println("Done.");
  }

}
