package me.adamix.mercury.server.item.core.rarity;

import org.jetbrains.annotations.NotNull;

public enum ItemRarity {
	UNCOMMON,
	COMMON,
	RARE,
	EPIC,
	LEGENDARY,
	MYTHICAL,
	UNIQUE;

	public @NotNull String translationKey() {
		return "item.rarity." + this.name().toLowerCase();
	}
}
