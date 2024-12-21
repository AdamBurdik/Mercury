package me.adamix.mercury.server.player.stats;

import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

public enum StatisticCategory implements Translatable {
	GENERAL, // General stuff without any category
	KILLED_COUNT, // Kill count of specific mob
	ITEMS_CRAFTED, // Count of specific items crafted
	ITEMS_USED, // Count of specific items used
	ITEMS_CONSUMED, // Count of specific items consumed
	DAMAGE_TAKEN, // Damage taken by specific mobs/ways
	DAMAGE_DEALT, // Damage dealt to specific mobs
	DEATHS; // Death count of specific ways

	@Override
	public @NotNull String translationKey() {
		return "player.statistics." + this.name().toLowerCase();
	}
}
