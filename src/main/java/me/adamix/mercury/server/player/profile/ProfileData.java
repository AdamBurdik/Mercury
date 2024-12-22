package me.adamix.mercury.server.player.profile;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.player.inventory.MercuryPlayerInventory;
import me.adamix.mercury.server.player.profile.quest.ProfileQuests;
import me.adamix.mercury.server.player.stats.Statistics;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents data class to store player profile data which is stored in database
 *
 */
@Getter
public class ProfileData {
	private final @NotNull UUID playerUniqueId;
	private final @NotNull UUID profileUniqueId;
	@Setter private @NotNull String translationId;
	private int health;
	@Setter private int maxHealth;
	@Setter private float movementSpeed;
	@Setter private float attackSpeed;
	private final @NotNull MercuryPlayerInventory playerInventory;
	private final @NotNull Statistics statistics;
	private final @NotNull ProfileQuests profileQuests;

	/**
	 * Constructs a new ProfileData instance all the required fields
	 *
	 * @param profileUniqueId the unique ID of the player profile
	 * @param health          the current health of the player
	 * @param maxHeath       the maximum health of the player
	 */
	public ProfileData(
			@NotNull UUID playerUniqueId,
			@NotNull UUID profileUniqueId,
			@NotNull String translationId,
			int health,
			int maxHeath,
			float movementSpeed,
			float attackSpeed,
			@NotNull MercuryPlayerInventory playerInventory,
			@NotNull Statistics statistics,
			@NotNull ProfileQuests profileQuests
	) {
		this.playerUniqueId = playerUniqueId;
		this.profileUniqueId = profileUniqueId;
		this.translationId = translationId;
		this.health = health;
		this.maxHealth = maxHeath;
		this.movementSpeed = movementSpeed;
		this.attackSpeed = attackSpeed;
		this.playerInventory = playerInventory;
		this.statistics = statistics;
		this.profileQuests = profileQuests;
	}

	/**
	 * Sets the current health of player
	 * Health cannot be above maxHealth or below zero
	 * @param health
	 */
	public void setHealth(int health) {
		this.health = Math.max(0, Math.min(health, maxHealth));
	}

	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("profileUniqueId", this.profileUniqueId.toString());
		map.put("playerUniqueId", this.playerUniqueId.toString());
		map.put("translationId", this.translationId);
		map.put("health", this.health);
		map.put("maxHealth", this.maxHealth);
		map.put("movementSpeed", this.movementSpeed);
		map.put("attackSpeed", this.attackSpeed);
		map.put("playerInventory", this.playerInventory.serialize());
		map.put("statistics", this.statistics.serialize());
		map.put("quests", this.profileQuests.serialize());

		return map;
	}

	@SuppressWarnings("unchecked")
	public static @NotNull ProfileData deserialize(Document document) {
		String playerStringUniqueId = document.getString("playerUniqueId");
		if (!document.containsKey("playerUniqueId")) {
			throw new RuntimeException("Player profile does not include player uuid!");
		}
		UUID playerUniqueId = UUID.fromString(playerStringUniqueId);

		String profileStringUniqueId = document.getString("profileUniqueId");
		if (!document.containsKey("profileUniqueId")) {
			throw new RuntimeException("Player profile with uuid " + playerStringUniqueId + " does not include player uuid!");
		}
		UUID profileUniqueId = UUID.fromString(profileStringUniqueId);

		String translationId = document.containsKey("translationId") ? document.getString("translationId") : PlayerDefaults.getTranslationId();
		int health = document.containsKey("health") ? document.getInteger("health") : PlayerDefaults.getHealth();
		int maxHealth = document.containsKey("maxHealth") ? document.getInteger("maxHealth") : PlayerDefaults.getMaxHealth();
		float movementSpeed = document.containsKey("movementSpeed") ? document.getDouble("movementSpeed").floatValue() : PlayerDefaults.getMovementSpeed();
		float attackSpeed = document.containsKey("attack") ? document.getDouble("attack").floatValue() : PlayerDefaults.getAttackSpeed();
		Object inventoryObject = document.get("inventory");
		MercuryPlayerInventory inventory;
		if (inventoryObject != null) {
			inventory = MercuryPlayerInventory.deserialize((Map<String, Object>) inventoryObject);
		} else {
			inventory = new MercuryPlayerInventory();
		}
		Object statisticsObject = document.get("statistics");
		Statistics profileStatistics;
		if (statisticsObject != null) {
			profileStatistics = Statistics.deserialize((Map<String, Object>) statisticsObject);
		} else {
			profileStatistics = new Statistics();
		}
		Object questsObject = document.get("quests");
		ProfileQuests quests;
		if (questsObject != null) {
			quests = ProfileQuests.deserialize((Map<String, Object>) questsObject);
		} else {
			quests = new ProfileQuests(null, new ArrayList<>());
		}

		return new ProfileData(
				playerUniqueId,
				profileUniqueId,
				translationId,
				health,
				maxHealth,
				movementSpeed,
				attackSpeed,
				inventory,
				profileStatistics,
				quests
		);
	}
}
