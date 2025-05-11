package Listners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);

    private static final String LOCATION = "./Config/";

    private ConfigReader(){}

    public static String get(String key, String filename) {
        Properties properties = new Properties();


        try (InputStream input = new FileInputStream(LOCATION + filename + ".properties")) {
            properties.load(input);
            String value = properties.getProperty(key);
            if (value == null) {
                log.warn("Key: {} not found in file: {}.properties", key, filename);
                return "";
            }
            return value;
        } catch (FileNotFoundException e) {
            log.warn("Properties file not found: {}" , filename,e);

        } catch (Exception e) {
            log.warn("file:%s got exception {}", filename,e);

        }

        return "";
    }




}

