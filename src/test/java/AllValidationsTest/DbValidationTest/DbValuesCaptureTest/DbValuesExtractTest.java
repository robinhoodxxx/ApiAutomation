package AllValidationsTest.DbValidationTest.DbValuesCaptureTest;

import AllValidations.DbValidations.DbValuesExtract;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import serviceUtils.JsonOperations;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


 class DbValuesExtractTest extends DbValuesExtract {



    @Test
    @DisplayName("Pos: Verify DbValues get Captured correctly for all fieldNames")
    void dbCaptureEndToEnd(){

        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "Tech Innovators Inc.");
        jsonMap.put("id", "");
        jsonMap.put("desc", null);
        jsonMap.put("date", 123.123);
        jsonMap.put("value at", "text");
        jsonMap.put("result", false);
        jsonMap.put("value", "");
        jsonMap.put("integer", false);
        jsonMap.put("boolean", "hell");

        JsonNode expJson = JsonOperations.loadJsonFromJsonFile("./src/test/java/AllValidationsTest/DbValidationTest/DbValuesCaptureTest/dbcaptureTemplate.json");
        JsonNode actualJson = JsonOperations.loadJsonFromJsonFile("./src/test/java/AllValidationsTest/DbValidationTest/DbValuesCaptureTest/dbCaptureDbResult.json");

        assertNotNull(expJson);
        assertNotNull(actualJson);
        assertTrue(dbExtract("query",expJson,actualJson,jsonMap));

        JsonPath jsonPathObj = JsonPath.from(actualJson.toString());


        expJson.fieldNames().forEachRemaining(field->{
            String col = expJson.get(field).asText().replaceAll("[()]","").trim();
                assertEquals(jsonMap.get(col),jsonPathObj.get(field));
        });

    }

}
