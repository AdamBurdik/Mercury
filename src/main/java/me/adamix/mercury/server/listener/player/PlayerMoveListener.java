package me.adamix.mercury.server.listener.player;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.ProfileSelectionInventory;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.state.PlayerState;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;


public class PlayerMoveListener implements EventListener<PlayerMoveEvent> {
	@Override
	public @NotNull Result run(@NotNull PlayerMoveEvent event) {
		MercuryPlayer player = MercuryPlayer.of(event);
		if (player.getState() == PlayerState.LIMBO) {
			event.setCancelled(true);

			Server.getProfileDataManager().getProfileDataListSync(player.getUuid(), (playerDataList -> {
				ProfileSelectionInventory inventory = new ProfileSelectionInventory(playerDataList);
				player.openGameInventory(inventory);
			}));
		}
		if (player.getState() == PlayerState.PLAY) {
			// Setting Y to 0 because walked distance should ignore jumping
			player.getProfileData().getStatistics().increase(
					"walked",
					(float) player.getPosition().withY(0).distance(event.getNewPosition().withY(0)));
		}

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerMoveEvent> eventType() {
		return PlayerMoveEvent.class;
	}
}
