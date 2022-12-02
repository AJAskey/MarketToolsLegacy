package net.ajaskey.market.tools.SIP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;

public class PrePostCovidAnalysis {

  final static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  public class RealData {
    String code;
    double ttm;
    double y1;
    double y2;
    double y3;
    double y4;

    public RealData(String code, String ttm, String y1, String y2, String y3, String y4) {
      this.code = code;
      this.ttm = Double.parseDouble(ttm);
      this.y1 = Double.parseDouble(y1);
      this.y2 = Double.parseDouble(y2);
      this.y3 = Double.parseDouble(y3);
      this.y4 = Double.parseDouble(y4);
    }

    @Override
    public String toString() {
      String ret = "";
      ret += String.format("\tttm=%f\n\ty1=%f\n\ty2=%f\n\ty3=%f\n\ty4=%f\n", ttm, y1, y2, y3, y4);
      return ret;
    }

  }

  public class DateData {
    String   code;
    DateTime y1;
    DateTime y2;
    DateTime y3;
    DateTime y4;

    public DateData(String code, String y1, String y2, String y3, String y4) {
      this.code = code;
      this.y1 = new DateTime(y1, sdf);
      this.y2 = new DateTime(y2, sdf);
      this.y3 = new DateTime(y3, sdf);
      this.y4 = new DateTime(y4, sdf);

    }

    @Override
    public String toString() {
      String ret = this.code;
      ret += String.format("\n\ty1=%s\n\ty2=%s\n\ty3=%s\n\ty4=%s", y1, y2, y3, y4);
      return ret;
    }
  }

  private static PrePostCovidAnalysis ppca = new PrePostCovidAnalysis();

  DateData ddList;
  RealData year1;
  RealData year2;
  RealData year3;
  RealData year4;

  List<PrePostCovidAnalysis> list = new ArrayList<>();

  public static void main(String[] args) {

    List<String> data = TextUtils.readTextFile("D:\\dev\\eclipse-markettools\\MarketTools\\sipdata\\MARGINANALYSIS.TXT", true);

    for (String s : data) {
      String ss = s.replace("\"", "");

      String flds[] = ss.split("\t");
      DateData dd = ppca.new DateData(flds[0], flds[3], flds[4], flds[5], flds[6]);
      RealData nm = ppca.new RealData(flds[0], flds[7], flds[8], flds[9], flds[10], flds[11]);
      RealData om = ppca.new RealData(flds[0], flds[12], flds[13], flds[14], flds[15], flds[16]);
      RealData cash = ppca.new RealData(flds[0], "0.0", flds[17], flds[18], flds[19], flds[20]);
      RealData debt = ppca.new RealData(flds[0], "0.0", flds[21], flds[22], flds[23], flds[24]);
      System.out.println(dd);
      System.out.printf(" nm\n%s", nm);
      System.out.printf(" om\n%s", om);
      System.out.printf(" cash\n%s", cash);
      System.out.printf(" debt\n%s", debt);

    }

  }

}
