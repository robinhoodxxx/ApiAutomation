package AllValidations;


import AllValidations.DbValidations.DbValidation;
import AllValidations.DbValidations.DbValuesExtract;
import AllValidations.Responsevalidation.JsonResValidation;
import AllValidations.Responsevalidation.ResponseCodeValidation;
import AllValidations.Responsevalidation.SchemaValidation;
import AllValidations.valuesExtract.ValuesExtract;
import Services.RestApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static Listners.CommonVariables.FAIL;
import static Listners.CommonVariables.PASS;
import static Listners.DataSheet.*;

public class AllValidations {

    private static final Logger log = LoggerFactory.getLogger(AllValidations.class);



    private AllValidations() {
    }


    public static String status(boolean status) {

        return status ? PASS : FAIL;
    }

    public static ValidationResponses emptyValidationResponses(String status) {
        return new ValidationResponses(status, new ArrayList<>());
    }

    public static String validationStatusLog(String validationType, String overallStatus) {

        return String.format("%s is %s", validationType, overallStatus);
    }


    public static void executionOpsOrder(Map<String, Object> testData) {

        Map<String, ValidationResponses> validations = new LinkedHashMap<>();
        Map<String, String> extractions = new LinkedHashMap<>();

        DbValuesExtract.overallDbExtract(testData, extractions);
        RestApi.serviceExecution(testData);
        ValuesExtract.requestCapture(testData,extractions);
        ValuesExtract.responseCapture(testData, extractions);
        JsonResValidation.responseValidation(testData, validations);
        ResponseCodeValidation.responseCodeValidation(testData, validations);
        SchemaValidation.schemaValidation(testData, validations);
        DbValidation.overallDbValidation(testData, validations);


        String testCaseStatus = overallTestcaseStatus(validations, extractions);
        testData.put(TESTCASE_STATUS, testCaseStatus);
        testData.put(EXTRACTIONS, extractions);
        testData.put(VALIDATIONS, validations);
        log.info(validationStatusLog(TESTCASE_STATUS, testCaseStatus));

    }

    public static String overallTestcaseStatus(Map<String, ValidationResponses> validations, Map<String, String> extractions) {

        boolean hasFailedExtraction = extractions.containsValue(FAIL);

        boolean hasFailedValidation = validations.values().stream().anyMatch(res ->
                res.overallStatus().equals(FAIL));

        return hasFailedExtraction || hasFailedValidation ? FAIL : PASS;
    }


}
