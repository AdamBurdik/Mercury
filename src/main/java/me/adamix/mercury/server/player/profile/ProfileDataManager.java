package me.adamix.mercury.server.player.profile;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.player.inventory.MercuryPlayerInventory;
import me.adamix.mercury.server.player.profile.quest.ProfileQuests;
import me.adamix.mercury.server.player.stats.Statistics;
import net.minestom.server.MinecraftServer;
import org.bson.Document;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ProfileDataManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileDataManager.class);
	private final MongoCollection<Document> profileDataCollection;
	private final Cache<UUID, ProfileData> profileDataCache;

	public ProfileDataManager(MongoDatabase playerDatabase) {
		this.profileDataCollection = playerDatabase.getCollection("profile_data");
		this.profileDataCache = Caffeine.newBuilder()
				.maximumSize(50)
				.expireAfterWrite(30, TimeUnit.MINUTES)
				.build();
	}

	/**
	 * Retrieves {@link ProfileData} from database or cache using profile ID
	 *
	 * @param profileUniqueId unique ID of player profile
	 * @return the {@link ProfileData} containing player profile data
	 */
	public @Nullable CompletableFuture<ProfileData> getProfileData(UUID profileUniqueId) {
		return CompletableFuture.supplyAsync(() -> {
			ProfileData cachedData = profileDataCache.getIfPresent(profileUniqueId);
			if (cachedData != null) {
				return cachedData;
			}

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

				return ProfileData.deserialize(document);
			}
		}).exceptionally((e -> {
			LOGGER.error("An error occurred while retrieving profile data", e);
			return null;
		}));
	}

	/**
	 * Retrieves profile data by unique ID synchronously
	 * @param profileUniqueId unique ID of player profile
	 * @param consumer consumer which will be called synchronously
	 */
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

	/**
	 * Save profile data to database and cache
	 *
	 * @param profileData profile data to save
	 */
	public void saveProfileData(ProfileData profileData) {
		UUID profileUniqueId = profileData.getProfileUniqueId();

		CompletableFuture.runAsync(() -> {

			Document playerDocument = new Document(profileData.serialize());

			try {
				profileDataCollection.replaceOne(Filters.eq("profileUniqueId", profileUniqueId.toString()), playerDocument, new ReplaceOptions().upsert(true));
			} catch (CodecConfigurationException e) {
				LOGGER.error("Data: {}", playerDocument);
				throw new RuntimeException(e);
			}
			profileDataCache.put(profileData.getProfileUniqueId(), profileData);
		}).exceptionally((e -> {
			LOGGER.error("An error occurred while saving profile data", e);
			return null;
		}));
	}

	/**
	 * Retrieves {@link ProfileData} list containing all player profile data from database
	 *
	 * @param playerUniqueId unique ID of player profile
	 * @return {@link CompletableFuture} with list of profile data
	 */
	public @Nullable CompletableFuture<List<ProfileData>> getProfileDataList(UUID playerUniqueId) {
		return CompletableFuture.supplyAsync(() -> {
			FindIterable<Document> playerDocumentIterable = profileDataCollection.find(Filters.eq("playerUniqueId", playerUniqueId.toString()));
			try (MongoCursor<Document> cursor = playerDocumentIterable.cursor()) {
				List<ProfileData> profileDataList = new ArrayList<>();

				while (cursor.hasNext()) {
					Document document = cursor.next();
					ProfileData extractedData = ProfileData.deserialize(document);
					profileDataList.add(extractedData);
					profileDataCache.put(extractedData.getProfileUniqueId(), extractedData);
				}

				return profileDataList;
			}
		}).exceptionally((e -> {
			LOGGER.error("An error occurred while retrieving list of profile data", e);
			return null;
		}));
	}

	/**
	 * Retrives {@link ProfileData} list containing all player profile data from database synchronously
	 * @param playerUniqueId player unique ID
	 * @param consumer consumer which will be called synchronously with list of profile data
	 */
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
