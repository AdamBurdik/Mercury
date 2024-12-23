package me.adamix.mercury.server.command.quest;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.QuestInventory;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.command.builder.Command;

public class QuestsCommand extends Command {
	public QuestsCommand() {
		super("quests");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			Server.getInventoryManager().open(new QuestInventory(), player);
		});
	}
}
