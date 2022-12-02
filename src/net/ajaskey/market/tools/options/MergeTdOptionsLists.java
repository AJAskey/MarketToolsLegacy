package net.ajaskey.market.tools.options;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;
import net.ajaskey.market.tools.SIP.SipUtils;

public class MergeTdOptionsLists {

  static String mainListName    = "D:/dev/eclipse-markettools/MarketTools/input/OptionDataTd.csv";
  static String listToMergeName = "D:/dev/MarketTools - dev/lists/OptionDataTd-20221126-162829.csv";
  // static String listToMergeName =
  // "D:/dev/OptionsDev/out/TdOptions-oddsends.csv";
  // D:/dev/OptionsDev/out/TdOptions-oddsends.csv"
  static String newListName = "D:/dev/eclipse-markettools/MarketTools/out/newOptionDataTd.csv";

  public class DataStruct {

    String  code;
    long    putOi;
    long    callOi;
    double  pcOi;
    long    putDollarOi;
    long    callDollarOi;
    double  pcDollarOi;
    boolean valid;

    public DataStruct(String s) {
      valid = false;
      try {
        String fld[] = s.split(",");
        code = fld[0].trim();
        if (code.length() > 0) {
          putOi = SipUtils.parseLong(fld[1]);
          callOi = SipUtils.parseLong(fld[2]);
          if (putOi > 0L && callOi > 0L) {
            pcOi = SipUtils.parseDouble(fld[3]);
            putDollarOi = SipUtils.parseLong(fld[4]);
            callDollarOi = SipUtils.parseLong(fld[5]);
            pcDollarOi = SipUtils.parseDouble(fld[6]);
            valid = true;
          }
        }
      }
      catch (

      Exception e) {
        valid = false;
      }
    }

    @Override
    public String toString() {
      String ret = String.format("%s, %d, %d, %f, %d, %d, %f", code, putOi, callOi, pcOi, putDollarOi, callDollarOi, pcDollarOi);
      return ret;
    }

  }

  static MergeTdOptionsLists mtol         = new MergeTdOptionsLists();
  static List<DataStruct>    existingList = new ArrayList<>();
  static List<DataStruct>    newList      = new ArrayList<>();

  public static void main(String[] args) {

    List<String> mainList = TextUtils.readTextFile(mainListName, true);
    for (String s : mainList) {
      DataStruct ds = mtol.new DataStruct(s);
      if (ds.valid) {
        existingList.add(ds);
//        System.out.println(ds);
      }
    }

    System.out.printf("%n%n%n");

    List<String> mergeList = TextUtils.readTextFile(listToMergeName, true);
    for (String s : mergeList) {
      DataStruct ds = mtol.new DataStruct(s);
      if (ds.valid) {
        newList.add(ds);
//        System.out.println(ds);
      }
    }

    List<DataStruct> updateList = new ArrayList<>();

    /**
     * Update existing data
     */
    for (DataStruct current : existingList) {
      DataStruct redo = null;
      for (DataStruct ds : newList) {
        if (current.code.equals(ds.code)) {
          System.out.printf("%s\t%s%n", current.code, ds.code);
          redo = ds;
          break;
        }
      }
      // Add new redo data to updateList
      if (redo != null) {
        updateList.add(redo);
      }
      else {
        updateList.add(current);
      }
    }

    System.out.printf("updateList : %d%n", updateList.size());

    /**
     * Add data for code not in existing list
     */
    for (DataStruct redo : newList) {
      DataStruct found = null;
      for (DataStruct ds : existingList) {
        if (redo.code.equals(ds.code)) {
          System.out.printf("%s\t%s%n", redo.code, ds.code);
          found = ds;
          break;
        }
      }
      // Add non-existing redo data to updateList
      if (found == null) {
        updateList.add(redo);
      }
    }

    System.out.printf("%n%n----------------------------------------------%n%n");

    try (PrintWriter pw = new PrintWriter(newListName)) {
      pw.println("CODE, PutOI, CallOI, PCOI, PutDollarOI, CallDollarOI, PCDollarOI");
      for (DataStruct ds : updateList) {
        System.out.println(ds);
        pw.println(ds);
      }
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    System.out.printf("mList      : %d%n", existingList.size());
    System.out.printf("mrgList    : %d%n", newList.size());
    System.out.printf("updateList : %d%n", updateList.size());

  }

}
