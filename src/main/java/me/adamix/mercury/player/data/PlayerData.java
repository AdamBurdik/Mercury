package me.adamix.mercury.player.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

@Getter
public class PlayerData {
	private final @NotNull UUID playerUniqueId;
	private @NotNull Duration playTime;

	public PlayerData(@NotNull UUID playerUniqueId, @NotNull Duration playTime) {
		this.playerUniqueId = playerUniqueId;
		this.playTime = playTime;
	}

	public void increasePlayTime(int secondAmount) {
		playTime = playTime.plusSeconds(secondAmount);
	}
}
