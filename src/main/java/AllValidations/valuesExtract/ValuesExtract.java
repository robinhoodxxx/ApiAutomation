package AllValidations.valuesExtract;


import Listners.ConfigReader;
import Listners.CustomLogger;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.path.json.JsonPath;
import serviceUtils.JsonOperations;

import java.util.Map;

import static AllValidations.AllValidations.status;
import static AllValidations.AllValidations.validationStatusLog;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;


public class ValuesExtract  {

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


        JsonNode expJson = JsonOperations.convertStringToJson(responseTemp);
        JsonNode actualJson = JsonOperations.convertStringToJson(actRes);

        String responseExtractStatus = status(extractValues(RESPONSE_EXTRACT, "", expJson, actualJson, testData));
        allExtracts.put(RESPONSE_EXTRACT, responseExtractStatus);
        log.info(validationStatusLog(RESPONSE_EXTRACT, responseExtractStatus));
    }


    public static void requestCapture(Map<String, Object> testData, Map<String, String> allExtracts) {

        String requestTemp = (String) testData.get(REQUEST_EXTRACT);
        String requestPayload = (String) testData.get(REQUEST_PAYLOAD);

        if (requestTemp==null||requestTemp.isBlank()){
            allExtracts.put(REQUEST_EXTRACT, NOT_ENABLE);
            log.info(validationStatusLog(REQUEST_EXTRACT,NOT_ENABLE));
            return;
        }

        if (requestPayload==null||requestPayload.isBlank()){
            allExtracts.put(REQUEST_EXTRACT, SKIP);
            log.info(validationStatusLog(REQUEST_EXTRACT,SKIP));
            return;
        }

        JsonNode requestExtract = JsonOperations.convertStringToJson(requestTemp);
        JsonNode reqPayload = JsonOperations.convertStringToJson(requestPayload);


        String requestExtractStatus = status(extractValues("RequestCapture", "", requestExtract, reqPayload, testData));
        allExtracts.put(REQUEST_EXTRACT, requestExtractStatus);
        log.info(validationStatusLog(REQUEST_EXTRACT,requestExtractStatus));
    }

    protected static boolean extractValues(String captureType, String queryName, JsonNode captureTemplate, JsonNode responseJson, Map<String, Object> testData) {
        // Early return if expJson is null or empty
        if (captureTemplate == null || captureTemplate.isEmpty()) {
            log.warning(captureType + " for this query is empty: " + queryName);
            return false; // Got some exception
        }
        if (responseJson == null) {
            log.warning(captureType + ":Response is null for the query: " + queryName);
            return false;
        }

        JsonPath jsonPathObj = JsonPath.from(responseJson.toString());

        final boolean[] isCaptureSuccessful = {true};

        // Iterate through expected JSON fields
        captureTemplate.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            String columnName = entry.getValue().asText().replaceAll("[()]", "").trim();

            // Check if the key exists in the actual JSON
            Object result = jsonPathObj.get(key);

            if (result == null) {
                log.warning(String.format("%s '%s' not found in query result: %s", captureType, key, queryName));
                isCaptureSuccessful[0] = false;
                return; // Skip to the next field
            }

            // Check if the column name exists in the testData map
            if (!testData.containsKey(columnName)) {
                log.warning(String.format("Column name '%s' not found in TestData Sheet", columnName));
                isCaptureSuccessful[0] = false;
                return; // Skip to the next field
            }

            // Capture the value from actualJson and update testData
            testData.put(columnName, result);
            log.info(String.format("%s successful for key: %s -> column: %s -> value: %s", captureType, key, columnName, result));
        });

        return isCaptureSuccessful[0];
    }


}
