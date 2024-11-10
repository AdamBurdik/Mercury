package me.adamix.mercury.mob.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MobManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MobManager.class);
	private final Map<String, GameMob> entityMap = new HashMap<>();

	public void register(String id, GameMob entity) {
		entityMap.put(id, entity);
		LOGGER.info("Entity {} has been registered", id);
	}

	public @Nullable GameMob get(String id) {
		return entityMap.get(id);
	}

	public @NotNull Set<String> getEntityIdCollection() {
		return entityMap.keySet();
	}
}
