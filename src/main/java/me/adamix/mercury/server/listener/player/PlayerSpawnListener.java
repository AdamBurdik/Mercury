package me.adamix.mercury.server.listener.player;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.inventory.ProfileSelectionInventory;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.state.PlayerState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;


public class PlayerSpawnListener implements EventListener<PlayerSpawnEvent> {
	@Override
	public @NotNull Result run(@NotNull PlayerSpawnEvent event) {
		if (!event.isFirstSpawn()) {
			return Result.SUCCESS;
		}

		MercuryPlayer player = MercuryPlayer.of(event);
		player.setNoGravity(true);
		player.setGameMode(GameMode.CREATIVE);
		player.addEffect(new Potion(PotionEffect.BLINDNESS, Byte.MAX_VALUE, Potion.INFINITE_DURATION));

		Server.getPlayerDataManager().loadPlayerData(player, () -> {
			Component message = Component.newline()
					.append(
							Component.text("Welcome to the server!")
									.color(TextColor.color(38, 160, 48))
					).appendNewline();
			player.sendMessage(message);

			Server.getProfileDataManager().getProfileDataListSync(player.getUuid(), (playerDataList -> {
				player.setState(PlayerState.LIMBO);
				ProfileSelectionInventory inventory = new ProfileSelectionInventory(playerDataList);
				player.openGameInventory(inventory);
			}));
		});

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerSpawnEvent> eventType() {
		return PlayerSpawnEvent.class;
	}
}
