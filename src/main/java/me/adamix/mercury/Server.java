package me.adamix.mercury;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import me.adamix.mercury.command.*;
import me.adamix.mercury.command.debug.*;
import me.adamix.mercury.command.server.PerformanceCommand;
import me.adamix.mercury.command.server.StopCommand;
import me.adamix.mercury.common.ColorPallet;
import me.adamix.mercury.configuration.Configuration;
import me.adamix.mercury.flag.ServerFlag;
import me.adamix.mercury.inventory.core.InventoryManager;
import me.adamix.mercury.item.core.ItemManager;
import me.adamix.mercury.item.core.blueprint.ItemBlueprintManager;
import me.adamix.mercury.listener.player.*;
import me.adamix.mercury.mob.core.MobManager;
import me.adamix.mercury.mob.zombie.FriendlyZombie;
import me.adamix.mercury.mob.zombie.RogueZombie;
import me.adamix.mercury.monitor.TickMonitorManager;
import me.adamix.mercury.placeholder.PlaceholderManager;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.player.data.PlayerDataManager;
import me.adamix.mercury.player.provider.GamePlayerProvider;
import me.adamix.mercury.terminal.MinestomTerminal;
import me.adamix.mercury.translation.Translation;
import me.adamix.mercury.translation.TranslationManager;
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
	@Getter private static Configuration config;
	@Getter private static MobManager mobManager;
	@Getter private static ItemBlueprintManager itemBlueprintManager;
	@Getter private static ItemManager itemManager;
	@Getter private static PlayerDataManager playerDataManager;
	@Getter private static InventoryManager inventoryManager;
	@Getter private static TranslationManager translationManager;
	@Getter private static PlaceholderManager placeholderManager;
	@Getter private static TickMonitorManager tickMonitorManager;
	private static MongoClient mongoClient;

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

	private static void init() {
		LOGGER.info("Initializing mercury server {} ({})", MinecraftServer.VERSION_NAME, MinecraftServer.PROTOCOL_VERSION);

		minecraftServer = MinecraftServer.init();
		File configFile = new File(ServerFlag.CONFIG_PATH);
		if (!configFile.exists()) {
			LOGGER.error("No server.toml file found at {}!", configFile.getAbsolutePath());
			return;
		}

		config = new Configuration(configFile);

		boolean mojangAuth = Boolean.TRUE.equals(config.getBoolean("mojang_auth"));
		if (mojangAuth) {
			LOGGER.info("Using mojang authentication.");
			MojangAuth.init();
		}

		String brandName = config.getString("brand_name");
		if (brandName != null) {
			MinecraftServer.setBrandName(brandName);
		}

		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

		// Connect to mongo cluster
		connectToMongoDatabase();

		// Initialize managers
		mobManager = new MobManager();
		itemBlueprintManager = new ItemBlueprintManager();
		itemManager = new ItemManager(itemBlueprintManager);
		playerDataManager = new PlayerDataManager(mongoClient);
		inventoryManager = new InventoryManager();
		translationManager = new TranslationManager();
		placeholderManager = new PlaceholderManager();
		tickMonitorManager = new TickMonitorManager();

		// Start tick monitor
		tickMonitorManager.start();

		// Load translations
		translationManager.loadTranslation("english.toml");
		translationManager.loadTranslation("czech.toml");

		// Register all items in /resource/item directory
		itemBlueprintManager.registerAllItems();

		// Register default entities
		mobManager.register(NamespaceID.from("mercury", "rogue_zombie"), RogueZombie.class);
		mobManager.register(NamespaceID.from("mercury", "friendly_zombie"), FriendlyZombie.class);

		// Register event listeners
		globalEventHandler.addListener(new AsyncPlayerConfigurationListener());
		globalEventHandler.addListener(new PlayerSpawnListener());
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
		commandManager.register(new TestCommand());
		commandManager.register(new ColorTestCommand());
		commandManager.register(new TranslationTestCommand());
		commandManager.register(new StopCommand());
		commandManager.register(new PerformanceCommand());
		commandManager.register(new InventoryTestCommand());
		commandManager.register(new EntityNameTestCommand());
		commandManager.register(new DatabaseTestCommand());

		// Set player provider to custom one
		MinecraftServer.getConnectionManager().setPlayerProvider(new GamePlayerProvider());
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


	public static void main(String[] args) {
		init();
		start();
	}
}