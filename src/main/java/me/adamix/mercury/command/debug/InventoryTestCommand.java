package me.adamix.mercury.command.debug;

import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.command.builder.Command;

import java.util.Arrays;

public class InventoryTestCommand extends Command {
	public InventoryTestCommand() {
		super("test_inv");

		setDefaultExecutor((sender, ctx) -> {
			if (sender instanceof GamePlayer player) {

				sender.sendMessage(
						Arrays.toString(player.getInventory().getItemStacks())
				);

			}
		});
	}
}
