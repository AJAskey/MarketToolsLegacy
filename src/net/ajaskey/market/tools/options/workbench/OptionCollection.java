package net.ajaskey.market.tools.options.workbench;

import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;

public class OptionCollection {

  private static List<OptionCollection> collectionList = new ArrayList<>();;

  public String           code;
  public DateTime         expiration;
  public double           ulPrice;
  public DateTime         dataDate;
  public List<OptionData> optionDataList;

  /**
   * 
   * @param c
   * @param exp
   */
  public OptionCollection(String c, DateTime exp, double ul, DateTime ts, OptionData od) {
    this.code = c;
    this.expiration = new DateTime(exp);
    this.ulPrice = ul;
    this.dataDate = new DateTime(ts);
    this.optionDataList = new ArrayList<>();
    this.optionDataList.add(od);
  }

  public OptionCollection() {
    this.code = "";
    this.expiration = null;
    this.ulPrice = 0.0;
    this.dataDate = null;
    this.optionDataList = new ArrayList<>();
  }

  /**
   * 
   * @param c
   * @param exp
   * @param od
   */
  public static void addToList(String c, DateTime exp, double ul, DateTime timeStamp, OptionData od) {

    for (OptionCollection oc : collectionList) {
      if (c.equalsIgnoreCase(oc.code)) {
        if (exp.isEqual(oc.expiration)) {
          oc.optionDataList.add(od);
          return;
        }
      }
    }

    OptionCollection oc = new OptionCollection(c, exp, ul, timeStamp, od);
    collectionList.add(oc);

  }

  public static List<OptionCollection> collate(Option opt) {
    for (OptionData od : opt.optList) {
      addToList(opt.sCode, od.expirationDate, opt.sUlLastTradePrice, opt.lastTrade, od);
    }
    return collectionList;
  }

  /**
   * 
   * @param c
   * @param exp
   * @param type
   * @return
   */
  public static OptionCollection get(String c, DateTime exp, String type) {

    OptionCollection ret = new OptionCollection();

    for (OptionCollection oc : collectionList) {

      if (c.equalsIgnoreCase(oc.code)) {
        if (exp.isEqual(oc.expiration)) {
          for (OptionData od : oc.optionDataList) {
            if (od.type.equalsIgnoreCase(type)) {

              ret.code = oc.code;
              ret.dataDate = new DateTime(oc.dataDate);
              ret.expiration = new DateTime(oc.expiration);
              ret.ulPrice = oc.ulPrice;
              ret.optionDataList.add(od);
            }
          }
        }
      }
    }
    return ret;
  }

  @Override
  public String toString() {
    String ret = String.format("%s\t%s\t%s%n", this.code, this.expiration, this.ulPrice);
    for (OptionData od : this.optionDataList) {
      ret += String.format("  %s%n", od);
    }
    return ret;
  }

}
