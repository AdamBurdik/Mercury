package me.adamix.mercury.command;

import me.adamix.mercury.item.core.GameItem;
import me.adamix.mercury.Server;
import me.adamix.mercury.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class ItemCommand extends Command {
	public ItemCommand() {
		super("item");


		setDefaultExecutor((sender, ctx) -> {
			sender.sendMessage(
					Component.text("Please specify action!")
							.color(TextColor.color(255, 0, 0))
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
					for (String id : Server.getItemManager().getItemIdCollection()) {
						suggestion.addEntry(new SuggestionEntry(id));
					}
			}
		});

		addSyntax((sender, ctx) -> {
			String action = ctx.get(actionArgument);
			switch (action.toLowerCase()) {
				case "give":
					sender.sendMessage(
							Component.text("Please specify item id!")
									.color(TextColor.color(255, 0, 0))
					);
					break;
				case "list":
					sender.sendMessage(
							Component.text(
									Server.getItemManager().getItemIdCollection().toString()
							).color(TextColor.color(128, 186, 255))
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

					GameItem item = Server.getItemManager().get(second);
					if (item == null) {
						sender.sendMessage(
								Component.text("Please specify valid item id!")
										.color(TextColor.color(255, 0, 0))
						);
						return;
					}

					item.give(player);
			}

		}, actionArgument, secondArgument);
	}
}
