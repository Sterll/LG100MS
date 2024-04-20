package fr.yanis.lg100ms.statistic.event;

import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.LGMSMain;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class AloneStatsListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        LGMSMain.getInstance().getStatsUtils().addPlayer(e.getPlayer(), LGMSMain.getInstance().getDBUtils().getPlayerStats(e.getPlayer()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        LGMSMain.getInstance().getStatsUtils().removePlayer(e.getPlayer());
    }
}
