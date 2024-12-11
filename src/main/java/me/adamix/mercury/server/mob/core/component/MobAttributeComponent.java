package me.adamix.mercury.server.mob.core.component;

import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.mob.core.attribute.MobAttribute;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public record MobAttributeComponent(@NotNull EnumMap<MobAttribute, Double> attributeMap) implements MercuryMobComponent {
	public @Nullable Double get(MobAttribute attribute) {
		return attributeMap.get(attribute);
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
