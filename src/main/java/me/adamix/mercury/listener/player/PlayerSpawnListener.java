package me.adamix.mercury.listener.player;

import me.adamix.mercury.managers.Managers;
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
		GamePlayer gamePlayer = GamePlayer.of(event);

		Component message = Component.newline()
						.append(
								Component.text("Welcome to the server!")
										.color(TextColor.color(38, 160, 48))
						).append(
								Component.newline()
						);

		gamePlayer.setNoGravity(true);
		gamePlayer.setGameMode(GameMode.CREATIVE);
		gamePlayer.sendMessage(message);
		gamePlayer.addEffect(new Potion(PotionEffect.BLINDNESS, Byte.MAX_VALUE, Potion.INFINITE_DURATION));

		Managers.getInventoryManager().open("profile_selection", gamePlayer);

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerSpawnEvent> eventType() {
		return PlayerSpawnEvent.class;
	}
}
