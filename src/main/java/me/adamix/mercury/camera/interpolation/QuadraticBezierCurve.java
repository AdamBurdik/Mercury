package me.adamix.mercury.camera.interpolation;

import lombok.Getter;
import me.adamix.mercury.utils.CameraMathUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;

import java.util.ArrayList;
import java.util.List;

// Fixed target
// Interpolated rotation
// Look ahead

@Getter
public class QuadraticBezierCurve implements IInterpolation {

	private Pos startingPos;
	private final Pos endingPos;
	private final Point controlPoint;
	private final int stepCount;

	public QuadraticBezierCurve(Pos endingPos, Point controlPoint, int stepCount) {
		this.endingPos = endingPos;
		this.controlPoint = controlPoint;
		this.stepCount = stepCount;
	}

	@Override
	public List<Pos> getPath() {
		List<Pos> posList = new ArrayList<>();

		List<Float> yawPoints = CameraMathUtils.createPoints(startingPos.yaw(), endingPos.yaw(), this.stepCount);
		List<Float> pitchPoints = CameraMathUtils.createPoints(startingPos.pitch(), endingPos.pitch(), this.stepCount);
		System.out.println(pitchPoints);
		System.out.println(yawPoints);

		float amount = 1 / (float) this.stepCount;
		float t = 0;
		for (int i = 0; i < this.stepCount; i++) {
			t += amount;

			Point m1 = CameraMathUtils.pointBetween(startingPos, controlPoint, t);
			Point m2 = CameraMathUtils.pointBetween(controlPoint, endingPos, t);
			Point m3 = CameraMathUtils.pointBetween(m1, m2, t);

			Pos curvePos = Pos.fromPoint(m3)
							.withView(yawPoints.get(i), pitchPoints.get(i));

			posList.add(curvePos);
		}

		return posList;
	}

	@Override
	public void setStartingPos(Pos startingPos) {
		this.startingPos = startingPos;
	}
}
