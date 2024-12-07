package me.adamix.mercury.server.dungeon.room;

import net.hollowcube.schem.Schematic;
import net.minestom.server.coordinate.Point;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DungeonRoom(@NotNull Schematic schematic, @Nullable Point northPoint, @Nullable Point southPoint,
                          @Nullable Point westPoint, @Nullable Point eastPoint) {

	public Point getPoint(Direction direction) {
		if (direction.equals(Direction.NORTH)) {
			return northPoint;
		}
		if (direction.equals(Direction.SOUTH)) {
			return southPoint;
		}
		if (direction.equals(Direction.WEST)) {
			return westPoint;
		}
		if (direction.equals(Direction.EAST)) {
			return eastPoint;
		}
		return null;
	}
}
