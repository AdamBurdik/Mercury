package me.adamix.mercury.server.inventory.core;

import lombok.Getter;
import me.adamix.mercury.server.inventory.core.context.CloseContext;
import me.adamix.mercury.server.inventory.core.context.InventoryConfig;
import me.adamix.mercury.server.inventory.core.context.ItemClickContext;
import me.adamix.mercury.server.inventory.core.context.OpenContext;


@Getter
public abstract class MercuryInventory {

	public void onInit(InventoryConfig config) {
		config.rows(1);
		config.title("Default Game Inventory");
	}

	public void onOpen(OpenContext ctx) {}
	public void onClick(ItemClickContext ctx) {}
	public void onClose(CloseContext ctx) {}
}
