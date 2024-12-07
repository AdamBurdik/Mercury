package me.adamix.mercury.server.listener.player;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.item.core.GameItem;
import me.adamix.mercury.server.item.core.ItemManager;
import me.adamix.mercury.server.player.GamePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerChangeHeldSlotListener implements EventListener<PlayerChangeHeldSlotEvent> {
	private static final Tag<UUID> tag = Tag.UUID("uniqueId");

	@Override
	public @NotNull Result run(@NotNull PlayerChangeHeldSlotEvent event) {
		GamePlayer player = GamePlayer.of(event);

		ItemStack itemStack = player.getInventory().getItemStack(event.getSlot());
		if (!itemStack.hasTag(tag)) {
			return Result.SUCCESS;
		}

		UUID uniqueId = itemStack.getTag(tag);

		ItemManager itemManager = Server.getItemManager();
		if (!itemManager.contains(uniqueId)) {
			return Result.SUCCESS;
		}
		GameItem item = itemManager.get(uniqueId);
		item.getAttributes().applyToPlayer(player);

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerChangeHeldSlotEvent> eventType() {
		return PlayerChangeHeldSlotEvent.class;
	}
}
