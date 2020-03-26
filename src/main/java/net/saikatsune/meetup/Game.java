package net.saikatsune.meetup;

import net.saikatsune.meetup.board.MeetupBoardProvider;
import net.saikatsune.meetup.board.SimpleBoardManager;
import net.saikatsune.meetup.commands.ForceStartCommand;
import net.saikatsune.meetup.commands.SetupCommand;
import net.saikatsune.meetup.commands.StatsCommand;
import net.saikatsune.meetup.commands.VoteCommand;
import net.saikatsune.meetup.enums.PlayerState;
import net.saikatsune.meetup.enums.Scenarios;
import net.saikatsune.meetup.gamestate.GameState;
import net.saikatsune.meetup.handler.FileHandler;
import net.saikatsune.meetup.handler.InventoryHandler;
import net.saikatsune.meetup.listener.*;
import net.saikatsune.meetup.listener.scenarios.FirelessListener;
import net.saikatsune.meetup.listener.scenarios.NoCleanListener;
import net.saikatsune.meetup.listener.scenarios.SoupListener;
import net.saikatsune.meetup.listener.scenarios.TimeBombListener;
import net.saikatsune.meetup.manager.*;
import net.saikatsune.meetup.tasks.StartingTask;
import net.saikatsune.meetup.tasks.TimeTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Game extends JavaPlugin {

    public static Game instance;

    private String prefix;

    private String mColor;
    private String sColor;

    private GameStateManager gameStateManager;
    private LocationManager locationManager;
    private GameManager gameManager;
    private WorldManager worldManager;
    private DatabaseManager databaseManager;
    private SimpleBoardManager simpleBoardManager;

    private FileHandler fileHandler;
    private InventoryHandler inventoryHandler;

    private StartingTask startingTask;
    private TimeTask timeTask;

    private ArrayList<Player> players;
    private ArrayList<Player> spectators;
    private ArrayList<UUID> loggedPlayers;

    private HashMap<UUID, Scenarios> hasVoted;

    private HashMap<Player, PlayerState> playerState;
    private HashMap<UUID, Integer> playerKills;

    private boolean preparing;
    private boolean databaseActive;

    private int startedWith;

    private File scoreboardFile;
    private FileConfiguration scoreboardConfig;

    @Override
    public void onEnable() {
        this.createConfigFile();

        instance = this;

        prefix = getConfig().getString("SETTINGS.PREFIX").replace("&", "§");

        mColor = getConfig().getString("SETTINGS.MAIN-COLOR").replace("&", "§");
        sColor = getConfig().getString("SETTINGS.SECONDARY-COLOR").replace("&", "§");

        scoreboardFile = new File(getDataFolder(), "scoreboards.yml");
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);

        if(!scoreboardFile.exists()) {
            saveResource("scoreboards.yml", false);
        }

        gameStateManager = new GameStateManager();
        locationManager = new LocationManager();
        gameManager = new GameManager();
        worldManager = new WorldManager();
        databaseManager = new DatabaseManager();

        fileHandler = new FileHandler();
        inventoryHandler = new InventoryHandler();

        startingTask = new StartingTask();
        timeTask = new TimeTask();

        players = new ArrayList<>();
        spectators = new ArrayList<>();
        loggedPlayers = new ArrayList<>();

        hasVoted = new HashMap<>();

        playerState = new HashMap<>();
        playerKills = new HashMap<>();

        preparing = true;
        databaseActive = getConfig().getBoolean("MYSQL.ENABLED");

        startedWith = 0;

        if(databaseActive) {
            try {
                databaseManager.connectToDatabase();
                getLogger().info("[MySQL] Connection to database succeeded!");
            } catch (ClassNotFoundException | SQLException e) {
                getLogger().info("[MySQL] Connection to database failed!");
            }

            try {
                databaseManager.createTable();
            } catch (SQLException e) {
                e.printStackTrace();
                getLogger().info("[MySQL] Table creation succeeded!");
            }
        }

        gameStateManager.setGameState(GameState.LOBBY);

        this.init(Bukkit.getPluginManager());

        worldManager.deleteWorld("uhc_meetup");
        worldManager.createWorld("uhc_meetup", World.Environment.NORMAL, WorldType.NORMAL);
        worldManager.createBorderLayer("uhc_meetup", getConfig().getInt("GAME.MAP-RADIUS"), 4, null);

        Bukkit.getScheduler().runTaskLater(this, new BukkitRunnable() {
            @Override
            public void run() {
                worldManager.loadWorld("uhc_meetup", getConfig().getInt("GAME.MAP-RADIUS"), 1000);
            }
        }, 20);
    }

    @Override
    public void onDisable() {
        players.clear();
        spectators.clear();

        if(databaseActive) {
            try {
                databaseManager.disconnectFromDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createConfigFile() {
        FileConfiguration config = getConfig();

        config.addDefault("SETTINGS.PREFIX", "&7[&6Meetup&7] ");
        config.addDefault("SETTINGS.MAIN-COLOR", "&6");
        config.addDefault("SETTINGS.SECONDARY-COLOR", "&f");

        config.addDefault("GAME.MIN-PLAYERS", 2);
        config.addDefault("GAME.MAP-RADIUS", 100);

        config.addDefault("MYSQL.ENABLED", true);
        config.addDefault("MYSQL.HOST", "localhost");
        config.addDefault("MYSQL.USERNAME", "root");
        config.addDefault("MYSQL.PASSWORD", "password");
        config.addDefault("MYSQL.DATABASE", "meetup");
        config.addDefault("MYSQL.PORT", 3306);

        config.options().copyDefaults(true);
        saveConfig();
    }

    private void init(PluginManager pluginManager) {
        getCommand("setup").setExecutor(new SetupCommand());
        getCommand("vote").setExecutor(new VoteCommand());
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("forcestart").setExecutor(new ForceStartCommand());

        pluginManager.registerEvents(new ConnectionListener(), this);
        pluginManager.registerEvents(new ChunkListener(), this);
        pluginManager.registerEvents(new WeatherChangeListener(), this);
        pluginManager.registerEvents(new BlockChangeListener(), this);
        pluginManager.registerEvents(new EntityDamageListener(), this);
        pluginManager.registerEvents(new PlayerInteractListener(), this);

        this.simpleBoardManager = new SimpleBoardManager(this, new MeetupBoardProvider(this));
        pluginManager.registerEvents(this.simpleBoardManager, this);

        pluginManager.registerEvents(new GlassBorderListener(), this);

        pluginManager.registerEvents(new FirelessListener(), this);
        pluginManager.registerEvents(new NoCleanListener(), this);
        pluginManager.registerEvents(new SoupListener(), this);
        pluginManager.registerEvents(new TimeBombListener(), this);

        pluginManager.registerEvents(new VoteCommand(), this);
        pluginManager.registerEvents(new StatsCommand(), this);
    }

    public void setDatabaseActive(boolean databaseActive) {
        this.databaseActive = databaseActive;
    }

    public void setPreparing(boolean preparing) {
        this.preparing = preparing;
    }

    public static Game getInstance() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getmColor() {
        return mColor;
    }

    public String getsColor() {
        return sColor;
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public HashMap<Player, PlayerState> getPlayerState() {
        return playerState;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public boolean isPreparing() {
        return preparing;
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public HashMap<UUID, Scenarios> getVoted() {
        return hasVoted;
    }

    public StartingTask getStartingTask() {
        return startingTask;
    }

    public TimeTask getTimeTask() {
        return timeTask;
    }

    public HashMap<UUID, Integer> getPlayerKills() {
        return playerKills;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public boolean isDatabaseActive() {
        return databaseActive;
    }

    public File getScoreboardFile() {
        return scoreboardFile;
    }

    public FileConfiguration getScoreboardConfig() {
        return scoreboardConfig;
    }

    public void setStartedWith(int startedWith) {
        this.startedWith = startedWith;
    }

    public int getStartedWith() {
        return startedWith;
    }

    public SimpleBoardManager getSimpleBoardManager() {
        return simpleBoardManager;
    }

    public ArrayList<UUID> getLoggedPlayers() {
        return loggedPlayers;
    }
}
