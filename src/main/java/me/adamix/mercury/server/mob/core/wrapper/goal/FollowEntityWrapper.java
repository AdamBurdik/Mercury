package me.adamix.mercury.server.mob.core.wrapper.goal;

import me.adamix.mercury.server.mob.core.goal.FollowEntityGoal;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlTable;

public class FollowEntityWrapper implements GoalWrapper {
	private double range;

	@Override
	public void parse(@NotNull TomlTable tomlTable) {
		if (!tomlTable.contains("range")) {
			throw new RuntimeException("Error while parsing follow entity goal! Table does not contain range value!");
		}
		Double doubleValue = tomlTable.getDouble("range");
		if (doubleValue == null) {
			throw new RuntimeException("Error while parsing follow entity goal! Table does not contain range value as double!");
		}

		this.range = doubleValue;
	}

	@Override
	public @NotNull GoalSelector get(@NotNull EntityCreature entityCreature) {
		return new FollowEntityGoal(entityCreature, this.range);
	}
}
