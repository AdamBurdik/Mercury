package me.adamix.mercury.core.utils;

import me.adamix.mercury.core.MercuryCore;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.slf4j.Logger;

public class LogUtils {
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	public static Logger getLogger() {
		String loggerName;

		Class<?> callerClass = STACK_WALKER.getCallerClass();
		if (callerClass == MercuryCore.class) {
			loggerName = "MercuryCore";
		} else {
			loggerName = "MercuryCore." + callerClass.getSimpleName();
		}

		return ComponentLogger.logger(loggerName);
	}
}
