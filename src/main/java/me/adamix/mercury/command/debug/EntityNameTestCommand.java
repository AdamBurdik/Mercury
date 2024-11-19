package me.adamix.mercury.command.debug;

import me.adamix.mercury.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EntityNameTestCommand extends Command {
	public EntityNameTestCommand() {
		super("test_entity_name");

		setDefaultExecutor((sender, ctx) -> {
			if (sender instanceof GamePlayer player) {

				var entity = new Entity(EntityType.ZOMBIE);
				entity.setAutoViewable(false);
				entity.setInstance(player.getInstance(), player.getPosition());

				for (@NotNull Player nearPlayer : player.getInstance().getPlayers()) {

					var entityMeta = entity.getEntityMeta();
					entityMeta.setCustomName(
							Component.text("Hello, " + nearPlayer.getUsername() + "!")
					);
					entityMeta.setCustomNameVisible(true);
					entity.addViewer(player);
				}
			}
		});
	}
}
