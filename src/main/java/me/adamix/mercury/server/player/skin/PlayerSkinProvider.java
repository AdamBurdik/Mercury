package me.adamix.mercury.server.player.skin;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerSkinProvider {
	private static final Cache<UUID, PlayerSkin> skinCache = Caffeine.newBuilder()
			.maximumSize(50)
			.expireAfterAccess(30, TimeUnit.MINUTES)
			.build();

	public static @Nullable PlayerSkin getByUniqueId(UUID playerUniqueId) {
		PlayerSkin playerSkin = skinCache.getIfPresent(playerUniqueId);
		if (playerSkin != null) {
			return playerSkin;
		}

		playerSkin = PlayerSkin.fromUuid(playerUniqueId.toString());
		if (playerSkin != null) {
			skinCache.put(playerUniqueId, playerSkin);
		}

		return playerSkin;
	}

}
