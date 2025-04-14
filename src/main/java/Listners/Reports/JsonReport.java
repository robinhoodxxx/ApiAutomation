package Listners.Reports;

import Listners.ConfigReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static Listners.CommonVariables.CONFIG;

public class JsonReport {

    private static final Logger log = LoggerFactory.getLogger(JsonReport.class);

    public static void generateJsonReport(List<Map<String,Object>> testData){

        String filePath = ConfigReader.get("JsonReportFilepath",CONFIG);
        File file = new File(filePath);


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, testData);
            log.info("JSON file created successfully: {}" , filePath);
        } catch (Exception e) {
            log.error("Json Report creation got failed: {}", e.getMessage());
        }
    }

    }

