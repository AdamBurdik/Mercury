package me.adamix.mercury.server.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.item.ItemStack;

@Getter
public class ItemClickContext {
	private final ItemStack itemStack;
	private final int slot;
	private final MercuryPlayer player;
	private boolean isCancelled = false;
	private boolean shouldClose = false;

	public ItemClickContext(ItemStack itemStack, int slot, MercuryPlayer player) {
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
