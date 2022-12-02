package net.ajaskey.market.tools.SIP.BigDB.reports.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SipWorkbook implements AutoCloseable {

  boolean              valid;
  Workbook             wb;
  private OutputStream os;

  private String filename;

  private SipWorkbook(String wbName) {

    try {
      filename = String.format("%s.xlsx", wbName);
      this.os = new FileOutputStream(new File(filename));
      this.wb = new XSSFWorkbook();
      this.valid = true;
    }
    catch (final FileNotFoundException e) {
      this.valid = false;
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unused")
  private SipWorkbook() {
    // Not Callable
  }

  @Override
  public void close() throws Exception {
    this.flush();
    this.os.close();
    this.wb.close();
  }

  public DataFormat createDataFormat() {
    return wb.createDataFormat();
  }

  public Workbook getWorkbook() {
    return wb;
  }

  public CellStyle createCellStyle() {
    return wb.createCellStyle();
  }

  public void flush() throws IOException {
    this.wb.write(this.os);
  }

  public Cell setCell(Row row, int cellId) {
    Cell cell = row.getCell(cellId);
    if (cell == null) {
      cell = row.createCell(cellId);
    }
    return cell;
  }

  public Cell setCell(Sheet sheet, int rowId, int cellId) {
    final Row row = this.setRow(sheet, rowId);
    return this.setCell(row, cellId);
  }

  public Cell setCell(String sheetName, int rowId, int cellId) {
    final Row row = this.setRow(sheetName, rowId);
    return this.setCell(row, cellId);
  }

  public Row setRow(Sheet sheet, int rowId) {
    Row row = sheet.getRow(rowId);
    if (row == null) {
      row = sheet.createRow(rowId);
    }
    return row;
  }

  public Row setRow(String sheetName, int rowId) {
    final Sheet sheet = this.setSheet(sheetName);
    return this.setRow(sheet, rowId);
  }

  public Sheet setSheet(String sheetname) {
    Sheet sheet = this.wb.getSheet(sheetname);
    if (sheet == null) {
      sheet = this.wb.createSheet(sheetname);
    }
    return sheet;
  }

  public Cell setValue(Row row, int cellId, Double val) {
    final Cell cell = this.setCell(row, cellId);
    cell.setCellValue(val);
    return cell;
  }

  public Cell setValue(Row row, int cellId, Integer val) {
    final Cell cell = this.setCell(row, cellId);
    cell.setCellValue(val);
    return cell;
  }

  public Cell setValue(Row row, int cellId, String val) {
    final Cell cell = this.setCell(row, cellId);
    cell.setCellValue(val);
    return cell;
  }

  public void setValue(Sheet sheet, int rowId, int cellId, Double val) {
    final Row row = this.setRow(sheet, rowId);
    this.setValue(row, cellId, val);
  }

  public void setValue(Sheet sheet, int rowId, int cellId, Integer val) {
    this.setRow(sheet, rowId);
    final Cell cell = this.setCell(sheet, rowId, cellId);
    cell.setCellValue(val);
  }

  public void setValue(Sheet sheet, int rowId, int cellId, String val) {
    this.setRow(sheet, rowId);
    final Cell cell = this.setCell(sheet, rowId, cellId);
    cell.setCellValue(val);
  }

  public static SipWorkbook create(String wbName) {
    return new SipWorkbook(wbName);
  }
}
