package me.adamix.mercury.server.player.profile;

import lombok.Getter;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.player.attribute.PlayerAttributeContainer;
import me.adamix.mercury.server.player.inventory.MercuryPlayerInventory;
import me.adamix.mercury.server.player.quest.PlayerQuests;
import me.adamix.mercury.server.player.stats.Statistics;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
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
	private final @NotNull PlayerAttributeContainer attributes;
	private final @NotNull MercuryPlayerInventory playerInventory;
	private final @NotNull Statistics statistics;
	private final @NotNull PlayerQuests playerQuests;
	private final long health;
	private final int level;
	private final long experience;

	/**
	 * Constructs a new ProfileData instance all the required fields
	 *
	 * @param playerUniqueId uuid of player
	 * @param profileUniqueId uuid of profile
	 * @param attributes player attribute object
	 * @param playerInventory player inventory object
	 * @param statistics player statistics
	 * @param playerQuests player quests
	 * @param level current player level
	 * @param experience current player experience
	 */
	public ProfileData(
			@NotNull UUID playerUniqueId,
			@NotNull UUID profileUniqueId,
			@NotNull PlayerAttributeContainer attributes,
			@NotNull MercuryPlayerInventory playerInventory,
			@NotNull Statistics statistics,
			@NotNull PlayerQuests playerQuests, long health,
			int level,
			long experience
	) {
		this.playerUniqueId = playerUniqueId;
		this.profileUniqueId = profileUniqueId;
		this.attributes = attributes;
		this.playerInventory = playerInventory;
		this.statistics = statistics;
		this.playerQuests = playerQuests;
		this.health = health;
		this.level = level;
		this.experience = experience;
	}

	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("profileUniqueId", this.profileUniqueId.toString());
		map.put("playerUniqueId", this.playerUniqueId.toString());
		map.put("attributes", this.attributes.serialize());
		map.put("inventory", this.playerInventory.serialize());
		map.put("statistics", this.statistics.serialize());
		map.put("quests", this.playerQuests.serialize());
		map.put("level", this.level);
		map.put("experience", this.experience);
		map.put("health", this.health);

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

		Object attributesObject = document.get("attributes");
		PlayerAttributeContainer attributes;
		if (attributesObject != null) {
			attributes = PlayerAttributeContainer.deserialize(((Map<String, Object>) attributesObject));
		} else {
			attributes = new PlayerAttributeContainer();
		}
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
		PlayerQuests quests;
		if (questsObject != null) {
			quests = PlayerQuests.deserialize((Map<String, Object>) questsObject);
		} else {
			quests = new PlayerQuests(new HashSet<>(), new HashSet<>());
		}

		Integer levelInteger = document.getInteger("level");
		int level = levelInteger != null ? levelInteger : 0;

		Long experienceLong = document.getLong("experience");
		long experience = experienceLong != null ? experienceLong : 0;

		Long healthLong = document.getLong("health");
		long health = healthLong != null ? experience : PlayerDefaults.getHealth();

		return new ProfileData(
				playerUniqueId,
				profileUniqueId,
				attributes,
				inventory,
				profileStatistics,
				quests,
				health,
				level,
				experience
		);
	}
}
