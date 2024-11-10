package me.adamix.mercury.listener.player;

import me.adamix.mercury.Server;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.player.state.PlayerState;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;


public class PlayerMoveListener implements EventListener<PlayerMoveEvent> {
	@Override
	public @NotNull Result run(@NotNull PlayerMoveEvent event) {
		GamePlayer player = GamePlayer.of(event);
		if (player.getState() == PlayerState.LIMBO) {
			event.setCancelled(true);
			Server.getInventoryManager().open("profile_selection", player);
		}

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerMoveEvent> eventType() {
		return PlayerMoveEvent.class;
	}
}
