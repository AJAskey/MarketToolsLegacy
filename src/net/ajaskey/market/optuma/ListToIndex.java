package net.ajaskey.market.optuma;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;

public class ListToIndex {

  final static String fname = "ibd 50";

  final static String theFile = "lists/" + fname + ".csv";
  static List<String> tickers = new ArrayList<>();

  final static boolean WITH_US = false;

  public static void main(String[] args) {

    List<String> codes = TextUtils.readTextFile(theFile, true);

    int knt = 0;
    for (String s : codes) {
      String fld[] = s.split(",");
      String code = fld[0].trim();
      if (!code.equalsIgnoreCase("code")) {
        knt++;
        System.out.println(code);
        tickers.add(code);
      }
    }
    System.out.println(knt);

    final String fname2 = "out/" + fname + ".txt";
    final double power = 1.0 / tickers.size();
    try (PrintWriter pw = new PrintWriter(fname2)) {
      if (WITH_US) {
        pw.printf("POWER(%s:US", tickers.get(0));
      }
      else {
        pw.printf("POWER(%s", tickers.get(0));
      }
      for (int i = 1; i < tickers.size(); i++) {
        final String s = tickers.get(i);
        if (WITH_US) {
          pw.printf(" * %s:US", s);
        }
        else {
          pw.printf("*%s", s);
        }
      }
      pw.printf(", POWER=%.4f)%n", power);
    }
    // POWER(BA:US * NOC:US * LMT:US * RTX:US, POWER=0.25)
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

  }

}
