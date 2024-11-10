package me.adamix.mercury.command;

import me.adamix.mercury.Server;
import me.adamix.mercury.common.ColorPallet;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.translation.Translation;
import me.adamix.mercury.translation.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class TranslationCommand extends Command {
	public TranslationCommand() {
		super("translation", "language", "lang");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof GamePlayer player)) {
				return;
			}

			Translation translation = TranslationManager.getTranslation(player);

			String text = translation.get("command.language.no_argument_specified");

			sender.sendMessage(
					Component.text(text)
							.color(TextColor.color(ColorPallet.SUCCESS.getColor()))
			);
		});

		var stringArgument = ArgumentType.String("code");
		stringArgument.setSuggestionCallback(((sender, context, suggestion) -> {
			for (String id : Server.getTranslationManager().getTranslationMap().keySet()) {
				suggestion.addEntry(new SuggestionEntry(id));
			}
		}));

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof GamePlayer player)) {
				return;
			}

			String translationId = ctx.get(stringArgument);
			// ToDo make sure player data is set
			// ToDo make sure translation id is valid
			player.getPlayerData().setTranslationId(translationId);

			Translation translation = TranslationManager.getTranslation(player);

			String text = translation.get("command.translation.successfully_set");
			player.sendMessage(
					Component.text(text)
							.color(ColorPallet.SUCCESS.getColor())
			);

		}, stringArgument);
	}
}
