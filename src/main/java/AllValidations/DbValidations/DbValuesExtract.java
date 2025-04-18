package AllValidations.DbValidations;


import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.path.json.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static AllValidations.AllValidations.status;
import static AllValidations.AllValidations.validationStatusLog;
import static AllValidations.DbValidations.DbValidation.getDbQueriesForExtract;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;
import static Listners.Reports.ExtentReport.testStatus;


public class DbValuesExtract {

    private static final Logger log = LoggerFactory.getLogger(DbValuesExtract.class);


    public static void overallDbExtract(Map<String, Object> testData, Map<String, String> allExtracts) {


        String queries = (String) testData.get(DB_EXTRACT);
        String app = (String) testData.get(APP);
        String env = (String) testData.get(ENV);


        if (queries == null || queries.isBlank()) {
            log.warn(validationStatusLog(DB_EXTRACT, NOT_ENABLE));
            allExtracts.put(DB_EXTRACT, NOT_ENABLE);
            return;
        }


        ExtentTest test = ((ExtentTest) testData.get(EXTENT)).createNode(DB_EXTRACT);

        List<String> listOfQueries = Arrays.stream(queries.split(SPLIT_REGEX))
                .map(String::trim) // Trim spaces
                .toList();
        List<DbQueryRequest> listOfDbRequests = getDbQueriesForExtract(app, listOfQueries, testData);
        Map<String, JsonNode> actualDbValues = MysqlDbOps.actualDbResponses(app, env, listOfDbRequests);

        String status = status(allDbExtraction(actualDbValues, listOfDbRequests, testData));

        allExtracts.put(DB_EXTRACT, status);
        listOfDbRequests.forEach(req->
            test.info(req.queryName()).info(req.query()).info("DbTemplate:"+req.DbJson().toPrettyString()).info("dbRes:"+actualDbValues.get(req.queryName()))

        );
        testStatus(DB_EXTRACT, status, test);
        log.info(validationStatusLog(DB_EXTRACT, status));

    }


    private static boolean allDbExtraction(Map<String, JsonNode> actualDbValues, List<DbQueryRequest> queries, Map<String, Object> testData) {

        final boolean[] status = {true};

        queries.forEach(req -> {
            JsonNode actualJson = actualDbValues.get(req.queryName());
            status[0] = status[0] && dbExtract(req.queryName(), req.DbJson(), actualJson, testData);

        });

        return status[0];
    }

    protected static boolean dbExtract(String queryName, JsonNode expJson, JsonNode actualJson, Map<String, Object> testData) {


        return extractValues(DB_EXTRACT, queryName, expJson, actualJson, testData);
    }


    protected static boolean extractValues(String captureType, String queryName, JsonNode captureTemplate, JsonNode responseJson, Map<String, Object> testData) {
        // Early return if expJson is null or empty
        if (captureTemplate == null || captureTemplate.isEmpty()) {
            log.warn( "{} for this query is empty: {}" ,captureType , queryName);
            return false; // Got some exception
        }
        if (responseJson == null) {
            log.warn("{}:Response is null for the query: {}" ,captureType , queryName);
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
                log.warn(String.format("%s '%s' not found in query result: %s", captureType, key, queryName));
                isCaptureSuccessful[0] = false;
                return; // Skip to the next field
            }

            // Check if the column name exists in the testData map
            if (!testData.containsKey(columnName)) {
                log.warn(String.format("Column name '%s' not found in TestData Sheet", columnName));
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
