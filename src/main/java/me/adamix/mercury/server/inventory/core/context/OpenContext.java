package me.adamix.mercury.server.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.core.component.ItemComponent;
import me.adamix.mercury.server.item.core.MercuryItem;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;

import java.util.HashMap;
import java.util.Map;

@Getter
public class OpenContext {
	private final MercuryPlayer player;
	private final Map<Integer, ItemComponent> itemComponentList = new HashMap<>();

	public OpenContext(MercuryPlayer player) {
		this.player = player;
	}

	public ItemComponent slot(int slot, ItemStack itemStack) {
		ItemComponent component = new ItemComponent(slot, itemStack);
		itemComponentList.put(slot, component);
		return component;
	}

	public ItemComponent slot(int slot, NamespaceID blueprintID) {
		MercuryItem mercuryItem = Server.getItemManager().buildItem(blueprintID);
		return this.slot(slot, mercuryItem.toItemStack(this.player));
	}
}
