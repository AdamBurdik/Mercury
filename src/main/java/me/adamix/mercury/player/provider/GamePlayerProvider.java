package me.adamix.mercury.player.provider;

import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GamePlayerProvider implements PlayerProvider {
	@Override
	public @NotNull Player createPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection connection) {
		return new GamePlayer(uuid, username, connection);
	}
}
