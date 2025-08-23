package me.adamix.mercury.core.listener;

import me.adamix.mercury.core.menu.MercuryMenu;
import me.adamix.mercury.core.menu.context.ItemClickContext;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player bukkitPlayer) {
			MercuryPlayer player = MercuryPlayer.of(bukkitPlayer);

			MercuryMenu.getMenu(player).ifPresent(instance -> {
				instance.getItem(event.getRawSlot()).ifPresent(item -> {

					ItemClickContext ctx = new ItemClickContext(
							player,
							item,
							event.getRawSlot(),
							event.getAction()
					);
					instance.getMenuSource().onItemClick(ctx);

					event.setCancelled(ctx.isCancelled());
				});
			});
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getReason() == InventoryCloseEvent.Reason.PLUGIN) {
			return;
		}
		if (event.getPlayer() instanceof Player bukkitPlayer) {
			MercuryPlayer player = MercuryPlayer.of(bukkitPlayer);

			MercuryMenu.getMenu(player).ifPresent(instance -> {
				instance.getMenuSource().close(player, true);
			});
		}
	}
}
