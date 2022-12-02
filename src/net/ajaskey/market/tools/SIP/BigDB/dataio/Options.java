package net.ajaskey.market.tools.SIP.BigDB.dataio;

import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;

public class Options {

  public static List<Options> optList = new ArrayList<>();

  public static void main(String[] args) {

    Options.readOptionData(2021, 4);

    for (final Options opt : Options.optList) {
      System.out.println(opt);
    }

  }

  /**
   * 
   * @param fname
   */
  public static void readOptionData(int yr, int qtr) {

    final String fname = String.format("%s%d/Q%d/OPTIONABLE-%dQ%d.TXT", FieldData.inbasedir, yr, qtr, yr, qtr);
    final List<String> oData = TextUtils.readTextFile(fname, true);

    if (oData.size() == 0) {
      System.out.printf("ERROR... Option Data from %s not found!%n", fname);
      return;
    }

    for (final String s : oData) {
      final String ss = s.replace("\"", "");
      final String fld[] = ss.split(Utils.TAB);
      final Options opt = new Options(fld[0].trim(), fld[1].trim());
      Options.optList.add(opt);
      if (fld[0].trim().equals("GOOGL")) {
        Options o = new Options("GOOG", fld[1].trim());
        Options.optList.add(o);
      }
      else if (fld[0].trim().equals("BRK.A")) {
        Options o = new Options("BRK.B", fld[1].trim());
        Options.optList.add(o);
      }
    }

  }

  /**
   * 
   * @param ticker
   * @return
   */
  public static boolean isOptionable(String ticker) {
    boolean ret = false;
    for (Options opt : optList) {
      if (opt.ticker.equalsIgnoreCase(ticker)) {
        ret = opt.optionable;
        break;
      }
    }
    return ret;
  }

  public boolean optionable;
  public String  ticker;

  /**
   * Constructor
   * 
   * @param t
   * @param o
   */
  public Options(String t, String o) {
    this.ticker = t;
    if (o.equalsIgnoreCase("T")) {
      this.optionable = true;
    }
    else {
      this.optionable = false;
    }
  }

  @Override
  public String toString() {
    final String ret = String.format("%-7s %s", this.ticker, this.optionable);
    return ret;
  }

}
