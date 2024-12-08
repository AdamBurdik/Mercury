package me.adamix.mercury.server.command;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.ProfileSelectionInventory;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.command.builder.Command;

public class InventoryCommand extends Command {
	public InventoryCommand() {
		super("inventory");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			Server.getProfileDataManager().getProfileDataListSync(player.getUuid(), (playerDataList -> {
				ProfileSelectionInventory inventory = new ProfileSelectionInventory(playerDataList);
				player.openGameInventory(inventory);
			}));

		});
	}
}
