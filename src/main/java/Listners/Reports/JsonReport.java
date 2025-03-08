package Listners.Reports;

import Listners.ConfigReader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.Map;

import static Listners.CommonVariables.CONFIG;

public class JsonReport {

    public static void generateJsonReport(List<Map<String,Object>> testData){

        String filePath = ConfigReader.get("JsonReportFilepath",CONFIG);
        File file = new File(filePath);


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, testData);
            System.out.println("JSON file created successfully: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    }

