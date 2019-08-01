package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationSingleton {
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final Properties CONFIG = getConfiguration();
    private static ConfigurationSingleton INSTANCE;

    public enum Property {
        serverURL,
        user,
        password,
        serverNumber,
        resetTime,
        debugMode
    }

    private ConfigurationSingleton() {
    }

    public static ConfigurationSingleton getInstance() {
        if (INSTANCE == null) INSTANCE = new ConfigurationSingleton();
        return INSTANCE;
    }

    public static String getProperty(Property prop) {
        return CONFIG.getProperty(prop.name());
    }

    private static Properties getConfiguration() {
        final File EXTERNAL_CONFIG_FILE = new File(CONFIG_FILE_NAME);
        Properties propFile = new Properties();
        if (EXTERNAL_CONFIG_FILE.exists()) {
            try {
                propFile.load(new FileInputStream(EXTERNAL_CONFIG_FILE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            InputStream inputStream = ConfigurationSingleton.class.getResourceAsStream("/config/" + CONFIG_FILE_NAME);
            try {
                propFile.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return propFile;
    }
}
