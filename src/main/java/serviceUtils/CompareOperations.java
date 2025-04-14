package serviceUtils;



import com.fasterxml.jackson.databind.JsonNode;


import java.util.*;

import static Listners.CommonVariables.DB_VALIDATION;
import static Listners.CommonVariables.JSON_VALIDATION;

public class CompareOperations {



    private CompareOperations() {
    }


    public static boolean isJsonsEqual(JsonNode expJson, JsonNode actualJson, StringBuilder comments, Set<String> ignoreAttributes, String validationType) {

        ignoreAttributes = Optional.ofNullable(ignoreAttributes).orElse(new HashSet<>());


        if (expJson == null || expJson.isEmpty()) {
            comments.append("ExpectedResponse is received as null or empty: ").append(expJson);
            return false;
        }

        // Handle null nodes
        if (actualJson == null || actualJson.isEmpty()) {
            comments.append("ActualResponse is received as null or empty: ").append(actualJson);
            return false;
        }
        return compareJsonNodes(expJson, actualJson, comments, "", ignoreAttributes, validationType);
    }

    private static boolean compareJsonNodes(JsonNode expJson, JsonNode actualJson, StringBuilder mismatches, String path, Set<String> ignoreAttributes, String validationType) {
        // Guard clause: Skip paths in the ignore list
        if (ignoreAttributes.contains(path)) return true;


        // Fast equality check
        if (expJson.hashCode() == actualJson.hashCode()) return true;

        // Check for type mismatch
        if (!expJson.getNodeType().equals(actualJson.getNodeType())) {
            appendMismatch(mismatches, path, "ValueType mismatch", expJson.getNodeType(), actualJson.getNodeType(), validationType);
            return false;
        }

        // Delegate to specific comparison methods
        if (expJson.isValueNode() && actualJson.isValueNode()) {
            return compareValueNodes(expJson, actualJson, mismatches, path, validationType);
        }
        if (expJson.isArray() && actualJson.isArray()) {
            return compareArrayNodes(expJson, actualJson, mismatches, path, ignoreAttributes, validationType);
        }
        if (expJson.isObject() && actualJson.isObject()) {
            return compareObjectNodes(expJson, actualJson, mismatches, path, ignoreAttributes, validationType);
        }

        // Unhandled cases
        mismatches.append(path).append(": Unhandled node type or mismatch.\n");
        return false;
    }

    private static boolean compareValueNodes(JsonNode expJson, JsonNode actualJson, StringBuilder mismatches, String path, String validationType) {
        if (!expJson.asText().equals(actualJson.asText())) {
            appendMismatch(mismatches, path, "Value mismatch", expJson.asText(), actualJson.asText(), validationType);
            return false;
        }
        return true;
    }

    private static boolean compareArrayNodes(JsonNode expJson, JsonNode actualJson, StringBuilder mismatches, String path, Set<String> ignoreAttributes, String validationType) {
        if (expJson.hashCode() == actualJson.hashCode()) return true;
        if (expJson.size() != actualJson.size()) {
            appendMismatch(mismatches, path, "Array size mismatch", expJson.size(), actualJson.size(), validationType);
            return false;
        }

        boolean allMatch = true;
        for (int i = 0; i < expJson.size(); i++) {
            if (!compareJsonNodes(expJson.get(i), actualJson.get(i), mismatches, path + "[" + i + "]", ignoreAttributes, validationType)) {
                allMatch = false;
            }
        }
        return allMatch;
    }

    private static boolean compareObjectNodes(JsonNode expJson, JsonNode actualJson, StringBuilder mismatches, String path, Set<String> ignoreAttributes, String validationType) {
        if (expJson.hashCode() == actualJson.hashCode()) return true;

        boolean isEqual = true;

        // Compare fields in expJson
        Iterator<Map.Entry<String, JsonNode>> fields1 = expJson.fields();
        while (fields1.hasNext()) {
            Map.Entry<String, JsonNode> entry1 = fields1.next();
            String fieldName = entry1.getKey();
            String newPath = path.isEmpty() ? fieldName : path + "." + fieldName;

            // Skip ignored fields
            if (ignoreAttributes.contains(newPath)) continue;

            if (!actualJson.has(fieldName)) {
                mismatches.append(newPath).append(": Field is missing in actual JSON ->").append(fieldName).append("\n");
                isEqual = false;
            } else if (!compareJsonNodes(entry1.getValue(), actualJson.get(fieldName), mismatches, newPath, ignoreAttributes, validationType)) {
                isEqual = false;
            }
        }


        // Check for extra fields in actualJson
        isEqual = isExtraFieldInObjectNode(expJson, actualJson, mismatches, path, validationType, isEqual);


        return isEqual;
    }

    private static boolean isExtraFieldInObjectNode(JsonNode expJson, JsonNode actualJson, StringBuilder mismatches, String path, String validationType, boolean isEqual) {

        if (validationType.equals(DB_VALIDATION)) return isEqual;

        Iterator<String> fields2 = actualJson.fieldNames();
        while (fields2.hasNext()) {
            String fieldName = fields2.next();
            String newPath = path.isEmpty() ? fieldName : path + "." + fieldName;
            if (!expJson.has(fieldName)) {
                mismatches.append(newPath).append(": Extra field found in actual JSON.\n");
                isEqual = false;
            }
        }

        return isEqual;
    }

    private static void appendMismatch(StringBuilder mismatches, String path, String message, Object expected, Object actual, String validationType) {

        if (validationType.equals(JSON_VALIDATION)) {
            path = "Attribute -> " + path;
        } else if (validationType.equals(DB_VALIDATION)) {
            path = "Column -> " + path;
        }

        mismatches.append(path).append(" : ").append(message).append("\n")
                .append("  Expected :").append(expected).append("; \t\t\t")
                .append("  Actual :").append(actual).append("; \n");
    }
}








