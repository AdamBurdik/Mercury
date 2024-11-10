package me.adamix.mercury.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.item.ItemStack;

@Getter
public class ItemClickContext {
	private final ItemStack itemStack;
	private final int slot;
	private final GamePlayer player;
	private boolean isCancelled = false;
	private boolean shouldClose = false;

	public ItemClickContext(ItemStack itemStack, int slot, GamePlayer player) {
		this.itemStack = itemStack;
		this.slot = slot;
		this.player = player;
	}

	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}

	public void close() {
		this.shouldClose = true;
	}
}
