package me.adamix.mercury.inventory.core.component;

import lombok.Getter;
import me.adamix.mercury.inventory.core.context.ItemClickContext;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Getter
public class ItemComponent implements InventoryComponent {
	private final ItemStack itemStack;
	private final int slot;
	private @Nullable Consumer<ItemClickContext> consumer;

	public ItemComponent(int slot, ItemStack itemStack) {
		this.slot = slot;
		this.itemStack = itemStack;
	}

	public ItemComponent onClick(Consumer<ItemClickContext> consumer) {
		this.consumer = consumer;
		return this;
	}

	public ItemComponent cancelOnClick() {
		this.consumer = (click) -> {
			click.setCancelled(true);
		};
		return this;
	}
}
