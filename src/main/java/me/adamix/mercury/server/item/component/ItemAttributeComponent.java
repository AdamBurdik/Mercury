package me.adamix.mercury.server.item.component;

import me.adamix.mercury.server.item.attribute.ItemAttribute;
import me.adamix.mercury.server.item.attribute.ItemAttributeValue;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

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
}
