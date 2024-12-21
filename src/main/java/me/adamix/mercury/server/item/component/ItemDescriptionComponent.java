package me.adamix.mercury.server.item.component;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents description component in item lore
 * <br>
 * Can contain multiple lines
 */
public record ItemDescriptionComponent(@NotNull String[] lines) implements MercuryItemComponent {
	public String getLine(int index) {
		return lines[index];
	}

	@Override
	public String name() {
		return "itemDescriptionComponent";
	}

	@Override
	public Map<String, Object> serialize() {
		return Map.of("lines", this.lines);
	}

	public static @NotNull ItemDescriptionComponent deserialize(Map<String, Object> map) {
		Object linesObject = map.get("lines");

		if (linesObject instanceof String[] linesArray) {
			return new ItemDescriptionComponent(linesArray);
		} else {
			throw new IllegalArgumentException("Invalid type for 'lines' in the map");
		}
	}
}
