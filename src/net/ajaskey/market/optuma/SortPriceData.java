package net.ajaskey.market.optuma;

import java.util.Comparator;

public class SortPriceData implements Comparator<PriceData> {

  /*
   * (non-Javadoc)
   *
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(final PriceData pd1, final PriceData pd2) {

    int ret = 0;
    if (pd1.isValid() && pd2.isValid()) {
      if (pd1.date.isGreaterThan(pd2.date)) {
        ret = 1;
      }
      else if (pd1.date.isLessThan(pd2.date)) {
        ret = -1;
      }
    }
    return ret;
  }
}
