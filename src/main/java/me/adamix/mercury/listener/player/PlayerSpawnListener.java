package me.adamix.mercury.listener.player;

import me.adamix.mercury.Server;
import me.adamix.mercury.inventory.ProfileSelectionInventory;
import me.adamix.mercury.player.GamePlayer;
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
		GamePlayer player = GamePlayer.of(event);

		Component message = Component.newline()
						.append(
								Component.text("Welcome to the server!")
										.color(TextColor.color(38, 160, 48))
						).append(
								Component.newline()
						);

		player.setNoGravity(true);
		player.setGameMode(GameMode.CREATIVE);
		player.sendMessage(message);
		player.addEffect(new Potion(PotionEffect.BLINDNESS, Byte.MAX_VALUE, Potion.INFINITE_DURATION));

		Server.getPlayerDataManager().getPlayerDataListSync(player.getUuid(), (playerDataList -> {
			ProfileSelectionInventory inventory = new ProfileSelectionInventory(playerDataList);
			player.openGameInventory(inventory);
		}));

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerSpawnEvent> eventType() {
		return PlayerSpawnEvent.class;
	}
}
