package com.jui;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The ConfigLoader class is designed to manage the loading of configuration
 * properties from files in Java. 
 * It follows the Singleton design pattern,
 * ensuring that only one instance of the class exists throughout the
 * application. This class provides functionality to load properties from either
 * the classpath or a specified file path, allowing for flexible configuration
 * management.
 * 
 * @author maurizio.farina@gmail.com
 * 
 *
 * 
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) // Private constructor to prevent external instantiation
public class ConfigLoader {

	@Getter
	private static final ConfigLoader instance = new ConfigLoader(); // Singleton instance

	private Properties properties = new Properties();

	/**
	 * Loads the properties file from the classpath.
	 * 
	 * @param filename The name of the properties file in the classpath.
	 * @throws IOException If an error occurs during file loading.
	 */
	public void loadFromClasspath(String filename) throws IOException {
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)) {
			if (input == null) {
				throw new IOException("Properties file '" + filename + "' not found in classpath");
			}
			properties.load(input);
		}
	}

	/**
	 * Loads the properties file from a specified file path.
	 * 
	 * @param filePath The full path of the properties file.
	 * @throws IOException If an error occurs during file loading.
	 */
	public void loadFromFile(String filePath) throws IOException {
		try (InputStream input = new FileInputStream(filePath)) {
			properties.load(input);
		}
	}

	/**
	 * Returns the value of a property given the key name.
	 * 
	 * @param key The key name.
	 * @return The value associated with the key, or null if the key does not exist.
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Returns the value of a property given the key name, with a default value if
	 * the key does not exist.
	 * 
	 * @param key          The key name.
	 * @param defaultValue The default value if the key does not exist.
	 * @return The value associated with the key, or the default value if the key
	 *         does not exist.
	 */
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * Returns all the loaded properties.
	 * 
	 * @return The loaded properties.
	 */
	public Properties getAllProperties() {
		return properties;
	}
}