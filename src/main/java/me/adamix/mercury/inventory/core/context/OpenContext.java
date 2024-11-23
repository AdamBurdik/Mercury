package me.adamix.mercury.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.Server;
import me.adamix.mercury.inventory.core.component.ItemComponent;
import me.adamix.mercury.item.core.GameItem;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;

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

	public ItemComponent slot(int slot, NamespaceID blueprintID) {
		GameItem gameItem = Server.getItemManager().buildItem(blueprintID);
		return this.slot(slot, gameItem.toItemStack(this.player));
	}
}
