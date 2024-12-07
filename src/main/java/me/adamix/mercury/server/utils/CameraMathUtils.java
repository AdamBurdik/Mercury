package me.adamix.mercury.server.utils;

import net.minestom.server.coordinate.Point;

import java.util.ArrayList;
import java.util.List;

public class CameraMathUtils {
	public static Point pointBetween(Point p1, Point p2, float t) {
		return p1.add(p2.sub(p1).mul(t));
	}

	public static List<Float> createPoints(float start, float end, int pointCount) {
		List<Float> pointList = new ArrayList<>();
		float step = (end - start) / (pointCount -1);
		for (int i = 0; i < pointCount; i++) {
			pointList.add(start + i * step);
		}
		return pointList;
	}
}
