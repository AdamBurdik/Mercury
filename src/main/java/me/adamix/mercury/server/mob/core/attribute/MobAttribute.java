package me.adamix.mercury.server.mob.core.attribute;

import lombok.Getter;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public enum MobAttribute {
	DAMAGE,
	ATTACK_SPEED(Attribute.ATTACK_SPEED),
	MOVEMENT_SPEED(Attribute.MOVEMENT_SPEED),
	MAX_HEALTH;

	@Nullable
	final Attribute defaultAttribute;

	MobAttribute() {
		this(null);
	}

	MobAttribute(@Nullable Attribute defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}

	public @NotNull String translationKey() {
		return "mob.attribute." + this.name().toLowerCase();
	}
}
