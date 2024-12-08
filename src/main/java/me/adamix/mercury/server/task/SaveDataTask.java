package me.adamix.mercury.server.task;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.data.PlayerData;
import me.adamix.mercury.server.player.profile.ProfileData;
import me.adamix.mercury.server.task.core.MercuryTask;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SaveDataTask implements MercuryTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveDataTask.class);
	private Task task;

	@Override
	public void start() {
		task = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
			LOGGER.info("Saving data");
			for (MercuryPlayer onlinePlayer : Server.getOnlinePlayers()) {
				PlayerData playerData = onlinePlayer.getPlayerData();
				Server.getPlayerDataManager().savePlayerData(playerData);

				ProfileData profileData = onlinePlayer.getProfileData();
				Server.getProfileDataManager().saveProfileData(profileData);
			}

		}, TaskSchedule.tick(1), TaskSchedule.minutes(5));
	}

	@Override
	public void cancel() {
		task.cancel();
		task = null;
	}
}
