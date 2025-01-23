package me.adamix.mercury.server.mob.core;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.mob.core.attribute.MobAttributeContainer;
import me.adamix.mercury.server.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.server.mob.core.blueprint.MercuryMobBlueprint;
import me.adamix.mercury.server.mob.core.component.MercuryMobComponent;
import me.adamix.mercury.server.mob.core.component.MobAttributeComponent;
import me.adamix.mercury.server.mob.core.register.ClassRegisteredMob;
import me.adamix.mercury.server.mob.core.register.ConfigRegisteredMob;
import me.adamix.mercury.server.mob.core.register.RegisteredMob;
import me.adamix.mercury.server.mob.core.wrapper.AIWrapperManager;
import me.adamix.mercury.server.mob.core.wrapper.goal.GoalWrapper;
import me.adamix.mercury.server.mob.core.wrapper.target.TargetWrapper;
import me.adamix.mercury.server.toml.MercuryConfiguration;
import me.adamix.mercury.server.utils.FileUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.io.File;
import java.util.*;

/**
 * Manager used to register, spawn and retrieve {@link MercuryMob game mobs} in the server
 */
public class MobManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MobManager.class);
	private final Map<NamespaceID, RegisteredMob> registeredMobs = new HashMap<>();
	private final Map<UUID, MercuryMob> gameMobs = new HashMap<>();

	public void registerAllMobs() {
		registeredMobs.clear();
		File itemDirectory = new File("resources/mobs/");
		List<File> fileList = FileUtils.getAllFiles(itemDirectory);

		for (File file : fileList) {
			String extension = FileUtils.getExtension(file);
			if (!extension.equals("toml")) {
				continue;
			}

			this.register(file);
		}
	}

	/**
	 * Registers a mob with unique ID from toml configuration
	 * @param tomlFile toml file containing mob configuration
	 */
	// ToDo Make this method shorter
	public void register(File tomlFile) {
		if (!tomlFile.exists()) {
			throw new RuntimeException("Unable to register item! File does not exist");
		}

		MercuryConfiguration toml = new MercuryConfiguration(tomlFile);

		@NotNull NamespaceID namespaceID = toml.getNamespacedIDSafe("id");
		@NotNull EntityType entityType = toml.getEntityTypeSafe("type");
		@NotNull String name = toml.getStringSafe("name");
		long health = toml.getLongSafe("health");

		List<MercuryMobComponent> componentList = new ArrayList<>();

		// Parse attributes to component
		TomlTable attributeTable = toml.getTomlTable("attributes");
		if (attributeTable != null) {
			MobAttributeContainer mobAttributeContainer = new MobAttributeContainer();
			mobAttributeContainer
					.set(MercuryAttribute.DAMAGE, attributeTable.getDouble("damage"))
					.set(MercuryAttribute.MOVEMENT_SPEED, attributeTable.getDouble("movement_speed"))
					.set(MercuryAttribute.ATTACK_SPEED, attributeTable.getDouble("attack_speed"))
					.set(MercuryAttribute.MAX_HEALTH, attributeTable.getDouble("max_health"));
			componentList.add(
					mobAttributeContainer.toComponent()
			);
		}

		// Parse ai goals
		AIWrapperManager wrapperManager = Server.getAiWrapperManager();
		List<GoalWrapper> goalWrapperList = new ArrayList<>();

		TomlArray goalArray = toml.getTomlArray("ai.goals");
		if (goalArray != null) {
			for (int i = 0; i < goalArray.size(); i++) {
				TomlTable goalTable = goalArray.getTable(i);

				if (!goalTable.contains("type")) {
					LOGGER.error("Goal table must contain type value! Skipping this goal in {}", tomlFile.getName());
					continue;
				}

				String goalType = goalTable.getString("type");
				if (goalType == null) {
					LOGGER.error("Goal table must contain type value as string! Skipping this goal in {}", tomlFile.getName());
					continue;
				}

				GoalWrapper goalWrapper = wrapperManager.getGoalWrapper(goalType);
				if (goalWrapper == null) {
					LOGGER.error("Goal with name '{}' is not registered! Skipping this goal in {}", goalType, tomlFile.getName());
					continue;
				}

				goalWrapper.parse(goalTable);
				goalWrapperList.add(goalWrapper);
			}
		}

		// Parse ai targets
		List<TargetWrapper> targetWrapperList = new ArrayList<>();
		TomlArray targetArray = toml.getTomlArray("ai.targets");
		if (targetArray != null) {
			for (int i = 0; i < targetArray.size(); i++) {
				TomlTable targetTable = targetArray.getTable(i);

				if (!targetTable.contains("type")) {
					LOGGER.error("Target table must contain type value! Skipping this target in {}", tomlFile.getName());
					continue;
				}

				String targetType = targetTable.getString("type");
				if (targetType == null) {
					LOGGER.error("Target table must contain type value as string! Skipping this target in {}", tomlFile.getName());
					continue;
				}

				TargetWrapper targetWrapper = wrapperManager.getTargetWrapper(targetType);
				if (targetWrapper == null) {
					LOGGER.error("Target with name '{}' is not registered! Skipping this target in {}", targetType, tomlFile.getName());
					continue;
				}

				targetWrapper.parse(targetTable);
				targetWrapperList.add(targetWrapper);
			}
		}

		MercuryMobBlueprint mobBlueprint = new MercuryMobBlueprint(
				namespaceID,
				entityType,
				name,
				componentList.toArray(new MercuryMobComponent[0]),
				goalWrapperList.toArray(new GoalWrapper[0]),
				targetWrapperList.toArray(new TargetWrapper[0]),
				health
		);
		this.register(namespaceID, new ConfigRegisteredMob(mobBlueprint));

	}

	/**
	 * Registers a mob with unique ID from class
	 * @param mobID unique ID for mob
	 * @param clazz class that extends {@link MercuryMob} representing the mob to register
	 */
	public void register(NamespaceID mobID, Class<? extends MercuryMob> clazz) {
		this.register(mobID, new ClassRegisteredMob(clazz));
	}

	/**
	 * Register a mob with unique ID
	 * @param mobID unique ID for mob
	 * @param registeredMob mob to register
	 */
	public void register(NamespaceID mobID, RegisteredMob registeredMob) {
		registeredMobs.put(mobID, registeredMob);
		LOGGER.info("Entity '{}' has been registered", mobID.asString());
	}

	/**
	 * Spawns an existing mercury mob to instance at given position
	 * Mob will be spawned with its vanilla attributes, meta and behaviour if available
	 * @param mob mob to spawn
	 * @param instance instance to spawn a mob
	 * @param position position where to spawn a mob
	 * @throws RuntimeException if there is an error during mob instantiation or behaviour initialization
	 */
	public void spawn(@NotNull MercuryMob mob, Instance instance, Pos position) {
		// Apply attributes to mob
		MobAttributeComponent mobAttributeComponent = mob.getComponent(MobAttributeComponent.class);
		if (mobAttributeComponent != null) {
			mobAttributeComponent.applyToMob(mob);
		}

		// Apply meta to mob
		EntityMeta meta = mob.getEntityMeta();
		meta.setCustomNameVisible(true);
		meta.setCustomName(MiniMessage.miniMessage().deserialize(mob.getName()));

		this.gameMobs.put(mob.getUuid(), mob);
		mob.setInstance(instance, position);
		mob.updateName();

		// Apply behaviour to mob
		MobBehaviour behaviour = mob.getBehaviour();
		if (behaviour != null) {
			behaviour.init(mob);
			behaviour.onSpawn(instance, position);
			mob.applyBehaviour();
		}
	}

	/**
	 * Initialize a mercury mob and spawn it to instance at given position
	 * Mob will be spawned with its vanilla attributes, meta and behaviour if available
	 * @param mobID namespace id of mob to spawn
	 * @param instance instance to spawn a mob
	 * @param position position where to spawn a mob
	 * @throws RuntimeException if the mob with the specified namespaceID is not registered or if there is an error during mob instantiation or behaviour initialization
	 */
	public void spawn(Instance instance, Pos position, @NotNull NamespaceID mobID) {
		if (!this.contains(mobID)) {
			throw new RuntimeException("Mob with namespaceID " + mobID + " does not exist.");
		}

		MercuryMob mob = registeredMobs.get(mobID).get();
		this.spawn(mob, instance, position);
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
	 * Retrieves a mercury mob by its unique UUID
	 *
	 * @param uuid unique identifier of the mob
	 * @return {@link MercuryMob} associated with the specified UUID, or {@code null} if not found
	 */
	public @Nullable MercuryMob get(UUID uuid) {
		return gameMobs.get(uuid);
	}

	/**
	 * Retrives a registered mercury mob by its namespace ID
	 * @param mobID namespace ID of mob
	 * @return {@link MercuryMob} mercury mob, or {@code null} if not there is no mob with specified ID
	 */
	public @Nullable MercuryMob getRegistered(NamespaceID mobID) {
		RegisteredMob registeredMob = registeredMobs.get(mobID);
		if (registeredMob == null) {
			return null;
		}
		return registeredMob.get();
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
