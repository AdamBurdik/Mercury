package me.adamix.mercury.player.provider;

import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

public class GamePlayerProvider implements PlayerProvider {

	@Override
	public @NotNull Player createPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
		return new GamePlayer(playerConnection, gameProfile);
	}
}
