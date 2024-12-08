package me.adamix.mercury.server.command;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.mob.core.MobManager;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.utils.NamespaceID;


public class EntityCommand extends Command {
	public EntityCommand()  {
		super("entity");

		setDefaultExecutor((sender, ctx) -> {
			sender.sendMessage(
					Component.text("Please specify action!")
							.color(ColorPallet.ERROR.getColor())
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
					for (NamespaceID namespaceID : Server.getMobManager().getEntityIdCollection()) {
						suggestion.addEntry(new SuggestionEntry(namespaceID.asString()));
					}
			}
		});

		addSyntax((sender, ctx) -> {
			String action = ctx.get(actionArgument);
			switch (action.toLowerCase()) {
				case "spawn":

					sender.sendMessage(
							Component.text("Please specify entity id!")
									.color(ColorPallet.ERROR.getColor())
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
				case "spawn":

					NamespaceID namespaceID = NamespaceID.from(second);

					MobManager mobManager = Server.getMobManager();
					if (!mobManager.contains(namespaceID)) {
						sender.sendMessage(
								Component.text("Please specify valid entity id!")
										.color(ColorPallet.ERROR.getColor())
						);
						return;
					}

					MercuryMob mob = mobManager.spawn(namespaceID, player.getInstance(), player.getPosition());

					for (MercuryPlayer onlinePlayer : Server.getOnlinePlayers()) {
						onlinePlayer.show(mob);
					}
			}

		}, actionArgument, secondArgument);
	}
}
