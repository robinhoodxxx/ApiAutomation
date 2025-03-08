package Runner;

import AllValidations.AllValidations;
import Listners.Reports.ExcelReport;
import Listners.Reports.ExtentReport;
import Listners.Reports.JsonReport;
import Services.RestApi;
import serviceUtils.ExcelOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Orchestrator {


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

        RestApi.serviceExecution(rowData);
        AllValidations.allValidationsExtractions(rowData);
        ExcelOperations.updateExcelRow(testData, rowData, row);

        return rowData;
    }


}
