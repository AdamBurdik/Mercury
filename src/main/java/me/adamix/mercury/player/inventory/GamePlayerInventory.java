package me.adamix.mercury.player.inventory;

import me.adamix.mercury.Server;
import me.adamix.mercury.item.core.GameItem;
import me.adamix.mercury.item.core.ItemManager;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GamePlayerInventory {
	private final Map<Integer, NamespaceID> items = new HashMap<>();
	private final Set<Integer> updatedSlots = new HashSet<>();

	public GamePlayerInventory() {
	}

	public void addItem(NamespaceID namespaceID) {
		for (int i = 0; i < 36; i++) {
			if (!items.containsKey(i)) {
				setItem(i, namespaceID);
				return;
			}
		}
	}

	public void setItem(int slot, NamespaceID namespaceID) {
		items.put(slot, namespaceID);
		updatedSlots.add(slot);
	}

	public void updatePlayerInventory(GamePlayer player, boolean force) {
		Set<Integer> toUpdate = force ? items.keySet() : updatedSlots;

		for (int slot : toUpdate) {
			NamespaceID namespaceID = items.get(slot);

			ItemManager itemManager = Server.getItemManager();
			GameItem gameItem = itemManager.get(namespaceID);
			if (gameItem == null) {
				continue;
			}

			ItemStack itemStack = gameItem.toItemStack(player);
			player.getInventory().setItemStack(slot, itemStack);
		}

		updatedSlots.clear();
	}
}
