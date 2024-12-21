package me.adamix.mercury.server.dungeon.instance;

import lombok.Getter;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;

import java.util.UUID;

@Getter
public class DungeonInstance extends InstanceContainer {
	public DungeonInstance() {
		super(UUID.randomUUID(), DimensionType.OVERWORLD);
	}
}
