package me.adamix.mercury.mob.core;

import me.adamix.mercury.exception.mob.GameMobNotFoundException;
import me.adamix.mercury.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MobManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MobManager.class);
	private final Map<NamespaceID, Class<? extends GameMob>> registeredMobs = new HashMap<>();
	private final Map<UUID, GameMob> gameMobs = new HashMap<>();

	public void register(NamespaceID namespaceID, Class<? extends GameMob> clazz) {
		registeredMobs.put(namespaceID, clazz);
		LOGGER.info("Entity {} has been registered", namespaceID.asString());
	}

	public @NotNull GameMob spawn(NamespaceID namespaceID, Instance instance, Pos position) {
		if (!this.contains(namespaceID)) {
			throw new GameMobNotFoundException("Game Mob with namespaceID " + namespaceID + " does not exist.");
		}

		Class<? extends GameMob> clazz = registeredMobs.get(namespaceID);
		try {
			Constructor<? extends GameMob> constructor = clazz.getConstructor();
			GameMob mob = constructor.newInstance();
			mob.applyVanillaAttributes();
			mob.applyBehaviour();

			MobBehaviour behaviour = mob.getBehaviour();
			if (behaviour != null) {
				mob.getBehaviour().onSpawn(instance, position);
			}

			this.gameMobs.put(mob.getUuid(), mob);
			return mob;

		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean contains(NamespaceID namespaceID) {
		return registeredMobs.containsKey(namespaceID);
	}

	public @Nullable GameMob get(UUID uuid) {
		return gameMobs.get(uuid);
	}

	public @NotNull Set<NamespaceID> getEntityIdCollection() {
		return registeredMobs.keySet();
	}
}
