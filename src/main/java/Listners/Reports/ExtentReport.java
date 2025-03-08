package Listners.Reports;

import AllValidations.ValidationResponses;
import Listners.ConfigReader;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;


import java.util.List;
import java.util.Map;

import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;

public class ExtentReport {

    String json = "{ \"name\": \"John Doe\", \"age\": 30, \"city\": \"New York\" }";




   public void generateExtentReport(List<Map<String,Object>> testData){

       String path = ConfigReader.get("ExtentReportPath",CONFIG);
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(path);
        htmlReporter.config().setDocumentTitle("Test Report");
        htmlReporter.config().setReportName("Automation Test Results");
        htmlReporter.config().setTheme(Theme.STANDARD);

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(htmlReporter);



        testData.forEach(data->
            singleTestCaseReport(data,extent)
        );
        extent.flush();

    }

    void singleTestCaseReport(Map<String,Object> data,ExtentReports extent){

        String testcaseName = String.valueOf(data.get(TESTCASE));
        String testId = String.valueOf(data.get(TC_ID));
        String overallStatus = String.valueOf(data.get(TESTCASE_STATUS));
        Map<String,ValidationResponses>  validations = (Map<String, ValidationResponses>) data.get(VALIDATIONS);

        Map<String,String> extractions = (Map<String, String>) data.get(EXTRACTIONS);

        ExtentTest test = extent.createTest(testId,testcaseName);

        extractions.forEach((key,value)->{
            ExtentTest testNode = test.createNode(key);
            testStatus(key,value,testNode);

        });
        validations.forEach((key,value)->{
            ExtentTest testNode = test.createNode(key);
            testStatus(key,value.overallStatus(),testNode);

        });


        overall(overallStatus,test);
        test.info(json);






    }

    void overall(String status,ExtentTest test){
        switch (status){
            case PASS ->  test.pass(TESTCASE_STATUS);
            case FAIL -> test.fail(TESTCASE_STATUS);
            default -> test.info(TESTCASE_STATUS);
        }
    }

    String evidencePreviewTag(String jsonString){
        return  "<div style='position: relative; display: inline-block;'>" +
                "<span style='cursor: pointer; color: blue; text-decoration: underline;'>Hover here for JSON preview</span>" +
                "<div style='visibility: hidden; opacity: 0; position: absolute; top: 100%; left: 50%; transform: translateX(-50%); background-color: #f9f9f9; padding: 10px; border: 1px solid #ccc; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); z-index: 1000; width: 200px; text-align: left; font-family: monospace; white-space: pre-wrap; transition: visibility 0s, opacity 0.3s;'>" +
                jsonString +
                "</div>" +
                "</div>" +
                "<style>" +
                "div:hover > div { visibility: visible; opacity: 1; }" +
                "</style>";
    }



    void testStatus(String validationName,String status,ExtentTest test){



        String evidencePath = "";
        switch (status) {
            case PASS->
                test.pass(validationName);

            case FAIL->
                test.fail(validationName);


            case SKIP->
                test.skip(validationName);

            default->

                test.info(validationName + " - Unknown Status").info("Evidence: " + evidencePath);


        }
    }




}
