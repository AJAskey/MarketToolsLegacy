package net.ajaskey.market.tools.SIP.excel;

import java.util.List;

public class Tester {

  public static void main(String[] args) {

    final WorkbookModel wb = new WorkbookModel("testwbname.xlsx");

    final SheetModel s1 = wb.addSheet("sheet1");

    System.out.println(s1.getName());
    System.out.println(s1.getParent().getName());

    final int rn1 = s1.addRow(new RowModel());

    System.out.println(s1.getRow(rn1).getParent().getName());
    System.out.println(s1.getRow(rn1).getParent().getParent().getName());

    System.out.println(rn1);

    final CellNumberModel c11 = new CellNumberModel(123.45);
    final RowModel rm1 = s1.getRow(rn1);
    rm1.set(c11, 1);

    final CellStringModel c12 = new CellStringModel("a string");
    rm1.set(c12, 2);

    new CellStringModel("a string");
    rm1.set(c12, 4);

    final CellNumberModel c15 = new CellNumberModel(15.51);
    rm1.set(c15, 5);

    final List<RowModel> theRows = s1.getRows();
    for (final RowModel r : theRows) {
      // List<CellModel> theCells = r.getCells();

    }
  }

}
