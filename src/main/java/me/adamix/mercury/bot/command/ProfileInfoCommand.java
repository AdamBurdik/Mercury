package me.adamix.mercury.bot.command;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.attribute.PlayerAttribute;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;

public class ProfileInfoCommand extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (!event.getName().equals("profile_info")) {
			return;
		}

		String string = event.getOption("uuid").getAsString();
		UUID uuid = UUID.fromString(string);

		event.deferReply().queue();

		Server.getProfileDataManager().fetchProfileData(uuid).thenAccept((profileData) -> {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle(String.format("Profile Information (%s)", profileData.getProfileUniqueId()));
			embed.setColor(Color.GREEN);
			embed.addField("General",
					String.format(
							"> **UUID:** %s \n> **Translation:** %s",
							profileData.getPlayerUniqueId(),
							profileData.getTranslationId()
					), true);
			embed.addField("Attributes",
					String.format(
							"> **Heatlh:** %s\n> **MaxHealth:** %s\n> **Speed:** %s\n> **AttackSpeed:** %s",
							profileData.getAttributes().get(PlayerAttribute.HEALTH),
							profileData.getAttributes().get(PlayerAttribute.MAX_HEALTH),
							profileData.getAttributes().get(PlayerAttribute.MOVEMENT_SPEED),
							profileData.getAttributes().get(PlayerAttribute.ATTACK_SPEED)
					), true);
			event.getHook().sendMessageEmbeds(embed.build()).queue();
		});
	}
}
