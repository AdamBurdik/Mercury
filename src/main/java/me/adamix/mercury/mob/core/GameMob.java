package me.adamix.mercury.mob.core;

import me.adamix.mercury.mob.core.attribute.MobAttribute;
import me.adamix.mercury.mob.core.attribute.MobAttributes;
import me.adamix.mercury.mob.core.behaviour.MobBehaviour;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	public void spawn(Pos position, Instance instance) {
		EntityCreature entity = new EntityCreature(this.entityType);

		entity.setCustomName(
				Component.text(name)
						.color(TextColor.color(60, 50, 40))
		);
		entity.setCustomNameVisible(true);
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
	}
}
