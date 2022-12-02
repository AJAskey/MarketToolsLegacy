package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.util.List;

import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;

public class WriteSpxEstimates {

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    System.out.println("SPX Estimates...");

    int year = 2022;
    int qtr = 3;

    List<CompanyDerived> drList = Scans.findSpx(year, qtr);

    WriteCompanyData.writeEstimates("sipout/SpxEstimates.csv", drList);

    System.out.println("Done.");
  }

}
