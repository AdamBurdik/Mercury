package me.adamix.mercury.server.command.dungeon;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.dungeon.room.DungeonRoom;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.hollowcube.schem.SchematicBuilder;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

public class RoomBuilderCommand extends Command {
	private static Point point1 = null;
	private static Point point2 = null;
	private static Point northPoint = null;
	private static Point southPoint = null;
	private static Point westPoint = null;
	private static Point eastPoint = null;

	public Point getTargetPoint(MercuryPlayer player) {
		return player.getTargetBlockPosition(10);
	}

	public RoomBuilderCommand() {
		super("room");

		var stringArg = ArgumentType.String("action");

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			String arg = ctx.get(stringArg);
			switch (arg) {
				case "point1":
					point1 = getTargetPoint(player);
					break;
				case "point2":
					point2 = getTargetPoint(player);
					break;
				case "north":
					northPoint = getTargetPoint(player);
					break;
				case "south":
					southPoint = getTargetPoint(player);
					break;
				case "west":
					westPoint = getTargetPoint(player);
					break;
				case "east":
					eastPoint = getTargetPoint(player);
					break;
				case "build":
					SchematicBuilder schematicBuilder = new SchematicBuilder();

					for (int y = point1.blockY(); y <= point2.blockY(); y++) {
						for (int z = point1.blockZ(); z <= point2.blockZ(); z++) {
							for (int x = point1.blockX(); x <= point2.blockX(); x++) {
								Block block = player.getInstance().getBlock(x, y, z);
								schematicBuilder.addBlock(x, y, z, block);
								player.getInstance().setBlock(x, y,z, Block.RED_STAINED_GLASS);
							}
						}
					}

					if (northPoint != null) {
						northPoint = northPoint.sub(point1);
					}
					if (southPoint != null) {
						southPoint = southPoint.sub(point1);
					}
					if (westPoint != null) {
						westPoint = westPoint.sub(point1);
					}
					if (eastPoint != null) {
						eastPoint = eastPoint.sub(point1);
					}

					DungeonRoom dungeonRoom = new DungeonRoom(
							schematicBuilder.build(),
							northPoint,
							southPoint,
							westPoint,
							eastPoint
					);

					Server.getRoomManager().registerRoom(dungeonRoom);
					break;
				case "list":
					player.sendMessage(
							String.valueOf(Server.getRoomManager().getRooms(Direction.NORTH))
					);

					player.sendMessage(
							String.valueOf(Server.getRoomManager().getRooms(Direction.SOUTH))
					);

					player.sendMessage(
							String.valueOf(Server.getRoomManager().getRooms(Direction.WEST))
					);

					player.sendMessage(
							String.valueOf(Server.getRoomManager().getRooms(Direction.EAST))
					);
					break;
			}

		}, stringArg);

	}
}
