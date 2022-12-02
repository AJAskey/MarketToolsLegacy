package net.ajaskey.covid19;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;

public class OurWorldData {

  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");

  private final static int ISO_CODE                           = 0;
  private final static int CONTINENT                          = 1;
  private final static int LOCATION                           = 2;
  private final static int DATE                               = 3;
  private final static int TOTAL_CASES                        = 4;
  private final static int NEW_CASES                          = 5;
  private final static int NEW_CASES_SMOOTHED                 = 6;
  private final static int TOTAL_DEATHS                       = 7;
  private final static int NEW_DEATHS                         = 8;
  private final static int NEW_DEATHS_SMOOTHED                = 9;
  private final static int TOTAL_CASES_PER_MILLION            = 10;
  private final static int NEW_CASES_PER_MILLION              = 11;
  private final static int NEW_CASES_SMOOTHED_PER_MILLION     = 12;
  private final static int TOTAL_DEATHS_PER_MILLION           = 13;
  private final static int NEW_DEATHS_PER_MILLION             = 14;
  private final static int NEW_DEATHS_SMOOTHED_PER_MILLION    = 15;
  private final static int REPRODUCTION_RATE                  = 16;
  private final static int ICU_PATIENTS                       = 17;
  private final static int ICU_PATIENTS_PER_MILLION           = 18;
  private final static int HOSP_PATIENTS                      = 19;
  private final static int HOSP_PATIENTS_PER_MILLION          = 20;
  private final static int WEEKLY_ICU_ADMISSIONS              = 21;
  private final static int WEEKLY_ICU_ADMISSIONS_PER_MILLION  = 22;
  private final static int WEEKLY_HOSP_ADMISSIONS             = 23;
  private final static int WEEKLY_HOSP_ADMISSIONS_PER_MILLION = 24;
  private final static int NEW_TESTS                          = 25;
  private final static int TOTAL_TESTS                        = 26;
  private final static int TOTAL_TESTS_PER_THOUSAND           = 27;
  private final static int NEW_TESTS_PER_THOUSAND             = 28;
  private final static int NEW_TESTS_SMOOTHED                 = 29;
  private final static int NEW_TESTS_SMOOTHED_PER_THOUSAND    = 30;
  private final static int TESTS_PER_CASE                     = 31;
  private final static int POSITIVE_RATE                      = 32;
  private final static int TESTS_UNITS                        = 33;
  private final static int STRINGENCY_INDEX                   = 34;
  private final static int POPULATION                         = 35;
  private final static int POPULATION_DENSITY                 = 36;
  private final static int MEDIAN_AGE                         = 37;
  private final static int AGED_65_OLDER                      = 38;
  private final static int AGED_70_OLDER                      = 39;
  private final static int GDP_PER_CAPITA                     = 40;
  private final static int EXTREME_POVERTY                    = 41;
  private final static int CARDIOVASC_DEATH_RATE              = 42;
  private final static int DIABETES_PREVALENCE                = 43;
  private final static int FEMALE_SMOKERS                     = 44;
  private final static int MALE_SMOKERS                       = 45;
  private final static int HANDWASHING_FACILITIES             = 46;
  private final static int HOSPITAL_BEDS_PER_THOUSAND         = 47;
  private final static int LIFE_EXPECTANCY                    = 48;
  private final static int HUMAN_DEVELOPMENT_INDEX            = 49;

  private final static int MIN_FLDS = 36;

  /**
   *
   * @return
   */
  public static List<String> downloadLatest() {

    System.out.println("Reading from OurWorldData ... ");

    final List<String> retList = new ArrayList<>();
    final String filename = "https://covid.ourworldindata.org/data/owid-covid-data.csv";

    final String s = Utils.getFromUrl(filename);

    final String fld[] = s.split(Utils.NL);

    for (final String element : fld) {
      final String tfld[] = element.split(",");
      if (tfld.length > OurWorldData.MIN_FLDS) {
        final String f = element.replace(",", "\t").trim();
        retList.add(f);
      }
    }

    return retList;
  }

  /**
   * Query internet for latest data and write to file for later processing.
   * 
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    final List<String> sList = OurWorldData.downloadLatest();

    try (PrintWriter pw = new PrintWriter("data/owid-covid-data.txt")) {
      pw.println(new DateTime().toFullString());
      for (String s : sList) {
        pw.println(s);
      }
    }

    process(null);

    System.out.println("Done.");

  }

  /**
   * 
   * @param sList
   */
  private static void process(List<String> sList) {

    /**
     * Only process for testing
     */
    if (sList == null) {
      return;
    }

    final List<OurWorldData> owdList = new ArrayList<>();
    for (int i = 1; i < sList.size(); i++) {
      final String str = sList.get(i).trim();
      final OurWorldData owd = new OurWorldData(str);
      if (owd.isValid()) {
        owdList.add(owd);
      }
    }
    try (PrintWriter pw = new PrintWriter("out/test-owd.csv")) {
      for (final OurWorldData owd : owdList) {
        System.out.println(owd);
        final String fld[] = owd.raw.split("\t");
        for (int i = 0; i < OurWorldData.MIN_FLDS; i++) {
          pw.printf("%s,", fld[i]);
        }
        pw.printf("%n");
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public String    raw;
  private double   aged_65_older;
  private double   aged_70_older;
  private double   cardiovasc_death_rate;
  private String   continent;
  private DateTime date;
  private double   diabetes_prevalence;
  private double   extreme_poverty;
  private double   female_smokers;
  private double   gdp_per_capita;
  private int      handwashing_facilities;
  private double   hospital_beds_per_thousand;
  private String   iso_code;
  private double   life_expectancy;
  private String   location;
  private double   male_smokers;
  private double   median_age;
  private int      new_cases;
  private double   new_cases_per_million;
  private double   new_cases_smoothed;
  private double   new_cases_smoothed_per_million;
  private int      new_deaths;
  private double   new_deaths_per_million;
  private double   new_deaths_smoothed;
  private double   new_deaths_smoothed_per_million;
  private int      new_tests;
  private double   new_tests_per_thousand;
  private double   new_tests_smoothed;
  private double   new_tests_smoothed_per_thousand;
  private long     population;
  private double   population_density;
  private double   positive_rate;
  private double   stringency_index;
  private double   tests_per_case;
  private String   tests_units;
  private int      total_cases;
  private double   total_cases_per_million;
  private int      total_deaths;
  private double   total_deaths_per_million;
  private int      total_tests;
  private double   total_tests_per_thousand;
  private boolean  valid;

  public void setTotal_cases(int total_cases) {
    this.total_cases = total_cases;
  }

  public void setTotal_deaths(int total_deaths) {
    this.total_deaths = total_deaths;
  }

  /**
   *
   * @param s
   */
  public OurWorldData(String s) {
    final String fld[] = s.trim().split("\t");
    this.valid = false;
    if (fld.length > OurWorldData.MIN_FLDS) {
      this.raw = s.trim();
      this.iso_code = fld[OurWorldData.ISO_CODE].trim();
      this.continent = fld[OurWorldData.CONTINENT].trim();
      this.location = fld[OurWorldData.LOCATION].trim();
      this.date = new DateTime(fld[OurWorldData.DATE].trim(), "yyyy-MM-dd");
      this.date.setSdf(OurWorldData.sdf);
      this.total_cases = this.ParseInteger(fld[OurWorldData.TOTAL_CASES]);
      this.new_cases = this.ParseInteger(fld[OurWorldData.NEW_CASES]);
      this.new_cases_smoothed = this.ParseDouble(fld[OurWorldData.NEW_CASES_SMOOTHED]);
      this.total_deaths = this.ParseInteger(fld[OurWorldData.TOTAL_DEATHS]);
      this.new_deaths = this.ParseInteger(fld[OurWorldData.NEW_DEATHS]);
      this.new_deaths_smoothed = this.ParseDouble(fld[OurWorldData.NEW_DEATHS_SMOOTHED]);
      this.new_tests = this.ParseInteger(fld[OurWorldData.NEW_TESTS]);
      this.total_tests = this.ParseInteger(fld[OurWorldData.TOTAL_TESTS]);
      this.tests_per_case = this.ParseDouble(fld[OurWorldData.TESTS_PER_CASE]);
      this.population = this.ParseLong(fld[OurWorldData.POPULATION]);
      this.valid = true;
    }
  }

  public double getAged_65_older() {
    return this.aged_65_older;
  }

  public double getAged_70_older() {
    return this.aged_70_older;
  }

  public double getCardiovasc_death_rate() {
    return this.cardiovasc_death_rate;
  }

  public String getContinent() {
    return this.continent;
  }

  public DateTime getDate() {
    return this.date;
  }

  public double getDiabetes_prevalence() {
    return this.diabetes_prevalence;
  }

  public double getExtreme_poverty() {
    return this.extreme_poverty;
  }

  public double getFemale_smokers() {
    return this.female_smokers;
  }

  public double getGdp_per_capita() {
    return this.gdp_per_capita;
  }

  public int getHandwashing_facilities() {
    return this.handwashing_facilities;
  }

  public double getHospital_beds_per_thousand() {
    return this.hospital_beds_per_thousand;
  }

  public String getIso_code() {
    return this.iso_code;
  }

  public double getLife_expectancy() {
    return this.life_expectancy;
  }

  public String getLocation() {
    return this.location;
  }

  public double getMale_smokers() {
    return this.male_smokers;
  }

  public double getMedian_age() {
    return this.median_age;
  }

  public int getNew_cases() {
    return this.new_cases;
  }

  public double getNew_cases_per_million() {
    return this.new_cases_per_million;
  }

  public double getNew_cases_smoothed() {
    return this.new_cases_smoothed;
  }

  public double getNew_cases_smoothed_per_million() {
    return this.new_cases_smoothed_per_million;
  }

  public int getNew_deaths() {
    return this.new_deaths;
  }

  public double getNew_deaths_per_million() {
    return this.new_deaths_per_million;
  }

  public double getNew_deaths_smoothed() {
    return this.new_deaths_smoothed;
  }

  public double getNew_deaths_smoothed_per_million() {
    return this.new_deaths_smoothed_per_million;
  }

  public int getNew_tests() {
    return this.new_tests;
  }

  public double getNew_tests_per_thousand() {
    return this.new_tests_per_thousand;
  }

  public double getNew_tests_smoothed() {
    return this.new_tests_smoothed;
  }

  public double getNew_tests_smoothed_per_thousand() {
    return this.new_tests_smoothed_per_thousand;
  }

  public long getPopulation() {
    return this.population;
  }

  public double getPopulation_density() {
    return this.population_density;
  }

  public double getPositive_rate() {
    return this.positive_rate;
  }

  public double getStringency_index() {
    return this.stringency_index;
  }

  public double getTests_per_case() {
    return this.tests_per_case;
  }

  public String getTests_units() {
    return this.tests_units;
  }

  public int getTotal_cases() {
    return this.total_cases;
  }

  public double getTotal_cases_per_million() {
    return this.total_cases_per_million;
  }

  public int getTotal_deaths() {
    return this.total_deaths;
  }

  public double getTotal_deaths_per_million() {
    return this.total_deaths_per_million;
  }

  public int getTotal_tests() {
    return this.total_tests;
  }

  public double getTotal_tests_per_thousand() {
    return this.total_tests_per_thousand;
  }

  public boolean isValid() {
    return this.valid;
  }

  @Override
  public String toString() {
    String ret = "";
    if (this.valid) {
      ret += String.format("%6s %25s %25s%12s%12d%12d%12d%15.3f%12d", this.iso_code, this.continent, this.location, this.date.toString(),
          this.total_cases, this.total_deaths, this.total_tests, this.tests_per_case, this.population);
      // ret += this.raw;
    }
    return ret;
  }

  /**
   *
   * @param s
   * @return
   */
  private double ParseDouble(String s) {
    double ret = 0.0;
    final String ss = s.trim();
    if (ss.length() > 0) {
      try {
        ret = Double.parseDouble(ss);
      }
      catch (final Exception e) {
        ret = 0.0;
      }
    }
    return ret;
  }

  /**
   *
   * @param s
   * @return
   */
  private int ParseInteger(String s) {
    int ret = 0;
    if (s.trim().length() > 0) {
      final String fld[] = s.split("\\.");
      final String ss = fld[0].trim();
      if (ss.length() > 0) {
        try {
          ret = Integer.parseInt(ss);
        }
        catch (final Exception e) {
          ret = 0;
        }
      }
    }
    return ret;
  }

  /**
   * 
   * @param s
   * @return
   */
  private long ParseLong(String s) {
    long ret = 0L;
    if (s.trim().length() > 0) {
      final String fld[] = s.split("\\.");
      final String ss = fld[0].trim();
      if (ss.length() > 0) {
        try {
          ret = Long.parseLong(ss);
        }
        catch (final Exception e) {
          ret = 0L;
        }
      }
    }
    return ret;
  }

  public String toSummary() {
    String ret = String.format("%-25s %-25s %s %12d", continent, location, date.format("yyyy-MMM-dd"), total_deaths);
    return ret;
  }

  public String toEUReport() {
    String ret = String.format("%s,%s,%s,%d,%f,%f,%f,%d", continent, location, date.format("yyyy-MMM-dd"), population, median_age, aged_65_older,
        aged_70_older, total_deaths);
    return ret;
  }

}
