package me.adamix.mercury.server.quest;

import lombok.Getter;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.stats.StatisticCategory;
import me.adamix.mercury.server.quest.core.MercuryQuest;
import me.adamix.mercury.server.quest.core.QuestData;
import me.adamix.mercury.server.quest.core.QuestProgress;
import net.minestom.server.utils.NamespaceID;

@Getter
public class ExampleQuest extends MercuryQuest {
	private final QuestData<Float> playerStartDistance = new QuestData<>();
	private final int targetWalked;

	public ExampleQuest(int targetWalked) {
		super(new NamespaceID("mercury", "example_quest"), new QuestProgress(targetWalked));
		this.targetWalked = targetWalked;
	}

	@Override
	public void start(MercuryPlayer player) {
		player.sendMessage("Example Quest started!");
		playerStartDistance.put(player, player.getProfileData().getStatistics().get(StatisticCategory.GENERAL, "walked"));
	}

	@Override
	public void tick(MercuryPlayer player) {
		float currentDistance = player.getProfileData().getStatistics().get(StatisticCategory.GENERAL, "walked");
		float startingDistance = playerStartDistance.get(player);
		if (currentDistance - startingDistance >= targetWalked) {
			this.finish(player);
		}
		setProgressStage(player, (int) (currentDistance - startingDistance));
	}

	@Override
	public void finish(MercuryPlayer player) {
		player.sendMessage("Example Quest finished!");
		player.getProfileData().getProfileQuests().completeQuest(this.getQuestID());
		super.finish(player);
	}

	@Override
	public void end(MercuryPlayer player) {
		super.end(player);
		playerStartDistance.remove(player);
	}
}
