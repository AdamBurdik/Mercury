package me.adamix.mercury.core.menu;

import lombok.Data;
import me.adamix.mercury.core.item.MercuryItem;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class MenuInstance {
	private final Map<Integer, MercuryItem> itemMap = new HashMap<>();
	private final @NotNull MercuryPlayer player;
	private final @NotNull Inventory inventory;
	private final @NotNull MercuryMenu menuSource;

	public MenuInstance(@NotNull MercuryPlayer player, @NotNull Inventory inventory, @NotNull MercuryMenu menuSource) {
		this.player = player;
		this.inventory = inventory;
		this.menuSource = menuSource;
	}

	public void setSlot(int slot, @NotNull MercuryItem item) {
		itemMap.put(slot, item);
		inventory.setItem(slot, item.toItemStack(player));
	}

	public @NotNull Optional<MercuryItem> getItem(int slot) {
		return Optional.ofNullable(itemMap.get(slot));
	}
}
