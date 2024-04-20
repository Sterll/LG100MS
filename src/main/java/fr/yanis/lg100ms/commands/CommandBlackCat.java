package fr.yanis.lg100ms.commands;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.Day;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.role.BlackCat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RoleCommand(key = CommandBlackCat.KEY,
        roleKeys = BlackCat.KEY + ".display",
        argNumbers = 1)
public class CommandBlackCat implements ICommandRole {

    public static final String KEY = BlackCat.KEY + ".command";

    @Override
    public void execute(WereWolfAPI game, IPlayerWW iPlayerWW, String[] args) {
        BlackCat role = (BlackCat) iPlayerWW.getRole();
        if(!role.canUse()){
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.cant_use");
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.offline_player");
            return;
        }
        IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);
        if (targetWW == null || targetWW.isState(StatePlayer.DEATH)) {
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.player_not_found");
            return;
        }
        if(target.getUniqueId() == iPlayerWW.getUUID()){
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.not_yourself");
            return;
        }
        role.addAffectedPlayer(targetWW);
        role.setCanUse(false);
        if(targetWW.getRole().getKey().contains("witch")){
            iPlayerWW.sendMessageWithKey(Prefix.GREEN, "lg100ms.role.black_cat.message.soso_found");
            role.setSosoFound(true);
        } else {
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.role.black_cat.message.not_found");
        }
    }
}
