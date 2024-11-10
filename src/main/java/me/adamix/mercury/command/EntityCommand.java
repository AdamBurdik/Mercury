package me.adamix.mercury.command;

import me.adamix.mercury.Server;
import me.adamix.mercury.mob.core.GameMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public class EntityCommand extends Command {
	public EntityCommand()  {
		super("entity");

		setDefaultExecutor((sender, ctx) -> {
			sender.sendMessage(
					Component.text("Please specify action!")
							.color(TextColor.color(255, 0, 0))
			);
		});

		String[] actionArray = new String[]{"spawn"};

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
				case "spawn":
					for (String id : Server.getMobManager().getEntityIdCollection()) {
						suggestion.addEntry(new SuggestionEntry(id));
					}
			}
		});

		addSyntax((sender, ctx) -> {
			String action = ctx.get(actionArgument);
			switch (action.toLowerCase()) {
				case "spawn":

					sender.sendMessage(
							Component.text("Please specify entity id!")
									.color(TextColor.color(255, 0, 0))
					);
					break;
			}

		}, actionArgument);

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof Player player)) {
				return;
			}

			String action = ctx.get(actionArgument);
			String second = ctx.get(secondArgument);

			switch (action.toLowerCase()) {
				case "spawn":

					GameMob entity = Server.getMobManager().get(second);
					if (entity == null) {
						sender.sendMessage(
								Component.text("Please specify valid entity id!")
										.color(TextColor.color(255, 0, 0))
						);
						return;
					}

					entity.spawn(player.getPosition(), player.getInstance());

			}

		}, actionArgument, secondArgument);
	}
}
