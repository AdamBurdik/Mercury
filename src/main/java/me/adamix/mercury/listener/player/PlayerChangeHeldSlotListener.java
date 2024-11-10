package me.adamix.mercury.listener.player;

import me.adamix.mercury.Server;
import me.adamix.mercury.item.core.GameItem;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class PlayerChangeHeldSlotListener implements EventListener<PlayerChangeHeldSlotEvent> {
	private static final Tag<String> tag = Tag.String("id");

	@Override
	public @NotNull Result run(@NotNull PlayerChangeHeldSlotEvent event) {
		GamePlayer player = GamePlayer.of(event);

		ItemStack itemStack = player.getInventory().getItemStack(event.getSlot());
		if (!itemStack.hasTag(tag)) {
			return Result.SUCCESS;
		}

		String id = itemStack.getTag(tag);
		GameItem item = Server.getItemManager().get(id);
		if (item == null) {
			return Result.SUCCESS;
		}

		item.getAttributes().applyToPlayer(player);

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerChangeHeldSlotEvent> eventType() {
		return PlayerChangeHeldSlotEvent.class;
	}
}
