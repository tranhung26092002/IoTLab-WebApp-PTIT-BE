package com.ptit.service.domain.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ExcelGenerator {

  private List<String> header;
  private String sheetName;
  private XSSFWorkbook workbook;
  private XSSFSheet sheet;

  public ExcelGenerator(List<String> header, String sheetName) {
    this.header = header;
    this.sheetName = sheetName;
    workbook = new XSSFWorkbook();
    writeHeader();
  }
  private void writeHeader() {
    sheet = workbook.createSheet(sheetName);
    Row row = sheet.createRow(0);
    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setBold(true);
    font.setFontHeight(16);
    style.setFont(font);
    int column = 0;
    for (String s : header) {
      createCell(row, column, s, style);
      column++;
    }
  }
  private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
    sheet.autoSizeColumn(columnCount);
    Cell cell = row.createCell(columnCount);
    if(valueOfCell == null){
      cell.setCellValue("");
    } else if (valueOfCell instanceof Integer) {
      cell.setCellValue((Integer) valueOfCell);
    } else if (valueOfCell instanceof Long) {
      cell.setCellValue((Long) valueOfCell);
    } else if (valueOfCell instanceof Double) {
      cell.setCellValue((Double) valueOfCell);
    } else if (valueOfCell instanceof String) {
      cell.setCellValue((String) valueOfCell);
    } else {
      cell.setCellValue((Boolean) valueOfCell);
    }
    cell.setCellStyle(style);
  }
  public void write(List<List<Object>> dataList) {
    int rowCount = 1;
    CellStyle style = workbook.createCellStyle();
    XSSFFont font = workbook.createFont();
    font.setFontHeight(14);
    style.setFont(font);
    for (List<Object> record: dataList) {
      Row row = sheet.createRow(rowCount++);
      int columnCount = 0;
      for (int i = 0; i < record.size(); i ++){
        createCell(row, columnCount++, record.get(i), style);
      }
    }
  }
  public void generateExcelFile(HttpServletResponse response) {
    try{
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition", "attachment; filename="+sheetName+".xlsx");
      ServletOutputStream outputStream = response.getOutputStream();
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }
}
