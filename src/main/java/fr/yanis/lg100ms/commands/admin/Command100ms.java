package fr.yanis.lg100ms.commands.admin;

import fr.ph1lou.werewolfapi.annotations.AdminCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.StateGame;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.gui.MainGui;
import fr.yanis.lg100ms.scenario.NoCamp;
import org.bukkit.entity.Player;

import java.util.HashMap;

@AdminCommand(key = Command100ms.KEY + ".display",
        descriptionKey = Command100ms.KEY + ".description",
        statesGame = {StateGame.GAME, StateGame.LOBBY, StateGame.END, StateGame.START, StateGame.TRANSPORTATION},
        moderatorAccess = true)
public class Command100ms implements ICommand {
    public static final String KEY = LGMSMain.KEY + ".command.main";

    @Override
    public void execute(WereWolfAPI wereWolfAPI, Player player, String[] strings) {
        MainGui.INVENTORY.open(player);
    }
}
