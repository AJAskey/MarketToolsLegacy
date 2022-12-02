package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.market.tools.SIP.SipUtils;
import net.ajaskey.market.tools.SIP.BigDB.SipStatistics;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;

public class GroupSalesNetQtrRpt {

  /**
   *
   * @author AJAskey
   *
   */
  public class TotalDataQtr {

    final static int acount = 8;

    final static int Q1 = 0;
    final static int Q2 = 1;
    final static int Q3 = 2;
    final static int Q4 = 3;
    final static int Q5 = 4;
    final static int Q6 = 5;
    final static int Q7 = 6;
    final static int Q8 = 7;

    String          group;
    String          sector;
    int             knt;
    SipStatistics[] shareStats;
    SipStatistics[] salesStats;
    SipStatistics[] netIncomeStats;
    double          estimatedNetIncome;
    long            estimateShares;

    List<CompanyDerived> companyList = new ArrayList<>();

    /**
     *
     * @param g
     */
    public TotalDataQtr(String g) {
      this.group = g;
      this.sector = "";
      this.knt = 0;
      this.estimatedNetIncome = 0.0;
      this.estimateShares = 0L;
      this.salesStats = new SipStatistics[TotalDataQtr.acount];
      this.netIncomeStats = new SipStatistics[TotalDataQtr.acount];
      this.shareStats = new SipStatistics[TotalDataQtr.acount];
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        this.salesStats[i] = new SipStatistics(String.format("%s : Sales : Q%d", g, i + 1));
        this.netIncomeStats[i] = new SipStatistics(String.format("%s : Net Earnings : Q%d", g, i + 1));
        this.shareStats[i] = new SipStatistics(String.format("%s : Shares : Q%d", g, i + 1));
      }
    }

    public TotalDataQtr(String g, String s) {
      this.group = g;
      this.sector = s;
      this.knt = 0;
      this.estimatedNetIncome = 0.0;
      this.estimateShares = 0L;
      this.salesStats = new SipStatistics[TotalDataQtr.acount];
      this.netIncomeStats = new SipStatistics[TotalDataQtr.acount];
      this.shareStats = new SipStatistics[TotalDataQtr.acount];
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        this.salesStats[i] = new SipStatistics(String.format("%s : Sales : Q%d", g, i));
        this.netIncomeStats[i] = new SipStatistics(String.format("%s : Net Earnings : Q%d", g, i));
        this.shareStats[i] = new SipStatistics(String.format("%s : Shares : Q%d", g, i));
      }
    }

    /**
     *
     * @param cdr
     */
    public void add(CompanyDerived cdr) {
      this.knt++;
      this.companyList.add(cdr);

      this.shareStats[TotalDataQtr.Q1].add(cdr.getFd().getShareData().getSharesQtr()[1]);
      this.shareStats[TotalDataQtr.Q2].add(cdr.getFd().getShareData().getSharesQtr()[2]);
      this.shareStats[TotalDataQtr.Q3].add(cdr.getFd().getShareData().getSharesQtr()[3]);
      this.shareStats[TotalDataQtr.Q4].add(cdr.getFd().getShareData().getSharesQtr()[4]);
      this.shareStats[TotalDataQtr.Q5].add(cdr.getFd().getShareData().getSharesQtr()[5]);
      this.shareStats[TotalDataQtr.Q6].add(cdr.getFd().getShareData().getSharesQtr()[6]);
      this.shareStats[TotalDataQtr.Q7].add(cdr.getFd().getShareData().getSharesQtr()[7]);
      this.shareStats[TotalDataQtr.Q8].add(cdr.getFd().getShareData().getSharesQtr()[8]);

      this.salesStats[TotalDataQtr.Q1].add(cdr.getFd().getIncSheetData().getSalesQtr()[1]);
      this.salesStats[TotalDataQtr.Q2].add(cdr.getFd().getIncSheetData().getSalesQtr()[2]);
      this.salesStats[TotalDataQtr.Q3].add(cdr.getFd().getIncSheetData().getSalesQtr()[3]);
      this.salesStats[TotalDataQtr.Q4].add(cdr.getFd().getIncSheetData().getSalesQtr()[4]);
      this.salesStats[TotalDataQtr.Q5].add(cdr.getFd().getIncSheetData().getSalesQtr()[5]);
      this.salesStats[TotalDataQtr.Q6].add(cdr.getFd().getIncSheetData().getSalesQtr()[6]);
      this.salesStats[TotalDataQtr.Q7].add(cdr.getFd().getIncSheetData().getSalesQtr()[7]);
      this.salesStats[TotalDataQtr.Q8].add(cdr.getFd().getIncSheetData().getSalesQtr()[8]);

      this.netIncomeStats[TotalDataQtr.Q1].add(cdr.getFd().getIncSheetData().getNetIncQtr()[1]);
      this.netIncomeStats[TotalDataQtr.Q2].add(cdr.getFd().getIncSheetData().getNetIncQtr()[2]);
      this.netIncomeStats[TotalDataQtr.Q3].add(cdr.getFd().getIncSheetData().getNetIncQtr()[3]);
      this.netIncomeStats[TotalDataQtr.Q4].add(cdr.getFd().getIncSheetData().getNetIncQtr()[4]);
      this.netIncomeStats[TotalDataQtr.Q5].add(cdr.getFd().getIncSheetData().getNetIncQtr()[5]);
      this.netIncomeStats[TotalDataQtr.Q6].add(cdr.getFd().getIncSheetData().getNetIncQtr()[6]);
      this.netIncomeStats[TotalDataQtr.Q7].add(cdr.getFd().getIncSheetData().getNetIncQtr()[7]);
      this.netIncomeStats[TotalDataQtr.Q8].add(cdr.getFd().getIncSheetData().getNetIncQtr()[8]);

      final double est = cdr.getFd().getEstimateData().getEpsQ1();
      if (Math.abs(est) > 0.0) {
        this.estimatedNetIncome += est * cdr.getFd().getShareData().getSharesQtr()[1];
        this.estimateShares += (long) cdr.getFd().getShareData().getSharesQtr()[1];
      }
    }

    /**
     *
     * @param yr
     * @return
     */
    public SipStatistics getNetIncomeStats(int qtr) {
      return this.netIncomeStats[qtr];
    }

    /**
     *
     * @param yr
     * @return
     */
    public SipStatistics getSaleStats(int qtr) {
      return this.salesStats[qtr];
    }

    /**
     *
     * @param yr
     * @return
     */
    public SipStatistics getShareStats(int qtr) {
      return this.shareStats[qtr];
    }

    @Override
    public String toString() {
      String ret = String.format("Group : %s with %d companies.%n", this.group, this.knt);
      ret += this.shareStats[TotalDataQtr.Q1] + Utils.NL + this.shareStats[TotalDataQtr.Q2] + Utils.NL + this.shareStats[TotalDataQtr.Q3] + Utils.NL
          + this.shareStats[TotalDataQtr.Q4] + Utils.NL + this.shareStats[TotalDataQtr.Q5] + Utils.NL + this.shareStats[TotalDataQtr.Q6] + Utils.NL
          + this.shareStats[TotalDataQtr.Q7] + Utils.NL + this.shareStats[TotalDataQtr.Q8] + Utils.NL;

      ret += "-------------" + Utils.NL;

      ret += this.salesStats[TotalDataQtr.Q1] + Utils.NL + this.salesStats[TotalDataQtr.Q2] + Utils.NL + this.salesStats[TotalDataQtr.Q3] + Utils.NL
          + this.salesStats[TotalDataQtr.Q4] + Utils.NL + this.salesStats[TotalDataQtr.Q5] + Utils.NL + this.salesStats[TotalDataQtr.Q6] + Utils.NL
          + this.salesStats[TotalDataQtr.Q7] + Utils.NL + this.salesStats[TotalDataQtr.Q8] + Utils.NL;

      ret += "-------------" + Utils.NL;

      ret += this.netIncomeStats[TotalDataQtr.Q1] + Utils.NL + this.netIncomeStats[TotalDataQtr.Q2] + Utils.NL + this.netIncomeStats[TotalDataQtr.Q3]
          + Utils.NL + this.netIncomeStats[TotalDataQtr.Q4] + Utils.NL + this.netIncomeStats[TotalDataQtr.Q5] + Utils.NL
          + this.netIncomeStats[TotalDataQtr.Q6] + Utils.NL + this.netIncomeStats[TotalDataQtr.Q7] + Utils.NL + this.netIncomeStats[TotalDataQtr.Q8]
          + Utils.NL;

      ret += "-------------" + Utils.NL;
      ret += String.format("EstimatedNetIncome : %f%n", this.estimatedNetIncome);
      ret += String.format("EstimatedShares    : %d%n", this.estimateShares);

      return ret;
    }
  }

  /**
   */
  static GroupSalesNetQtrRpt gsnr      = new GroupSalesNetQtrRpt();
  static List<TotalDataQtr>  tdSecList = new ArrayList<>();
  static List<TotalDataQtr>  tdIndList = new ArrayList<>();

  /***************************************************************************************************************************
   *
   * @param args
   */
  public static void main(String[] args) {

    final int year = 2022;
    final int quarter = 4;

    GroupSalesNetQtrRpt.ProcessSpx(year, quarter);
    // GroupSalesNetQtrRpt.ProcessSml(year, quarter);
    GroupSalesNetQtrRpt.notSpxProcessing(year, quarter);
    GroupSalesNetQtrRpt.allSectorProcessing(year, quarter);
    GroupSalesNetQtrRpt.ProcessSemiconductors(year, quarter);
    GroupSalesNetQtrRpt.ProcessRetail(year, quarter);
    // GroupSalesNetQtrRpt.ProcessAutos(year, quarter);
    // GroupSalesNetQtrRpt.ProcessBanks(year, quarter);
    // GroupSalesNetQtrRpt.ProcessBiotech(year, quarter);
    // GroupSalesNetQtrRpt.ProcessPharm(year, quarter);
    //
    // GroupSalesNetQtrRpt.allIndustryProcessing(year, quarter);

  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void ProcessSpx(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findSpx(year, quarter);

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(cdrList, "SPX");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
    GroupSalesNetQtrRpt.generateDebugReport(td);

  }

  public static void notSpxProcessing(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 100000L);

    final List<CompanyDerived> notSpxList = new ArrayList<>();

    for (CompanyDerived cdr : cdrList) {
      String str = cdr.getFd().getCompanyInfo().getSnpIndexStr();
      System.out.println(str);
      if (!str.equalsIgnoreCase("SP500")) {
        notSpxList.add(cdr);
      }
    }

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(notSpxList, "Over $10");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
    GroupSalesNetQtrRpt.generateDebugReport(td);

  }

  /**
   *
   * @param lst
   * @param grp
   * @return
   */
  private static TotalDataQtr calculateData(List<CompanyDerived> lst, String grp) {
    final TotalDataQtr ret = GroupSalesNetQtrRpt.gsnr.new TotalDataQtr(grp);
    for (final CompanyDerived cdr : lst) {
      ret.add(cdr);
    }
    return ret;
  }

  /**
   *
   * @param td
   */
  private static void generateDebugReport(TotalDataQtr td) {
    final String fname = String.format("sipout/salesnet/q/%s_debug.txt", td.group);
    try (PrintWriter pw = new PrintWriter(fname)) {
      pw.println(td);
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param td
   */
  private static void generateReport(TotalDataQtr td, String dir) {
    final String fname = String.format("%s/%s.csv", dir, td.group);
    try (PrintWriter pw = new PrintWriter(fname)) {
      final DateTime now = new DateTime();
      final SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy HH:mm");
      now.setSdf(sdf);
      pw.printf("Group : %s  ::  Companies : %d,,,,,,,,,%s%n", td.group, td.knt, now);
      // pw.println(",2022,2021,2020,2019,2018,2017");
      pw.println(",Q1,Q2,Q3,Q4,Q5,Q6,Q7,Q8");

      pw.printf("Count");
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        pw.printf(",%d", td.shareStats[i].getN());
      }
      pw.printf("%nAvg Shares (M)");
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        pw.printf(",%f", td.shareStats[i].getMean());
      }
      pw.printf("%nShares (M)");
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        pw.printf(",%f", td.shareStats[i].getSum());
      }
      pw.printf("%nSales ($M)");
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        pw.printf(",%f", td.salesStats[i].getSum());
      }
      pw.printf("%nNet Income ($M)");
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        pw.printf(",%f", td.netIncomeStats[i].getSum());
      }
      pw.printf("%nSales Per Share");
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        final double tmp = td.salesStats[i].getSum() / td.shareStats[i].getSum();
        pw.printf(",%f", tmp);
      }
      pw.printf("%nNet Income Per Share");
      for (int i = 0; i < TotalDataQtr.acount; i++) {
        final double tmp = td.netIncomeStats[i].getSum() / td.shareStats[i].getSum();
        pw.printf(",%f", tmp);
      }
      final double tmp = td.estimatedNetIncome / (double) td.estimateShares;
      pw.printf(",,%f, %d, %f%n", td.estimatedNetIncome, td.estimateShares, tmp);
      pw.println();

    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void allIndustryProcessing(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<String> industryList = SipUtils.getIndustryList(cdrList);
    System.out.println(industryList.size());

    System.out.println();

    List<CompanyDerived> indLst = null;
    for (final String ind : industryList) {
      indLst = SipUtils.getIndustryCompaniesFromList(ind, cdrList);
      final String sec = indLst.get(0).getFd().getSector();
      final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(indLst, ind.replaceAll(",", ";"), sec);
      GroupSalesNetQtrRpt.tdIndList.add(td);
      System.out.printf("%s : %d%n", ind, indLst.size());
    }

    System.out.println();

    try (PrintWriter pw = new PrintWriter("sipout/salesnet/q/industry_fundies_qtr.txt");
        PrintWriter pwcsv = new PrintWriter("sipout/salesnet/q/industry_fundies_qtr.csv")) {
      for (final TotalDataQtr td : GroupSalesNetQtrRpt.tdIndList) {
        GroupSalesNetQtrRpt.generateIndReportTxt(td, pw);
      }
      pwcsv.printf("%n%n%s%n", indShortReport);
    }
    catch (final FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void allSectorProcessing(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<String> sectorList = SipUtils.getSectorList(cdrList);
    System.out.println(sectorList.size());

    System.out.println();

    List<CompanyDerived> secLst = null;
    for (final String sec : sectorList) {
      secLst = SipUtils.getSectorCompaniesFromList(sec, cdrList);
      final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(secLst, sec.replaceAll(",", ";"));
      GroupSalesNetQtrRpt.tdSecList.add(td);
      System.out.printf("%s : %d%n", sec, secLst.size());
    }

    System.out.println();

    for (final TotalDataQtr td : GroupSalesNetQtrRpt.tdSecList) {
      GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
      GroupSalesNetQtrRpt.generateDebugReport(td);
    }

  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void ProcessAutos(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<CompanyDerived> autoList = new ArrayList<>();

    for (final CompanyDerived cdr : cdrList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = GroupSalesNetQtrRpt.getId(ind, 8);
      if (id == 53101010 || id == 53101020 || id == 53403010) {
        autoList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : autoList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(autoList.size());

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(autoList, "Autos");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void ProcessBanks(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<CompanyDerived> bankList = new ArrayList<>();

    for (final CompanyDerived cdr : cdrList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = GroupSalesNetQtrRpt.getId(ind, 8);
      if (id == 55101010) {
        bankList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : bankList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(bankList.size());

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(bankList, "Banks");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void ProcessBiotech(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<CompanyDerived> biotechList = new ArrayList<>();

    for (final CompanyDerived cdr : cdrList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = GroupSalesNetQtrRpt.getId(ind, 8);
      if (id == 56202010) {
        biotechList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : biotechList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(biotechList.size());

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(biotechList, "Biotech");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
    GroupSalesNetQtrRpt.generateDebugReport(td);
  }

  public static void ProcessPharm(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<CompanyDerived> pharmList = new ArrayList<>();

    for (final CompanyDerived cdr : cdrList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = GroupSalesNetQtrRpt.getId(ind, 8);
      if (id == 56201040) {
        pharmList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : pharmList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(pharmList.size());

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(pharmList, "Pharm");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
    GroupSalesNetQtrRpt.generateDebugReport(td);
  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void ProcessRetail(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<CompanyDerived> retailList = new ArrayList<>();

    for (final CompanyDerived cdr : cdrList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = GroupSalesNetQtrRpt.getId(ind, 3);
      if (id == 534) {
        retailList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : retailList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(retailList.size());

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(retailList, "Retail");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
    GroupSalesNetQtrRpt.generateDebugReport(td);
  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void ProcessSemiconductors(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<CompanyDerived> semiList = new ArrayList<>();

    for (final CompanyDerived cdr : cdrList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = GroupSalesNetQtrRpt.getId(ind, 6);
      if (id == 571010) {
        semiList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : semiList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(semiList.size());

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(semiList, "Semiconductors");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
    GroupSalesNetQtrRpt.generateDebugReport(td);
  }

  public static void ProcessSml(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findSml(year, quarter);

    final TotalDataQtr td = GroupSalesNetQtrRpt.calculateData(cdrList, "SML");

    GroupSalesNetQtrRpt.generateReport(td, "sipout/salesnet/q");
    GroupSalesNetQtrRpt.generateDebugReport(td);

  }

  /**
   *
   * @param val
   * @return
   */
  private static double[] calcChange(double[] val) {
    final double[] ret = new double[val.length - 1];
    for (int i = 0; i < ret.length; i++) {
      final double d = Math.abs(val[i + 1]);
      if (d > 0.0) {
        ret[i] = (val[i] - val[i + 1]) / d;
      }
      else {
        ret[i] = 0.0;
      }
    }
    return ret;
  }

  private static TotalDataQtr calculateData(List<CompanyDerived> lst, String grp, String sec) {
    final TotalDataQtr ret = GroupSalesNetQtrRpt.calculateData(lst, grp);
    ret.sector = sec;
    return ret;
  }

  private static String fmtWidth(double dval, int before, int after, int width) {
    final int total = before + after + 1;
    final String sfmt = String.format("%%%d.%df", total, after);
    final String sVal = String.format(sfmt, dval);
    final int wid = Math.max(total, width);
    final String ret = GroupSalesNetQtrRpt.fmtWidth(sVal.trim(), wid);
    return ret;
  }

  private static String fmtWidth(int ival, int width) {
    final String istr = String.format("%d", ival);
    return GroupSalesNetQtrRpt.fmtWidth(istr.trim(), width);
  }

  private static String fmtWidth(String str, int width) {
    final String ifmt = String.format("%%-%ds", width);
    final String ret = String.format(ifmt, str);
    return ret;
  }

  private static String fmtWidthPercent(double dval, int before, int after, int width) {
    final int total = before + after + 2;
    final String sfmt = String.format("%%%d.%df", total, after);
    final double d = dval * 100.0;
    final String sVal = String.format(sfmt, d) + "%";
    final int wid = Math.max(total, width);
    final String ret = GroupSalesNetQtrRpt.fmtWidth(sVal.trim(), wid);
    return ret;
  }

  private static String fmtWidthRight(String str, int width) {
    final String ifmt = String.format("%%%ds", width);
    final String ret = String.format(ifmt, str);
    return ret;
  }

  /**
   *
   * @param td
   * @param dir
   */
  private static void generateIndReport(TotalDataQtr td, PrintWriter pw) {

    final double[] values = new double[TotalDataQtr.acount];

    final DateTime now = new DateTime();
    final SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy HH:mm");
    now.setSdf(sdf);

    pw.printf("%nGroup : %s  ::  Companies : %d   :::   %s%n", td.group, td.knt, td.sector);
    pw.println(",2022,2021,2020,2019,2018,2017,,2023,2022,2021,2020,2019,2018");
    pw.println(",TTM,Y1,Y2,Y3,Y4,Y5,,Est,TTM,Y1,Y2,Y3,Y4");

    pw.printf("Count");
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf(",%d", td.shareStats[i].getN());
    }

    pw.printf("%nAvg Shares (M)");
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf(",%f", td.shareStats[i].getMean());
      values[i] = td.shareStats[i].getMean();
    }
    pw.printf(",,");
    double[] pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nShares (M)");
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf(",%f", td.shareStats[i].getSum());
    }

    pw.printf("%nSales ($M)");
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf(",%f", td.salesStats[i].getSum());
      values[i] = td.salesStats[i].getSum();
    }
    pw.printf(",,");
    pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nNet Income ($M)");
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf(",%f", td.netIncomeStats[i].getSum());
      values[i] = td.netIncomeStats[i].getSum();
    }
    pw.printf(",,");
    pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nSales Per Share");
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      final double tmp = td.salesStats[i].getSum() / td.shareStats[i].getSum();
      pw.printf(",%f", tmp);
      values[i] = tmp;
    }
    pw.printf(",,");
    pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nNet Income Per Share");
    double netIncPerShr1 = 0.0;
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      final double tmp = td.netIncomeStats[i].getSum() / td.shareStats[i].getSum();
      if (i == 0) {
        netIncPerShr1 = tmp;
      }
      pw.printf(",%f", tmp);
      values[i] = tmp;
    }
    final double estNetIncPerShr = td.estimatedNetIncome / td.estimateShares;
    double estNetIncChg = 0.0;
    if (Math.abs(netIncPerShr1) > 0.0) {
      estNetIncChg = (estNetIncPerShr - netIncPerShr1) / Math.abs(netIncPerShr1);
    }
    pw.printf(",,%f", estNetIncChg);

    pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nNet Margin");
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      final double ns = td.salesStats[i].getSum();
      double margin = 0.0;
      if (ns > 0.0) {
        final double ni = td.netIncomeStats[i].getSum();
        margin = ni / ns;
      }
      pw.printf(",%f", margin);
    }

    pw.println();

  }

  static private String indShortReport = "Industry,                                                        SalesPerShareChg,       NetIncomePerShareChg,      2023NetIncChg";

  /**
   *
   * @param td
   * @param pw
   */
  private static void generateIndReportTxt(TotalDataQtr td, PrintWriter pw) {

    final int col1width = 23;
    final String sCol1Pad = GroupSalesNetQtrRpt.fmtWidth(" ", col1width);

    final int coldatawidth = 14;
    final String sColDataPad = GroupSalesNetQtrRpt.fmtWidth(" ", coldatawidth);

    final double[] values = new double[TotalDataQtr.acount];

    final DateTime now = new DateTime();
    final SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy HH:mm");
    now.setSdf(sdf);

    pw.printf("%nGroup : %s  ::  Companies : %d   :::   %s%n", td.group, td.knt, td.sector);
    indShortReport += Utils.NL + fmtWidth(td.group, 70);

    String str = sCol1Pad + GroupSalesNetQtrRpt.fmtWidth("2022", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("2021", coldatawidth)
        + GroupSalesNetQtrRpt.fmtWidth("2020", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("2019", coldatawidth)
        + GroupSalesNetQtrRpt.fmtWidth("2018", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("2017", coldatawidth);
    str += "" + GroupSalesNetQtrRpt.fmtWidth("2023", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("2022", coldatawidth)
        + GroupSalesNetQtrRpt.fmtWidth("2021", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("2020", coldatawidth)
        + GroupSalesNetQtrRpt.fmtWidth("2019", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("2018", coldatawidth);
    pw.println(str);

    str = sCol1Pad + GroupSalesNetQtrRpt.fmtWidth("TTM", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("Y1", coldatawidth)
        + GroupSalesNetQtrRpt.fmtWidth("Y2", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("Y3", coldatawidth)
        + GroupSalesNetQtrRpt.fmtWidth("Y4", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("Y5", coldatawidth) + ""
        + GroupSalesNetQtrRpt.fmtWidth("Est", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("TTM", coldatawidth)
        + GroupSalesNetQtrRpt.fmtWidth("Y1", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("Y2", coldatawidth)
        + GroupSalesNetQtrRpt.fmtWidth("Y3", coldatawidth) + GroupSalesNetQtrRpt.fmtWidth("Y4", coldatawidth);
    pw.println(str);

    pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth("Count", col1width));
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth((int) td.shareStats[i].getN(), coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetQtrRpt.fmtWidth("Avg Shares (M)", col1width));
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth(td.shareStats[i].getMean(), 10, 2, coldatawidth));
      values[i] = td.shareStats[i].getMean();
    }
    pw.printf(sColDataPad);
    double[] pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {

      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidthPercent(d, 10, 2, coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetQtrRpt.fmtWidth("Shares (M)", col1width));
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth(td.shareStats[i].getSum(), 10, 2, coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetQtrRpt.fmtWidth("Sales ($M)", col1width));
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth(td.salesStats[i].getSum(), 10, 2, coldatawidth));
      values[i] = td.salesStats[i].getSum();
    }
    pw.printf(sColDataPad);
    pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidthPercent(d, 10, 2, coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetQtrRpt.fmtWidth("Net Income (M)", col1width));
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth(td.netIncomeStats[i].getSum(), 10, 2, coldatawidth));
      values[i] = td.netIncomeStats[i].getSum();
    }
    pw.printf(sColDataPad);
    pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidthPercent(d, 10, 2, coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetQtrRpt.fmtWidth("Sales Per Share", col1width));
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      final double tmp = td.salesStats[i].getSum() / td.shareStats[i].getSum();
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth(tmp, 10, 2, coldatawidth));
      values[i] = tmp;
    }
    pw.printf(sColDataPad);
    pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidthPercent(d, 10, 2, coldatawidth));
    }
    indShortReport += String.format(",%s", fmtWidth(pctChg[0], 10, 4, 25));

    pw.printf("%n%s", GroupSalesNetQtrRpt.fmtWidth("Net Income Per Share", col1width));
    double netIncPerShr1 = 0.0;
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      final double tmp = td.netIncomeStats[i].getSum() / td.shareStats[i].getSum();
      if (i == 0) {
        netIncPerShr1 = tmp;
      }
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidthPercent(tmp, 10, 2, coldatawidth));
      values[i] = tmp;
    }

    final double estNetIncPerShr = td.estimatedNetIncome / td.estimateShares;
    double estNetIncChg = 0.0;
    if (Math.abs(netIncPerShr1) > 0.0) {
      estNetIncChg = (estNetIncPerShr - netIncPerShr1) / Math.abs(netIncPerShr1);
    }

    pw.printf("%s", GroupSalesNetQtrRpt.fmtWidthPercent(estNetIncChg, 10, 2, coldatawidth));
    pctChg = GroupSalesNetQtrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth(d, 10, 2, coldatawidth));
    }
    indShortReport += String.format(",%s", fmtWidth(pctChg[0], 10, 4, 25));
    indShortReport += String.format(",%s", fmtWidth(estNetIncChg, 10, 4, 25));

    pw.printf("%n%s", GroupSalesNetQtrRpt.fmtWidth("Net Marging", col1width));
    for (int i = 0; i < TotalDataQtr.acount; i++) {
      final double ns = td.salesStats[i].getSum();
      double margin = 0.0;
      if (ns > 0.0) {
        final double ni = td.netIncomeStats[i].getSum();
        margin = ni / ns;
      }
      pw.printf("%s", GroupSalesNetQtrRpt.fmtWidth(margin, 10, 2, coldatawidth));
    }

    pw.println();

  }

  /**
   *
   * @param ind
   * @param len
   * @return
   */
  private static int getId(String ind, int len) {
    final int ret = Integer.parseInt(ind.substring(0, len));
    return ret;
  }

  public double estimatedEps;

}
