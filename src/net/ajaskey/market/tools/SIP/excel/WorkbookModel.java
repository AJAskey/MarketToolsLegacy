package net.ajaskey.market.tools.SIP.excel;

import java.util.ArrayList;
import java.util.List;

public class WorkbookModel {

  private String           wbName;
  private List<SheetModel> wbSheets = new ArrayList<>();

  public WorkbookModel(String name) {
    this.setName(name);
  }

  public void addSheet(SheetModel sm) {
    sm.setParent(this);
    this.wbSheets.add(sm);
  }

  public SheetModel addSheet(String name) {
    final SheetModel sm = new SheetModel(name, this);
    sm.setParent(this);
    this.wbSheets.add(sm);
    return sm;
  }

  public String getName() {
    return this.wbName;
  }

  public List<SheetModel> getSheets() {
    return this.wbSheets;
  }

  public List<SheetModel> getWbSheets() {
    return this.wbSheets;
  }

  public void setName(String wbName) {
    this.wbName = wbName;
  }

  public void setWbSheets(List<SheetModel> wbSheets) {
    this.wbSheets = wbSheets;
  }

}
