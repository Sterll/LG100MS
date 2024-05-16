package fr.yanis.lg100ms.scenario;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.PlayerWWKillEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.commands.admin.Command100ms;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

@Scenario(key = AutoResizeGroup.KEY + ".display",
        configValues = {
                @IntValue(key = AutoResizeGroup.KEY + ".config.group4_playerlimit", defaultValue = 19, meetUpValue = 19, step = 1, item = UniversalMaterial.CLOCK),
                @IntValue(key = AutoResizeGroup.KEY + ".config.group3_playerlimit", defaultValue = 19, meetUpValue = 19, step = 1, item = UniversalMaterial.CLOCK),
                @IntValue(key = AutoResizeGroup.KEY + ".config.group4_villagerpercentage", defaultValue = 40, meetUpValue = 40, step = 1, item = UniversalMaterial.COMPASS),
                @IntValue(key = AutoResizeGroup.KEY + ".config.group3_villagerpercentage", defaultValue = 35, meetUpValue = 35, step = 1, item = UniversalMaterial.COMPASS),
                @IntValue(key = AutoResizeGroup.KEY + ".config.group4_werewolfpercentage", defaultValue = 25, meetUpValue = 25, step = 1, item = UniversalMaterial.DIAMOND_SWORD),
                @IntValue(key = AutoResizeGroup.KEY + ".config.group4_commonpercentage", defaultValue = 30, meetUpValue = 30, step = 1, item = UniversalMaterial.ANVIL),
                @IntValue(key = AutoResizeGroup.KEY + ".config.group3_commonpercentage", defaultValue = 25, meetUpValue = 25, step = 1, item = UniversalMaterial.ANVIL)
        })
public class AutoResizeGroup extends ListenerWerewolf {
    public static final String KEY = LGMSMain.KEY + ".scenario.auto_resize_group";

    private BukkitTask task = null;

    private static int percentageVillager;
    private static int percentageWerewolf;
    private static int percentageNeutral;

    public AutoResizeGroup(WereWolfAPI game) {
        super(game);
    }

    @EventHandler
    public void onGameStart(StartEvent e) {
       task = Bukkit.getScheduler().runTaskTimerAsynchronously(LGMSMain.getInstance(), this::verifyGroup, 0, 20 * 5);
    }

    @EventHandler
    public void onGameStop(StopEvent e){
        if(task != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onWin(WinEvent e){
        if(task != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e){
        if(LGMSMain.getInstance().isDev()){
            getGame().setGroup(1);
        }
    }

    private void verifyGroup() {

        int group4PlayerLimit = (int) getGame().getConfig().getValue(AutoResizeGroup.KEY + ".config.group4_playerlimit");
        int group3PlayerLimit = (int) getGame().getConfig().getValue(AutoResizeGroup.KEY + ".config.group3_playerlimit");

        int group4VillagerPercentage = (int) getGame().getConfig().getValue(AutoResizeGroup.KEY + ".config.group4_villagerpercentage");
        int group3VillagerPercentage = (int) getGame().getConfig().getValue(AutoResizeGroup.KEY + ".config.group3_villagerpercentage");
        int group4WerewolfPercentage = (int) getGame().getConfig().getValue(AutoResizeGroup.KEY + ".config.group4_werewolfpercentage");
        int group4CommonPercentage = (int) getGame().getConfig().getValue(AutoResizeGroup.KEY + ".config.group4_commonpercentage");
        int group3CommonPercentage = (int) getGame().getConfig().getValue(AutoResizeGroup.KEY + ".config.group3_commonpercentage");

        percentageVillager = (int) checkPercentage(Camp.VILLAGER);
        percentageWerewolf = (int) checkPercentage(Camp.WEREWOLF);
        percentageNeutral = (int) checkPercentage(Camp.NEUTRAL);
        if(LGMSMain.getInstance().isDev()){
            Bukkit.broadcastMessage("§c[DEBUG] §7Vérification du groupe en cours...");
            Bukkit.broadcastMessage("§c[DEBUG] §7Joueurs en vie: " + LGMSMain.getInstance().getPlayerAlive(getGame()).size());
            Bukkit.broadcastMessage("§c[DEBUG] §7Pourcentage de villageois: " + percentageVillager);
            Bukkit.broadcastMessage("§c[DEBUG] §7Pourcentage de loup-garou: " + percentageWerewolf);
            Bukkit.broadcastMessage("§c[DEBUG] §7Pourcentage de neutre: " + percentageNeutral);

            if (LGMSMain.getInstance().getPlayerAlive(getGame()).size() >= 5 && getGame().getGroup() != 5) {
                getGame().setGroup(5);
                return;
            }
            if (LGMSMain.getInstance().getPlayerAlive(getGame()).size() == 4 && getGame().getGroup() != 4) {
                getGame().setGroup(4);
                return;
            }
            if (LGMSMain.getInstance().getPlayerAlive(getGame()).size() <= 3 && getGame().getGroup() != 3) {
                getGame().setGroup(3);
                return;
            }
        } else {
            if (LGMSMain.getInstance().getPlayerAlive(getGame()).size() < group4PlayerLimit && LGMSMain.getInstance().getPlayerAlive(getGame()).size() >= group3PlayerLimit && getGame().getGroup() != 4) {
                getGame().setGroup(4);
                return;
            }
            if (LGMSMain.getInstance().getPlayerAlive(getGame()).size() < group3PlayerLimit && getGame().getGroup() != 3) {
                getGame().setGroup(3);
                return;
            }
        }

        if((percentageWerewolf + percentageNeutral) < group4CommonPercentage && percentageWerewolf > group4WerewolfPercentage && getGame().getGroup() != 4){
            getGame().setGroup(4);
        }
        if((percentageWerewolf + percentageNeutral) < group3CommonPercentage && getGame().getGroup() != 3){
            getGame().setGroup(3);
        }

        if(percentageVillager < group4VillagerPercentage && percentageVillager > group3VillagerPercentage && getGame().getGroup() != 4){
            getGame().setGroup(4);
        }
        if(percentageVillager < group3VillagerPercentage && getGame().getGroup() != 3){
            getGame().setGroup(3);
        }
    }

    public double checkPercentage(Camp camp){
        int playerCount = LGMSMain.getInstance().getPlayerAlive(getGame()).size();
        AtomicInteger playerInCamp = new AtomicInteger();
        LGMSMain.getInstance().getPlayerAlive(getGame()).forEach(playerWW -> {
            if(playerWW.getRole().getCamp() == camp){
                playerInCamp.getAndIncrement();
            }
        });
        return ((double) playerInCamp.get() / playerCount) * 100;
    }

    public static int getPercentageVillager() {
        return percentageVillager;
    }

    public static int getPercentageWerewolf() {
        return percentageWerewolf;
    }

    public static int getPercentageNeutral() {
        return percentageNeutral;
    }
}
