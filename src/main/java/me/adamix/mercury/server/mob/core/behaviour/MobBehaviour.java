package me.adamix.mercury.server.mob.core.behaviour;

import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.ai.TargetSelector;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MobBehaviour {
	private final List<GoalSelector> goalSelectors = new ArrayList<>();
	private final List<TargetSelector> targetSelectors = new ArrayList<>();

	public void addGoalSelector(GoalSelector goalSelector) {
		this.goalSelectors.add(goalSelector);
	}

	public void addGoalSelectors(List<GoalSelector> goalSelectors) {
		this.goalSelectors.addAll(goalSelectors);
	}

	public void addTargetSelector(TargetSelector targetSelector) {
		this.targetSelectors.add(targetSelector);
	}

	public void addTargetSelectors(List<TargetSelector> targetSelectors) {
		this.targetSelectors.addAll(targetSelectors);
	}

	/**
	 * Called when entity is created, before all other methods in behaviour
	 * @param entity entity that has been spawned
	 */
	public void init(EntityCreature entity) {};

	/**
	 * Called right after {@link #init(EntityCreature)}, when spawning entity to instance
	 * @param instance instance where entity will be spawned in
	 * @param pos position where entity will be spawned
	 */
	public void onSpawn(Instance instance, Pos pos) {}
	public void onPlayerAttack() {}
	public void onDamagedByPlayer() {}
	public void onDeath() {}

	public static MobBehaviour empty() {
		return new MobBehaviour();
	}
}
