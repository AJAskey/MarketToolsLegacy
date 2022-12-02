package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.ajaskey.market.tools.SIP.SipSectorIndustryRS;
import net.ajaskey.market.tools.SIP.BigDB.collation.CompanySummary;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.dataio.Options;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Utilities;

/**
 * 
 * @author Andy Askey
 * 
 *         Needed:
 * 
 *         SIP IndSecRS Notebook exported to Tab Delimited file
 * 
 *         File path to that file when instantiating SipSectorIndustryRS
 *
 */
public class WriteOptumaExternal {

  private static List<CompanySummary> csumData = null;
  private static List<CompanyDerived> dRList   = null;

  /**
   * 
   * @param code
   * @return
   */
  private static CompanySummary getCompanySummaryData(String code) {

    for (CompanySummary cs : csumData) {
      String ticker = cs.ticker;
      if (ticker.equals(code)) {
        return cs;
      }
    }
    return null;
  }

  /**
   * Main processor of SIP data for writing file for Optuma to import.
   * 
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {

    System.out.println("WriteOptumaExternal...");

    int year = 2022;
    int quarter = 4;

    /**
     * Process Sector and Industry Relative Strength
     */
    SipSectorIndustryRS.initialize(" SipSectorIndustryRS ", year, quarter, true);

    csumData = CompanySummary.getCompanySummary(year, quarter);

    Options.readOptionData(year, quarter);

    dRList = Scans.findMajor(year, quarter, 1.0, 1000L);

    // String optumaFile = "D:/dev/MarketTools - dev/lists/OptumaExternalData.csv";
    String optumaFile = "out/OptumaExternalData.csv";

    try (PrintWriter pw = new PrintWriter(optumaFile); PrintWriter pwCode = new PrintWriter("out/OptumaExternalCodes.csv")) {

      pw.printf("Code, CityExt, StateExt, CountryExt, LocationExt, SectorExt, IndustryExt, OptionableExt, NavExt,");
      pw.printf("FloatExt, MktCapExt, RS1Ext, RS3Ext, RS6Ext, RS12Ext, SectorRsExt, IndustryRsExt %n");

      pwCode.println("Code");

      for (final CompanyDerived cdr : dRList) {

        CompanySummary cs = getCompanySummaryData(cdr.getFd().getTicker());

        double secRs = SipSectorIndustryRS.getSectorRS(cs.sector);
        double indRs = SipSectorIndustryRS.getIndustryRS(cs.industry);

        String rsStr = "0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        if (cs != null) {
          rsStr = String.format("%f, %f, %f, %f, %f, %f", cs.rs1, cs.rs3, cs.rs6, cs.rs12, secRs, indRs);
        }
        print(pw, cdr, rsStr);
        pwCode.println(cdr.getTicker());
      }

      /**
       * Add important stocks and ETFs not found by scan
       */
      CompanyDerived brkb = Scans.findCompany(year, quarter, "BRK.B");
      print(pw, brkb, "0,0,0.0,0.0,0.0,0.0");

      printExtras(pw, "GASS", "ERITHREA", "ATHINA", "Greece", "Industrials", "Freight & Logistics - Marine");
      printExtras(pw, "CCO", "SAN ANTONIO", "TX", "USA", "Consumer Cyclicals", "Advertising & Marketing");
      printExtras(pw, "FLNG", "", "HAMITON", "Bermuda", "Energy", "Oil & Gas - Transportation Services");
      printExtras(pw, "Z", "SEATTLE", "WA", "USA", "Real Estate", "Real Estate Services");

      printExtras(pw, "XLB", "NEW YORK", "NY", "USA", "Basic Materials", "ETF");
      printExtras(pw, "XLE", "NEW YORK", "NY", "USA", "Energy", "ETF");
      printExtras(pw, "UNG", "NEW YORK", "NY", "USA", "Energy", "ETF");
      printExtras(pw, "XLF", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "XLI", "NEW YORK", "NY", "USA", "Industrial", "ETF");
      printExtras(pw, "XLK", "NEW YORK", "NY", "USA", "Technology", "ETF");
      printExtras(pw, "XLP", "NEW YORK", "NY", "USA", "Cyclicals", "ETF");
      printExtras(pw, "XLU", "NEW YORK", "NY", "USA", "Utilities", "ETF");
      printExtras(pw, "XLV", "NEW YORK", "NY", "USA", "Healthcare", "ETF");
      printExtras(pw, "XLY", "NEW YORK", "NY", "USA", "Consumer Cyclicals", "ETF");
      printExtras(pw, "XLC", "NEW YORK", "NY", "USA", "Technology", "ETF");
      printExtras(pw, "XLRE", "NEW YORK", "NY", "USA", "Real Estate", "ETF");

      printExtras(pw, "SPY", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "QQQ", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "DIA", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "XHB", "NEW YORK", "NY", "USA", "Real Estate", "ETF");
      printExtras(pw, "VNQ", "NEW YORK", "NY", "USA", "Real Estate", "ETF");
      printExtras(pw, "TLT", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "IWM", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "XRT", "NEW YORK", "NY", "USA", "Consumer Cyclicals", "ETF");
      printExtras(pw, "KRE", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "KIE", "NEW YORK", "NY", "USA", "Insurance - Multiline & Brokers", "ETF");
      printExtras(pw, "IGV", "NEW YORK", "NY", "USA", "Software", "ETF");
      printExtras(pw, "SMH", "NEW YORK", "NY", "USA", "Technology", "ETF");
      printExtras(pw, "IBB", "NEW YORK", "NY", "USA", "Healthcare", "ETF");
      printExtras(pw, "BOTZ", "NEW YORK", "NY", "USA", "Technology", "ETF");
      printExtras(pw, "EWT", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "INDA", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "EWJ", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "EWQ", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "EFA", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "EWI", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "EWY", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "EWZ", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "EEM", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "FXI", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "HYG", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "JNK", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "MBB", "NEW YORK", "NY", "USA", "Financials", "ETF");
      printExtras(pw, "IAI", "NEW YORK", "NY", "USA", "Financials", "ETF");

      printExtras(pw, "PHO", "NEW YORK", "NY", "USA", "Utilities - Water & Related", "ETF");
      printExtras(pw, "WOOD", "NEW YORK", "NY", "USA", "Basic Materials", "ETF");
      printExtras(pw, "TAN", "NEW YORK", "NY", "USA", "Energy", "ETF");
      printExtras(pw, "IEZ", "NEW YORK", "NY", "USA", "Energy", "ETF");

      printExtras(pw, "ITA", "NEW YORK", "NY", "USA", "Industrials", "ETF");
      printExtras(pw, "JETS", "NEW YORK", "NY", "USA", "Industrials", "ETF");
      printExtras(pw, "IYT", "NEW YORK", "NY", "USA", "Industrials", "ETF");
      printExtras(pw, "GLD", "NEW YORK", "NY", "USA", "Basic Materials", "ETF");
      printExtras(pw, "SLV", "NEW YORK", "NY", "USA", "Basic Materials", "ETF");
      printExtras(pw, "RING", "NEW YORK", "NY", "USA", "Basic Materials", "ETF");
      printExtras(pw, "PICK", "NEW YORK", "NY", "USA", "Basic Materials", "ETF");

      SipSectorIndustryRS.writeIndustryWatchlistForOptuma();

    }

    System.out.println("Done.");
  }

  /**
   * 
   * @param pw
   * @param code
   * @param city
   * @param state
   * @param country
   * @param sector
   * @param industry
   */
  private static void printExtras(PrintWriter pw, String code, String city, String state, String country, String sector, String industry) {
    printcol(pw, code);
    printcol(pw, city);
    printcol(pw, state);
    printcol(pw, country);

    String location = buildLocation(city, state, country);
    printcol(pw, location);
    printcol(pw, sector);
    printcol(pw, industry);
    pw.println("1, 0, 0, 0, 0, 0, 0, 0, 0, 0");
  }

  /**
   * 
   * @param city
   * @param state
   * @param country
   * @return
   */
  private static String buildLocation(String city, String state, String country) {
    String fldCity[] = city.split(" ");
    String uCity = "";
    for (String s : fldCity) {
      String uC = StringUtils.capitalize(s.toLowerCase());
      uCity += uC.trim() + " ";
    }
    String tmp = String.format("%s %s  %s", uCity.trim(), state, country);
    String loc = tmp.replace("United States", "USA");
    return loc;
  }

  /**
   * 
   * @param pw
   * @param cdr
   * @param rsStr
   */
  private static void print(PrintWriter pw, CompanyDerived cdr, String rsStr) {
    try {
      FieldData fd = cdr.getFd();
      printcol(pw, fd.getTicker());
      printcol(pw, fd.getCompanyInfo().getCity());
      printcol(pw, fd.getCompanyInfo().getState());
      printcol(pw, fd.getCompanyInfo().getCountry());
      String location = buildLocation(fd.getCompanyInfo().getCity(), fd.getCompanyInfo().getState(), fd.getCompanyInfo().getCountry());
      printcol(pw, location);
      String sec = Utilities.cleanSecInd(fd.getCompanyInfo().getSector());
      String ind = Utilities.cleanSecInd(fd.getCompanyInfo().getIndustry());
      printcol(pw, sec);
      printcol(pw, ind);
      if (Options.isOptionable(fd.getTicker())) {
        printcol(pw, 1);
      }
      else {
        printcol(pw, 0);
      }
      Double navps = cdr.getNetNavps();
      printcol(pw, Math.max(navps, 0));

      double tmp;
      tmp = fd.getShareData().getFloatshr();
      printcol(pw, tmp);

      tmp = fd.getShareData().getMktCap();
      printcol(pw, tmp);

      pw.printf("%s", rsStr);

      pw.println();
    }
    catch (Exception e) {
    }
  }

  private static void printcol(PrintWriter pw, double d) {
    pw.printf("%f,", d);
  }

  private static void printcol(PrintWriter pw, int i) {
    pw.printf("%d,", i);
  }

  private static void printcol(PrintWriter pw, String str) {
    pw.printf("%s,", str.replace(",", ";").trim());
  }

}
