package fr.yanis.lg100ms.role;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.RoleAttribute;
import fr.ph1lou.werewolfapi.enums.VoteStatus;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.vote.VoteResultEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.yanis.lg100ms.LGMSMain;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Role(key = Dictator.KEY + ".display",
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        defaultAura = Aura.NEUTRAL)
public class Dictator extends RoleImpl implements IPower, IAffectedPlayers {

    public static final String KEY = LGMSMain.KEY + ".role.dictator";
    private boolean power = true;
    private boolean used = false;
    private final List<IPlayerWW> affectedPlayer = new ArrayList<>();
    public Dictator(@NotNull WereWolfAPI main, @NotNull IPlayerWW playerWW) {
        super(main, playerWW);
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(this.game, this).setDescription(game.translate(KEY + ".description"))
                .setItems(game.translate(KEY + ".items"))
                .build();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVoteEnd(VoteResultEvent e){
        if(!affectedPlayer.isEmpty()){
            e.setCancelled(true);
            e.setPlayerWW(affectedPlayer.get(0));
            String message = game.translate(Prefix.BLUE, Dictator.KEY + ".vote_result_ejector");
            Bukkit.broadcastMessage(message.replace("&player&", affectedPlayer.get(0).getName()));
            e.getPlayerWW().clearPotionEffects();
            e.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.POISON, 20*20, 1, "votePoison"));
            clearAffectedPlayer();
            return;
        }
        if(isUsed()){
            e.setCancelled(true);
            if(e.getPlayerWW() == null){
                Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, LGMSMain.KEY + ".vote.no_result"));
                return;
            }
            else Bukkit.broadcastMessage(game.translate(Prefix.YELLOW, LGMSMain.KEY + ".vote.result").replace("&player&", e.getPlayerWW().getName()).replace("&votes&", String.valueOf(game.getVoteManager().getVotes(e.getPlayerWW()))));
            e.getPlayerWW().clearPotionEffects();
            e.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.POISON, 20*20, 1, "votePoison"));
            Set<? extends IPlayerWW> alreadyVotedPlayers = game.getVoteManager().getAlreadyVotedPlayers();
            ((Set<IPlayerWW>) alreadyVotedPlayers).add(e.getPlayerWW());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onVote(DayEvent e){
        if(hasPower() && game.getVoteManager().isStatus(VoteStatus.IN_PROGRESS)){
            String message = game.translate(Prefix.RED, Dictator.KEY + ".ejector_message");
            int timerValue = game.getConfig().getTimerValue(TimerBase.VOTE_DURATION);
            if(timerValue < 60) message = message.replace("&timer&", String.valueOf(timerValue)) + "s";
            int minute = timerValue / 60;
            int second = timerValue - (minute * 60);
            message = message.replace("&timer&", minute + "m" + second + "s");
            getPlayerWW().sendMessage(new TextComponent(message));
        }
    }

    @Override
    public void recoverPower() {

    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isUsed() {
        return used;
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return this.power;
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayer.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        this.affectedPlayer.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayer;
    }
}
