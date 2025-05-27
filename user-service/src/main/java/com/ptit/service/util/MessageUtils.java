package com.ptit.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MessageUtils {

    private static MessageUtils instance;
    private final Properties properties = new Properties();

    public static String getMsg(String message) {
        try {
            String str = getInstance().properties.getProperty(message);
            return str != null ? new String(str.getBytes("ISO-8859-1"), "UTF-8") : "";
        } catch (Exception ignored) {
        }
        return "";
    }

    public static String getMsg(String message, Object... args) {
        try {
            String str = getInstance().properties.getProperty(message);
            return String.format(
                    str != null ? new String(str.getBytes("ISO-8859-1"), "UTF-8") : "", args);
        } catch (Exception ignored) {
        }
        return "";
    }

    private static MessageUtils getInstance(){
        if (instance == null) {
            MessageUtils newIntance = new MessageUtils();
            newIntance.loadPropertiesFile();
            instance = newIntance;
        }
        return instance;
    }

    private void loadPropertiesFile() {
        InputStream iStream = getClass().getClassLoader().getResourceAsStream("messages.properties");
        try {
            properties.load(iStream);
        } catch (IOException ignored) {
        } finally {
            try {
                if (iStream != null) {
                    iStream.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
