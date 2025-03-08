package serviceUtils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.*;

import static Listners.CommonVariables.NULL_REPLACE_REGEX;


public class Helper {


    private Helper(){}

    public static boolean isRunEnable(Object val){
     return  String.valueOf(val).equalsIgnoreCase("yes")||String.valueOf(val).equalsIgnoreCase("y");
    }


    private static JsonNode transformJson(JsonNode inputNode) {
        // Create a new ObjectNode for the transformed output
        ObjectNode outputNode = new ObjectMapper().createObjectNode();

        inputNode.fieldNames().forEachRemaining(fieldName -> {
            JsonNode valueNode = inputNode.get(fieldName);

            if (valueNode.isObject()) {
                // If the value is an object, call transformJson recursively
                outputNode.set(fieldName, transformJson(valueNode));
            } else if (valueNode.isArray()) {
                // If the value is an array, handle each element
                for (JsonNode arrayElement : valueNode) {
                    outputNode.putArray(fieldName).add(transformJson(arrayElement));
                }
            } else if (valueNode.isTextual()) {
                // If the value is a string, replace it with "(key)"
                outputNode.put(fieldName, "(" + fieldName + ")");
            } else if (valueNode.isInt()) {
                // If the value is an integer, replace it with (key)
                outputNode.put(fieldName, "(fieldName)");
            } else {
                // For other types, you can choose how to handle them
                outputNode.set(fieldName, valueNode); // Copy the original value
            }
        });

        return outputNode;
    }

    public static String jsonToStringTemplate(JsonNode inputNode) {

        return transformJson(inputNode).toPrettyString();
    }


    public static void printMapsAsTable(List<Map<String, Object>> listOfMaps) {
        // Step 1: Collect all unique column headers
        Set<String> uniqueKeys = new LinkedHashSet<>();
        for (Map<String, Object> map : listOfMaps) {
            uniqueKeys.addAll(map.keySet());
        }
        List<String> columnHeaders = new ArrayList<>(uniqueKeys);

        // Step 2: Calculate the maximum width for each column
        Map<String, Integer> columnWidths = new HashMap<>();
        for (String header : columnHeaders) {
            int maxWidth = header.length();
            for (Map<String, Object> map : listOfMaps) {
                String value = map.get(header).toString();
                if (value == null) {
                    value= NULL_REPLACE_REGEX;
                    System.out.println(value);

                }

                maxWidth = Math.max(maxWidth, value.length());
            }
            columnWidths.put(header, maxWidth);
        }

        // Step 3: Create format string for table rows
        StringBuilder formatBuilder = new StringBuilder();
        for (String header : columnHeaders) {
            formatBuilder.append("| %-").append(columnWidths.get(header)).append("s ");
        }
        formatBuilder.append("|\n");
        String rowFormat = formatBuilder.toString();

        // Step 4: Print the table header
        printSeparator(columnWidths, columnHeaders);
        System.out.printf(rowFormat, columnHeaders.toArray());
        printSeparator(columnWidths, columnHeaders);

        // Step 5: Print each row of data
        for (Map<String, Object> map : listOfMaps) {
            List<Object> rowData = new ArrayList<>();
            for (String header : columnHeaders) {
                rowData.add(map.getOrDefault(header, ""));
            }
            System.out.printf(rowFormat, rowData.toArray());
        }
        printSeparator(columnWidths, columnHeaders);
    }



    private static void printSeparator(Map<String, Integer> columnWidths, List<String> columnHeaders) {
        for (String header : columnHeaders) {
            System.out.print("+");
            System.out.print("-".repeat(columnWidths.get(header) + 2));
        }
        System.out.println("+");
    }




    private static boolean matchesIgnorePath(String path, String ignorePath) {
        // Split paths into segments by "."
        String[] pathSegments = path.split("\\.");
        String[] ignoreSegments = ignorePath.split("\\.");

        int i = 0;
        int j = 0;

        while (i < pathSegments.length && j < ignoreSegments.length) {
            String pathSegment = pathSegments[i];
            String ignoreSegment = ignoreSegments[j];

            if (ignoreSegment.equals("[*]")) {
                // Match wildcard against any array index
                if (!pathSegment.matches("\\[\\d+]")) {
                    return false; // If it's not an array index, wildcard doesn't apply
                }
                j++; // Move to next segment in ignore path
            } else if (!pathSegment.equals(ignoreSegment)) {
                return false; // Mismatch between static segments
            } else {
                j++; // Move to next segment in ignore path
            }
            i++; // Move to next segment in actual path
        }

        // Check if both paths were fully traversed
        return i == pathSegments.length && j == ignoreSegments.length;
    }





}
