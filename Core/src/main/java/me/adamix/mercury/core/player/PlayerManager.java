package me.adamix.mercury.core.player;

import lombok.Setter;
import me.adamix.mercury.core.defaults.PlayerDefaults;
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
		// ToDO Get default translation code from config
		return mercuryPlayerMap.computeIfAbsent(player.getUniqueId(), uuid -> new MercuryPlayer(player, PlayerDefaults.getDefaultTranslationCode()));
	}

	public @Nullable MercuryPlayer get(@NotNull UUID uuid) {
		return mercuryPlayerMap.get(uuid);
	}

	public void remove(@NotNull UUID uuid) {
		mercuryPlayerMap.remove(uuid);
	}
}
