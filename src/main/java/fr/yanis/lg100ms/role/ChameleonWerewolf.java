package fr.yanis.lg100ms.role;

import de.domedd.betternick.BetterNick;
import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.events.game.timers.WereWolfListEvent;
import fr.ph1lou.werewolfapi.events.roles.detective.InvestigateEvent;
import fr.ph1lou.werewolfapi.events.roles.seer.SeerEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.impl.RoleWereWolf;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.task.SkinTask;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import xyz.haoshoku.nick.api.NickAPI;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Role(key = ChameleonWerewolf.KEY + ".display",
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF,
        defaultAura = Aura.NEUTRAL,
        loreKey = ChameleonWerewolf.KEY + ".lore")
public class ChameleonWerewolf extends RoleWereWolf implements IAffectedPlayers {

    public static final String KEY = LGMSMain.KEY + ".role.chameleon_werewolf";
    public final ArrayList<IPlayerWW> affectedPlayer = new ArrayList<>();
    private boolean isTransformed = false;
    private boolean canUse,listed = false;
    private final SkinProperty oldSkin;
    private final String oldName;
    private final String oldDisplayRole;
    private final String oldDisplayCamp;
    private SkinTask task;
    private ArrayList<IPlayerWW> playersRightClicked = new ArrayList<>();

    public ChameleonWerewolf(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW){
        super(game, playerWW);
        try {
            oldSkin = LGMSMain.getInstance().getSkinsRestorer().getSkinStorage().getPlayerSkin(playerWW.getName(), true).get().getSkinProperty();
        } catch (DataRequestException e) {
            throw new RuntimeException(e);
        }
        oldName = playerWW.getName();
        oldDisplayRole = getDisplayRole();
        oldDisplayCamp = getDisplayCamp();
    }

    @EventHandler
    public void onRightClickOnPlayer(PlayerInteractAtEntityEvent e){
        if(!isTransformed) return;
        if(!(e.getRightClicked() instanceof Player)) return;
        IPlayerWW iPlayerWW = game.getPlayerWW(e.getPlayer().getUniqueId()).get();
        IPlayerWW target = game.getPlayerWW(e.getRightClicked().getUniqueId()).get();
        if(!target.getRole().isCamp(Camp.WEREWOLF)) return;
        if(playersRightClicked.contains(target)) return;
        target.sendMessageWithKey(Prefix.ORANGE, KEY + ".message.right_click");
        addPlayerRightClicked(target);
    }

    @EventHandler
    public void onNight(NightEvent e){
        if(listed){
            canUse = true;
            getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.ability_enable");
        }
    }

    @EventHandler
    public void onDay(DayEvent e){
        if(listed) canUse = false;
    }

    @EventHandler
    public void onWin(WinEvent e){
        if(isTransformed){
            getTask().stop();
        }
    }

    @EventHandler
    public void onList(WereWolfListEvent e){
        listed = true;
    }

    @EventHandler
    public void onGameStoped(StopEvent e){
        if(isTransformed){
            getTask().stop();
        }
    }

    /**@EventHandler
    public void onSeerCheck(SeerEvent e){
        if(getAffectedPlayers().contains(e.getPlayerWW()) && isTransformed){
            e.setCamp(getAffectedPlayers().get(0).getRole().getCamp().getKey());
        }
    }

    @EventHandler
    public void onDetectiveCheck(InvestigateEvent e){
        if(e.getPlayerWWs().contains(getPlayerWW()) && isTransformed){
            e.setSameCamp(e.getPlayerWW().getRole().getCamp() == getAffectedPlayers().get(0).getRole().getCamp());
        }
    }**/

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayer.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayer.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayer.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayer;
    }

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate(KEY + ".description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    public boolean isTransformed() {
        return isTransformed;
    }

    public void setTransformed(boolean transformed) {
        isTransformed = transformed;
    }

    public SkinProperty getOldSkin() {
        return oldSkin;
    }

    public boolean canUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public String getOldDisplayCamp() {
        return oldDisplayCamp;
    }

    public String getOldDisplayRole() {
        return oldDisplayRole;
    }

    public String getOldName() {
        return oldName;
    }

    @SuppressWarnings("deprecation")
    public void refresh(String name, Player player) {
        String oldName = player.getDisplayName();
        NickAPI.setSkin(player, name);
        NickAPI.nick(player, name);
        NickAPI.refreshPlayer(player);
        BetterNick.getApi().setPlayerTablistName(player, oldName, "", "");
        //BetterNick.getApi().setPlayerSkin(player, name);
        /**Bukkit.getScheduler().runTask(LGMSMain.getInstance(), () -> {

            try {
                Method getHandle = player.getClass().getMethod("getHandle");
                Object entityPlayer = getHandle.invoke(player);
                boolean gameProfileExists = false;
                try {
                    Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                    gameProfileExists = true;
                } catch (ClassNotFoundException ignored) {
                }
                try {
                    Class.forName("com.mojang.authlib.GameProfile");
                    gameProfileExists = true;
                } catch (ClassNotFoundException ignored) {
                }
                if (!gameProfileExists) {
                    Field nameField = entityPlayer.getClass().getSuperclass().getDeclaredField("name");
                    nameField.setAccessible(true);
                    nameField.set(entityPlayer, name);
                } else {
                    Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
                    Field ff = profile.getClass().getDeclaredField("name");
                    ff.setAccessible(true);
                    ff.set(profile, name);
                }
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.hidePlayer(player);
                    onlinePlayer.showPlayer(player);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                     InvocationTargetException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        });**/
    }

    public SkinTask getTask() {
        return task;
    }

    public void setTask(SkinTask task) {
        this.task = task;
    }

    public ArrayList<IPlayerWW> getPlayersRightClicked() {
        return playersRightClicked;
    }

    public void setPlayersRightClicked(ArrayList<IPlayerWW> playersRightClicked) {
        this.playersRightClicked = playersRightClicked;
    }

    public void addPlayerRightClicked(IPlayerWW playerRightClicked) {
        this.playersRightClicked.add(playerRightClicked);
    }

    public void removePlayerRightClicked(IPlayerWW playerRightClicked) {
        this.playersRightClicked.remove(playerRightClicked);
    }

    public void clearPlayersRightClicked() {
        this.playersRightClicked.clear();
    }
}
