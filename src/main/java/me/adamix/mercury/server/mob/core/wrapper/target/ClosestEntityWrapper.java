package me.adamix.mercury.server.mob.core.wrapper.target;

import me.adamix.mercury.server.mob.core.wrapper.predicate.PredicateParser;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.TargetSelector;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import org.tomlj.TomlTable;

import java.util.function.Predicate;

public class ClosestEntityWrapper implements TargetWrapper {
	private double range;
	private Predicate<Entity> predicate;

	@Override
	public void parse(TomlTable tomlTable) {
		if (!tomlTable.contains("range") || !tomlTable.contains("predicate")) {
			throw new RuntimeException("Error while parsing closest entity target! Table does not contain range or predicate value!");
		}
		Double doubleValue = tomlTable.getDouble("range");
		if (doubleValue == null) {
			throw new RuntimeException("Error while parsing follow entity goal! Table does not contain range value as double!");
		}
		TomlTable predicateTable = tomlTable.getTable("predicate");
		if (predicateTable == null) {
			throw new RuntimeException("Error while parsing follow entity goal! Table does not contain predicate table!");
		}

		this.predicate = PredicateParser.parse(predicateTable);
		this.range = doubleValue;
	}

	@Override
	public TargetSelector get(EntityCreature entityCreature) {
		return new ClosestEntityTarget(entityCreature, this.range, this.predicate);
	}
}
