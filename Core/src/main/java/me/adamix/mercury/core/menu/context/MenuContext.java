package me.adamix.mercury.core.menu.context;

import lombok.Getter;
import me.adamix.mercury.core.menu.MercuryMenu;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class MenuContext {
	private final @NotNull MercuryPlayer player;

	protected MenuContext(@NotNull MercuryPlayer player) {
		this.player = player;
	}

	public void reRender() {
		MercuryMenu.getMenu(player).ifPresent(instance -> {
			RenderContext ctx = new RenderContext(player, instance);
			instance.getMenuSource().render(ctx);
		});
	}

	public void reOpen() {
		MercuryMenu.getMenu(player).ifPresent(instance -> {
			instance.getPlayer().getBukkitPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
			instance.getMenuSource().close(instance.getPlayer(), false);
			instance.getMenuSource().open(instance.getPlayer());
		});
	}
}
