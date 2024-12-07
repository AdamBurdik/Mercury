package me.adamix.mercury.server.mob.core.attribute;

import lombok.Getter;

@Getter
public enum MobAttribute {
	MOVEMENT_SPEED(1f),
	DAMAGE(1f),
	HEALTH(100f),
	MAX_HEALTH(100f);

	private final float defaultValue;

	MobAttribute(float defaultValue) {
		this.defaultValue = defaultValue;
	}
}
