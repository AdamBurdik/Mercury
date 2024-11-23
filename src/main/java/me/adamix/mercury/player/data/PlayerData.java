package me.adamix.mercury.player.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class PlayerData {
	private final @NotNull UUID playerUniqueId;

	public PlayerData(@NotNull UUID playerUniqueId) {
		this.playerUniqueId = playerUniqueId;
	}
}
