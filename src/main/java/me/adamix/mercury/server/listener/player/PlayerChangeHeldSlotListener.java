package me.adamix.mercury.server.listener.player;

import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import org.jetbrains.annotations.NotNull;


public class PlayerChangeHeldSlotListener implements EventListener<PlayerChangeHeldSlotEvent> {
	@Override
	public @NotNull Result run(@NotNull PlayerChangeHeldSlotEvent event) {
		MercuryPlayer player = MercuryPlayer.of(event);

		player.updateAttributes(event.getSlot());

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerChangeHeldSlotEvent> eventType() {
		return PlayerChangeHeldSlotEvent.class;
	}
}
