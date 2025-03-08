package Listners.Reports;

import AllValidations.ValidationResponses;
import Listners.ConfigReader;
import serviceUtils.ExcelOperations;

import java.util.*;
import java.util.stream.Collectors;

import static Listners.CommonVariables.CONFIG;
import static Listners.DataSheet.*;


public class ExcelReport {


    private static final Set<String> excelReportColumns =  new LinkedHashSet<>(Arrays.asList(
            TESTCASE_STATUS,
            TC_ID,
            TESTCASE,
            SERVICE_TYPE,
            SERVICE_NAME,
            APP,
            ENV,
            REQUEST_HEADERS,
            REQUEST_PAYLOAD,
            EXP_RESPONSE_PAYLOAD,
            ACTUAL_RESPONSE_RECEIVED,
            ACTUAL_HEADERS_RECEIVED,
            ACTUAL_STATUS_CODE,
            SERVICE_STATUS
    ));

    public static void generateExcelReport(List<Map<String, Object>> testData) {

        String path = ConfigReader.get("ExcelReportPath", CONFIG);


        List<Map<String, Object>> processedData = testData.stream()
                .map(data -> {
                    // Step 1: Extract statuses and merge them
                    Map<String, Object> extractMap = new LinkedHashMap<>(extractOverallStatus(data));
                    Map<String, Object> validationMap = new LinkedHashMap<>(validationOverallStatus(data));

                    // Step 2: Filter required fields
                    Map<String, Object> filteredData = excelReportColumns.stream()
                            .filter(data::containsKey) // Ensure key exists in original data
                            .collect(Collectors.toMap(
                                    key -> key,          // Maintain key order as per excelReportColumns
                                    data::get,           // Get value from the original map
                                    (existing, replacement) -> existing, // Merge function (not needed here)
                                    LinkedHashMap::new   // Preserve order
                            ));
                    // Step 3: Merge filtered data with extracted statuses
                    filteredData.putAll(extractMap);
                    filteredData.putAll(validationMap);

                    return filteredData;
                })
                .toList();

        // Generate the Excel report with the new processed data
        ExcelOperations.createExcelReport(processedData, path);


    }


    private static Map<String, Object> validationOverallStatus(Map<String, Object> testData) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, ValidationResponses> validationsMap = (Map<String, ValidationResponses>) testData.get(VALIDATIONS);


        for (Map.Entry<String, ValidationResponses> entry : validationsMap.entrySet()) {
            String key = entry.getKey();
            ValidationResponses validationResponse = entry.getValue();

            if (validationResponse != null) {
                result.put(key, validationResponse.overallStatus()); // Extract overallStatus
            }
        }

        return result;
    }

    private static Map<String, Object> extractOverallStatus(Map<String, Object> testData) {

        return (Map<String, Object>) testData.get(EXTRACTIONS);


    }

}
