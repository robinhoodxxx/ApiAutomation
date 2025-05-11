package serviceUtils;

import Listners.ConfigReader;
import Listners.RunStopException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Listners.CommonVariables.CONFIG;


public class JsonOperations {


    private static final Logger log = LoggerFactory.getLogger(JsonOperations.class);


    private JsonOperations() {
    }

    public static List<String> getListFromJsonFile(String jsonFilePath, String path) {

        try {
            String json = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            return JsonPath.from(json).getList(path);


        } catch (Exception e) {
            throw new RunStopException(e);
        }


    }

    public static JsonNode convertStringToJson(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();


        // Read the file content as a raw string

        try {
            if (jsonString == null || jsonString.isBlank()) return objectMapper.createObjectNode();

            // Try to parse the raw JSON string (this may fail if there are placeholders like (id))
            return objectMapper.readTree(jsonString);

            // If successful, pretty-print the JSON


        } catch (JsonParseException e) {
            log.warn("Incorrect Json format: " + jsonString, e);
        } catch (Exception e) {
            // If parsing fails, return the raw string
            log.error("convertStringToJson failed ", e);

        }


        return null;
    }

    public static Map<String, Object> convertJsonToMap(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) return new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, new TypeReference<>() {
            });
        } catch (JsonParseException e) {
            log.warn("convertJsonToMap is has not in json format :{} {}", jsonString, e.getMessage());
        } catch (Exception e) {
            log.warn("convertJsonToMap is failed :{} {}", jsonString, e.getMessage());

        }

        return new HashMap<>();
    }

    public static JsonNode convertMapToJson(Map<String, Object> map) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (map == null || map.isEmpty()) return objectMapper.createObjectNode();

            return objectMapper.valueToTree(map);
        } catch (Exception e) {
            log.error("Map to json conversion failed", e);
        }

        return objectMapper.createObjectNode();
    }

    public static String convertMaptoJsonString(Map<String, Object> map) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (map == null || map.isEmpty()) return "";

            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Map to json conversion failed", e);
        }

        return null;
    }

    // Method to convert JsonNode to String and handle exceptions
    public static String convertJsonNodeToString(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isEmpty()) return "";

        try {

            // Convert JsonNode to String using ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(jsonNode);

        } catch (Exception e) {

            log.error("json to string conversion failed ", e);

        }
        return null;
    }

    // Method to load JsonNode from a file for demonstration purposes
    public static JsonNode loadJsonFromJsonFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                log.error("File not found: {}", filePath, new FileNotFoundException());
            }

            return objectMapper.readTree(file);

        } catch (JsonParseException e) {

            log.warn("json exception", e);


        } catch (Exception e) {
            log.error("LoadingJsonFile Got:  Exception :", e);
        }
        return null;
    }


    public static Map<String, Object> dummymap() {
        HashMap<String, Object> jsonMap = new HashMap<>();

        // Manually adding unique keys and values extracted from the JSON
        jsonMap.put("name", "Tech Innovators Inc.");
        jsonMap.put("street", "1234 Innovation Drive");
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


        return jsonMap;
    }

    public static String jsonFileToString(String filePath) {
        try {
            // Read the entire content of the file as a string
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            // Handle file reading errors
            log.error(e.getMessage());
            return "Error reading file.";
        }
    }


    /* Returns a JSON string based on the input.
     * If the input is a valid file path, it reads the file and returns its content as a JSON string.
     * If the input is a valid JSON string, it returns the same string.
     *
     * @param input JSON string or file path
     * @return JSON string
     * @throws IllegalArgumentException if the input is neither a valid JSON string nor a valid file path
     * @throws IOException if there is an error reading the file
     */

    public static String getRequestJsonString(String input, String app) {
        String path = String.format(ConfigReader.get("RequestTemplatePath", CONFIG), app);
        return getJsonString(input, path);
    }

    public static String getResponseJsonString(String input, String app) {
        String path = String.format(ConfigReader.get("ResponseTemplatePath", CONFIG), app);
        return getJsonString(input, path);
    }

    public static String getschemaJsonString(String input, String app) {
        String path = String.format(ConfigReader.get("ResponseSchemaPath", CONFIG), app);
        return getJsonString(input, path);
    }

    private static String getJsonString(String input, String pathType) {
        try {

            if (input == null || input.isBlank()) {
                return input;
            }


            // Check if the input is a valid JSON string
            if (isJsonString(input)) {
                return input;
            }

            // Check if the input is a valid file path
            if (isFilePath(input)) {
                return jsonFileToString(pathType + input);
            }

            log.warn("Template File Not found :{}", input);
            // If neither, throw an exception

        } catch (Exception e) {
            log.warn("Exception triggered for getJsonString", e);
        }
        return null;
    }

    /**
     * Checks if the input is a valid file path.
     *
     * @param input the input string
     * @return true if the input is a valid file path, false otherwise
     */
    private static boolean isFilePath(String input) {
        return Files.exists(Paths.get(input));
    }


    /**
     * Checks if the input is a valid JSON string.
     * This is a simple check and does not validate the full JSON structure.
     *
     * @param input the input string
     * @return true if the input starts with '{' or '[', false otherwise
     */
    private static boolean isJsonString(String input) {
        String trimmedInput = input.trim();
        return trimmedInput.startsWith("{") || trimmedInput.startsWith("[");
    }


}
