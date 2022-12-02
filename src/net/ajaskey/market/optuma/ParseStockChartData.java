package net.ajaskey.market.optuma;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;

public class ParseStockChartData {

  /**
   * 
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    ParseStockChartData.process("Bully");
    // ParseStockChartData.process("BullySec");
    ParseStockChartData.process("TRIN");
    ParseStockChartData.process("TRINQ");
    ParseStockChartData.process("McClellanOscSPX");
    ParseStockChartData.process("McClellanSumSPX");
    ParseStockChartData.process("McClellanOscNYSE");
    ParseStockChartData.process("McClellanSumNYSE");
    // ParseStockChartData.process("VIX");
    // ParseStockChartData.process("TYX", 10.0);
    // ParseStockChartData.processWithHistory("TNX", 10.0);
    ParseStockChartData.process("BTCUSD");

    System.out.println("Done.");
  }

  /**
   * 
   * @param code
   */
  private static void process(String code) {
    final List<PriceData> recentData = ParseStockChartData.readStockChartData(code);
    ParseStockChartData.write(code, null, recentData);
  }

  private static void process(String code, double scaler) {
    final List<PriceData> recentData = ParseStockChartData.readStockChartData(code);
    final List<PriceData> scaled = ParseStockChartData.scaleData(recentData, scaler);
    ParseStockChartData.write(code, null, scaled);
  }

  /**
   * 
   */
  private static void processWithHistory(String code, double scaler) {
    final List<PriceData> histData = ParseStockChartData.readHistoricalData(code);
    final List<PriceData> recentData = ParseStockChartData.readStockChartData(code);
    final List<PriceData> scaled = ParseStockChartData.scaleData(recentData, scaler);
    ParseStockChartData.write(code, histData, scaled);

  }

  /**
   * 
   * @param code
   * @return
   */
  private static List<PriceData> readHistoricalData(String code) {
    final String dir = "D:/dev/MarketTools - dev/data/historic";
    final String fname = dir + "/" + code + ".csv";
    final List<String> data = TextUtils.readTextFile(fname, true);
    final List<PriceData> retData = new ArrayList<>();
    for (final String s : data) {
      final String fld[] = s.split(",");
      final DateTime dt = new DateTime(fld[0].trim(), "yyyyMMdd");
      if (dt.getCal() == null) {
      }
      else {
        final double clse = Double.parseDouble(fld[1].trim());
        final PriceData pd = new PriceData(dt, clse);
        if (pd.isValid()) {
          retData.add(pd);
        }
      }
    }

    return retData;
  }

  /**
   * 
   * @param code
   * @return
   */
  private static List<PriceData> readStockChartData(String code) {
    final String dir = "D:/data2/scdata";

    final List<String> data = TextUtils.readTextFile(dir + "/" + code + ".html", true);

    final List<PriceData> retData = new ArrayList<>();
    boolean process = false;
    for (final String s : data) {

      if (s.contains("<pre>")) {
        process = true;
      }
      else if (s.contains("</pre>")) {
        process = false;
        break;
      }

      if (process) {
        final PriceData pd = new PriceData(s.split("\\s+"), "MM-dd-yyyy", 1);
        if (pd.isValid()) {
          retData.add(pd);
        }
      }
    }

    return retData;
  }

  /**
   * 
   * @param data
   * @param scaler
   * @return
   */
  private static List<PriceData> scaleData(List<PriceData> data, double scaler) {
    final List<PriceData> ret = new ArrayList<>();
    for (final PriceData pd : data) {
      if (pd.isValid()) {
        final PriceData newPd = new PriceData(pd.date, pd.open / scaler, pd.high / scaler, pd.low / scaler, pd.close / scaler, pd.volume);
        if (newPd.isValid()) {
          ret.add(newPd);
        }
      }
    }
    return ret;
  }

  /**
   * 
   * @param code
   * @param histData
   * @param recentData
   */
  private static void write(String code, List<PriceData> histData, List<PriceData> recentData) {
    final String dir = "D:/data2/MA/CSV Data/PVData";
    if (histData != null) {
      Collections.sort(histData, new SortPriceData());
    }
    Collections.sort(recentData, new SortPriceData());
    try (PrintWriter pw = new PrintWriter(dir + "/" + code + "Daily.csv")) {
      if (histData != null) {
        System.out.println("Writing " + code + " historical data.");
        for (final PriceData pd : histData) {
          // System.out.println(pd);
          pw.write(pd.toOptumaString(code + "Daily", 1.0));
          pw.println();
        }
      }
      System.out.println("Writing " + code + " recent data.");
      for (final PriceData rd : recentData) {
        // System.out.println(rd);
        pw.write(rd.toOptumaString(code + "Daily", 1.0));
        pw.println();
      }
    }
    catch (final FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
