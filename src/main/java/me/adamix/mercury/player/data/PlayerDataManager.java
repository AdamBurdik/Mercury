package me.adamix.mercury.player.data;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.adamix.mercury.player.inventory.GamePlayerInventory;
import me.adamix.mercury.serialization.GamePlayerInventorySerializer;
import net.minestom.server.MinecraftServer;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PlayerDataManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerDataManager.class);
	private final MongoDatabase playerDataDatabase;
	private final MongoCollection<Document> profileDataCollection;

	public PlayerDataManager(MongoClient mongoClient) {
		this.playerDataDatabase = mongoClient.getDatabase("PlayerData");
		this.profileDataCollection = playerDataDatabase.getCollection("profile_data");
	}

	/**
	 * Retrieves PlayerData from database using profile ID
	 *
	 * @param profileUniqueId unique ID of player profile
	 * @return the PlayerData containing player profile data
	 */
	public @Nullable CompletableFuture<PlayerData> getPlayerData(UUID profileUniqueId) {
		return CompletableFuture.supplyAsync(() -> {
			FindIterable<Document> playerDocumentIterable = profileDataCollection.find(Filters.eq("profileUniqueId", profileUniqueId.toString()));
			try (MongoCursor<Document> cursor = playerDocumentIterable.cursor()) {
				if (cursor.available() < 0) {
					return null;
				}
				else if (cursor.available() > 1) {
					LOGGER.warn("Player Data Collection contains more than 1 value for profile unique id {}!", profileUniqueId);
				}

				Document document = playerDocumentIterable.first();
				if (document == null) {
					return null;
				}

				return extractPlayerData(document);
			}
		});
	}

	private PlayerData extractPlayerData(Document document) {
		// ToDo Change hardcoded default values
		// Maybe add helper method to get default values?
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

		String translationId = document.containsKey("translationId") ? document.getString("translationId") : "en";
		int health = document.containsKey("health") ? document.getInteger("health") : 100;
		int maxHealth = document.containsKey("maxHealth") ? document.getInteger("maxHealth") : 100;
		float movementSpeed = document.containsKey("movementSpeed") ? document.getDouble("movementSpeed").floatValue() : 0.1f;
		float attackSpeed = document.containsKey("attack") ? document.getDouble("attack").floatValue() : 0.1f;
		GamePlayerInventory inventory = GamePlayerInventorySerializer.deserialize(document);

		return new PlayerData(
				playerUniqueId,
				profileUniqueId,
				translationId,
				health,
				maxHealth,
				movementSpeed,
				attackSpeed,
				inventory
		);
	}

	/**
	 * Save player data to database
	 *
	 * @param playerData player data
	 */
	public void savePlayerData(PlayerData playerData) {
		UUID profileUniqueId = playerData.getProfileUniqueId();

		CompletableFuture.runAsync(() -> {
			Document playerDocument = new Document()
					.append("profileUniqueId", profileUniqueId.toString())
					.append("playerUniqueId", playerData.getPlayerUniqueId().toString())
					.append("translationId", playerData.getTranslationId())
					.append("health", playerData.getHealth())
					.append("maxHealth", playerData.getMaxHealth())
					.append("movementSpeed", playerData.getMovementSpeed())
					.append("attackSpeed", playerData.getAttackSpeed())
					.append("inventory", GamePlayerInventorySerializer.serialize(playerData.getPlayerInventory()));

			profileDataCollection.replaceOne(Filters.eq("profileUniqueId", profileUniqueId.toString()), playerDocument, new ReplaceOptions().upsert(true));
		});
	}

	/**
	 * Retrieves PlayerData list containing all player profile data from database
	 *
	 * @param playerUniqueId - unique ID of player profile
	 * @return list of PlayerData
	 */
	public @Nullable CompletableFuture<List<PlayerData>> getPlayerDataList(UUID playerUniqueId) {
		return CompletableFuture.supplyAsync(() -> {
			FindIterable<Document> playerDocumentIterable = profileDataCollection.find(Filters.eq("playerUniqueId", playerUniqueId.toString()));
			try (MongoCursor<Document> cursor = playerDocumentIterable.cursor()) {
				List<PlayerData> playerDataList = new ArrayList<>();

				while (cursor.hasNext()) {
					Document document = cursor.next();
					playerDataList.add(
							extractPlayerData(document)
					);
				}

				return playerDataList;
			}
		});
	}

	public void getPlayerDataListSync(UUID playerUniqueId, Consumer<List<PlayerData>> consumer) {
		MinecraftServer.getSchedulerManager().buildTask(() -> {
			getPlayerDataList(playerUniqueId).thenAccept(consumer);
		}).schedule();
	}

}
