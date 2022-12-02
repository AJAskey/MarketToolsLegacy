package net.ajaskey.market.optuma;

public class PriceDerived extends TickerPriceData {

  private double dma5;
  private double dma20;
  private double dma50;
  private double dma150;

  public PriceDerived(String exch, String code) {

    super(exch, code);

    dma5 = getMa(5);
    dma5 = getMa(20);
    dma5 = getMa(50);
    dma5 = getMa(150);
  }

  public double getDma5() {
    return dma5;
  }

  public double getDma20() {
    return dma20;
  }

  public double getDma50() {
    return dma50;
  }

  public double getDma150() {
    return dma150;
  }

  public double getMa(int duration) {
    return getMa(duration, 0);
  }

  public double getMa(int duration, int offset) {
    double ret = 0.0;
    int len = this.getNumPrices() - offset;
    if (len > duration) {
      double tot = 0.0;
      int start = offset;
      int stop = offset + duration;
      for (int i = start; i > stop; i--) {
        tot += this.tickerPrices.get(i).close;
      }
      ret = tot / (double) duration;
    }
    return ret;
  }

  public boolean isUp(int duration) {
    boolean ret = false;
    int len = duration * 2 + 1;
    if (this.getNumPrices() > len) {
      double d5 = getMa(5);
      ret = true;
    }
    return ret;
  }

}
