package me.adamix.mercury.server.terminal;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.command.sender.ConsoleSender;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.translation.Translation;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import org.jetbrains.annotations.ApiStatus;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// Source: https://github.com/Minestom/Minestom/blob/area/src/main/java/net/minestom/server/terminal/MinestomTerminal.java
public class MinestomTerminal {

	private static final Logger LOGGER = LoggerFactory.getLogger(MinestomTerminal.class);
	private static final CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();
	private static final ConsoleSender consoleSender = new ConsoleSender();
	private static final String PROMPT = "> ";

	private static volatile Terminal terminal;
	private static volatile boolean running = false;

	@ApiStatus.Internal
	public static void start() {
		final Thread thread = new Thread(null, () -> {
			try {
				terminal = TerminalBuilder.terminal();

			} catch (IOException e) {
				LOGGER.error(e.toString());
			}
			LineReader reader = LineReaderBuilder.builder()
					.terminal(terminal)
					.build();
			running = true;

			while (running) {
				String command;
				try {
					command = reader.readLine(PROMPT);
					String[] split = command.split(" ");
					if (!COMMAND_MANAGER.commandExists(split[0])) {
						String translationId =  Server.getConfig().getString("console_translation_id");
						Translation translation = Server.getTranslationManager().getTranslation(translationId);

						consoleSender.sendMessage(
								translation.getComponent("command.invalid")
										.color(ColorPallet.RED.getColor())
						);

						continue;
					}

					COMMAND_MANAGER.execute(consoleSender, command);
				} catch (UserInterruptException e) {
					System.exit(0);
					return;
				} catch (EndOfFileException e) {
					return;
				}
			}
		}, "Jline");
		thread.setDaemon(true);
		thread.start();
	}

	@ApiStatus.Internal
	public static void stop() {
		running = false;
		if (terminal != null) {
			try {
				terminal.close();
			} catch (IOException e) {
				LOGGER.error(e.toString());
			}
		}
	}

}