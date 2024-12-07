package me.adamix.mercury.server.mob.core.goal;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.pathfinding.Navigator;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;


public class FollowEntityGoal extends GoalSelector {
	private final Cooldown cooldown = new Cooldown(Duration.of(5, TimeUnit.SERVER_TICK));
	private final double range;
	private boolean stop;
	private Entity cachedTarget;

	public FollowEntityGoal(@NotNull EntityCreature entityCreature, double range) {
		super(entityCreature);
		this.range = range;
	}

	@Override
	public boolean shouldStart() {
		this.cachedTarget = findTarget();
		return this.cachedTarget != null;
	}

	@Override
	public void start() {
		final Point targetPosition = this.cachedTarget.getPosition();
		entityCreature.getNavigator().setPathTo(targetPosition);
	}

	@Override
	public void tick(long time) {
		Entity target;
		if (this.cachedTarget != null) {
			target = this.cachedTarget;
			this.cachedTarget = null;
		} else {
			target = findTarget();
		}

		this.stop = target == null;

		if (!stop) {

			// Attack the target entity
			if (entityCreature.getDistanceSquared(target) <= range * range) {
				stop = true;
				return;
			}

			// Move toward the target entity
			Navigator navigator = entityCreature.getNavigator();
			final var pathPosition = navigator.getPathPosition();
			final var targetPosition = target.getPosition();
			if (pathPosition == null || !pathPosition.samePoint(targetPosition)) {
				if (this.cooldown.isReady(time)) {
					this.cooldown.refreshLastUpdate(time);
					navigator.setPathTo(targetPosition);
				}
			}
		}

	}

	@Override
	public boolean shouldEnd() {
		return this.stop;
	}

	@Override
	public void end() {
		entityCreature.getNavigator().setPathTo(null);
	}
}
