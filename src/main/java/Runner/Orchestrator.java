package Runner;

import AllValidations.AllValidations;
import Listners.CustomLogger;
import Listners.Reports.ExcelReport;
import Listners.Reports.ExtentReport;
import Listners.Reports.JsonReport;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import serviceUtils.ExcelOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Listners.CommonVariables.EXTENT;
import static Listners.DataSheet.*;

public class Orchestrator {
    private static final CustomLogger log = CustomLogger.getInstance();


    public static void main(String[] args) {

        String testData = "src/main/resources/InputTestData/DataSheet/TestData.xlsx";

        runner(testData);
    }

    public static void runner(String inputSheet) {

        ExtentReports extent = ExtentReport.getExtent();

        List<Map<String, Object>> allData = new ArrayList<>();

        List<Integer> runData = ExcelOperations.readExecutionReadyData(inputSheet);

        runData.forEach(row -> allData.add(singleRowExecution(inputSheet, row, extent)));


        extent.flush();
        JsonReport.generateJsonReport(allData);
        ExcelReport.generateExcelReport(allData);

    }

    public static Map<String, Object> singleRowExecution(String testData, int row, ExtentReports extent) {


        Map<String, Object> rowData = ExcelOperations.readExcelSingleRow(testData, row);
        String testcaseName = String.valueOf(rowData.get(TESTCASE));
        String testId = String.valueOf(rowData.get(TC_ID));

        ExtentTest test = extent.createTest(testId, testcaseName);
        rowData.put(EXTENT, test);

        AllValidations.executionOpsOrder(rowData);
        String overallStatus = String.valueOf(rowData.get(TESTCASE_STATUS));
        ExtentReport.overall(overallStatus, test);
        rowData.remove(EXTENT);
        ExcelOperations.updateExcelRow(testData, rowData, row);
        log.info("*******************************");

        return rowData;
    }


}
