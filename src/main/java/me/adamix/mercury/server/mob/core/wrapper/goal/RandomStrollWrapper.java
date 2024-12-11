package me.adamix.mercury.server.mob.core.wrapper.goal;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import org.tomlj.TomlTable;

public class RandomStrollWrapper implements GoalWrapper {
	private int radius;

	@Override
	public void parse(TomlTable tomlTable) {
		if (!tomlTable.contains("radius")) {
			throw new RuntimeException("Error while parsing random stroll goal! Table does not contain radius value!");
		}
		Long longValue = tomlTable.getLong("radius");
		if (longValue == null) {
			throw new RuntimeException("Error while parsing radius goal! Table does not contain radius value as int!");
		}

		this.radius = Math.toIntExact(longValue);
	}

	@Override
	public GoalSelector get(EntityCreature entityCreature) {
		return new RandomStrollGoal(entityCreature, this.radius);
	}
}
