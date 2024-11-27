package me.adamix.mercury.dungeon.configuration;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

public record DungeonInstanceConfiguration(
		@NotNull Pos spawnPos,
		@NotNull String worldName
) {
}
