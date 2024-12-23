package me.adamix.mercury.server.command.debug;

import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.command.builder.Command;

import java.util.Arrays;

public class InventoryTestCommand extends Command {
	public InventoryTestCommand() {
		super("test_inv");

		setDefaultExecutor((sender, ctx) -> {
			if (sender instanceof MercuryPlayer player) {

				sender.sendMessage(
						Arrays.toString(player.getInventory().getItemStacks())
				);

			}
		});
	}
}
