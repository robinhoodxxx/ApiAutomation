package AllValidationsTest.FieldComparisonsTest;


import AllValidations.FieldComparisons.FieldComparisons;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import serviceUtils.JsonOperations;
import static Listners.CommonVariables.*;

import static org.junit.jupiter.api.Assertions.*;

class FieldComparisonTest extends FieldComparisons {


    @Test
    @DisplayName("TC_01: Verify overallStatus as false when payload received as null empty or not an array format")
     void invalidRequestPayload(){
        ObjectMapper obj= new ObjectMapper();

        assertEquals(FAIL, fieldValidation(null).overallStatus());
        assertEquals(FAIL, fieldValidation(obj.createObjectNode()).overallStatus());
    }


    @Test
    @DisplayName("TC_02:Verify invalid validation symbol received check")
     void invalidSymbolCheck(){

        JsonNode js = JsonOperations.loadJsonFromJsonFile("./src/test/java/AllValidationsTest/FieldComparisonsTest/TC_02InvalidSymbol.json");
        assertNotNull(js);
        assertEquals(FAIL, fieldValidation(js).overallStatus());

    }

    @Test
    @DisplayName("TC_03:Verify the all valid values with symbols gives correct response")
    void verifyAllChecks(){

        JsonNode js = JsonOperations.loadJsonFromJsonFile("./src/test/java/AllValidationsTest/FieldComparisonsTest/TC_03AllChecks.json");
        assertNotNull(js);
        assertEquals(PASS, fieldValidation(js).overallStatus());

    }


}
