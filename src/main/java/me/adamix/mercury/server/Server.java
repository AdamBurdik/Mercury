package me.adamix.mercury.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import me.adamix.mercury.bot.Bot;
import me.adamix.mercury.server.command.*;
import me.adamix.mercury.server.command.debug.*;
import me.adamix.mercury.server.command.dungeon.EnterDungeonCommand;
import me.adamix.mercury.server.command.dungeon.GenerateCommand;
import me.adamix.mercury.server.command.dungeon.RoomBuilderCommand;
import me.adamix.mercury.server.command.server.PerformanceCommand;
import me.adamix.mercury.server.command.server.StopCommand;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.dungeon.DungeonManager;
import me.adamix.mercury.server.dungeon.instance.DungeonInstanceManager;
import me.adamix.mercury.server.dungeon.room.RoomManager;
import me.adamix.mercury.server.flag.ServerFlag;
import me.adamix.mercury.server.inventory.core.InventoryManager;
import me.adamix.mercury.server.item.core.ItemManager;
import me.adamix.mercury.server.item.core.blueprint.ItemBlueprintManager;
import me.adamix.mercury.server.listener.player.*;
import me.adamix.mercury.server.mob.core.MobManager;
import me.adamix.mercury.server.mob.zombie.FriendlyZombie;
import me.adamix.mercury.server.mob.zombie.RogueZombie;
import me.adamix.mercury.server.monitor.TickMonitorManager;
import me.adamix.mercury.server.placeholder.PlaceholderManager;
import me.adamix.mercury.server.player.GamePlayer;
import me.adamix.mercury.server.player.data.PlayerDataManager;
import me.adamix.mercury.server.player.profile.ProfileDataManager;
import me.adamix.mercury.server.player.provider.GamePlayerProvider;
import me.adamix.mercury.server.task.PlayTimeTask;
import me.adamix.mercury.server.task.SaveDataTask;
import me.adamix.mercury.server.task.core.TaskManager;
import me.adamix.mercury.server.terminal.MinestomTerminal;
import me.adamix.mercury.server.toml.TomlConfiguration;
import me.adamix.mercury.server.translation.Translation;
import me.adamix.mercury.server.translation.TranslationManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

public class Server {

	public static final Pos SPAWN_LOCATION = new Pos(0.5f, 64, 0.5f);
	public static final Pos LIMBO_LOCATION = new Pos(0.5f, 0, 0.5f);
	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
	private static MinecraftServer minecraftServer;
	@Getter private static InstanceContainer mainInstance;
	@Getter private static TomlConfiguration config;
	@Getter private static MobManager mobManager;
	@Getter private static ItemBlueprintManager itemBlueprintManager;
	@Getter private static ItemManager itemManager;
	@Getter private static PlayerDataManager playerDataManager;
	@Getter private static ProfileDataManager profileDataManager;
	@Getter private static InventoryManager inventoryManager;
	@Getter private static TranslationManager translationManager;
	@Getter private static PlaceholderManager placeholderManager;
	@Getter private static TickMonitorManager tickMonitorManager;
	@Getter private static DungeonInstanceManager dungeonInstanceManager;
	@Getter private static DungeonManager dungeonManager;
	@Getter private static RoomManager roomManager;
	@Getter private static TaskManager taskManager;
	private static MongoClient mongoClient;

	/**
	 * Connects to mongo database using credentials from .env
	 */
	private static void connectToMongoDatabase() {
		Dotenv dotenv = Dotenv.configure().directory(ServerFlag.RESOURCES_PATH).load();

		String mongoUsername = dotenv.get("MONGO_USERNAME");
		String mongoPassword = dotenv.get("MONGO_PASSWORD");
		String mongoLink = dotenv.get("MONGO_LINK");

		String connectionString = "mongodb+srv://"
				+ mongoUsername
				+ ":"
				+ mongoPassword
				+ mongoLink;

		mongoClient = MongoClients.create(connectionString);
	}

	/**
	 * Start discord bot using token from .env
	 */
	private static void connectDiscordBot() {
		Dotenv dotenv = Dotenv.configure().directory(ServerFlag.RESOURCES_PATH).load();

		String botToken = dotenv.get("DISCORD_BOT_TOKEN");
		if (botToken == null) {
			LOGGER.error("Cannot found DISCORD_BOT_TOKEN in .env!");
			return;
		}
		Bot.start(botToken);
	}

	/**
	 * Load default values from configuration
	 */
	private static void loadDefaults() {
		PlayerDefaults.load(Server.file("defaults/player.toml"));
	}

	/**
	 * Initialize all managers
	 */
	private static void initManagers() {
		mobManager = new MobManager();
		itemBlueprintManager = new ItemBlueprintManager();
		itemManager = new ItemManager(itemBlueprintManager);
		MongoDatabase playerDatabase = mongoClient.getDatabase("PlayerData");

		playerDataManager = new PlayerDataManager(playerDatabase);
		profileDataManager = new ProfileDataManager(playerDatabase);
		inventoryManager = new InventoryManager();
		translationManager = new TranslationManager();
		placeholderManager = new PlaceholderManager();
		tickMonitorManager = new TickMonitorManager();
		dungeonInstanceManager = new DungeonInstanceManager();
		dungeonManager = new DungeonManager();
		roomManager = new RoomManager();
		taskManager = new TaskManager();
	}

	/**
	 * Initialize server
	 */
	private static void init() {
		LOGGER.info("Initializing mercury server {} ({})", MinecraftServer.VERSION_NAME, MinecraftServer.PROTOCOL_VERSION);

		minecraftServer = MinecraftServer.init();

		// Load server config
		File configFile = new File(ServerFlag.CONFIG_PATH);
		if (!configFile.exists()) {
			LOGGER.error("No server.toml file found at {}!", configFile.getAbsolutePath());
			return;
		}

		config = new TomlConfiguration(configFile);

		// Load discord bot config
		File botConfigFile = new File(ServerFlag.BOT_CONFIG_PATH);
		if (!configFile.exists()) {
			LOGGER.error("No bot.toml file found at {}!", configFile.getAbsolutePath());
			return;
		}

		TomlConfiguration botConfig = new TomlConfiguration(botConfigFile);

		connectToMongoDatabase();
		loadDefaults();
		initManagers();

		// Set player provider to custom one
		MinecraftServer.getConnectionManager().setPlayerProvider(new GamePlayerProvider());

		if (botConfig.getBooleanSafe("enabled")) {
			connectDiscordBot();
		}

		// Set mojang auth
		boolean mojangAuth = Boolean.TRUE.equals(config.getBooleanSafe("mojang_auth"));
		if (mojangAuth) {
			LOGGER.info("Using mojang authentication.");
			MojangAuth.init();
		}

		// Set brand name
		String brandName = config.getString("brand_name");
		if (brandName != null) {
			MinecraftServer.setBrandName(brandName);
		}

		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

		// Start tick monitor for tab list
		tickMonitorManager.start();

		// Load translations
		translationManager.loadTranslation("english.toml");
		translationManager.loadTranslation("czech.toml");

		itemBlueprintManager.registerAllItems();

		// Register default entities
		// ToDo move to configuration file
		mobManager.register(NamespaceID.from("mercury", "rogue_zombie"), RogueZombie.class);
		mobManager.register(NamespaceID.from("mercury", "friendly_zombie"), FriendlyZombie.class);

		// Register dungeon instances and dungeons
		dungeonInstanceManager.registerAllInstances();
		dungeonManager.registerAllDungeons();

		// Register event listeners
		globalEventHandler.addListener(new AsyncPlayerConfigurationListener());
		globalEventHandler.addListener(new PlayerSpawnListener());
		globalEventHandler.addListener(new PlayerDisconnectListener());
		globalEventHandler.addListener(new PlayerMoveListener());
		globalEventHandler.addListener(new PlayerChangeHeldSlotListener());
		globalEventHandler.addListener(new PlayerCommandListener());

		// Configure default instance
		InstanceManager instanceManager = MinecraftServer.getInstanceManager();
		mainInstance = instanceManager.createInstanceContainer();
		mainInstance.setChunkSupplier(LightingChunk::new);
		mainInstance.setChunkLoader(new AnvilLoader("worlds/main"));

		// Register commands
		CommandManager commandManager = MinecraftServer.getCommandManager();
		commandManager.register(new EntityCommand());
		commandManager.register(new GamemodeCommand());
		commandManager.register(new InventoryCommand());
		commandManager.register(new ItemCommand());
		commandManager.register(new ClassCommand());
		commandManager.register(new TranslationCommand());
		commandManager.register(new OldTestCommand());
		commandManager.register(new ColorTestCommand());
		commandManager.register(new TranslationTestCommand());
		commandManager.register(new StopCommand());
		commandManager.register(new PerformanceCommand());
		commandManager.register(new InventoryTestCommand());
		commandManager.register(new EntityNameTestCommand());
		commandManager.register(new DatabaseTestCommand());
		commandManager.register(new PlayTimeCommand());
		commandManager.register(new EnterDungeonCommand());
		commandManager.register(new RoomBuilderCommand());
		commandManager.register(new GenerateCommand());
		commandManager.register(new CheckPlayerDataCommand());

		// Start tasks
		taskManager.startTask(new PlayTimeTask());
		taskManager.startTask(new SaveDataTask());

		MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
			taskManager.stopAllTasks();

			LOGGER.info("Server shutting down!");
			new Thread(() -> {
				try {
					while (!MinecraftServer.isStopping()) {
						Thread.sleep(1000);
					}
					System.exit(0);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}).start();
		});
	}

	private static void start() {
		minecraftServer.start("0.0.0.0", 25565);
		MinestomTerminal.start();
	}

	public static void stop() {
		// Stop and save necessary stuff
		tickMonitorManager.stop();

		for (@NotNull Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
			GamePlayer player = GamePlayer.of(onlinePlayer);
			Translation translation = translationManager.getTranslation(player.getTranslationId());

			player.kick(
					translation.getComponent("server.stopped")
							.color(ColorPallet.NEGATIVE_RED.getColor())
			);
		}

		MinecraftServer.stopCleanly();
	}

	public static Collection<GamePlayer> getOnlinePlayers() {
		Collection<Player> playerCollection = MinecraftServer.getConnectionManager().getOnlinePlayers();
		return playerCollection.stream().map(GamePlayer::of).collect(Collectors.toList());
	}

	public static File file(String path) {
		return new File(ServerFlag.RESOURCES_PATH + path);
	}


	public static void main(String[] args) {
		init();
		start();
	}
}