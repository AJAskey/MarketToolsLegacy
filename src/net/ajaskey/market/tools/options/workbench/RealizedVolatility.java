package net.ajaskey.market.tools.options.workbench;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import net.ajaskey.common.Utils;
import net.ajaskey.market.optuma.TickerPriceData;

public class RealizedVolatility {

  /**
   *
   * @param args
   */
  public static void main(String[] args) {

    final String exch = "AMEX";
    final String ticker = "SPY";

    final RealizedVolatility rv = new RealizedVolatility(exch, ticker, 20, 25.0);

    System.out.println(Utils.NL + rv);

  }

  /**
   *
   * @param now
   * @param previous
   * @return
   */
  private static double change(double now, double previous) {
    double ret = 0.0;
    if (previous != 0.0) {
      ret = (now - previous) / previous;
    }
    return ret;
  }

  private double                      annualVol;
  private final DescriptiveStatistics ds;
  private final double                iv;
  private double                      oneDayVol;
  private double                      premium;
  private final int                   td;
  private String                      ticker;
  private final double                timeFactor;

  /**
   *
   * @param exch
   * @param ticker
   * @param td
   * @return
   */
  public RealizedVolatility(String exch, String ticker, int td, double iv) {

    this.td = td;
    this.iv = iv;
    this.premium = 0.0;
    this.timeFactor = 252.0 / td;

    final TickerPriceData spyData = new TickerPriceData(exch, ticker);

    this.ds = new DescriptiveStatistics();

    final int last = spyData.getPriceData().size() - 1;
    final int minVals = Math.max(100, td + 1);
    if (last > minVals) {
      final int first = last - td + 1;
      int knt = 0;
      for (int i = first; i <= last; i++) {
        final double chg = RealizedVolatility.change(spyData.getPriceData().get(i).close, spyData.getPriceData().get(i - 1).close);
        this.ds.addValue(chg);
        knt++;
        System.out.printf("knt=%3d, chg=%6.2f%% %n", knt, chg * 100.0);
      }

      this.oneDayVol = this.ds.getStandardDeviation() * 100.0;
      this.annualVol = this.oneDayVol * timeFactor;
      if (this.annualVol != 0.0) {
        this.premium = (this.iv / this.annualVol - 1.0) * 100.0;
      }
      this.ticker = ticker;
    }

  }

  public double getAnnualVol() {
    return this.annualVol;
  }

  public DescriptiveStatistics getDs() {
    return this.ds;
  }

  public double getIv() {
    return this.iv;
  }

  public double getOneDayVol() {
    return this.oneDayVol;
  }

  public double getPremium() {
    return this.premium;
  }

  public int getTd() {
    return this.td;
  }

  public String getTicker() {
    return this.ticker;
  }

  public double getTimeFactor() {
    return this.timeFactor;
  }

  @Override
  public String toString() {
    final String ret = String.format(
        "%s%n Trading Days : %d -- %.2f%n Daily RVol   : %5.2f%%%n Yearly RVol  : %5.2f%%%n IV           : %.2f%%%n Premium      : %4.2f%%",
        this.ticker, this.td, this.timeFactor, this.oneDayVol, this.annualVol, this.iv, this.premium);
    return ret;
  }

}
