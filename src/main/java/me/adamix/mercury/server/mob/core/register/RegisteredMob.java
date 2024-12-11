package me.adamix.mercury.server.mob.core.register;

import me.adamix.mercury.server.mob.core.MercuryMob;
import org.jetbrains.annotations.NotNull;

public interface RegisteredMob {
	@NotNull MercuryMob get();
}
