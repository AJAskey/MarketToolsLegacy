package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.DowEnum;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.MarketTools;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;
import net.ajaskey.market.tools.SIP.BigDB.collation.CompanySummary;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;

public class WriteOwership {

  public static void main(String[] args) throws FileNotFoundException {

    final int year = 2022;
    final int qtr = 3;
    final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;

    MarketTools.parseSipData(year, qtr, ft, false);

    CompanyDerived.loadDb(year, qtr, ft);

    final List<String> sList = CompanySummary.get(year, qtr, SnpEnum.NONE, DowEnum.NONE, ExchEnum.NONE, 5.0, 1L);

    final List<FieldData> fdList = CompanyDerived.getFieldData(sList, year, qtr);

    Utils.makeDir("sipout");
    final String fname = String.format("sipout/Ownership-%dQ%d.csv", year, qtr);

    try (PrintWriter pw = new PrintWriter(fname)) {
      pw.println(
          "Ticker,Name,Sector,Industry,Avg Price,ShrOut,Inst NetShr,Inst Change($),Inst Percent,Insider NetShr,Insider Change($),Insider Percent");
      for (final FieldData fd : fdList) {

        double price = fd.getShareData().getPrice();
        if (fd.getCompanyInfo().getPriceQtr()[1] > 0.0 && fd.getCompanyInfo().getPriceQtr()[2] > 0.0 && fd.getCompanyInfo().getPriceQtr()[3] > 0.0) {
          price = (fd.getCompanyInfo().getPriceQtr()[1] + fd.getCompanyInfo().getPriceQtr()[2] + fd.getCompanyInfo().getPriceQtr()[3]) / 3.0;
        }
        else if (fd.getShareData().getPrice52lo() > 0.0 && fd.getShareData().getPrice52hi() > 0.0) {
          price = (fd.getShareData().getPrice52lo() + fd.getShareData().getPrice52hi()) / 2.0;
        }

        double shares = fd.getShareData().getSharesQtr()[1] * 1000000.0;
        if (shares == 0.0) {
          shares = fd.getShareData().getFloatshr() * 1000000;
        }

        final double ibuyShr = fd.getShareData().getInsiderBuyShrs();
        final double isellShr = fd.getShareData().getInsiderSellShrs();
        final double inetShr = ibuyShr - isellShr;
        final double ichg = inetShr * price;

        final double fbuyShr = fd.getShareData().getInstBuyShrs();
        final double fsellShr = fd.getShareData().getInstSellShrs();
        final double fnetShr = fbuyShr - fsellShr;
        final double fchg = fnetShr * price;

        if (shares > 0.0) {
          double fpOut = 0.0;
          double ipOut = 0.0;
          fpOut = fnetShr / shares;
          ipOut = inetShr / shares;

          final String name = fd.getName().replace(",", ";");
          final String sector = fd.getSector().replace(",", ";");
          final String industry = fd.getIndustry().replace(",", ";");

          final String s = String.format("%s,%s,%s,%s,%f,%d,%d,%d,%f,%d,%d,%f", fd.getTicker(), name, sector, industry, price, (int) shares,
              (int) fnetShr, (int) fchg, fpOut, (int) inetShr, (int) ichg, ipOut);
          pw.println(s);
        }
      }
    }
    System.out.println("Done.");
  }

}
