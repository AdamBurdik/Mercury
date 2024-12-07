package me.adamix.mercury.server.command;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.player.GamePlayer;
import me.adamix.mercury.server.translation.Translation;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;

import java.time.Duration;

public class PlayTimeCommand extends Command {
	public PlayTimeCommand()  {
		super("playtime");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof GamePlayer player)) {
				return;
			}

			Duration playtime = player.getPlayerData().getPlayTime();

			Translation translation = Server.getTranslationManager().getTranslation(player.getTranslationId());
			Component component = translation.getComponent("command.playtime.your_playtime");

			player.sendMessage(
					component.append(
							Component.text(playtime.toHours() + "h, " + playtime.toMinutes() % 60 + "min, " + playtime.getSeconds() % 60 + "s")
					).color(ColorPallet.MATERIAL_EMERALD.getColor())
			);
		});
	}
}
