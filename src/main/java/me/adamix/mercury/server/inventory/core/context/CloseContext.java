package me.adamix.mercury.server.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.server.player.MercuryPlayer;

@Getter
public class CloseContext {
	private final MercuryPlayer player;
	private boolean isCancelled = false;

	public CloseContext(MercuryPlayer player) {
		this.player = player;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
}
