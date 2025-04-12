package Listners.Reports;

import Listners.ConfigReader;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import static Listners.CommonVariables.*;
import static Listners.DataSheet.TESTCASE_STATUS;

public class ExtentReport {



    public static ExtentReports getExtent() {
        String path = ConfigReader.get("ExtentReportPath", CONFIG);
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(path);
        htmlReporter.config().setDocumentTitle("Test Report");
        htmlReporter.config().setReportName("Automation Test Results");
        htmlReporter.config().setTheme(Theme.STANDARD);

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        return extent;
    }


    public static void overall(String status, ExtentTest test) {
        String val= TESTCASE_STATUS+":"+status;
        switch (status) {
            case PASS -> test.pass(val);
            case FAIL -> test.fail(val);
            case SKIP -> test.skip(val);
            default -> test.info(val);
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
