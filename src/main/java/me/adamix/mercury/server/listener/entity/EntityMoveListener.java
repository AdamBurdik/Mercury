package me.adamix.mercury.server.listener.entity;

import me.adamix.mercury.server.event.EntityMoveEvent;
import me.adamix.mercury.server.utils.EntityCollisionUtils;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;

public class EntityMoveListener implements EventListener<EntityMoveEvent> {
	@Override
	public @NotNull Result run(@NotNull EntityMoveEvent event) {
		Vec collision = EntityCollisionUtils.calculateEntityCollisions(event.getEntity());

		event.getEntity().teleport(event.getPosition().add(collision));

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<EntityMoveEvent> eventType() {
		return EntityMoveEvent.class;
	}
}
