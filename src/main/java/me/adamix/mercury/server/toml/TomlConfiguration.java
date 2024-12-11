package me.adamix.mercury.server.toml;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class TomlConfiguration {
	private final TomlParseResult parseResult;
	private final String fileName;

	public TomlConfiguration(@NotNull File tomlFile) {
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

	public void mustContain(@NotNull String dottedKey) {
		if (!this.parseResult.contains(dottedKey)) {
			throw new RuntimeException("Unable to find property " + dottedKey + " in " + fileName + "!");
		}
	}

	public boolean getBooleanSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Boolean value = this.parseResult.getBoolean(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	public boolean getBoolean(@NotNull String dottedKey) {
		return Boolean.TRUE.equals(this.parseResult.getBoolean(dottedKey));
	}

	public @Nullable String getString(@NotNull String dottedKey) {
		return this.parseResult.getString(dottedKey);
	}

	public @NotNull String getStringSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		String value = this.parseResult.getString(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	public int getIntegerSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Long value = this.parseResult.getLong(dottedKey);
		Objects.requireNonNull(value);
		return value.intValue();
	}

	public int getInteger(@NotNull String dottedKey) {
		Long value = this.parseResult.getLong(dottedKey);
		if (value == null) {
			return 0;
		}
		return value.intValue();
	}

	public float getFloatSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Double value = this.parseResult.getDouble(dottedKey);
		Objects.requireNonNull(value);
		return value.floatValue();
	}

	public float getFloat(@NotNull String dottedKey) {
		Double value = this.parseResult.getDouble(dottedKey);
		if (value == null) {
			return 0f;
		}
		return value.floatValue();
	}


	public @Nullable NamespaceID getNamespacedID(@NotNull String dottedKey) {
		String value = getString(dottedKey);
		if (value == null) {
			return null;
		}
		return NamespaceID.from(value);
	}

	public @NotNull NamespaceID getNamespacedIDSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		return NamespaceID.from(getStringSafe(dottedKey));
	}

	public @NotNull Pos getPosSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		TomlArray tomlArray = this.parseResult.getArray(dottedKey);
		Objects.requireNonNull(tomlArray);
		if (tomlArray.size() != 3 && tomlArray.size() != 5) {
			throw new RuntimeException("Invalid position in " + fileName + "! Array with position must have 3 (x, y, z) or 5 (x, y, z, yaw, pitch) values!");
		}
		double x = tomlArray.getDouble(0);
		double y = tomlArray.getDouble(1);
		double z = tomlArray.getDouble(2);
		float yaw = 0f;
		float pitch = 0f;
		if (tomlArray.size() == 5) {
			yaw = (float) tomlArray.getDouble(3);
			pitch = (float) tomlArray.getDouble(4);
		}
		return new Pos(x, y, z, yaw, pitch);
	}

	public @Nullable Pos getPos(@NotNull String dottedKey) {
		TomlArray tomlArray = this.parseResult.getArray(dottedKey);
		if (tomlArray == null) {
			return null;
		}
		if (tomlArray.size() != 3 && tomlArray.size() != 5) {
			return null;
		}
		double x = tomlArray.getDouble(0);
		double y = tomlArray.getDouble(1);
		double z = tomlArray.getDouble(2);
		float yaw = 0f;
		float pitch = 0f;
		if (tomlArray.size() == 5) {
			yaw = (float) tomlArray.getDouble(3);
			pitch = (float) tomlArray.getDouble(4);
		}
		return new Pos(x, y, z, yaw, pitch);
	}

	public @NotNull Material getMaterialSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		Material material = Material.fromNamespaceId(getStringSafe(dottedKey));
		if (material == null) {
			throw new RuntimeException("Invalid material id " + dottedKey + " in " + fileName + "! Please specify valid one. eg. 'minecraft:stone'");
		}
		return material;
	}

	public @Nullable Material getMaterial(@NotNull String dottedKey) {
		String value = getString(dottedKey);
		if (value == null) {
			return null;
		}
		return Material.fromNamespaceId(value);
	}

	public @NotNull EntityType getEntityTypeSafe(@NotNull String dottedKey) {
		mustContain(dottedKey);
		EntityType entityType = EntityType.fromNamespaceId(getStringSafe(dottedKey));
		if (entityType == null) {
			throw new RuntimeException("Invalid entity type id " + dottedKey + " in " + fileName + "! Please specify valid one. eg. 'minecraft:zombie'");
		}
		return entityType;
	}

	public @Nullable EntityType getEntityType(@NotNull String dottedKey) {
		String value = getString(dottedKey);
		if (value == null) {
			return null;
		}
		return EntityType.fromNamespaceId(value);
	}

	public @Nullable TomlTable getTable(@NotNull String dottedKey) {
		return this.parseResult.getTable(dottedKey);
	}

	public @Nullable TomlArray getArray(@NotNull String dottedKey) {
		return this.parseResult.getArray(dottedKey);
	}
}
