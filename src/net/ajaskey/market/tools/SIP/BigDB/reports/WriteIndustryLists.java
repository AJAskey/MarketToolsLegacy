package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.market.tools.SIP.SipUtils;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.SortByMCap;

public class WriteIndustryLists {

  static int bigListCnt = 0;

  static PrintWriter bigListPw = null;

  static List<String> invalidTickers = new ArrayList<>();

  final static String listDir = "D:/dev/MarketTools - dev/lists/";

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {

    System.out.println("Writing IndustryLists...");

    final int year = 2022;
    final int quarter = 4;

    WriteIndustryLists.setInvalidTickers();

    final List<CompanyDerived> cdrList = new ArrayList<>();

    final List<CompanyDerived> dRList = Scans.findMajor(year, quarter, 1.0, 1000L);

    // Options.readOptionData(year, quarter);
    for (final CompanyDerived cdr : dRList) {
      cdr.getMarketCapQdata().getMostRecent();
      if (cdr.getMarketCapQdata().getMostRecent() > 1.0 && WriteIndustryLists.validTicker(cdr.getTicker())) {
        cdrList.add(cdr);
      }
    }

    final List<String> industryList = SipUtils.getIndustryList(cdrList);
    for (final String s : industryList) {
      System.out.println(s);
    }
    System.out.println(industryList.size());
    final List<String> sectorList = SipUtils.getSectorList(cdrList);
    for (final String s : sectorList) {
      System.out.println(s);
    }
    System.out.println(sectorList.size());

    try {
      WriteIndustryLists.bigListPw = new PrintWriter(WriteIndustryLists.listDir + "big_indsec.csv");
      WriteIndustryLists.bigListPw.println("Code,Exchange");

      WriteIndustryLists.buildAutomotiveList(cdrList);
      WriteIndustryLists.buildBiotechList(cdrList);
      WriteIndustryLists.buildChemicalsList(cdrList);
      WriteIndustryLists.buildConstructionList(cdrList);
      WriteIndustryLists.buildDefenseList(cdrList);
      WriteIndustryLists.buildElectronicsList(cdrList);
      WriteIndustryLists.buildEnergyList(cdrList);
      WriteIndustryLists.buildBankList(cdrList);
      WriteIndustryLists.buildFinanceList(cdrList);
      WriteIndustryLists.buildForestList(cdrList);
      WriteIndustryLists.buildHealthCareList(cdrList);
      WriteIndustryLists.buildHomeBuilderList(cdrList);
      WriteIndustryLists.buildInsuranceList(cdrList);
      WriteIndustryLists.buildMetalsList(cdrList);
      WriteIndustryLists.buildOnlineServicesList(cdrList);
      WriteIndustryLists.buildPharmList(cdrList);
      WriteIndustryLists.buildTransportsList(cdrList);
      WriteIndustryLists.buildRealEstateList(cdrList);
      WriteIndustryLists.buildRecreationList(cdrList);
      WriteIndustryLists.buildRetailList(cdrList);
      WriteIndustryLists.buildSemiconductorList(cdrList);
      WriteIndustryLists.buildShippingList(cdrList);
      WriteIndustryLists.buildTransportsList(cdrList);

      WriteIndustryLists.buildSectorMaterials(cdrList);

      WriteIndustryLists.bigListPw.close();
    }
    catch (final FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    WriteIndustryLists.writeInvalidTickers();

    System.out.println("Done.");
  }

  /**
   *
   * @param dRList
   */
  private static void buildAutomotiveList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 53101010 || id == 53101020 || id == 53403010) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_automotive.csv");
    WriteIndustryLists.writeListAll(list, "indall_automotive.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildBankList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 55101010) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_bank.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildBiotechList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 56202010) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_biotech.csv");
    WriteIndustryLists.writeListAll(list, "indall_biotech.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildChemicalsList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 511) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_chemicals.csv");
    WriteIndustryLists.writeListAll(list, "indall_chemicals.csv");
  }

  /**
   * 
   * @param dRList
   */
  private static void buildConstructionList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 51202010 || id == 52201020 || id == 53203020 || id == 52102020) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_construction.csv");
    WriteIndustryLists.writeListAll(list, "indall_construction.csv");
  }

  private static void buildDefenseList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 52101010) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_defense.csv");
    WriteIndustryLists.writeListAll(list, "indall_defense.csv");
  }

  /**
   * 
   * @param dRList
   */
  private static void buildElectronicsList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id1 = WriteIndustryLists.getId(ind, 3);
      final int id2 = WriteIndustryLists.getId(ind, 8);
      if (id1 == 571 || id2 == 52102030) {
        final int id3 = WriteIndustryLists.getId(ind, 6);
        if (id3 != 571010) {
          list.add(cdr);
        }
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_electronics.csv");
    WriteIndustryLists.writeListAll(list, "indall_electronics.csv");
  }

  /**
   *
   * @param dRList
   * @throws FileNotFoundException
   */
  private static void buildEnergyList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 2);
      if (id == 50) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_energy.csv");
  }

  /**
   * 
   * @param dRList
   */
  private static void buildFinanceList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 551) {
        final int id2 = WriteIndustryLists.getId(ind, 8);
        if (id2 != 55101010) {
          list.add(cdr);
        }
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_finance.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildForestList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 513) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_forest.csv");
  }

  /**
   * 
   * @param dRList
   */
  private static void buildHealthCareList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 561) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_healthcare.csv");
  }

  /**
   * 
   * @param dRList
   */
  private static void buildHomeBuilderList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      // if (id == 53203010 || id == 53204030 || id == 53204040 || id == 51301010) {
      if (id == 53203010 || id == 53204040) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_homebuilder.csv");
  }

  /**
   * 
   * @param dRList
   */
  private static void buildInsuranceList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 553) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_insurance.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildMetalsList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 512) {
        final int id2 = WriteIndustryLists.getId(ind, 8);
        if (id2 != 51202010) {
          list.add(cdr);
        }
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_metals.csv");
  }

  /**
   * 
   * @param dRList
   */
  private static void buildOnlineServicesList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 57201030) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_onlineservices.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildPharmList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 56201040) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_pharm.csv");
    WriteIndustryLists.writeListAll(list, "indall_pharm.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildRealEstateList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 601) {
        // final int id2 = WriteIndustryLists.getId(ind, 6);
        // if (id2 != 601020) {
        list.add(cdr);
        // }
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_realestate.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildRecreationList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 53205020 || id == 53301010 || id == 53301020 || id == 53301030 || id == 53301040 || id == 52406020) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_recreation.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildRetailList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 534) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_retail.csv");
  }

  /**
   * Not GOLD
   *
   * @param dRList
   */
  private static void buildSectorMaterials(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String sec = cdr.getFd().getSector();
      if (sec.equals("51  - Basic Materials")) {
        final String ind = cdr.getFd().getIndustry();
        final int id = WriteIndustryLists.getId(ind, 8);
        if (id != 51201060) {
          list.add(cdr);
        }
      }
    }

    WriteIndustryLists.writeListTop(list, "sec_materials.csv");
    WriteIndustryLists.writeListAll(list, "secall_materials.csv");

  }

  /**
   * 
   * @param dRList
   */
  private static void buildSemiconductorList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 6);
      if (id == 571010) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_semiconductor.csv");
  }

  private static void buildShippingList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 8);
      if (id == 52405020) {
        list.add(cdr);
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_shipping.csv");
    WriteIndustryLists.writeListAll(list, "indall_shipping.csv");
  }

  /**
   *
   * @param dRList
   */
  private static void buildTransportsList(List<CompanyDerived> dRList) {
    final List<CompanyDerived> list = new ArrayList<>();

    for (final CompanyDerived cdr : dRList) {
      final String ind = cdr.getFd().getIndustry();
      final int id = WriteIndustryLists.getId(ind, 3);
      if (id == 524) {
        if (!cdr.getTicker().equalsIgnoreCase("CAR")) {
          list.add(cdr);
        }
      }
    }

    WriteIndustryLists.writeListTop(list, "ind_transports.csv");

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

  /**
   *
   * @param dRList
   * @return //
   */
//  private static List<String> getIndustryList(List<CompanyDerived> dRList) {
//    final Set<String> indSet = new HashSet<>();
//    for (final CompanyDerived cdr : dRList) {
//      indSet.add(cdr.getFd().getIndustry());
//    }
//
//    final List<String> retList = new ArrayList<>(indSet);
//    Collections.sort(retList);
//    return new ArrayList<>(retList);
//  }

  /**
   *
   * @param dRList
   * @return
   */
//  private static List<String> getSectorList(List<CompanyDerived> dRList) {
//    final Set<String> secSet = new HashSet<>();
//    for (final CompanyDerived cdr : dRList) {
//      secSet.add(cdr.getFd().getSector());
//    }
//
//    final List<String> retList = new ArrayList<>(secSet);
//    Collections.sort(retList);
//    return new ArrayList<>(retList);
//  }

  /**
   * 
   */
  private static void setInvalidTickers() {
    WriteIndustryLists.invalidTickers.add("GOOG");
    WriteIndustryLists.invalidTickers.add("ABX");
    WriteIndustryLists.invalidTickers.add("ATH");
    WriteIndustryLists.invalidTickers.add("PLD");
    WriteIndustryLists.invalidTickers.add("HTZ");
    WriteIndustryLists.invalidTickers.add("BAM.A");
    WriteIndustryLists.invalidTickers.add("TECK.B");

    // WriteIndustryLists.invalidTickers.add("AMCR");
    // WriteIndustryLists.invalidTickers.add("CHWY");
    // WriteIndustryLists.invalidTickers.add("GMAB");
    // WriteIndustryLists.invalidTickers.add("PACK");
    // WriteIndustryLists.invalidTickers.add("UBER");
    WriteIndustryLists.invalidTickers.add("ABNB");
    WriteIndustryLists.invalidTickers.add("AMBP");
    WriteIndustryLists.invalidTickers.add("ARHS");
    // WriteIndustryLists.invalidTickers.add("ARNC");
    WriteIndustryLists.invalidTickers.add("BEKE");
    WriteIndustryLists.invalidTickers.add("BMBL");
    WriteIndustryLists.invalidTickers.add("BNTX");
    WriteIndustryLists.invalidTickers.add("CARR");
    WriteIndustryLists.invalidTickers.add("CNR");
    WriteIndustryLists.invalidTickers.add("CPNG");
    WriteIndustryLists.invalidTickers.add("DASH");
    WriteIndustryLists.invalidTickers.add("DFH");
    WriteIndustryLists.invalidTickers.add("DOCS");
    WriteIndustryLists.invalidTickers.add("DUOL");
    WriteIndustryLists.invalidTickers.add("GDRX");
    WriteIndustryLists.invalidTickers.add("GMAB");
    WriteIndustryLists.invalidTickers.add("EVEX");
    WriteIndustryLists.invalidTickers.add("GFS");
    WriteIndustryLists.invalidTickers.add("LEGN");
    WriteIndustryLists.invalidTickers.add("LI");
    WriteIndustryLists.invalidTickers.add("JOBY");
    WriteIndustryLists.invalidTickers.add("KRTX");
    WriteIndustryLists.invalidTickers.add("LCID");
    WriteIndustryLists.invalidTickers.add("MIME");
    // WriteIndustryLists.invalidTickers.add("MP");
    WriteIndustryLists.invalidTickers.add("NVEI");
    WriteIndustryLists.invalidTickers.add("OGN");
    WriteIndustryLists.invalidTickers.add("PLTK");
    WriteIndustryLists.invalidTickers.add("PSNY");
    WriteIndustryLists.invalidTickers.add("PTVE");
    // WriteIndustryLists.invalidTickers.add("QS");
    // WriteIndustryLists.invalidTickers.add("REYN");
    WriteIndustryLists.invalidTickers.add("RIVN");
    WriteIndustryLists.invalidTickers.add("RPRX");
    WriteIndustryLists.invalidTickers.add("SLVM");
    WriteIndustryLists.invalidTickers.add("TFII");
    // WriteIndustryLists.invalidTickers.add("XP");
    WriteIndustryLists.invalidTickers.add("XPEV");
    WriteIndustryLists.invalidTickers.add("YNDX");
    WriteIndustryLists.invalidTickers.add("ZIM");

  }

  /**
   * 
   * @param ticker
   * @return
   */
  private static boolean validTicker(String ticker) {
    for (final String t : WriteIndustryLists.invalidTickers) {
      if (ticker.equalsIgnoreCase(t)) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   */
  private static void writeInvalidTickers() {

    try (PrintWriter pwt = new PrintWriter("out/Invalid_Tickers.csv")) {
      for (final String t : WriteIndustryLists.invalidTickers) {
        pwt.printf("%s,US%n", t);
      }
    }
    catch (final FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param list
   * @param fname
   */
  private static void writeListAll(List<CompanyDerived> list, String fname) {
    final List<String> idxGroup = new ArrayList<>();
    Collections.sort(list, new SortByMCap());
    try (PrintWriter pw = new PrintWriter(WriteIndustryLists.listDir + fname)) {
      pw.println("Code,Exchange");
      int knt = 0;
      for (final CompanyDerived cdr : list) {
        final String str = String.format("%S,US", cdr.getTicker());
        pw.printf("%s%n", str);
        // bigListPw.printf("%s%n", str);
        idxGroup.add(str.replace(",", ":"));
        knt++;
        if (knt > 99) {
          break;
        }
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param list
   * @param fname
   */
  private static void writeListTop(List<CompanyDerived> list, String fname) {
    final List<String> idxGroup = new ArrayList<>();
    Collections.sort(list, new SortByMCap());
    try (PrintWriter pw = new PrintWriter(WriteIndustryLists.listDir + fname)) {
      pw.println("Code,Exchange");
      int knt = 0;
      for (final CompanyDerived cdr : list) {
        // if (Options.isOptionable(cdr.getTicker())) {
        final String str = String.format("%S,US", cdr.getTicker());
        pw.printf("%s%n", str);
        if (WriteIndustryLists.bigListCnt < 500) {
          WriteIndustryLists.bigListCnt++;
          WriteIndustryLists.bigListPw.printf("%s%n", str);
        }
        idxGroup.add(str.replace(",", ":"));
        knt++;
        if (knt >= 21) {
          break;
        }
        // }
      }
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }

    final String fname2 = String.format("out/customcode-%s", fname.replace(".csv", ".txt"));
    final double power = 1.0 / idxGroup.size();
    try (PrintWriter pw = new PrintWriter(fname2)) {
      pw.printf("POWER(%s", idxGroup.get(0));
      for (int i = 1; i < idxGroup.size(); i++) {
        final String s = idxGroup.get(i);
        pw.printf(" * %s", s);
      }
      pw.printf(", POWER=%.4f)%n", power);
    }
    // POWER(BA:US * NOC:US * LMT:US * RTX:US, POWER=0.25)
    catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

}
