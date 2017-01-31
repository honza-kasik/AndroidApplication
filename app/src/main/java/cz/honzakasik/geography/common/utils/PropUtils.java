package cz.honzakasik.geography.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cz.honzakasik.geography.App;

public class PropUtils {

    private static final Properties properties = new Properties();

    static {
        String configFileName = "config.properties";
        try {
            InputStream inputStream = App.getContext().getAssets().open(configFileName);
            if (inputStream == null) {
                throw new NullPointerException("Could not load properties file!");
            }
            try {
                properties.load(inputStream);
                properties.putAll(System.getProperties());
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load ".concat(configFileName));
        }

    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}
