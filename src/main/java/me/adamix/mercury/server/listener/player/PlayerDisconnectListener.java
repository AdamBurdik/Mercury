package me.adamix.mercury.server.listener.player;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.data.PlayerData;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;


public class PlayerDisconnectListener implements EventListener<PlayerDisconnectEvent> {
	@Override
	public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
		MercuryPlayer player = MercuryPlayer.of(event);

		PlayerData playerData = player.getPlayerData();
		Server.getPlayerDataManager().savePlayerData(playerData);
		Server.getProfileDataManager().saveProfileData(player.getProfileData());

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerDisconnectEvent> eventType() {
		return PlayerDisconnectEvent.class;
	}
}
