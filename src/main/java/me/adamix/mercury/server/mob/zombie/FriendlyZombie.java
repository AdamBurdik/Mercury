package me.adamix.mercury.server.mob.zombie;

import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.mob.core.attribute.MobAttribute;
import me.adamix.mercury.server.mob.core.attribute.MobAttributes;
import me.adamix.mercury.server.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.server.mob.core.goal.FollowEntityGoal;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;

import java.util.List;

public class FriendlyZombie extends MercuryMob {
	public FriendlyZombie() {
		super(EntityType.ZOMBIE,
				"<dark_green><translation:entity.zombie.friendly>",
				new MobAttributes()
						.set(MobAttribute.MOVEMENT_SPEED, 0.21f),
				new Behaviour()
		);
	}

	private static class Behaviour extends MobBehaviour {

		@Override
		public void init(EntityCreature entity) {
			addTargetSelector(
					new ClosestEntityTarget(entity, 32, e -> e instanceof Player)
			);
			addGoalSelectors(List.of(
					new FollowEntityGoal(entity, 3),
					new RandomStrollGoal(entity, 5)
			));
		}
	}
}
