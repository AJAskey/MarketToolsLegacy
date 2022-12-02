package net.ajaskey.market.tools.SIP.excel;

public class CellNumberModel extends CellModel {

  private final double val;

  public CellNumberModel(double d) {
    this.val = d;
    this.cellType = CellModel.NumberType;
  }

  public double getVal() {
    return this.val;
  }

}
