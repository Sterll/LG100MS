package fr.yanis.lg100ms.scenario;

import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.yanis.lg100ms.LGMSMain;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

@Scenario(key = ExecuteCommandOnGameStop.KEY)
public class ExecuteCommandOnGameStop extends ListenerWerewolf {
    public static final String KEY = LGMSMain.KEY + ".scenario.execute_command_on_game_stop";

    public ExecuteCommandOnGameStop(WereWolfAPI game) {
        super(game);
    }

    @EventHandler
    public void onGameFinish(WinEvent e){
        System.out.println(e.getRole());
        LGMSMain.instance.getConfig().getConfigurationSection("command_victory." + e.getRole().replace("werewolf.categories.", "")).getKeys(false).forEach(key -> {
            Bukkit.getScheduler().runTaskLater(LGMSMain.instance, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), key);
            }, 20L * LGMSMain.instance.getConfig().getInt("command_victory." + e.getRole().replace("werewolf.categories.", "") + "." + key + ".delay"));
        });
    }
}
