package me.adamix.mercury.core.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * The MercuryArray interface defines methods for accessing array data in a configuration.
 * This interface is used to interact with an array of items within a configuration.
 */
public interface MercuryArray {
	/**
	 * Gets the name of the configuration source.
	 *
	 * @return The name of the configuration source.
	 */
	@NotNull String name();

	/**
	 * Gets the size of the array.
	 *
	 * @return The size of the array.
	 */
	int size();

	/**
	 * Gets an object at the given index.
	 *
	 * @param index The index of the object in the array.
	 * @return The object at the specified index, or null if not found.
	 */
	@Nullable Object getObject(int index);

	/**
	 * Gets a string value at the given index.
	 *
	 * @param index The index of the string value in the array.
	 * @return The string value at the specified index, or null if not found.
	 */
	@Nullable String getString(int index);

	/**
	 * Gets an integer value at the given index.
	 *
	 * @param index The index of the integer value in the array.
	 * @return The integer value at the specified index, or null if not found.
	 */
	@Nullable Integer getInteger(int index);

	/**
	 * Gets a long value at the given index.
	 *
	 * @param index The index of the long value in the array.
	 * @return The long value at the specified index, or null if not found.
	 */
	@Nullable Long getLong(int index);

	/**
	 * Gets a float value at the given index.
	 *
	 * @param index The index of the float value in the array.
	 * @return The float value at the specified index, or null if not found.
	 */
	@Nullable Float getFloat(int index);

	/**
	 * Gets a double value at the given index.
	 *
	 * @param index The index of the double value in the array.
	 * @return The double value at the specified index, or null if not found.
	 */
	@Nullable Double getDouble(int index);

	/**
	 * Gets a boolean value at the given index.
	 *
	 * @param index The index of the boolean value in the array.
	 * @return The boolean value at the specified index, or null if not found.
	 */
	@Nullable Boolean getBoolean(int index);

	/**
	 * Gets a MercuryArray object at the given index.
	 *
	 * @param index The index of the MercuryArray object in the array.
	 * @return The MercuryArray object at the specified index, or null if not found.
	 */
	@Nullable MercuryArray getArray(int index);

	/**
	 * Gets a MercuryTable object at the given index.
	 *
	 * @param index The index of the MercuryTable object in the array.
	 * @return The MercuryTable object at the specified index, or null if not found.
	 */
	@Nullable MercuryTable getTable(int index);
	
	/**
	 * Converts the values into a {@code String[]} array.
	 *
	 * @return an array of strings, one for each index
	 */
	default @Nullable String[] toStringArray() {
		String[] array = new String[size()];
		for (int i = 0; i < size(); i++) {
			array[i] = getString(i);
		}
		return array;
	}

	/**
	 * Converts the values into an {@code int[]} array.
	 *
	 * @return an array of ints, one for each index
	 */
	default @Nullable Integer[] toIntArray() {
		Integer[] array = new Integer[size()];
		for (int i = 0; i < size(); i++) {
			array[i] = getInteger(i);
		}
		return array;
	}

	/**
	 * Converts the values into a {@code long[]} array.
	 *
	 * @return an array of longs, one for each index
	 */
	default @Nullable Long[] toLongArray() {
		Long[] array = new Long[size()];
		for (int i = 0; i < size(); i++) {
			array[i] = getLong(i);
		}
		return array;
	}

	/**
	 * Converts the values into a {@code double[]} array.
	 *
	 * @return an array of doubles, one for each index
	 */
	default @Nullable Double[] toDoubleArray() {
		Double[] array = new Double[size()];
		for (int i = 0; i < size(); i++) {
			array[i] = getDouble(i);
		}
		return array;
	}

	/**
	 * Converts the values into a {@code boolean[]} array.
	 *
	 * @return an array of booleans, one for each index
	 */
	default @Nullable Boolean[] toBooleanArray() {
		Boolean[] array = new Boolean[size()];
		for (int i = 0; i < size(); i++) {
			array[i] = getBoolean(i);
		}
		return array;
	}

	/**
	 * Converts the values into a {@code float[]} array.
	 *
	 * @return an array of floats, one for each index
	 */
	default @Nullable Float[] toFloatArray() {
		Float[] array = new Float[size()];
		for (int i = 0; i < size(); i++) {
			array[i] = getFloat(i);
		}
		return array;
	}

	/**
	 * Converts the values into a {@code MercuryTable[]} array.
	 * @return
	 */
	default @Nullable MercuryTable[] toTableArray() {
		MercuryTable[] array = new MercuryTable[size()];
		for (int i = 0; i < size(); i++) {
			array[i] = getTable(i);
		}
		return array;
	}
}
