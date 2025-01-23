package me.adamix.mercury.server.listener.entity;

import me.adamix.mercury.server.attribute.MercuryAttribute;
import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityAttackEvent;
import org.jetbrains.annotations.NotNull;


public class EntityAttackListener implements EventListener<EntityAttackEvent> {
	@Override
	public @NotNull Result run(@NotNull EntityAttackEvent event) {
		if (!(event.getEntity() instanceof MercuryPlayer player)) {
			return Result.INVALID;
		}

		Entity target = event.getTarget();
		if (!(target instanceof MercuryMob mob)) {
			return Result.INVALID;
		}

		double damage = player.getProfileData().getAttributes().get(MercuryAttribute.DAMAGE);
		mob.damage((long) damage);
		mob.updateName();

		return Result.SUCCESS;
	}

	@Override
	public @NotNull Class<EntityAttackEvent> eventType() {
		return EntityAttackEvent.class;
	}
}
