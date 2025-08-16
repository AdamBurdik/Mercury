package me.adamix.mercury.core.configuration;

import me.adamix.mercury.core.configuration.exception.MissingPropertyException;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Represents a configuration section or structured data node in the Mercury configuration system.<br>
 *
 * <p>This interface provides access to nested configuration values using dotted key notation.
 * It acts like a table or map for structured data, supporting both flat and nested entries.</p>
 *
 */
public interface MercuryTable {

	/**
	 * Gets the name of the configuration source.
	 *
	 * @return The name of the configuration source.
	 */
	@NotNull String name();

	/**
	 * Checks if the configuration contains a given property.
	 *
	 * @param dottedKey The dotted key representing the property to check.
	 * @return True if the property exists, false otherwise.
	 */
	boolean contains(@NotNull String dottedKey);

	/**
	 * Gets an object by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The object corresponding to the key, or null if not found.
	 */
	@Nullable Object getObject(@NotNull String dottedKey);

	/**
	 * Gets a string value by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The string value corresponding to the key, or null if not found.
	 */
	@Nullable String getString(@NotNull String dottedKey);

	/**
	 * Gets an integer value by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The integer value corresponding to the key, or null if not found.
	 */
	@Nullable Integer getInteger(@NotNull String dottedKey);

	/**
	 * Gets a long value by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The long value corresponding to the key, or null if not found.
	 */
	@Nullable Long getLong(@NotNull String dottedKey);

	/**
	 * Gets a float value by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The float value corresponding to the key, or null if not found.
	 */
	@Nullable Float getFloat(@NotNull String dottedKey);

	/**
	 * Gets a double value by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The double value corresponding to the key, or null if not found.
	 */
	@Nullable Double getDouble(@NotNull String dottedKey);

	/**
	 * Gets a boolean value by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The boolean value corresponding to the key, or null if not found.
	 */
	@Nullable Boolean getBoolean(@NotNull String dottedKey);

	/**
	 * Gets a table value by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The MercuryTable object corresponding to the key, or null if not found.
	 */
	@Nullable MercuryTable getTable(@NotNull String dottedKey);

	/**
	 * Gets an array value by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The MercuryArrayOld object corresponding to the key, or null if not found.
	 */
	@Nullable MercuryArray getArray(@NotNull String dottedKey);

	Set<Map.Entry<String, Object>> dottedEntrySet(boolean includeTables);

	Set<String> keySet();
	Set<String> dottedKeySet();

	default <T> @Nullable T get(@NotNull String dottedKey) {
		//noinspection unchecked
		return (T) getObject(dottedKey);
	}

	/**
	 * Gets a value by the given dotted key, and cast it to type.
	 * @param dottedKey The dotted key representing the property
	 * @param defaultValue The default value that will be returned if property does not exist.
	 * @return The generic object.
	 * @param <T> Type to cast value to.
	 */
	default <T> @NotNull T getOrDefault(@NotNull String dottedKey, @NotNull T defaultValue) {
		T value = get(dottedKey);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	default  <T> @Nullable T get(@NotNull String dottedKey, @NotNull Class<T> type) {
		Object object = getObject(dottedKey);
		return switch (object) {
			case null -> null;
			case Long l when type == Integer.class -> type.cast(l.intValue());
			case Double d when type == Float.class -> type.cast(d.floatValue());
			default -> type.cast(object);
		};

	}

	default  <T> @NotNull T getSafe(@NotNull String dottedKey) throws MissingPropertyException {
		T value = get(dottedKey);
		if (value == null) {
			throw new MissingPropertyException(dottedKey, name());
		}
		return value;
	}

	default  <T> @NotNull T getSafe(@NotNull String dottedKey, @NotNull Class<T> type) throws MissingPropertyException {
		T value = get(dottedKey, type);
		if (value == null) {
			throw new MissingPropertyException(dottedKey, name());
		}
		return value;
	}

	/**
	 * Gets a Key object by the given dotted key.
	 *
	 * @param dottedKey The dotted key representing the property.
	 * @return The Key object corresponding to the key, or null if not found.
	 */
	default @Nullable Key getKey(@NotNull String dottedKey) {
		String value = getString(dottedKey);
		return value != null ? Key.key(value) : null;
	}
}
