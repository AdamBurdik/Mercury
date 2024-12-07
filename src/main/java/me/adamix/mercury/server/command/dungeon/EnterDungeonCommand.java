package me.adamix.mercury.server.command.dungeon;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.GamePlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.utils.NamespaceID;

import java.util.Set;

public class EnterDungeonCommand extends Command {
	public EnterDungeonCommand() {
		super("dungeon");

		var dungeonIDArgument = ArgumentType.String("dungeonID");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof GamePlayer player)) {
				return;
			}

			NamespaceID dungeonID = NamespaceID.from(ctx.get(dungeonIDArgument));

			Server.getDungeonManager().create(dungeonID, Set.of(player));

		});
	}
}
