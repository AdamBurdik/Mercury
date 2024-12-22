package me.adamix.mercury.bot;

import lombok.Getter;
import me.adamix.mercury.bot.command.ProfileInfoCommand;
import me.adamix.mercury.bot.listener.SessionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot {
	@Getter
	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	private static JDA api;
	@Getter
	private static boolean isRunning;

	public static void start(@NotNull String botToken) {
		isRunning = true;
		api = JDABuilder.createDefault(botToken)
				.addEventListeners(new SessionListener())
				.addEventListeners(new ProfileInfoCommand())
				.build();

		api.updateCommands().addCommands(
				Commands.slash("profile_info", "Gets profile data")
						.addOption(OptionType.STRING, "uuid", "Unique ID of profile")
		).queue();
	}

	public static void stop() {
		isRunning = false;
		api.shutdownNow();
	}
}
