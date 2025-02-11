package me.adamix.mercury.server.player.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.exceptions.PlayerDataNotAvailableException;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.state.PlayerState;
import me.adamix.mercury.server.player.stats.StatisticCategory;
import me.adamix.mercury.server.player.stats.Statistics;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PlayerDataManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerDataManager.class);
	private final MongoCollection<Document> playerDataCollection;
	private final Cache<UUID, PlayerData> playerDataCache;

	public PlayerDataManager(MongoDatabase playerDatabase) {
		this.playerDataCollection = playerDatabase.getCollection("player_data");
		this.playerDataCache = Caffeine.newBuilder()
				.maximumSize(50)
				.expireAfterWrite(30, TimeUnit.MINUTES)
				.build();
	}

	public @Nullable PlayerData getPlayerData(@NotNull UUID playerUniqueId) {
		return playerDataCache.getIfPresent(playerUniqueId);
	}

	public boolean hasPlayerData(@NotNull UUID playerUniqueId) {
		return getPlayerData(playerUniqueId) != null;
	}

	/**
	 * Retrieves {@link PlayerData} from cache, if not present fetches from database
	 *
	 * @param playerUniqueId unique ID of player
	 * @return the {@link PlayerData} containing player data
	 */
	public @Nullable CompletableFuture<PlayerData> fetchPlayerData(UUID playerUniqueId) {
		return CompletableFuture.supplyAsync(() -> {
			PlayerData cachedPlayerData = playerDataCache.getIfPresent(playerUniqueId);
			if (cachedPlayerData != null) {
				return cachedPlayerData;
			}

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

				return PlayerData.deserialize(document);
			}
		}).exceptionally((e -> {
			LOGGER.error(String.valueOf(e));
			throw new RuntimeException(e);
		}));
	}

	/**
	 * Save player data to database and cache
	 *
	 * @param playerData player data to save
	 */
	public void savePlayerData(PlayerData playerData) {
		UUID playerUniqueId = playerData.getPlayerUniqueId();

		CompletableFuture.runAsync(() -> {
			Document playerDocument = new Document()
					.append("playerUniqueId", playerData.getPlayerUniqueId().toString())
					.append("statistics", playerData.getStatistics().serialize());

			playerDataCollection.replaceOne(Filters.eq("playerUniqueId", playerUniqueId.toString()), playerDocument, new ReplaceOptions().upsert(true));
			playerDataCache.put(playerData.getPlayerUniqueId(), playerData);
		}).exceptionally((e -> {
			LOGGER.error(String.valueOf(e));
			throw new RuntimeException(e);
		}));
	}

	/**
	 * Loads player data and sets it to the player instance. <br>
	 * If the data is unavailable, the player will be kicked from the server.
	 *
	 * @param player   the player whose data is being loaded.
	 * @param runnable the function that will be called after the player data is loaded.
	 * @throws PlayerDataNotAvailableException if player data is not available in the database.
	 *                                         The player will be kicked as a result.
	 */
	public void loadPlayerData(@NotNull MercuryPlayer player, @Nullable Runnable runnable) {
		CompletableFuture<PlayerData> completableFuture = Server.getPlayerDataManager().fetchPlayerData(player.getUuid());
		if (completableFuture == null) {
			player.kick("Cannot get player data from database! Please notify admins about this message!");
			throw new PlayerDataNotAvailableException("Cannot get player data of " + player.getUsername() + "!");
		}
		completableFuture.thenAccept(data -> {
			if (data == null) {
				data = new PlayerData(
						player.getUuid(),
						PlayerDefaults.getTranslationId(),
						new Statistics()
				);
				Server.getPlayerDataManager().savePlayerData(data);
			}
			player.setPlayerData(data);
			player.setState(PlayerState.INIT);
			if (runnable != null) {
				runnable.run();
			}
		});
	}
}
