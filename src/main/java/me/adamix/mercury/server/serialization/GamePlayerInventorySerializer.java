package me.adamix.mercury.server.serialization;

import me.adamix.mercury.server.item.core.GameItem;
import me.adamix.mercury.server.player.inventory.GamePlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GamePlayerInventorySerializer {
	public static Map<String, Object> serialize(GamePlayerInventory inventory) {
		return Map.of(
				"items", inventory.getItems().entrySet().stream()
						.collect(Collectors.toMap(
								Map.Entry::getKey,
								entry -> entry.getValue().toString()
						))
		);
	}

	public static GamePlayerInventory deserialize(Map<String, Object> data) {
		GamePlayerInventory inventory = new GamePlayerInventory();
		Map<Integer, GameItem> itemsData = (Map<Integer, GameItem>) data.getOrDefault("items", new HashMap<>());

		itemsData.forEach((slot, item) ->
				inventory.getItems().put(slot, item)
		);

		return inventory;
	}
}
