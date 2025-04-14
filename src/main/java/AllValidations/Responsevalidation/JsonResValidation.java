package AllValidations.Responsevalidation;


import AllValidations.ValidationResponses;
import Listners.ConfigReader;
import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static Listners.Reports.ExtentReport.testStatus;


public class JsonResValidation {

    private static final Logger log = LoggerFactory.getLogger(JsonResValidation.class);

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
        ExtentTest test = ((ExtentTest) testData.get(EXTENT)).createNode(RES_VALID);


        //getting jsonString if it is json file or jsonString
        String expRes = JsonOperations.getResponseJsonString(TemplateOps.processTemplate(expResponse, testData), app);


        Set<String> ignoreCols = Arrays.stream(ignoreColumns.split(SPLIT_REGEX))
                .map(String::trim).filter(s -> !s.isEmpty()) // Trim and remove empty spaces
                .collect(Collectors.toSet());

        ValidationResponses res = jsonResponseValidation(expRes, actRes, ignoreCols);
        allValidations.put(RES_VALID, res);
        test.info(EXP_RESPONSE_PAYLOAD+":"+expRes).info(ACTUAL_RESPONSE_RECEIVED+":"+actRes);
        test.info(RES_IGNORE_FIELDS + ": " + ignoreColumns);
        testStatus(RES_VALID, res.overallStatus(), test);
        log.info(validationStatusLog(RES_VALID, res.overallStatus()));
    }

    public static ValidationResponses jsonResponseValidation(String expRes, String actRes, Set<String> ignoreAttributes) {
        StringBuilder jsonComments = new StringBuilder();


        JsonNode actualRes = JsonOperations.convertStringToJson(actRes);
        JsonNode expResponse = JsonOperations.convertStringToJson(expRes);


        boolean result = CompareOperations.isJsonsEqual(expResponse, actualRes, jsonComments, ignoreAttributes, JSON_VALIDATION);

        return new ValidationResponses(status(result), List.of(new JsonResponse(actualRes, result, jsonComments)));
    }
}
