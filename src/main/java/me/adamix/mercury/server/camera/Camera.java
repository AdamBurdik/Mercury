package me.adamix.mercury.server.camera;

import me.adamix.mercury.server.camera.interpolation.IInterpolation;
import me.adamix.mercury.server.player.GamePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Camera {
	private final GamePlayer player;
	private Entity camera;
	private Task task;

	public Camera(GamePlayer player) {
		this.player = player;
	}

	public void create(Pos pos) {
		camera = new Entity(EntityType.BAT);
		camera.setInvisible(false);
		camera.setGlowing(true);
		camera.setNoGravity(true);
		camera.setInstance(player.getInstance(), pos);
	}

	public void start() {
		camera.addPassenger(player);
		player.spectate(camera);
	}

	public void stop() {
		player.stopSpectating();
		camera.removePassenger(player);
		task.cancel();
		task = null;
	}

	public void destroy() {
		camera.remove();
	}

	public void moveTo(IInterpolation interpolation, int delay) {
		interpolation.setStartingPos(this.camera.getPosition());
		List<Pos> path = interpolation.getPath();

		AtomicInteger i = new AtomicInteger();
		this.task = MinecraftServer.getSchedulerManager()
				.buildTask(() -> {
					if (i.get() >= interpolation.getStepCount()) {
						this.stop();
						return;
					}

					Pos pos = path.get(i.get());
					camera.teleport(pos);

					i.getAndIncrement();

				})
				.repeat(TaskSchedule.tick(delay))
				.schedule();
	}
}
