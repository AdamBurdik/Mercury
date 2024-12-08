package me.adamix.mercury.server.command;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.item.core.MercuryItem;
import me.adamix.mercury.server.item.core.ItemManager;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.inventory.GamePlayerInventory;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.utils.NamespaceID;

public class ItemCommand extends Command {
	public ItemCommand() {
		super("item");


		setDefaultExecutor((sender, ctx) -> {
			sender.sendMessage(
					Component.text("Please specify action!")
							.color(ColorPallet.ERROR.getColor())
			);
		});

		String[] actionArray = new String[]{"give"};

		var actionArgument = ArgumentType.String("action");
		actionArgument.setSuggestionCallback(((sender, ctx, suggestion) -> {
			for (String action : actionArray) {
				suggestion.addEntry(new SuggestionEntry(action));
			}
		}));
		var secondArgument = ArgumentType.String("second");
		secondArgument.setSuggestionCallback((sender, ctx, suggestion) -> {
			String action = ctx.get(actionArgument);

			switch (action.toLowerCase()) {
				case "give":
					for (NamespaceID namespaceID : Server.getItemBlueprintManager().getItemIdCollection()) {
						suggestion.addEntry(new SuggestionEntry(namespaceID.asString()));
					}
			}
		});

		addSyntax((sender, ctx) -> {
			String action = ctx.get(actionArgument);
			switch (action.toLowerCase()) {
				case "give":
					sender.sendMessage(
							Component.text("Please specify item id!")
									.color(ColorPallet.ERROR.getColor())
					);
					break;
				case "list":
					sender.sendMessage(
							Component.text(
									Server.getItemBlueprintManager().getItemIdCollection().toString()
							).color(ColorPallet.BLUE.getColor())
					);
					break;
			}

		}, actionArgument);

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			String action = ctx.get(actionArgument);
			String second = ctx.get(secondArgument);

			switch (action.toLowerCase()) {
				case "give":

					ItemManager itemManager = Server.getItemManager();
					NamespaceID blueprintID = NamespaceID.from(second);

					if (!itemManager.canBuild(blueprintID)) {
						sender.sendMessage(
								Component.text("Please specify valid item id!")
										.color(ColorPallet.ERROR.getColor())
						);
						return;
					}

					MercuryItem mercuryItem = itemManager.buildItem(blueprintID);

					GamePlayerInventory inventory = player.getGameInventory();
					inventory.addItem(mercuryItem);
					inventory.updatePlayerInventory(player, false);
			}

		}, actionArgument, secondArgument);
	}
}
