package Listners;

public class CommonVariables {

    //null value regex
    public static final String NULL_REPLACE_REGEX = "$null";
    public static final String SPLIT_REGEX = "\\|";
    //status
    public static final String PASS = "PASSED";
    public static final String FAIL = "FAILED";
    public static final String SKIP = "SKIPPED";
    public static final String NOT_ENABLE = "NOT_ENABLED";
    //log msg variables
    public static final String LOG_MSG_FILE_NAME = "messages";

    //dbConnection details
    public static final String CONFIG = "config";
    public static final String FILE_NOT_FOUND_KEY = "FileNotFound";
    public static final String INVALID_ROW = "InvalidRowNumber";
    //RestApi variables
    public static final String STATUS_CODE = "statusCode";
    public static final String RES_HEADERS = "headers";
    public static final String RES_BODY = "body";
    public static final String RES_TIME = "responseTime";
    public static final String ERROR = "error";
    //Report
    public static final String EXTENT = "Extent";
    //Types of Validations or comparison variables
    public static final String DB_VALIDATION = "db";
    public static final String JSON_VALIDATION = "json";
    public static final String ALL_VALIDATION_STATUS = NOT_ENABLE;
    private CommonVariables() {
    }


}
