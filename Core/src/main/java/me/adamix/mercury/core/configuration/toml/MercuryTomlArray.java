package me.adamix.mercury.core.configuration.toml;

import me.adamix.mercury.core.configuration.MercuryArray;
import me.adamix.mercury.core.configuration.MercuryTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

public class MercuryTomlArray implements MercuryArray {
	private final @NotNull TomlArray tomlArray;
	private final @NotNull String name;

	public MercuryTomlArray(@NotNull TomlArray tomlArray, @NotNull String name) {
		this.tomlArray = tomlArray;
		this.name = name;
	}

	@Override
	public @NotNull String name() {
		return name;
	}

	@Override
	public int size() {
		return tomlArray.size();
	}

	@Override
	public @Nullable Object getObject(int index) {
		return tomlArray.get(index);
	}

	@Override
	public @Nullable String getString(int index) {
		return tomlArray.getString(index);
	}

	@Override
	public @Nullable Integer getInteger(int index) {
		Long longValue = getLong(index);
		return longValue == null ? null : longValue.intValue();
	}

	@Override
	public @Nullable Long getLong(int index) {
		return tomlArray.getLong(index);
	}

	@Override
	public @Nullable Float getFloat(int index) {
		Double doubleValue = getDouble(index);
		return doubleValue == null ? null : doubleValue.floatValue();
	}

	@Override
	public @Nullable Double getDouble(int index) {
		return tomlArray.getDouble(index);
	}

	@Override
	public @Nullable Boolean getBoolean(int index) {
		return tomlArray.getBoolean(index);
	}

	@Override
	public @Nullable MercuryArray getArray(int index) {
		TomlArray array = tomlArray.getArray(index);
		return array == null ? null : new MercuryTomlArray(array, name + "[" + index + "]");
	}

	@Override
	public @Nullable MercuryTable getTable(int index) {
		TomlTable table = tomlArray.getTable(index);
		return table == null ? null : new MercuryTomlTable(table, name + "[" + index + "]");
	}
}
