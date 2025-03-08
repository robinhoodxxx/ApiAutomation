package UtilsTests.TemplateOpsTest;

import java.util.HashMap;
import java.util.Map;


import serviceUtils.JsonOperations;
import serviceUtils.TemplateOps;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.*;

import java.text.MessageFormat;


import static org.junit.jupiter.api.Assertions.*;

 class TemplateOpsTest extends TemplateOps{

    private static Map<String, Object> jsonMap;

    @BeforeAll
    static void DataSetup() {
        jsonMap = new HashMap<>();
        jsonMap.put("name", "Tech Innovators Inc.");
        jsonMap.put("street", "1234 Innovation Drive ");
        jsonMap.put("city", "Tech City");
        jsonMap.put("state", "CA");
        jsonMap.put("zip", "90210");
        jsonMap.put("country", "USA");
        jsonMap.put("id", "101");
        jsonMap.put("position", "Social Media Specialist");
        jsonMap.put("skills", "Social Media");
        jsonMap.put("title", "Market Research");
        jsonMap.put("status", "Completed");
        jsonMap.put("startDate", "2023-02-01");
        jsonMap.put("endDate", "2023-04-15");
        jsonMap.put("established", "2010-05-10");
        jsonMap.put("employeesCount", 50);
        jsonMap.put("revenue", "89,88,893");
        jsonMap.put("profit", "10%");
        jsonMap.put("regex", "^n${9-10}hj");
        jsonMap.put("null", null);
        jsonMap.put("job", "null");
        jsonMap.put("empty","");
        jsonMap.put("blank"," ");
    }



    @Test
    @DisplayName("null, null value,blank and empty values check for url Template")
    void nullBlankEmptyCheckForTemplate() {

        String url = "https://api.developer.com/(id)/details/(null)/(job)/(blank)/(empty)val";
        String actualVal = TemplateOps.processTemplate(url, jsonMap);
        assertEquals("https://api.developer.com/101/details/null/null/ /val", actualVal);

    }





    @Test
    @DisplayName("Object value check for url Template")
    void ObjectValueCheckForTemplate() {

        String url = "https://api.developer.com/(state)/details/(zip)/where country=(country )?user=( skills )&empPercentage<=( employeesCount)&(startDate)";
        String actualVal = TemplateOps.processTemplate(url, jsonMap);
        assertEquals
                (MessageFormat.format
                        ("https://api.developer.com/{0}/details/{1}/where country={2}?user={3}&empPercentage<={4}&{5}",
                                jsonMap.get("state"), jsonMap.get("zip"), jsonMap.get("country"), jsonMap.get("skills"), jsonMap.get("employeesCount"), jsonMap.get("startDate")
                        ), actualVal);
    }


    @Test
    @DisplayName("Verify the json template converting as expected are replaced with correct values in map")
    void ValidateJsonCheckForTemplate() {
        final String filepath = "./src/test/java/UtilsTests/TemplateOpsTest/JsonTemplate.json";
        String json = JsonOperations.jsonFileToString(filepath);
        String actualVal = TemplateOps.processTemplate(json, jsonMap);
        JsonNode actualJson = JsonOperations.convertStringToJson(actualVal);

        final String resultFilepath = "./src/test/java/UtilsTests/TemplateOpsTest/OriginalJson.json";

        JsonNode expJson = JsonOperations.loadJsonFromJsonFile(resultFilepath);


        assertNotNull(expJson);
        assertNotNull(actualJson);

        boolean isValid = expJson.hashCode()==actualJson.hashCode();


        assertTrue(isValid);

    }

    @Test
    @DisplayName("Verify the DB template converting as expected are replaced with correct values in map")
    void ValidateDBCheckForTemplate(){
        final String template = " street=(street)|regex=$null|city=( city)|name =Automation|count=0|null=(null)|job=(job) |empty=(empty)|blank=(blank)|date=(endDate) 12:12:00 PM|no=(no)";
       Map<String,String> expMap= TemplateOps.exceptedDb(template,jsonMap);

        assertEquals(jsonMap.get("street"),expMap.get("street"));
        assertEquals(jsonMap.get("city"),expMap.get("city"));
        assertEquals("Automation",expMap.get("name"));
        assertNull(expMap.get("null"));
        assertNull(expMap.get("regex"));
        assertEquals(jsonMap.get("null"),expMap.get("null"));
        assertNotNull(expMap.get("job"));
        assertEquals(jsonMap.get("job")+" ",expMap.get("job"));
        assertEquals(jsonMap.get("empty"),expMap.get("empty"));
        assertEquals(jsonMap.get("blank"),expMap.get("blank"));
        assertEquals(jsonMap.get("endDate")+" 12:12:00 PM",expMap.get("date"));
        assertEquals("(no)",expMap.get("no"));

    }
    
    
    

    @Test
    @DisplayName("Verify Are all open brackets have immediate closed brackets ()")
    void AreBracketsMatched() {

        String input1 = "This is a test (example) string with (multiple) brackets."; // true
        String input2 = "string (string) (string string string)"; // true
        String input3 = "string(string(string)string)"; // false
        String input4 = "This is an (unmatched string with no closing bracket"; // false
        String input5 = "Unmatched closing bracket) at the beginning";//false
        String input6 = "string (string( string string)"; //false
        String input7 = "string (string)( string) string)"; //false
        String input8 = "string (string)( string) string("; //false
        String input9 = "(string(string) (string)string) )"; // false


        assertTrue(TemplateOps.areBracketsMatched(input1));
        assertTrue(TemplateOps.areBracketsMatched(input2));
        assertFalse(TemplateOps.areBracketsMatched(input3));
        assertFalse(TemplateOps.areBracketsMatched(input4));
        assertFalse(TemplateOps.areBracketsMatched(input5));
        assertFalse(TemplateOps.areBracketsMatched(input6));
        assertFalse(TemplateOps.areBracketsMatched(input7));
        assertFalse(TemplateOps.areBracketsMatched(input8));
        assertFalse(TemplateOps.areBracketsMatched(input9));

    }
    
    


}
