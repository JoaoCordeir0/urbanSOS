package br.com.urbansos.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String FILE_NAME = "config.properties";
    private static Properties properties;

    public static String getAwsAccessKeyId() {
        return getProperty("aws.accessKeyId");
    }

    public static String getAwsSecretKey() {
        return getProperty("aws.secretKey");
    }

    private static String getProperty(String key) {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(key);
    }

    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (input == null) {
                System.out.println("Unable to find " + FILE_NAME);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
