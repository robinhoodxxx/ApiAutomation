package Runner;

import AllValidations.AllValidations;
import Listners.Reports.ExcelReport;
import Listners.Reports.ExtentReport;
import Listners.Reports.JsonReport;
import Listners.RunStopException;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serviceUtils.ExcelOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Listners.CommonVariables.EXTENT;
import static Listners.DataSheet.*;

public class Orchestrator {

    private static final Logger log = LoggerFactory.getLogger(Orchestrator.class);


    public static void main(String[] args) {

        try {
            String testData = "src/main/resources/InputTestData/DataSheet/TestData.xlsx";

            log.info("Execution started for the Data sheet :{}",testData);
            runner(testData);

        } catch (Exception e) {
            throw new RunStopException(e);
        }finally {

        }

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
