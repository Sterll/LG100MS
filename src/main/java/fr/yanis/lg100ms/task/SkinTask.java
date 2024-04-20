package fr.yanis.lg100ms.task;

import de.domedd.betternick.BetterNick;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.role.ChameleonWerewolf;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.haoshoku.nick.api.NickAPI;

public class SkinTask extends BukkitRunnable {

    private WereWolfAPI game;
    private Player player, target;
    private IPlayerWW iPlayerWW;
    private ChameleonWerewolf role;
    private int timer;

    public SkinTask(WereWolfAPI game, Player player, IPlayerWW playerWW, Player target, ChameleonWerewolf role, int timer){
        this.player = player;
        this.target = target;
        this.role = role;
        this.timer = timer;
        this.iPlayerWW = playerWW;
        this.game = game;
    }

    @Override
    public void run() {
        if(timer == 30){
            String message = game.translate(Prefix.RED, ChameleonWerewolf.KEY + ".message.ability_alert");
            iPlayerWW.sendMessage(new TextComponent(message.replace("&pseudo&", target.getName())));
        }
        if(timer == 0){
            stop();
        }
        timer--;
    }

    public void stop(){
        //LGMSMain.getInstance().getSkinsRestorer().getSkinApplier(Player.class).applySkin(player, role.getOldSkin());
        role.clearAffectedPlayer();
        role.clearPlayersRightClicked();
        role.setTransformed(false);
        role.setCanUse(false);
        role.setDisplayRole(role.getOldDisplayRole());
        role.setDisplayCamp(role.getOldDisplayCamp());
        //BetterNick.getApi().resetPlayerSkin(player);
        NickAPI.resetSkin(player);
        NickAPI.resetNick(player);
        NickAPI.refreshPlayer(player);
        BetterNick.getApi().removeNickedPlayer(player);
        String message2 = game.translate(Prefix.RED, ChameleonWerewolf.KEY + ".message.ability_disabled");
        iPlayerWW.sendMessage(new TextComponent(message2.replace("&pseudo&", target.getName())));
        cancel();
    }
}
