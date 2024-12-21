package me.adamix.mercury.server.utils;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

public class PosUtils {
	public static @Nullable Pos findGroundPoint(Pos startPos, Instance instance) {
		int x = startPos.blockX();
		int z = startPos.blockZ();
		int y = startPos.blockY();

		while (y >= -64) {
			if (instance.getBlock(x, y, z).isAir()) {
				return new Pos(x, y, z, startPos.yaw(), startPos.pitch());
			}
			y--;
		}

		return null;
	}
}
