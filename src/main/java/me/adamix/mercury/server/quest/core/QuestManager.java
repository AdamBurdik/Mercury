package me.adamix.mercury.server.quest.core;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.profile.ProfileData;
import me.adamix.mercury.server.player.profile.quest.ProfileQuests;
import me.adamix.mercury.server.quest.ExampleQuest;
import me.adamix.mercury.server.task.QuestTickTask;
import net.minestom.server.MinecraftServer;
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
			quest.tick(MercuryPlayer.of(MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(playerUniqueId)));
		});
	}

	public void startQuest(NamespaceID questID, MercuryPlayer player) {
		MercuryQuest quest = getRegisteredQuest(questID);
		if (quest == null) {
			return;
		}

		ProfileData profileData = player.getProfileData();
		ProfileQuests profileQuests = profileData.getProfileQuests();
		profileQuests.addActiveQuest(questID);
		this.activeQuests.put(player.getUuid(), quest);
		quest.start(player);
		profileQuests.setTrackingQuest(questID);
	}

	public void finishQuest(MercuryPlayer player, NamespaceID questID) {
		player.getProfileData().getProfileQuests().completeQuest(questID);
		activeQuests.remove(player.getUuid());
	}

}
