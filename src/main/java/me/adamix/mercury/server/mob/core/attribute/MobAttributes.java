package me.adamix.mercury.server.mob.core.attribute;

import java.util.EnumMap;

public class MobAttributes {
	private final EnumMap<MobAttribute, Float> attributeMap = new EnumMap<>(MobAttribute.class);

	public MobAttributes() {
		for (MobAttribute attribute : MobAttribute.values()) {
			attributeMap.put(attribute, attribute.getDefaultValue());
		}
	}

	public MobAttributes set(MobAttribute attribute, float value) {
		attributeMap.put(attribute, value);
		return this;
	}

	public float get(MobAttribute attribute) {
		return attributeMap.get(attribute);
	}
}
