package me.adamix.mercury.camera.interpolation;

import net.minestom.server.coordinate.Pos;

import java.util.List;

public interface IInterpolation {
	List<Pos> getPath();
	void setStartingPos(Pos startingPos);
	int getStepCount();
}
