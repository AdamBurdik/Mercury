package me.adamix.mercury.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class GamemodeCommand extends Command {
	public GamemodeCommand() {
		super("gamemode");

		setDefaultExecutor((sender, ctx) -> {
			sender.sendMessage(
					Component.text("Please specify gamemode")
							.color(TextColor.color(255, 0, 0))
			);
		});

		var stringArgument = ArgumentType.String("gamemode");
		stringArgument.setSuggestionCallback((sender, ctx, suggestion) -> {
			for (GameMode value : GameMode.values()) {
				suggestion.addEntry(new SuggestionEntry(value.name().toLowerCase()));
			}
		});

		addSyntax((sender, ctx) -> {
			if (sender instanceof Player player) {
				player.setGameMode(GameMode.valueOf(ctx.get(stringArgument).toUpperCase()));
			}
		}, stringArgument);
	}
}
