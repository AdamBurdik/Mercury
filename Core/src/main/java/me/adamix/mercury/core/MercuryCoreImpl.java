package me.adamix.mercury.core;

import lombok.Data;
import me.adamix.mercury.core.signal.SignalManager;
import org.jetbrains.annotations.NotNull;

@Data
public class MercuryCoreImpl {
	private final @NotNull SignalManager signalManager;

	public MercuryCoreImpl() {
		this.signalManager = new SignalManager();
	}
}
