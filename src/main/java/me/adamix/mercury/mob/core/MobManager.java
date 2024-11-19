package me.adamix.mercury.mob.core;

import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MobManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MobManager.class);
	private final Map<NamespaceID, GameMob> entityMap = new HashMap<>();

	public void register(NamespaceID namespaceID, GameMob entity) {
		entityMap.put(namespaceID, entity);
		LOGGER.info("Entity {} has been registered", namespaceID.asString());
	}

	public @Nullable GameMob get(NamespaceID namespaceID) {
		return entityMap.get(namespaceID);
	}

	public boolean contains(NamespaceID namespaceID) {
		return entityMap.containsKey(namespaceID);
	}

	public @NotNull Set<NamespaceID> getEntityIdCollection() {
		return entityMap.keySet();
	}
}
