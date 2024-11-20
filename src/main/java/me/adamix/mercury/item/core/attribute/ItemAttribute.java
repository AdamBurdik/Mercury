package me.adamix.mercury.item.core.attribute;

import lombok.Getter;
import net.minestom.server.entity.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public enum ItemAttribute {
	DAMAGE,
	ATTACK_SPEED(Attribute.ATTACK_SPEED),
	MOVEMENT_SPEED(Attribute.MOVEMENT_SPEED),
	MAX_HEALTH;

	@Nullable
	final Attribute defaultAttribute;

	ItemAttribute() {
		this(null);
	}

	ItemAttribute(@Nullable Attribute defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}

	public @NotNull String translationKey() {
		return "item.attribute." + this.name().toLowerCase();
	}
}
