package Runner;

import AllValidations.AllValidations;
import Listners.CustomLogger;
import Listners.Reports.ExcelReport;
import Listners.Reports.ExtentReport;
import Listners.Reports.JsonReport;
import serviceUtils.ExcelOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Orchestrator {
    private static final CustomLogger log = CustomLogger.getInstance();


    public static void main(String[] args) {

        String testData = "src/main/resources/InputTestData/DataSheet/TestData.xlsx";

        runner(testData);
    }

    public static void runner(String inputSheet) {


        List<Map<String, Object>> allData = new ArrayList<>();

        List<Integer> runData = ExcelOperations.readExecutionReadyData(inputSheet);

        runData.forEach(row -> allData.add(singleRowExecution(inputSheet, row)));


        JsonReport.generateJsonReport(allData);
        ExtentReport rep = new ExtentReport();
        rep.generateExtentReport(allData);
        ExcelReport.generateExcelReport(allData);

    }

    public static Map<String, Object> singleRowExecution(String testData, int row) {

        Map<String, Object> rowData = ExcelOperations.readExcelSingleRow(testData, row);

        AllValidations.executionOpsOrder(rowData);
        ExcelOperations.updateExcelRow(testData, rowData, row);
        log.info("*******************************");

        return rowData;
    }


}
