package AllValidations.valuesExtract;

import AllValidations.DbValidations.DbValuesExtract;
import Listners.ConfigReader;
import Listners.CustomLogger;
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


        JsonNode expJson = JsonOperations.convertStringToJson(responseTemp);
        JsonNode actualJson = JsonOperations.convertStringToJson(actRes);

        String status = status(extractValues(RESPONSE_EXTRACT, "", expJson, actualJson, testData));
        allExtracts.put(RESPONSE_EXTRACT, status);
        log.info(validationStatusLog(RESPONSE_EXTRACT, status));
    }


    public static boolean requestCapture(JsonNode expJson, JsonNode actualJson, Map<String, Object> testData) {


        return extractValues("RequestCapture", "", expJson, actualJson, testData);
    }


}
