package fr.yanis.lg100ms.commands;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.role.Dictator;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = CommandDictator.KEY,
    roleKeys = Dictator.KEY + ".display",
    argNumbers = 1,
    requiredPower = true)
public class CommandDictator implements ICommandRole {
    public static final String KEY = Dictator.KEY + ".command";

    @Override
    public void execute(WereWolfAPI game, IPlayerWW iPlayerWW, String[] args) {
        if(!game.getVoteManager().isStatus(VoteStatus.IN_PROGRESS)) {
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.cant_use");
            return;
        }
        if(!(iPlayerWW.getRole() instanceof Dictator)) return;
        Dictator role = (Dictator) iPlayerWW.getRole();
        Player playerArg = Bukkit.getPlayer(args[0]);
        if (playerArg == null) {
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.offline_player");
            return;
        }
        UUID uuid1 = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(uuid1).orElse(null);
        if (playerWW1 == null || playerWW1.isState(StatePlayer.DEATH)) {
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.player_not_found");
            return;
        }
        if (iPlayerWW.getUUID().equals(uuid1)) {
            iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.not_yourself");
            return;
        }
        role.addAffectedPlayer(playerWW1);
        role.setPower(false);
        role.setUsed(true);
        String message = game.translate(Prefix.GREEN, "lg100ms.role.dictator.command_success");
        iPlayerWW.sendMessage(new TextComponent(message.replace("&player&", playerArg.getName())));
    }
}
