package me.adamix.mercury.core.player;

import me.adamix.mercury.core.MercuryCore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MercuryPlayer {
	private final @NotNull Player bukkitPlayer;

	public MercuryPlayer(@NotNull Player bukkitPlayer) {
		this.bukkitPlayer = bukkitPlayer;
	}

	public static @NotNull MercuryPlayer of(@NotNull Player player) {
		return MercuryCore.playerManager().getOrCreate(player);
	}
}
