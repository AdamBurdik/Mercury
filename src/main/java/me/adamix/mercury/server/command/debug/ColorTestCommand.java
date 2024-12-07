package me.adamix.mercury.server.command.debug;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;

public class ColorTestCommand extends Command {
	public ColorTestCommand() {
		super("test_color");

		setDefaultExecutor((sender, ctx) -> {
			Component component = Component.empty();

			for (int r = 0; r <= 255; r++) {
				component = component
						.append(
								Component.text("|")
										.color(TextColor.color(r, 0, 0))
						);
			}
			sender.sendMessage(component);
			for (int g  = 0; g <= 255; g++) {
				component = component
						.append(
								Component.text("|")
										.color(TextColor.color(0, g, 0))
						);
			}
			sender.sendMessage(component);
			for (int b = 0; b <= 255; b++) {
				component = component
						.append(
								Component.text("|")
										.color(TextColor.color(0, 0, b))
						);
			}
			sender.sendMessage(component);
		});
	}
}
