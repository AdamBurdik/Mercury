package me.adamix.mercury.server.command.debug;

import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.attribute.Attribute;

public class PlayerAttributeCommand extends Command {
	public PlayerAttributeCommand() {
		super("attributes");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			sender.sendMessage(String.valueOf(player.getProfileData().getAttributes().serialize()));
			sender.sendMessage(String.valueOf(player.getAttributeValue(Attribute.ATTACK_SPEED)));
		});
	}
}
