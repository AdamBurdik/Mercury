package me.adamix.mercury.core.player;

import lombok.Setter;
import me.adamix.mercury.core.MercuryCore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MercuryPlayer {
	private final @NotNull Player bukkitPlayer;
	@Setter
	private @NotNull String translationCode;

	public MercuryPlayer(@NotNull Player bukkitPlayer, @NotNull String translationCode) {
		this.bukkitPlayer = bukkitPlayer;
		this.translationCode = translationCode;
	}

	/**
	 * Gets a player name.
	 * @return a player name.
	 */
	public @NotNull String getName() {
		return bukkitPlayer.getName();
	}

	/**
	 * Gets a player uuid.
	 * @return a player uuids
	 */
	public @NotNull UUID getUniqueId() {
		return bukkitPlayer.getUniqueId();
	}

	/**
	 * Gets a translation code of the player.
	 * @return translation code.
	 */
	public @NotNull String getTranslationCode() {
		return translationCode;
	}


	@ApiStatus.Internal
	public @NotNull Player getBukkitPlayer() {
		return bukkitPlayer;
	}

	public static @NotNull MercuryPlayer of(@NotNull Player player) {
		return MercuryCore.playerManager().getOrCreate(player);
	}
}
