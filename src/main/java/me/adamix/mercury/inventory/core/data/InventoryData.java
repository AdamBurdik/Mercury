package me.adamix.mercury.inventory.core.data;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.inventory.core.component.ItemComponent;
import net.minestom.server.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class InventoryData {
	private final Inventory inventory;
	private final List<ItemComponent> itemComponentList;
	@Setter
	private boolean forceClose;

	public InventoryData(
			Inventory inventory,
			List<ItemComponent> itemComponentList
	) {
		this.inventory = inventory;
		this.itemComponentList = itemComponentList;
	}

	public @Nullable ItemComponent getItemComponent(int slot) {
		for (ItemComponent component : itemComponentList) {
			if (component.getSlot() == slot) {
				return component;
			}
		}
		return null;
	}
}
