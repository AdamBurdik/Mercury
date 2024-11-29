package me.adamix.mercury.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.Server;
import me.adamix.mercury.inventory.core.component.ItemComponent;
import me.adamix.mercury.item.core.GameItem;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;

import java.util.HashMap;
import java.util.Map;

@Getter
public class OpenContext {
	private final GamePlayer player;
	private final Map<Integer, ItemComponent> itemComponentList = new HashMap<>();

	public OpenContext(GamePlayer player) {
		this.player = player;
	}

	public ItemComponent slot(int slot, ItemStack itemStack) {
		ItemComponent component = new ItemComponent(slot, itemStack);
		itemComponentList.put(slot, component);
		return component;
	}

	public ItemComponent slot(int slot, NamespaceID blueprintID) {
		GameItem gameItem = Server.getItemManager().buildItem(blueprintID);
		return this.slot(slot, gameItem.toItemStack(this.player));
	}
}
