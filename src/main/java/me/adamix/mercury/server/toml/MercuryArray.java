package me.adamix.mercury.server.toml;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.util.Objects;

public class MercuryArray {

	private final @NotNull TomlArray tomlArray;
	private final @NotNull String name;

	public MercuryArray(@NotNull TomlArray tomlArray, @NotNull String name) {
		this.tomlArray = tomlArray;
		this.name = name;
	}

	public void requireCorrectIndex(int index) {
		if (index < 0) {
			throw new RuntimeException("Index cannot be less than 0!");
		}
		if (index >= tomlArray.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	public int size() {
		return tomlArray.size();
	}

	public @Nullable String getString(int index) {
		return tomlArray.getString(index);
	}

	public @NotNull String getStringSafe(int index) {
		requireCorrectIndex(index);
		String value = getString(index);
		Objects.requireNonNull(value);
		return value;
	}

	public @Nullable Boolean getBoolean(int index) {
		return tomlArray.getBoolean(index);
	}

	public boolean getBooleanSafe(int index) {
		requireCorrectIndex(index);
		Boolean value = getBoolean(index);
		Objects.requireNonNull(value);
		return value;
	}

	public @Nullable Integer getInteger(int index) {
		long value = tomlArray.getLong(index);
		return (int) value;
	}

	public int getIntegerSafe(int index) {
		requireCorrectIndex(index);
		Integer value = getInteger(index);
		Objects.requireNonNull(value);
		return value;
	}

	public @Nullable Long getLong(int index) {
		return tomlArray.getLong(index);
	}

	public long getLongSafe(int index) {
		requireCorrectIndex(index);
		Long value = getLong(index);
		Objects.requireNonNull(value);
		return value;
	}

	public @Nullable MercuryArray getArray(int index) {
		TomlArray array = tomlArray.getArray(index);
		return array != null ? new MercuryArray(array, this.name + "[" + index + "]") : null;
	}

	public @Nullable MercuryTable getTable(int index) {
		TomlTable table = tomlArray.getTable(index);
		return table != null ? new MercuryTable(table, this.name + "[" + index + "]") : null;
	}

	public @Nullable TomlArray getTomlArray(int index) {
		return tomlArray.getArray(index);
	}

	public @Nullable TomlArray getTomlArraySafe(int index) {
		requireCorrectIndex(index);
		TomlArray value = getTomlArray(index);
		Objects.requireNonNull(value);
		return value;
	}


	public @Nullable TomlTable getTomlTable(int index) {
		return tomlArray.getTable(index);
	}

	public @NotNull TomlTable getTomlTableSafe(int index) {
		requireCorrectIndex(index);
		TomlTable value = getTomlTable(index);
		Objects.requireNonNull(value);
		return value;
	}

	public @Nullable NamespaceID getNamespaceID(int index) {
		return NamespaceID.from(tomlArray.getString(index));
	}

	public @NotNull NamespaceID getNamespaceIDSafe(int index) {
		requireCorrectIndex(index);
		NamespaceID value = getNamespaceID(index);
		Objects.requireNonNull(value);
		return value;
	}

	public @Nullable String getMaterial(int index) {
		return tomlArray.getString(index);
	}

	public @NotNull String getMaterialSafe(int index) {
		requireCorrectIndex(index);
		String value = getMaterial(index);
		Objects.requireNonNull(value);
		return value;
	}

	public @Nullable String getEntityType(int index) {
		return tomlArray.getString(index);
	}

	public @NotNull String getEntityTypeSafe(int index) {
		requireCorrectIndex(index);
		String value = getEntityType(index);
		Objects.requireNonNull(value);
		return value;
	}

	public String[] toStringArray() {
		String[] result = new String[tomlArray.size()];
		for (int i = 0; i < tomlArray.size(); i++) {
			result[i] = getStringSafe(i);
		}
		return result;
	}

	public boolean[] toBooleanArray() {
		boolean[] result = new boolean[tomlArray.size()];
		for (int i = 0; i < tomlArray.size(); i++) {
			result[i] = getBooleanSafe(i);
		}
		return result;
	}

	public int[] toIntegerArray() {
		int[] result = new int[tomlArray.size()];
		for (int i = 0; i < tomlArray.size(); i++) {
			result[i] = getIntegerSafe(i);
		}
		return result;
	}

	public long[] toLongArray() {
		long[] result = new long[tomlArray.size()];
		for (int i = 0; i < tomlArray.size(); i++) {
			result[i] = getLongSafe(i);
		}
		return result;
	}

	public MercuryArray[] toArrayArray() {
		MercuryArray[] result = new MercuryArray[tomlArray.size()];
		for (int i = 0; i < tomlArray.size(); i++) {
			result[i] = getArray(i);
		}
		return result;
	}

	public MercuryTable[] toTableArray() {
		MercuryTable[] result = new MercuryTable[tomlArray.size()];
		for (int i = 0; i < tomlArray.size(); i++) {
			result[i] = getTable(i);
		}
		return result;
	}

	public NamespaceID[] toNamespacedIDArray() {
		NamespaceID[] result = new NamespaceID[tomlArray.size()];
		for (int i = 0; i < tomlArray.size(); i++) {
			result[i] = getNamespaceID(i);
		}
		return result;
	}
}
