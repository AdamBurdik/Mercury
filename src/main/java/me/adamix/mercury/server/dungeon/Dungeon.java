package me.adamix.mercury.server.dungeon;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.dungeon.configuration.DungeonConfiguration;
import me.adamix.mercury.server.dungeon.instance.DungeonInstance;
import me.adamix.mercury.server.dungeon.spawner.DungeonSpawner;
import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.utils.PosUtils;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Getter

public class Dungeon {
	private static final Tag<UUID> dungeonUniqueIdTag = Tag.UUID("dungeonUniqueId");
	private final @NotNull NamespaceID dungeonID;
	private final @NotNull UUID uniqueId;
	private final @NotNull DungeonInstance instance;
	private final @NotNull Set<MercuryPlayer> playerSet;
	private final @NotNull DungeonConfiguration config;
	private Task task;

	public Dungeon(
			@NotNull NamespaceID dungeonID,
			@NotNull UUID uniqueId,
			@NotNull DungeonConfiguration config,
			@NotNull Set<MercuryPlayer> playerSet
	) throws IOException {
		this.dungeonID = dungeonID;
		this.uniqueId = uniqueId;
		this.config = config;
		this.playerSet = playerSet;

		this.instance = new DungeonInstance();
		this.instance.setChunkLoader(new PolarLoader(Path.of("worlds/dungeons/" + config.instance() + ".polar")));
		MinecraftServer.getInstanceManager().registerInstance(this.instance);
	}

	public void start() {
		// Teleport all players to start
		Pos spawnPos = config.spawnPos();
		for (MercuryPlayer player : playerSet) {
			player.setInstance(instance, spawnPos);
		}
		startTask();
	}

	public void startTask() {
		task = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
			for (DungeonSpawner dungeonSpawner : config.dungeonSpawnerSet()) {
				dungeonSpawner.tick(this);
			}
		}, TaskSchedule.tick(1), TaskSchedule.tick(1));
	}

	public boolean spawnMob(MercuryMob mob, Pos position) {
		@Nullable Pos groundPosition = PosUtils.findGroundPoint(position, this.instance);
		if (groundPosition == null) {
			return false;
		}

		// ToDo Add some check if mob is spawned successfully ( make spawn return boolean? )
		Server.getMobManager().spawn(mob, this.instance, groundPosition);
		mob.setTag(dungeonUniqueIdTag, this.uniqueId);
		return true;
	}

	public void dispose() {
		MinecraftServer.getInstanceManager().unregisterInstance(this.instance);
		task.cancel();
		task = null;
	}

	public static @Nullable Dungeon of(MercuryPlayer player) {
		return of(player.getDungeonUniqueId());
	}

	public static @Nullable Dungeon of(UUID dungeonUniqueId) {
		return Server.getDungeonManager().getDungeon(dungeonUniqueId);
	}
}
