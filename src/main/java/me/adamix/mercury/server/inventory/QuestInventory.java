package me.adamix.mercury.server.inventory;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.core.MercuryInventory;
import me.adamix.mercury.server.inventory.core.context.InventoryConfig;
import me.adamix.mercury.server.inventory.core.context.OpenContext;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.quest.core.MercuryQuest;
import me.adamix.mercury.server.quest.core.QuestManager;
import me.adamix.mercury.server.translation.Translation;
import me.adamix.mercury.server.translation.TranslationManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;

import java.util.Set;

public class QuestInventory extends MercuryInventory {
	@Override
	public void onInit(InventoryConfig config) {
		config.title("Your current quests");
		config.rows(4);
	}

	@Override
	public void onOpen(OpenContext ctx) {
		MercuryPlayer player = ctx.getPlayer();

		QuestManager questManager = Server.getQuestManager();
		Translation translation = TranslationManager.getTranslation(player);

		Set<NamespaceID> activeQuests = player.getProfileData().getPlayerQuests().getActiveQuests();
		int i = 0;
		for (NamespaceID activeQuestID : activeQuests) {
			MercuryQuest quest = questManager.getRegisteredQuest(activeQuestID);
			if (quest == null) {
				continue;
			}

			ItemStack item = ItemStack.of(Material.BOOK)
					.withCustomName(Component.text(translation.get(quest.translationName())))
					.withLore(Component.text(translation.get(quest.translationDescription())))
					.withTag(Tag.String("questID"), quest.getQuestID().asString());

			ctx.slot(i, item)
					.onClick((click) -> {
						click.setCancelled(true);
						MercuryPlayer clickedPlayer = click.getPlayer();
						String questID = click.getItemStack().getTag(Tag.String("questID"));
						clickedPlayer.getProfileData().getPlayerQuests().setTrackingQuest(NamespaceID.from(questID));
					});

			i++;
		}
	}
}
