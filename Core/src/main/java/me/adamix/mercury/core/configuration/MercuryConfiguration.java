package me.adamix.mercury.core.configuration;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Interface defines methods for accessing configuration data.
 * This interface can be implemented by configuration formats (e.g., TOML, JSON, YAML).
 */
public interface MercuryConfiguration extends MercuryTable {

	/**
	 * Gets the name of the configuration source.
	 *
	 * @return The name of the configuration source.
	 */
	@NotNull String name();

	/**
	 * Gets the path of the configuration source.
	 * @return The path to the configuration source.
	 */
	@NotNull Path filePath();
}
