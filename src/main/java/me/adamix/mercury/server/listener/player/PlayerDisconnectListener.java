package me.adamix.mercury.server.listener.player;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.GamePlayer;
import me.adamix.mercury.server.player.data.PlayerData;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;


public class PlayerDisconnectListener implements EventListener<PlayerDisconnectEvent> {
	@Override
	public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
		GamePlayer player = GamePlayer.of(event);

		PlayerData playerData = player.getPlayerData();
		if (playerData != null) {
			Server.getPlayerDataManager().savePlayerData(playerData);
		}

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerDisconnectEvent> eventType() {
		return PlayerDisconnectEvent.class;
	}
}
