package me.adamix.mercury.command;

import me.adamix.mercury.Server;
import me.adamix.mercury.common.ColorPallet;
import me.adamix.mercury.item.core.GameItem;
import me.adamix.mercury.item.core.ItemManager;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.player.inventory.GamePlayerInventory;
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
			if (!(sender instanceof GamePlayer player)) {
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

					GameItem gameItem = itemManager.buildItem(blueprintID);

					GamePlayerInventory inventory = player.getGameInventory();
					inventory.addItem(gameItem);
					inventory.updatePlayerInventory(player, false);
			}

		}, actionArgument, secondArgument);
	}
}
