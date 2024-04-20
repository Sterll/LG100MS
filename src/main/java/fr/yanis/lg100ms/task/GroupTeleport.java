package fr.yanis.lg100ms.task;

import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.scenario.NoCamp;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class GroupTeleport extends BukkitRunnable {

    private int timer;
    private IPlayerWW playerWW;
    private NoCamp noCamp;

    public GroupTeleport(int timer, IPlayerWW playerWW, NoCamp noCamp){
        this.timer = timer;
        this.playerWW = playerWW;
        this.noCamp = noCamp;
    }

    @Override
    public void run() {
        if(timer == 0){
            if(playerWW.isState(StatePlayer.JUDGEMENT)) cancel();
            if(noCamp.getCompteur().get(playerWW) >= noCamp.getGame().getConfig().getValue(NoCamp.KEY + ".config.sendtitle")){
                noCamp.createGroupOfPlayer(playerWW, noCamp.getGame().getConfig().getValue(NoCamp.KEY + ".config.rayonAttacked"));
                if(noCamp.getGroup().get(playerWW).isEmpty()){
                    noCamp.getCompteur().replace(playerWW, noCamp.getGame().getConfig().getValue(NoCamp.KEY + ".config.sendtitle") - 1);
                    cancel();
                }
                noCamp.getCompteur().replace(playerWW, noCamp.getGame().getConfig().getValue(NoCamp.KEY + ".config.sendtitle") - 1);
                noCamp.getGame().getMapManager().transportation(playerWW, Math.random() * 2 * Math.PI);
                playerWW.sendMessageWithKey(Prefix.RED, NoCamp.KEY + ".message.teleported");
                if(noCamp.getPlayerTeleported().containsKey(playerWW)) {
                    noCamp.getPlayerTeleported().replace(playerWW, noCamp.getPlayerTeleported().get(playerWW) + 1);
                } else {
                    noCamp.getPlayerTeleported().put(playerWW, 1);
                }
            }
            noCamp.getTimer().replace(playerWW, false);
            cancel();
            return;
        }
        timer--;
    }
}
