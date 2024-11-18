package me.adamix.mercury;

import lombok.Getter;
import me.adamix.mercury.command.*;
import me.adamix.mercury.command.server.StopCommand;
import me.adamix.mercury.common.ColorPallet;
import me.adamix.mercury.configuration.Configuration;
import me.adamix.mercury.flag.ServerFlag;
import me.adamix.mercury.inventory.ProfileSelectionInventory;
import me.adamix.mercury.inventory.core.InventoryManager;
import me.adamix.mercury.item.core.ItemManager;
import me.adamix.mercury.listener.player.*;
import me.adamix.mercury.Server;
import me.adamix.mercury.mob.core.MobManager;
import me.adamix.mercury.placeholder.PlaceholderManager;
import me.adamix.mercury.player.GamePlayer;
import me.adamix.mercury.player.data.PlayerDataManager;
import me.adamix.mercury.player.provider.GamePlayerProvider;
import me.adamix.mercury.terminal.MinestomTerminal;
import me.adamix.mercury.translation.Translation;
import me.adamix.mercury.translation.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class Server {

	public static final Pos SPAWN_LOCATION = new Pos(0.5f, 64, 0.5f);
	public static final Pos LIMBO_LOCATION = new Pos(0.5f, 0, 0.5f);
	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
	private static MinecraftServer minecraftServer;
	@Getter private static InstanceContainer mainInstance;
	@Getter private static Configuration config;
	@Getter private static MobManager mobManager;
	@Getter private static ItemManager itemManager;
	@Getter private static PlayerDataManager playerDataManager;
	@Getter private static InventoryManager inventoryManager;
	@Getter private static TranslationManager translationManager;
	@Getter private static PlaceholderManager placeholderManager;

	private static TomlParseResult loadConfig() throws IOException {
		Path source = Paths.get(ServerFlag.CONFIG_PATH);
		TomlParseResult result = Toml.parse(source);
		result.errors().forEach(error -> LOGGER.error(error.toString()));

		return result;
	}

	private static void init() {

		minecraftServer = MinecraftServer.init();
		File configFile = new File(ServerFlag.CONFIG_PATH);
		if (!configFile.exists()) {
			LOGGER.error("No server.toml file found at {}!", configFile.getAbsolutePath());
			return;
		}

		config = new Configuration(configFile);

		boolean mojangAuth = Boolean.TRUE.equals(config.getBoolean("mojangAuth"));
		if (mojangAuth) {
			LOGGER.info("Using mojang authentication.");
			MojangAuth.init();
		}

		String brandName = config.getString("brandName");
		if (brandName != null) {
			MinecraftServer.setBrandName(brandName);
		}

		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

		// Initialize managers
		mobManager = new MobManager();
		itemManager = new ItemManager();
		playerDataManager = new PlayerDataManager();
		inventoryManager = new InventoryManager();
		translationManager = new TranslationManager();
		placeholderManager = new PlaceholderManager();

		// Load translations
		translationManager.loadTranslation("english.toml");
		translationManager.loadTranslation("czech.toml");

		// Register default inventories
		inventoryManager.register("profile_selection", new ProfileSelectionInventory());

		// Register all items in /resource/item directory
		itemManager.registerAllItems();

		// Register event listeners
		globalEventHandler.addListener(new AsyncPlayerConfigurationListener());
		globalEventHandler.addListener(new PlayerSpawnListener());
		globalEventHandler.addListener(new PlayerMoveListener());
		globalEventHandler.addListener(new PlayerChangeHeldSlotListener());
		globalEventHandler.addListener(new PlayerCommandListener());

		// Taken from https://github.com/AtlasEngineCa/WorldSeedEntityEngine/blob/master/src/test/java/Main.java#L132
		AtomicReference<TickMonitor> lastTick = new AtomicReference<>();
		globalEventHandler.addListener(ServerTickMonitorEvent.class, event -> lastTick.set(event.getTickMonitor()));

		MinecraftServer.getSchedulerManager().scheduleTask(() -> {
			Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
			if (players.isEmpty()) return;

			final Runtime runtime = Runtime.getRuntime();
			final TickMonitor tickMonitor = lastTick.get();
			final long ramUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;

			final Component header = Component.newline()
					.append(Component.text("RAM USAGE: " + ramUsage + " MB", NamedTextColor.GRAY).append(Component.newline())
							.append(Component.text("TICK TIME: " + MathUtils.round(tickMonitor.getTickTime(), 2) + "ms", NamedTextColor.GRAY))).append(Component.newline());

			final Component footer = Component.newline()
					.append(Component.text("          Mercury          ")
							.color(TextColor.color(57, 200, 73))
							.append(Component.newline()));

			Audiences.players().sendPlayerListHeaderAndFooter(header, footer);
		}, TaskSchedule.tick(10), TaskSchedule.tick(10));

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

		// Set player provider to custom one
		MinecraftServer.getConnectionManager().setPlayerProvider(new GamePlayerProvider());
	}

	private static void start() {
		minecraftServer.start("0.0.0.0", 25565);
		MinestomTerminal.start();
	}

	public static void stop() {
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

	public static void main(String[] args) {
		init();
		start();
	}
}