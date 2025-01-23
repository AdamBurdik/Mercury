package me.adamix.mercury.server.utils;

import me.adamix.mercury.server.attribute.MercuryAttributeValue;
import net.minestom.server.entity.attribute.AttributeOperation;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlTable;

public class TomlUtils {
	public static @Nullable MercuryAttributeValue parseAttribute(TomlTable table, String fileName) {
		if (!table.contains(fileName)) {
			return null;
		}

		Object attributeObject = table.get(fileName);

		if (attributeObject instanceof Number number) {
			return new MercuryAttributeValue(number.floatValue(), AttributeOperation.ADD_VALUE);
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

			return new MercuryAttributeValue((float) value, operation);
		}

		return null;
	}
}
