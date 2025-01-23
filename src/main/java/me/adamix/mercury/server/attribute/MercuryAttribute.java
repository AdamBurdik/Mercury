package me.adamix.mercury.server.attribute;

import lombok.Getter;
import net.kyori.adventure.translation.Translatable;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public enum MercuryAttribute implements Translatable {
	DAMAGE,
	ATTACK_SPEED(Attribute.ATTACK_SPEED),
	MOVEMENT_SPEED(Attribute.MOVEMENT_SPEED),
	MAX_HEALTH;

	@Nullable
	private final Attribute defaultAttribute;

	MercuryAttribute() {
		this(null);
	}

	MercuryAttribute(@Nullable Attribute defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}

	@Override
	public @NotNull String translationKey() {
		return "attribute." + this.name();
	}
}
