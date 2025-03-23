package Listners.Reports;

import Listners.ConfigReader;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import static Listners.CommonVariables.*;
import static Listners.DataSheet.TESTCASE_STATUS;

public class ExtentReport {

    String json = "{ \"name\": \"John Doe\", \"age\": 30, \"city\": \"New York\" }";


    public static ExtentReports getExtent() {
        String path = ConfigReader.get("ExtentReportPath", CONFIG);
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(path);
        htmlReporter.config().setDocumentTitle("Test Report");
        htmlReporter.config().setReportName("Automation Test Results");
        htmlReporter.config().setTheme(Theme.STANDARD);

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        return extent;
    }


    public static void overall(String status, ExtentTest test) {
        switch (status) {
            case PASS -> test.pass(TESTCASE_STATUS);
            case FAIL -> test.fail(TESTCASE_STATUS);
            case SKIP -> test.skip(TESTCASE_STATUS);
            default -> test.info(TESTCASE_STATUS);
        }
    }


    public static void testStatus(String validationName, String status, ExtentTest testNode) {


        switch (status) {
            case PASS -> testNode.pass(validationName);

            case FAIL -> testNode.fail(validationName);

            case SKIP -> testNode.skip(validationName);

            default -> testNode.info(validationName + " - Unknown Status");

        }


    }


}
