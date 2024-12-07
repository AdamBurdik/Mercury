package me.adamix.mercury.server.mob.core;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.mob.core.attribute.MobAttribute;
import me.adamix.mercury.server.mob.core.attribute.MobAttributes;
import me.adamix.mercury.server.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.server.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GameMob extends EntityCreature {
	private final @NotNull EntityType entityType;
	private final @NotNull String name;
	private final @NotNull MobAttributes mobAttributes;
	private final @Nullable MobBehaviour behaviour;

	public GameMob(
			@NotNull EntityType entityType,
			@NotNull String name,
			@NotNull MobAttributes mobAttributes,
			@Nullable MobBehaviour behaviour
		) {
		super(entityType);
		this.entityType = entityType;
		this.name = name;
		this.mobAttributes = mobAttributes;
		this.behaviour = behaviour;
	}

	/**
	 * Applies attributes which entity should include by default
	 */
	public void applyVanillaAttributes() {
		this.getAttribute(Attribute.MOVEMENT_SPEED)
				.setBaseValue(
						mobAttributes.get(MobAttribute.MOVEMENT_SPEED)
				);
	}

	/**
	 * Applies goal and target selectors from behaviour to entity
	 */
	public void applyBehaviour() {
		if (this.behaviour == null) {
			return;
		}

		addAIGroup(
				this.behaviour.getGoalSelectors(),
				this.behaviour.getTargetSelectors()
		);
	}

	/**
	 * Updates entity entity for specific player, supports placeholders
	 * @param player player to update name for
	 */
	public void updateName(GamePlayer player) {
		Component component = Server.getPlaceholderManager().parse(this.name, player);

		// Create copy of original immutable metadata map
		EntityMetaDataPacket metaDataPacket = this.getMetadataPacket();
		Map<Integer, Metadata.Entry<?>> entries = new HashMap<>(metaDataPacket.entries());

		entries.put(2, Metadata.OptChat(component));

		EntityMetaDataPacket packet = new EntityMetaDataPacket(
				this.getEntityId(),
				entries
		);

		player.sendPacket(packet);
	}
}
