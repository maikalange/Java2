/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.legalmind.utils;

/**
 *
 * @author JoeNyirenda
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

    private final Properties properties;

    public ConfigHelper(String propertiesFilePath) {
        properties = new Properties();
        loadProperties(propertiesFilePath);
    }

    private void loadProperties(String propertiesFilePath) {
        
        try (InputStream input = ClassLoader.getSystemResourceAsStream(propertiesFilePath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
