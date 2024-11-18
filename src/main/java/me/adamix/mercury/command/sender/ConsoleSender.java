package me.adamix.mercury.command.sender;

import me.adamix.mercury.utils.ComponentUtils;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.command.CommandSender;
import net.minestom.server.permission.Permission;
import net.minestom.server.tag.TagHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents the console when sending a command to the server.
 */
public class ConsoleSender implements CommandSender {
	private static final ComponentLogger LOGGER = ComponentLogger.logger(net.minestom.server.command.ConsoleSender.class);

	private final Set<Permission> permissions = new CopyOnWriteArraySet<>();
	private final TagHandler tagHandler = TagHandler.newHandler();

	private final Identity identity = Identity.nil();
	private final Pointers pointers = Pointers.builder()
			.withStatic(Identity.UUID, this.identity.uuid())
			.build();

	@Override
	public void sendMessage(@NotNull String message) {
		LOGGER.info(message);
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		LOGGER.info(ComponentUtils.componentToAnsi(message));
	}

	@NotNull
	@Override
	public Set<Permission> getAllPermissions() {
		return permissions;
	}

	@Override
	public @NotNull TagHandler tagHandler() {
		return tagHandler;
	}

	@Override
	public @NotNull Identity identity() {
		return this.identity;
	}

	@Override
	public @NotNull Pointers pointers() {
		return this.pointers;
	}
}
