package me.adamix.mercury.core.entity.builder;

import me.adamix.mercury.core.entity.EntityManager;
import me.adamix.mercury.core.entity.MercuryEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityBuilder {
	private final @NotNull EntityManager entityManager;
	private @NotNull EntityType entityType = EntityType.ZOMBIE;
	private @Nullable String name;

	private boolean hasAi = true;

	public EntityBuilder(@NotNull EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public @NotNull EntityBuilder name(@NotNull String name) {
		this.name = name;
		return this;
	}

	public @NotNull EntityBuilder type(@NotNull EntityType type) {
		this.entityType = type;
		return this;
	}

	public @NotNull EntityBuilder ai(boolean value) {
		this.hasAi = value;
		return this;
	}

	public @NotNull MercuryEntity spawn(@NotNull World world, @NotNull Location location) {
		Entity bukkitEntity = world.spawn(location, entityType.getEntityClass(), CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
			if (entity instanceof LivingEntity livingEntity) {
				livingEntity.setAI(hasAi);
				livingEntity.setVisibleByDefault(false);
			}
		});


		MercuryEntity entity = new MercuryEntity(bukkitEntity, name == null ? "" : name);
		entityManager.register(entity);

		return entity;
	}
}
