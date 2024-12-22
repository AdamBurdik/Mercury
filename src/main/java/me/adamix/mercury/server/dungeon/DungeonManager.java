package me.adamix.mercury.server.dungeon;

import me.adamix.mercury.server.dungeon.configuration.DungeonConfiguration;
import me.adamix.mercury.server.dungeon.spawner.DungeonSpawner;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.toml.MercuryArray;
import me.adamix.mercury.server.toml.MercuryConfiguration;
import me.adamix.mercury.server.toml.MercuryTable;
import me.adamix.mercury.server.utils.FileUtils;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DungeonManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(DungeonManager.class);
	private final Map<NamespaceID, DungeonConfiguration> configurationMap = new HashMap<>();
	private final Map<UUID, Dungeon> dungeonMap = new HashMap<>();

	public @NotNull Dungeon create(@NotNull NamespaceID dungeonID, @NotNull Set<MercuryPlayer> playerSet) {
		DungeonConfiguration config = configurationMap.get(dungeonID);
		UUID uuid = UUID.randomUUID();
		Dungeon dungeon;
		try {
			dungeon = new Dungeon(dungeonID, uuid, config, playerSet);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		dungeonMap.put(uuid, dungeon);

		for (MercuryPlayer mercuryPlayer : playerSet) {
			mercuryPlayer.setDungeonUniqueId(uuid);
		}

		return dungeon;
	}

	public void register(@NotNull NamespaceID dungeonID, @NotNull DungeonConfiguration config) {
		LOGGER.info("Dungeon '{}' has been registered", dungeonID.asString());
		configurationMap.put(dungeonID, config);
	}

	public void registerAllDungeons() {
		configurationMap.clear();
		FileUtils.forEachFile("resources/dungeons", FileUtils.isTomlPredicate, this::register);
	}

	public void register(@NotNull File tomlFile) {
		if (!tomlFile.exists()) {
			throw new RuntimeException("Unable to register dungeon! File does not exist");
		}

		MercuryConfiguration toml = new MercuryConfiguration(tomlFile);

		NamespaceID dungeonID = toml.getNamespacedIDSafe("id");
		Pos spawnPosition = toml.getPosSafe("spawn_pos");
		String worldName = toml.getStringSafe("instance");

		Set<DungeonSpawner> dungeonSpawnerSet = new HashSet<>();
		MercuryArray spawnerArray = toml.getArray("spawners");

		if (spawnerArray != null) {
			for (int i = 0; i < spawnerArray.size(); i++) {
				MercuryTable spawnerTable = spawnerArray.getTable(i);
				if (spawnerTable == null) {
					continue;
				}

				Pos position = spawnerTable.getPosSafe("position");
				int radius = spawnerTable.getIntegerSafe("radius");
				NamespaceID[] entityTypes = spawnerTable.getArraySafe("entity_types").toNamespacedIDArray();
				int spawnInterval = spawnerTable.getIntegerSafe("spawn_interval");
				int spawnIntervalOffset = spawnerTable.getIntegerSafe("spawn_interval_offset");
				int maxMobCount = spawnerTable.getIntegerSafe("max_mobs");
				int[] mobsPerSpawn = spawnerTable.getArraySafe("mobs_per_spawn").toIntegerArray();

				DungeonSpawner dungeonSpawner = new DungeonSpawner(
						UUID.randomUUID(),
						position,
						radius,
						entityTypes,
						spawnInterval,
						spawnIntervalOffset,
						maxMobCount,
						mobsPerSpawn
				);
				dungeonSpawnerSet.add(dungeonSpawner);
			}
		}

		DungeonConfiguration config = new DungeonConfiguration(
				spawnPosition,
				worldName,
				dungeonSpawnerSet
		);

		this.register(dungeonID, config);
	}

	public Set<NamespaceID> getDungeonIds() {
		return configurationMap.keySet();
	}

	public boolean hasDungeon(@NotNull UUID dungeonUniqueId) {
		return dungeonMap.containsKey(dungeonUniqueId);
	}

	public @Nullable Dungeon getDungeon(@Nullable UUID dungeonUniqueId) {
		if (dungeonUniqueId == null) {
			return null;
		}

		return dungeonMap.get(dungeonUniqueId);
	}
}
