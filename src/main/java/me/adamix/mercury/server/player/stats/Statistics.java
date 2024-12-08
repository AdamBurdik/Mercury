package me.adamix.mercury.server.player.stats;

import me.adamix.mercury.server.common.SerializableEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Statistics implements SerializableEntity {
	private final Map<String, Float> statisticMap = new HashMap<>();

	public void set(@NotNull String name, float value) {
		statisticMap.put(name, value);
	}

	public float get(@NotNull String name) {
		if (!statisticMap.containsKey(name)) {
			return 0f;
		}
		return statisticMap.get(name);
	}

	public void increase(@NotNull String name, float amount) {
		set(name, get(name) + amount);
	}

	@Override
	public Map<String, Object> serialize() {
		return new HashMap<>(statisticMap);
	}
}
