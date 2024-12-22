package me.adamix.mercury.server.player.profile.quest;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class ProfileQuests {
	@Setter
	private @Nullable NamespaceID activeQuest;
	private final List<NamespaceID> completedQuests;

	public ProfileQuests(@Nullable NamespaceID activeQuest, List<NamespaceID> completedQuests) {
		this.activeQuest = activeQuest;
		this.completedQuests = completedQuests;
	}

	public void completeCurrentQuest() {
		completedQuests.add(activeQuest);
		activeQuest = null;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("activeQuestID", this.activeQuest != null ? this.activeQuest : "null");
		map.put("completedQuestIds", this.completedQuests);

		return map;
	}

	public static ProfileQuests deserialize(Map<String, Object> map) {
		NamespaceID activeQuestID = null;
		Object activeQuestIDObject = map.get("activeQuestID");
		if (activeQuestIDObject instanceof String activeQuestIDString) {
			if (!"null".equals(activeQuestIDString)) {
				activeQuestID = NamespaceID.from(activeQuestIDString);
			}
		}

		List<NamespaceID> completedQuestIds = new ArrayList<>();
		Object completedQuestObject = map.get("completedQuestIds");
		if (completedQuestObject instanceof List<?> list) {
			for (Object item : list) {
				if (item instanceof NamespaceID ID) {
					completedQuestIds.add(ID);
				}
				if (item instanceof String stringId) {
					completedQuestIds.add(NamespaceID.from(stringId));
				}
			}
		}

		return new ProfileQuests(activeQuestID, completedQuestIds);
	}
}
