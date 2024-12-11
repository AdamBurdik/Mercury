package me.adamix.mercury.server.mob.core.register;

import me.adamix.mercury.server.mob.core.MercuryMob;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public record ClassRegisteredMob(Class<? extends MercuryMob> clazz) implements RegisteredMob {

	@Override
	public @NotNull MercuryMob get() {
		try {
			Constructor<? extends MercuryMob> constructor = clazz.getConstructor();
			return constructor.newInstance();

		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
