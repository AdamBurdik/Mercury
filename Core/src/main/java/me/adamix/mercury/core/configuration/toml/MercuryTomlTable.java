package me.adamix.mercury.core.configuration.toml;

import me.adamix.mercury.core.configuration.MercuryArray;
import me.adamix.mercury.core.configuration.MercuryTable;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.util.Map;
import java.util.Set;

public class MercuryTomlTable implements MercuryTable {
	private final @NotNull TomlTable tomlTable;
	private final @NotNull String name;

	public MercuryTomlTable(@NotNull TomlTable tomlTable, @NotNull String name) {
		this.tomlTable = tomlTable;
		this.name = name;
	}

	@Override
	public @NotNull String name() {
		return name;
	}

	@Override
	public boolean contains(@NotNull String dottedKey) {
		return tomlTable.contains(dottedKey);
	}

	@Override
	public @Nullable Object getObject(@NotNull String dottedKey) {
		return tomlTable.get(dottedKey);
	}

	@Override
	public @Nullable String getString(@NotNull String dottedKey) {
		return tomlTable.getString(dottedKey);
	}

	@Override
	public @Nullable Integer getInteger(@NotNull String dottedKey) {
		Long longValue = getLong(dottedKey);
		return longValue == null ? null : longValue.intValue();
	}

	@Override
	public @Nullable Long getLong(@NotNull String dottedKey) {
		return tomlTable.getLong(dottedKey);
	}

	@Override
	public @Nullable Float getFloat(@NotNull String dottedKey) {
		Double doubleValue = getDouble(dottedKey);
		return doubleValue == null ? null : doubleValue.floatValue();
	}

	@Override
	public @Nullable Double getDouble(@NotNull String dottedKey) {
		return tomlTable.getDouble(dottedKey);
	}

	@Override
	public @Nullable Boolean getBoolean(@NotNull String dottedKey) {
		return tomlTable.getBoolean(dottedKey);
	}

	@Override
	public @Nullable MercuryTable getTable(@NotNull String dottedKey) {
		TomlTable table = tomlTable.getTable(dottedKey);
		return table == null ? null : new MercuryTomlTable(table, name + "." + dottedKey);
	}

	@Override
	public @Nullable MercuryArray getArray(@NotNull String dottedKey) {
		TomlArray array = tomlTable.getArray(dottedKey);
		return array == null ? null : new MercuryTomlArray(array, name + "." + dottedKey);
	}

	@Override
	public Set<Map.Entry<String, Object>> dottedEntrySet(boolean includeTables) {
		return tomlTable.dottedEntrySet();
	}

	@Override
	public Set<String> keySet() {
		return tomlTable.keySet();
	}

	@Override
	public Set<String> dottedKeySet() {
		return tomlTable.dottedKeySet();
	}
}
