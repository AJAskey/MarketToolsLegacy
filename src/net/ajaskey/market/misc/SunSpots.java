package net.ajaskey.market.misc;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.TextUtils;
import net.ajaskey.market.optuma.OptumaCommon;

public class SunSpots {

  public static void main(String[] args) throws FileNotFoundException {

    List<String> data = TextUtils.readTextFile("data/SN_d_tot_V20.csv", true);

    try (PrintWriter pw = new PrintWriter(OptumaCommon.optumaPath + "/DC/sunspots.csv")) {
      for (int i = 1; i < data.size(); i++) {
        String fld[] = data.get(i).split(",");
        int year = Integer.parseInt(fld[0].trim());
        int month = Integer.parseInt(fld[1].trim()) - 1;
        int day = Integer.parseInt(fld[2].trim());
        int spots = Integer.parseInt(fld[3].trim());
        if (spots >= 0) {
          DateTime dt = new DateTime(year, month, day);
          pw.println(dt.format("yyyy-MM-dd") + "," + spots);
        }
      }
    }
  }

}
