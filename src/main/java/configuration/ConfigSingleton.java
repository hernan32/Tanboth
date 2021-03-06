package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigSingleton {
    private final String CONFIG_FILE_NAME = "config.properties";
    private final File EXTERNAL_CONFIG_FILE = new File(CONFIG_FILE_NAME);
    private final InputStream INTERNAL_CONFIG_FILE = getClass().getResourceAsStream("/config/" + CONFIG_FILE_NAME);
    private Properties CONFIG;
    private static ConfigSingleton INSTANCE;

    public enum Property {
        serverURL, user, password, serverNumber, resetTime
    }

    public enum Option {
        debugMode, autoSellItems, sellEpics, autoIncreaseStats
    }

    private ConfigSingleton() throws IOException {
        CONFIG = getConfiguration();
    }

    public static ConfigSingleton getInstance() throws IOException {
        if (INSTANCE == null) INSTANCE = new ConfigSingleton();
        return INSTANCE;
    }

    public String getProperty(Property prop) {
        return CONFIG.getProperty(prop.name());
    }

    public boolean getOption(Option opt) {
        return Boolean.parseBoolean(CONFIG.getProperty(opt.name()));
    }

    private Properties getConfiguration() throws IOException {
        Properties propFile = new Properties();
        if (EXTERNAL_CONFIG_FILE.exists()) propFile.load(new FileInputStream(EXTERNAL_CONFIG_FILE));
        else {
            propFile.load(INTERNAL_CONFIG_FILE);
        }
        return propFile;
    }

}