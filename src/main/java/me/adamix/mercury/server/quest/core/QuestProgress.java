package me.adamix.mercury.server.quest.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestProgress {
	private int stageCount;
	private int currentStage;

	public QuestProgress(int stageCount) {
		this(stageCount, 0);
	}

	public QuestProgress(int stageCount, int currentStage) {
		this.stageCount = stageCount;
		this.currentStage = currentStage;
	}

	public float getCurrentPercentage() {
		return (float) (100 * currentStage) / stageCount;
	}

	public QuestProgress clone() {
		return new QuestProgress(stageCount, currentStage);
	}
}
