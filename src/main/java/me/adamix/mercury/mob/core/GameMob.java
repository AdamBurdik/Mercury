package me.adamix.mercury.mob.core;

import lombok.Getter;
import me.adamix.mercury.Server;
import me.adamix.mercury.mob.core.attribute.MobAttribute;
import me.adamix.mercury.mob.core.attribute.MobAttributes;
import me.adamix.mercury.mob.core.behaviour.MobBehaviour;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
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

@Getter
public class GameMob extends EntityCreature {
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
		super(entityType);
		this.entityType = entityType;
		this.name = name;
		this.attributes = attributes;
		this.behaviour = behaviour;
	}

	public void applyVanillaAttributes() {
		this.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				.setBaseValue(
						attributes.get(MobAttribute.MOVEMENT_SPEED)
				);
	}

	public void applyBehaviour() {
		if (this.behaviour == null) {
			return;
		}

		addAIGroup(
				this.behaviour.getGoalSelectors(),
				this.behaviour.getTargetSelectors()
		);
	}

	public void updateName(GamePlayer player) {
		EntityMeta entityMeta = this.getEntityMeta();
		Component component = Server.getPlaceholderManager().parse(this.name, player);
		entityMeta.setCustomName(component);
		entityMeta.setCustomNameVisible(true);
	}
}
