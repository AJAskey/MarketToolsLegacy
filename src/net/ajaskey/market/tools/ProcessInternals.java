package net.ajaskey.market.tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.market.math.MovingAverages;
import net.ajaskey.market.optuma.OptumaCommon;
import net.ajaskey.market.optuma.PriceData;
import net.ajaskey.market.optuma.TickerPriceData;

// TODO: Auto-generated Javadoc
/**
 * The Class ProcessInternals.
 */
public class ProcessInternals {

  /** The addn. */
  // Adv - Decl
  static TickerPriceData addn = null;

  /** The addq. */
  static TickerPriceData addq = null;

  /** The addspx. */
  static TickerPriceData addspx = null;

  /** The adrn. */
  // a/d ratio
  static TickerPriceData adrn = null;

  /** The adrq. */
  static TickerPriceData adrq = null;

  /** The adrspx. */
  static TickerPriceData adrspx = null;

  /** The advn. */
  // advancing
  static TickerPriceData advn = null;

  /** The advq. */
  static TickerPriceData advq = null;

  /** The advrn. */
  // av/dv vol ratio
  static TickerPriceData advrn = null;

  /** The advrq. */
  static TickerPriceData advrq = null;

  /** The advrspx. */
  static TickerPriceData advrspx = null;

  /** The advspx. */
  static TickerPriceData advspx = null;

  /** The avvn. */
  // adv volume
  static TickerPriceData avvn = null;

  /** The avvq. */
  static TickerPriceData avvq = null;

  /** The avvspx. */
  static TickerPriceData avvspx = null;

  /** The avvspx bm. */
  static TickerPriceData avvspxBm = null;

  /** The decn. */
  // declining
  static TickerPriceData decn = null;

  /** The decq. */
  static TickerPriceData decq = null;

  /** The decspx. */
  static TickerPriceData decspx = null;

  /** The dvcn. */
  // dec volume
  static TickerPriceData dvcn = null;

  /** The dvcq. */
  static TickerPriceData dvcq = null;

  /** The dvcspx. */
  static TickerPriceData dvcspx = null;

  /** The dvcspx bm. */
  static TickerPriceData dvcspxBm = null;

  /** The mcc oscn. */
  static TickerPriceData mccOscn = null;

  /** The mcc oscq. */
  static TickerPriceData mccOscq = null;

  /** The mcc oscspx. */
  static TickerPriceData mccOscspx = null;

  /** The mcc sumn. */
  static TickerPriceData mccSumn = null;

  /** The mcc sumq. */
  static TickerPriceData mccSumq = null;

  /** The mcc sumspx. */
  static TickerPriceData mccSumspx = null;

  /** The spy data. */
  static TickerPriceData spyData = null;

  /** The trinn. */
  static TickerPriceData trinn = null;

  /** The trinq. */
  static TickerPriceData trinq = null;

  /** The trinspx. */
  static TickerPriceData trinspx = null;

  /** The vix. */
  static TickerPriceData vix = null;

  /** The vxo. */
  static TickerPriceData vxo = null;

  /**
   * Inits the.
   */
  public static void init() {
    // Adv - Decl
    ProcessInternals.addn = new TickerPriceData("WI", "ADDN", "NYSE Advances-Declines");
    ProcessInternals.addq = new TickerPriceData("WI", "ADDQ", "NASD Advances-Declines");
    ProcessInternals.addspx = new TickerPriceData("WI", "ADDSPX", "SPX Advances-Declines");

    // a/d ratio
    ProcessInternals.adrn = new TickerPriceData("WI", "ADRN", "NYSE Adv Decl Ratio");
    ProcessInternals.adrq = new TickerPriceData("WI", "ADRS", "NASD Adv Decl Ratio");
    ProcessInternals.adrspx = new TickerPriceData("WI", "ADRSPX", "SPX AdvDecl Ratio");

    // advancing
    ProcessInternals.advn = new TickerPriceData("WI", "ADVN", "NYSE Advancing");
    ProcessInternals.advq = new TickerPriceData("WI", "ADVQ", "NASD Advancing");
    ProcessInternals.advspx = new TickerPriceData("WI", "ADVSPX", "SPX Advancing");

    // adv volume
    ProcessInternals.avvn = new TickerPriceData("WI", "AVVN", "NYSE Adv Volume");
    ProcessInternals.avvq = new TickerPriceData("WI", "AVVQ", "NASD Adv Volume");
    ProcessInternals.avvspx = new TickerPriceData("WI", "AVVSPX");

    // declining
    ProcessInternals.decn = new TickerPriceData("WI", "DECN", "NYSE Declining");
    ProcessInternals.decq = new TickerPriceData("WI", "DECQ", "NASD Declining");
    ProcessInternals.decspx = new TickerPriceData("WI", "DECSPX", "SPX Declining");

    // dec volume
    ProcessInternals.dvcn = new TickerPriceData("WI", "DVCN", "NYSE Decl Volume");
    ProcessInternals.dvcq = new TickerPriceData("WI", "DVCQ", "NASD Decl Volume");
    ProcessInternals.dvcspx = new TickerPriceData("WI", "DVCSPX", "SPX Decl Volume");

    ProcessInternals.spyData = new TickerPriceData("AMEX", "SPY", "SPY ETF Data");

    ProcessInternals.avvspxBm = new TickerPriceData("BM", "AVVSPX", "SPX Advancing Volume");
    ProcessInternals.dvcspxBm = new TickerPriceData("BM", "DVCSPX", "SPX Declining Volume");

  }

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws FileNotFoundException the file not found exception
   */
  public static void main(String[] args) throws FileNotFoundException {

    ProcessInternals.init();

    ProcessInternals.advrn = ProcessInternals.setRatio("ADVRN", ProcessInternals.avvn, ProcessInternals.dvcn, "NYSE Adv Decl Volume Ratio");
    ProcessInternals.advrq = ProcessInternals.setRatio("ADVRQ", ProcessInternals.avvq, ProcessInternals.dvcq, "NASD Adv Decl Volume Ratio");
    ProcessInternals.advrspx = ProcessInternals.setRatio("ADVRSPX", ProcessInternals.avvspx, ProcessInternals.dvcspx, "SPX Adv Decl Volume Ratio");

    ProcessInternals.trinn = ProcessInternals.setRatio("TRIN-NYSE", ProcessInternals.adrn, ProcessInternals.advrn, "NYSE TRIN");
    ProcessInternals.trinq = ProcessInternals.setRatio("TRIN-NASD", ProcessInternals.adrq, ProcessInternals.advrq, "NASD TRIN");
    ProcessInternals.trinspx = ProcessInternals.setRatio("TRIN-SPX", ProcessInternals.adrspx, ProcessInternals.advrspx, "SPX TRIN");

    ProcessInternals.mccOscn = ProcessInternals.setMcClellanOscillator(ProcessInternals.addn, ProcessInternals.advn, ProcessInternals.avvn);
    ProcessInternals.mccSumn = ProcessInternals.setMcClellanSummation(ProcessInternals.mccOscn);

    ProcessInternals.mccOscq = ProcessInternals.setMcClellanOscillator(ProcessInternals.addq, ProcessInternals.advq, ProcessInternals.avvq);
    ProcessInternals.mccSumq = ProcessInternals.setMcClellanSummation(ProcessInternals.mccOscq);

    ProcessInternals.mccOscspx = ProcessInternals.setMcClellanOscillator(ProcessInternals.addspx, ProcessInternals.advspx, ProcessInternals.avvspx);
    ProcessInternals.mccSumspx = ProcessInternals.setMcClellanSummation(ProcessInternals.mccOscspx);

    ProcessInternals.write(ProcessInternals.adrn);
    ProcessInternals.write(ProcessInternals.advrn);
    ProcessInternals.write(ProcessInternals.advrq);
    ProcessInternals.write(ProcessInternals.advrspx);
    ProcessInternals.write(ProcessInternals.trinn);
    ProcessInternals.write(ProcessInternals.trinq);
    ProcessInternals.write(ProcessInternals.trinspx);

    ProcessInternals.write(ProcessInternals.mccOscn);
    ProcessInternals.write(ProcessInternals.mccSumn);

    ProcessInternals.write(ProcessInternals.mccOscq);
    ProcessInternals.write(ProcessInternals.mccSumq);

    ProcessInternals.write(ProcessInternals.mccOscspx);
    ProcessInternals.write(ProcessInternals.mccSumspx);

    ProcessInternals.writeSpyVsSpxVolume(ProcessInternals.avvspxBm, ProcessInternals.dvcspxBm, ProcessInternals.spyData);
  }

  /**
   * Write.
   *
   * @param tpd the TickerPriceData
   *
   * @throws FileNotFoundException the file not found exception
   */
  public static void write(TickerPriceData tpd) throws FileNotFoundException {

    final String fname = tpd.getTicker() + ".csv";
    try (PrintWriter pw = new PrintWriter(String.format("%sDC\\%s", OptumaCommon.optumaPath, fname))) {
      for (final PriceData pd : tpd.getPriceData()) {
        pw.printf("%s, %.2f%n", pd.date.format("yyyy-MM-dd"), pd.close);
      }
    }
  }

  /**
   * RANA: (Advances - Declines)/(Advances +Declines).
   *
   * @param ad  the ad
   * @param adv the adv
   * @param dec the dec
   * @return the ticker price data
   */
  private static TickerPriceData setMcClellanOscillator(TickerPriceData ad, TickerPriceData adv, TickerPriceData dec) {

    final MovingAverages ema19 = new MovingAverages(19);
    final MovingAverages ema39 = new MovingAverages(39);

    final TickerPriceData ret = new TickerPriceData(String.format("%s-mco : Merged : %s", ad.getTicker(), "McClellan Oscillator"));

    for (final PriceData pd : ad.getPriceData()) {

      final DateTime dt = pd.date;

      final long advKnt = (long) adv.getClose(dt);
      final long decKnt = (long) dec.getClose(dt);
      if (advKnt > 100 && decKnt > 100) {
        final long adKnt = (long) ad.getClose(dt);
        final long total = advKnt + decKnt;

        final double rana = (double) adKnt / (double) total;
        final double mco = (ema19.getEma() - ema39.getEma()) * 1000.0;
        ema19.addValue(rana);
        ema39.addValue(rana);

        final PriceData pdnew = new PriceData(dt, mco, mco, mco, mco, 0);
        ret.addPriceData(pdnew);

        // System.out.printf("%s\t%15.6f\t%12.4f\t%12.4f\t%12.4f %n", dt, rana,
        // ema19.getEma(), ema39.getEma(), mco);
      }
    }
    return ret;
  }

  /**
   * Sets the mc clellan summation.
   *
   * @param mccOsc the mcc osc
   * @return the ticker price data
   */
  private static TickerPriceData setMcClellanSummation(TickerPriceData mccOsc) {

    final String code = mccOsc.getTicker().replace("mco", "sum");
    final TickerPriceData ret = new TickerPriceData(String.format("%s : Merged : %s", code, "McClellan Summation"));

    double sum = 0.0;
    for (final PriceData pd : mccOsc.getTickerPrices()) {
      sum += pd.close;
      final PriceData pdnew = new PriceData(pd.date, sum, sum, sum, sum, 0);
      ret.addPriceData(pdnew);
    }
    return ret;
  }

  /**
   * Sets the ratio.
   *
   * @param code  the code
   * @param list1 the list 1
   * @param list2 the list 2
   * @param desc  the desc
   * @return the ticker price data
   */
  private static TickerPriceData setRatio(String code, TickerPriceData list1, TickerPriceData list2, String desc) {

    final DateTime d1 = list1.getFirstDate();
    final DateTime d2 = list2.getFirstDate();

    System.out.println(d1);
    System.out.println(d2);

    final TickerPriceData ret = new TickerPriceData(String.format("%s : Merged : %s", code, desc));
    for (int i = 0; i < list1.getNumPrices(); i++) {

      final DateTime dt = list1.getOffsetDate(i);
      if (dt != null) {
        final double p2 = list2.getClose(dt);
        double d = 0.0;
        if (Math.abs(p2) > 0.0) {
          final double p1 = list1.getClose(dt);
          d = p1 / p2;
        }
        final PriceData pd = new PriceData(dt, d, d, d, d, 0);
        ret.addPriceData(pd);
      }
    }
    ret.reversePriceData();

    return ret;
  }

  /**
   * Write spy vs spx volume.
   *
   * @param avv the avv
   * @param dvc the dvc
   * @param spy the spy
   * @throws FileNotFoundException the file not found exception
   */
  private static void writeSpyVsSpxVolume(TickerPriceData avv, TickerPriceData dvc, TickerPriceData spy) throws FileNotFoundException {

    DateTime d1 = new DateTime(2010, DateTime.JANUARY, 2);

    final TickerPriceData totSpxVol = new TickerPriceData(" SPX volume");

    final DateTime today = new DateTime();
    while (d1.isLessThanOrEqual(today)) {

      final double p1 = avv.getActualClose(d1);
      if (p1 >= 0.0) {
        final double p2 = dvc.getActualClose(d1);
        if (p2 >= 0.0) {

          final double d = (p1 + p2) * 1E06;
          final PriceData tot = new PriceData(d1, d, d, d, d, 0);
          totSpxVol.addPriceData(tot);
        }
      }
      d1.add(DateTime.DATE, 1);
    }

    String fname = "SPX-Total-Volume.csv";
    try (PrintWriter pw = new PrintWriter(String.format("%sDC\\%s", OptumaCommon.optumaPath, fname))) {
      for (final PriceData pd : totSpxVol.getPriceData()) {
        pw.printf("%s, %.2f%n", pd.date.format("yyyy-MM-dd"), pd.close);
      }
    }

    // **********************************************/

    final List<PriceData> vrList = new ArrayList<>();
    d1 = new DateTime(2010, DateTime.JANUARY, 2);
    fname = "SPY-ETF-Total-Volume.csv";
    try (PrintWriter pw = new PrintWriter(String.format("%sDC\\%s", OptumaCommon.optumaPath, fname))) {
      for (final PriceData pd : spy.getPriceData()) {
        if (pd.date.isGreaterThanOrEqual(d1)) {
          pw.printf("%s, %d%n", pd.date.format("yyyy-MM-dd"), pd.volume);
          final double p1 = totSpxVol.getActualClose(pd.date);
          if (p1 > 0.0) {
            final double d = pd.volume / p1;
            final PriceData tot = new PriceData(pd.date, d, d, d, d, 0);
            vrList.add(tot);
          }

        }
      }
    }

    fname = "SPY-to-SPX-VolumeRatio.csv";
    try (PrintWriter pw = new PrintWriter(String.format("%sDC\\%s", OptumaCommon.optumaPath, fname))) {
      for (final PriceData pd : vrList) {
        pw.printf("%s, %f%n", pd.date.format("yyyy-MM-dd"), pd.close);
      }
    }

  }

}

// The Formula for Arms Index (TRIN) is:
// TRIN = (Advancing Volume/Declining Volume) / (Advancing Stocks/Declining Stocks)

//Ratio-Adjusted Net Advances (RANA): (Advances - Declines)/(Advances +Declines)
//  McClellan Oscillator: 19-day EMA of RANA - 39-day EMA of RANA
//  19-day EMA* = (Current Day RANA - Prior Day EMA) * .10 + Prior Day EMA)
//  39-day EMA* = (Current Day RANA - Prior Day EMA) * .05 + Prior Day EMA)

//McClellan Summation
//Previous day's Summation Index* + current day McClellan Oscillator

//Initial SMA: 10-period sum / 10
//  Multiplier: (2 / (Time periods + 1) ) = (2 / (10 + 1) ) = 0.1818 (18.18%)
//  EMA: {Close - EMA(previous day)} x multiplier + EMA(previous day).
