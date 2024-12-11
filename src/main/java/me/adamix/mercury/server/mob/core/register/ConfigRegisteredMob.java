package me.adamix.mercury.server.mob.core.register;

import me.adamix.mercury.server.mob.core.MercuryMob;
import me.adamix.mercury.server.mob.core.blueprint.MercuryMobBlueprint;
import org.jetbrains.annotations.NotNull;

public record ConfigRegisteredMob(MercuryMobBlueprint mobBlueprint) implements RegisteredMob {
	@Override
	public @NotNull MercuryMob get() {
		return mobBlueprint.build();
	}
}
