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
import me.adamix.mercury.server.command.mob.SpawnMobCommand;
import me.adamix.mercury.server.command.npc.NPCCommand;
import me.adamix.mercury.server.command.party.PartyCommand;
import me.adamix.mercury.server.command.quest.QuestCommand;
import me.adamix.mercury.server.command.quest.QuestsCommand;
import me.adamix.mercury.server.command.server.PerformanceCommand;
import me.adamix.mercury.server.command.server.StopCommand;
import me.adamix.mercury.server.common.ColorPallet;
import me.adamix.mercury.server.defaults.PlayerDefaults;
import me.adamix.mercury.server.dungeon.DungeonManager;
import me.adamix.mercury.server.flag.ServerFlag;
import me.adamix.mercury.server.inventory.core.InventoryManager;
import me.adamix.mercury.server.item.ItemManager;
import me.adamix.mercury.server.item.blueprint.ItemBlueprintManager;
import me.adamix.mercury.server.listener.entity.EntityMoveListener;
import me.adamix.mercury.server.listener.player.*;
import me.adamix.mercury.server.mob.core.MobManager;
import me.adamix.mercury.server.mob.core.wrapper.AIWrapperManager;
import me.adamix.mercury.server.mob.zombie.FriendlyZombie;
import me.adamix.mercury.server.mob.zombie.RogueZombie;
import me.adamix.mercury.server.monitor.TickMonitorManager;
import me.adamix.mercury.server.party.PartyManager;
import me.adamix.mercury.server.placeholder.PlaceholderManager;
import me.adamix.mercury.server.player.MercuryPlayer;
import me.adamix.mercury.server.player.data.PlayerDataManager;
import me.adamix.mercury.server.player.profile.ProfileDataManager;
import me.adamix.mercury.server.player.provider.GamePlayerProvider;
import me.adamix.mercury.server.quest.core.QuestManager;
import me.adamix.mercury.server.task.PlayTimeTask;
import me.adamix.mercury.server.task.PlayerTickTask;
import me.adamix.mercury.server.task.SaveDataTask;
import me.adamix.mercury.server.task.core.TaskManager;
import me.adamix.mercury.server.terminal.MinestomTerminal;
import me.adamix.mercury.server.toml.MercuryConfiguration;
import me.adamix.mercury.server.translation.Translation;
import me.adamix.mercury.server.translation.TranslationManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class Server {

	public static final Pos SPAWN_LOCATION = new Pos(0.5f, 64, 0.5f);
	public static final Pos LIMBO_LOCATION = new Pos(0.5f, 0, 0.5f);
	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
	private static MinecraftServer minecraftServer;
	private static EventNode<Event> eventNode;
	@Getter private static InstanceContainer mainInstance;
	@Getter private static MercuryConfiguration config;
	@Getter private static MobManager mobManager;
	@Getter private static ItemBlueprintManager itemBlueprintManager;
	@Getter private static ItemManager itemManager;
	@Getter private static PlayerDataManager playerDataManager;
	@Getter private static ProfileDataManager profileDataManager;
	@Getter private static InventoryManager inventoryManager;
	@Getter private static TranslationManager translationManager;
	@Getter private static PlaceholderManager placeholderManager;
	@Getter private static TickMonitorManager tickMonitorManager;
	@Getter private static DungeonManager dungeonManager;
	@Getter private static TaskManager taskManager;
	@Getter private static AIWrapperManager aiWrapperManager;
	@Getter private static QuestManager questManager;
	@Getter private static PartyManager partyManager;
	private static MongoClient mongoClient;

	/**
	 * Connects to mongo database using credentials from .env
	 */
	private static void connectToMongoDatabase() {
		if (mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
		}
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
		if (Bot.isRunning()) {
			return;
		}
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
		itemManager = new ItemManager();
		MongoDatabase playerDatabase = mongoClient.getDatabase("PlayerData");

		playerDataManager = new PlayerDataManager(playerDatabase);
		profileDataManager = new ProfileDataManager(playerDatabase);
		inventoryManager = new InventoryManager();
		translationManager = new TranslationManager();
		placeholderManager = new PlaceholderManager();
		tickMonitorManager = new TickMonitorManager();
		dungeonManager = new DungeonManager();
		taskManager = new TaskManager();
		aiWrapperManager = new AIWrapperManager();
		questManager = new QuestManager();
		partyManager = new PartyManager();
	}

	/**
	 * Initialize server
	 */
	private static void init() {
		LOGGER.info("Initializing mercury server {} ({})", MinecraftServer.VERSION_NAME, MinecraftServer.PROTOCOL_VERSION);

		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			System.err.println("Exception in thread " + thread.getName() + ": " + throwable.getMessage());
			throwable.printStackTrace();
		});

		minecraftServer = MinecraftServer.init();

		connectToMongoDatabase();
		initManagers();
		reload();

		// Set player provider to custom one
		MinecraftServer.getConnectionManager().setPlayerProvider(new GamePlayerProvider());

		// Start tick monitor for tab list
		tickMonitorManager.start();

		// Configure default instance
		InstanceManager instanceManager = MinecraftServer.getInstanceManager();
		mainInstance = instanceManager.createInstanceContainer();
		mainInstance.setChunkSupplier(LightingChunk::new);
		mainInstance.setChunkLoader(new AnvilLoader("worlds/main"));

		registerListeners();

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

	/**
	 * Start the server
	 */
	private static void start() {
		minecraftServer.start("0.0.0.0", 25565);
		MinestomTerminal.start();
	}

	/**
	 * Stop the server and kick all players
	 */
	public static void stop() {
		// Stop and save necessary stuff
		tickMonitorManager.stop();

		for (@NotNull Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
			MercuryPlayer player = MercuryPlayer.of(onlinePlayer);
			Translation translation = translationManager.getTranslation(player.getTranslationId());

			player.kick(
					translation.getComponent("server.stopped")
							.color(ColorPallet.NEGATIVE_RED.getColor())
			);
		}

		MinecraftServer.stopCleanly();
	}

	/**
	 * Reload all configurations
	 * <br>
	 * Also called on server startup to load configuration
	 */
	public static void reload() {
		File configFile = new File(ServerFlag.CONFIG_PATH);
		if (!configFile.exists()) {
			throw new RuntimeException("No server.toml file found at " + configFile.getAbsolutePath());
		}
		config = new MercuryConfiguration(configFile);

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

		if (Bot.isRunning()) {
			Bot.stop();
		}

		// Load discord bot config
		File botConfigFile = new File(ServerFlag.BOT_CONFIG_PATH);
		if (!configFile.exists()) {
			throw new RuntimeException("No bot.toml file found at " + configFile.getAbsolutePath());
		}

		MercuryConfiguration botConfig = new MercuryConfiguration(botConfigFile);
		if (botConfig.getBooleanSafe("enabled")) {
			connectDiscordBot();
		}

		PlayerDefaults.load(Server.file("defaults/player.toml"));
		dungeonManager.registerAllDungeons();
		itemBlueprintManager.registerAllItems();

		translationManager.clearTranslations();
		translationManager.loadTranslation("english.toml");
		translationManager.loadTranslation("czech.toml");

		// Register default entities
		mobManager.registerAllMobs();
		// ToDo move to configuration file
		mobManager.register(NamespaceID.from("mercury", "rogue_zombie"), RogueZombie.class);
		mobManager.register(NamespaceID.from("mercury", "friendly_zombie"), FriendlyZombie.class);

		registerCommands();

		taskManager.stopAllTasks();
		taskManager.startTask(new PlayTimeTask());
		taskManager.startTask(new SaveDataTask());
		taskManager.startTask(new PlayerTickTask());

		questManager.init();
	}

	/**
	 * Unregister all current listeners
	 * <br>
	 * And registers all listeners
	 */
	private static void registerListeners() {
		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

		if (eventNode != null) {
			globalEventHandler.removeChild(eventNode);
			eventNode = null;
		}
		eventNode = EventNode.all("main");

		eventNode.addListener(new AsyncPlayerConfigurationListener());
		eventNode.addListener(new PlayerSpawnListener());
		eventNode.addListener(new PlayerDisconnectListener());
		eventNode.addListener(new PlayerMoveListener());
		eventNode.addListener(new PlayerChangeHeldSlotListener());
		eventNode.addListener(new PlayerCommandListener());
		eventNode.addListener(new EntityMoveListener());

		globalEventHandler.addChild(eventNode);
	}

	/**
	 * Unregister all currently registered commands
	 * <br>
	 * And register all commands
	 */
	private static void registerCommands() {
		CommandManager commandManager = MinecraftServer.getCommandManager();
		for (@NotNull Command command : commandManager.getCommands()) {
			commandManager.unregister(command);
		}

		commandManager.register(new GamemodeCommand());
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
		commandManager.register(new PlayTimeCommand());
		commandManager.register(new EnterDungeonCommand());
		commandManager.register(new CheckPlayerDataCommand());
		commandManager.register(new DebugCommand());
		commandManager.register(new SpawnMobCommand());
		commandManager.register(new InstanceConverterCommand());
		commandManager.register(new QuestCommand());
		commandManager.register(new NPCCommand());
		commandManager.register(new QuestsCommand());
		commandManager.register(new PartyCommand());
	}


	/**
	 * Retrieves collection of online players
	 * @return collectio of {@link MercuryPlayer}
	 */
	public static Collection<MercuryPlayer> getOnlinePlayers() {
		Collection<Player> playerCollection = MinecraftServer.getConnectionManager().getOnlinePlayers();
		return playerCollection.stream().map(MercuryPlayer::of).collect(Collectors.toList());
	}

	public static @Nullable MercuryPlayer getOnlinePlayerByUniqueId(@NotNull UUID uniqueId) {
		Player player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uniqueId);
		if (player == null) {
			return null;
		}
		return MercuryPlayer.of(player);
	}

	/**
	 * Retrieves specific file in resource directory
	 * @param path the relative path of file
	 * @return the {@link File} from specific path
	 */
	public static File file(String path) {
		return new File(ServerFlag.RESOURCES_PATH + path);
	}

	public static void main(String[] args) {
		init();
		start();
	}
}