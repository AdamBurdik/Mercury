package me.adamix.mercury.inventory.core;

import me.adamix.mercury.inventory.core.context.CloseContext;
import me.adamix.mercury.inventory.core.context.InventoryConfig;
import me.adamix.mercury.inventory.core.context.ItemClickContext;
import me.adamix.mercury.inventory.core.context.OpenContext;

public abstract class GameInventory {
	public void onInit(InventoryConfig config) {
		config.rows(1);
		config.title("Default Game Inventory");
	}

	public void onOpen(OpenContext ctx) {

	}

	public void onClick(ItemClickContext ctx) {

	}

	public void onClose(CloseContext ctx) {

	}
}
