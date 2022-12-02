package net.ajaskey.covid19;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;

public class ProcessOurWorldData {

  static List<OurWorldData> owdList = new ArrayList<>();

  static List<String> dList = null;

  static String[] EU = { "Austria", "Italy", "Italy", "Belgium", "Latvia", "Bulgaria", "Lithuania", "Croatia", "Luxembourg", "Cyprus", "Malta",
      "Czechia", "Netherlands", "Denmark", "Poland", "Estonia", "Portugal", "Finland", "Romania", "France", "Slovakia", "Germany", "Slovenia",
      "Greece", "Spain", "Hungary", "Sweden", "Ireland", "United Kingdom" };

  /**
   *
   * @param args
   */
  public static void main(String[] args) {

    dList = TextUtils.readTextFile("data/owid-covid-data.txt", true);
    // final List<String> dList = OurWorldData.downloadLatest();

    System.out.println(dList.get(0));
    for (int i = 2; i < dList.size(); i++) {
      final String str = dList.get(i).trim();
      final OurWorldData owd = new OurWorldData(str);
      if (owd.isValid()) {
        ProcessOurWorldData.owdList.add(owd);
      }
    }

    // processSummary();

    processOptuma();
  }

  /**
   * 
   * @param country
   * @return
   */
  private static boolean isEU(String country) {
    for (String s : EU) {
      if (country.equalsIgnoreCase(s)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   */
  public static void processSummary() {
    DateTime dt = new DateTime();
    dt.add(DateTime.DATE, -1);
    for (OurWorldData owd : owdList) {
      if (owd.getDate().sameDate(dt)) {
        if (isEU(owd.getLocation())) {
          System.out.println(owd.toEUReport());
        }
      }
    }
  }

  public static void processOptuma() {

    // final List<String> dList = OurWorldData.downloadLatest();

    try {

      mergeCurrentUS("data/us-current-covid.txt");

      ProcessOurWorldData.printData("North America", "United States", false);
      ProcessOurWorldData.printData("North America", "Canada", false);
      ProcessOurWorldData.printData("Asia", "India", false);
      ProcessOurWorldData.printData("Europe", "France", false);
      ProcessOurWorldData.printData("Europe", "Germany", false);
      ProcessOurWorldData.printData("Europe", "Italy", false);
      ProcessOurWorldData.printData("Europe", "Austria", false);
      ProcessOurWorldData.printData("Europe", "Bulgaria", false);
      ProcessOurWorldData.printData("Europe", "Switzerland", false);
      ProcessOurWorldData.printData("Europe", "Kosovo", false);
      ProcessOurWorldData.printData("Europe", "Denmark", false);
      ProcessOurWorldData.printData("Europe", "Spain", false);
      ProcessOurWorldData.printData("Europe", "United Kingdom", false);
      ProcessOurWorldData.printData("Europe", "Greece", false);
      ProcessOurWorldData.printData("Europe", "Croatia", false);
      ProcessOurWorldData.printData("Europe", "Hungary", false);
      ProcessOurWorldData.printData("Europe", "Ireland", false);
      ProcessOurWorldData.printData("Europe", "Romania", false);
      ProcessOurWorldData.printData("Europe", "Montenegro", false);
      ProcessOurWorldData.printData("Europe", "Norway", false);
      ProcessOurWorldData.printData("Europe", "Poland", false);
      ProcessOurWorldData.printData("Europe", "Sweden", false);
      ProcessOurWorldData.printData("Europe", "Portugal", false);
      ProcessOurWorldData.printData("Europe", "Russia", false);
      ProcessOurWorldData.printData("Europe", "Ukraine", false);

      ProcessOurWorldData.printData("", "World", false);

      ProcessOurWorldData.printData("North America", "", true);
      ProcessOurWorldData.printData("Europe", "", true);
      ProcessOurWorldData.printData("Africa", "", true);
      ProcessOurWorldData.printData("Asia", "", true);
      ProcessOurWorldData.printData("South America", "", true);
    }
    catch (final FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println("Done.");

  }

  /**
   * 
   * @param fname
   */
  private static void mergeCurrentUS(String fname) {

    List<String> data = TextUtils.readTextFile(fname, true);
    int usCases = Integer.parseInt(data.get(0).trim());
    int usDeaths = Integer.parseInt(data.get(1).trim());
    DateTime today = new DateTime();

    for (OurWorldData owd : owdList) {
      if (owd.getLocation().equalsIgnoreCase("United States")) {
        if (owd.getDate().isEqual(today)) {
          if (usCases > owd.getTotal_cases() && usDeaths > owd.getTotal_deaths()) {
            System.out.println("Using hand entered US cases and deaths data.");
            owd.setTotal_cases(usCases);
            owd.setTotal_deaths(usDeaths);
          }
          return;
        }
      }
    }
  }

  /**
   *
   * @param owdList
   * @return
   */
  private static List<PlotData> combine(List<OurWorldData> owdList) {

    final List<PlotData> pdList = new ArrayList<>();

    final HashSet<String> sDates = new HashSet<>();
    final DateTime earliest = new DateTime();
    // Find earliest date
    for (final OurWorldData owd : owdList) {
      final String s = owd.getDate().format("yyyy-MM-dd");
      sDates.add(s);
      if (owd.getDate().isLessThan(earliest)) {
        earliest.set(owd.getDate());
      }
    }

    final List<String> sDatesList = new ArrayList<>(sDates);
    Collections.sort(sDatesList);

    for (final String sd : sDatesList) {
      final PlotData pd = new PlotData(new DateTime(sd, "yyyy-MM-dd"));
      pdList.add(pd);
    }

    for (final PlotData pd : pdList) {
      for (final OurWorldData owd : owdList) {

        if (owd.getDate().isEqual(pd.getDate())) {
          if (owd.getTotal_cases() > 0) {
            pd.incTotalCases(owd.getTotal_cases());
          }
          if (owd.getTotal_deaths() > 0) {
            pd.incTotalDeaths(owd.getTotal_deaths());
          }
          if (owd.getTotal_tests() > 0) {
            pd.incTotalTests(owd.getTotal_tests());
          }
          if (owd.getPopulation() > 0) {
            pd.incPopulation(owd.getPopulation());
          }

          if (owd.getNew_cases() > 0) {
            pd.incNewCases(owd.getNew_cases());
          }
          if (owd.getNew_deaths() > 0) {
            pd.incNewDeaths(owd.getNew_deaths());
          }
        }
      }
    }

    return pdList;
  }

  /**
   *
   * @param continent
   * @param location
   * @return
   */
  private static List<OurWorldData> get(String continent, String location) {

    final List<OurWorldData> retList = new ArrayList<>();

    for (final OurWorldData owd : ProcessOurWorldData.owdList) {

      if (continent.length() > 0) {
        if (owd.getContinent().equalsIgnoreCase(continent)) {
          if (location.length() > 0) {
            if (owd.getLocation().equalsIgnoreCase(location)) {
              retList.add(owd);
            }
          }
          else {
            retList.add(owd);
          }
        }
      }
      else if (location.length() > 0) {
        if (owd.getLocation().equalsIgnoreCase(location)) {
          retList.add(owd);
        }
      }
    }

    return retList;
  }

  /**
   *
   * @param continent
   * @param location
   * @param combine
   * @throws FileNotFoundException
   */
  private static void printData(String continent, String location, boolean combine) throws FileNotFoundException {

    final List<OurWorldData> newList = ProcessOurWorldData.get(continent, location);

    List<PlotData> plotList = null;
    if (combine) {
      plotList = ProcessOurWorldData.combine(newList);
    }
    else {
      plotList = new ArrayList<>();
      for (final OurWorldData owd : newList) {
        plotList.add(new PlotData(owd.getDate(), owd.getTotal_cases(), owd.getNew_cases(), owd.getTotal_deaths(), owd.getNew_deaths(),
            owd.getTotal_tests(), owd.getPopulation()));
      }
    }

    final String cont = continent.replace(" ", "").trim();
    final String loc = location.replace(" ", "").trim();

    int lastPoint = plotList.size() - 1;
    if (cont.equalsIgnoreCase("EUROPE")) {
      lastPoint--;
    }

    final String path = net.ajaskey.market.optuma.OptumaCommon.optumaPath + "DC\\";
    String fname = String.format("%s_%s_TotalCases.csv", cont, loc);
    String filename = path + fname;
    int lastCaseKnt = 0;
    try (PrintWriter pw = new PrintWriter(filename)) {
      for (int i = 0; i <= lastPoint; i++) {
        final PlotData pd = plotList.get(i);
        if (pd.getTotalCases() > lastCaseKnt) {
          final String s = String.format("%s,%d", pd.getDate().format("yyyy-MM-dd"), pd.getTotalCases());
          pw.println(s);
          lastCaseKnt = pd.getTotalCases();
        }
      }
    }

    fname = String.format("%s_%s_CurrentTests.csv", cont, loc);
    filename = path + fname;
    try (PrintWriter pw = new PrintWriter(filename)) {
      for (int i = 30; i <= lastPoint; i++) {
        final PlotData pd = plotList.get(i);
        final int tt = plotList.get(i).getTotalTests();
        final int tt30 = plotList.get(i - 30).getTotalTests();
        final int ct = tt - tt30;
        if (ct > 1000000) {
          final String s = String.format("%s,%d", pd.getDate().format("yyyy-MM-dd"), ct);
          pw.println(s);
        }
      }
    }

    fname = String.format("%s_%s_CurrentCases.csv", cont, loc);
    filename = path + fname;
    try (PrintWriter pw = new PrintWriter(filename)) {
      for (int i = 30; i <= lastPoint; i++) {
        final PlotData pd = plotList.get(i);
        final int tc = plotList.get(i).getTotalCases();
        final int tc30 = plotList.get(i - 30).getTotalCases();
        final int cc = tc - tc30;
        final String s = String.format("%s,%d", pd.getDate().format("yyyy-MM-dd"), cc);
        pw.println(s);
      }
    }

    fname = String.format("%s_%s_TotalDeaths.csv", cont, loc);
    filename = path + fname;
    try (PrintWriter pw = new PrintWriter(filename)) {
      int lastDeathKnt = 0;
      for (int i = 0; i <= lastPoint; i++) {
        final PlotData pd = plotList.get(i);
        if (pd.getTotalDeaths() > lastDeathKnt) {
          final String s = String.format("%s,%d", pd.getDate().format("yyyy-MM-dd"), pd.getTotalDeaths());
          pw.println(s);
          lastDeathKnt = pd.getTotalDeaths();
        }
      }
    }

    fname = String.format("%s_%s_NewCases.csv", cont, loc);
    filename = path + fname;
    try (PrintWriter pw = new PrintWriter(filename)) {
      for (int i = 0; i <= lastPoint; i++) {
        final PlotData pd = plotList.get(i);
        final String s = String.format("%s,%d", pd.getDate().format("yyyy-MM-dd"), pd.getNewCases());
        pw.println(s);
      }
    }

    fname = String.format("%s_%s_NewDeaths.csv", cont, loc);
    filename = path + fname;
    try (PrintWriter pw = new PrintWriter(filename)) {
      for (int i = 0; i <= lastPoint; i++) {
        final PlotData pd = plotList.get(i);
        final String s = String.format("%s,%d", pd.getDate().format("yyyy-MM-dd"), pd.getNewDeaths());
        pw.println(s);
      }
    }

    fname = String.format("%s_%s_TotalTests.csv", cont, loc);
    filename = path + fname;
    try (PrintWriter pw = new PrintWriter(filename)) {
      int lastTestKnt = 0;
      for (int i = 0; i <= lastPoint; i++) {
        final PlotData pd = plotList.get(i);
        if (pd.getTotalTests() > lastTestKnt) {
          final String s = String.format("%s,%d", pd.getDate().format("yyyy-MM-dd"), pd.getTotalTests());
          pw.println(s);
          lastTestKnt = pd.getTotalTests();
        }
      }
    }

    fname = String.format("%s_%s_CFR.csv", cont, loc);
    filename = path + fname;
    try (PrintWriter pw = new PrintWriter(filename)) {
      for (int i = 0; i <= lastPoint; i++) {
        PlotData pd = plotList.get(i);
        double tc = pd.getTotalCases();
        if (tc > 0) {
          double cfr = (double) pd.getTotalDeaths() / (double) tc * 100.0;
          final String s = String.format("%s,%.2f", pd.getDate().format("yyyy-MM-dd"), cfr);
          pw.println(s);
        }
      }
    }

    fname = String.format("%s_%s_DeathsPerMillion.csv", cont, loc);
    filename = path + fname;
    try (PrintWriter pw = new PrintWriter(filename)) {
      for (int i = 0; i <= lastPoint; i++) {
        PlotData pd = plotList.get(i);
        long pop = pd.getPopulation();
        if (pop > 0) {
          double dpm = (double) pd.getTotalDeaths() / (double) pop * 1000000.0;
          final String s = String.format("%s,%.5f", pd.getDate().format("yyyy-MM-dd"), dpm);
          pw.println(s);
        }
      }
    }

    // Find Real CFR
    double totDeaths = 0.0;
    double totCases = 0.0;
    for (int i = lastPoint - 14 + 1; i <= lastPoint; i++) {
      PlotData pd = plotList.get(i);
      totDeaths += (double) pd.getNewDeaths();
      totCases += (double) (pd.getTotalCases() - plotList.get(i - 1).getTotalCases());
    }
    double lastCFR = totDeaths / totCases;
    System.out.printf("%s %s current CFR = %.2f%n", cont, loc, lastCFR * 100.0);

    fname = String.format("%s_%s_DeathProjection.csv", cont, loc);
    filename = path + fname;
    int timeBack = 35;
    try (PrintWriter pw = new PrintWriter(filename)) {
      for (int i = lastPoint - timeBack + 1; i <= lastPoint; i++) {
        final PlotData pd = plotList.get(i);
        final int tc = plotList.get(i).getTotalCases();
        final int tcBack = plotList.get(i - timeBack).getTotalCases();
        final int cc = tc - tcBack;
        double projDeath = (double) cc * lastCFR / (double) timeBack;
        DateTime projDate = new DateTime(pd.getDate());
        projDate.add(DateTime.DATE, timeBack);
        final String s = String.format("%s,%f", projDate.format("yyyy-MM-dd"), projDeath);
        pw.println(s);
      }
    }
  }

}
