package net.ajaskey.market.tools.SIP.excel;

public class CellStringModel extends CellModel {

  private final String val;

  public CellStringModel(String s) {
    this.val = s;
    this.cellType = CellModel.StringType;
  }

  public String getVal() {
    return this.val;
  }

}
