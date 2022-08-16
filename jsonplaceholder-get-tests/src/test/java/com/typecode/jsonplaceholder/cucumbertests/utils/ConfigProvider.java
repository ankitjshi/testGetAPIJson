package com.typecode.jsonplaceholder.cucumbertests.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * It is used to set environment variables and fetch the variables from the path
 * endpoint-property/environment.properties
 */
public class ConfigProvider {
    InputStream iStream;
    public static String BASE_URL;
    public static String ENDPOINT_SERVICE;

    private final org.apache.logging.log4j.Logger log = Logger.log;

    private static final String configFilePath = "endpoint-property/environment.properties";

    public ConfigProvider(String environment) {
        try {
            Properties props = this.getPropValues(configFilePath);
            ConfigProvider.BASE_URL = props.getProperty("BASE_URL."+environment);
        } catch (IOException exc) {
            log.info("Could not read configuration file");
        }
    }

    public Properties getPropValues(String configFilePath) throws IOException {
        Properties prop = new Properties();
        try {
            iStream = getClass().getClassLoader().getResourceAsStream(configFilePath);
            if (iStream != null) {
                prop.load(iStream);
            } else {
                throw new FileNotFoundException("property file '" + configFilePath + "' not found in the classpath");
            }
        } catch (Exception e) {
            log.error("Exception: " + e);
        } finally {
            iStream.close();
        }
        return prop;
    }
}
