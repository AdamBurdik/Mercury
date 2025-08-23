package me.adamix.mercury.core.entity;

import me.adamix.mercury.core.entity.builder.EntityBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EntityManager {
	private final Map<UUID, MercuryEntity> entityMap = new HashMap<>();

	public void register(@NotNull MercuryEntity entity) {
		System.out.println("Registering entity: " + entity);
		entityMap.put(entity.getBukkitEntity().getUniqueId(), entity);
	}

	public @NotNull Optional<MercuryEntity> get(@NotNull UUID uuid) {
		System.out.println("Getting entity: " + uuid);
		return Optional.ofNullable(entityMap.get(uuid));
	}

	public @NotNull EntityBuilder builder() {
		return new EntityBuilder(this);
	}
}
