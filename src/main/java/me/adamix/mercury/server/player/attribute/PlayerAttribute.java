package me.adamix.mercury.server.player.attribute;

import net.kyori.adventure.translation.Translatable;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PlayerAttribute implements Translatable {
	DAMAGE,
	ATTACK_SPEED(Attribute.ATTACK_SPEED),
	MOVEMENT_SPEED(Attribute.MOVEMENT_SPEED),
	HEALTH,
	MAX_HEALTH;

	@Nullable
	final Attribute defaultAttribute;

	PlayerAttribute() {
		this(null);
	}

	PlayerAttribute(@Nullable Attribute defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}

	@Override
	public @NotNull String translationKey() {
		return "player.attribute." + this.name();
	}
}
