package me.adamix.mercury.dungeon.instance;

import lombok.Getter;
import me.adamix.mercury.dungeon.configuration.DungeonInstanceConfiguration;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import java.util.UUID;

@Getter
public class DungeonInstance extends InstanceContainer {
	private final Pos spawnPos;

	public DungeonInstance(DungeonInstanceConfiguration config) {
		super(UUID.randomUUID(), DimensionType.OVERWORLD);

		this.spawnPos = config.spawnPos();
	}
}
