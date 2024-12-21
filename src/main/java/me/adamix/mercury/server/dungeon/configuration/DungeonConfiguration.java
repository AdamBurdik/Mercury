package me.adamix.mercury.server.dungeon.configuration;

import me.adamix.mercury.server.dungeon.spawner.DungeonSpawner;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record DungeonConfiguration(
		@NotNull Pos spawnPos,
		@NotNull String instance,
		@NotNull Set<DungeonSpawner> dungeonSpawnerSet
) {
}
