package AllValidations.valuesExtract;

import AllValidations.DbValidations.DbValuesExtract;
import Listners.ConfigReader;
import Listners.CustomLogger;
import Listners.Reports.ExtentReport;
import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.databind.JsonNode;
import serviceUtils.JsonOperations;

import java.util.Map;

import static AllValidations.AllValidations.status;
import static AllValidations.AllValidations.validationStatusLog;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;


public class ValuesExtract extends DbValuesExtract {

    private static final CustomLogger log = CustomLogger.getInstance();


    public static void responseCapture(Map<String, Object> testData, Map<String, String> allExtracts) {


        String responseTemp = (String) testData.get(RESPONSE_EXTRACT);
        String actRes = (String) testData.get(ACTUAL_RESPONSE_RECEIVED);
        String resStatus = (String) testData.get(SERVICE_STATUS);


        if (responseTemp == null || responseTemp.isBlank()) {
            log.info(validationStatusLog(RESPONSE_EXTRACT, NOT_ENABLE));
            allExtracts.put(RESPONSE_EXTRACT, NOT_ENABLE);
            return;
        }


        if (!resStatus.equals(PASS)) {
            log.info(String.format(ConfigReader.get("skipValidation", CONFIG), RESPONSE_EXTRACT, SKIP, resStatus));
            allExtracts.put(RESPONSE_EXTRACT, SKIP);
            return;
        }


        ExtentTest test = ((ExtentTest) testData.get(EXTENT)).createNode(RESPONSE_EXTRACT);

        JsonNode expJson = JsonOperations.convertStringToJson(responseTemp);
        JsonNode actualJson = JsonOperations.convertStringToJson(actRes);

        String responseExtractStatus = status(extractValues(RESPONSE_EXTRACT, "", expJson, actualJson, testData));
        allExtracts.put(RESPONSE_EXTRACT, responseExtractStatus);
        ExtentReport.testStatus(RESPONSE_EXTRACT, responseExtractStatus, test);
        test.info(responseTemp);
        log.info(validationStatusLog(RESPONSE_EXTRACT, responseExtractStatus));
    }


    public static void requestCapture(Map<String, Object> testData, Map<String, String> allExtracts) {

        String requestTemp = (String) testData.get(REQUEST_EXTRACT);
        String requestPayload = (String) testData.get(REQUEST_PAYLOAD);


        if (requestTemp == null || requestTemp.isBlank()) {
            allExtracts.put(REQUEST_EXTRACT, NOT_ENABLE);
            log.info(validationStatusLog(REQUEST_EXTRACT, NOT_ENABLE));
            return;
        }

        if (requestPayload == null || requestPayload.isBlank()) {
            allExtracts.put(REQUEST_EXTRACT, SKIP);
            log.info(validationStatusLog(REQUEST_EXTRACT, SKIP));
            return;
        }

        ExtentTest test = ((ExtentTest) testData.get(EXTENT)).createNode(REQUEST_EXTRACT);


        JsonNode requestExtract = JsonOperations.convertStringToJson(requestTemp);
        JsonNode reqPayload = JsonOperations.convertStringToJson(requestPayload);


        String requestExtractStatus = status(extractValues(REQUEST_EXTRACT, "", requestExtract, reqPayload, testData));
        allExtracts.put(REQUEST_EXTRACT, requestExtractStatus);
        ExtentReport.testStatus(RESPONSE_EXTRACT, requestExtractStatus, test);
        test.info(requestTemp);

        log.info(validationStatusLog(REQUEST_EXTRACT, requestExtractStatus));
    }


}
