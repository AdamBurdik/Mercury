package me.adamix.mercury.inventory.core;

import me.adamix.mercury.inventory.core.component.ItemComponent;
import me.adamix.mercury.inventory.core.context.CloseContext;
import me.adamix.mercury.inventory.core.context.InventoryConfig;
import me.adamix.mercury.inventory.core.context.ItemClickContext;
import me.adamix.mercury.inventory.core.context.OpenContext;
import me.adamix.mercury.inventory.core.data.InventoryData;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class InventoryManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryManager.class);
	private static final Tag<String> tag = Tag.String("inventory_id");
	private final Map<String, GameInventory> registeredInventoryMap = new HashMap<>();
	private final Map<UUID, InventoryData> playerInventoryData = new HashMap<>();

	public InventoryManager(GlobalEventHandler globalEventHandler) {
		EventNode<InventoryEvent> node = EventNode.type("click", EventFilter.INVENTORY)
				.addListener(InventoryPreClickEvent.class, event -> {
					GameInventory inventory = getGameInventory(event.getInventory());
					if (inventory == null) {
						return;
					}

					// Handle lambda function
					InventoryData inventoryData = playerInventoryData.get(event.getPlayer().getUuid());
					ItemComponent itemComponent = inventoryData.getItemComponent(event.getSlot());
					if (itemComponent == null) {
						return;
					}

					GamePlayer player = GamePlayer.of(event);

					ItemClickContext clickContext = new ItemClickContext(
							itemComponent.getItemStack(),
							event.getSlot(),
							player
					);
					Consumer<ItemClickContext> consumer = itemComponent.getConsumer();
					if (consumer != null) {
						consumer.accept(clickContext);
					}

					event.setCancelled(clickContext.isCancelled());

					if (clickContext.isShouldClose()) {
						close(player);
					}

					// Handle global click if event is not cancelled
					if (event.isCancelled()) {
						return;
					}

					inventory.onClick(clickContext);

					event.setCancelled(clickContext.isCancelled());

					if (clickContext.isShouldClose()) {
						close(player);
					}
				})
				.addListener(InventoryCloseEvent.class, event -> {
					GameInventory inventory = getGameInventory(event.getInventory());
					if (inventory == null) {
						return;
					}

					GamePlayer player = GamePlayer.of(event);
					InventoryData inventoryData = playerInventoryData.get(player.getUuid());

					CloseContext closeContext = new CloseContext(player);

					inventory.onClose(closeContext);

					if (closeContext.isCancelled() && !inventoryData.isForceClose()) {
						open(event.getInventory().getTag(tag), player);
					}
				});
		globalEventHandler.addChild(node);
	}

	public @Nullable GameInventory getGameInventory(@Nullable Inventory inventory) {
		if (inventory == null) {
			return null;
		}

		if (!inventory.hasTag(tag)) {
			return null;
		}

		String id = inventory.getTag(tag);
		return registeredInventoryMap.get(id);
	}

	public void register(String id, GameInventory inventory) {
		registeredInventoryMap.put(id, inventory);
	}

	public @Nullable GameInventory get(String id) {
		return registeredInventoryMap.get(id);
	}

	public void open(String id, GamePlayer player) {
		GameInventory gameInventory = get(id);
		if (gameInventory == null) {
			return;
		}

		InventoryConfig inventoryConfig = new InventoryConfig();
		gameInventory.onInit(inventoryConfig);

		InventoryType inventoryType = InventoryType.valueOf("CHEST_" + inventoryConfig.getRows() + "_ROW");

		Inventory inventory = new Inventory(inventoryType, inventoryConfig.getTitle());
		inventory.setTag(tag, id);

		OpenContext openContext = new OpenContext(player);
		gameInventory.onOpen(openContext);

		List<ItemComponent> itemComponentList = openContext.getItemComponentList();
		for (ItemComponent component : itemComponentList) {
			inventory.setItemStack(
					component.getSlot(),
					component.getItemStack()
			);
		}

		InventoryData inventoryData = new InventoryData(inventory, itemComponentList);
		playerInventoryData.put(player.getUuid(), inventoryData);

		MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
			player.openInventory(inventory);
		});
	}

	public void close(GamePlayer player) {
		Inventory inventory = player.getOpenInventory();
		GameInventory gameInventory = getGameInventory(inventory);
		if (gameInventory == null) {
			return;
		}

		playerInventoryData.get(player.getUuid()).setForceClose(true);
		player.closeInventory();
	}
}
