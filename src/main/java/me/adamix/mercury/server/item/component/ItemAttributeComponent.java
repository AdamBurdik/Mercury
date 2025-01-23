package me.adamix.mercury.server.item.component;

import me.adamix.mercury.server.attribute.AttributeContainer;
import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.attribute.MercuryAttributeValue;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.attribute.PlayerAttributeContainer;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record ItemAttributeComponent(EnumMap<MercuryAttribute, MercuryAttributeValue> attributeMap) implements MercuryItemComponent {
	public @Nullable MercuryAttributeValue get(MercuryAttribute attribute) {
		return attributeMap.get(attribute);
	}

	public void applyToPlayer(MercuryPlayer player) {
		// Apply default ones
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
		// Apply custom ones
		PlayerAttributeContainer playerAttribute = player.getProfileData().getAttributes();

		// Damage
		if (attributeMap.containsKey(MercuryAttribute.DAMAGE)) {
			MercuryAttributeValue damageAttribute = attributeMap.get(MercuryAttribute.DAMAGE);
			playerAttribute.modify(MercuryAttribute.DAMAGE, damageAttribute.value(), damageAttribute.operation());
		}
		// Attack Speed
		if (attributeMap.containsKey(MercuryAttribute.ATTACK_SPEED)) {
			MercuryAttributeValue attackSpeedAttribute = attributeMap.get(MercuryAttribute.ATTACK_SPEED);
			playerAttribute.modify(MercuryAttribute.ATTACK_SPEED, attackSpeedAttribute.value(), attackSpeedAttribute.operation());
		}
	}

	@Override
	public String name() {
		return "itemAttributeComponent";
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		ItemAttributeComponent that = (ItemAttributeComponent) object;
		return Objects.equals(attributeMap, that.attributeMap);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(attributeMap);
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
		AttributeContainer attributeContainer = new AttributeContainer();

		map.forEach((name, attributeValue) -> {
			MercuryAttribute attribute = MercuryAttribute.valueOf(name);

			Map<String, Object> valueMap = (Map<String, Object>) attributeValue;
			attributeContainer.set(attribute, (double) valueMap.get("value"), AttributeOperation.valueOf((String) valueMap.get("operation")));
		});

		return new ItemAttributeComponent(attributeContainer.getAttributeMap());
	}
}
