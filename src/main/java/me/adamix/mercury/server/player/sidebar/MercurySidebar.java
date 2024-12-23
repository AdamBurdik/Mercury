package me.adamix.mercury.server.player.sidebar;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.party.MercuryParty;
import me.adamix.mercury.server.placeholder.PlaceholderManager;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.quest.core.MercuryQuest;
import net.kyori.adventure.text.Component;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.utils.NamespaceID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MercurySidebar {
	private final Sidebar sidebar;

	public MercurySidebar() {
		this.sidebar = new Sidebar(Component.empty());
	}

	private void addQuestTracker(MercuryPlayer player, List<String> lines) {
		NamespaceID activeQuestID = player.getProfileData().getProfileQuests().getTrackingQuest();
		if (activeQuestID == null) {
			return;
		}
		MercuryQuest quest = Server.getQuestManager().getRegisteredQuest(activeQuestID);
		if (quest == null) {
			return;
		}

		// ToDo Get this from config
		String[] questTrackerLines = {
				"",
				"<#21497b><translation:scoreboard.tracking_quest.subtitle>",
				"<white><tracking_quest:name>",
				"<light_gray><tracking_quest:description>"
		};

		lines.addAll(Arrays.asList(questTrackerLines));
	}

	private void addParty(MercuryPlayer player, List<String> lines) {
		UUID partyUniqueId = player.getPartyUniqueId();
		if (partyUniqueId == null) {
			return;
		}

		String[] partyLines = {
				"",
				"<blue><party:name>",
				"<dark_blue><party:description>"
		};

		lines.addAll(Arrays.asList(partyLines));
	}

	public void clear() {
		sidebar.getLines().forEach(line -> sidebar.removeLine(line.getId()));
	}

	public void update(MercuryPlayer player) {
		// ToDO Get this from config
		String title = "<gold><translation:scoreboard.title>";

		List<String> lineList = new ArrayList<>();
		addQuestTracker(player, lineList);
		addParty(player, lineList);

		PlaceholderManager placeholderManager = Server.getPlaceholderManager();

		this.clear();
		int i = 16;
		for (String line : lineList) {
			sidebar.createLine(
					new Sidebar.ScoreboardLine(
							"line_" + i,
							placeholderManager.parse(line, player),
							i,
							Sidebar.NumberFormat.blank()
					)
			);

			i--;
		}

		sidebar.setTitle(placeholderManager.parse(title, player));
	}

	public void show(MercuryPlayer player) {
		update(player);
		sidebar.addViewer(player);
	}

	public void hide(MercuryPlayer player) {
		sidebar.removeViewer(player);
	}
}
