package me.adamix.mercury.server.command.mob;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.utils.NamespaceID;

public class SpawnMobCommand extends Command {
	public SpawnMobCommand() {
		super("spawn", "summon");

		var stringArg = ArgumentType.String("entityID");
		stringArg.setSuggestionCallback((sender, ctx, suggestion) -> {
			for (NamespaceID namespaceID : Server.getMobManager().getEntityIdCollection()) {
				suggestion.addEntry(new SuggestionEntry(namespaceID.asString()));
			}
		});

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			String entityID = ctx.get(stringArg);
			Server.getMobManager().spawn(player.getInstance(), player.getPosition(), NamespaceID.from(entityID));

		}, stringArg);
	}
}
