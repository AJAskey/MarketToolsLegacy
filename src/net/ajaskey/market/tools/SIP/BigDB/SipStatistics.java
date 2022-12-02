package net.ajaskey.market.tools.SIP.BigDB;

import net.ajaskey.common.Utils;

public class SipStatistics extends org.apache.commons.math3.stat.descriptive.DescriptiveStatistics {

  private static final long serialVersionUID = 3834547482564759957L;

  private final String description;

  public SipStatistics() {
    this.description = "Undefined";
  }

  public SipStatistics(String d) {
    this.description = d;
  }

  public String getDescription() {
    return this.description;
  }

  /**
   *
   * Utility for easy of use
   *
   * @return
   */
  public double getMedian() {

    return this.getPercentile(50);
  }

  public void add(double d) {
    if (Math.abs(d) > 0.0) {
      this.addValue(d);
    }
  }

//  public void print() {
//    System.out.println(this.toString());
//  }

  @Override
  public String toString() {

    String ret = "Stats for : " + this.description + Utils.NL;
    ret += Utils.TAB + "Count     : " + this.getN() + Utils.NL;
    ret += Utils.TAB + "Median    : " + this.getMedian() + Utils.NL;
    // ret += Utils.TAB + "Geom Mean : " + this.getGeometricMean() + Utils.NL;
    // ret += Utils.TAB + "Quad Mean : " + this.getQuadraticMean() + Utils.NL;
    ret += Utils.TAB + "StdDev    : " + this.getStandardDeviation() + Utils.NL;
    ret += Utils.TAB + "Min       : " + this.getMin() + Utils.NL;
    ret += Utils.TAB + "  -2 StdDev : " + this.getPercentile(5) + Utils.NL;
    ret += Utils.TAB + "  Mean      : " + this.getMean() + Utils.NL;
    ret += Utils.TAB + "  +2 StdDev : " + this.getPercentile(95) + Utils.NL;
    ret += Utils.TAB + "Max       : " + this.getMax() + Utils.NL;
    ret += Utils.TAB + "Kurtosis  : " + this.getKurtosis() + Utils.NL;
    ret += Utils.TAB + "Sum       : " + this.getSum() + Utils.NL;
    return ret;
  }

}
