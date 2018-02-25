package org.dorkmaster.docs.inject.util;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    private final static Config instance = new Config();
    private final static Logger logger = LoggerFactory.getLogger(Config.class);

    private Config() {
        super();
    }

    public static Config instance() {
        return instance;
    }

    private Map<String, Map<String, String>> settings = new ConcurrentHashMap<>();

    private boolean tryBundle(String group, String file) {
        try {
            Map<String, String> tmp = new HashMap<>();
            ResourceBundle bundle = ResourceBundle.getBundle("/" + file);
            for (String key : bundle.keySet()) {
                tmp.put(key, bundle.getString(key));
            }
            settings.put(group, tmp);
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    private boolean tryProperties(String group, String file) {
        try (InputStream in = new FileInputStream(file)) {
            Properties p = new Properties();
            p.load(in);

            Map<String, String> tmp = new HashMap<>();
            for (String key : p.stringPropertyNames()) {
                tmp.put(key, p.getProperty(key));
            }
            settings.put(group, tmp);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void load(String logicalName, String file) {
        if (!settings.containsKey(file)) {
            if (!tryBundle(logicalName, file)) {
                if (!tryProperties(logicalName, file)) {
                    logger.warn("Unable to load config file [{}]", file);
                }
            }
        }
    }

    public void load(String file) {
        load(file, file);
    }

    public Value value(String file, String name) {
        // -D override
        String value = System.getProperty(name);
        if (null != value) {
            return new Value(file, name, value);
        }

        load(file);
        return new Value(file, name, settings.get(file).get(name));
    }

    public static class Value {
        String file;
        String name;
        String value;

        public Value(String file, String name, String value) {
            this.file = file;
            this.name = name;
            this.value = value;
        }

        public boolean isNull() {
            return null == value;
        }

        public String getFile() {
            return file;
        }

        public String getName() {
            return name;
        }

        public String asString() {
            return asString(null);
        }

        public String asString(String def) {
            return isNull() ? def : value;
        }

        public Integer asInt() {
            return asInt(null);
        }

        public Integer asInt(Integer def) {
            return isNull() ? def : NumberUtils.toInt(value);
        }

        public Boolean asBool() {
            return asBool(false);
        }

        public Boolean asBool(Boolean def) {
            return isNull() ? def : BooleanUtils.toBoolean(value);
        }
    }
}
