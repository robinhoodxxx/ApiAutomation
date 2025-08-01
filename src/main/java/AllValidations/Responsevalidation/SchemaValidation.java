package AllValidations.Responsevalidation;

import AllValidations.ValidationResponses;
import Listners.ConfigReader;
import Listners.Reports.ExtentReport;
import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serviceUtils.JsonOperations;

import java.util.ArrayList;
import java.util.Map;

import static AllValidations.AllValidations.*;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;


public class SchemaValidation {

    private static final Logger log = LoggerFactory.getLogger(SchemaValidation.class);

    public static void schemaValidation(Map<String, Object> testData, Map<String, ValidationResponses> allValidations) {
        String actRes = (String) testData.get(ACTUAL_RESPONSE_RECEIVED);
        String schema = (String) testData.get(SCHEMA);
        String app = String.valueOf(testData.get(APP));



        schema = JsonOperations.getschemaJsonString(schema,app);

        if (!actRes.equals(PASS)) {
            log.info(String.format(ConfigReader.get("skipValidation", CONFIG), SCHEMA_VALID, SKIP, actRes));
            allValidations.put(SCHEMA_VALID, emptyValidationResponses(SKIP));
            return;
        }

        if (schema == null || schema.isBlank()) {
            log.info(validationStatusLog(SCHEMA_VALID, NOT_ENABLE));
            allValidations.put(SCHEMA_VALID, emptyValidationResponses(NOT_ENABLE));
            return;
        }
        ExtentTest test = ((ExtentTest) testData.get(EXTENT)).createNode(SCHEMA_VALID);


        JsonNode res = JsonOperations.convertStringToJson(actRes);
        JsonNode jsSchema = JsonOperations.convertStringToJson(schema);

        ValidationResponses response = new ValidationResponses(status(validateJsonAgainstSchema(res, jsSchema)), new ArrayList<>());

        log.info(validationStatusLog(SCHEMA_VALID, response.overallStatus()));
        allValidations.put(SCHEMA_VALID, response);
        ExtentReport.testStatus(SCHEMA_VALID,response.overallStatus(),test);


    }

    public static boolean validateJsonAgainstSchema(JsonNode res, JsonNode schemaJson) {

        if (schemaJson == null) {
            log.warn("SchemaJson is received as null");
            return false;
        }

        try {
            // Create a JSON Schema factory
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            // Load the schema
            JsonSchema schema = factory.getJsonSchema(schemaJson);

            // Validate the JSON response against the schema
            ProcessingReport report = schema.validate(res);

            // Check if the validation was successful
            if (report.isSuccess()) {
                return true;
            }
                // Print detailed error messages for mismatches
                for (ProcessingMessage message : report) {
                    log.info("Validation error: {}" , message.getMessage());
                    log.info("Path: {}" , message.getLogLevel());
                    log.info("Details: {}" , message.asJson());
                }
                return false;

        } catch (ProcessingException e) {
            log.warn("Error processing schema: {}" , e.getMessage());
            return false;
        }
    }
}
