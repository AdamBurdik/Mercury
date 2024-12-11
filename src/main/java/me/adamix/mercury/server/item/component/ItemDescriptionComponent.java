package me.adamix.mercury.server.item.component;

import org.jetbrains.annotations.NotNull;

/**
 * Represents description component in item lore
 * <br>
 * Can contain multiple lines
 */
public record ItemDescriptionComponent(@NotNull String[] lines) implements MercuryItemComponent {
	public String getLine(int index) {
		return lines[index];
	}
}
