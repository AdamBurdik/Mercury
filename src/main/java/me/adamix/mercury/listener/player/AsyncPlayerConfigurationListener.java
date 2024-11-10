package me.adamix.mercury.listener.player;

import me.adamix.mercury.Server;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerConfigurationListener implements EventListener<AsyncPlayerConfigurationEvent> {
	@Override
	public @NotNull Result run(@NotNull AsyncPlayerConfigurationEvent event) {
		final Player player = event.getPlayer();
		event.setSpawningInstance(Server.getMainInstance());
		player.setRespawnPoint(Server.LIMBO_LOCATION);

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<AsyncPlayerConfigurationEvent> eventType() {
		return AsyncPlayerConfigurationEvent.class;
	}
}
