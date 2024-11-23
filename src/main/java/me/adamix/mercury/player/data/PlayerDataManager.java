package me.adamix.mercury.player.data;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDataManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerDataManager.class);
	private final MongoCollection<Document> playerDataCollection;

	public PlayerDataManager(MongoDatabase playerDatabase) {
		this.playerDataCollection = playerDatabase.getCollection("player_data");
	}

	/**
	 * Retrieves {@link PlayerData} from database using player ID
	 *
	 * @param playerUniqueId unique ID of player
	 * @return the {@link PlayerData} containing player data
	 */
	public @Nullable CompletableFuture<PlayerData> getPlayerData(UUID playerUniqueId) {
		return CompletableFuture.supplyAsync(() -> {
			FindIterable<Document> playerDocumentIterable = playerDataCollection.find(Filters.eq("playerUniqueId", playerUniqueId.toString()));
			try (MongoCursor<Document> cursor = playerDocumentIterable.cursor()) {
				if (cursor.available() < 0) {
					return null;
				}
				else if (cursor.available() > 1) {
					LOGGER.warn("Player data collection contains more than 1 value for player unique id {}!", playerUniqueId);
				}

				Document document = playerDocumentIterable.first();
				if (document == null) {
					return null;
				}

				return extractProfileData(document);
			}
		});
	}

	private PlayerData extractProfileData(Document document) {
		// ToDo Change hardcoded default values
		// Maybe add helper method to get default values?
		String playerStringUniqueId = document.getString("playerUniqueId");
		if (!document.containsKey("playerUniqueId")) {
			throw new RuntimeException("Player data does not include player uuid!");
		}
		UUID playerUniqueId = UUID.fromString(playerStringUniqueId);

		return new PlayerData(playerUniqueId);
	}

	/**
	 * Save player data to database
	 *
	 * @param playerData player data to save
	 */
	public void savePlayerData(PlayerData playerData) {
		UUID playerUniqueId = playerData.getPlayerUniqueId();

		CompletableFuture.runAsync(() -> {
			Document playerDocument = new Document()
					.append("playerUniqueId", playerData.getPlayerUniqueId().toString());

			playerDataCollection.replaceOne(Filters.eq("playerUniqueId", playerUniqueId.toString()), playerDocument, new ReplaceOptions().upsert(true));
		});
	}
}
