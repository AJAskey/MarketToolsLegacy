package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.market.misc.Debug;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;

public class WriteBestCompanies {

  /**
   *
   * @param dList
   * @return
   */
  public static List<CompanyDerived> findBest(List<CompanyDerived> dRList) {

    final List<CompanyDerived> zList = new ArrayList<>();

    final DateTime now = new DateTime();
    now.setSdf(Utils.sdfFull);

    for (final CompanyDerived cdr : dRList) {

//      if (cdr.getFd().getTicker().equalsIgnoreCase("BHVN")) {
//        System.out.println("BVHN");
//      }

      FieldData fd = cdr.getFd();
      String ticker = fd.getTicker();

      final int qtrs = cdr.getSalesQdata().getQuarterDataKnt();
      if (!Scans.checkMinValue(ticker, " Sales Quarters of data", (double) qtrs, 4.0)) {
        continue;
      }

      if (!Scans.checkMinValue(ticker, " Sales QoQ", cdr.getSalesQdata().getQoQ(), 10.0)) {
        continue;
      }
      if (!Scans.checkMinValue(ticker, " Sales YoY", cdr.getSalesQdata().getYoY(), 10.0)) {
        continue;
      }

      if (!Scans.checkMinValue(ticker, " NetIncome Neg", cdr.getNetIncQdata().getMostRecent(), 0.01)) {
        continue;
      }
      if (!Scans.checkMinValue(ticker, " NetIncome TTM Neg", cdr.getNetIncQdata().getTtm(), 0.01)) {
        continue;
      }
      if (!Scans.checkMinValue(ticker, " NetIncome QoQ", cdr.getNetIncQdata().getQoQ(), 25.0)) {
        continue;
      }

      if (!Scans.checkMinValue(ticker, " Cash from Operations Neg", cdr.getCashFromOpsQdata().getMostRecent(), 0.01)) {
        continue;
      }
      if (!Scans.checkMinValue(ticker, " Cash from Operations TTM Neg", cdr.getCashFromOpsQdata().getTtm(), 0.01)) {
        continue;
      }

      if (!Scans.checkMinValue(ticker, " OpMargin", cdr.getOpMarginQdata().getMostRecent(), 10.0)) {
        continue;
      }
      if (!Scans.checkMinValue(ticker, " NetMargin", cdr.getNetMarginQdata().getMostRecent(), 10.0)) {
        continue;
      }

      if (!Scans.checkMinValue(ticker, " Equity Neg", cdr.getEquityQdata().getMostRecent(), 0.0)) {
        continue;
      }
      if (!Scans.checkMinValue(ticker, " ROE", cdr.getRoeQdata().getMostRecent(), 10.0)) {
        continue;
      }

      if (!Scans.checkMinValue(ticker, " EPS QoQ", cdr.getEpsDilContQdata().getQoQ(), 25.0)) {
        continue;
      }
      if (!Scans.checkMinValue(ticker, " EPS YoY", cdr.getEpsDilContQdata().getYoY(), 20.0)) {
        continue;
      }

      if (!Scans.checkMinValue(ticker, " EPS Q0 Growth Est", cdr.getEpsEstQ0Growth(), 25.0)) {
        continue;
      }

      final boolean earnEst = cdr.getEpsEstQ0() < 0.0 || cdr.getEpsEstY1() < 0.0;
      if (earnEst) {
        final String s = ticker + " Negative earnings estimates";
        Debug.LOGGER.info(String.format("  %-15s%n", s));
        continue;
      }

      zList.add(cdr);

    }

    return zList;

  }

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    int year = 2022;
    int qtr = 4;

    Debug.init("debug/BestCompany.dbg");

    List<CompanyDerived> dRList = Scans.findMajor(year, qtr, 20.0, 500000L);

    final List<CompanyDerived> dList = WriteBestCompanies.findBest(dRList);

    String sList = "";
    try (PrintWriter pw = new PrintWriter("sipout/BestCompanies.txt"); PrintWriter pwcsv = new PrintWriter("sipout/BestCompanies.csv")) {

      for (final CompanyDerived cdr : dList) {
        WriteCompanyData.writeCompanyInfo(pw, cdr);
        WriteCompanyData.write(pw, cdr);
        WriteCompanyData.writeQuarterly(pw, cdr);
        pw.println("");

        pwcsv.printf("%s,US%n", cdr.getTicker());

        sList += String.format("%n  %s", cdr.getTicker());
      }
    }
    Debug.LOGGER.info(String.format("  In Best List %s%n", sList));

    System.out.println("Done.");
  }

}
