package Listners;


import java.util.Set;



public class DataSheet {
    //TestData Headers
    public static final String RUN_FLAG = "RunFlag";        //InputTestData
    public static final String TC_ID = "TestcaseId";        //InputTestData //ExcelReport
    public static final String TESTCASE = "Testcase";    //InputTestData  /ExcelReport
    public static final String SERVICE_TYPE = "ServiceType";  //InputTestData //ExcelReport
    public static final String SERVICE_NAME = "ServiceName";  //InputTestData  /ExcelReport


    public static final String APP = "App";  //InputTestData
    public static final String ENV = "Environment";  //InputTestData //ExcelReport
    public static final String SCHEMA = "ResponseSchema";  //InputTestData  //ExcelReport


    public static final String REQUEST_HEADERS = "RequestHeaders"; //InputTestData //ExcelReport
    public static final String REQUEST_PAYLOAD = "RequestPayload"; //InputTestData //ExcelReport
    public static final String EXP_RESPONSE_PAYLOAD = "ExpectedResPayload";//InputTestData //ExcelReport

    public static final String ACTUAL_RESPONSE_RECEIVED = "ActualResponseReceived";


    public static final String EXP_REQUEST_HEADERS_TEMP = "ExpectedResHeaders"; //InputTestData
    public static final String EXP_REQUEST_HEADERS = "ExpectedResHeaders"; //InputTestData
    public static final String ACTUAL_HEADERS_RECEIVED = "ActualHeadersReceived";

    public static final String HEADERS_EXTRACT = "HeadersExtract"; //InputTestData
    public static final String REQUEST_EXTRACT = "RequestExtract";  //InputTestData
    public static final String RES_IGNORE_FIELDS = "ResponseIgnoreFields";  //InputTestData


    public static final String RESPONSE_EXTRACT = "ResponseExtract";  //InputTestData
    public static final String DB_EXTRACT = "PreDbExtract";  //InputTestData

    public static final String EXP_STATUS_CODE = "ExpStatusCodes"; //InputTestData
    public static final String ACTUAL_STATUS_CODE = "ActualStatusCode";
    public static final String ACTUAL_RES_TIME = "ResponseTime";
    public static final String SERVICE_STATUS = "ServiceStatus";


    public static final String DB_VALID = "DbValidation"; //InputTestData
    public static final String DB_QUERIES = "DbQueries"; //InputTestData
    public static final String DB_IGNORE_FIELDS = "DbIgnoreFields"; //InputTestData
    public static final String VALIDATIONS = "AllValidations";
    public static final String EXTRACTIONS = "AllExtractions";

    public static final String FIELD_VALID = "FieldValidation"; //InputTestData
    public static final String RES_VALID = "ResponseValidation";
    public static final String SCHEMA_VALID = "SchemaValidation"; //InputTestData

    public static final String RES_CODE_VALID = "ResponseCodeValidation";
    public static final String TESTCASE_STATUS = "OverallStatus";


    public static final Set<String> TestDataFixedExcelColumns = Set.of(
            RUN_FLAG,
            TC_ID,
            TESTCASE,
            SERVICE_TYPE,
            SERVICE_NAME,
            APP,
            ENV,
            SCHEMA,
            REQUEST_HEADERS,
            REQUEST_PAYLOAD,
            EXP_RESPONSE_PAYLOAD,
            EXP_REQUEST_HEADERS_TEMP,
            RES_IGNORE_FIELDS,
            EXP_STATUS_CODE,
            HEADERS_EXTRACT,
            REQUEST_EXTRACT,
            RESPONSE_EXTRACT,
            DB_EXTRACT,
            DB_VALID,
            DB_QUERIES,
            DB_IGNORE_FIELDS,
            FIELD_VALID
    );


}
