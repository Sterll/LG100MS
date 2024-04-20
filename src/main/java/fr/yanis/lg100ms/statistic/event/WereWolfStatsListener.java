package fr.yanis.lg100ms.statistic.event;

import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.commands.admin.Command100ms;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class WereWolfStatsListener implements Listener {

    public BukkitTask task;
    public HashMap<IPlayerWW, Integer> players = new HashMap<>();

    @EventHandler
    public void onStop(StopEvent e){
        LGMSMain.getInstance().getGameStats().setStarted(false);
    }

    @EventHandler
    public void onWin(WinEvent e){
        LGMSMain.getInstance().getGameStats().setStarted(false);
        if(Bukkit.getPluginManager().getPlugin("WereWolfPlugin") == null) return;
        task.cancel();
        if(!Command100ms.devMode){
            if(LGMSMain.getInstance().getGameStats().getPlayers().size() < 18) return;
            if(LGMSMain.getInstance().getGameStats().getTimer() < 1800) return;
        }
        LGMSMain.getInstance().getDBUtils().createNewGameStat(LGMSMain.getInstance().getGameStats().getPlayers().size(), LGMSMain.getInstance().getGameStats().getTimer(), e.getPlayers(), e.getRole());
        for (IPlayerWW player : LGMSMain.getInstance().getGameStats().getPlayers()) {
            if(!players.containsKey(player)) LGMSMain.getInstance().getDBUtils().addPlayerStat(player, e.getPlayers().contains(player), LGMSMain.getInstance().getGameStats().getTimer(), LGMSMain.getInstance().getGameStats().getTimer());
            else LGMSMain.getInstance().getDBUtils().addPlayerStat(player, e.getPlayers().contains(player), LGMSMain.getInstance().getGameStats().getTimer(), players.get(player));
        }
        LGMSMain.getInstance().getGameStats().reset();
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent e){
        if(Bukkit.getPluginManager().getPlugin("WereWolfPlugin") == null) return;
        players.put(e.getPlayerWW(), LGMSMain.getInstance().getGameStats().getTimer());
    }

    @EventHandler
    public void onStart(StartEvent e){
        LGMSMain.getInstance().getGameStats().setStarted(true);
        if(Bukkit.getPluginManager().getPlugin("WereWolfPlugin") == null) return;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(LGMSMain.getInstance(), () -> {
            LGMSMain.getInstance().getGameStats().addTimer();
        }, 0, 20);
        e.getWereWolfAPI().getPlayersWW().forEach(playerWW -> {
            LGMSMain.getInstance().getGameStats().addPlayer(playerWW);
        });
    }

}
