package me.adamix.mercury.server.event;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class EntityMoveEvent implements EntityEvent {
	private final @NotNull Entity entity;
	private final @NotNull Pos position;

	public EntityMoveEvent(@NotNull Entity entity, @NotNull Pos position) {
		this.entity = entity;
		this.position = position;
	}

	@Override
	public @NotNull Entity getEntity() {
		return this.entity;
	}

	public @NotNull Pos getPosition() {
		return this.position;
	}
}
