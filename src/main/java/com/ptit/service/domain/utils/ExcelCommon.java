package com.ptit.service.domain.utils;

import com.ommanisoft.common.exceptions.ExceptionOm;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class ExcelCommon {

  public static Workbook read(String filePath) {
    if (!filePath.endsWith(".xlsx") && !filePath.endsWith(".xls")) {
      throw new ExceptionOm(HttpStatus.BAD_REQUEST, "Chỉ chấp nhận file excel.");
    }
    File file = new File(filePath);
    if (!file.exists()) {
      throw new ExceptionOm(HttpStatus.BAD_REQUEST, "File không tồn tại");
    }

    try {
      FileInputStream excelFile = new FileInputStream(new File(filePath));

      return new XSSFWorkbook(excelFile);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public static Object getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case _NONE:
      case BLANK:
        return null;
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        return cell.getNumericCellValue();
      case BOOLEAN:
        return cell.getBooleanCellValue();
      default:
        throw new RuntimeException("get cell value error with cell type " + cell.getCellType());
    }
  }

  public static boolean isEmptyRow(Row row) {
    if (row == null) {
      return true;
    }
    if (row.getLastCellNum() <= 0) {
      return true;
    }
    for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
      Cell cell = row.getCell(cellNum);
      if (cell != null
          && cell.getCellType() != CellType.BLANK
          && StringUtils.hasLength(cell.toString())) {
        return false;
      }
    }
    return true;
  }
}
