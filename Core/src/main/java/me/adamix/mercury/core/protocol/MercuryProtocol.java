package me.adamix.mercury.core.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import me.adamix.mercury.core.MercuryCore;
import me.adamix.mercury.core.player.MercuryPlayer;
import me.adamix.mercury.core.protocol.data.EntityMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MercuryProtocol {

	public static void sendEntityMetadata(@NotNull EntityMetadata metadata, @NotNull MercuryPlayer player) {
		MercuryCore.plugin().getLogger().info("Sending metadata " + metadata.toString() + " to " + player.getName());
		MercuryCore.plugin().getLogger().info("Id: " + metadata.getBukkitEntity().getId());
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

		packet.getIntegers().write(0, metadata.getBukkitEntity().getId());
		WrappedDataWatcher watcher = metadata.toWatcher();

		// Not really sure why this list is needed but it works.
		// https://www.spigotmc.org/threads/unable-to-modify-entity-metadata-packet-using-protocollib-1-19-3.582442/#post-4517187
		final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

		for(final WrappedWatchableObject entry : watcher.getWatchableObjects()) {
			if(entry == null) continue;

			final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
			wrappedDataValueList.add(
					new WrappedDataValue(
							watcherObject.getIndex(),
							watcherObject.getSerializer(),
							entry.getRawValue()
					)
			);
		}

		packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);
		MercuryCore.protocolManager().sendServerPacket(player.getBukkitPlayer(), packet);
	}
}
