package me.adamix.mercury.server;

import me.adamix.mercury.server.player.stats.StatisticCategory;
import me.adamix.mercury.server.player.stats.Statistics;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsSerializationTest {

	@Test
	public void testStatisticsSerialization() {
		Statistics statistics = new Statistics();
		statistics.set(StatisticCategory.GENERAL, "test1", 69);
		statistics.set(StatisticCategory.ITEMS_CRAFTED, "test2", -50);
		statistics.set(StatisticCategory.DEATHS, "test3", 420);

		Map<String, Object> serializedStatistics = statistics.serialize();

		Statistics deserializedStatistics = Statistics.deserialize(serializedStatistics);

		assertEquals(
				statistics.get(StatisticCategory.GENERAL, "test1"),
				deserializedStatistics.get(StatisticCategory.GENERAL, "test1")
		);
		assertEquals(
				statistics.get(StatisticCategory.ITEMS_CRAFTED, "test2"),
				deserializedStatistics.get(StatisticCategory.ITEMS_CRAFTED, "test2")
		);
		assertEquals(
				statistics.get(StatisticCategory.DEATHS, "test3"),
				deserializedStatistics.get(StatisticCategory.DEATHS, "test3")
		);
	}
}
