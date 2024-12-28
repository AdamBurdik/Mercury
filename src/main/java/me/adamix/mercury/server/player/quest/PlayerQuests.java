package me.adamix.mercury.server.player.quest;

import lombok.Getter;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class PlayerQuests {
	private final @NotNull Set<NamespaceID> activeQuests;
	private final @NotNull Set<NamespaceID> completedQuests;
	private @Nullable NamespaceID trackingQuest;

	public PlayerQuests(@NotNull Set<NamespaceID> activeQuests, @NotNull Set<NamespaceID> completedQuests) {
		this.activeQuests = activeQuests;
		this.completedQuests = completedQuests;
	}

	public void completeQuest(@NotNull NamespaceID questID) {
		activeQuests.remove(questID);
		completedQuests.add(questID);

		if (questID.equals(trackingQuest)) {
			setTrackingQuest(null);
		}
	}

	public boolean isCompleted(@NotNull NamespaceID questID) {
		return this.completedQuests.contains(questID);
	}

	public void addActiveQuest(@NotNull NamespaceID questID) {
		this.activeQuests.add(questID);
	}

	public void setTrackingQuest(@Nullable NamespaceID questID) {
		this.trackingQuest = questID;
	}

	private List<String> toStringList(Set<NamespaceID> namespaceIDSet) {
		List<String> activeQuestList = new ArrayList<>();
		for (NamespaceID activeQuest : namespaceIDSet) {
			activeQuestList.add(activeQuest.asString());
		}
		return activeQuestList;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("activeQuests", toStringList(this.activeQuests));
		map.put("completedQuests", toStringList(this.completedQuests));

		return map;
	}

	public static PlayerQuests deserialize(Map<String, Object> map) {
		Set<NamespaceID> activeQuestIDs = new HashSet<>();
		Object activeQuestObject = map.get("activeQuests");
		if (activeQuestObject instanceof List<?> list) {
			for (Object item : list) {
				if (item instanceof String stringId) {
					activeQuestIDs.add(NamespaceID.from(stringId));
				}
			}
		}

		Set<NamespaceID> completedQuestIDs = new HashSet<>();
		Object completedQuestObject = map.get("completedQuests");
		if (completedQuestObject instanceof List<?> list) {
			for (Object item : list) {
				if (item instanceof String stringId) {
					completedQuestIDs.add(NamespaceID.from(stringId));
				}
			}
		}

		return new PlayerQuests(activeQuestIDs, completedQuestIDs);
	}
}
