package me.adamix.mercury.core.menu.context;

import lombok.Getter;
import lombok.Setter;
import me.adamix.mercury.core.item.MercuryItem;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.event.inventory.InventoryAction;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ItemClickContext extends MenuContext {
	private final @NotNull MercuryPlayer player;
	private final @NotNull MercuryItem item;
	private final int slot;
	private final @NotNull InventoryAction action;
	private boolean isCancelled;

	public ItemClickContext(@NotNull MercuryPlayer player, @NotNull MercuryItem item, int slot, @NotNull InventoryAction action) {
		super(player);
		this.player = player;
		this.item = item;
		this.slot = slot;
		this.action = action;
	}
}


