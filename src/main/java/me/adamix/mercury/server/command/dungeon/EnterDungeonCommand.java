package me.adamix.mercury.server.command.dungeon;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.dungeon.Dungeon;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.utils.NamespaceID;

import java.util.Set;

public class EnterDungeonCommand extends Command {
	public EnterDungeonCommand() {
		super("dungeon");

		var dungeonIDArgument = ArgumentType.String("dungeonID");

		setDefaultExecutor((sender, ctx) -> {
			sender.sendMessage("Please specify dungeon id!");
		});

		dungeonIDArgument.setSuggestionCallback((sender, ctx, suggestion) -> {
			for (NamespaceID dungeonId : Server.getDungeonManager().getDungeonIds()) {
				suggestion.addEntry(new SuggestionEntry(dungeonId.asString()));
			}
		});

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			NamespaceID dungeonID = NamespaceID.from(ctx.get(dungeonIDArgument));
			Dungeon dungeon = Server.getDungeonManager().create(dungeonID, Set.of(player));
			dungeon.start();

		}, dungeonIDArgument);}
}
