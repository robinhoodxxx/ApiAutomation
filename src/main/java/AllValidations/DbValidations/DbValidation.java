package AllValidations.DbValidations;

import AllValidations.ValidationResponses;
import Listners.ConfigReader;
import Listners.CustomLogger;
import com.aventstack.extentreports.ExtentTest;
import com.codoid.products.fillo.Recordset;
import com.fasterxml.jackson.databind.JsonNode;
import serviceUtils.ExcelOperations;
import serviceUtils.Helper;
import serviceUtils.JsonOperations;
import serviceUtils.TemplateOps;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static AllValidations.AllValidations.status;
import static AllValidations.AllValidations.validationStatusLog;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;
import static Listners.Reports.ExtentReport.testStatus;
import static serviceUtils.CompareOperations.isJsonsEqual;


public class DbValidation {
    static final String QUERY_NAME = "QueryName";
    static final String QUERY = "Query";
    private static final CustomLogger log = CustomLogger.getInstance();

    private DbValidation() {
    }

    public static void overallDbValidation(Map<String, Object> testData, Map<String, ValidationResponses> allValidations) {


        if (!Helper.isRunEnable(testData.get(DB_VALID))) {
            log.info(validationStatusLog(DB_VALID, NOT_ENABLE));
            allValidations.put(DB_VALID, new ValidationResponses(NOT_ENABLE, new ArrayList<>()));
            return;
        }

        String app = (String) (testData.get(APP));
        String env = String.valueOf(testData.get(ENV));
        String queries = (String) testData.get(DB_QUERIES);
        String ignoreColumns = String.valueOf(testData.get(DB_IGNORE_FIELDS));
        ExtentTest test = ((ExtentTest) testData.get(EXTENT)).createNode(DB_VALID);


        if (queries == null || queries.isBlank()) {
            allValidations.put(DB_VALID, new ValidationResponses(FAIL, new ArrayList<>()));
            testStatus(DB_VALID, FAIL, test);
            log.warning(MessageFormat.format("{0} are empty ,Even {1} is Enabled", DB_QUERIES, DB_VALID));
            return;
        }

        List<String> listOfQueries = Arrays.stream(queries.split(SPLIT_REGEX))
                .map(String::trim) // Trim spaces
                .toList();


        Set<String> ignoreCols = Arrays.stream(ignoreColumns.split(SPLIT_REGEX))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());


        List<DbQueryRequest> listOfDbRequests = getDbQueriesForValidation(app, listOfQueries, testData);

        ValidationResponses res = dbValidation(app, env, listOfDbRequests, ignoreCols);
        allValidations.put(DB_VALID, res);
        testStatus(DB_VALID, res.overallStatus(), test);
        res.responses().forEach(val -> {
            DbQueryResponse db = (DbQueryResponse) val;
            test.info(db.res().queryName() + ": " + db.res().query());
            test.info(String.valueOf(db.res().DbJson()));
        });

        log.info(validationStatusLog(DB_VALID, res.overallStatus()));
    }


    private static ValidationResponses dbValidation(String app, String env, List<DbQueryRequest> queries, Set<String> ignoreColumns) {

        final boolean[] result = {true};
        List<Object> actualDbResults = new ArrayList<>();

        try {

            Map<String, JsonNode> actualDbValues = MysqlDbOps.actualDbResponses(app, env, queries);

            if (actualDbValues.isEmpty()) {
                log.warning("Db responses are came up as empty");
                return new ValidationResponses(FAIL, actualDbResults);
            }

            queries.forEach(db -> {

                StringBuilder comments = new StringBuilder();
                String queryName = db.queryName();
                JsonNode expDbJson = db.DbJson();

                JsonNode actualDbJson = actualDbValues.get(queryName);

                boolean status = isJsonsEqual(expDbJson, actualDbJson, comments, ignoreColumns, DB_VALIDATION);

                DbQueryResponse actualDbRecord = new DbQueryResponse(new DbQueryRequest(db.queryName(), db.query(), actualDbJson), status, comments);

                result[0] = result[0] && status;

                actualDbResults.add(actualDbRecord);
                log.info(comments.toString());
            });


            return new ValidationResponses(status(result[0]), actualDbResults);
        } catch (Exception e) {
            log.warning("DB Validation :" + FAIL, e);
        }

        return new ValidationResponses(FAIL, actualDbResults);
    }


    private static List<DbQueryRequest> getDbQueries(String app, List<String> queries, String validation, Map<String, Object> testData) {

        List<DbQueryRequest> queryDetails = new ArrayList<>();

        String dbQueriesFilePath = ConfigReader.get("DbQueriesFilePath", CONFIG);

        queries.forEach(queryName -> {

            final String filloQuery = "select " + QUERY_NAME + " ," + QUERY + "," + validation + " from " + app + " where " + QUERY_NAME + " = '" + queryName + "'";

            try {
                Recordset rec = ExcelOperations.getFilloRecord(dbQueriesFilePath, filloQuery);
                if (rec == null) {
                    queryDetails.add(new DbQueryRequest(queryName, "", null));
                    log.warning(String.format("No records found for this DbQuery:%s in file %s", queryName, dbQueriesFilePath));
                    return;
                }

                String actualQuery = TemplateOps.processTemplate(rec.getField(QUERY), testData);

                String expJson = rec.getField(validation);

                //Db validation we use template change for extraction we don't use template process
                if (validation.equals(DB_VALID)) {
                    expJson = TemplateOps.processTemplate(expJson, testData);
                }
                JsonNode json = JsonOperations.convertStringToJson(expJson);

                queryDetails.add(new DbQueryRequest(rec.getField(QUERY_NAME), actualQuery, json));


            } catch (Exception e) {
                log.warning("exception got triggered in getDbQueries", e);
                queryDetails.add(new DbQueryRequest(queryName, "", null));
            }
        });

        return queryDetails;
    }


    static List<DbQueryRequest> getDbQueriesForExtract(String app, List<String> queries, Map<String, Object> testData) {

        return getDbQueries(app, queries, "DbExtract", testData);
    }

    private static List<DbQueryRequest> getDbQueriesForValidation(String app, List<String> queries, Map<String, Object> testData) {

        return getDbQueries(app, queries, DB_VALID, testData);
    }

}
