package me.adamix.mercury.listener.player;

import me.adamix.mercury.common.ColorPallet;
import me.adamix.mercury.Server;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.translation.Translation;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerCommandEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerCommandListener implements EventListener<PlayerCommandEvent> {
	@Override
	public @NotNull Result run(@NotNull PlayerCommandEvent event) {
		String command = event.getCommand();
		String[] split = command.split(" ");
		if (!MinecraftServer.getCommandManager().commandExists(split[0])) {
			GamePlayer player = GamePlayer.of(event);

			Translation translation = Server.getTranslationManager().getTranslation(player.getTranslationId());
			player.sendMessage(
					translation.getComponent("command.invalid")
							.color(ColorPallet.ERROR.getColor())
			);

			return Result.INVALID;
		}

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerCommandEvent> eventType() {
		return PlayerCommandEvent.class;
	}
}
