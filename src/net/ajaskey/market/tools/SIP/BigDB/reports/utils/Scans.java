package net.ajaskey.market.tools.SIP.BigDB.reports.utils;

import java.util.List;

import net.ajaskey.market.misc.Debug;
import net.ajaskey.market.tools.SIP.BigDB.DowEnum;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.FiletypeEnum;
import net.ajaskey.market.tools.SIP.BigDB.MarketTools;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;
import net.ajaskey.market.tools.SIP.BigDB.collation.CompanySummary;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;

public class Scans {

  /**
   *
   * @param ticker
   * @param desc
   * @param val
   * @param max
   * @return
   */
  public static boolean checkMaxValue(final String ticker, final String desc, final double val, final double max) {

    boolean ret = true;

    if (val > max) {
      final String s = ticker + desc;
      Debug.LOGGER.info(String.format("  %-15s Value : %.2f is greater than Max : %.2f%n", s, val, max));
      ret = false;
    }

    return ret;
  }

  /**
   *
   * @param ticker
   * @param desc
   * @param val
   * @param min
   * @return
   */
  public static boolean checkMinValue(final String ticker, final String desc, final double val, final double min) {

    boolean ret = true;

    if (val < min) {
      final String s = ticker + desc;
      Debug.LOGGER.info(String.format("  %-15s Value : %.2f is less than Min : %.2f%n", s, val, min));
      ret = false;
    }

    return ret;
  }

  /**
   *
   * @param year
   * @param qtr
   * @param ticker
   * @return
   */
  public static CompanyDerived findCompany(int year, int qtr, String ticker) {
    final FieldData fd = FieldData.getFromMemory(ticker, year, qtr);
    final CompanyDerived cdr = new CompanyDerived(fd);
    return cdr;
  }

  /**
   *
   * @param year
   * @param qtr
   * @param lowPrice
   * @param lowVolume
   * @return
   */
  public static List<CompanyDerived> findMajor(int year, int qtr, double lowPrice, long lowVolume) {

    final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;
    MarketTools.parseSipData(year, qtr, ft, false);
    CompanyDerived.loadDb(year, qtr, ft);

    final List<String> possibles = CompanySummary.get(year, qtr, SnpEnum.NONE, DowEnum.NONE, ExchEnum.MAJOR, lowPrice, lowVolume);
    final List<FieldData> fdList = FieldData.getListFromMemory(possibles, year, qtr);

    final List<CompanyDerived> drList = CompanyDerived.processList(fdList);

    return drList;
  }

  /**
   * 
   * @param year
   * @param qtr
   * @return
   */
  public static List<CompanyDerived> findSpx(int year, int qtr) {

    final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;
    MarketTools.parseSipData(year, qtr, ft, false);
    CompanyDerived.loadDb(year, qtr, ft);

    final List<String> possibles = CompanySummary.get(year, qtr, SnpEnum.SP500, DowEnum.NONE, ExchEnum.NONE, 0, 0);
    final List<FieldData> fdList = FieldData.getListFromMemory(possibles, year, qtr);

    final List<CompanyDerived> drList = CompanyDerived.processList(fdList);
    return drList;
  }

  /**
   * 
   * @param year
   * @param qtr
   * @return
   */
  public static List<CompanyDerived> findSml(int year, int qtr) {

    final FiletypeEnum ft = FiletypeEnum.BIG_BINARY;
    MarketTools.parseSipData(year, qtr, ft, false);
    CompanyDerived.loadDb(year, qtr, ft);

    final List<String> possibles = CompanySummary.get(year, qtr, SnpEnum.SP600, DowEnum.NONE, ExchEnum.NONE, 0, 0);
    final List<FieldData> fdList = FieldData.getListFromMemory(possibles, year, qtr);

    final List<CompanyDerived> drList = CompanyDerived.processList(fdList);
    return drList;
  }

}
