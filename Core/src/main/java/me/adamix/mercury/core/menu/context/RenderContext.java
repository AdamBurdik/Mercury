package me.adamix.mercury.core.menu.context;

import lombok.Getter;
import me.adamix.mercury.core.item.MercuryItem;
import me.adamix.mercury.core.menu.MenuInstance;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.jetbrains.annotations.NotNull;

@Getter
public class RenderContext extends MenuContext {
	private final @NotNull MenuInstance instance;

	public RenderContext(@NotNull MercuryPlayer player, @NotNull MenuInstance instance) {
		super(player);
		this.instance = instance;
	}

	public void setSlot(int slot, @NotNull MercuryItem item) {
		instance.setSlot(slot, item);
	}
}
