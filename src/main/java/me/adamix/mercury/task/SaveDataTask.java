package me.adamix.mercury.task;

import me.adamix.mercury.Server;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.player.data.PlayerData;
import me.adamix.mercury.player.profile.ProfileData;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SaveDataTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveDataTask.class);
	private Task task;

	public void start() {
		task = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
			LOGGER.info("Saving data");
			for (GamePlayer onlinePlayer : Server.getOnlinePlayers()) {
				PlayerData playerData = onlinePlayer.getPlayerData();
				if (playerData != null) {
					Server.getPlayerDataManager().savePlayerData(playerData);
				}
				ProfileData profileData = onlinePlayer.getProfileData();
				if (profileData != null) {
					Server.getProfileDataManager().saveProfileData(profileData);
				}
			}

		}, TaskSchedule.tick(1), TaskSchedule.minutes(5));
	}

	public void stop() {
		task.cancel();
		task = null;
	}
}
