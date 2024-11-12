package me.adamix.mercury.configuration;

import me.adamix.mercury.common.SerializableEntity;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Configuration class to handle toml files
 */
public class Configuration implements SerializableEntity {
	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
	private TomlParseResult result;

	public Configuration(File tomlFile) {
		try {
			result = Toml.parse(tomlFile.toPath());
			if (result.hasErrors()) {
				result.errors().forEach(error -> LOGGER.error("Error while parsing {}! {}", tomlFile.getName(), error.toString()));
			}

		} catch (IOException e) {
			LOGGER.error(e.toString());
		}
	}

	public @Nullable String getString(String key) {
		return result.getString(key);
	}

	public int getInt(String key) {
		Double value = result.getDouble(key);
		if (value == null) {
			return 0;
		}
		return value.intValue();
	}

	public boolean getBoolean(String key) {
		Boolean value = result.getBoolean(key);
		if (value == null) {
			return false;
		}
		return value;
	}

	@Override
	public Map<String, Object> serialize() {
		return result.toMap();
	}
}
