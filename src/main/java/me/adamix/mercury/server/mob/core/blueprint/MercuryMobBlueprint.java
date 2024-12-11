package me.adamix.mercury.server.mob.core.blueprint;

import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.server.mob.core.component.MercuryMobComponent;
import me.adamix.mercury.server.mob.core.wrapper.goal.GoalWrapper;
import me.adamix.mercury.server.mob.core.wrapper.target.TargetWrapper;
import net.minestom.server.entity.EntityType;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public record MercuryMobBlueprint(
		@NotNull NamespaceID blueprintID,
		@NotNull EntityType entityType,
		@NotNull String name,
		@NotNull MercuryMobComponent[] components,
		@NotNull GoalWrapper[] goalWrappers,
		@NotNull TargetWrapper[] targetWrappers
) {
	public MercuryMob build() {
		MobBehaviour mobBehaviour = new MobBehaviour();

		MercuryMob mob = new MercuryMob(
				this.entityType,
				this.name,
				this.components,
				mobBehaviour
		);

		for (@NotNull GoalWrapper goalWrapper : goalWrappers) {
			mobBehaviour.addGoalSelector(goalWrapper.get(mob));
		}

		for (@NotNull TargetWrapper targetWrapper : targetWrappers) {
			mobBehaviour.addTargetSelector(targetWrapper.get(mob));
		}

		return mob;
	}
}
