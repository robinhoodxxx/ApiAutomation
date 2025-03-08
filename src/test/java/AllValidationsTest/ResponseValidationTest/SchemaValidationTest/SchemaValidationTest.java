package AllValidationsTest.ResponseValidationTest.SchemaValidationTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import serviceUtils.JsonOperations;

import  AllValidations.Responsevalidation.SchemaValidation;
import static org.junit.jupiter.api.Assertions.*;

class SchemaValidationTest extends SchemaValidation{

    private static JsonNode jsonSchema;
    private static JsonNode jsonSchemaAddtionlFields;
    private final ObjectMapper mapper = new ObjectMapper();


    @BeforeAll
    static void schemaPrepare() {
        jsonSchema = JsonOperations.loadJsonFromJsonFile("src/test/java/AllValidationsTest/ResponseValidationTest/SchemaValidationTest/schema.json");
        jsonSchemaAddtionlFields = JsonOperations.loadJsonFromJsonFile("src/test/java/AllValidationsTest/ResponseValidationTest/SchemaValidationTest/addFalseSchema.json");

    }


    @Test
    @DisplayName("Valid JSON response matches schema")
    void testValidJson() throws Exception {
        // Valid JSON response with all required fields and correct types
        String jsonResponse = "{ \"company\": { \"name\": \"Tech Corp\", \"location\": { \"address\": { \"street\": \"123 Main St\", \"city\": \"New York\", \"state\": \"NY\", \"zip\": 10001 }, \"country\": \"USA\" } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertTrue(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("Missing required field: company")
    void testMissingCompanyField() throws Exception {
        // JSON response missing the top-level "company" field
        String jsonResponse = "{}";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("Missing required field: name in company")
    void testMissingNameField() throws Exception {
        // JSON response missing the "name" field in the "company" object
        String jsonResponse = "{ \"company\": { \"location\": { \"address\": { \"street\": \"123 Main St\", \"city\": \"New York\", \"state\": \"NY\", \"zip\": 10001 }, \"country\": \"USA\" } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("Missing required field: location in company")
    void testMissingLocationField() throws Exception {
        // JSON response missing the "location" field in the "company" object
        String jsonResponse = "{ \"company\": { \"name\": \"Tech Corp\" } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("Missing required field: address in location")
    void testMissingAddressField() throws Exception {
        // JSON response missing the "address" field in the "location" object
        String jsonResponse = "{ \"company\": { \"name\": \"Tech Corp\", \"location\": { \"country\": \"USA\" } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("Missing required field: country in location")
    void testMissingCountryField() throws Exception {
        // JSON response missing the "country" field in the "location" object
        String jsonResponse = "{ \"company\": { \"name\": \"Tech Corp\", \"location\": { \"address\": { \"street\": \"123 Main St\", \"city\": \"New York\", \"state\": \"NY\", \"zip\": 10001 } } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("Missing required field: street in address")
    void testMissingStreetField() throws Exception {
        // JSON response missing the "street" field in the "address" object
        String jsonResponse = "{ \"company\": { \"name\": \"Tech Corp\", \"location\": { \"address\": { \"city\": \"New York\", \"state\": \"NY\", \"zip\": 10001 }, \"country\": \"USA\" } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("Type mismatch: zip is a string instead of an integer")
    void testTypeMismatchZipField() throws Exception {
        // JSON response where the "zip" field is a string instead of an integer
        String jsonResponse = "{ \"company\": { \"name\": \"Tech Corp\", \"location\": { \"address\": { \"street\": \"123 Main St\", \"city\": \"New York\", \"state\": \"NY\", \"zip\": \"10001\" }, \"country\": \"USA\" } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("Type mismatch: name is an integer instead of a string")
    void testTypeMismatchNameField() throws Exception {
        // JSON response where the "name" field is an integer instead of a string
        String jsonResponse = "{ \"company\": { \"name\": 12345, \"location\": { \"address\": { \"street\": \"123 Main St\", \"city\": \"New York\", \"state\": \"NY\", \"zip\": 10001 }, \"country\": \"USA\" } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("JSON response with extra field should pass if additionalProperties is not restricted")
    void testExtraFieldInResponseAllowed() throws Exception {
        // Schema without additionalProperties restriction

        // JSON response with an extra field
        String jsonResponse = "{ \"company\": { \"name\": \"john\" , \"location\": { \"address\": { \"street\": \"123 Main St\", \"city\": \"New York\", \"state\": \"NY\", \"zip\": 10001 }, \"country\": \"USA\" , \"extra field\": \"extra field not mentioned in schema\" } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertTrue(validateJsonAgainstSchema(res, jsonSchema));
    }

    @Test
    @DisplayName("JSON response with extra field should fail if additionalProperties is false")
    void testExtraFieldInResponse() throws Exception {
        // Schema with additionalProperties: false

        // JSON response with an extra field
        String jsonResponse = "{ \"company\": { \"name\": \"john\" , \"location\": { \"address\": { \"street\": \"123 Main St\", \"city\": \"New York\", \"state\": \"NY\", \"zip\": 10001 }, \"country\": \"USA\" , \"extra field\": \"extra field not mentioned in schema\" } } }";
        JsonNode res = mapper.readTree(jsonResponse);

        assertFalse(validateJsonAgainstSchema(res, jsonSchemaAddtionlFields));
    }

}
