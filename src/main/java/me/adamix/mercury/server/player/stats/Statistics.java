package me.adamix.mercury.server.player.stats;

import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Statistics {
	private final EnumMap<StatisticCategory, Map<String, Double>> statisticMap = new EnumMap<>(StatisticCategory.class);

	public Map<String, Double> getCategoryMap(@NotNull StatisticCategory category) {
		if (!statisticMap.containsKey(category)) {
			statisticMap.put(category, new HashMap<>());
		}
		return statisticMap.get(category);
	}

	public void set(@NotNull StatisticCategory category, @NotNull String name, double value) {
		getCategoryMap(category).put(name, value);
	}

	public double get(@NotNull StatisticCategory category, @NotNull String name) {
		Double value = getCategoryMap(category).get(name);
		return value != null ? value : 0;
	}

	public void increase(@NotNull StatisticCategory category, @NotNull String name, float amount) {
		this.set(category, name, get(category, name) + amount);
	}


	public @NotNull Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		statisticMap.forEach((category, valueMap) -> {
			map.put(category.name(), valueMap);
		});

		return map;
	}

	@SuppressWarnings("unchecked")
	public static @NotNull Statistics deserialize(Map<String, Object> map) {
		Statistics statistics = new Statistics();

		map.forEach((categoryString, valueObject) -> {
			StatisticCategory category = StatisticCategory.valueOf(categoryString);
			Map<String, Double> valueMap = (Map<String, Double>) valueObject;
			valueMap.forEach((name, value) -> {
				statistics.set(category, name, value.floatValue());
			});
		});

		return statistics;
	}
}
