package me.adamix.mercury.command;

import me.adamix.mercury.camera.Camera;
import me.adamix.mercury.camera.interpolation.QuadraticBezierCurve;
import me.adamix.mercury.player.GamePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCommand extends Command {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestCommand.class);

	private Entity p2;
	private Entity controlPoint;
	private List<Entity> curveList = new ArrayList<>();

//	private void camera(Pos startingPos, Pos endingPos, float interval, float speed, GamePlayer player) {
//
//		Entity camera = new Entity(EntityType.BAT);
//		camera.setInstance(player.getInstance(), startingPos);
//		camera.setNoGravity(true);
//		player.spectate(camera);
//		camera.addPassenger(player);
//
////		List<Float> xPoints = createPoints((float) startingPos.x(), (float) endingPos.x(), 75);
////		List<Float> yPoints = createPoints((float) startingPos.y(), (float) endingPos.y(), 75);
////		List<Float> zPoints = createPoints((float) startingPos.z(), (float) endingPos.z(), 75);
////		List<Float> pitchPoints = createPoints(startingPos.pitch(), endingPos.pitch(), 75);
////		List<Float> yawPoints = createPoints(startingPos.yaw(), endingPos.yaw(), 75);
//		List<Pos> points = quadraticCurve(startingPos, endingPos, 75);
//		AtomicInteger i = new AtomicInteger();
//
//		Task[] taskHolder = new Task[1]; // Array to hold the Task reference
//		taskHolder[0] = MinecraftServer.getSchedulerManager()
//				.buildTask(() -> {
//					if (i.get() >= 100) {
//						player.stopSpectating();
//						player.teleport(camera.getPosition());
//						camera.remove();
//						taskHolder[0].cancel();
//						return;
//					}
//
//					if (i.get() < 75) {
//						Entity block = new Entity(EntityType.BLOCK_DISPLAY);
//						BlockDisplayMeta meta = (BlockDisplayMeta) block.getEntityMeta();
//						meta.setBlockState(Block.LIGHT_BLUE_CONCRETE);
//						meta.setScale(new Vec(0.2, 0.2, 0.2));
//						meta.setHasGlowingEffect(true);
//						meta.setHasNoGravity(true);
//
//						block.setInstance(player.getInstance(), points.get(i.get()));
//
//						camera.teleport(
//								new Pos(
//										points.get(i.get())
//								)
//						);
//					}
//
//					i.getAndIncrement();
//				})
//				.repeat(TaskSchedule.tick(1))
//				.schedule();
//	}

	private Point pointBetween(Point p1, Point p2, float time) {
		return p1.add(p2.sub(p1).mul(time));
	}

	private List<Point> quadraticBezierCurve(Point p1, Point p2, Point controlPoint, int count) {
		List<Point> pointList = new ArrayList<>();

		float amount = 1 / (float) count;
		float t = 0;
		for (int i = 0; i < count; i++) {
			t += amount;

			Point m1 = pointBetween(p1, controlPoint, t);
			Point m2 = pointBetween(controlPoint, p2, t);
			Point m3 = pointBetween(m1, m2, t);
			pointList.add(m3);
		}

		return pointList;
	}

	private void camera(GamePlayer player, Pos pos1, Pos pos2, Pos controlPos, Duration duration, int stepCount) {
		List<Point> pointList = quadraticBezierCurve(
				pos1,
				pos2,
				controlPos,
				stepCount
		);

		LOGGER.info(String.valueOf(pointList));
		LOGGER.info(String.valueOf(pointList.size()));

		Entity camera = new Entity(EntityType.BAT);
		camera.setNoGravity(true);
		camera.setInstance(player.getInstance(), pointList.getFirst());
		player.spectate(camera);
		camera.addPassenger(player);

		AtomicInteger i = new AtomicInteger();
		Task[] tasks = new Task[1];
		tasks[0] = MinecraftServer.getSchedulerManager()
				.buildTask(() -> {
					if (i.get() >= stepCount) {
						tasks[0].cancel();
						player.stopSpectating();
						camera.remove();
						return;
					}

					Point point = pointList.get(i.get());
					Point nextPoint;
					if (pointList.size() >= i.get() + 1) {
						nextPoint = pos2;
					} else {
						nextPoint = pointList.get(i.get() + 1);
					}
					if (nextPoint == null) {
						nextPoint = pos2;
					}

					camera.teleport(Pos.fromPoint(point).withLookAt(nextPoint));

					i.getAndIncrement();
				})
				.repeat(TaskSchedule.tick(1))
				.schedule();
	}


	public TestCommand() {
		super("test");

		var stringArg = ArgumentType.String("point");
		var duration = ArgumentType.Integer("seconds");
		var count = ArgumentType.Integer("count");

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof GamePlayer player)) {
				return;
			}

			String string = ctx.get(stringArg);

			switch (string) {
				case "pos":
					if (this.p2 != null) {
						this.p2.remove();
					}
					this.p2 = spawnPoint(player.getPosition(), player.getInstance(), true);
					break;
				case "control":
					if (this.controlPoint != null) {
						this.controlPoint.remove();
					}
					this.controlPoint = spawnPoint(player.getPosition(), player.getInstance(), true);
			}

		}, stringArg);

		addSyntax((sender, ctx) -> {
			if (!(sender instanceof GamePlayer player)) {
				return;
			}

			int delay = ctx.get(duration);
			int stepCount = ctx.get(count);

			for (Entity entity : curveList) {
				entity.remove();
			}
			curveList.clear();

			Camera camera = new Camera(player);
			camera.create(player.getPosition());
			QuadraticBezierCurve interpolation = new QuadraticBezierCurve(
					p2.getPosition(),
					controlPoint.getPosition(),
					stepCount
			);
			camera.start();
			camera.moveTo(interpolation, delay);

		}, duration, count);
	}

	private Entity spawnPoint(Pos pos, Instance instance, boolean glow) {
		Entity entity = new Entity(EntityType.BLOCK_DISPLAY);
		BlockDisplayMeta meta = (BlockDisplayMeta) entity.getEntityMeta();
		meta.setScale(new Vec(0.2f, 0.2f, 0.2f));
		meta.setHasNoGravity(true);
		meta.setBlockState(Block.RED_CONCRETE);
		meta.setHasGlowingEffect(glow);

		entity.setInstance(instance, pos);
		return entity;
	}
}
