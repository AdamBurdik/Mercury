package me.adamix.mercury.core.menu.impl;

import me.adamix.mercury.core.item.MercuryItem;
import me.adamix.mercury.core.menu.MercuryMenu;
import me.adamix.mercury.core.menu.context.InitialContext;
import me.adamix.mercury.core.menu.context.ItemClickContext;
import me.adamix.mercury.core.menu.context.RenderContext;
import me.adamix.mercury.core.menu.state.MenuState;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.jetbrains.annotations.NotNull;

public class TestMenu extends MercuryMenu {
	private static final MenuState<Integer> COUNTER = MenuState.create(0);

	@Override
	public void init(@NotNull InitialContext ctx) {
		ctx.setTitle("<rainbow>Menu for <player:name>");
		ctx.setRows(6);
	}

	@Override
	public void render(@NotNull RenderContext ctx) {
		ctx.setSlot(0, MercuryItem.of(Material.LIME_DYE).setName("++ <player:name> number: " + COUNTER.get(ctx).value()));
		ctx.setSlot(1, MercuryItem.of(Material.DIAMOND).setName("-- <player:name> number: " + COUNTER.get(ctx).value()));
	}

	@Override
	public void onItemClick(@NotNull ItemClickContext ctx) {
		int multiplier = 1;
		if (ctx.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			multiplier = 10;
		}
		final int value = multiplier;
		ctx.setCancelled(true);
		if (ctx.getSlot() == 0) {
			COUNTER.get(ctx).update(n -> n + value);
		}
		if (ctx.getSlot() == 1) {
			COUNTER.get(ctx).update(n -> n - value);
		}
		ctx.reRender();
	}

	@Override
	public void close(@NotNull MercuryPlayer player, boolean resetState) {
		super.close(player, resetState);
		if (resetState) {
			COUNTER.clear(player);
		}
	}
}
