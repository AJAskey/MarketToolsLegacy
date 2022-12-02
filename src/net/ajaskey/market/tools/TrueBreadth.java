package net.ajaskey.market.tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.market.optuma.OptumaCommon;
import net.ajaskey.market.optuma.TickerPriceData;

public class TrueBreadth {

  static SimpleDateFormat  sdf     = new SimpleDateFormat("yyyy-MM-dd");
  static List<TrueBreadth> spxList = new ArrayList<>();

  public static void main(String[] args) throws FileNotFoundException {
    final TickerPriceData advspx = new TickerPriceData("WI", "ADVSPX");
    final TickerPriceData decspx = new TickerPriceData("WI", "DECSPX");
    final TickerPriceData avvspx = new TickerPriceData("WI", "AVVSPX");
    final TickerPriceData dvcspx = new TickerPriceData("WI", "DVCSPX");

    final DateTime startDate = new DateTime(2016, DateTime.JANUARY, 01);
    final DateTime nullDate = new DateTime(startDate);
    nullDate.add(DateTime.DATE, -1);

    for (int i = 0; i < advspx.getNumPrices(); i++) {
      DateTime dt = null;
      try {
        dt = advspx.getOffsetDate(i);
      }
      catch (final Exception e) {
        dt = nullDate;
      }
      try {
        if (dt != null && dt.isGreaterThan(startDate)) {
          final double adv = advspx.getOffset(i).close * avvspx.getOffset(i).close;
          final double dec = decspx.getOffset(i).close * dvcspx.getOffset(i).close;
          dt.setSdf(TrueBreadth.sdf);
          TrueBreadth.spxList.add(new TrueBreadth(dt, adv, dec));
          // System.out.printf("%s\t%f\t%f\t%f\t%f\t%f\t%f\t%f\t%f%n",
          // advspx.getOffset(i).date, advspx.getOffset(i).close,
          // avvspx.getOffset(i).close, decspx.getOffset(i).close,
          // dvcspx.getOffset(i).close, adv, dec, tot, sum);
        }
      }
      catch (final Exception e) {
        e.printStackTrace();
      }

    }

    final int last = TrueBreadth.spxList.size() - 1;
    double sum = 0.0;
    try (PrintWriter pw = new PrintWriter(OptumaCommon.optumaPath + "\\Quandl\\spxBreadth.csv")) {
      for (int i = last; i >= 0; i--) {
        final TrueBreadth td = TrueBreadth.spxList.get(i);
        final String sDate = td.today.toString();
        System.out.printf("%s, %f, %f%n", sDate, td.advV, td.decV);
        sum = td.advV - td.decV;
        pw.printf("%s, %f%n", sDate, sum);
      }
    }
  }

  double advV;

  double decV;

  DateTime today;

  public TrueBreadth(DateTime dt, double a, double d) {
    this.today = dt;
    this.advV = a;
    this.decV = d;
  }

}
