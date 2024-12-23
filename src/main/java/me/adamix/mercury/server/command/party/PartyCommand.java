package me.adamix.mercury.server.command.party;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.party.PartyManager;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

import java.util.UUID;

public class PartyCommand extends Command {
	public PartyCommand() {
		super("party");

		setDefaultExecutor((sender, ctx) -> {
			sender.sendMessage(
					Component.text("Please specify action!")
							.color(ColorPallet.ERROR.getColor())
			);
		});

		var actionArgument = ArgumentType.String("action");
		actionArgument.setSuggestionCallback((sender, ctx, suggestion) -> {
			suggestion.addEntry(new SuggestionEntry("create"));
			suggestion.addEntry(new SuggestionEntry("join"));
			suggestion.addEntry(new SuggestionEntry("accept"));
			suggestion.addEntry(new SuggestionEntry("leave"));
			suggestion.addEntry(new SuggestionEntry("info"));
		});

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			String action = ctx.get(actionArgument);

			PartyManager partyManager = Server.getPartyManager();

			switch (action) {
				case "create":
					partyManager.createParty(player);
					break;
				case "join":
					player.sendMessage(
							Component.text("Please specify player name!")
					);
					break;
				case "leave":
					break;
				case "info":
					break;
			}
		}, actionArgument);

		var playerArgument = ArgumentType.String("playername");
		playerArgument.setSuggestionCallback((sender, ctx, suggestion) -> {
			for (MercuryPlayer onlinePlayer : Server.getOnlinePlayers()) {
				suggestion.addEntry(new SuggestionEntry(onlinePlayer.getUsername()));
			}
		});

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			String action = ctx.get(actionArgument);
			String playerName = ctx.get(playerArgument);

			switch (action) {
				case "join":
					MercuryPlayer partyMember = MercuryPlayer.of(MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(playerName));
					UUID partyUniqueId = partyMember.getPartyUniqueId();
					if (partyUniqueId == null) {
						sender.sendMessage(
								Component.text("Player is not in part!")
										.color(ColorPallet.ERROR.getColor())
						);
						break;
					}

					Server.getPartyManager().requestJoinParty(player, partyUniqueId);
					sender.sendMessage(
							Component.text("Your request to join '" + Server.getPartyManager().getParty(partyUniqueId).getName() + "' party has been sent!")
									.color(ColorPallet.SUCCESS.getColor())
					);
					break;
				case "accept":
					MercuryPlayer requester = MercuryPlayer.of(MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(playerName));
					Server.getPartyManager().acceptJoinRequest(player, requester);
					requester.sendMessage(
							Component.text("You have joined '" + Server.getPartyManager().getParty(player.getPartyUniqueId()).getName() + "' party")
									.color(ColorPallet.SUCCESS.getColor())
					);
					for (UUID allMember : Server.getPartyManager().getParty(player.getPartyUniqueId()).getAllMembers()) {
						MercuryPlayer member = MercuryPlayer.of(MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(allMember));
						member.sendMessage(
								Component.text(requester.getUsername() + " has joined your party!")
										.color(ColorPallet.SUCCESS.getColor())
						);
					}
			}

		}, actionArgument, playerArgument);
	}
}
