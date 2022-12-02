package net.ajaskey.market.tools.SIP;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Utilities;

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
public class SipSectorIndustryRS2 {

  private static final double MAX_RS = 80.0;

  private static boolean doDebug = false;

  private final static List<Fields> fieldList = new ArrayList<>();

  public static List<GroupData> sectorList   = new ArrayList<>();
  public static List<GroupData> industryList = new ArrayList<>();

  private static SipSectorIndustryRS2 ssirs = new SipSectorIndustryRS2();

  public static GroupData getSectorGroup(String code) {
    GroupData ret = null;
    for (GroupData gd : sectorList) {
      for (Fields f : gd.list) {
        if (code.equals(f.code)) {
          ret = gd;
          break;
        }
      }
    }
    return ret;
  }

  public static GroupData getIndustryGroup(String code) {
    GroupData ret = null;
    for (GroupData gd : industryList) {
      for (Fields f : gd.list) {
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
   * Data for each individual company line from the input file.
   *
   */
  public class Fields {
    String    code;
    String    industry;
    double    rs1;
    double    rs12;
    double    rs3;
    double    rs6;
    String    sector;
    double    weightedRS;
    GroupData indGroup;

    /**
     *
     * @param ss
     */
    public Fields(String ss) {

      indGroup = null;

      final String fld[] = ss.split(",");
      this.code = fld[0].trim();
      this.sector = Utilities.cleanSecInd(fld[1]);
      this.industry = Utilities.cleanSecInd(fld[2]);

      double tmp = Utils.parseDouble(fld[3].trim());
      this.rs1 = Math.min(tmp, SipSectorIndustryRS2.MAX_RS);
      tmp = Utils.parseDouble(fld[4].trim());
      this.rs3 = Math.min(tmp, SipSectorIndustryRS2.MAX_RS);
      tmp = Utils.parseDouble(fld[5].trim());
      this.rs6 = Math.min(tmp, SipSectorIndustryRS2.MAX_RS);
      tmp = Utils.parseDouble(fld[6].trim());
      this.rs12 = Math.min(tmp, SipSectorIndustryRS2.MAX_RS);

      this.calcRS();
    }

    /**
     * One-third 12m + One-third 6m + 0.255 3m + 0.085 1m
     *
     */
    private void calcRS() {
      this.weightedRS = this.rs12 * 0.33 + this.rs6 * 0.33 + this.rs3 * 0.255 + this.rs1 * 0.085;
    }

    @Override
    public String toString() {
      String ret = this.code;
      ret += String.format("\t%s\t%s\t%.2f", this.industry, this.sector, this.weightedRS);
      ret += String.format("%n\tIndGroup : %s\t%.2f\t%d\t\tHigh RS %s : %.2f\tLow RS %s : %.2f", this.indGroup.groupName, this.indGroup.groupRS,
          this.indGroup.list.size(), this.indGroup.highRs.code, this.indGroup.highRs.weightedRS, this.indGroup.lowRs.code,
          this.indGroup.lowRs.weightedRS);
      for (Fields f : this.indGroup.list) {
        ret += String.format("%n\t\t%s\t%.2f", f.code, f.weightedRS);
      }
      return ret;
    }
  }

  /**
   *
   * Collated data for each Industry and Sector from input data file.
   *
   */
  public class GroupData {

    String       groupName;
    double       groupRS;
    List<Fields> list = null;
    Fields       highRs;
    Fields       lowRs;

    /**
     *
     * @param s
     */
    public GroupData(String s) {
      this.groupName = s;
      this.list = new ArrayList<>();
      this.groupRS = 0.0;
      highRs = null;
      lowRs = null;
    }

    public String getName() {
      return this.groupName;
    }

    public double getRS() {
      return this.groupRS;
    }

    public int getCount() {
      return this.list.size();
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
   * @param industry
   * @return
   */
  private static GroupData getIndustry(String industry) {
    for (final GroupData gd : SipSectorIndustryRS2.industryList) {
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
   * @param industry
   * @return
   */
  public static double getIndustryRS(String industry) {

    double ret = 0.0;
    GroupData gd = getIndustry(industry);
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
  private static GroupData getSector(String sector) {
    for (final GroupData gd : SipSectorIndustryRS2.sectorList) {
      if (gd.groupName.equals(sector)) {
        return gd;
      }
    }
    return null;
  }

  /**
   * 
   * @param sector
   * @return
   */
  public static double getSectorRS(String sector) {

    double ret = 0.0;
    GroupData gd = getSector(sector);
    if (gd != null) {
      ret = gd.groupRS;
    }
    return ret;
  }

  /**
   * Class SipSectorIndustryRS Constructor
   * 
   * @param desc
   * @param fileandpath
   * @param debug
   */
  public SipSectorIndustryRS2(String desc, String fileandpath, boolean debug) {

    doDebug = debug;

    SipSectorIndustryRS2.processSipSectorIndustryData(fileandpath);

    SipSectorIndustryRS2.createLists();

    System.out.printf("%nSipSectorIndustryRS initialized : %s%n Sectors    : %3d%n Industries : %3d%n%n", desc, sectorList.size(),
        industryList.size());

  }

  private SipSectorIndustryRS2() {

  }

  /**
   *
   * @param args
   */
  public static void main(String[] args) {

//    int year = 2022;
//    int quarter = 3;
//    List<CompanyDerived> dRList = Scans.findMajor(year, quarter, 2.0, 5000L);

    SipSectorIndustryRS2 init = new SipSectorIndustryRS2(" SipSectorIndustryRS ", "input/CODESECTORINDUSTRY-SIP.txt", true);

    writeIndustryWatchlistForOptuma();

    GroupData gd = getIndustry("Semiconductors");

    if (gd != null) {
      System.out.printf("%n%n%s\t%.2f%n", gd.groupName, gd.groupRS);
    }

  }

  private static void writeIndustryWatchlistForOptuma() {
    try (PrintWriter pw = new PrintWriter("out/SipIndustry-watchlist.csv")) {
      for (GroupData gd : industryList) {
        Fields f = gd.highRs;
        pw.printf("%s, %s, %s, %.2f%n", f.code, gd.groupName, getSectorGroup(f.code).groupName, gd.groupRS);
      }
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * Local lists
   */
  static List<String> usList = null;
  static List<String> uiList = null;

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
    Collections.sort(usList);
    for (final String s : usList) {
      final GroupData gd = SipSectorIndustryRS2.ssirs.new GroupData(s);
      for (Fields f : fieldList) {
        if (f.sector.equals(s)) {
          gd.list.add(f);
        }
      }
      gd.calcGroupRS();
      SipSectorIndustryRS2.sectorList.add(gd);
      if (doDebug) {
        System.out.printf("%s : %.2f :: %d%n", s, gd.groupRS, gd.list.size());
      }
    }

    System.out.printf("%n%n%n");

    /**
     * Create a list of Industries
     */
    Collections.sort(uiList);
    for (final String s : uiList) {
      final GroupData gd = SipSectorIndustryRS2.ssirs.new GroupData(s);
      for (Fields f : fieldList) {
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
      SipSectorIndustryRS2.industryList.add(gd);
      if (doDebug) {
        System.out.printf("%s : %.2f :: %d%n", s, gd.groupRS, gd.list.size());
      }
    }

    if (doDebug) {
      System.out.printf("%nSectors    : %d%n", SipSectorIndustryRS2.sectorList.size());
      System.out.printf("Industries : %d%n", SipSectorIndustryRS2.industryList.size());
    }

    /**
     * Debugging output of Fields list
     */
    try (PrintWriter pwDbg = new PrintWriter("debug/ssi.dbg"); PrintWriter pwFields = new PrintWriter("out/SipCodes.csv")) {
      for (Fields f : fieldList) {
        pwDbg.println(f);
        pwFields.printf("%s%n", f.code);
      }
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fills:
   * 
   * fieldList with contents of SIP data file.
   * 
   * usList with a set of individual sectors.
   * 
   * uiList with a set of individual industries.
   */
  private static void processSipSectorIndustryData(String fileandpath) {

    Set<String> uniqSectors = new HashSet<>();
    Set<String> uniqIndustries = new HashSet<>();

    final List<String> data = TextUtils.readTextFile(fileandpath, true);

    /**
     * The pw file duplicates the input for debug purposes.
     */
    try (PrintWriter pw = new PrintWriter("debug/SipSectorIndustryRS.csv")) {
      pw.println("code, sector, industry, rs1, rs3, rs6, rs12, weightedRS");
      for (final String s : data) {
        final String ss = s.replaceAll(",", ";").replaceAll("\t", ", ").replaceAll("\"", "");

        final Fields fld = SipSectorIndustryRS2.ssirs.new Fields(ss);

        if (fld.industry.trim().length() > 0) {
          SipSectorIndustryRS2.fieldList.add(fld);
          uniqSectors.add(fld.sector);
          uniqIndustries.add(fld.industry);

          pw.printf("%s, %s, %s, %.2f, %.2f, %.2f, %.2f, %.4f%n", fld.code, fld.sector, fld.industry, fld.rs1, fld.rs3, fld.rs6, fld.rs12,
              fld.weightedRS);
        }
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

    SipSectorIndustryRS2.usList = new ArrayList<>(uniqSectors);
    SipSectorIndustryRS2.uiList = new ArrayList<>(uniqIndustries);

  }

}
