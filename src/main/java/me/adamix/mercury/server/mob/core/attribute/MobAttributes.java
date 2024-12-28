package me.adamix.mercury.server.mob.core.attribute;

import me.adamix.mercury.server.mob.core.component.MobAttributeComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class MobAttributes {
	private final EnumMap<MobAttribute, Double> attributeMap = new EnumMap<>(MobAttribute.class);

	public @NotNull MobAttributes set(@NotNull MobAttribute attribute, @Nullable Double value) {
		if (value != null) {
			this.attributeMap.put(attribute, value);
		}
		return this;
	}

	public boolean has(MobAttribute attribute) {
		return attributeMap.containsKey(attribute);
	}

	public @Nullable Double get(MobAttribute attribute) {
		return attributeMap.get(attribute);
	}

	public MobAttributeComponent toComponent() {
		return new MobAttributeComponent(this.attributeMap);
	}
}
