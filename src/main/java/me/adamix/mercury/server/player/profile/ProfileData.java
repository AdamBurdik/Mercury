package me.adamix.mercury.server.player.profile;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.player.attribute.PlayerAttributes;
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
	@Setter private @NotNull String translationId;
	private final @NotNull PlayerAttributes attributes;
	private final @NotNull MercuryPlayerInventory playerInventory;
	private final @NotNull Statistics statistics;
	private final @NotNull PlayerQuests playerQuests;

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
			@NotNull PlayerAttributes attributes,
			@NotNull MercuryPlayerInventory playerInventory,
			@NotNull Statistics statistics,
			@NotNull PlayerQuests playerQuests
	) {
		this.playerUniqueId = playerUniqueId;
		this.profileUniqueId = profileUniqueId;
		this.translationId = translationId;
		this.attributes = attributes;
		this.playerInventory = playerInventory;
		this.statistics = statistics;
		this.playerQuests = playerQuests;
	}

	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("profileUniqueId", this.profileUniqueId.toString());
		map.put("playerUniqueId", this.playerUniqueId.toString());
		map.put("translationId", this.translationId);
		map.put("attributes", this.attributes.serialize());
		map.put("inventory", this.playerInventory.serialize());
		map.put("statistics", this.statistics.serialize());
		map.put("quests", this.playerQuests.serialize());

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
		Object attributesObject = document.get("attributes");
		PlayerAttributes attributes;
		if (attributesObject != null) {
			attributes = PlayerAttributes.deserialize(((Map<String, Object>) attributesObject));
		} else {
			attributes = new PlayerAttributes();
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

		return new ProfileData(
				playerUniqueId,
				profileUniqueId,
				translationId,
				attributes,
				inventory,
				profileStatistics,
				quests
		);
	}
}
