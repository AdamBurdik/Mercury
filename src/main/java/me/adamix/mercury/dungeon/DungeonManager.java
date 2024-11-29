package me.adamix.mercury.dungeon;

import me.adamix.mercury.Server;
import me.adamix.mercury.dungeon.configuration.DungeonConfiguration;
import me.adamix.mercury.dungeon.instance.DungeonInstance;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.toml.TomlConfiguration;
import me.adamix.mercury.utils.FileUtils;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DungeonManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(DungeonManager.class);
	private final Map<NamespaceID, DungeonConfiguration> configurationMap = new HashMap<>();
	private final Map<UUID, Dungeon> dungeonMap = new HashMap<>();

	public @NotNull Dungeon create(@NotNull NamespaceID dungeonID, @NotNull Set<GamePlayer> playerSet) {
		DungeonConfiguration config = configurationMap.get(dungeonID);
		DungeonInstance dungeonInstance = Server.getDungeonInstanceManager().create(config.instanceID());

		Dungeon dungeon = new Dungeon(dungeonID, dungeonInstance, playerSet);
		dungeonMap.put(UUID.randomUUID(), dungeon);
		return dungeon;
	}

	public void register(@NotNull NamespaceID dungeonID, @NotNull DungeonConfiguration config) {
		LOGGER.info("Dungeon '{}' has been registered", dungeonID.asString());
		configurationMap.put(dungeonID, config);
	}

	public void registerAllDungeons() {
		FileUtils.forEachFile("resources/dungeon/dungeons", FileUtils.isTomlPredicate, this::register);
	}

	public void register(@NotNull File tomlFile) {
		if (!tomlFile.exists()) {
			throw new RuntimeException("Unable to register dungeon instance! File does not exist");
		}

		TomlConfiguration toml = new TomlConfiguration(tomlFile);

		NamespaceID dungeonID = toml.getNamespacedIDSafe("id");
		NamespaceID instanceID = toml.getNamespacedIDSafe("dungeon_id");

		DungeonConfiguration config = new DungeonConfiguration(
				instanceID
		);

		this.register(dungeonID, config);
	}

	public boolean hasDungeon(@NotNull UUID dungeonUniqueId) {
		return dungeonMap.containsKey(dungeonUniqueId);
	}

	public @Nullable Dungeon getDungeon(@NotNull UUID dungeonUniqueId) {
		return dungeonMap.get(dungeonUniqueId);
	}
}
