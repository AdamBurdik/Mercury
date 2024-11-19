package me.adamix.mercury.mob.zombie;

import me.adamix.mercury.mob.core.GameMob;
import me.adamix.mercury.mob.core.attribute.MobAttribute;
import me.adamix.mercury.mob.core.attribute.MobAttributes;
import me.adamix.mercury.mob.core.behaviour.MobBehaviour;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget;
import net.minestom.server.utils.time.TimeUnit;

import java.util.List;

public class RogueZombie extends GameMob {
	public RogueZombie() {
		super(EntityType.ZOMBIE,
				"<dark_green><translation:entity.zombie.rogue>",
				new MobAttributes()
						.set(MobAttribute.MOVEMENT_SPEED, 0.21f),
				new Behaviour()

		);
	}

	private static class Behaviour extends MobBehaviour {

		@Override
		public void init(EntityCreature entity) {
			addTargetSelectors(List.of(
					new LastEntityDamagerTarget(entity, 32),
					new ClosestEntityTarget(entity, 32, e -> e instanceof Player player && player.getGameMode() == GameMode.SURVIVAL)
			));
			addGoalSelectors(List.of(
					new MeleeAttackGoal(entity, 1.6, 20, TimeUnit.SERVER_TICK),
					new RandomStrollGoal(entity, 5)
			));
		}
	}
}
