package me.adamix.mercury.server.command.quest;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.quest.core.MercuryQuest;
import me.adamix.mercury.server.quest.core.QuestManager;
import me.adamix.mercury.server.quest.core.QuestProgress;
import me.adamix.mercury.server.quest.core.result.QuestResult;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.utils.NamespaceID;

public class QuestCommand extends Command {
	public QuestCommand() {
		super("quest");

		setDefaultExecutor((sender, ctx) -> {
			sender.sendMessage(Component.text("Please specify action!").color(ColorPallet.ERROR.getColor()));
		});

		var actionArgument = ArgumentType.String("action");
		actionArgument.setSuggestionCallback((sender, ctx, suggestion) -> {
			suggestion.addEntry(new SuggestionEntry("start"));
			suggestion.addEntry(new SuggestionEntry("progress"));
		});
		addSyntax((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			String action = ctx.get(actionArgument);
			switch (action) {
				case "start":
					sender.sendMessage(Component.text("Please specify quest ID!").color(ColorPallet.ERROR.getColor()));
					break;
				case "progress":
					QuestManager questManager = Server.getQuestManager();
					NamespaceID activeQuestID = player.getProfileData().getProfileQuests().getTrackingQuest();
					if (activeQuestID == null) {
						sender.sendMessage(Component.text("No tracking quest!").color(ColorPallet.ERROR.getColor()));
						break;
					}
					MercuryQuest quest = questManager.getRegisteredQuest(activeQuestID);
					if (quest == null) {
						sender.sendMessage(Component.text("No tracking quest!").color(ColorPallet.ERROR.getColor()));
						break;
					}
					QuestProgress progress = quest.getProgress(player);
					player.sendMessage(
							Component.text(progress.getCurrentPercentage() + "%")
					);
					break;
			}

		}, actionArgument);

		var questIDArgument = ArgumentType.String("questID");
		questIDArgument.setSuggestionCallback((sender, ctx, suggestion) -> {
			String action = ctx.get(actionArgument);
			if (action.equals("start")) {
				for (NamespaceID registeredQuestId : Server.getQuestManager().getRegisteredQuestIds()) {
					suggestion.addEntry(new SuggestionEntry(registeredQuestId.asString()));
				}
			}
		});

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			String questIDString = ctx.get(questIDArgument);
			NamespaceID questID = NamespaceID.from(questIDString);

			QuestResult result = Server.getQuestManager().startQuest(questID, player);
			Component component = switch (result) {
				case QuestResult.QUEST_NOT_FOUND -> Component.text("Quest with ID " + questIDString + " does not exist!")
						.color(ColorPallet.ERROR.getColor());
				case QUEST_ALREADY_COMPLETED -> Component.text("You have already completed this quest!")
						.color(ColorPallet.ERROR.getColor());
				case SUCCESS -> Component.text("Successfully accepted quest with ID " + questIDString + ".")
						.color(ColorPallet.SUCCESS.getColor());
			};
			sender.sendMessage(component);

		}, actionArgument, questIDArgument);
	}
}
