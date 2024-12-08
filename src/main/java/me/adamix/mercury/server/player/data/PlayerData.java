package me.adamix.mercury.server.player.data;

import lombok.Getter;
import me.adamix.mercury.server.player.stats.Statistics;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class PlayerData {
	private final @NotNull UUID playerUniqueId;
	private final @NotNull Statistics statistics;

	public PlayerData(@NotNull UUID playerUniqueId, @NotNull Statistics statistics) {
		this.playerUniqueId = playerUniqueId;
		this.statistics = statistics;
	}
}
