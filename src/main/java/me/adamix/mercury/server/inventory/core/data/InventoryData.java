package me.adamix.mercury.server.inventory.core.data;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.server.inventory.core.component.InventoryItemComponent;
import net.minestom.server.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
public class InventoryData {
	private final Inventory inventory;
	private final Map<Integer, InventoryItemComponent> itemComponentMap;
	@Setter
	private boolean forceClose;

	public InventoryData(
			Inventory inventory,
			Map<Integer, InventoryItemComponent> itemComponentMap
	) {
		this.inventory = inventory;
		this.itemComponentMap = itemComponentMap;
	}

	public @Nullable InventoryItemComponent getItemComponent(int slot) {
		return itemComponentMap.get(slot);
	}
}
