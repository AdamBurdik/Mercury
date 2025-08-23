package me.adamix.mercury.core.menu;

import me.adamix.mercury.core.menu.context.InitialContext;
import me.adamix.mercury.core.menu.context.ItemClickContext;
import me.adamix.mercury.core.menu.context.RenderContext;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MercuryMenu {
	private static final Map<MercuryPlayer, MenuInstance> inventoryMap = new HashMap<>();

	public static @NotNull Optional<MenuInstance> getMenu(@NotNull MercuryPlayer player) {
		return Optional.ofNullable(inventoryMap.get(player));
	}

	public void open(@NotNull MercuryPlayer player) {
		InitialContext initialContext = new InitialContext(player);
		init(initialContext);

		Inventory inventory = Bukkit.createInventory(null, initialContext.getRows() * 9, initialContext.getTitle());
		MenuInstance instance = new MenuInstance(player, inventory, this);
		inventoryMap.put(player, instance);

		RenderContext renderContext = new RenderContext(player, instance);
		render(renderContext);

		player.getBukkitPlayer().openInventory(inventory);
	}

	public void init(@NotNull InitialContext ctx) {

	}

	public void render(@NotNull RenderContext ctx) {

	}

	public void onItemClick(@NotNull ItemClickContext ctx) {

	}

	public void close(@NotNull MercuryPlayer player, boolean resetState) {
		if (resetState) {
			inventoryMap.remove(player);
		}
	}
}
