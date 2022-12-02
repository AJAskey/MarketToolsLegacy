package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;
import net.ajaskey.market.tools.SIP.BigDB.collation.CompanyDataAll;
import net.ajaskey.market.tools.SIP.BigDB.collation.CompanySummary;
import net.ajaskey.market.tools.SIP.BigDB.collation.YearQuarter;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class WriteInstOwnership {

  private static List<YearQuarter> yqList = new ArrayList<>();

  /**
   *
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    int year = 2022;
    int qtr = 3;

    final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;

    FieldData.setAllMemory(ft);

    final List<String> sp500 = CompanySummary.getSnp(SnpEnum.SP500, year, qtr);

    final List<FieldData> fdList = FieldData.getQFromMemory(sp500, year, qtr);

    final Set<String> hashSet = new HashSet<>();
    for (final FieldData fd : fdList) {
      hashSet.add(fd.getTicker());
    }
    final List<String> tickers = new ArrayList<>(hashSet);
    Collections.sort(tickers);

    for (final String t : tickers) {
      final List<CompanyDataAll> cData = CompanyDataAll.get(t);
      System.out.println(t);

      for (final CompanyDataAll cda : cData) {
        if (cda.checkQ(1) && cda.checkQ(2) && cda.checkQ(3)) {
          final String s = String.format("%s\t%.2f\t%.2f\t%.2f", t, cda.getQ1().getShareData().getInstOwnership(),
              cda.getQ2().getShareData().getInstOwnership(), cda.getQ3().getShareData().getInstOwnership());
          System.out.println(s);
        }
      }
    }

    System.out.println("Done.");
  }

}
