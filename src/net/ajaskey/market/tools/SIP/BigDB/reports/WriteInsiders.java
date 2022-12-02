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

public class WriteInsiders {

  public static void main(String[] args) throws FileNotFoundException {

    final int year = 2022;
    final int qtr = 3;
    final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;

    MarketTools.parseSipData(year, qtr, ft, false);

    FieldData.setQMemory(year, qtr, ft);

    final List<String> sList = CompanySummary.get(year, qtr, SnpEnum.NONE, DowEnum.NONE, ExchEnum.NONE, 5.0, 1L);

    final List<FieldData> fdList = CompanyDerived.getFieldData(sList, year, qtr);

    Utils.makeDir("sipout");
    final String fname = String.format("sipout/Insiders-%dQ%d.csv", year, qtr);

    try (PrintWriter pw = new PrintWriter(fname)) {
      pw.println("Ticker,Name,Sector,Industry,NetShr,Price,Change($),ShrOut,Percent");
      for (final FieldData fd : fdList) {

        double price = fd.getShareData().getPrice();
        if (fd.getCompanyInfo().getPriceQtr()[1] > 0.0 && fd.getCompanyInfo().getPriceQtr()[2] > 0.0 && fd.getCompanyInfo().getPriceQtr()[3] > 0.0) {
          price = (fd.getCompanyInfo().getPriceQtr()[1] + fd.getCompanyInfo().getPriceQtr()[2] + fd.getCompanyInfo().getPriceQtr()[3]) / 3.0;
        }
        else if (fd.getShareData().getPrice52lo() > 0.0 && fd.getShareData().getPrice52hi() > 0.0) {
          price = (fd.getShareData().getPrice52lo() + fd.getShareData().getPrice52hi()) / 2.0;
        }

        final double buyShr = fd.getShareData().getInsiderBuyShrs();
        final double sellShr = fd.getShareData().getInsiderSellShrs();
        final double netShr = buyShr - sellShr;
        final double chg = netShr * price;
        double shares = fd.getShareData().getSharesQtr()[1] * 1000000.0;
        if (shares == 0.0) {
          shares = fd.getShareData().getFloatshr() * 1000000;
        }

        if (shares > 0.0) {
          double pOut = 0.0;
          if (shares != 0.0) {
            pOut = netShr / shares;
          }

          final String name = fd.getName().replace(",", ";");
          final String sector = fd.getSector().replace(",", ";");
          final String industry = fd.getIndustry().replace(",", ";");

          final String s = String.format("%s,%s,%s,%s,%d,%.2f,%d,%d,%f", fd.getTicker(), name, sector, industry, (int) netShr, price, (int) chg,
              (int) shares, pOut);
          pw.println(s);
        }
      }
    }
    System.out.println("Done.");
  }

}
