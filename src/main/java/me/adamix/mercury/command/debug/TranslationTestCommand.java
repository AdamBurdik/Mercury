package me.adamix.mercury.command.debug;

import me.adamix.mercury.Server;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class TranslationTestCommand extends Command {
	public TranslationTestCommand() {
		super("test_translation");

		var arg = ArgumentType.String("key");

		addSyntax((sender, ctx) -> {
			String key = ctx.get(arg);

			if (sender instanceof GamePlayer player) {

				sender.sendMessage(
						Server.getPlaceholderManager()
								.parse(key, player)
				);

			}

		}, arg);
	}
}
