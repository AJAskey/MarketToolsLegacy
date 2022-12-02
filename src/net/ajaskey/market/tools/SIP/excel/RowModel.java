package net.ajaskey.market.tools.SIP.excel;

public class RowModel {

  private static final int MAXCOL = 1000;

  private SheetModel        parent;
  private final CellModel[] rowCells = new CellModel[100];

  private int rowNumber;

  public CellModel get(int col) {
    if (col > 0 && col < RowModel.MAXCOL) {
      return this.rowCells[col];
    }
    return null;
  }

  public SheetModel getParent() {
    return this.parent;
  }

  public int getRowNumber() {
    return this.rowNumber;
  }

  public void set(CellModel cm, int col) {
    if (col > 0 && col < RowModel.MAXCOL) {
      this.rowCells[col] = cm;
    }
  }

  public void setRowNumber(int rowNumber) {
    this.rowNumber = rowNumber;
  }

  protected void setParent(SheetModel parent) {
    this.parent = parent;
  }

}
