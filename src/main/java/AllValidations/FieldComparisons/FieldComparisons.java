package AllValidations.FieldComparisons;

import AllValidations.ValidationResponses;
import Listners.CustomLogger;
import com.fasterxml.jackson.databind.JsonNode;
import serviceUtils.JsonOperations;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static AllValidations.AllValidations.emptyValidationResponses;
import static AllValidations.AllValidations.status;
import static Listners.CommonVariables.FAIL;
import static Listners.CommonVariables.NOT_ENABLE;
import static Listners.DataSheet.DB_VALID;
import static Listners.DataSheet.FIELD_VALID;


public class FieldComparisons {

    private static final CustomLogger log = CustomLogger.getInstance();


    public static void overallFieldValidations(Map<String,Object>testData,Map<String,ValidationResponses> allValidations){

        String fieldVals = (String) testData.get(FIELD_VALID);

        if(fieldVals==null||fieldVals.isBlank()){
            log.info(FIELD_VALID+" is "+NOT_ENABLE);
            allValidations.put(FIELD_VALID,new ValidationResponses(NOT_ENABLE,new ArrayList<>()));
            return;
        }

        JsonNode js = JsonOperations.convertStringToJson(fieldVals);


        ValidationResponses res = fieldValidation(js);
        allValidations.put(FIELD_VALID,res);
        log.info(FIELD_VALID+" is "+res.overallStatus());

    }

    public static ValidationResponses fieldValidation(JsonNode js) {

        if(js==null|| js.isEmpty()) return emptyValidationResponses(FAIL);

        List<Object> fieldValidations = new ArrayList<>();
        final boolean[] overallStatus = {true};

        if (!js.isArray()) {
            log.warning(MessageFormat.format("{0} is {1} , due given {0} is not in Array format as expected",DB_VALID,FAIL));
            return new ValidationResponses(FAIL, fieldValidations);
        }


        js.forEach(node -> {
            StringBuilder comments = new StringBuilder();
            boolean status = validator(node);
            overallStatus[0] = status && overallStatus[0];
            fieldValidations.add(new FieldComResponse(new FieldComRequest(node), status, comments));
        });


        return new ValidationResponses(status(overallStatus[0]), fieldValidations);
    }


    private static  boolean comparator(JsonNode expected, JsonNode actual, JsonNode validator) {
      String message = "incorrect symbol";

        if (expected == null || actual == null || validator == null) {
            log.info("fields are null");
            return false;
        }

        String symbol = validator.asText().trim();

        if (expected.getClass() != actual.getClass()) {
            log.info("fields instance type not match");
            return false;
        }

        switch (expected.getNodeType()) {
            case NUMBER -> {
                double expectedNum = expected.doubleValue();
                double actualNum = actual.doubleValue();

                return switch (symbol) {
                    case "=" -> expectedNum == actualNum;
                    case ">" -> expectedNum > actualNum;
                    case ">=" -> expectedNum >= actualNum;
                    case "<" -> expectedNum < actualNum;
                    case "<=" -> expectedNum <= actualNum;
                    case "!=" -> expectedNum != actualNum;
                    default -> {
                        log.info(message);
                        yield false;
                    }
                };
            }
            case STRING -> {
                String expectedStr = expected.asText();
                String actualStr = actual.asText();
                return switch (symbol) {
                    case "=" -> expectedStr.equals(actualStr);
                    case "!=" -> !expectedStr.equals(actualStr);
                    case "==" -> expectedStr.equalsIgnoreCase(actualStr);
                    case "~" -> expectedStr.contains(actualStr);
                    case "^" -> expectedStr.startsWith(actualStr);
                    case "%" -> expectedStr.endsWith(actualStr);
                    case "$" -> expectedStr.matches(actualStr);

                    default -> {
                        log.info(message);
                        yield false;
                    }
                };
            }
            case BOOLEAN -> {
                boolean expectedBool = expected.asBoolean();
                boolean actualBool = actual.asBoolean();

                return switch (symbol) {
                    case "=" -> expectedBool == actualBool;
                    case "!=" -> expectedBool != actualBool;
                    default -> {
                        log.info(message);
                        yield false;
                    }
                };
            }
            default -> {
                return false;
            }
        }
    }

    private static  boolean validator(JsonNode node) {


        JsonNode expected = node.get("expected");
        JsonNode actual = node.get("actual");
        JsonNode symbol = node.get("validator");


        return comparator(expected, actual, symbol);

    }

}

