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
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class InventoryManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryManager.class);
	private final Map<UUID, GameInventory> playerInventoryMap = new HashMap<>();
	private final Map<UUID, InventoryData> playerInventoryData = new HashMap<>();

	public InventoryManager() {
		EventNode<InventoryEvent> node = EventNode.type("click", EventFilter.INVENTORY)
				.addListener(InventoryPreClickEvent.class, event -> {
					GamePlayer player = GamePlayer.of(event);

					if (!playerHasInventory(player.getUuid())) {
						return;
					}

					GameInventory inventory = getPlayerInventory(player.getUuid());

					// Handle lambda function
					InventoryData inventoryData = playerInventoryData.get(event.getPlayer().getUuid());
					ItemComponent itemComponent = inventoryData.getItemComponent(event.getSlot());
					if (itemComponent == null) {
						return;
					}

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
					GamePlayer player = GamePlayer.of(event);

					if (!playerHasInventory(player.getUuid())) {
						return;
					}

					GameInventory inventory = getPlayerInventory(player.getUuid());

					InventoryData inventoryData = playerInventoryData.get(player.getUuid());

					CloseContext closeContext = new CloseContext(player);

					inventory.onClose(closeContext);

					if (closeContext.isCancelled() && !inventoryData.isForceClose()) {
						open(inventory, player);
					} else {
						playerInventoryMap.remove(player.getUuid());
						playerInventoryData.remove(player.getUuid());
					}
				});

		MinecraftServer.getGlobalEventHandler().addChild(node);
	}

	public void open(GameInventory gameInventory, GamePlayer player) {
		InventoryConfig inventoryConfig = new InventoryConfig();
		gameInventory.onInit(inventoryConfig);

		InventoryType inventoryType = InventoryType.valueOf("CHEST_" + inventoryConfig.getRows() + "_ROW");
		Inventory inventory = new Inventory(inventoryType, inventoryConfig.getTitle());

		OpenContext openContext = new OpenContext(player);
		gameInventory.onOpen(openContext);

		Map<Integer, ItemComponent> itemComponentMap = openContext.getItemComponentList();
		itemComponentMap.forEach((slot, component) -> {
			inventory.setItemStack(
					slot,
					component.getItemStack()
			);
		});

		InventoryData inventoryData = new InventoryData(inventory, itemComponentMap);
		playerInventoryData.put(player.getUuid(), inventoryData);
		playerInventoryMap.put(player.getUuid(), gameInventory);

		MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
			player.openInventory(inventory);
		});

	}

	public void close(GamePlayer player) {
		if (!playerHasInventory(player.getUuid())) {
			return;
		}

		getPlayerInventoryData(player.getUuid()).setForceClose(true);
		player.closeInventory();
	}

	public boolean hasPlayerData(UUID playerUniqueId) {
		return playerInventoryData.containsKey(playerUniqueId);
	}

	public @NotNull InventoryData getPlayerInventoryData(UUID playerUniqueId) {
		if (!playerInventoryData.containsKey(playerUniqueId)) {
			throw new RuntimeException("Player with uuid " + playerUniqueId + " does not have inventory data!");
		}

		return playerInventoryData.get(playerUniqueId);
	}

	public boolean playerHasInventory(UUID playerUniqueId) {
		return playerInventoryMap.containsKey(playerUniqueId);
	}

	public @NotNull GameInventory getPlayerInventory(UUID playerUniqueId) {
		if (!playerInventoryMap.containsKey(playerUniqueId)) {
			throw new RuntimeException("Player with uuid " + playerUniqueId + " does not have any inventory!");
		}

		return playerInventoryMap.get(playerUniqueId);
	}
}
