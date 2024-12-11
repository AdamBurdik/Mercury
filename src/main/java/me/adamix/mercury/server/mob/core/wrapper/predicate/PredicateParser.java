package me.adamix.mercury.server.mob.core.wrapper.predicate;

import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlTable;

import java.util.function.Predicate;

public class PredicateParser {
	public static @NotNull Predicate<Entity> parse(TomlTable tomlTable) {
		if (!tomlTable.contains("type") || !tomlTable.contains("value")) {
			throw new RuntimeException("Predicate must contain type and value fields!");
		}
		String type = tomlTable.getString("type");
		String value = tomlTable.getString("value");
		if (type == null || value == null) {
			throw new RuntimeException("Predicate must contain type and value field as strings!");
		}

		return switch (type.toLowerCase()) {
			case "instanceof" -> InstanceOfPredicate.parse(value);
			default -> (e -> false);
		};
	}
}
