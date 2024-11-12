package me.adamix.mercury.command;

import me.adamix.mercury.managers.Managers;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.command.builder.Command;

public class InventoryCommand extends Command {
	public InventoryCommand() {
		super("inventory");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof GamePlayer player)) {
				return;
			}

			Managers.getInventoryManager().open("profile_selection", player);

		});
	}
}
