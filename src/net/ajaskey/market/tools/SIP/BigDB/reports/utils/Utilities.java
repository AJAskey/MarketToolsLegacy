package net.ajaskey.market.tools.SIP.BigDB.reports.utils;

import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;

public class Utilities {

  /**
   * 
   * @param secInd
   * @return
   */
  public static String cleanSecInd(String secInd) {
    int idx = secInd.indexOf("-");
    String ret = secInd.trim();
    if (idx > 0) {
      ret = secInd.substring(idx + 1).trim();
    }
    return cleanForCsv(ret);
  }

  /**
   * 
   * @param str
   * @return
   */
  public static String cleanForCsv(String str) {
    return str.replace(",", ";");
  }

  /**
   * 
   * @param enumStr
   * @return
   */
  public static ExchEnum convertExchange(String enumStr) {
    ExchEnum ret = ExchEnum.NONE;
    try {
      if (enumStr.contains("M - Nasdaq")) {
        ret = ExchEnum.NASDAQ;
      }
      else if (enumStr.contains("N - New York")) {
        ret = ExchEnum.NYSE;
      }
      else if (enumStr.contains("A - American")) {
        ret = ExchEnum.AMEX;
      }
      else if (enumStr.contains("O - Over the counter")) {
        ret = ExchEnum.OTC;
      }
    }
    catch (final Exception e) {
      System.out.println(FieldData.getWarning(e));
      ret = ExchEnum.NONE;
    }
    return ret;
  }

}
