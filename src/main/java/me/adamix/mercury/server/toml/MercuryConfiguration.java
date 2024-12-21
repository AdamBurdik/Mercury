package me.adamix.mercury.server.toml;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MercuryConfiguration extends MercuryToml {

	private final @NotNull TomlParseResult parseResult;
	private final @NotNull String fileName;

	public MercuryConfiguration(@NotNull File tomlFile) {
		try {
			this.fileName = tomlFile.getName();
			this.parseResult = Toml.parse(tomlFile.toPath());
			if (this.parseResult.hasErrors()) {
				this.parseResult.errors().forEach(error -> {
					throw new RuntimeException("Error while parsing " + tomlFile + "!\n " + error.toString());
				});
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void mustContain(@NotNull String dottedKey) {
		if (!this.parseResult.contains(dottedKey)) {
			throw new RuntimeException("Unable to find property '" + dottedKey + "' in " + fileName + "!");
		}
	}

	@Override
	public @Nullable String getString(@NotNull String dottedKey) {
		return parseResult.getString(dottedKey);
	}

	public @NotNull String getStringSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		String value = getString(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Boolean getBoolean(@NotNull String dottedKey) {
		return parseResult.getBoolean(dottedKey);
	}

	public boolean getBooleanSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Boolean value = parseResult.getBoolean(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Integer getInteger(@NotNull String dottedKey) {
		Long value = parseResult.getLong(dottedKey);
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
		return parseResult.getLong(dottedKey);
	}

	public long getLongSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Long value = getLong(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Float getFloat(@NotNull String dottedKey) {
		Double value = parseResult.getDouble(dottedKey);
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
		return parseResult.getDouble(dottedKey);
	}

	public double getDoubleSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Double value = getDouble(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable MercuryTable getTable(@NotNull String dottedKey) {
		TomlTable value = getTomlTable(dottedKey);
		if (value == null) {
			return null;
		}
		return new MercuryTable(value, this.fileName + "." + dottedKey);
	}

	@Override
	public @Nullable MercuryArray getArray(@NotNull String dottedKey) {
		TomlArray value = getTomlArray(dottedKey);
		if (value == null) {
			return null;
		}
		return new MercuryArray(value, this.fileName + "." + dottedKey);
	}

	@Override
	public @Nullable TomlTable getTomlTable(@NotNull String dottedKey) {
		return parseResult.getTable(dottedKey);
	}

	public @NotNull TomlTable getTomlTableSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		TomlTable value = getTomlTable(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable TomlArray getTomlArray(@NotNull String dottedKey) {
		return parseResult.getArray(dottedKey);
	}

	public @NotNull TomlArray getTomlArraySafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		TomlArray value = getTomlArray(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	@Override
	public @Nullable Pos getPos(@NotNull String dottedKey) {
		return getPos(parseResult.getArray(dottedKey));
	}

	@Override
	public @NotNull String getName() {
		return this.fileName;
	}
}
