package me.adamix.mercury.server.dungeon.room;

import net.minestom.server.utils.Direction;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
	private final List<DungeonRoom> northRoomList = new ArrayList<>();
	private final List<DungeonRoom> southRoomList = new ArrayList<>();
	private final List<DungeonRoom> westRoomList = new ArrayList<>();
	private final List<DungeonRoom> eastRoomList = new ArrayList<>();

	public void registerRoom(DungeonRoom dungeonRoom) {
		if (dungeonRoom.northPoint() != null) {
			northRoomList.add(dungeonRoom);
		}
		if (dungeonRoom.southPoint() != null) {
			southRoomList.add(dungeonRoom);
		}
		if (dungeonRoom.westPoint() != null) {
			westRoomList.add(dungeonRoom);
		}
		if (dungeonRoom.eastPoint() != null) {
			eastRoomList.add(dungeonRoom);
		}
	}

	public List<DungeonRoom> getRooms(Direction direction) {
		if (direction.equals(Direction.NORTH)) {
			return northRoomList;
		}
		if (direction.equals(Direction.SOUTH)) {
			return southRoomList;
		}
		if (direction.equals(Direction.WEST)) {
			return westRoomList;
		}
		if (direction.equals(Direction.EAST)) {
			return eastRoomList;
		}
		return List.of();
	}
}
