package me.adamix.mercury.server.player.inventory;

import lombok.Getter;
import me.adamix.mercury.server.item.core.GameItem;
import me.adamix.mercury.server.player.GamePlayer;
import net.minestom.server.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GamePlayerInventory {
	@Getter private final Map<Integer, GameItem> items = new HashMap<>();
	private final Set<Integer> updatedSlots = new HashSet<>();

	public GamePlayerInventory() {
	}

	public void addItem(GameItem gameItem) {
		for (int i = 0; i < 36; i++) {
			if (!items.containsKey(i)) {
				setItem(i, gameItem);
				return;
			}
		}
	}

	public void setItem(int slot, GameItem gameItem) {
		items.put(slot, gameItem);
		updatedSlots.add(slot);
	}

	public void updatePlayerInventory(GamePlayer player, boolean force) {
		Set<Integer> toUpdate = force ? items.keySet() : updatedSlots;

		for (int slot : toUpdate) {
			GameItem gameItem = items.get(slot);

			ItemStack itemStack = gameItem.toItemStack(player);
			player.getInventory().setItemStack(slot, itemStack);
		}

		updatedSlots.clear();
	}
}
