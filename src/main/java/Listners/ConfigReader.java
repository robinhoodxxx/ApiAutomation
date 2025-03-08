package Listners;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    static CustomLogger log = CustomLogger.getInstance();

    private static final String LOCATION = "./src/main/resources/Config/";

    private ConfigReader(){}

    public static String get(String key, String filename) {
        Properties properties = new Properties();


        try (InputStream input = new FileInputStream(LOCATION + filename + ".properties")) {
            properties.load(input);
            return properties.getProperty(key);

        } catch (FileNotFoundException e) {
            log.warning("Properties file not found:" + filename,e);

        } catch (NullPointerException ex) {
            log.warning(String.format("key:%s not found in the file:%s.properties", key, filename));

        } catch (Exception e) {
            log.warning(String.format("file:%s got exception %s", filename, e));

        }

        return "";
    }


}

