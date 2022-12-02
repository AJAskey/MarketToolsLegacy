package net.ajaskey.market.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ajaskey.common.TextUtils;

public class CompareIndustryLists {

  final static String listDir = "D:\\dev\\eclipse-markettools\\MarketTools\\out";

  public static void main(String[] args) {

    final File folder = new File(listDir);
    final File[] existingFiles = folder.listFiles();
    for (File f : existingFiles) {
      System.out.println("\n" + f.getAbsolutePath());
      if (f.getName().startsWith("customcode-")) {
        List<String> currentList = readCustomCode(f.getAbsolutePath());
        List<String> oldList = readCustomCode(listDir + "/Indexs/" + f.getName());
        compare(currentList, oldList);
      }
    }

  }

  private static void compare(List<String> currentList, List<String> oldList) {
    System.out.println("\n---Compare\n");
    for (String c : currentList) {
      // System.out.printf(" c=%s%n", c);
      boolean bIn = true;
      for (String o : oldList) {
        // System.out.printf(" o=%s%n", o);
        if (c.equalsIgnoreCase(o)) {
          bIn = false;
          break;
        }
      }
      if (bIn) {
        System.out.printf("IN : %s%n", c);
      }
    }

    for (String c : oldList) {
      // System.out.printf(" c=%s%n", c);
      boolean bIn = true;
      for (String o : currentList) {
        // System.out.printf(" o=%s%n", o);
        if (c.equalsIgnoreCase(o)) {
          bIn = false;
          break;
        }
      }
      if (bIn) {
        System.out.printf("OUT : %s%n", c);
      }
    }
  }

  private static List<String> readCustomCode(String theFile) {

    List<String> ret = new ArrayList<>();
    // System.out.println(theFile);

    List<String> tmp = TextUtils.readTextFile(theFile, true);
    String line = tmp.get(0);
    String line1 = line.replace("POWER(", "");
    String[] line2 = line1.split(",");
    String[] code = line2[0].split("\\*");

    for (String s : code) {
      // System.out.println(s.trim());
      ret.add(s.trim());
    }

    return ret;
  }

}
