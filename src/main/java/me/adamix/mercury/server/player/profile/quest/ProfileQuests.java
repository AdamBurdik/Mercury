package me.adamix.mercury.server.player.profile.quest;

import lombok.Getter;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class ProfileQuests {
	private final @NotNull Set<NamespaceID> activeQuests;
	private final @NotNull Set<NamespaceID> completedQuests;
	private @Nullable NamespaceID trackingQuest;

	public ProfileQuests(@NotNull Set<NamespaceID> activeQuests, @NotNull Set<NamespaceID> completedQuests) {
		this.activeQuests = activeQuests;
		this.completedQuests = completedQuests;
	}

	public void completeQuest(@NotNull NamespaceID questID) {
		activeQuests.remove(questID);
		completedQuests.add(questID);
	}

	public void addActiveQuest(@NotNull NamespaceID questID) {
		this.activeQuests.add(questID);
	}

	public void setTrackingQuest(@Nullable NamespaceID questID) {
		this.trackingQuest = questID;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("activeQuestIDs", this.activeQuests);
		map.put("completedQuestIDs", this.completedQuests);

		return map;
	}

	public static ProfileQuests deserialize(Map<String, Object> map) {
		Set<NamespaceID> activeQuestIDs = new HashSet<>();
		Object activeQuestObject = map.get("activeQuestIDs");
		if (activeQuestObject instanceof List<?> list) {
			for (Object item : list) {
				if (item instanceof NamespaceID ID) {
					activeQuestIDs.add(ID);
				}
				if (item instanceof String stringId) {
					activeQuestIDs.add(NamespaceID.from(stringId));
				}
			}
		}

		Set<NamespaceID> completedQuestIDs = new HashSet<>();
		Object completedQuestObject = map.get("completedQuestIds");
		if (completedQuestObject instanceof List<?> list) {
			for (Object item : list) {
				if (item instanceof NamespaceID ID) {
					completedQuestIDs.add(ID);
				}
				if (item instanceof String stringId) {
					completedQuestIDs.add(NamespaceID.from(stringId));
				}
			}
		}

		return new ProfileQuests(activeQuestIDs, completedQuestIDs);
	}
}
