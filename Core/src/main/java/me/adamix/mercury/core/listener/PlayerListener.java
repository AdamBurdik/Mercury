package me.adamix.mercury.core.listener;

import me.adamix.mercury.core.MercuryCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		MercuryCore.playerManager().remove(event.getPlayer().getUniqueId());
	}
}
