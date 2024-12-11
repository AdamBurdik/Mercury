package me.adamix.mercury.server.mob.core;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.mob.core.attribute.MobAttributes;
import me.adamix.mercury.server.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.server.mob.core.component.MercuryMobComponent;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Metadata;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MercuryMob extends EntityCreature {
	private final @NotNull EntityType entityType;
	private final @NotNull String name;
	private final @NotNull MercuryMobComponent[] components;
	private final @Nullable MobBehaviour behaviour;

	public MercuryMob(
			@NotNull EntityType entityType,
			@NotNull String name,
			@NotNull MercuryMobComponent[] components,
			@Nullable MobBehaviour behaviour
		) {
		super(entityType);
		this.entityType = entityType;
		this.name = name;
		this.components = components;
		this.behaviour = behaviour;
	}

	public MercuryMob(
			@NotNull EntityType entityType,
			@NotNull String name,
			@NotNull MobAttributes attributes,
			@NotNull MobBehaviour behaviour
			) {
		this(entityType, name, new MercuryMobComponent[]{attributes.toComponent()}, behaviour);
	}

	public boolean hasComponent(Class<? extends MercuryMobComponent> clazz) {
		return getComponent(clazz) != null;
	}

	public <T extends MercuryMobComponent> @Nullable T getComponent(Class<T> clazz) {
		for (@NotNull MercuryMobComponent itemComponent : components) {
			if (itemComponent.getClass().equals(clazz)) {
				if (clazz.isInstance(itemComponent)) {
					return clazz.cast(itemComponent);
				}
			}
		}
		return null;
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
	public void updateName(MercuryPlayer player) {
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
