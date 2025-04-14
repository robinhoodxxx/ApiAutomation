package Services;

import Listners.ConfigReader;
import Listners.Reports.ExtentReport;
import com.aventstack.extentreports.ExtentTest;
import com.codoid.products.fillo.Recordset;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serviceUtils.ExcelOperations;
import serviceUtils.JsonOperations;
import serviceUtils.TemplateOps;

import java.util.HashMap;
import java.util.Map;

import static AllValidations.AllValidations.validationStatusLog;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;
import static serviceUtils.JsonOperations.convertJsonToMap;
import static serviceUtils.JsonOperations.convertMaptoJsonString;

public class RestApi {

    private static final Logger log = LoggerFactory.getLogger(RestApi.class);
    static String url = "Url";
    static String method = "Method";

    public static void serviceExecution(Map<String, Object> testData) {

        String service = (String) testData.get(SERVICE_NAME);
        String app = (String) testData.get(APP);
        String env = (String) testData.get(ENV);
        String reqBodyTemp = (String) testData.get(REQUEST_PAYLOAD);
        String reqHeadersTemp = (String) testData.get(REQUEST_HEADERS);
        String serviceType = (String) testData.get(SERVICE_TYPE);
        ExtentTest test = ((ExtentTest) testData.get(EXTENT)).createNode(SERVICE_STATUS);


        if (service == null || service.isBlank()) {
            log.info(String.format("%s is blank or null So, ServiceExecution is %s", SERVICE_NAME, NOT_ENABLE));
            testData.put(SERVICE_STATUS, NOT_ENABLE);
            return;
        }

        String reqBody = TemplateOps.processTemplate(JsonOperations.getRequestJsonString(reqBodyTemp, app), testData);

        String reqHead = TemplateOps.processTemplate(reqHeadersTemp, testData);

        testData.put(REQUEST_HEADERS, reqHead);
        testData.put(REQUEST_PAYLOAD, reqBody);

        RestRequest req = restDetails(serviceType, service, app, env);

        if (req == null) {
            testData.put(SERVICE_STATUS, FAIL);
            return;
        }


        RestResponse response = triggerApiRequestWithRetry(req.methodType(), req.url(), reqBody, convertJsonToMap(reqHead));

        if (response == null) {
            testData.put(SERVICE_STATUS, FAIL);
            ExtentReport.testStatus(SERVICE_STATUS, FAIL, test);
            return;
        }

        testData.put(ACTUAL_STATUS_CODE, response.statusCode());
        testData.put(ACTUAL_HEADERS_RECEIVED, response.responseHeaders());
        testData.put(ACTUAL_RESPONSE_RECEIVED, response.responseBody());
        testData.put(ACTUAL_RES_TIME, response.responseTime());
        testData.put(SERVICE_STATUS, PASS);
        test.info(ACTUAL_RESPONSE_RECEIVED + ": " + response.responseBody())
                .info(ACTUAL_HEADERS_RECEIVED + ": " + response.responseHeaders())
                .info(ACTUAL_STATUS_CODE + ": " + response.statusCode())
                .info(ACTUAL_RES_TIME + ": " + response.responseTime());

        ExtentReport.testStatus(SERVICE_STATUS, PASS, test);
        log.info(validationStatusLog(SERVICE_STATUS, PASS));

    }

    private static RestRequest restDetails(String serviceType, String serviceName, String app, String env) {


        String filepath = ConfigReader.get("ServiceConfigFilePath", CONFIG);

        String query = "select " + url + "," + method + " from " + serviceType + " where " + SERVICE_NAME + " = '" + serviceName + "' and " + APP + " = '" + app + "' and " + ENV + " = '" + env + "'";

        Recordset rec = ExcelOperations.getFilloRecord(filepath, query);

        if (rec == null) {
            log.warn("No records found for this Rest service: {} , App: {} and Env: {} and query is {}: ", serviceName, app, env, query);
            return null;
        }

        try {
            String methodType = rec.getField(method);
            String restUrl = rec.getField(url);

            return new RestRequest(restUrl, methodType);

        } catch (Exception e) {
            log.warn("Exception triggered during restDetails", e);
        }
        return null;
    }


    protected static RestResponse triggerApiRequest(String methodType, String url, String requestBody, Map<String, Object> headersMap) {


        try {
            // Set up the request specification
            RequestSpecification requestSpec = RestAssured.given();

            // Add headers if provided
            if (headersMap != null && !headersMap.isEmpty()) {
                requestSpec.headers(headersMap);
                log.info("{} : {}",REQUEST_HEADERS , headersMap);
            }

            // Add body if provided
            if (requestBody != null && !requestBody.isBlank()) {
                log.info("{} : {}",REQUEST_PAYLOAD , requestBody);
                requestSpec.body(requestBody);
            }

            // Trigger the request based on the method type
            Response response = switch (methodType.trim().toUpperCase()) {
                case "GET" -> requestSpec.get(url);
                case "POST" -> requestSpec.post(url);
                case "PUT" -> requestSpec.put(url);
                case "DELETE" -> requestSpec.delete(url);
                case "PATCH" -> requestSpec.patch(url);
                default -> throw new IllegalArgumentException("Unsupported HTTP method: " + methodType);
            };

            Map<String, Object> resHeaders = new HashMap<>();

            for (Header header : response.getHeaders()) {
                resHeaders.putIfAbsent(header.getName(), header.getValue());
            }


            // Extract response details
            return new RestResponse(PASS, response.getStatusCode(), response.getBody().asString(), convertMaptoJsonString(resHeaders), response.getTime());


        } catch (Exception e) {
            log.warn("Rest service got exception", e);
        }


        return null;

    }


    public static RestResponse triggerApiRequestWithRetry(String methodType, String url, String requestBody, Map<String, Object> headersMap) {


        int retry = 3;

        RestResponse response = triggerApiRequest(methodType, url, requestBody, headersMap);


        for (int i = 1; i <= retry; i++) {

            if (response != null) {
                break;
            }
            response = triggerApiRequest(methodType, url, requestBody, headersMap);


            log.info("Retrying Rest Service again count: {}" , i);
        }

        return response;

    }


}
