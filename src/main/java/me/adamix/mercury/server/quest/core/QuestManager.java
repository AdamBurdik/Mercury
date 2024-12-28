package me.adamix.mercury.server.quest.core;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.profile.ProfileData;
import me.adamix.mercury.server.player.quest.PlayerQuests;
import me.adamix.mercury.server.quest.ExampleQuest;
import me.adamix.mercury.server.quest.core.result.QuestResult;
import me.adamix.mercury.server.task.QuestTickTask;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QuestManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestManager.class);
	private final Map<NamespaceID, MercuryQuest> registeredQuests = new HashMap<>();
	private final Map<UUID, MercuryQuest> activeQuests = new ConcurrentHashMap<>();

	public void init() {
		registeredQuests.clear();
		registerQuest(new ExampleQuest(100));

		Server.getTaskManager().startTask(new QuestTickTask());
	}

	public void registerQuest(MercuryQuest quest) {
		LOGGER.info("Quest '{}' has been registered", quest.getQuestID());
		registeredQuests.put(quest.getQuestID(), quest);
	}

	public Set<NamespaceID> getRegisteredQuestIds() {
		return registeredQuests.keySet();
	}

	public @Nullable MercuryQuest getRegisteredQuest(NamespaceID questID) {
		return registeredQuests.get(questID);
	}

	public void tickQuests() {
		activeQuests.forEach((playerUniqueId, quest) -> {
			quest.tick(Server.getOnlinePlayerByUniqueId(playerUniqueId));
		});
	}

	public boolean exits(NamespaceID questID) {
		return this.registeredQuests.containsKey(questID);
	}

	public QuestResult startQuest(NamespaceID questID, MercuryPlayer player) {
		MercuryQuest quest = getRegisteredQuest(questID);
		if (quest == null) {
			return QuestResult.QUEST_NOT_FOUND;
		}

		ProfileData profileData = player.getProfileData();
		PlayerQuests playerQuests = profileData.getPlayerQuests();
		if (playerQuests.isCompleted(questID)) {
			return QuestResult.QUEST_ALREADY_COMPLETED;
		}

		playerQuests.addActiveQuest(questID);
		this.activeQuests.put(player.getUuid(), quest);
		quest.start(player);
		playerQuests.setTrackingQuest(questID);
		player.updateSidebar();

		return QuestResult.SUCCESS;
	}

	public QuestResult finishQuest(MercuryPlayer player, NamespaceID questID) {
		if (!exits(questID)) {
			return QuestResult.QUEST_NOT_FOUND;
		}
		PlayerQuests playerQuests = player.getProfileData().getPlayerQuests();

		if (playerQuests.isCompleted(questID)) {
			return QuestResult.QUEST_ALREADY_COMPLETED;
		}

		activeQuests.remove(player.getUuid());
		playerQuests.completeQuest(questID);
		player.updateSidebar();

		return QuestResult.SUCCESS;
	}

}
