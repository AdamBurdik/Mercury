package me.adamix.mercury.bot.listener;

import me.adamix.mercury.bot.Bot;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SessionListener extends ListenerAdapter {

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		Bot.getLOGGER().info("Bot started");
	}
}
