package me.adamix.mercury.server.player.inventory;

import lombok.Getter;
import me.adamix.mercury.server.item.core.MercuryItem;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GamePlayerInventory {
	@Getter private final Map<Integer, MercuryItem> items = new HashMap<>();
	private final Set<Integer> updatedSlots = new HashSet<>();

	public GamePlayerInventory() {
	}

	public void addItem(MercuryItem mercuryItem) {
		for (int i = 0; i < 36; i++) {
			if (!items.containsKey(i)) {
				setItem(i, mercuryItem);
				return;
			}
		}
	}

	public void setItem(int slot, MercuryItem mercuryItem) {
		items.put(slot, mercuryItem);
		updatedSlots.add(slot);
	}

	public void updatePlayerInventory(MercuryPlayer player, boolean force) {
		Set<Integer> toUpdate = force ? items.keySet() : updatedSlots;

		for (int slot : toUpdate) {
			MercuryItem mercuryItem = items.get(slot);

			ItemStack itemStack = mercuryItem.toItemStack(player);
			player.getInventory().setItemStack(slot, itemStack);
		}

		updatedSlots.clear();
	}
}
