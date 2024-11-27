package me.adamix.mercury.dungeon;

import me.adamix.mercury.Server;
import me.adamix.mercury.dungeon.configuration.DungeonConfiguration;
import me.adamix.mercury.dungeon.instance.DungeonInstance;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.utils.FileUtils;
import me.adamix.mercury.utils.TomlUtils;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DungeonManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(DungeonManager.class);
	private final Map<NamespaceID, DungeonConfiguration> configurationMap = new HashMap<>();
	private final Map<UUID, Dungeon> dungeonMap = new HashMap<>();

	public Dungeon create(NamespaceID dungeonID, Set<GamePlayer> playerSet) {
		DungeonConfiguration config = configurationMap.get(dungeonID);
		DungeonInstance dungeonInstance = Server.getDungeonInstanceManager().create(config.instanceID());

		Dungeon dungeon = new Dungeon(dungeonID, dungeonInstance, playerSet);
		dungeonMap.put(UUID.randomUUID(), dungeon);
		return dungeon;
	}

	public void register(NamespaceID dungeonID, DungeonConfiguration config) {
		LOGGER.info("Dungeon '{}' has been registered", dungeonID.asString());
		configurationMap.put(dungeonID, config);
	}

	public void registerAllDungeons() {
		FileUtils.forEachFile("resources/dungeon/dungeons", FileUtils.isTomlPredicate, this::register);
	}

	public void register(File tomlFile) {
		if (!tomlFile.exists()) {
			throw new RuntimeException("Unable to register dungeon instance! File does not exist");
		}

		try {
			TomlParseResult result = Toml.parse(tomlFile.toPath());
			TomlUtils.handleErrors(result, tomlFile.getName());

			NamespaceID dungeonID = TomlUtils.getNamespacedID(result, "id");
			NamespaceID instanceID = TomlUtils.getNamespacedID(result, "instance");

			DungeonConfiguration config = new DungeonConfiguration(
					instanceID
			);
			this.register(dungeonID, config);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasDungeon(UUID dungeonUniqueId) {
		return dungeonMap.containsKey(dungeonUniqueId);
	}

	public @Nullable Dungeon getDungeon(UUID dungeonUniqueId) {
		return dungeonMap.get(dungeonUniqueId);
	}
}
