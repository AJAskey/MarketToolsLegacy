package net.ajaskey.market.tools.SIP.BigDB.reports;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import net.ajaskey.market.tools.SIP.BigDB.MarketTools;
import net.ajaskey.market.tools.SIP.BigDB.dataio.FieldData;
import net.ajaskey.market.tools.SIP.BigDB.derived.CompanyDerived;
import net.ajaskey.market.tools.SIP.BigDB.reports.excel.SipWorkbook;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Scans;
import net.ajaskey.market.tools.SIP.BigDB.reports.utils.Utilities;

public class WriteExcel {

  public static void writeCompanyInfo(SipWorkbook wbook, CompanyDerived cdr) {

    FieldData fd = cdr.getFd();

    Sheet sheet = wbook.setSheet(fd.getTicker());

    Row row = wbook.setRow(sheet, 0);

    int col = 0;
    wbook.setValue(row, col++, fd.getTicker());
    wbook.setValue(row, col++, fd.getName());
    wbook.setValue(row, col++, Utilities.cleanSecInd(fd.getSector()));
    wbook.setValue(row, col++, Utilities.cleanSecInd(fd.getIndustry()));
    wbook.setValue(row, col++, MarketTools.getSnpIndexStr(fd));
    wbook.setValue(row, col++, MarketTools.getExchange(fd).toString());

    row = wbook.setRow(sheet, 1);
    wbook.setValue(row, 1, "Price");
    Cell c = wbook.setValue(row, 2, fd.getShareData().getPrice());
    CellStyle cs = wbook.createCellStyle();
    cs.setAlignment(HorizontalAlignment.RIGHT);
    DataFormat df = wbook.createDataFormat();
    cs.setDataFormat(df.getFormat("0.000"));
    c.setCellStyle(cs);

  }

  public static void main(String[] args) throws Exception {

    int year = 2022;
    int qtr = 3;

    List<CompanyDerived> dRList = Scans.findMajor(year, qtr, 20.0, 500000L);
    final List<CompanyDerived> dList = WriteZombies.findZombies(dRList, year, qtr);

    try (SipWorkbook wbook = SipWorkbook.create("out/testpoi")) {
      for (int i = 0; i < 5; i++) {
        CompanyDerived cdr = dList.get(i);
        writeCompanyInfo(wbook, cdr);
      }
    }

    System.out.println("Done.");

  }

}
