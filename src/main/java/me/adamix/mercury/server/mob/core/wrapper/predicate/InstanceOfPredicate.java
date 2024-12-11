package me.adamix.mercury.server.mob.core.wrapper.predicate;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class InstanceOfPredicate {
	public static @NotNull Predicate<Entity> parse(String value) {
		EntityType entityType = EntityType.fromNamespaceId(NamespaceID.from(value));
		if (entityType == null) {
			return entity -> false;
		}

		return entity -> entity.getEntityType().equals(EntityType.PLAYER);
	}
}
