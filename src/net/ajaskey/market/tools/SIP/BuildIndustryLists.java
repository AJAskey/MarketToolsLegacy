package net.ajaskey.market.tools.SIP;

import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.DowEnum;
import net.ajaskey.market.tools.SIP.BigDB.ExchEnum;
import net.ajaskey.market.tools.SIP.BigDB.SnpEnum;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Utilities;

public class BuildIndustryLists {

  static List<BuildIndustryLists> list    = null;
  final static double             MILLION = 1000000.0;

  /**
   *
   * @param args
   */
  public static void main(String[] args) {
    final List<String> text = TextUtils.readTextFile("data/SipStockList.txt", false);

    list = new ArrayList<>();

    int knt = 0;
    for (final String s : text) {
      knt++;
      // System.out.printf("%d : %s\n", knt, s);
      final BuildIndustryLists code = BuildIndustryLists.parse(s);
      if (code.valid) {
        BuildIndustryLists.list.add(code);
      }
    }

    BuildIndustryLists.process();

  }

  /**
   *
   * @param s
   * @return
   */
  private static BuildIndustryLists parse(String s) {
    final String[] fld = s.split("\t");
    final BuildIndustryLists ret = new BuildIndustryLists();
    ret.ticker = fld[0].trim().replace("\"", "");
    ret.name = fld[1].trim().replace("\"", "");
    ret.exch = Utilities.convertExchange(fld[2].trim().replace("\"", ""));
    switch (ret.exch) {
      case NASDAQ:
      case NYSE:
        ret.valid = true;
        break;
      default:
        ret.valid = false;
        break;
    }

    if (ret.valid) {
      ret.sector = Utilities.cleanSecInd(fld[3].trim().replace("\"", ""));
      ret.industry = Utilities.cleanSecInd(fld[4].trim().replace("\"", ""));
      ret.price = BuildIndustryLists.parseDouble(fld[5].trim().replace("\"", ""));
      ret.mCap = BuildIndustryLists.parseDouble(fld[6].trim().replace("\"", ""));
      ret.avgVol = BuildIndustryLists.parseDouble(fld[7].trim().replace("\"", ""));
      ret.numEmp = (int) (Math.round(parseDouble(fld[8].trim().replace("\"", ""))));
      ret.shares = (int) (Math.round((BuildIndustryLists.parseDouble(fld[9].trim().replace("\"", "")))));
      ret.outstanding = (int) (Math.round((BuildIndustryLists.parseDouble(fld[10].trim().replace("\"", "")))));
      ret.dow = BuildIndustryLists.parseDowStock(fld[11].trim().replace("\"", ""));
      ret.snp = BuildIndustryLists.parseSpnStock(fld[12].trim().replace("\"", ""));
      ret.street = fld[13].trim().replace("\"", "");
      ret.city = fld[14].trim().replace("\"", "");
      ret.state = fld[15].trim().replace("\"", "");
      ret.country = fld[16].trim().replace("\"", "");
      ret.zip = fld[17].trim().replace("\"", "");

      System.out.println(ret);
    }

    return ret;
  }

  /**
   *
   * @param str
   * @return
   */
  private static double parseDouble(String str) {
    double ret = -1.0;
    try {
      ret = Double.parseDouble(str);
    }
    catch (final Exception e) {
      ret = -2.0;
    }
    return ret;
  }

  /**
   *
   * @param str
   * @return
   */
  private static DowEnum parseDowStock(String str) {
    DowEnum ret = DowEnum.NONE;
    try {
      ret = DowEnum.valueOf(str.toUpperCase());
    }
    catch (final Exception e) {
      ret = DowEnum.NONE;
    }
    return ret;
  }

  /**
   *
   * @param str
   * @return
   */
  private static int parseInteger(String str) {
    int ret = -1;
    try {
      ret = Integer.parseInt(str);
    }
    catch (final Exception e) {
      ret = -2;
    }
    return ret;
  }

  /**
   *
   * @param str
   * @return
   */
  private static SnpEnum parseSpnStock(String str) {
    SnpEnum ret = SnpEnum.NONE;
    if (str.equals("500")) {
      ret = SnpEnum.SP500;
    }
    else if (str.equals("SmallCap 600")) {
      ret = SnpEnum.SP600;
    }
    else if (str.equals("MidCap 400")) {
      ret = SnpEnum.SP400;
    }
    return ret;
  }

  /**
   *
   */
  private static void process() {
    System.out.printf("Count : %d\n", BuildIndustryLists.list.size());
  }

  double   avgVol;
  String   city;
  String   country;
  DowEnum  dow;
  ExchEnum exch;
  String   industry;
  double   mCap;
  String   name;
  int      numEmp;
  int      outstanding;
  double   price;
  String   sector;
  int      shares;
  SnpEnum  snp;
  String   state;
  String   street;
  String   ticker;
  boolean  valid;
  String   zip;

  public BuildIndustryLists() {
    this.ticker = "";
    this.name = "";
    this.sector = "";
    this.industry = "";
    this.street = "";
    this.city = "";
    this.state = "";
    this.country = "";
    this.zip = "";
    this.exch = ExchEnum.NONE;
    this.dow = DowEnum.NONE;
    this.snp = SnpEnum.NONE;
    this.price = 0.0;
    this.mCap = 0.0;
    this.avgVol = 0.0;
    this.shares = 0;
    this.outstanding = 0;
    this.valid = false;
  }

  @Override
  public String toString() {
    String ret = this.ticker + Utils.TAB + this.name + Utils.TAB + this.exch + Utils.NL;
    ret += Utils.TAB + "Float: " + this.shares + "(M)" + Utils.TAB + "Outstanding: " + this.outstanding + "(M)";
    ret += Utils.TAB + "Market Cap: " + (int) (this.mCap + 0.5) + "(M)" + Utils.TAB + "Employees: " + this.numEmp;
    return ret;
  }

}
