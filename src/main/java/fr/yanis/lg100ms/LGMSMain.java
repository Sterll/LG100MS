package fr.yanis.lg100ms;

import fr.minuskube.inv.InventoryManager;
import fr.ph1lou.werewolfapi.GetWereWolfAPI;
import fr.ph1lou.werewolfapi.annotations.Author;
import fr.ph1lou.werewolfapi.annotations.ModuleWerewolf;
import fr.ph1lou.werewolfapi.basekeys.LoverBase;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.database.DBUtils;
import fr.yanis.lg100ms.database.DatabaseManager;
import fr.yanis.lg100ms.statistic.*;
import fr.yanis.lg100ms.statistic.event.AloneStatsListener;
import fr.yanis.lg100ms.statistic.event.WereWolfStatsListener;
import fr.yanis.lg100ms.statistic.placeholder.PlayerExpansion;
import fr.yanis.lg100ms.statistic.placeholder.ServerExpansion;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@ModuleWerewolf(key = LGMSMain.KEY + ".lg100ms", defaultLanguage = "fr", item = UniversalMaterial.BED, loreKeys = {}, authors = @Author(uuid = "fa4d1b9d-f918-47f0-86c3-25eecaf1d752", name = "Stterll"))
public final class LGMSMain extends JavaPlugin implements Listener {

    public static final String KEY = "lg100ms";
    public static LGMSMain instance;
    public GetWereWolfAPI ww;
    public DBUtils dbUtils;
    public GameStats gameStats;
    public PlayerStatsUtils playerStatsUtils;
    private SkinsRestorer skinsRestorer;
    private GameStatsUtils gameStatsUtils;
    private final InventoryManager invManager = new InventoryManager(this);
    @Override
    public void onEnable() {
        saveDefaultConfig();
        if(Bukkit.getPluginManager().getPlugin("PlaceHolderAPI") != null){
            new PlayerExpansion().register();
            new ServerExpansion().register();
        }
        if(Bukkit.getPluginManager().getPlugin("WereWolfPlugin") != null){
            GetWereWolfAPI ww = getServer().getServicesManager().load(GetWereWolfAPI.class);
            getServer().getPluginManager().registerEvents(new WereWolfStatsListener(), this);
            this.invManager.init();
        }
        if(Bukkit.getPluginManager().getPlugin("SkinsRestorer") != null){
            skinsRestorer = SkinsRestorerProvider.get();
        }
        getServer().getPluginManager().registerEvents(new AloneStatsListener(), this);
        DatabaseManager.initAllDatabaseConnection();
        dbUtils = new DBUtils();
        gameStats = new GameStats();
        playerStatsUtils = new PlayerStatsUtils();
        instance = this;
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
            try{
                // Verify if table created
                Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `game_stats` (`werewolfwinrate` INT DEFAULT '0',`werewolfwin` INT DEFAULT '0',`werewolflose` INT DEFAULT '0',`loverwinrate` INT DEFAULT '0',`loverwin` INT DEFAULT '0',`loverlose` INT DEFAULT '0',`villagerwinrate` INT DEFAULT '0',`villagerwin` INT DEFAULT '0',`villagerlose` INT DEFAULT '0',`neutralwinrate` INT DEFAULT '0',`neutralwin` INT DEFAULT '0',`neutrallose` INT DEFAULT '0');");
                PreparedStatement preparedStatement2 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `player_stats` (`uuid` VARCHAR(56),`name` VARCHAR(56),`winrate` INT,`win` INT,`lose` INT,`werewolfwinrate` INT,`werewolfwin` INT,`werewolflose` INT,`loverwinrate` INT,`loverwin` INT,`loverlose` INT,`villagerwinrate` INT,`villagerwin` INT,`villagerlose` INT,`neutralwinrate` INT,`neutralwin` INT,`neutrallose` INT,PRIMARY KEY (`uuid`));");

                preparedStatement.execute();
                preparedStatement2.execute();
                connection.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
            HashMap<String, Object> gameStats = dbUtils.getGameStats();
            gameStatsUtils = new GameStatsUtils(gameStats);
        }, 20L * 2);
    }

    @Override
    public void onDisable() {

    }

    public GetWereWolfAPI getWereWolfAPI() {
        return ww;
    }
    public static LGMSMain getInstance() {
        return instance;
    }
    public DBUtils getDBUtils() {
        return dbUtils;
    }

    public GameStats getGameStats() {
        return gameStats;
    }

    public ArrayList<IPlayerWW> getPlayerAlive(WereWolfAPI game){
        ArrayList<IPlayerWW> players = new ArrayList<>();
        for(IPlayerWW playerWW :game.getPlayersWW()){
            if(playerWW.isState(StatePlayer.ALIVE)){
                players.add(playerWW);
            }
        }
        return players;
    }

    public SkinsRestorer getSkinsRestorer() {
        return skinsRestorer;
    }

    public PlayerStatsUtils getStatsUtils() {
        return playerStatsUtils;
    }

    public GameStatsUtils getGameStatsUtils() {
        return gameStatsUtils;
    }

    public InventoryManager getInvManager() {
        return invManager;
    }
}
