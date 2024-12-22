package me.adamix.mercury.server.quest.core;


import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.utils.NamespaceID;

@Getter
public abstract class MercuryQuest {
	private final NamespaceID questID;
	private final QuestData<QuestProgress> playerProgress = new QuestData<>();
	private final QuestProgress defaultProgress;

	public MercuryQuest(NamespaceID questID, QuestProgress progress) {
		this.questID = questID;
		this.defaultProgress = progress;
	}

	public abstract void start(MercuryPlayer player);
	public abstract void tick(MercuryPlayer player);

	public void finish(MercuryPlayer player) {
		Server.getQuestManager().finishQuest(player);
		this.end(player);
	}

	public void end(MercuryPlayer player) {
		playerProgress.remove(player);
	}

	public QuestProgress getProgress(MercuryPlayer player) {
		if (!playerProgress.containsKey(player)) {
			playerProgress.put(player, defaultProgress.clone());
		}
		return playerProgress.get(player);
	}

	public void setProgressStage(MercuryPlayer player, int currentStage) {
		getProgress(player).setCurrentStage(currentStage);
	}
}
