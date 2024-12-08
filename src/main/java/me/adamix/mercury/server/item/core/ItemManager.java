package me.adamix.mercury.server.item.core;


import me.adamix.mercury.server.item.core.blueprint.MercuryItemBlueprint;
import me.adamix.mercury.server.item.core.blueprint.ItemBlueprintManager;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemManager {
	private final ItemBlueprintManager blueprintManager;
	// ToDo Move item map to database and add cache system
	private final Map<UUID, MercuryItem> gameItemMap = new HashMap<>();

	public ItemManager(ItemBlueprintManager blueprintManager) {
		this.blueprintManager = blueprintManager;
	}

	public MercuryItem buildItem(NamespaceID blueprintID) {
		MercuryItemBlueprint itemBlueprint = blueprintManager.get(blueprintID);

		UUID randomUniqueId = UUID.randomUUID();
		MercuryItem mercuryItem = itemBlueprint.build(randomUniqueId);
		gameItemMap.put(randomUniqueId, mercuryItem);

		return mercuryItem;
	}

	public boolean contains(UUID itemUniqueId) {
		return gameItemMap.containsKey(itemUniqueId);
	}

	public @NotNull MercuryItem get(UUID itemUniqueId) {
		if (!gameItemMap.containsKey(itemUniqueId)) {
			throw new RuntimeException("No item with id " + itemUniqueId + "!");
		}

		return gameItemMap.get(itemUniqueId);
	}

	public boolean canBuild(NamespaceID blueprintID) {
		return blueprintManager.contains(blueprintID);
	}
}
