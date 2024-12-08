package me.adamix.mercury.server.player.profile;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.player.inventory.GamePlayerInventory;
import me.adamix.mercury.server.player.stats.Statistics;
import me.adamix.mercury.server.serialization.GamePlayerInventorySerializer;
import net.minestom.server.MinecraftServer;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ProfileDataManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileDataManager.class);
	private final MongoCollection<Document> profileDataCollection;

	public ProfileDataManager(MongoDatabase playerDatabase) {
		this.profileDataCollection = playerDatabase.getCollection("profile_data");
	}

	/**
	 * Retrieves ProfileData from database using profile ID
	 *
	 * @param profileUniqueId unique ID of player profile
	 * @return the ProfileData containing player profile data
	 */
	public @Nullable CompletableFuture<ProfileData> getProfileData(UUID profileUniqueId) {
		return CompletableFuture.supplyAsync(() -> {
			FindIterable<Document> playerDocumentIterable = profileDataCollection.find(Filters.eq("profileUniqueId", profileUniqueId.toString()));
			try (MongoCursor<Document> cursor = playerDocumentIterable.cursor()) {
				if (cursor.available() < 0) {
					return null;
				}
				else if (cursor.available() > 1) {
					LOGGER.warn("Player profile data collection contains more than 1 value for profile unique id {}!", profileUniqueId);
				}

				Document document = playerDocumentIterable.first();
				if (document == null) {
					return null;
				}

				return extractProfileData(document);
			}
		});
	}

	public void getProfileDataSync(UUID profileUniqueId, Consumer<ProfileData> consumer) {
		MinecraftServer.getSchedulerManager().buildTask(() -> {
			CompletableFuture<ProfileData> profileData = getProfileData(profileUniqueId);
			if (profileData == null) {
				consumer.accept(null);
			} else {
				profileData.thenAccept(consumer);
			}

		}).schedule();
	}

	private ProfileData extractProfileData(Document document) {
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
		GamePlayerInventory inventory = GamePlayerInventorySerializer.deserialize(document);

		Map<String, Object> statisticMap = document.containsKey("statistics") ? document.get("statistics", Document.class) : new HashMap<>();
		Statistics profileStatistics = new Statistics();

		statisticMap.forEach((key, value) -> {
			if (value instanceof Float floatValue) {
				profileStatistics.set(key, floatValue);
			}
		});

		return new ProfileData(
				playerUniqueId,
				profileUniqueId,
				translationId,
				health,
				maxHealth,
				movementSpeed,
				attackSpeed,
				inventory,
				profileStatistics
		);
	}

	/**
	 * Save player data to database
	 *
	 * @param profileData player data
	 */
	public void saveProfileData(ProfileData profileData) {
		UUID profileUniqueId = profileData.getProfileUniqueId();

		CompletableFuture.runAsync(() -> {
			Document playerDocument = new Document()
					.append("profileUniqueId", profileUniqueId.toString())
					.append("playerUniqueId", profileData.getPlayerUniqueId().toString())
					.append("translationId", profileData.getTranslationId())
					.append("health", profileData.getHealth())
					.append("maxHealth", profileData.getMaxHealth())
					.append("movementSpeed", profileData.getMovementSpeed())
					.append("attackSpeed", profileData.getAttackSpeed())
					.append("inventory", GamePlayerInventorySerializer.serialize(profileData.getPlayerInventory()))
					.append("statistics", profileData.getStatistics().serialize()
			);

			profileDataCollection.replaceOne(Filters.eq("profileUniqueId", profileUniqueId.toString()), playerDocument, new ReplaceOptions().upsert(true));
		});
	}

	/**
	 * Retrieves ProfileData list containing all player profile data from database
	 *
	 * @param playerUniqueId - unique ID of player profile
	 * @return list of ProfileData
	 */
	public @Nullable CompletableFuture<List<ProfileData>> getProfileDataList(UUID playerUniqueId) {
		return CompletableFuture.supplyAsync(() -> {
			FindIterable<Document> playerDocumentIterable = profileDataCollection.find(Filters.eq("playerUniqueId", playerUniqueId.toString()));
			try (MongoCursor<Document> cursor = playerDocumentIterable.cursor()) {
				List<ProfileData> profileDataList = new ArrayList<>();

				while (cursor.hasNext()) {
					Document document = cursor.next();
					profileDataList.add(
							extractProfileData(document)
					);
				}

				return profileDataList;
			}
		});
	}

	public void getProfileDataListSync(UUID playerUniqueId, Consumer<List<ProfileData>> consumer) {
		MinecraftServer.getSchedulerManager().buildTask(() -> {
			CompletableFuture<List<ProfileData>> profileDataList = getProfileDataList(playerUniqueId);
			if (profileDataList == null) {
				consumer.accept(null);
			} else {
				profileDataList.thenAccept(consumer);
			}
		}).schedule();
	}

}
