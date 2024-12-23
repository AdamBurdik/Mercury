package me.adamix.mercury.server.npc;

import net.minestom.server.entity.*;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

public class NPC extends Entity {
	private final String username;

	private final String skinTexture;
	private final String skinSignature;

	public NPC(@NotNull String username, @Nullable String skinTexture, @Nullable String skinSignature) {
		super(EntityType.PLAYER);
		this.username = username;

		this.skinTexture = skinTexture;
		this.skinSignature = skinSignature;

		setNoGravity(true);
	}

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void updateNewViewer(@NotNull Player player) {
		var properties = new ArrayList<PlayerInfoUpdatePacket.Property>();
		if (skinTexture != null && skinSignature != null) {
			properties.add(new PlayerInfoUpdatePacket.Property("textures", skinTexture, skinSignature));
		}
		var entry = new PlayerInfoUpdatePacket.Entry(getUuid(), username, properties, false,
				0, GameMode.SURVIVAL, null, null, 5);
		player.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));

		// Spawn the player entity
		super.updateNewViewer(player);

		// Enable skin layers
		player.sendPackets(new EntityMetaDataPacket(getEntityId(), Map.of(17, Metadata.Byte((byte) 127))));
	}

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void updateOldViewer(@NotNull Player player) {
		super.updateOldViewer(player);

		player.sendPacket(new PlayerInfoRemovePacket(getUuid()));
	}
}