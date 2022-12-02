package net.ajaskey.market.tools.SIP;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ajaskey.market.tools.SIP.BigDB.collation.CompanySummary;

/**
 *
 * @author Andy Askey
 *
 *         Read "input/CODESECTORINDUSTRY-SIP.TXT" and Create fieldList of all
 *         individual data records
 *
 *         Create a unique list of Sectors (available externally)
 *
 *         Create a unique list of Industries (available externally)
 *
 *         Write "out/SipSectorIndustryRS.csv" containing code, sector,
 *         industry, 4xRS, calculated RS [for debug or use by a separate
 *         process]
 *
 *         In processing code, instantiate SipSectorIndustryRS to initialize.
 *         Use public calls and data structures.
 *
 *         SipSectorIndustryRS.sectorList, SipSectorIndustryRS.industryList(),
 *         SipSectorIndustryRS.getSector(String sec),
 *         SipSectorIndustryRS.getIndustry(String ind), GroupData.getName and
 *         GroupData.calcGroupRS as needed.
 *
 */
public class SipSectorIndustryRS {

  /**
   *
   * Collated data for each Industry and Sector from input data file.
   *
   */
  public class GroupData {

    private final String groupName;
    private double       groupRS;
    private Fields       highRs;
    private List<Fields> list = null;
    private Fields       lowRs;

    /**
     *
     * @param s
     */
    public GroupData(String s) {
      this.groupName = s;
      this.list = new ArrayList<>();
      this.groupRS = 0.0;
      this.highRs = null;
      this.lowRs = null;
    }

    public int getCount() {
      return this.list.size();
    }

    public String getName() {
      return this.groupName;
    }

    public double getRS() {
      return this.groupRS;
    }

    /**
     * Average all in the group
     */
    private void calcGroupRS() {
      if (this.list.size() > 0) {
        double grs = 0.0;
        for (final Fields f : this.list) {
          grs += f.weightedRS;
        }
        this.groupRS = grs / this.list.size();
      }
    }
  }

  /**
   *
   * Data for each individual company line from the input file.
   *
   */
  private class Fields {
    String    code;
    GroupData indGroup;
    String    industry;
    double    rs1;
    double    rs12;
    double    rs3;
    double    rs6;
    String    sector;
    double    weightedRS;

    /**
     *
     * @param ss
     */
    public Fields(CompanySummary cs) {

      this.indGroup = null;

      this.code = cs.ticker;
      this.sector = cs.sector;
      this.industry = cs.industry;

      this.rs1 = cs.rs1;
      this.rs3 = cs.rs3;
      this.rs6 = cs.rs6;
      this.rs12 = cs.rs12;

      this.calcRS();
    }

    @Override
    public String toString() {
      String ret = this.code;
      ret += String.format("\t%s\t%s\t%.2f", this.industry, this.sector, this.weightedRS);
      ret += String.format("%n\tIndGroup : %s\t%.2f\t%d\t\tHigh RS %s : %.2f\tLow RS %s : %.2f", this.indGroup.groupName, this.indGroup.groupRS,
          this.indGroup.list.size(), this.indGroup.highRs.code, this.indGroup.highRs.weightedRS, this.indGroup.lowRs.code,
          this.indGroup.lowRs.weightedRS);
      for (final Fields f : this.indGroup.list) {
        ret += String.format("%n\t\t%s\t%.2f", f.code, f.weightedRS);
      }
      return ret;
    }

    /**
     * One-third 12m + One-third 6m + (75% of .34) 3m + (25% of .34) 1m
     *
     */
    private void calcRS() {
      this.weightedRS = this.rs12 * 0.33 + this.rs6 * 0.33 + this.rs3 * 0.255 + this.rs1 * 0.085;
    }
  }

  private static boolean            doDebug      = false;
  private final static List<Fields> fieldList    = new ArrayList<>();
  private static List<GroupData>    industryList = new ArrayList<>();

  private static List<GroupData> sectorList = new ArrayList<>();

  private static SipSectorIndustryRS ssirs = new SipSectorIndustryRS();

  private static List<String> uiList = null;

  /**
   * Local lists
   */
  private static List<String> usList = null;

  /**
   *
   * @param industry
   * @return
   */
  public static double getIndustryRS(String industry) {

    double ret = 0.0;
    final GroupData gd = SipSectorIndustryRS.getIndustry(industry);
    if (gd != null) {
      ret = gd.groupRS;
    }
    return ret;
  }

  /**
   *
   * @param sector
   * @return
   */
  public static double getSectorRS(String sector) {

    double ret = 0.0;
    final GroupData gd = SipSectorIndustryRS.getSector(sector);
    if (gd != null) {
      ret = gd.groupRS;
    }
    return ret;
  }

  /**
   *
   * @param args
   */
  public static void main(String[] args) {

    final int year = 2022;
    final int quarter = 3;

    initialize(" SipSectorIndustryRS ", year, quarter, true);

    SipSectorIndustryRS.writeIndustryWatchlistForOptuma();

    final GroupData gd = SipSectorIndustryRS.getIndustry("Semiconductors");

    if (gd != null) {
      System.out.printf("%n%n%s\t%.2f%n", gd.groupName, gd.groupRS);
    }

  }

  /**
   * Creates.
   *
   * A GroupData list for each individual sector.
   *
   * A GroupData list for each individual industry.
   *
   * Adds companies to lists from fieldLists
   */
  private static void createLists() {

    /**
     * Create a list of Sectors
     */
    Collections.sort(SipSectorIndustryRS.usList);
    for (final String s : SipSectorIndustryRS.usList) {
      final GroupData gd = SipSectorIndustryRS.ssirs.new GroupData(s);
      for (final Fields f : SipSectorIndustryRS.fieldList) {
        if (f.sector.equals(s)) {
          gd.list.add(f);
        }
      }
      gd.calcGroupRS();
      SipSectorIndustryRS.sectorList.add(gd);
      if (SipSectorIndustryRS.doDebug) {
        System.out.printf("%s : %.2f :: %d%n", s, gd.groupRS, gd.list.size());
      }
    }

    System.out.printf("%n%n%n");

    /**
     * Create a list of Industries
     */
    Collections.sort(SipSectorIndustryRS.uiList);
    for (final String s : SipSectorIndustryRS.uiList) {
      final GroupData gd = SipSectorIndustryRS.ssirs.new GroupData(s);
      for (final Fields f : SipSectorIndustryRS.fieldList) {
        if (f.industry.equals(s)) {
          f.indGroup = gd;
          gd.list.add(f);
          if (gd.highRs == null) {
            gd.highRs = f;
          }
          else if (f.weightedRS > gd.highRs.weightedRS) {
            gd.highRs = f;
          }
          if (gd.lowRs == null) {
            gd.lowRs = f;
          }
          else if (f.weightedRS < gd.lowRs.weightedRS) {
            gd.lowRs = f;
          }
        }
      }
      gd.calcGroupRS();
      SipSectorIndustryRS.industryList.add(gd);
      if (SipSectorIndustryRS.doDebug) {
        System.out.printf("%s : %.2f :: %d%n", s, gd.groupRS, gd.list.size());
      }
    }

    if (SipSectorIndustryRS.doDebug) {
      System.out.printf("%nSectors    : %d%n", SipSectorIndustryRS.sectorList.size());
      System.out.printf("Industries : %d%n", SipSectorIndustryRS.industryList.size());
    }

    /**
     * Debugging output of Fields list
     */
    try (PrintWriter pwDbg = new PrintWriter("debug/ssi.dbg"); PrintWriter pwFields = new PrintWriter("out/SipCodes.csv")) {
      for (final Fields f : SipSectorIndustryRS.fieldList) {
        pwDbg.println(f);
        pwFields.printf("%s%n", f.code);
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param industry
   * @return
   */
  private static GroupData getIndustry(String industry) {
    for (final GroupData gd : SipSectorIndustryRS.industryList) {
      // System.out.printf("%s : %s %n", industry, gd.groupName);
      if (gd.groupName.equals(industry)) {
        if (gd.groupRS == 0.0) {
          gd.calcGroupRS();
        }
        return gd;
      }
    }
    return null;
  }

  /**
   *
   * @param code
   * @return
   */
  private static GroupData getIndustryGroup(String code) {
    GroupData ret = null;
    for (final GroupData gd : SipSectorIndustryRS.industryList) {
      for (final Fields f : gd.list) {
        if (code.equals(f.code)) {
          ret = gd;
          break;
        }
      }
    }
    return ret;
  }

  /**
   *
   * @param sector
   * @return
   */
  private static GroupData getSector(String sector) {
    for (final GroupData gd : SipSectorIndustryRS.sectorList) {
      if (gd.groupName.equals(sector)) {
        return gd;
      }
    }
    return null;
  }

  /**
   *
   * @param code
   * @return
   */
  private static GroupData getSectorGroup(String code) {
    GroupData ret = null;
    for (final GroupData gd : SipSectorIndustryRS.sectorList) {
      for (final Fields f : gd.list) {
        if (code.equals(f.code)) {
          ret = gd;
          break;
        }
      }
    }
    return ret;
  }

  /**
   * Filter duplicate or problematic tickers
   *
   * @param ticker
   * @return
   */
  private static boolean ignoredTicker(String ticker) {
    if (ticker.equalsIgnoreCase("GOOG")) {
      return true;
    }
    return false;
  }

  /**
   * Fills:
   *
   * fieldList with contents of SIP CompanySummary file.
   *
   * usList with a set of individual sectors.
   *
   * uiList with a set of individual industries.
   */
  private static void processSipSectorIndustryData(int year, int quarter) {

    final Set<String> uniqSectors = new HashSet<>();
    final Set<String> uniqIndustries = new HashSet<>();

    final List<CompanySummary> csumList = CompanySummary.getCompanySummary(year, quarter);

    /**
     * The pw file duplicates the input for debug purposes.
     */
    try (PrintWriter pw = new PrintWriter("debug/SipSectorIndustryRS.csv");
        PrintWriter pw1 = new PrintWriter("debug/SipSectorIndustryRS-invalid.csv")) {

      pw.println("code, sector, industry, rs1, rs3, rs6, rs12, weightedRS");
      pw1.println("code, sector, industry, rs1, rs3, rs6, rs12, weightedRS");

      for (final CompanySummary cs : csumList) {

        if (!SipSectorIndustryRS.ignoredTicker(cs.ticker)) {

          final Fields fld = SipSectorIndustryRS.ssirs.new Fields(cs);

          if (cs.isValid()) {

            SipSectorIndustryRS.fieldList.add(fld);
            uniqSectors.add(cs.sector);
            uniqIndustries.add(cs.industry);

            pw.printf("%s, %s, %s, %.2f, %.2f, %.2f, %.2f, %.4f%n", fld.code, fld.sector, fld.industry, fld.rs1, fld.rs3, fld.rs6, fld.rs12,
                fld.weightedRS);
          }
          else {
            pw1.printf("%s, %s, %s, %.2f, %.2f, %.2f, %.2f, %.4f%n", fld.code, fld.sector, fld.industry, fld.rs1, fld.rs3, fld.rs6, fld.rs12,
                fld.weightedRS);
          }

        }
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

    SipSectorIndustryRS.usList = new ArrayList<>(uniqSectors);
    SipSectorIndustryRS.uiList = new ArrayList<>(uniqIndustries);

  }

  public static void writeIndustryWatchlistForOptuma() {
    try (PrintWriter pw = new PrintWriter("out/SipIndustry-watchlist.csv")) {
      for (final GroupData gd : SipSectorIndustryRS.industryList) {
        final Fields f = gd.highRs;
        pw.printf("%s, %s, %s, %.2f%n", f.code, gd.groupName, SipSectorIndustryRS.getSectorGroup(f.code).groupName, gd.groupRS);
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * Read all and setup future calls to getSectorRS(sec) and getIndustryRS(ind)
   * 
   * @param desc
   * @param year
   * @param quarter
   * @param debug
   */
  public static void initialize(String desc, int year, int quarter, boolean debug) {

    SipSectorIndustryRS.doDebug = debug;

    SipSectorIndustryRS.processSipSectorIndustryData(year, quarter);

    SipSectorIndustryRS.createLists();

    System.out.printf("%nSipSectorIndustryRS initialized : %s%n Sectors    : %3d%n Industries : %3d%n%n", desc, SipSectorIndustryRS.sectorList.size(),
        SipSectorIndustryRS.industryList.size());

  }

  private SipSectorIndustryRS() {

  }

}
