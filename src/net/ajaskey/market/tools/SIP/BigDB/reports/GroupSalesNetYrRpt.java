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

public class GroupSalesNetYrRpt {

  /**
   *
   * @author AJAskey
   *
   */
  public class TotalData {

    final static int acount = 6;

    final static int TTM = 0;
    final static int Y1  = 1;
    final static int Y2  = 2;
    final static int Y3  = 3;
    final static int Y4  = 4;
    final static int Y5  = 5;

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
    public TotalData(String g) {
      this.group = g;
      this.sector = "";
      this.knt = 0;
      this.estimatedNetIncome = 0.0;
      this.estimateShares = 0L;
      this.salesStats = new SipStatistics[TotalData.acount];
      this.netIncomeStats = new SipStatistics[TotalData.acount];
      this.shareStats = new SipStatistics[TotalData.acount];
      for (int i = 0; i < TotalData.acount; i++) {
        this.salesStats[i] = new SipStatistics(String.format("%s : Sales : Y%d", g, i));
        this.netIncomeStats[i] = new SipStatistics(String.format("%s : Net Earnings : Y%d", g, i));
        this.shareStats[i] = new SipStatistics(String.format("%s : Shares : Y%d", g, i));
      }
    }

    public TotalData(String g, String s) {
      this.group = g;
      this.sector = s;
      this.knt = 0;
      this.estimatedNetIncome = 0.0;
      this.estimateShares = 0L;
      this.salesStats = new SipStatistics[TotalData.acount];
      this.netIncomeStats = new SipStatistics[TotalData.acount];
      this.shareStats = new SipStatistics[TotalData.acount];
      for (int i = 0; i < TotalData.acount; i++) {
        this.salesStats[i] = new SipStatistics(String.format("%s : Sales : Y%d", g, i));
        this.netIncomeStats[i] = new SipStatistics(String.format("%s : Net Earnings : Y%d", g, i));
        this.shareStats[i] = new SipStatistics(String.format("%s : Shares : Y%d", g, i));
      }
    }

    /**
     *
     * @param cdr
     */
    public void add(CompanyDerived cdr) {
      this.knt++;
      this.companyList.add(cdr);

      this.shareStats[TotalData.TTM].add(cdr.getFd().getShareData().getSharesQtr()[TotalData.Y1]);
      this.shareStats[TotalData.Y1].add(cdr.getFd().getShareData().getSharesYr()[TotalData.Y1]);
      this.shareStats[TotalData.Y2].add(cdr.getFd().getShareData().getSharesYr()[TotalData.Y2]);
      this.shareStats[TotalData.Y3].add(cdr.getFd().getShareData().getSharesYr()[TotalData.Y3]);
      this.shareStats[TotalData.Y4].add(cdr.getFd().getShareData().getSharesYr()[TotalData.Y4]);
      this.shareStats[TotalData.Y5].add(cdr.getFd().getShareData().getSharesYr()[TotalData.Y5]);

      this.salesStats[TotalData.TTM].add(cdr.getFd().getIncSheetData().getSalesYr()[TotalData.TTM]);
      this.salesStats[TotalData.Y1].add(cdr.getFd().getIncSheetData().getSalesYr()[TotalData.Y1]);
      this.salesStats[TotalData.Y2].add(cdr.getFd().getIncSheetData().getSalesYr()[TotalData.Y2]);
      this.salesStats[TotalData.Y3].add(cdr.getFd().getIncSheetData().getSalesYr()[TotalData.Y3]);
      this.salesStats[TotalData.Y4].add(cdr.getFd().getIncSheetData().getSalesYr()[TotalData.Y4]);
      this.salesStats[TotalData.Y5].add(cdr.getFd().getIncSheetData().getSalesYr()[TotalData.Y5]);

      this.netIncomeStats[TotalData.TTM].add(cdr.getFd().getIncSheetData().getNetIncYr()[TotalData.TTM]);
      this.netIncomeStats[TotalData.Y1].add(cdr.getFd().getIncSheetData().getNetIncYr()[TotalData.Y1]);
      this.netIncomeStats[TotalData.Y2].add(cdr.getFd().getIncSheetData().getNetIncYr()[TotalData.Y2]);
      this.netIncomeStats[TotalData.Y3].add(cdr.getFd().getIncSheetData().getNetIncYr()[TotalData.Y3]);
      this.netIncomeStats[TotalData.Y4].add(cdr.getFd().getIncSheetData().getNetIncYr()[TotalData.Y4]);
      this.netIncomeStats[TotalData.Y5].add(cdr.getFd().getIncSheetData().getNetIncYr()[TotalData.Y5]);

      final double est = cdr.getFd().getEstimateData().getEpsY1();
      if (Math.abs(est) > 0.0) {
        this.estimatedNetIncome += est * cdr.getFd().getShareData().getSharesQtr()[TotalData.Y1];
        this.estimateShares += (long) cdr.getFd().getShareData().getSharesQtr()[TotalData.Y1];
      }
    }

    /**
     *
     * @param yr
     * @return
     */
    public SipStatistics getNetIncomeStats(int yr) {
      return this.netIncomeStats[yr];
    }

    /**
     *
     * @param yr
     * @return
     */
    public SipStatistics getSaleStats(int yr) {
      return this.salesStats[yr];
    }

    /**
     *
     * @param yr
     * @return
     */
    public SipStatistics getShareStats(int yr) {
      return this.shareStats[yr];
    }

    @Override
    public String toString() {
      String ret = String.format("Group : %s with %d companies.%n", this.group, this.knt);
      ret += this.shareStats[TotalData.TTM] + Utils.NL + this.shareStats[TotalData.Y1] + Utils.NL + this.shareStats[TotalData.Y2] + Utils.NL
          + this.shareStats[TotalData.Y3] + Utils.NL + this.shareStats[TotalData.Y4] + Utils.NL + this.shareStats[TotalData.Y5];
      return ret;
    }
  }

  /**
   */
  static GroupSalesNetYrRpt gsnr      = new GroupSalesNetYrRpt();
  static List<TotalData>     tdSecList = new ArrayList<>();
  static List<TotalData>     tdIndList = new ArrayList<>();

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
      final TotalData td = GroupSalesNetYrRpt.calculateData(indLst, ind.replaceAll(",", ";"), sec);
      GroupSalesNetYrRpt.tdIndList.add(td);
      System.out.printf("%s : %d%n", ind, indLst.size());
    }

    System.out.println();

    try (PrintWriter pw = new PrintWriter("sipout/salesnet/industry_fundies.txt");
        PrintWriter pwcsv = new PrintWriter("sipout/industry_fundies.csv")) {
      for (final TotalData td : GroupSalesNetYrRpt.tdIndList) {
        GroupSalesNetYrRpt.generateIndReportTxt(td, pw);
      }
      pwcsv.printf("%n%n%s%n", indShortReport);
    }
    catch (final FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void allProcessing(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 100000L);

    final TotalData td = GroupSalesNetYrRpt.calculateData(cdrList, "Over $10");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
    GroupSalesNetYrRpt.generateDebugReport(td);

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
      final TotalData td = GroupSalesNetYrRpt.calculateData(secLst, sec.replaceAll(",", ";"));
      GroupSalesNetYrRpt.tdSecList.add(td);
      System.out.printf("%s : %d%n", sec, secLst.size());
    }

    System.out.println();

    for (final TotalData td : GroupSalesNetYrRpt.tdSecList) {
      GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
      GroupSalesNetYrRpt.generateDebugReport(td);
    }

  }

  /***************************************************************************************************************************
   *
   * @param args
   */
  public static void main(String[] args) {

    final int year = 2022;
    final int quarter = 4;

    GroupSalesNetYrRpt.ProcessSpx(year, quarter);
    GroupSalesNetYrRpt.ProcessSml(year, quarter);
    GroupSalesNetYrRpt.allProcessing(year, quarter);
    GroupSalesNetYrRpt.allSectorProcessing(year, quarter);
    GroupSalesNetYrRpt.ProcessSemiconductors(year, quarter);
    GroupSalesNetYrRpt.ProcessRetail(year, quarter);
    GroupSalesNetYrRpt.ProcessAutos(year, quarter);
    GroupSalesNetYrRpt.ProcessBanks(year, quarter);
    GroupSalesNetYrRpt.ProcessBiotech(year, quarter);
    GroupSalesNetYrRpt.ProcessPharm(year, quarter);

    GroupSalesNetYrRpt.allIndustryProcessing(year, quarter);

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
      final int id = GroupSalesNetYrRpt.getId(ind, 8);
      if (id == 53101010 || id == 53101020 || id == 53403010) {
        autoList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : autoList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(autoList.size());

    final TotalData td = GroupSalesNetYrRpt.calculateData(autoList, "Autos");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
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
      final int id = GroupSalesNetYrRpt.getId(ind, 8);
      if (id == 55101010) {
        bankList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : bankList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(bankList.size());

    final TotalData td = GroupSalesNetYrRpt.calculateData(bankList, "Banks");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
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
      final int id = GroupSalesNetYrRpt.getId(ind, 8);
      if (id == 56202010) {
        biotechList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : biotechList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(biotechList.size());

    final TotalData td = GroupSalesNetYrRpt.calculateData(biotechList, "Biotech");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
    GroupSalesNetYrRpt.generateDebugReport(td);
  }

  public static void ProcessPharm(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findMajor(year, quarter, 10.0, 50000L);

    final List<CompanyDerived> pharmList = new ArrayList<>();

    for (final CompanyDerived cdr : cdrList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = GroupSalesNetYrRpt.getId(ind, 8);
      if (id == 56201040) {
        pharmList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : pharmList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(pharmList.size());

    final TotalData td = GroupSalesNetYrRpt.calculateData(pharmList, "Pharm");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
    GroupSalesNetYrRpt.generateDebugReport(td);
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
      final int id = GroupSalesNetYrRpt.getId(ind, 3);
      if (id == 534) {
        retailList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : retailList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(retailList.size());

    final TotalData td = GroupSalesNetYrRpt.calculateData(retailList, "Retail");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
    GroupSalesNetYrRpt.generateDebugReport(td);
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
      final int id = GroupSalesNetYrRpt.getId(ind, 6);
      if (id == 571010) {
        semiList.add(cdr);
      }
    }

    for (final CompanyDerived cdr : semiList) {
      final FieldData fd = cdr.getFd();
      System.out.printf("%s\t%s%n", fd.getTicker(), fd.getIndustry());
    }
    System.out.println(semiList.size());

    final TotalData td = GroupSalesNetYrRpt.calculateData(semiList, "Semiconductors");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
    GroupSalesNetYrRpt.generateDebugReport(td);
  }

  public static void ProcessSml(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findSml(year, quarter);

    final TotalData td = GroupSalesNetYrRpt.calculateData(cdrList, "SML");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
    GroupSalesNetYrRpt.generateDebugReport(td);

  }

  /**
   *
   * @param year
   * @param quarter
   */
  public static void ProcessSpx(int year, int quarter) {

    final List<CompanyDerived> cdrList = Scans.findSpx(year, quarter);

    final TotalData td = GroupSalesNetYrRpt.calculateData(cdrList, "SPX");

    GroupSalesNetYrRpt.generateReport(td, "sipout/salesnet");
    GroupSalesNetYrRpt.generateDebugReport(td);

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

  /**
   *
   * @param lst
   * @param grp
   * @return
   */
  private static TotalData calculateData(List<CompanyDerived> lst, String grp) {
    final TotalData ret = GroupSalesNetYrRpt.gsnr.new TotalData(grp);
    for (final CompanyDerived cdr : lst) {
      ret.add(cdr);
    }
    return ret;
  }

  private static TotalData calculateData(List<CompanyDerived> lst, String grp, String sec) {
    final TotalData ret = GroupSalesNetYrRpt.calculateData(lst, grp);
    ret.sector = sec;
    return ret;
  }

  private static String fmtWidth(double dval, int before, int after, int width) {
    final int total = before + after + 1;
    final String sfmt = String.format("%%%d.%df", total, after);
    final String sVal = String.format(sfmt, dval);
    final int wid = Math.max(total, width);
    final String ret = GroupSalesNetYrRpt.fmtWidth(sVal.trim(), wid);
    return ret;
  }

  private static String fmtWidth(int ival, int width) {
    final String istr = String.format("%d", ival);
    return GroupSalesNetYrRpt.fmtWidth(istr.trim(), width);
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
    final String ret = GroupSalesNetYrRpt.fmtWidth(sVal.trim(), wid);
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
   */
  private static void generateDebugReport(TotalData td) {
    final String fname = String.format("sipout/salesnet/%s_debug.txt", td.group);
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
   * @param dir
   */
  private static void generateIndReport(TotalData td, PrintWriter pw) {

    final double[] values = new double[TotalData.acount];

    final DateTime now = new DateTime();
    final SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy HH:mm");
    now.setSdf(sdf);

    pw.printf("%nGroup : %s  ::  Companies : %d   :::   %s%n", td.group, td.knt, td.sector);
    pw.println(",2022,2021,2020,2019,2018,2017,,2023,2022,2021,2020,2019,2018");
    pw.println(",TTM,Y1,Y2,Y3,Y4,Y5,,Est,TTM,Y1,Y2,Y3,Y4");

    pw.printf("Count");
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf(",%d", td.shareStats[i].getN());
    }

    pw.printf("%nAvg Shares (M)");
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf(",%f", td.shareStats[i].getMean());
      values[i] = td.shareStats[i].getMean();
    }
    pw.printf(",,");
    double[] pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nShares (M)");
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf(",%f", td.shareStats[i].getSum());
    }

    pw.printf("%nSales ($M)");
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf(",%f", td.salesStats[i].getSum());
      values[i] = td.salesStats[i].getSum();
    }
    pw.printf(",,");
    pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nNet Income ($M)");
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf(",%f", td.netIncomeStats[i].getSum());
      values[i] = td.netIncomeStats[i].getSum();
    }
    pw.printf(",,");
    pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nSales Per Share");
    for (int i = 0; i < TotalData.acount; i++) {
      final double tmp = td.salesStats[i].getSum() / td.shareStats[i].getSum();
      pw.printf(",%f", tmp);
      values[i] = tmp;
    }
    pw.printf(",,");
    pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nNet Income Per Share");
    double netIncPerShr1 = 0.0;
    for (int i = 0; i < TotalData.acount; i++) {
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

    pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf(",%f", d);
    }

    pw.printf("%nNet Margin");
    for (int i = 0; i < TotalData.acount; i++) {
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
  private static void generateIndReportTxt(TotalData td, PrintWriter pw) {

    final int col1width = 23;
    final String sCol1Pad = GroupSalesNetYrRpt.fmtWidth(" ", col1width);

    final int coldatawidth = 14;
    final String sColDataPad = GroupSalesNetYrRpt.fmtWidth(" ", coldatawidth);

    final double[] values = new double[TotalData.acount];

    final DateTime now = new DateTime();
    final SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy HH:mm");
    now.setSdf(sdf);

    pw.printf("%nGroup : %s  ::  Companies : %d   :::   %s%n", td.group, td.knt, td.sector);
    indShortReport += Utils.NL + fmtWidth(td.group, 70);

    String str = sCol1Pad + GroupSalesNetYrRpt.fmtWidth("2022", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("2021", coldatawidth)
        + GroupSalesNetYrRpt.fmtWidth("2020", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("2019", coldatawidth)
        + GroupSalesNetYrRpt.fmtWidth("2018", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("2017", coldatawidth);
    str += "" + GroupSalesNetYrRpt.fmtWidth("2023", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("2022", coldatawidth)
        + GroupSalesNetYrRpt.fmtWidth("2021", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("2020", coldatawidth)
        + GroupSalesNetYrRpt.fmtWidth("2019", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("2018", coldatawidth);
    pw.println(str);

    str = sCol1Pad + GroupSalesNetYrRpt.fmtWidth("TTM", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("Y1", coldatawidth)
        + GroupSalesNetYrRpt.fmtWidth("Y2", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("Y3", coldatawidth)
        + GroupSalesNetYrRpt.fmtWidth("Y4", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("Y5", coldatawidth) + ""
        + GroupSalesNetYrRpt.fmtWidth("Est", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("TTM", coldatawidth)
        + GroupSalesNetYrRpt.fmtWidth("Y1", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("Y2", coldatawidth)
        + GroupSalesNetYrRpt.fmtWidth("Y3", coldatawidth) + GroupSalesNetYrRpt.fmtWidth("Y4", coldatawidth);
    pw.println(str);

    pw.printf("%s", GroupSalesNetYrRpt.fmtWidth("Count", col1width));
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidth((int) td.shareStats[i].getN(), coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetYrRpt.fmtWidth("Avg Shares (M)", col1width));
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidth(td.shareStats[i].getMean(), 10, 2, coldatawidth));
      values[i] = td.shareStats[i].getMean();
    }
    pw.printf(sColDataPad);
    double[] pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {

      pw.printf("%s", GroupSalesNetYrRpt.fmtWidthPercent(d, 10, 2, coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetYrRpt.fmtWidth("Shares (M)", col1width));
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidth(td.shareStats[i].getSum(), 10, 2, coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetYrRpt.fmtWidth("Sales ($M)", col1width));
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidth(td.salesStats[i].getSum(), 10, 2, coldatawidth));
      values[i] = td.salesStats[i].getSum();
    }
    pw.printf(sColDataPad);
    pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidthPercent(d, 10, 2, coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetYrRpt.fmtWidth("Net Income (M)", col1width));
    for (int i = 0; i < TotalData.acount; i++) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidth(td.netIncomeStats[i].getSum(), 10, 2, coldatawidth));
      values[i] = td.netIncomeStats[i].getSum();
    }
    pw.printf(sColDataPad);
    pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidthPercent(d, 10, 2, coldatawidth));
    }

    pw.printf("%n%s", GroupSalesNetYrRpt.fmtWidth("Sales Per Share", col1width));
    for (int i = 0; i < TotalData.acount; i++) {
      final double tmp = td.salesStats[i].getSum() / td.shareStats[i].getSum();
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidth(tmp, 10, 2, coldatawidth));
      values[i] = tmp;
    }
    pw.printf(sColDataPad);
    pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidthPercent(d, 10, 2, coldatawidth));
    }
    indShortReport += String.format(",%s", fmtWidth(pctChg[0], 10, 4, 25));

    pw.printf("%n%s", GroupSalesNetYrRpt.fmtWidth("Net Income Per Share", col1width));
    double netIncPerShr1 = 0.0;
    for (int i = 0; i < TotalData.acount; i++) {
      final double tmp = td.netIncomeStats[i].getSum() / td.shareStats[i].getSum();
      if (i == 0) {
        netIncPerShr1 = tmp;
      }
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidthPercent(tmp, 10, 2, coldatawidth));
      values[i] = tmp;
    }

    final double estNetIncPerShr = td.estimatedNetIncome / td.estimateShares;
    double estNetIncChg = 0.0;
    if (Math.abs(netIncPerShr1) > 0.0) {
      estNetIncChg = (estNetIncPerShr - netIncPerShr1) / Math.abs(netIncPerShr1);
    }

    pw.printf("%s", GroupSalesNetYrRpt.fmtWidthPercent(estNetIncChg, 10, 2, coldatawidth));
    pctChg = GroupSalesNetYrRpt.calcChange(values);
    for (final double d : pctChg) {
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidth(d, 10, 2, coldatawidth));
    }
    indShortReport += String.format(",%s", fmtWidth(pctChg[0], 10, 4, 25));
    indShortReport += String.format(",%s", fmtWidth(estNetIncChg, 10, 4, 25));

    pw.printf("%n%s", GroupSalesNetYrRpt.fmtWidth("Net Marging", col1width));
    for (int i = 0; i < TotalData.acount; i++) {
      final double ns = td.salesStats[i].getSum();
      double margin = 0.0;
      if (ns > 0.0) {
        final double ni = td.netIncomeStats[i].getSum();
        margin = ni / ns;
      }
      pw.printf("%s", GroupSalesNetYrRpt.fmtWidth(margin, 10, 2, coldatawidth));
    }

    pw.println();

  }

  /**
   *
   * @param td
   */
  private static void generateReport(TotalData td, String dir) {
    final String fname = String.format("%s/%s.csv", dir, td.group);
    try (PrintWriter pw = new PrintWriter(fname)) {
      final DateTime now = new DateTime();
      final SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy HH:mm");
      now.setSdf(sdf);
      pw.printf("Group : %s  ::  Companies : %d,,,,,,,,%s%n", td.group, td.knt, now);
      pw.println(",2022,2021,2020,2019,2018,2017");
      pw.println(",TTM,Y1,Y2,Y3,Y4,Y5");

      pw.printf("Count");
      for (int i = 0; i < TotalData.acount; i++) {
        pw.printf(",%d", td.shareStats[i].getN());
      }
      pw.printf("%nAvg Shares (M)");
      for (int i = 0; i < TotalData.acount; i++) {
        pw.printf(",%f", td.shareStats[i].getMean());
      }
      pw.printf("%nShares (M)");
      for (int i = 0; i < TotalData.acount; i++) {
        pw.printf(",%f", td.shareStats[i].getSum());
      }
      pw.printf("%nSales ($M)");
      for (int i = 0; i < TotalData.acount; i++) {
        pw.printf(",%f", td.salesStats[i].getSum());
      }
      pw.printf("%nNet Income ($M)");
      for (int i = 0; i < TotalData.acount; i++) {
        pw.printf(",%f", td.netIncomeStats[i].getSum());
      }
      pw.printf("%nSales Per Share");
      for (int i = 0; i < TotalData.acount; i++) {
        final double tmp = td.salesStats[i].getSum() / td.shareStats[i].getSum();
        pw.printf(",%f", tmp);
      }
      pw.printf("%nNet Income Per Share");
      for (int i = 0; i < TotalData.acount; i++) {
        final double tmp = td.netIncomeStats[i].getSum() / td.shareStats[i].getSum();
        pw.printf(",%f", tmp);
      }
      final double tmp = td.estimatedNetIncome / td.estimateShares;
      pw.printf(",,%f, %f%n", td.estimatedNetIncome, tmp);
      pw.println();

    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
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
