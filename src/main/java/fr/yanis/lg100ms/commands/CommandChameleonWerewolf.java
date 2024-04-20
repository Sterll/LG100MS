package fr.yanis.lg100ms.commands;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.role.ChameleonWerewolf;
import fr.yanis.lg100ms.task.GroupTeleport;
import fr.yanis.lg100ms.task.SkinTask;
import net.md_5.bungee.api.chat.TextComponent;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

@RoleCommand(key = CommandChameleonWerewolf.KEY,
        roleKeys = ChameleonWerewolf.KEY + ".display",
        argNumbers = {0, 1})
public class CommandChameleonWerewolf implements ICommandRole {

    public static final String KEY = ChameleonWerewolf.KEY + ".command";

    @Override
    public void execute(WereWolfAPI game, IPlayerWW iPlayerWW, String[] args) {
        ChameleonWerewolf role = (ChameleonWerewolf) iPlayerWW.getRole();
        Player player = Bukkit.getPlayer(iPlayerWW.getUUID());
        switch (args.length) {
            case 1:
                if (Bukkit.getPlayer(args[0]) == null || game.getPlayerWW(Bukkit.getPlayer(args[0]).getUniqueId()).orElse(null) == null) {
                    iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.player_not_found");
                    return;
                }
                if (!game.getPlayerWW(Bukkit.getPlayer(args[0]).getUniqueId()).get().isState(StatePlayer.ALIVE)) {
                    iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.player_not_found");
                    return;
                }
                if (!role.canUse()) {
                    iPlayerWW.sendMessageWithKey(Prefix.RED, "lg100ms.check.cant_use");
                    return;
                }
                if (!role.isTransformed()) {
                    Player target = Bukkit.getPlayer(args[0]);
                    role.addAffectedPlayer(game.getPlayerWW(target.getUniqueId()).get());
                    SkinProperty targetSkin = null;
                    try {
                        targetSkin = LGMSMain.getInstance().getSkinsRestorer().getSkinStorage().getPlayerSkin(target.getName(), true).get().getSkinProperty();
                    } catch (DataRequestException e) {
                        throw new RuntimeException(e);
                    }
                    //LGMSMain.getInstance().getSkinsRestorer().getSkinApplier(Player.class).applySkin(player, targetSkin);
                    int timer = game.getConfig().getTimerValue(TimerBase.DAY_DURATION);
                    role.setTransformed(true);
                    role.setDisplayRole(role.getAffectedPlayers().get(0).getRole().getDisplayRole());
                    role.setDisplayCamp(role.getAffectedPlayers().get(0).getRole().getDisplayCamp());
                    role.refresh(target.getDisplayName(), player);
                    String message = game.translate(Prefix.GREEN, ChameleonWerewolf.KEY + ".message.ability_use");
                    iPlayerWW.sendMessage(new TextComponent(message.replace("&pseudo&", target.getName())));
                    role.setTask(new SkinTask(game, player, iPlayerWW, target, role, timer));
                    role.getTask().runTaskTimer(LGMSMain.getInstance(), 0, 20);
                } else {
                    iPlayerWW.sendMessageWithKey(Prefix.RED, ChameleonWerewolf.KEY + ".message.already_transformed");
                }
                break;
            case 0:
                if (role.isTransformed()) {
                    role.getTask().stop();
                } else {
                    iPlayerWW.sendMessageWithKey(Prefix.RED, ChameleonWerewolf.KEY + ".message.not_transformed");
                }
        }
    }
}
