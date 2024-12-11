package me.adamix.mercury.server.item.rarity;

import me.adamix.mercury.server.item.component.ItemRarityComponent;
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

	public ItemRarityComponent toComponent() {
		return new ItemRarityComponent(this);
	}
}
