package me.adamix.mercury.mob.core;

import me.adamix.mercury.Server;
import me.adamix.mercury.mob.core.attribute.MobAttribute;
import me.adamix.mercury.mob.core.attribute.MobAttributes;
import me.adamix.mercury.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class GameMob {
	private final @NotNull EntityType entityType;
	private final @NotNull String name;
	private final @NotNull MobAttributes attributes;
	private @Nullable MobBehaviour behaviour;

	public GameMob(
			@NotNull EntityType entityType,
			@NotNull String name,
			@NotNull MobAttributes attributes,
			@Nullable MobBehaviour behaviour
		) {
		this.entityType = entityType;
		this.name = name;
		this.attributes = attributes;
		this.behaviour = behaviour;
	}

	public void spawn(Pos position, Instance instance, List<GamePlayer> players) {
		EntityCreature entity = new EntityCreature(this.entityType);

		entity.setAutoViewable(false);

		entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
						.setBaseValue(
								attributes.get(MobAttribute.MOVEMENT_SPEED)
						);

		if (this.behaviour != null) {
			this.behaviour.init(entity);

			entity.addAIGroup(
					this.behaviour.getGoalSelectors(),
					this.behaviour.getTargetSelectors()
			);
		}

		entity.setInstance(instance, position);


		for (GamePlayer player : players) {
			EntityMeta entityMeta = entity.getEntityMeta();
			Component component = Server.getPlaceholderManager().parse(this.name, player);
			entityMeta.setCustomName(component);
			entityMeta.setCustomNameVisible(true);
			entity.addViewer(player);
		}
	}
}
