package me.adamix.mercury.server.item.component;

import me.adamix.mercury.server.item.attribute.ItemAttribute;
import me.adamix.mercury.server.item.attribute.ItemAttributeValue;
import me.adamix.mercury.server.item.attribute.ItemAttributes;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public record ItemAttributeComponent(EnumMap<ItemAttribute, ItemAttributeValue> attributeMap) implements MercuryItemComponent {
	public @Nullable ItemAttributeValue get(ItemAttribute attribute) {
		return attributeMap.get(attribute);
	}

	public void applyToPlayer(MercuryPlayer player) {
		attributeMap.forEach((attribute, value) -> {
			Attribute defaultAttribute = attribute.getDefaultAttribute();
			if (defaultAttribute != null) {

				player.getAttribute(defaultAttribute)
						.addModifier(
								new AttributeModifier(
										defaultAttribute.name(),
										value.value(),
										value.operation()
								)
						);
			}
		});
	}

	@Override
	public String name() {
		return "itemAttributeComponent";
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		attributeMap.forEach((name, attributeValue) -> {
			map.put(name.name(), Map.of(
					"value", attributeValue.value(),
					"operation", attributeValue.operation().name()
			));
		});


		return map;
	}

	@SuppressWarnings("unchecked")
	public static @NotNull ItemAttributeComponent deserialize(Map<String, Object> map) {
		ItemAttributes itemAttributes = new ItemAttributes();

		map.forEach((name, attributeValue) -> {
			ItemAttribute itemAttribute = ItemAttribute.valueOf(name);

			Map<String, Object> valueMap = (Map<String, Object>) attributeValue;
			itemAttributes.set(itemAttribute, (float) valueMap.get("value"), AttributeOperation.valueOf((String) valueMap.get("operation")));
		});

		return itemAttributes.toComponent();
	}
}
