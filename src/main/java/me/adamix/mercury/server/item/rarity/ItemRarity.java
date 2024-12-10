package me.adamix.mercury.server.item.rarity;

import me.adamix.mercury.server.item.component.RarityComponent;
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

	public RarityComponent toComponent() {
		return new RarityComponent(this);
	}
}
