package me.adamix.mercury.server.dungeon.spawner;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.dungeon.Dungeon;
import me.adamix.mercury.server.mob.core.MercuryMob;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Getter
public class DungeonSpawner {
	private static final Logger LOGGER = LoggerFactory.getLogger(DungeonSpawner.class);
	private static final Tag<UUID> spawnerUniqueIdTag = Tag.UUID("spawnerUniqueId");
	private final @NotNull UUID uniqueId;
	private final @NotNull Point point;
	private final int radius;
	private final @NotNull NamespaceID[] entityTypes;
	private final int spawnInterval;
	private final int spawnIntervalOffset;
	private final int maxMobCount;
	private final int[] mobsPerSpawn;

	private final Random random;
	private int currentSpawnTime = 0;
	private final Set<UUID> mobSet = new HashSet<>();

	public DungeonSpawner(
			@NotNull UUID uniqueId,
			@NotNull Point point,
			int radius,
			@NotNull NamespaceID[] entityTypes,
			int spawnInterval,
			int spawnIntervalOffset,
			int maxMobCount,
			int[] mobsPerSpawn
	) {
		this.uniqueId = uniqueId;
		this.point = point;
		this.radius = radius;
		this.entityTypes = entityTypes;
		this.spawnInterval = spawnInterval;
		this.spawnIntervalOffset = spawnIntervalOffset;
		this.maxMobCount = maxMobCount;
		this.mobsPerSpawn = mobsPerSpawn;
		if (mobsPerSpawn.length != 2) {
			throw new IllegalArgumentException("Invalid mobsPerSpawn array: " + Arrays.toString(mobsPerSpawn) + "! It should have exactly 2 elements.");
		}
		this.random = new Random();
	}

	public void tick(Dungeon dungeon) {
		currentSpawnTime++;
		if (currentSpawnTime >= spawnInterval) {
			this.spawn(dungeon);
			this.currentSpawnTime = this.random.nextInt(-spawnIntervalOffset, spawnIntervalOffset);
		}
	}

	public void spawn(Dungeon dungeon) {
		int mobGap = this.maxMobCount - this.mobSet.size();
		int mobCount = Math.min(this.random.nextInt(mobsPerSpawn[0], mobsPerSpawn[1]), mobGap);

		for (int i = 0; i < mobCount; i++) {
			int entityIndex = this.random.nextInt(0, entityTypes.length);
			NamespaceID mobID = entityTypes[entityIndex];

			@Nullable MercuryMob mercuryMob = Server.getMobManager().getRegistered(mobID);
			if (mercuryMob == null) {
				LOGGER.warn("Mob with ID '{}' cannot be found!", mobID.asString());
				continue;
			}

			mercuryMob.setTag(spawnerUniqueIdTag, this.uniqueId);

			int yOffset = this.random.nextInt(-this.radius, this.radius);
			int xOffset = this.random.nextInt(-this.radius, this.radius);
			int zOffset = this.random.nextInt(-this.radius, this.radius);

			Point offsetPoint = point.add(xOffset, yOffset, zOffset);
			Pos offsetPosition = new Pos(offsetPoint, this.random.nextFloat(-180, 180), this.random.nextFloat(-90, 90));

			if (dungeon.spawnMob(mercuryMob, offsetPosition)) {
				this.mobSet.add(mercuryMob.getUuid());
			}
		}
	}

	public void removeMob(UUID uniqueId) {
		this.mobSet.remove(uniqueId);
	}
}
