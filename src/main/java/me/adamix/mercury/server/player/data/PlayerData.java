package me.adamix.mercury.server.player.data;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.player.stats.Statistics;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerData {
	private final @NotNull UUID playerUniqueId;
	private final @NotNull Statistics statistics;
	@Setter
	private @NotNull String translationId;

	public PlayerData(
			@NotNull UUID playerUniqueId,
			@NotNull String translationId,
			@NotNull Statistics statistics
	) {
		this.playerUniqueId = playerUniqueId;
		this.translationId = translationId;
		this.statistics = statistics;
	}

	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("playerUniqueId", this.playerUniqueId.toString());
		map.put("translationId", this.translationId);
		map.put("statistics", this.statistics.serialize());

		return map;
	}

	@SuppressWarnings("unchecked")
	public static @NotNull PlayerData deserialize(Document document) {
		String playerStringUniqueId = document.getString("playerUniqueId");
		if (!document.containsKey("playerUniqueId")) {
			throw new RuntimeException("Player profile does not include player uuid!");
		}
		UUID playerUniqueId = UUID.fromString(playerStringUniqueId);

		String translationId = document.containsKey("translationId") ? document.getString("translationId") : PlayerDefaults.getTranslationId();
		Object statisticsObject = document.get("statistics");
		Statistics playerStatistics;
		if (statisticsObject != null) {
			playerStatistics = Statistics.deserialize((Map<String, Object>) statisticsObject);
		} else {
			playerStatistics = new Statistics();
		}

		return new PlayerData(
				playerUniqueId,
				translationId,
				playerStatistics
		);
	}
}
