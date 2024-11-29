package me.adamix.mercury.dungeon.instance;

import me.adamix.mercury.dungeon.configuration.DungeonInstanceConfiguration;
import me.adamix.mercury.toml.TomlConfiguration;
import me.adamix.mercury.utils.FileUtils;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DungeonInstanceManager {
	private final Logger LOGGER = LoggerFactory.getLogger(DungeonInstanceManager.class);
	private final Map<NamespaceID, DungeonInstanceConfiguration> configurationMap = new HashMap<>();
	private final Set<DungeonInstance> dungeonInstanceSet = new HashSet<>();

	public void register(@NotNull NamespaceID instanceID, @NotNull DungeonInstanceConfiguration config) {
		LOGGER.info("Dungeon Instance '{}' has been registered", instanceID.asString());
		configurationMap.put(instanceID, config);
	}

	public void registerAllInstances() {
		FileUtils.forEachFile("resources/dungeon/instances", FileUtils.isTomlPredicate, this::register);
	}


	public void register(@NotNull File tomlFile) {
		if (!tomlFile.exists()) {
			throw new RuntimeException("Unable to register dungeon instance! File does not exist");
		}

		TomlConfiguration toml = new TomlConfiguration(tomlFile);

		NamespaceID instanceID = toml.getNamespacedIDSafe("id");
		String worldName = toml.getStringSafe("world_name");
		Pos spawnPos = toml.getPosSafe("spawn_pos");

		DungeonInstanceConfiguration config = new DungeonInstanceConfiguration(
				spawnPos,
				worldName
		);
		this.register(instanceID, config);
	}

	public @NotNull DungeonInstance create(@NotNull NamespaceID instanceID) {
		DungeonInstanceConfiguration config =  configurationMap.get(instanceID);
		DungeonInstance instance = new DungeonInstance(config);
		dungeonInstanceSet.add(instance);
		return instance;
	}

}
