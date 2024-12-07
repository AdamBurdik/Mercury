package me.adamix.mercury.server.command.debug;

import me.adamix.mercury.server.Server;
import me.adamix.mercury.server.player.inventory.GamePlayerInventory;
import me.adamix.mercury.server.player.profile.ProfileData;
import me.adamix.mercury.server.player.profile.ProfileDataManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.attribute.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class DatabaseTestCommand extends Command {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTestCommand.class);

	public DatabaseTestCommand() {
		super("db");

		var stringArg = ArgumentType.String("uuid1");
		var stringArg2 = ArgumentType.String("uuid2");

		setDefaultExecutor((sender, ctx) -> {
			ProfileDataManager manager = Server.getProfileDataManager();
			var retrievedData = manager.getProfileDataList(UUID.fromString("498ac192-52ea-463b-9129-c4cd53c2c057"));
			retrievedData.thenAccept(retrievedPlayerData -> {
				LOGGER.info("Retrieved data: {}", retrievedPlayerData.toString());
			});
		});

		addSyntax((sender, ctx) -> {

			Random random = new Random();
			int health = random.nextInt(50, 150);
			UUID uuid = UUID.fromString(ctx.get(stringArg));
			UUID playerUUID = UUID.fromString(ctx.get(stringArg2));

			var data = new ProfileData(
					playerUUID,
					uuid,
					"en",
					health,
					health,
					(float) Attribute.MOVEMENT_SPEED.defaultValue(),
					(float) Attribute.ATTACK_SPEED.defaultValue(),
					new GamePlayerInventory()
			);

			ProfileDataManager manager = Server.getProfileDataManager();
			LOGGER.info("saving data");
			manager.saveProfileData(data);
			var retrievedData = manager.getProfileData(data.getProfileUniqueId());
			retrievedData.thenAccept(retrievedPlayerData -> {
				LOGGER.info("Retrieved data: {}", retrievedPlayerData);
			});

		}, stringArg, stringArg2);
	}
}
