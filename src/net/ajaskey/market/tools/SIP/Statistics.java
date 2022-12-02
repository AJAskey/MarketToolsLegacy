/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Original author : Andy Askey (ajaskey34@gmail.com)
 */

package net.ajaskey.market.tools.SIP;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Statistics {

  final private static String NL  = "\n";
  final private static String TAB = "\t";

  /**
   * net.ajaskey.market.tools.SIP.main
   *
   * @param args
   */
  public static void main(final String[] args) {

    final Statistics stats = new Statistics("atest");
    for (double i = -200.0; i < 100.0; i += 1.5) {
      stats.addValue(i);
    }
    for (double i = 1000.0; i < 2000.0; i += .255) {
      stats.addValue(i);
    }
    stats.addValue(5000.0);
    stats.addValue(0.0);

    System.out.println(stats.ds.getMin());
    System.out.println(stats.ds.getMax());
    System.out.println(stats.ds.getStandardDeviation());
    System.out.println(stats.getN());
    System.out.println(stats.getMean());
    System.out.println(stats.getMedian());

  }

  public String name;

  private final DescriptiveStatistics ds;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public Statistics(final String n) {

    this.ds = new DescriptiveStatistics();
    this.name = n;
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.addValue
   *
   * @param d
   */
  public void addValue(final double d) {

    if (d != 0.0) {
      this.ds.addValue(d);
    }
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.addValues
   *
   * @param qd
   */
  public void addValues(final QuarterlyData qd) {

    if (qd.q1 != 0.0) {
      this.ds.addValue(qd.q1);
    }
    if (qd.q2 != 0.0) {
      this.ds.addValue(qd.q2);
    }
    if (qd.q3 != 0.0) {
      this.ds.addValue(qd.q3);
    }
    if (qd.q4 != 0.0) {
      this.ds.addValue(qd.q4);
    }
    if (qd.q5 != 0.0) {
      this.ds.addValue(qd.q5);
    }
    if (qd.q6 != 0.0) {
      this.ds.addValue(qd.q6);
    }
    if (qd.q7 != 0.0) {
      this.ds.addValue(qd.q7);
    }
    if (qd.q8 != 0.0) {
      this.ds.addValue(qd.q8);
    }
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getMax
   *
   * @return
   */
  public double getMax() {

    return this.ds.getMax();
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getMean
   *
   * @return
   */
  public double getMean() {

    return this.ds.getMean();
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getMedian
   *
   * @return
   */
  public double getMedian() {

    return this.ds.getPercentile(50);
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getMin
   *
   * @return
   */
  public double getMin() {

    return this.ds.getMin();
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getN
   *
   * @return
   */
  public long getN() {

    return this.ds.getN();
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getStdDev
   *
   * @return
   */
  public double getStdDev() {

    return this.ds.getStandardDeviation();
  }

  /**
   *
   * net.ajaskey.market.tools.SIP.getSum
   *
   * @return
   */
  public double getSum() {

    return this.ds.getSum();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String ret = "Stats for " + this.name + Statistics.NL;
    ret += Statistics.TAB + "Count  : " + this.getN() + Statistics.NL;
    ret += Statistics.TAB + "Mean   : " + QuarterlyData.fmt(this.getMean()) + Statistics.NL;
    ret += Statistics.TAB + "StdDev : " + QuarterlyData.fmt(this.getStdDev()) + Statistics.NL;
    ret += Statistics.TAB + "Min    : " + QuarterlyData.fmt(this.getMin()) + Statistics.NL;
    ret += Statistics.TAB + "  -2 StdDev : " + QuarterlyData.fmt(this.getPercentile(5)) + " (knt=" + (int) (this.getN() * 0.05) + ")" + Statistics.NL;
    ret += Statistics.TAB + "  Median    : " + QuarterlyData.fmt(this.getMedian()) + Statistics.NL;
    ret += Statistics.TAB + "  +2 StdDev : " + QuarterlyData.fmt(this.getPercentile(95)) + Statistics.NL;
    ret += Statistics.TAB + "Max    : " + QuarterlyData.fmt(this.getMax()) + Statistics.NL;
    return ret;
  }

  /**
   * net.ajaskey.market.tools.SIP.getPercentile
   *
   * @param i
   * @return
   */
  private double getPercentile(final int percentile) {

    return this.ds.getPercentile(percentile);
  }
}
