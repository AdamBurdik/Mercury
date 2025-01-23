package me.adamix.mercury.server.mob.core.attribute;

import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.mob.core.component.MobAttributeComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class MobAttributeContainer {
	private final EnumMap<MercuryAttribute, Double> attributeMap = new EnumMap<>(MercuryAttribute.class);

	public @NotNull MobAttributeContainer set(@NotNull MercuryAttribute attribute, @Nullable Double value) {
		if (value != null) {
			this.attributeMap.put(attribute, value);
		}
		return this;
	}

	public boolean has(MercuryAttribute attribute) {
		return attributeMap.containsKey(attribute);
	}

	public @Nullable Double get(MercuryAttribute attribute) {
		return attributeMap.get(attribute);
	}

	public MobAttributeComponent toComponent() {
		return new MobAttributeComponent(this.attributeMap);
	}
}
