package net.ajaskey.market.tools.SIP.excel;

import java.util.ArrayList;
import java.util.List;

public class SheetModel {

  private WorkbookModel  parent;
  private String         sheetName;
  private List<RowModel> sheetRows = new ArrayList<>();

  /**
   *
   * @param name
   */
  public SheetModel(String name) {
    this.setParent(null);
    this.setName(name);
  }

  /**
   *
   * @param name
   * @param wbParent
   */
  public SheetModel(String name, WorkbookModel wbParent) {
    this.setName(name);
    this.setParent(wbParent);
    wbParent.addSheet(this);
  }

  public int addRow() {
    final RowModel row = new RowModel();
    this.addRow(row);
    return row.getRowNumber();
  }

  public int addRow(RowModel rm) {
    rm.setParent(this);
    this.sheetRows.add(rm);
    rm.setRowNumber(this.sheetRows.size());
    return rm.getRowNumber();
  }

  public String getName() {
    return this.sheetName;
  }

  public WorkbookModel getParent() {
    return this.parent;
  }

  public RowModel getRow(int row) {
    if (row < 1) {
      return null;
    }
    else if (row > this.sheetRows.size()) {
      return null;
    }
    else {
      return this.sheetRows.get(row - 1);
    }
  }

  public List<RowModel> getRows() {
    return this.sheetRows;
  }

  public List<RowModel> getSheetRows() {
    return this.sheetRows;
  }

  public void setName(String sheetName) {
    this.sheetName = sheetName;
  }

  public void setParent(WorkbookModel parent) {
    this.parent = parent;
  }

  public void setSheetRows(List<RowModel> sheetRows) {
    this.sheetRows = sheetRows;
  }

}
