package me.adamix.mercury.core;

import lombok.Data;
import me.adamix.mercury.core.placeholder.PlaceholderManager;
import me.adamix.mercury.core.player.PlayerManager;
import me.adamix.mercury.core.signal.SignalManager;
import me.adamix.mercury.core.translation.TranslationManager;
import org.jetbrains.annotations.NotNull;

@Data
public class MercuryCoreImpl {
	private final @NotNull SignalManager signalManager;
	private final @NotNull PlayerManager playerManager;
	private final @NotNull TranslationManager translationManager;
	private final @NotNull PlaceholderManager placeholderManager;

	public MercuryCoreImpl() {
		this.signalManager = new SignalManager();
		this.playerManager = new PlayerManager();
		this.translationManager = new TranslationManager();
		this.placeholderManager = new PlaceholderManager();
	}
}
