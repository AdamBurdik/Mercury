package me.adamix.mercury.dungeon.instance;

import me.adamix.mercury.dungeon.configuration.DungeonInstanceConfiguration;
import me.adamix.mercury.utils.FileUtils;
import me.adamix.mercury.utils.TomlUtils;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DungeonInstanceManager {
	private final Logger LOGGER = LoggerFactory.getLogger(DungeonInstanceManager.class);
	private final Map<NamespaceID, DungeonInstanceConfiguration> configurationMap = new HashMap<>();
	private final Set<DungeonInstance> dungeonInstanceSet = new HashSet<>();

	public void register(NamespaceID instanceID, DungeonInstanceConfiguration config) {
		LOGGER.info("Dungeon Instance '{}' has been registered", instanceID.asString());
		configurationMap.put(instanceID, config);
	}

	public void registerAllInstances() {
		FileUtils.forEachFile("resources/dungeon/instances", FileUtils.isTomlPredicate, this::register);
	}


	public void register(File tomlFile) {
		if (!tomlFile.exists()) {
			throw new RuntimeException("Unable to register dungeon instance! File does not exist");
		}

		try {
			TomlParseResult result = Toml.parse(tomlFile.toPath());
			TomlUtils.handleErrors(result, tomlFile.getName());

			NamespaceID instanceID = TomlUtils.getNamespacedID(result, "id");
			Pos spawnPos = TomlUtils.getPos(result, "spawn_pos");
			String worldName = TomlUtils.getString(result, "world_name");

			DungeonInstanceConfiguration config = new DungeonInstanceConfiguration(
					spawnPos,
					worldName
			);
			register(instanceID, config);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public DungeonInstance create(NamespaceID instanceID) {
		DungeonInstanceConfiguration config =  configurationMap.get(instanceID);
		DungeonInstance instance = new DungeonInstance(config);
		dungeonInstanceSet.add(instance);
		return instance;
	}

}
