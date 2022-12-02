package net.ajaskey.covid19;

import net.ajaskey.common.DateTime;

public class PlotData {

  private final DateTime date;
  private int            totalCases;
  private int            totalDeaths;
  private int            totalTests;
  private int            newCases;
  private int            newDeaths;
  private long           population;

  public PlotData(DateTime dt) {
    this.date = new DateTime(dt);
    this.totalCases = 0;
    this.totalDeaths = 0;
    this.totalTests = 0;
    this.newCases = 0;
    this.newDeaths = 0;
    this.population = 0L;
  }

  public PlotData(DateTime dt, int tc, int nc, int td, int nd, int tt, long pop) {
    this.date = new DateTime(dt);
    this.totalCases = tc;
    this.newCases = nc;
    this.totalDeaths = td;
    this.newDeaths = nd;
    this.totalTests = tt;
    this.population = pop;
  }

  public DateTime getDate() {
    return this.date;
  }

  public int getTotalCases() {
    return this.totalCases;
  }

  public int getNewCases() {
    return this.newCases;
  }

  public int getTotalDeaths() {
    return this.totalDeaths;
  }

  public int getNewDeaths() {
    return this.newDeaths;
  }

  public int getTotalTests() {
    return this.totalTests;
  }

  public void incTotalCases(int tc) {
    this.totalCases += tc;
  }

  public void incPopulation(long pop) {
    this.population += pop;
  }

  public void incTotalDeaths(int td) {
    this.totalDeaths += td;
  }

  public void incNewCases(int nc) {
    this.newCases += nc;
  }

  public void incNewDeaths(int nd) {
    this.newDeaths += nd;
  }

  public void incTotalTests(int tt) {
    this.totalTests += tt;
  }

  public void setTotalCases(int tc) {
    this.totalCases = tc;
  }

  public void setTotalDeaths(int td) {
    this.totalDeaths = td;
  }

  public void setTotalTests(int tt) {
    this.totalTests = tt;
  }

  @Override
  public String toString() {
    final String ret = String.format("%s %12d %12d %12d %20L", this.date, this.totalCases, this.totalDeaths, this.totalTests, this.population);
    return ret;
  }

  public long getPopulation() {
    return population;
  }
}
