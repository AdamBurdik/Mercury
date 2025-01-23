package me.adamix.mercury.server.mob.core.component;

import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.mob.core.MercuryMob;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public record MobAttributeComponent(@NotNull EnumMap<MercuryAttribute, Double> attributeMap) implements MercuryMobComponent {
	public @Nullable Double get(MercuryAttribute attribute) {
		return attributeMap.get(attribute);
	}

	public void set(MercuryAttribute attribute, double value) {
		attributeMap.put(attribute, value);
	}

	public void setIfPresent(MercuryAttribute attribute, double value) {
		if (attributeMap.containsKey(attribute)) {
			this.set(attribute, value);
		}
	}

	public void applyToMob(MercuryMob mob) {
		attributeMap.forEach((attribute, value) -> {
			Attribute defaultAttribute = attribute.getDefaultAttribute();
			if (defaultAttribute != null) {

				mob.getAttribute(defaultAttribute)
						.setBaseValue(value);
			}
		});
	}
}
