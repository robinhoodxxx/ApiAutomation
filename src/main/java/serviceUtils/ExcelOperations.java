package serviceUtils;

import Listners.ConfigReader;
import Listners.CustomLogger;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static Listners.CommonVariables.*;
import static Listners.DataSheet.RUN_FLAG;
import static Listners.DataSheet.TestDataFixedExcelColumns;

public class ExcelOperations {
    private static final CustomLogger log = CustomLogger.getInstance();


    public static Map<String, Object> readExcelSingleRow(String excelFilePath, int rowNumber) {
        if (rowNumber < 1) {
            log.error(String.format(ConfigReader.get(INVALID_ROW, LOG_MSG_FILE_NAME), rowNumber + 1, excelFilePath));
        }
        Map<String, Object> rowData = new LinkedHashMap<>();
        try (InputStream fis = new FileInputStream(excelFilePath); Workbook workbook = new XSSFWorkbook(fis)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();


            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0); // First row for headers

            // Loop through the specified row
            Row dataRow = sheet.getRow(rowNumber);
            if (dataRow != null) { // Ensure the row exists
                for (Cell headerCell : headerRow) {
                    String columnName = headerCell.getStringCellValue().trim(); // Get the column header

                    if (columnName.isBlank()) {
                        break;
                    }

                    Cell dataCell = dataRow.getCell(headerCell.getColumnIndex());

                    String data = getCellValueAsString(dataCell, evaluator);
                    data = data.equals(NULL_REPLACE_REGEX) ? null : data;
                    rowData.put(columnName, data);

                }
            } else {
                throw new NullPointerException(String.format(ConfigReader.get("EmptyRow", LOG_MSG_FILE_NAME), rowNumber, excelFilePath));
            }
        } catch (FileNotFoundException e) {
            log.error(String.format(ConfigReader.get(FILE_NOT_FOUND_KEY, LOG_MSG_FILE_NAME), excelFilePath), e);

        } catch (Exception e) {
            log.error("Excel ReadSingleRow failed for row:" + rowNumber + " for file" + excelFilePath, e);
        }

        return rowData;
    }

    private static boolean executionReadyMap(Cell cell, int row) {


        try {

            String runFlag = cell.getStringCellValue().trim();

            if (runFlag.equalsIgnoreCase("Y") || runFlag.equalsIgnoreCase("YES")) { //Finding the only "Y" Run testcases
                return true;
            }
        } catch (IllegalStateException e) {
            log.warning(String.format(" RowNo: %s for %s column cellValue is not String DataType :", row + 1, RUN_FLAG), e);
        } catch (Exception e) {
            log.warning(e.getMessage(), e);
        }
        return false;
    }

    public static List<Integer> readExecutionReadyData(String excelFilePath) {
        List<Integer> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(excelFilePath); Workbook workbook = new XSSFWorkbook(fis)) {


            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {  // Skipping header row
                Cell runFlagCell = sheet.getRow(i).getCell(0);

                if (runFlagCell == null || runFlagCell.getCellType() == CellType.BLANK) {
                    break;
                }

                if (executionReadyMap(runFlagCell, i)) {
                    list.add(i);
                }

            }
            log.info("Total Testcase Count is: " + list.size());
        } catch (FileNotFoundException e) {
            log.error(String.format(ConfigReader.get(FILE_NOT_FOUND_KEY, LOG_MSG_FILE_NAME), excelFilePath), e);
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;

    }


    public static List<Map<String, Object>> readExcelData(String excelFilePath) {
        List<Map<String, Object>> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream((excelFilePath)); Workbook workbook = new XSSFWorkbook(fis)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();


            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);  // Assuming first row is the header

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {  // Skipping header row
                Map<String, Object> dataMap = new LinkedHashMap<>();

                Row dataRow = sheet.getRow(i);
                for (Cell headerCell : headerRow) {
                    String columnName = headerCell.getStringCellValue();  // Get column name from header
                    Cell dataCell = dataRow.getCell(headerCell.getColumnIndex());
                    if (dataCell != null) {
                        dataMap.put(columnName, getCellValueAsString(dataCell, evaluator));
                    }
                }
                list.add(dataMap);
            }
        } catch (FileNotFoundException e) {
            log.error(String.format(ConfigReader.get(FILE_NOT_FOUND_KEY, LOG_MSG_FILE_NAME), excelFilePath), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }

    private static String getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return ""; // Check for null cell
        DataFormatter dataFormatter = new DataFormatter(); // Create a DataFormatter instance


        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC ->
                // Format numeric values using DataFormatter to get the exact representation
                    dataFormatter.formatCellValue(cell);
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> evaluateFormulaCell(cell, evaluator); // Evaluate formulas and return as string
            default -> "";
        };
    }

    private static String evaluateFormulaCell(Cell cell, FormulaEvaluator evaluator) {
        CellValue cellValue = evaluator.evaluate(cell); // Evaluate the formula
        return formatCellValueAsString(cellValue); // Convert evaluated value to string
    }

    private static String formatCellValueAsString(CellValue cellValue) {
        return switch (cellValue.getCellType()) {
            case STRING -> cellValue.getStringValue();
            case NUMERIC -> {
                double num = cellValue.getNumberValue();
                yield num % 1 == 0 ? String.valueOf((int) num) : String.valueOf(num);
            }

            case BOOLEAN -> String.valueOf(cellValue.getBooleanValue());

            case ERROR -> {
                String s = "Error: " + cellValue.getErrorValue();
                log.warning("formatCellValueAsString throws: " + s);
                yield s;
            }
            default -> "";
        };
    }


    private static void setCellValue(Cell cell, Object value) {


        if (cell.getCellType() == CellType.FORMULA) {
            return;
        }

        switch (value) {
            case null -> cell.setCellValue("");
            case String s -> cell.setCellValue(s);
            case Integer i -> cell.setCellValue(i);
            case Double v -> cell.setCellValue(v);
            case Boolean b -> cell.setCellValue(b);
            default ->
                // Handle other types as strings
                    cell.setCellValue(value.toString());
        }
    }

    public static void updateExcelRow(String excelFilePath, Map<String, Object> data, int rowNumber) {
        if (rowNumber < 1) {
            log.error(String.format(ConfigReader.get(INVALID_ROW, LOG_MSG_FILE_NAME), rowNumber + 1, excelFilePath));
            return;
        }

        try (FileInputStream fis = new FileInputStream(excelFilePath); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Get first sheet

            // Get header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row is missing in the Excel file: " + excelFilePath);
            }

            // Get row, or create if missing
            Row rowToUpdate = sheet.getRow(rowNumber);
            if (rowToUpdate == null) {
                rowToUpdate = sheet.createRow(rowNumber);
            }


            // Iterate through headers
            for (Cell headerCell : headerRow) {
                String header = headerCell.getStringCellValue().trim();

                if (TestDataFixedExcelColumns.contains(header) || !data.containsKey(header)) {
                    continue;
                }

                // Get cell
                Cell cellToUpdate = rowToUpdate.getCell(headerCell.getColumnIndex());

                if (cellToUpdate == null) {
                    cellToUpdate = rowToUpdate.createCell(headerCell.getColumnIndex());
                }

                // Get value
                String value = data.get(header) == null ? NULL_REPLACE_REGEX : data.get(header).toString();
                // Update the value
                setCellValue(cellToUpdate, value);

            }

            // Save file properly
            try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
                workbook.write(fos);

            }

        } catch (FileNotFoundException e) {
            log.error(String.format(ConfigReader.get(FILE_NOT_FOUND_KEY, LOG_MSG_FILE_NAME), excelFilePath), e);
        } catch (Exception e) {
            log.error("Excel update failed for row:" + rowNumber + " for file" + excelFilePath, e);
        }
    }


    public static Recordset getFilloRecord(String fileName, String query) {

        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(fileName);

            Recordset recordset = connection.executeQuery(query);

            if (!recordset.next()) {
                log.info("NO records for the " + query);
                return null;
            }
            return recordset;

        } catch (Exception e) {
            log.error("Exception triggered in filo", e);
        }

        return null;
    }


    public static void createExcelReport(List<Map<String, Object>> data, String filePath) {
        // Create a new workbook and a sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create a font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.getFillBackgroundColor();

        // Create the header row
        Row headerRow = sheet.createRow(0);
        AtomicInteger colNum = new AtomicInteger();
        for (String key : data.getFirst().keySet()) {
            Cell cell = headerRow.createCell(colNum.getAndIncrement());
            cell.setCellValue(key);
            cell.setCellStyle(headerCellStyle);
        }

        // Create data rows
        AtomicInteger rowNum = new AtomicInteger(1);
        data.forEach(rowData -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            colNum.set(0);

            for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                String key = entry.getKey();
                Cell cell = row.createCell(colNum.getAndIncrement());
                Object value = rowData.get(key);
                setCellValue(cell, value); // Handle different data types
            }
        });

        // Resize all columns to fit the content size

        for (int i = 0; i < colNum.get(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            log.info("Excel Report created successfully :" + filePath);
        } catch (Exception e) {
            log.warning("Excel report failed ", e);
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                log.warning("Excel report failed ", e);
            }
        }
    }


}
