package me.adamix.mercury.server.command.debug;

import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;

public class DebugCommand extends Command {
	public DebugCommand() {
		super("debug");

		setDefaultExecutor((sender, ctx) -> {
			if (sender instanceof MercuryPlayer player) {
				player.setInDebug(!player.isInDebug());
				if (player.isInDebug()) {
					player.sendMessage(
							Component.text("Debug mode has been enabled!")
									.color(ColorPallet.SUCCESS.getColor())
					);
				} else {
					player.sendMessage(
							Component.text("Debug mode has been disabled!")
									.color(ColorPallet.ERROR.getColor())
					);
				}
				player.getGameInventory().updatePlayerInventory(player, true);
			}
		});
	}
}
