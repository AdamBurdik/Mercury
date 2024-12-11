package me.adamix.mercury.server.mob.core.wrapper.target;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.TargetSelector;
import org.tomlj.TomlTable;

public interface TargetWrapper {
	void parse(TomlTable tomlTable);
	TargetSelector get(EntityCreature entityCreature);
}
