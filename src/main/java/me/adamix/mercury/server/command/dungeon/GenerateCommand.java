package me.adamix.mercury.server.command.dungeon;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.dungeon.room.DungeonRoom;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.hollowcube.schem.Rotation;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.utils.Direction;

import java.util.List;
import java.util.Random;

public class GenerateCommand extends Command {
	public Point getAbsolute(Point starting, Point relative) {
		return starting.sub(relative);
	}

	public void generateRoom(Point origin, Direction direction, Random random, Instance instance, int i) {
		if (i < 0) {
			return;
		}
		List<DungeonRoom> possibleRooms = Server.getRoomManager().getRooms(direction);
		DungeonRoom room = possibleRooms.get(random.nextInt(0, possibleRooms.size()));

		Point directionPoint = room.getPoint(direction);

		Point point = getAbsolute(origin, directionPoint);
		Rotation rotation = Rotation.NONE;
		if (direction.equals(Direction.SOUTH)) {
			rotation = Rotation.CLOCKWISE_180;
		}
		if (direction.equals(Direction.WEST)) {
			rotation = Rotation.CLOCKWISE_270;
		}
		if (direction.equals(Direction.EAST)) {
			rotation = Rotation.CLOCKWISE_90;
		}

		RelativeBlockBatch batch = room.schematic().build(rotation, false);
		batch.apply(instance, point, () -> {
			Point d = getAbsolute(origin, room.northPoint());
			if (d != null) {
				generateRoom(d, Direction.NORTH, random, instance, i - 1);
			}
		});
	}

	public GenerateCommand() {
		super("generate", "gen");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			Point startPoint = player.getTargetBlockPosition(10);
			generateRoom(startPoint, Direction.NORTH, new Random(), player.getInstance(), 10);

//			List<DungeonRoom> possibleRooms = manager.getRooms(Direction.NORTH);
//			DungeonRoom room = possibleRooms.get(random.nextInt(0, possibleRooms.size()));
//			Point directionPoint = room.northPoint();
//
//			Point point = getAbsolute(startPoint, directionPoint);
//			RelativeBlockBatch batch = room.schematic().build(Rotation.NONE, true);
//
//			batch.apply(player.getInstance(), point, () -> {
//				System.out.println("Finished");
//			});

		});
	}
}
