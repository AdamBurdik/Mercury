package me.adamix.mercury.server.mob.core;

import me.adamix.mercury.server.mob.core.behaviour.MobBehaviour;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.metadata.EntityMeta;
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

/**
 * Manager used to register, spawn and retrieve {@link MercuryMob game mobs} in the server
 */
public class MobManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MobManager.class);
	private final Map<NamespaceID, Class<? extends MercuryMob>> registeredMobs = new HashMap<>();
	private final Map<UUID, MercuryMob> gameMobs = new HashMap<>();

	/**
	 * Registers a game mob with unique namespaced ID
	 * @param namespaceID unique ID for mob
	 * @param clazz class that extends {@link MercuryMob} representing the mob to register
	 */
	public void register(NamespaceID namespaceID, Class<? extends MercuryMob> clazz) {
		registeredMobs.put(namespaceID, clazz);
		LOGGER.info("Entity '{}' has been registered", namespaceID.asString());
	}

	/**
	 * Spawns a new game mob in a specific instance at a given position.
	 * The mob will be initialized with its vanilla attributes and behaviour, if defined
	 *
	 * @param namespaceID unique identifier of the mob to spawn
	 * @param instance {@link Instance} in which to spawn the mob
	 * @param position {@link Pos} indicating where to spawn the mob
	 * @return newly spawned {@link MercuryMob}
	 * @throws RuntimeException if the mob with the specified namespaceID is not registered or if there is an error during mob instantiation or behaviour initialization
	 */
	public @NotNull MercuryMob spawn(NamespaceID namespaceID, Instance instance, Pos position) {
		if (!this.contains(namespaceID)) {
			throw new RuntimeException("Game Mob with namespaceID " + namespaceID + " does not exist.");
		}

		Class<? extends MercuryMob> clazz = registeredMobs.get(namespaceID);
		try {
			Constructor<? extends MercuryMob> constructor = clazz.getConstructor();
			MercuryMob mob = constructor.newInstance();
			mob.applyVanillaAttributes();

			// Apply meta to mob
			EntityMeta meta = mob.getEntityMeta();
			meta.setCustomNameVisible(true);

			this.gameMobs.put(mob.getUuid(), mob);
			mob.setInstance(instance, position);

			MobBehaviour behaviour = mob.getBehaviour();
			if (behaviour != null) {
				behaviour.init(mob);
				behaviour.onSpawn(instance, position);
				mob.applyBehaviour();
			}

			return mob;

		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks if a mob is registered under the given namespace ID
	 *
	 * @param namespaceID unique identifier of the mob
	 * @return {@code true} if the mob is registered, {@code false} otherwise
	 */
	public boolean contains(NamespaceID namespaceID) {
		return registeredMobs.containsKey(namespaceID);
	}

	/**
	 * Retrieves a game mob by its unique UUID
	 *
	 * @param uuid unique identifier of the mob
	 * @return {@link MercuryMob} associated with the specified UUID, or {@code null} if not found
	 */
	public @Nullable MercuryMob get(UUID uuid) {
		return gameMobs.get(uuid);
	}

	/**
	 * Retrieves all registered entity namespace IDs
	 *
	 * @return set of all registered {@link NamespaceID} values
	 */
	public @NotNull Set<NamespaceID> getEntityIdCollection() {
		return registeredMobs.keySet();
	}
}
