package me.adamix.mercury.server.inventory.core.component;

import lombok.Getter;
import me.adamix.mercury.server.inventory.core.context.ItemClickContext;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Getter
public class InventoryItemComponent implements InventoryComponent {
	private final ItemStack itemStack;
	private final int slot;
	private @Nullable Consumer<ItemClickContext> consumer;

	public InventoryItemComponent(int slot, ItemStack itemStack) {
		this.slot = slot;
		this.itemStack = itemStack;
	}

	public InventoryItemComponent onClick(Consumer<ItemClickContext> consumer) {
		this.consumer = consumer;
		return this;
	}

	public InventoryItemComponent cancelOnClick() {
		this.consumer = (click) -> {
			click.setCancelled(true);
		};
		return this;
	}
}
