package me.adamix.mercury.server.mob.core;

import lombok.Getter;
import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.event.EntityMoveEvent;
import me.adamix.mercury.server.mob.core.attribute.MobAttributeContainer;
import me.adamix.mercury.server.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.server.mob.core.component.MercuryMobComponent;
import me.adamix.mercury.server.mob.core.component.MobAttributeComponent;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
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
	private long currentHealth;
	private Pos lastPosition;
	private Vec motion;

	public MercuryMob(
			@NotNull EntityType entityType,
			@NotNull String name,
			@NotNull MercuryMobComponent[] components,
			@Nullable MobBehaviour behaviour,
			long currentHealth
		) {
		super(entityType);
		this.entityType = entityType;
		this.name = name;
		this.components = components;
		this.behaviour = behaviour;
		this.currentHealth = currentHealth;
	}

	public MercuryMob(
			@NotNull EntityType entityType,
			@NotNull String name,
			@NotNull MobAttributeContainer attributes,
			@NotNull MobBehaviour behaviour,
			long currentHealth
			) {
		this(entityType, name, new MercuryMobComponent[]{attributes.toComponent()}, behaviour, currentHealth);
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
	 * Updates entity name for all viewers
	 */
	public void updateName() {
		for (Player player : this.getViewers()) {
			this.updateName(MercuryPlayer.of(player));
		}
	}

	/**
	 * Updates entity name for specific player
	 * @param player player to update name for
	 */
	public void updateName(MercuryPlayer player) {
		Component component = Server.getPlaceholderManager().parse(this.name, player, this);

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

	@SuppressWarnings("UnstableApiUsage")
	@Override
	protected void movementTick() {
		super.movementTick();
		this.lastPosition = this.position;
		this.motion = this.position.sub(this.previousPosition).asVec();
		EventDispatcher.call(new EntityMoveEvent(this, this.position));
	}

	public double getMaxHealth() {
		MobAttributeComponent component = getComponent(MobAttributeComponent.class);
		if (component == null) {
			return 0;
		}

		Double maxHealthValue = component.get(MercuryAttribute.MAX_HEALTH);
		return maxHealthValue != null ? maxHealthValue.floatValue() : 0f;
	}

	public void damage(long amount) {
		this.currentHealth -= amount;
		if (currentHealth < 0) {
			this.kill();
		}
	}
}
