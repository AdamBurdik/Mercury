package me.adamix.mercury.server.item.attribute;

import lombok.Getter;
import me.adamix.mercury.server.item.component.ItemAttributeComponent;
import net.minestom.server.entity.attribute.AttributeOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;

@Getter
public class ItemAttributes {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemAttributes.class);
	private final EnumMap<ItemAttribute, ItemAttributeValue> attributeMap = new EnumMap<>(ItemAttribute.class);

	public @NotNull ItemAttributes set(@NotNull ItemAttribute attribute, float value) {
		return this.set(attribute, value, AttributeOperation.ADD_VALUE);
	}

	public @NotNull ItemAttributes set(@NotNull ItemAttribute attribute, @Nullable ItemAttributeValue value) {
		if (value != null) {
			this.attributeMap.put(attribute, value);
		}
		return this;
	}

	public @NotNull ItemAttributes set(@NotNull ItemAttribute attribute, float value, @NotNull AttributeOperation operation) {
		if (operation == AttributeOperation.MULTIPLY_TOTAL) {
			LOGGER.warn("AttributeOperation.MULTIPLY_TOTAL is not supported at the moment! Item information will be displayed incorrectly.");
		}
		return set(attribute, new ItemAttributeValue(value, operation));
	}

	public boolean has(ItemAttribute attribute) {
		return attributeMap.containsKey(attribute);
	}

	public @Nullable ItemAttributeValue get(ItemAttribute attribute) {
		return attributeMap.get(attribute);
	}

	public ItemAttributeComponent toComponent() {
		return new ItemAttributeComponent(this.attributeMap);
	}
}
