package AllValidations.Responsevalidation;


import AllValidations.ValidationResponses;
import Listners.ConfigReader;
import Listners.CustomLogger;
import com.fasterxml.jackson.databind.JsonNode;
import serviceUtils.CompareOperations;
import serviceUtils.JsonOperations;
import serviceUtils.TemplateOps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static AllValidations.AllValidations.*;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;



public class JsonResValidation {

    private static final CustomLogger log = CustomLogger.getInstance();

    private JsonResValidation() {
    }


    public static void responseValidation(Map<String, Object> testData, Map<String, ValidationResponses> allValidations) {

        String resStatus = (String) testData.get(SERVICE_STATUS);

        if (!resStatus.equals(PASS)) {
            log.info(String.format(ConfigReader.get("skipValidation", CONFIG), RES_VALID, SKIP, SERVICE_STATUS));
            allValidations.put(RES_VALID, emptyValidationResponses(SKIP));
            return;
        }


        String actRes = String.valueOf(testData.get(ACTUAL_RESPONSE_RECEIVED));
        String expResponse = (String) testData.get(EXP_RESPONSE_PAYLOAD);
        String ignoreColumns = String.valueOf(testData.get(RES_IGNORE_FIELDS));
        String app = (String) testData.get(APP);

        if (expResponse == null || expResponse.isBlank()) {
            log.info(validationStatusLog(RES_VALID, NOT_ENABLE));
            allValidations.put(RES_VALID, emptyValidationResponses(NOT_ENABLE));
            return;
        }

        //getting jsonString if it is json file or jsonString
        String expRes = JsonOperations.getRequestJsonString(expResponse,app);


        Set<String> ignoreCols = Arrays.stream(ignoreColumns.split(SPLIT_REGEX))
                .map(String::trim).filter(s -> !s.isEmpty()) // Trim and remove empty spaces
                .collect(Collectors.toSet());

        ValidationResponses res = jsonResponseValidation(expRes, actRes, ignoreCols, testData);
        allValidations.put(RES_VALID, res);
        log.info(validationStatusLog(RES_VALID, res.overallStatus()));
    }

    public static ValidationResponses jsonResponseValidation(String expRes, String actRes, Set<String> ignoreAttributes, Map<String, Object> testData) {
        StringBuilder jsonComments = new StringBuilder();




        JsonNode actualRes = JsonOperations.convertStringToJson(actRes);
        JsonNode expResponse = JsonOperations.convertStringToJson(TemplateOps.processTemplate(expRes, testData));


        boolean result = CompareOperations.isJsonsEqual(expResponse, actualRes, jsonComments, ignoreAttributes, JSON_VALIDATION);

        return new ValidationResponses(status(result), List.of(new JsonResponse(actualRes, result, jsonComments)));
    }
}
