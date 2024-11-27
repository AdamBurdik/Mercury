package me.adamix.mercury.utils;

import me.adamix.mercury.item.core.attribute.ItemAttributeValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class TomlUtils {
	public static @Nullable ItemAttributeValue parseItemAttribute(TomlTable table, String fileName) {
		if (!table.contains(fileName)) {
			return null;
		}

		Object attributeObject = table.get(fileName);

		if (attributeObject instanceof Number number) {
			return new ItemAttributeValue(number.floatValue(), AttributeOperation.ADD_VALUE);
		} else if (attributeObject instanceof TomlTable attributeTable) {
			Double doubleValue = attributeTable.getDouble("value");
			if (doubleValue == null) {
				return null;
			}
			double value = doubleValue;

			String stringOperation = attributeTable.getString("operation");
			if (stringOperation == null) {
				return null;
			}
			AttributeOperation operation;
			try {
				operation = AttributeOperation.valueOf(stringOperation);
			} catch (IllegalArgumentException e) {
				return null;
			}

			return new ItemAttributeValue((float) value, operation);
		}

		return null;
	}

	public static void handleErrors(TomlParseResult result, String fileName) {
		if (result.hasErrors()) {
			result.errors().forEach(error -> {
				throw new RuntimeException("Error while parsing " + fileName + "!\n " + error.toString());
			});
		}
	}

	public static void mustContain(TomlParseResult result, String dottedKey) {
		if (!result.contains(dottedKey)) {
			throw new RuntimeException("Unable to find property " + dottedKey + "!");
		}
	}

	public static @NotNull String getString(TomlParseResult result, String dottedKey) {
		mustContain(result, dottedKey);
		String value = result.getString(dottedKey);
		Objects.requireNonNull(value);
		return value;
	}

	public static NamespaceID getNamespacedID(TomlParseResult result, String dottedKey) {
		mustContain(result, dottedKey);
		return NamespaceID.from(getString(result, dottedKey));
	}

	public static NamespaceID getNamespacedID(TomlParseResult result, String dottedKey, String domain) {
		mustContain(result, dottedKey);
		return new NamespaceID(domain, getString(result, dottedKey));
	}

	public static Pos getPos(TomlParseResult result, String dottedKey) {
		mustContain(result, dottedKey);
		TomlArray tomlArray = result.getArray(dottedKey);
		Objects.requireNonNull(tomlArray);
		if (tomlArray.size() != 3 && tomlArray.size() != 5) {
			throw new RuntimeException("Array with position must have 3 (x, y, z) or 5 (x, y, z, yaw, pitch) values!");
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

	public static Material getMaterial(TomlParseResult result, String dottedKey) {
		mustContain(result, dottedKey);
		return Material.fromNamespaceId(getString(result, dottedKey));
	}
}
