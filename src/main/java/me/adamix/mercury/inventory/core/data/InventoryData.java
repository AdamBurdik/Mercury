package me.adamix.mercury.inventory.core.data;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.inventory.core.component.ItemComponent;
import net.minestom.server.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
public class InventoryData {
	private final Inventory inventory;
	private final Map<Integer, ItemComponent> itemComponentMap;
	@Setter
	private boolean forceClose;

	public InventoryData(
			Inventory inventory,
			Map<Integer, ItemComponent> itemComponentMap
	) {
		this.inventory = inventory;
		this.itemComponentMap = itemComponentMap;
	}

	public @Nullable ItemComponent getItemComponent(int slot) {
		return itemComponentMap.get(slot);
	}
}
