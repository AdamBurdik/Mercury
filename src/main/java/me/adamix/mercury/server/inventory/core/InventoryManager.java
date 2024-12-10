package me.adamix.mercury.server.inventory.core;

import me.adamix.mercury.server.inventory.core.component.InventoryItemComponent;
import me.adamix.mercury.server.inventory.core.context.CloseContext;
import me.adamix.mercury.server.inventory.core.context.InventoryConfig;
import me.adamix.mercury.server.inventory.core.context.ItemClickContext;
import me.adamix.mercury.server.inventory.core.context.OpenContext;
import me.adamix.mercury.server.inventory.core.data.InventoryData;
import me.adamix.mercury.server.player.MercuryPlayer;
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
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class InventoryManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryManager.class);
	private final Map<UUID, MercuryInventory> playerInventoryMap = new HashMap<>();
	private final Map<UUID, InventoryData> playerInventoryData = new HashMap<>();

	public InventoryManager() {
		EventNode<InventoryEvent> node = EventNode.type("click", EventFilter.INVENTORY)
				.addListener(InventoryPreClickEvent.class, event -> {
					MercuryPlayer player = MercuryPlayer.of(event);

					if (!playerHasInventory(player.getUuid())) {
						return;
					}

					MercuryInventory inventory = getPlayerInventory(player.getUuid());

					// Handle lambda function
					InventoryData inventoryData = playerInventoryData.get(event.getPlayer().getUuid());
					InventoryItemComponent inventoryItemComponent = inventoryData.getItemComponent(event.getSlot());
					if (inventoryItemComponent == null) {
						return;
					}

					ItemClickContext clickContext = new ItemClickContext(
							inventoryItemComponent.getItemStack(),
							event.getSlot(),
							player
					);
					Consumer<ItemClickContext> consumer = inventoryItemComponent.getConsumer();
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
					MercuryPlayer player = MercuryPlayer.of(event);

					if (!playerHasInventory(player.getUuid())) {
						return;
					}

					MercuryInventory inventory = getPlayerInventory(player.getUuid());

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

	public void open(MercuryInventory mercuryInventory, MercuryPlayer player) {
		InventoryConfig inventoryConfig = new InventoryConfig();
		mercuryInventory.onInit(inventoryConfig);

		InventoryType inventoryType = InventoryType.valueOf("CHEST_" + inventoryConfig.getRows() + "_ROW");
		Inventory inventory = new Inventory(inventoryType, inventoryConfig.getTitle());

		OpenContext openContext = new OpenContext(player);
		mercuryInventory.onOpen(openContext);

		Map<Integer, InventoryItemComponent> itemComponentMap = openContext.getItemComponentList();
		itemComponentMap.forEach((slot, component) -> {
			inventory.setItemStack(
					slot,
					component.getItemStack()
			);
		});

		InventoryData inventoryData = new InventoryData(inventory, itemComponentMap);
		playerInventoryData.put(player.getUuid(), inventoryData);
		playerInventoryMap.put(player.getUuid(), mercuryInventory);

		MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
			player.openInventory(inventory);
		});

	}

	public void close(MercuryPlayer player) {
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

	public @NotNull MercuryInventory getPlayerInventory(UUID playerUniqueId) {
		if (!playerInventoryMap.containsKey(playerUniqueId)) {
			throw new RuntimeException("Player with uuid " + playerUniqueId + " does not have any inventory!");
		}

		return playerInventoryMap.get(playerUniqueId);
	}
}
