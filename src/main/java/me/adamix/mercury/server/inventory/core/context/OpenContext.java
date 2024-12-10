package me.adamix.mercury.server.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.inventory.core.component.InventoryItemComponent;
import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class OpenContext {
	private final MercuryPlayer player;
	private final Map<Integer, InventoryItemComponent> itemComponentList = new HashMap<>();

	public OpenContext(MercuryPlayer player) {
		this.player = player;
	}

	public InventoryItemComponent slot(int slot, ItemStack itemStack) {
		InventoryItemComponent component = new InventoryItemComponent(slot, itemStack);
		itemComponentList.put(slot, component);
		return component;
	}

	public @NotNull InventoryItemComponent slot(int slot, NamespaceID blueprintID) {
		Optional<MercuryItem> optionalMercuryItem = Server.getItemManager().buildItem(blueprintID);
		if (optionalMercuryItem.isEmpty()) {
			throw new RuntimeException(String.format("Cannot set slot %d to blueprint with id %s", slot, blueprintID));
		}
		return this.slot(slot, optionalMercuryItem.get().toItemStack(this.player));
	}
}
