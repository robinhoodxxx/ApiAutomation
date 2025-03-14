package serviceUtils;

import Listners.CommonVariables;
import Listners.CustomLogger;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateOps {

    private static final CustomLogger log = CustomLogger.getInstance();


    public static String processTemplate(String template, Map<String, Object> dataMap) {
        final String NULL_REPLACE_REGEX = RandomStringUtils.randomAlphanumeric(5);

        return commonProcessTemplate(template, dataMap, NULL_REPLACE_REGEX).replaceAll(String.format("\"%s\"", NULL_REPLACE_REGEX), "null").replaceAll(NULL_REPLACE_REGEX, "null");
    }

    private static String commonProcessTemplate(String template, Map<String, Object> dataMap, String nullRegex) {


        if (template == null || template.isBlank()) {
            log.warning("Template is received as null or blank");
            return "";
        }

        if (!template.contains("(")) {
            log.info("Template no need of processing");
            return template;
        }

        if (!areBracketsMatched(template)) {
            log.warning("Check your template has some ( are not closed or mismatched:" + template);
        }

        // Regular expression to find placeholders in the format (columnName)
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(template);

        // Replace each placeholder with the corresponding value from the map
        StringBuilder processedURL = new StringBuilder();
        while (matcher.find()) {
            String columnName = matcher.group(1).trim(); // Extract the text inside parentheses

            if (!dataMap.containsKey(columnName)) {
                log.warning(MessageFormat.format("column not found in Data Sheet : {0}. Replacing with default mapping as column name-> \"{0}\"", columnName));
            }
            Object replacementValue = dataMap.getOrDefault(columnName, matcher.group(0));
            Object val = Optional.ofNullable(replacementValue).orElse(nullRegex);
            String replacementString = escapeRegex(String.valueOf(val));

            matcher.appendReplacement(processedURL, replacementString);
        }
        matcher.appendTail(processedURL);

        return processedURL.toString();
    }

    private static String escapeRegex(String str) {
        return str.replaceAll("([\\\\.^$|?*+()\\[\\]{}])", "\\\\$1");
    }


    public static boolean areBracketsMatched(String input) {
        int balance = 0; // Counter for unmatched opening brackets

        for (char ch : input.toCharArray()) {
            if (ch == '(') {
                // If the previous character was an open bracket, return false
                if (balance > 0) {
                    return false; // Immediate close after open
                }
                balance++; // Increment for an opening bracket
            } else if (ch == ')') {
                balance--; // Decrement for a closing bracket
                if (balance < 0) {
                    return false; // More closing than opening brackets
                }
            }
        }

        return balance == 0; // True if all brackets are matched
    }


    public static Map<String, String> exceptedDb(String template, Map<String, Object> dataMap) {
        final String NULL_REPLACE_REGEX = RandomStringUtils.randomAlphanumeric(5);

        Map<String, String> expectedDbMap = new HashMap<>();
        String processTemplate = commonProcessTemplate(template, dataMap, NULL_REPLACE_REGEX);

        String[] columnValues = processTemplate.split("\\|");
        for (String columnValue : columnValues) {
            String[] arr = columnValue.split("=");
            String column = arr[0].trim();
            String value = "";

            try {
                value = arr[1];
            } catch (ArrayIndexOutOfBoundsException ignored) {
                log.info(String.format("Column is :%s is empty, So it validates as null in db check", column));
            }

            String actualValue = value.trim().equals(NULL_REPLACE_REGEX) || value.trim().equals(CommonVariables.NULL_REPLACE_REGEX) ? null : value;
            expectedDbMap.put(column, actualValue);
        }


        return expectedDbMap;
    }


}





