package me.adamix.mercury.server.item.component;

import org.jetbrains.annotations.NotNull;

import java.util.*;

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
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		ItemDescriptionComponent that = (ItemDescriptionComponent) object;
		return Objects.deepEquals(lines, that.lines);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(lines);
	}

	@Override
	public Map<String, Object> serialize() {
		return Map.of("lines", List.of(this.lines));
	}

	public static @NotNull ItemDescriptionComponent deserialize(Map<String, Object> map) {
		Object linesObject = map.get("lines");

		if (linesObject instanceof List<?> objectList) {
			List<String> lines = new ArrayList<>();
			for (Object object : objectList) {
				if (object instanceof String string) {
					lines.add(string);
				}
			}
			return new ItemDescriptionComponent(lines.toArray(new String[]{}));
		} else {
			throw new IllegalArgumentException("Invalid type for 'lines' in the map");
		}
	}
}
