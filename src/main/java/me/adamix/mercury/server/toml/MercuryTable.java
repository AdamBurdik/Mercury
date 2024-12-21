package me.adamix.mercury.server.toml;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.util.Objects;

public class MercuryTable extends MercuryToml {

	private final @NotNull TomlTable tomlTable;
	private final @NotNull String name;

	public MercuryTable(@NotNull TomlTable tomlTable, @NotNull String name) {
		this.tomlTable = tomlTable;
		this.name = name;
	}

	@Override
	public void mustContain(@NotNull String dottedKey) {
		if (!this.tomlTable.contains(dottedKey)) {
			throw new RuntimeException("Unable to find property '" + dottedKey + "' in " + name + "!");
		}
	}

	@Override
	public @Nullable String getString(@NotNull String dottedKey) {
		return tomlTable.getString(dottedKey);
	}

	public @NotNull String getStringSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		String value = getString(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Boolean getBoolean(@NotNull String dottedKey) {
		return tomlTable.getBoolean(dottedKey);
	}

	public boolean getBooleanSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Boolean value = tomlTable.getBoolean(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Integer getInteger(@NotNull String dottedKey) {
		Long value = tomlTable.getLong(dottedKey);
		if (value == null) {
			return null;
		}
		return value.intValue();
	}

	public int getIntegerSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Integer value = getInteger(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Long getLong(@NotNull String dottedKey) {
		return tomlTable.getLong(dottedKey);
	}

	public long getLongSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Long value = getLong(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Float getFloat(@NotNull String dottedKey) {
		Double value = tomlTable.getDouble(dottedKey);
		if (value == null) {
			return null;
		}
		return value.floatValue();
	}

	public float getFloatSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Float value = getFloat(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Double getDouble(@NotNull String dottedKey) {
		return tomlTable.getDouble(dottedKey);
	}

	public double getDoubleSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Double value = getDouble(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable MercuryTable getTable(@NotNull String dottedKey) {
		return new MercuryTable(getTomlTableSafe(dottedKey), this.name + "." + dottedKey);
	}

	public @NotNull MercuryTable getTableSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		MercuryTable table = getTable(dottedKey);
		Objects.requireNonNull(table);
		return table;
	}

	@Override
	public @Nullable MercuryArray getArray(@NotNull String dottedKey) {
		return new MercuryArray(getTomlArraySafe(dottedKey), this.name + dottedKey);
	}

	public @NotNull MercuryArray getArraySafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		MercuryArray array = getArray(dottedKey);
		Objects.requireNonNull(array);
		return array;
	}

	@Override
	public @Nullable TomlTable getTomlTable(@NotNull String dottedKey) {
		return tomlTable.getTable(dottedKey);
	}

	public @NotNull TomlTable getTomlTableSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		TomlTable value = getTomlTable(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable TomlArray getTomlArray(@NotNull String dottedKey) {
		return tomlTable.getArray(dottedKey);
	}

	public @NotNull TomlArray getTomlArraySafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		TomlArray value = getTomlArray(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Pos getPos(@NotNull String dottedKey) {
		return getPos(tomlTable.getArray(dottedKey));
	}

	@Override
	public @NotNull String getName() {
		return "";
	}
}
