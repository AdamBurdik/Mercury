package me.adamix.mercury.server.mob.core.wrapper.goal;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.GoalSelector;
import org.tomlj.TomlTable;

public interface GoalWrapper {
	void parse(TomlTable tomlTable);
	GoalSelector get(EntityCreature entityCreature);
}
