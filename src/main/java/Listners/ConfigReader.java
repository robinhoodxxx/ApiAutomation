package Listners;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import static Listners.CommonVariables.CONFIG;

public class ConfigReader {

    static CustomLogger log = CustomLogger.getInstance();

    private static final String LOCATION = "./src/main/resources/Config/";

    private ConfigReader(){}

    public static String get(String key, String filename) {
        Properties properties = new Properties();


        try (InputStream input = new FileInputStream(LOCATION + filename + ".properties")) {
            properties.load(input);
            String value = properties.getProperty(key);
            if (value == null) {
                log.warning(String.format("Key: %s not found in file: %s.properties", key, filename));
                return "";
            }
            return value;
        } catch (FileNotFoundException e) {
            log.warning("Properties file not found:" + filename,e);

        } catch (Exception e) {
            log.warning(String.format("file:%s got exception %s", filename, e));

        }

        return "";
    }




}

