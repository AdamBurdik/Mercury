package me.adamix.mercury.core.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
	private final @NotNull Map<UUID, MercuryPlayer> mercuryPlayerMap = new HashMap<>();

	public @Nullable MercuryPlayer get(@NotNull Player player) {
		return get(player.getUniqueId());
	}

	public @NotNull MercuryPlayer getOrCreate(@NotNull Player player) {
		return mercuryPlayerMap.computeIfAbsent(player.getUniqueId(), uuid -> new MercuryPlayer(player));
	}

	public @Nullable MercuryPlayer get(@NotNull UUID uuid) {
		return mercuryPlayerMap.get(uuid);
	}
}
