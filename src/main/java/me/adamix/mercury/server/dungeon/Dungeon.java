package me.adamix.mercury.server.dungeon;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.dungeon.instance.DungeonInstance;
import me.adamix.mercury.server.player.GamePlayer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@Getter
public class Dungeon {
	private final NamespaceID namespaceID;
	private final DungeonInstance instance;
	private final Set<GamePlayer> playerSet;

	public Dungeon(NamespaceID namespaceID, DungeonInstance dungeonInstance, Set<GamePlayer> playerSet) {
		this.namespaceID = namespaceID;
		this.instance = dungeonInstance;
		this.playerSet = playerSet;
	}

	public void start() {
		// Teleport all players to start
		Pos spawnPos = instance.getSpawnPos();
		for (GamePlayer player : playerSet) {
			player.setInstance(instance, spawnPos);
		}
	}

	public static @Nullable Dungeon of(GamePlayer player) {
		return of(player.getDungeonUniqueId());
	}

	public static @Nullable Dungeon of(UUID dungeonUniqueId) {
		return Server.getDungeonManager().getDungeon(dungeonUniqueId);
	}
}
