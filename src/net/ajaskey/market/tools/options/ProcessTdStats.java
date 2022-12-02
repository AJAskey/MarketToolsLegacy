package net.ajaskey.market.tools.options;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;
import net.ajaskey.common.Utils;

public class ProcessTdStats {

  public class TdStatsData {
    DateTime dateCollected;
    String   code;
    long     dailyPutVolume;
    long     dailyCallVolume;
    long     dailyPutDollar;
    long     dailyCallDollar;
    long     oiPutVolume;
    long     oiCallVolume;
    long     oiPutDollar;
    long     oiCallDollar;
    double   putIv;
    double   callIv;
    double   rVol;
    int      readPos;
    boolean  valid;

    /**
     * 
     * @param when
     */
    public TdStatsData(DateTime when) {

      this.init();

      try {
        this.readPos = ProcessTdStats.currentFileDataPos;
        this.dateCollected = new DateTime(when);

        String ss = "";
        while (ss.length() == 0) {
          final String s = ProcessTdStats.currentFileData.get(ProcessTdStats.currentFileDataPos);
          ss = s.trim();
          ProcessTdStats.currentFileDataPos++;
          if (currentFileDataPos >= currentFileData.size()) {
            return;
          }
        }

        this.code = ss.replaceAll(":", "");
        if (this.isCode(this.code)) {

          final String s1 = getNextFromFile();
          if (s1.startsWith("Volume Put   :")) {
            final long[] d1 = this.parseData1(s1);
            this.dailyPutVolume = d1[0];
            this.dailyCallVolume = d1[1];

            final String s2 = getNextFromFile();
            if (s2.startsWith("Volume Put $ :")) {
              final long[] d2 = this.parseData1(s2);
              this.dailyPutDollar = d2[0];
              this.dailyCallDollar = d2[1];

              final String s3 = getNextFromFile();
              if (s3.startsWith("OI Put       :")) {
                final long[] d3 = this.parseData1(s3);
                this.oiPutVolume = d3[0];
                this.oiCallVolume = d3[1];

                final String s4 = getNextFromFile();
                if (s4.startsWith("OI Put     $ :")) {
                  final long[] d4 = this.parseData1(s4);
                  this.oiPutDollar = d4[0];
                  this.oiCallDollar = d4[1];

                  final String s5 = getNextFromFile();
                  if (s5.startsWith("Avg IV Put   :")) {
                    final double[] d5 = this.parseData2(s5);
                    this.putIv = d5[0];
                    this.callIv = d5[1];
                    this.rVol = d5[2];

                    this.valid = true;
                  }
                }
              }
            }
          }
        }
        else {
          ProcessTdStats.currentFileDataPos++;
          System.out.printf("Warning : %s code not found!%n", this.code);
        }
      }
      catch (final Exception e) {
        this.valid = false;
        System.out.printf("currentFileDataPos : %d%n", currentFileDataPos);
        e.printStackTrace();
      }

    }

    /**
     * Get next line of positive length
     * 
     * @return
     */
    private String getNextFromFile() {
      String ret = "";
      try {
        while (ret.length() == 0) {
          ret = ProcessTdStats.currentFileData.get(ProcessTdStats.currentFileDataPos).trim();
          currentFileDataPos++;
        }
      }
      catch (Exception e) {
        ret = "";
      }
      return ret;
    }

    @Override
    public String toString() {
      String ret;
      if (this.valid) {
        ret = String.format("%s, %s, %d, %d, %d, %d, %d, %d, %d, %d, %.2f, %.2f, %.2f", this.code, this.dateCollected.toFullString(),
            this.dailyPutVolume, this.dailyCallVolume, this.oiPutVolume, this.oiCallVolume, this.dailyPutDollar, this.dailyCallDollar,
            this.oiPutDollar, this.oiCallDollar, this.putIv, this.callIv, this.rVol);
      }
      else {
        ret = "Invalid";
      }
      return ret;
    }

    public String toCsv() {
      String ret;
      if (this.valid) {
        String sDate = this.dateCollected.format("E,yyyy-MM-dd,HH:mm:ss");
        double pcOi = 0.0;
        double pcOiDollar = 0.0;
        if (this.oiCallDollar > 0L) {
          pcOiDollar = (double) this.oiPutDollar / (double) this.oiCallDollar;
        }
        if (this.oiCallVolume > 0L) {
          pcOi = (double) this.oiPutVolume / (double) this.oiCallVolume;
        }

        ret = String.format("%s, %s, %d, %d, %d, %d, %.2f, %d, %d, %d, %d, %.2f, %.2f, %.2f, %.2f", this.code, sDate, this.dailyPutVolume,
            this.dailyCallVolume, this.oiPutVolume, this.oiCallVolume, pcOi, this.dailyPutDollar, this.dailyCallDollar, this.oiPutDollar,
            this.oiCallDollar, pcOiDollar, this.putIv, this.callIv, this.rVol);
      }
      else {
        ret = "Invalid";
      }
      return ret;
    }

    /**
     *
     */
    private void init() {
      this.dateCollected = null;
      this.code = "";
      this.dailyPutVolume = 0L;
      this.dailyPutDollar = 0L;
      this.oiPutVolume = 0L;
      this.oiPutDollar = 0L;
      this.dailyCallVolume = 0L;
      this.dailyCallDollar = 0L;
      this.oiCallVolume = 0L;
      this.oiCallDollar = 0L;
      this.putIv = 0.0;
      this.callIv = 0.0;
      this.rVol = 0.0;
      this.readPos = 0;
      this.valid = false;

    }

    /**
     * 
     * @param s
     * @return
     */
    private boolean isCode(String s) {
      if (s.length() > 0) {
        for (final String cs : ProcessTdStats.codes) {
          if (s.startsWith(cs)) {
            return true;
          }
        }
      }
      return false;
    }

    /**
     *
     * @param s
     * @return
     */
    private long[] parseData1(String s) {
      final long[] ret = new long[2];

      final String ss = s.replace(",", "").replace("$", "").replace("Call", "").replace("\t", " ");
      final String fld[] = ss.trim().split(":");
      ret[0] = Utils.parseLong(fld[1].trim());
      ret[1] = Utils.parseLong(fld[2].trim());
      return ret;
    }

    /**
     *
     * @param s
     * @return
     */
    private double[] parseData2(String s) {
      final double[] ret = new double[3];

      final String ss = s.replace(",", "").replace("$", "").replace("Call", "").replace("RealizedVol", "").replace("Premium", "").replace("hVol", "")
          .replace("Skew", "").replace("\t", " ");
      final String fld[] = ss.trim().split(":");
      ret[0] = Utils.parseDouble(fld[1].trim());
      ret[1] = Utils.parseDouble(fld[2].trim());
      ret[2] = Utils.parseDouble(fld[3].trim());
      return ret;
    }
  }

  /**
   * 
   * @author Computer
   *
   */
  public class DataSorter implements Comparator<TdStatsData> {

    @Override
    public int compare(TdStatsData tsd1, TdStatsData tsd2) {
      if (tsd1 == null || tsd2 == null) {
        return 0;
      }

      int ret = 0;

      try {
        if (tsd1.dateCollected.isGreaterThan(tsd2.dateCollected)) {
          ret = -1;
        }
        else if (tsd1.dateCollected.isLessThan(tsd2.dateCollected)) {
          ret = 1;
        }
        else {
          ret = tsd1.code.compareTo(tsd2.code);
        }
      }
      catch (final Exception e) {
        ret = 0;
      }

      return ret;
    }

  }

  static int               currentFileDataPos = 0;
  static List<TdStatsData> rawList            = new ArrayList<>();
  static List<String>      currentFileData    = new ArrayList<>();
  static List<String>      codes              = new ArrayList<>();

  static ProcessTdStats   ptds = new ProcessTdStats();
  static SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMdd-HHmmss");

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {

//    final String filename = "input/tdstats-data/tdstats-20210515-051803.txt";
//    ProcessTdStats.currentFileData = TextUtils.readTextFile(filename, false);
//    final DateTime dt = new DateTime("20210815-051803", ProcessTdStats.sdf);

    ProcessTdStats.addCodes();

    File directoryPath = new File("input/tdstats-data");
    File fileList[] = directoryPath.listFiles();
    for (File f : fileList) {
      String fname = f.getName();
      String sDt = fname.replace("tdstats-", "").replace(".txt", "");
      // System.out.printf("%s %s%n", fname, sDt);

      final DateTime dt = new DateTime(sDt, ProcessTdStats.sdf);
      ProcessTdStats.currentFileData = TextUtils.readTextFile(f, false);
      currentFileDataPos = 0;

      while (currentFileDataPos < currentFileData.size()) {

        final TdStatsData tdsd = ProcessTdStats.ptds.new TdStatsData(dt);
//        if (tdsd.code.startsWith("Combined")) {
//          System.out.println(tdsd);
//        }
        if (tdsd.valid) {
          rawList.add(tdsd);
          System.out.println(tdsd);
        }
        else if (tdsd.code.startsWith("Combined")) {
          // String fld[] = tdsd.code.split(" ");
          tdsd.code = "Combined";
          tdsd.valid = true;
          rawList.add(tdsd);
          System.out.println(tdsd);
        }
      }
    }

    buildOptumaData("SPY");
    buildOptumaData("QQQ");
    buildOptumaData("Combined");

    try (PrintWriter pw = new PrintWriter("out/tdstats-all.csv")) {
      pw.println(
          "Code, Day, Date, Time, Put Volume, Call Volume, Put OI, Call OI, pc OI, Put Dollar, Call Dollar, Put OI Dollar, Call OI Dollar, pc OI Dollar, IV Put, IV Call, rVol");
      for (TdStatsData tdsd : rawList) {
//        DateTime eod = new DateTime(tdsd.dateCollected);
//        eod.settime(16, 0, 0);
//        DateTime bnd = new DateTime(tdsd.dateCollected);
//        bnd.add(DateTime.DATE, 1);
//        bnd.settime(9, 30, 0);
////        if (tdsd.dateCollected.isGreaterThan(eod) && tdsd.dateCollected.isLessThan(bnd)) {
//        if (tdsd.dateCollected.isLessThan(bnd)) {
//          System.out.printf("%s\t%s\t%s%n", tdsd.dateCollected.toFullString(), eod.toFullString(), bnd.toFullString());
        pw.println(tdsd.toCsv());
        // }
      }
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * 
   * @param code
   */
  private static void buildOptumaData(String code) {

    String fname;

    try {

      fname = String.format("%sOptions/%s_OI_Puts.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
      try (PrintWriter pw = new PrintWriter(fname)) {
        for (TdStatsData tdsd : rawList) {
          if (tdsd.code.equalsIgnoreCase(code)) {
            String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
            long d = tdsd.oiPutVolume;
            pw.printf("%s,%d%n", sDate, d);
          }
        }
      }

      fname = String.format("%sOptions/%s_OI_Calls.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
      try (PrintWriter pw = new PrintWriter(fname)) {
        for (TdStatsData tdsd : rawList) {
          if (tdsd.code.equalsIgnoreCase(code)) {
            String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
            long d = tdsd.oiCallVolume;
            pw.printf("%s,%d%n", sDate, d);
          }
        }
      }

      fname = String.format("%sOptions/%s_pcOI.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
      try (PrintWriter pw = new PrintWriter(fname)) {
        for (TdStatsData tdsd : rawList) {
          if (tdsd.code.equalsIgnoreCase(code)) {
            String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
            if (tdsd.oiCallVolume > 0L) {
              double d = (double) tdsd.oiPutVolume / (double) tdsd.oiCallVolume;
              pw.printf("%s,%.2f%n", sDate, d);
            }
          }
        }
      }

      fname = String.format("%sOptions/%s_OI_Dollar_Puts.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
      try (PrintWriter pw = new PrintWriter(fname)) {
        for (TdStatsData tdsd : rawList) {
          if (tdsd.code.equalsIgnoreCase(code)) {
            String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
            long d = tdsd.oiPutDollar;
            pw.printf("%s,%d%n", sDate, d);
          }
        }
      }

      fname = String.format("%sOptions/%s_OI_Dollar_Calls.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
      try (PrintWriter pw = new PrintWriter(fname)) {
        for (TdStatsData tdsd : rawList) {
          if (tdsd.code.equalsIgnoreCase(code)) {
            String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
            long d = tdsd.oiCallDollar;
            pw.printf("%s,%d%n", sDate, d);
          }
        }
      }

      fname = String.format("%sOptions/%s_pcOI_Dollar.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
      try (PrintWriter pw = new PrintWriter(fname)) {
        for (TdStatsData tdsd : rawList) {
          if (tdsd.code.equalsIgnoreCase(code)) {
            String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
            if (tdsd.oiCallDollar > 0L) {
              double d = (double) tdsd.oiPutDollar / (double) tdsd.oiCallDollar;
              pw.printf("%s,%.2f%n", sDate, d);
            }
          }
        }
      }

      if (!code.equalsIgnoreCase("Combined")) {
        fname = String.format("%sOptions/%s_Avg_IV_Puts.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
        try (PrintWriter pw = new PrintWriter(fname)) {
          for (TdStatsData tdsd : rawList) {
            if (tdsd.code.equalsIgnoreCase(code)) {
              String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
              double d = tdsd.putIv;
              pw.printf("%s,%.2f%n", sDate, d);
            }
          }
        }

        fname = String.format("%sOptions/%s_Avg_IV_Calls.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
        try (PrintWriter pw = new PrintWriter(fname)) {
          for (TdStatsData tdsd : rawList) {
            if (tdsd.code.equalsIgnoreCase(code)) {
              String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
              double d = tdsd.callIv;
              pw.printf("%s,%.2f%n", sDate, d);
            }
          }
        }

        fname = String.format("%sOptions/%s_rVol.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
        try (PrintWriter pw = new PrintWriter(fname)) {
          for (TdStatsData tdsd : rawList) {
            if (tdsd.code.equalsIgnoreCase(code)) {
              String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
              double d = tdsd.rVol;
              pw.printf("%s,%.2f%n", sDate, d);
            }
          }
        }

        // double putPrem =stats.premium = ((stats.calliv / stats.realvol) - 1.0) *
        // 100.0
        fname = String.format("%sOptions/%s_Put_Premium.csv", net.ajaskey.market.optuma.OptumaCommon.optumaPath, code);
        try (PrintWriter pw = new PrintWriter(fname)) {
          for (TdStatsData tdsd : rawList) {
            if (tdsd.code.equalsIgnoreCase(code)) {
              String sDate = tdsd.dateCollected.format("yyyy-MM-dd");
              if (tdsd.rVol > 0.0) {
                double d = ((tdsd.putIv / tdsd.rVol) - 1.0) * 100.0;
                pw.printf("%s,%.2f%n", sDate, d);
              }
            }
          }
        }
      }

    }
    catch (Exception e) {

    }
  }

  /**
   * 
   */
  private static void addCodes() {
    ProcessTdStats.codes.add("XLB");
    ProcessTdStats.codes.add("XLC");
    ProcessTdStats.codes.add("XLE");
    ProcessTdStats.codes.add("XLF");
    ProcessTdStats.codes.add("XLI");
    ProcessTdStats.codes.add("XLK");
    ProcessTdStats.codes.add("XLP");
    ProcessTdStats.codes.add("XLU");
    ProcessTdStats.codes.add("XLV");
    ProcessTdStats.codes.add("XLY");
    ProcessTdStats.codes.add("XHB");
    ProcessTdStats.codes.add("XRT");
    ProcessTdStats.codes.add("KRE");
    ProcessTdStats.codes.add("XLRE");
    ProcessTdStats.codes.add("SMH");
    ProcessTdStats.codes.add("IBB");
    ProcessTdStats.codes.add("IGV");
    ProcessTdStats.codes.add("IYT");
    ProcessTdStats.codes.add("HYG");
    ProcessTdStats.codes.add("IWM");
    ProcessTdStats.codes.add("DIA");
    ProcessTdStats.codes.add("QQQ");
    ProcessTdStats.codes.add("SPY");
    ProcessTdStats.codes.add("Combined");
  }

  /**
   * 
   */
  public ProcessTdStats() {
    // TODO Auto-generated constructor stub
  }

}
