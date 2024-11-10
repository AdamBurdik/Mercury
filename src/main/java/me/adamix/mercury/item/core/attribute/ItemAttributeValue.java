package me.adamix.mercury.item.core.attribute;

import me.adamix.mercury.common.SerializableEntity;
import net.minestom.server.entity.attribute.AttributeOperation;

import java.util.Map;

public record ItemAttributeValue(float value, AttributeOperation operation) implements SerializableEntity {
	@Override
	public Map<String, Object> serialize() {
		return Map.of(
				"value", value,
				"operation", operation.name()
		);
	}
}
