package net.ajaskey.market.tools;

import java.io.File;

import net.ajaskey.common.DateTime;

public class RemoveWeekendFiles {

  public static void main(String[] args) {

    File directoryPath = new File("input/tdstats-data");
    File fileList[] = directoryPath.listFiles();
    for (File f : fileList) {
      String fld[] = f.getName().split("-");
      DateTime dt = new DateTime(fld[1], "yyyyMMdd");
      String sDate = dt.toFullString();
      if (sDate.startsWith("Sat") || sDate.startsWith("Sun")) {
        System.out.printf("%s\t%s%n", fld[1], dt.toFullString());
        f.delete();
      }
    }

  }

}
