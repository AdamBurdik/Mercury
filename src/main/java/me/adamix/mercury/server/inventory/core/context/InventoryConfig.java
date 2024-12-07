package me.adamix.mercury.server.inventory.core.context;

import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class InventoryConfig {
	private int rows;
	private Component title;

	public InventoryConfig rows(int rows) {
		this.rows = Math.max(Math.min(6, rows), 1);
		return this;
	}

	public InventoryConfig title(Component title) {
		this.title = title;
		return this;
	}

	public InventoryConfig title(String title) {
		return this.title(Component.text(title));
	}
}
