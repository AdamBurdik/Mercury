package me.adamix.mercury.server.inventory.core.context;

import lombok.Getter;
import me.adamix.mercury.server.player.GamePlayer;

@Getter
public class CloseContext {
	private final GamePlayer player;
	private boolean isCancelled = false;

	public CloseContext(GamePlayer player) {
		this.player = player;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
}
