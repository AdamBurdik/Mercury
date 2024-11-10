package me.adamix.mercury.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.inventory.core.component.ItemComponent;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OpenContext {
	private final GamePlayer player;
	private final List<ItemComponent> itemComponentList = new ArrayList<>();

	public OpenContext(GamePlayer player) {
		this.player = player;
	}

	public ItemComponent slot(int slot, ItemStack itemStack) {
		ItemComponent component = new ItemComponent(slot, itemStack);
		itemComponentList.add(component);
		return component;
	}
}
