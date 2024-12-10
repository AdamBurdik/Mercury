package me.adamix.mercury.server.listener.player;

import me.adamix.mercury.server.item.MercuryItem;
import me.adamix.mercury.server.item.component.AttributeComponent;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PlayerChangeHeldSlotListener implements EventListener<PlayerChangeHeldSlotEvent> {
	private static final Tag<UUID> tag = Tag.UUID("uniqueId");

	@Override
	public @NotNull Result run(@NotNull PlayerChangeHeldSlotEvent event) {
		MercuryPlayer player = MercuryPlayer.of(event);

		ItemStack itemStack = player.getInventory().getItemStack(event.getSlot());
		if (!itemStack.hasTag(tag)) {
			return Result.SUCCESS;
		}

		Optional<MercuryItem> optionalItem = player.getGameInventory().get(event.getSlot());
		if (optionalItem.isEmpty()) {
			return Result.SUCCESS;
		}
		MercuryItem item = optionalItem.get();
		AttributeComponent attributeComponent = item.getComponent(AttributeComponent.class);
		if (attributeComponent != null) {
			attributeComponent.applyToPlayer(player);
		}

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<PlayerChangeHeldSlotEvent> eventType() {
		return PlayerChangeHeldSlotEvent.class;
	}
}
