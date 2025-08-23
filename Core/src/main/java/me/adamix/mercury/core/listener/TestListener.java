package me.adamix.mercury.core.listener;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import me.adamix.mercury.core.MercuryCore;
import me.adamix.mercury.core.entity.MercuryEntity;
import me.adamix.mercury.core.player.MercuryPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TestListener implements Listener {
	@EventHandler
	public void onChunkLoad(@NotNull PlayerChunkLoadEvent event) {
		Player bukkitPlayer = event.getPlayer();
		MercuryPlayer player = MercuryPlayer.of(bukkitPlayer);

		for (Entity bukkitEntity : event.getChunk().getEntities()) {

			Optional<MercuryEntity> entityOptional = MercuryCore.entityManager().get(bukkitEntity.getUniqueId());
			if (entityOptional.isEmpty()) {
				continue;
			}
			MercuryEntity entity = entityOptional.get();

			Bukkit.getScheduler().scheduleSyncDelayedTask(MercuryCore.plugin(), () -> entity.updateName(player), 5);
		}
	}
}
