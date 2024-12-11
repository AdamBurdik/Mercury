package me.adamix.mercury.server.mob.core.wrapper;

import me.adamix.mercury.server.mob.core.wrapper.goal.FollowEntityWrapper;
import me.adamix.mercury.server.mob.core.wrapper.goal.GoalWrapper;
import me.adamix.mercury.server.mob.core.wrapper.goal.RandomStrollWrapper;
import me.adamix.mercury.server.mob.core.wrapper.target.ClosestEntityWrapper;
import me.adamix.mercury.server.mob.core.wrapper.target.TargetWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AIWrapperManager {
	private final Map<String, GoalWrapper> goalWrapperMap = new HashMap<>();
	private final Map<String, TargetWrapper> targetWrapperMap = new HashMap<>();

	public AIWrapperManager() {
		registerGoalWrapper("followEntity", new FollowEntityWrapper());
		registerGoalWrapper("randomStroll", new RandomStrollWrapper());

		registerTargetWrapper("closestEntity", new ClosestEntityWrapper());
	}

	public void registerGoalWrapper(@NotNull String name, @NotNull GoalWrapper goalWrapper) {
		goalWrapperMap.put(name, goalWrapper);
	}

	public void registerTargetWrapper(@NotNull String name, @NotNull TargetWrapper targetWrapper) {
		targetWrapperMap.put(name, targetWrapper);
	}

	public @Nullable GoalWrapper getGoalWrapper(@NotNull String name) {
		return goalWrapperMap.get(name);
	}

	public @Nullable TargetWrapper getTargetWrapper(@NotNull String name) {
		return targetWrapperMap.get(name);
	}
}
