package com.liang.kafka.excel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lianghaiyang
 * @date 2018/07/18
 */
public class ExcelUtils {
    public static final String EXCEL_SUFFIX_XLSX = ".xlsx";
    private static final String EXCEL_SUFFIX_XLS = ".xls";
    private static final String SPACE_REGEX = "^\\s*$";
    private static final String LETTER_NUMBER_REGEX = "^[0-9a-zA-Z]+$";
    /**
     * 对应类型的正则表达式
     */
    private static final String LONG_REGEX = "^(-)?[0-9]{1,19}$";
    private static final String INT_REGEX = "^[0-9]{1,10}$";
    private static final String BOOLEAN_REGEX = "^[01]$";
    private static final String DOUBLE_REGEX = "^(-)?([0-9]*([.][0-9])*)?$";
    private static final String COMMA_LONG_REGEX = "^[1-9][0-9]{0,18}([,][1-9][0-9]{0,18})*[,]*$";

    /**
     * 读取excel文件
     *
     * @param cellTypes cellType 表示读取excel的数据类型
     *                  可以取值为: int , long , double , string, boolean, date
     */
    public static ExcelData readExcel(InputStream inputStream, String suffix, List<ExcelColumnTypeEnum> cellTypes) {
        assert suffix.equals(EXCEL_SUFFIX_XLSX) || suffix.equals(EXCEL_SUFFIX_XLS) : "不是excel文件";
        ExcelData excelData = new ExcelData();
        Map<Integer, List<Integer>> errorMap = new HashMap<>(8);
        try {
            Workbook wb;
            if (suffix.equals(EXCEL_SUFFIX_XLS)) {
                POIFSFileSystem fs = new POIFSFileSystem(inputStream);
                wb = new HSSFWorkbook(fs);
            } else {
                wb = new XSSFWorkbook(inputStream);
            }
            wb.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            // 默认只取第一个sheet
            Sheet sheet = wb.getSheetAt(0);
            excelData.setName(sheet.getSheetName());
            // 用于格式化单元格数据
            DataFormatter formatter = new DataFormatter();
            List<List<Object>> rows = new ArrayList<>();
            for (Row row : sheet) {
                if (row == null) {
                    continue;
                }
                // 获取excel第一行标题
                if (row.getRowNum() == 0) {
                    List<String> titles = new ArrayList<>();
                    for (Cell cell : row) {
                        String stringValue = formatter.formatCellValue(cell).replace(" ", "");
                        titles.add(stringValue);
                    }
                    excelData.setTitles(titles);
                    continue;
                }
                List<Object> cells = new ArrayList<>();
                // 获取从1开始的行数
                int rowNum = row.getRowNum() + 1;
                // 初始化列信息第一列为0
                // 处理excel其它行数据
                for (int i = 0; i < cellTypes.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // 将所有内容转化为文本, 并去掉两端空格
                    String stringValue = formatter.formatCellValue(cell).trim();
                    //去掉\r\n
                    stringValue = stringValue.replaceAll("[\r\n\\\\]", "");
                    ExcelColumnTypeEnum excelColumnTypeEnum = cellTypes.get(i);
                    switch (excelColumnTypeEnum) {
                        case INT:
                            boolean intMatches = stringValue.matches(INT_REGEX);
                            if (intMatches) {
                                cells.add(Integer.valueOf(stringValue));
                            } else {
                                putInErrorMap(rowNum, i + 1, errorMap);
                            }
                            break;
                        case LONG:
                            boolean longMatches = stringValue.matches(LONG_REGEX);
                            if (longMatches) {
                                cells.add(Long.valueOf(stringValue));
                            } else {
                                putInErrorMap(rowNum, i + 1, errorMap);
                            }
                            break;
                        case DOUBLE:
                            boolean doubleMatches = stringValue.matches(DOUBLE_REGEX);
                            if (doubleMatches) {
                                cells.add(Double.valueOf(stringValue));
                            } else {
                                putInErrorMap(rowNum, i + 1, errorMap);
                            }
                            break;
                        case STRING:
                            if (StringUtils.isNotEmpty(stringValue)) {
                                cells.add(stringValue);
                            } else {
                                putInErrorMap(rowNum, i + 1, errorMap);
                            }
                            break;
                        case BOOLEAN:
                            boolean booleanMatches = stringValue.matches(BOOLEAN_REGEX);
                            if (booleanMatches) {
                                cells.add(Integer.parseInt(stringValue) == 1);
                            } else {
                                putInErrorMap(rowNum, i + 1, errorMap);
                            }
                            break;
                        case DATE:
                            break;
                        case NULL_DOUBLE:
                            if (StringUtils.isNotEmpty(stringValue)) {
                                doubleMatches = stringValue.matches(DOUBLE_REGEX);
                                if (doubleMatches) {
                                    cells.add(Double.valueOf(stringValue));
                                } else {
                                    putInErrorMap(rowNum, i + 1, errorMap);
                                }
                            } else {
                                cells.add(stringValue);
                            }
                            break;
                        case NULL_LONG:
                            if (StringUtils.isNotEmpty(stringValue)) {
                                longMatches = stringValue.matches(LONG_REGEX);
                                if (longMatches) {
                                    cells.add(Long.valueOf(stringValue));
                                } else {
                                    putInErrorMap(rowNum, i + 1, errorMap);
                                }
                            } else {
                                cells.add(stringValue);
                            }
                            break;
                        case NULL_STRING:
                            if (StringUtils.isNotEmpty(stringValue)) {
                                cells.add(stringValue);
                            } else {
                                cells.add(stringValue);
                            }
                            break;
                        case COMMA_LONG:
                            // 逗号间隔的id, 去掉空格
                            stringValue = stringValue.replace(" ", "");
                            boolean commaLongMatches = stringValue.matches(COMMA_LONG_REGEX);
                            if (commaLongMatches) {
                                cells.add(stringValue);
                            } else {
                                putInErrorMap(rowNum, i + 1, errorMap);
                            }
                            break;
                        case NULL_COMMA_LONG:
                            if (StringUtils.isNotEmpty(stringValue)) {
                                // 逗号间隔的id, 去掉空格
                                stringValue = stringValue.replace(" ", "");
                                commaLongMatches = stringValue.matches(COMMA_LONG_REGEX);
                                if (commaLongMatches) {
                                    cells.add(stringValue);
                                } else {
                                    putInErrorMap(rowNum, i + 1, errorMap);
                                }
                            } else {
                                cells.add(stringValue);
                            }
                            break;
                        default:
                            break;
                    }
                }
                rows.add(cells);
            }
            excelData.setRows(rows);
            excelData.setErrorMap(errorMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excelData;
    }

    /**
     * 根据传入的行列, 将错误信息加入errorMap
     * 这里的行列, 均需从1开始
     */
    private static void putInErrorMap(Integer rowNum, Integer columnNum, Map<Integer, List<Integer>> errorMap) {
        if (errorMap.containsKey(rowNum)) {
            errorMap.get(rowNum).add(columnNum);
        } else {
            List<Integer> errorColumns = new ArrayList<>();
            errorColumns.add(columnNum);
            errorMap.put(rowNum, errorColumns);
        }
    }

    /**
     * 导出excel文件
     */
    public static void exportExcel(ExcelData data, List<ExcelColumnLockEnum> columnLocks, OutputStream out) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            String sheetName = data.getName();
            if (null == sheetName) {
                sheetName = "Sheet1";
            }
            XSSFSheet sheet = wb.createSheet(sheetName);
            // 设置保护模式的密码
            System.out.println(LocalDate.now().toString());
            writeExcel(wb, sheet, data, columnLocks);
            if (CollectionUtils.isNotEmpty(columnLocks)) {
                assert columnLocks.size() == data.getRows().get(0).size() : "锁长度和列长度不同";
                protectSheet(sheet, LocalDate.now().toString());
                System.out.println(LocalDate.now().toString());
            }
            wb.write(out);
        }
    }

    /**
     * 导出excel设置头信息
     */
    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes("UTF-8"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void writeExcel(XSSFWorkbook wb, Sheet sheet, ExcelData data, List<ExcelColumnLockEnum> columnLocks) {
        Boolean isLockedTitle = CollectionUtils.isNotEmpty(columnLocks);
        int rowIndex = writeTitlesToExcel(wb, sheet, data.getTitles(), isLockedTitle);
        writeRowsToExcel(wb, sheet, data.getRows(), rowIndex, columnLocks);
    }

    /**
     * 写入标题
     */
    private static int writeTitlesToExcel(XSSFWorkbook wb, Sheet sheet, List<String> titles, Boolean isLocked) {
        int rowIndex = 0;
        int colIndex = 0;

        Font titleFont = wb.createFont();
        XSSFCellStyle titleStyle = wb.createCellStyle();
        titleStyle.setLocked(isLocked);
        titleStyle.setFont(titleFont);
        Row titleRow = sheet.createRow(rowIndex);
        for (String field : titles) {
            Cell cell = titleRow.createCell(colIndex);
            cell.setCellValue(field);
            cell.setCellStyle(titleStyle);
            colIndex++;
        }
        rowIndex++;
        return rowIndex;
    }

    /**
     * 向Excel中写入数据
     */
    private static int writeRowsToExcel(XSSFWorkbook wb, Sheet sheet, List<List<Object>> rows, int rowIndex, List<ExcelColumnLockEnum> columnLocks) {
        int colIndex;
        XSSFCellStyle dataStyle;
        for (List<Object> rowData : rows) {
            Row dataRow = sheet.createRow(rowIndex);
            colIndex = 0;
            for (Object cellData : rowData) {
                if (CollectionUtils.isNotEmpty(columnLocks)) {
                    assert columnLocks.size() == rowData.size() : "锁长度和列长度不同";
                    boolean isLocked = columnLocks.get(colIndex).isLocked();
                    dataStyle = wb.createCellStyle();
                    dataStyle.setLocked(isLocked);
                } else {
                    dataStyle = wb.createCellStyle();
                }
                Cell cell = dataRow.createCell(colIndex);
                if (cellData != null) {
                    cell.setCellValue(cellData.toString());
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(dataStyle);
                colIndex++;
            }
            rowIndex++;
        }
        return rowIndex;
    }

    /**
     * 设置Excel操作权限
     * 如果不设置密码,则取消权限
     */
    private static void protectSheet(XSSFSheet sheet, String password) {
        if (StringUtils.isNotEmpty(password)) {
            sheet.protectSheet(password);
            sheet.enableLocking();
            CTSheetProtection sheetProtection = safeGetProtectionField(sheet);
            // 设置可以插入行
            sheetProtection.setInsertRows(false);
            sheetProtection.setDeleteRows(false);
            // 设置是否锁定单元格, 锁定后所有受保护单元格所在行列不能删除
            sheetProtection.setSelectLockedCells(false);
            sheetProtection.setSelectUnlockedCells(false);
            // 设置禁止插入列
            sheetProtection.setInsertColumns(true);
            sheetProtection.setDeleteColumns(true);
            // 设置可以格式化
            sheetProtection.setFormatCells(false);
            sheetProtection.setFormatColumns(false);
            sheetProtection.setFormatRows(false);
            // 设置可以排序和查找
            sheetProtection.setSort(false);
            sheetProtection.setAutoFilter(false);
            sheetProtection.setSheet(true);
            sheetProtection.setObjects(true);
            sheetProtection.setPivotTables(false);
        } else {
            sheet.getCTWorksheet().unsetSheetProtection();
        }
    }

    /**
     * 获取权限操作字段
     */
    private static CTSheetProtection safeGetProtectionField(XSSFSheet sheet) {
        if (!sheet.getCTWorksheet().isSetSheetProtection()) {
            return sheet.getCTWorksheet().addNewSheetProtection();
        }
        return sheet.getCTWorksheet().getSheetProtection();
    }

    /**
     * 设置列宽
     */
    private static void autoSizeColumns(Sheet sheet, int columnNumber) {

        for (int i = 0; i < columnNumber; i++) {
            int orgWidth = sheet.getColumnWidth(i);
            sheet.autoSizeColumn(i, true);
            int newWidth = sheet.getColumnWidth(i) + 100;
            if (newWidth > orgWidth) {
                sheet.setColumnWidth(i, newWidth);
            } else {
                sheet.setColumnWidth(i, orgWidth);
            }
        }
    }

    /**
     * 设置边界
     */
    private static void setBorder(XSSFCellStyle style, BorderStyle border, XSSFColor color) {
        style.setBorderTop(border);
        style.setBorderLeft(border);
        style.setBorderRight(border);
        style.setBorderBottom(border);
        style.setBorderColor(XSSFCellBorder.BorderSide.TOP, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.LEFT, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, color);
    }
}
