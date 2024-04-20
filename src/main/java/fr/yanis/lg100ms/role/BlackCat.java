package fr.yanis.lg100ms.role;

import fr.ph1lou.werewolfapi.annotations.Role;
import fr.ph1lou.werewolfapi.basekeys.Prefix;
import fr.ph1lou.werewolfapi.basekeys.RoleBase;
import fr.ph1lou.werewolfapi.enums.*;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.day_cycle.NightEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.utils.WinConditionsCheckEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.lovers.ILoverManager;
import fr.ph1lou.werewolfapi.player.impl.AuraModifier;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IAuraModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.role.impl.RoleImpl;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IAura;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.lover.CursedLover;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Role(key = BlackCat.KEY + ".display",
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER,
        defaultAura = Aura.NEUTRAL,
        requireRoles = RoleBase.WITCH)
public class BlackCat extends RoleImpl implements IAffectedPlayers {

    public static final String KEY = LGMSMain.KEY + ".role.black_cat";
    public final ArrayList<IPlayerWW> affectedPlayer = new ArrayList<>();
    private IPlayerWW lover = null;
    private boolean canUse,sosoFound,isHargneux,canRevenge,isRevenge,alreadyCrossed = false;

    private String suspectList;

    public BlackCat(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW) {
        super(game, playerWW);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(alreadyCrossed) return;
        if(sosoFound) return;
        for(Entity entity : e.getPlayer().getNearbyEntities(30,30,30)){
            if(entity instanceof Player){
                if(game.getPlayersWW() == null) return;
                if(!game.getPlayersWW().contains(entity.getUniqueId())) return;
                if(game.getPlayerWW(entity.getUniqueId()).get().getRole().getKey().contains("witch")){
                    alreadyCrossed = true;
                    Bukkit.getScheduler().runTaskLaterAsynchronously(LGMSMain.getInstance(), () -> {
                        getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.soso_detect");
                    }, 20 * (60*5));
                }
            }
        }
    }

    @EventHandler
    public void onDay(DayEvent e){
        if(isRevenge()){
            getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE, KEY));
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent e){
        if(e.getEntity() instanceof Player && ((Player) e.getEntity()).getPlayer().getUniqueId() == getPlayerUUID()){
            if(e.getCause() == EntityDamageEvent.DamageCause.FALL && isRevenge()) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onNight(NightEvent e){
        if(isRevenge()){
            getPlayerWW().addPotionModifier(PotionModifier.remove(PotionEffectType.DAMAGE_RESISTANCE, KEY));
        }
        if(!sosoFound){
            canUse = true;
            getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.night_begin");
            Bukkit.getScheduler().runTaskLaterAsynchronously(LGMSMain.getInstance(), () -> canUse = false, 20 * 180);
        }
        if(!isHargneux() && getPlayerWW().isState(StatePlayer.ALIVE)){
            ArrayList<Player> villager = new ArrayList<>();
            ArrayList<Player> werewolf = new ArrayList<>();
            ArrayList<Player> neutral = new ArrayList<>();
            for(Entity entity : Bukkit.getPlayer(getPlayerUUID()).getNearbyEntities(70,70,70)){
                if(entity instanceof Player && game.getPlayerWW(entity.getUniqueId()).get().isState(StatePlayer.ALIVE)){
                    Camp camp = game.getPlayerWW(entity.getUniqueId()).get().getRole().getCamp();
                    if(camp== Camp.VILLAGER){
                        villager.add((Player) entity);
                    }
                    if(camp == Camp.NEUTRAL){
                        neutral.add((Player) entity);
                    }
                    if(camp == Camp.WEREWOLF){
                        werewolf.add((Player) entity);
                    }
                }
            }

            Random random = new Random();
            Player player = null;
            boolean hasVillager = !villager.isEmpty();
            boolean hasWerewolf = !werewolf.isEmpty();
            boolean hasNeutral = !neutral.isEmpty();

            int totalCamps = (hasVillager ? 1 : 0) + (hasWerewolf ? 1 : 0) + (hasNeutral ? 1 : 0);
            int villagerChance = hasVillager ? 20 : 0;
            int werewolfChance = hasWerewolf ? 55 : 0;
            int neutralChance = hasNeutral ? 25 : 0;
            if(totalCamps == 0) return;
            if (totalCamps == 1) {
                if (hasVillager) {
                    player = villager.get(random.nextInt(villager.size()));
                } else if (hasWerewolf) {
                    player = werewolf.get(random.nextInt(werewolf.size()));
                } else if (hasNeutral) {
                    player = neutral.get(random.nextInt(neutral.size()));
                }
            }
            if (totalCamps == 2) {
                if (!hasVillager) {
                    werewolfChance = 70;
                    neutralChance = 30;
                } else if (!hasWerewolf) {
                    villagerChance = 45;
                    neutralChance = 55;
                } else if (!hasNeutral){
                    villagerChance = 28;
                    werewolfChance = 72;
                }
            }
            int r = random.nextInt(100);
            if (r <= villagerChance) {
                player = villager.get(random.nextInt(villager.size()));
            } else if (r > villagerChance && r <= werewolfChance) {
                player = werewolf.get(random.nextInt(werewolf.size()));
            } else if (r > werewolfChance && r <= neutralChance){
                player = neutral.get(random.nextInt(neutral.size()));
            }
            if(player == null) return;
            if(random.nextBoolean()){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 120, 0, false, false), true);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 30, 0, false, false), true);
            }
            game.getPlayerWW(player.getUniqueId()).get().sendMessageWithKey(Prefix.RED, KEY + ".message.cat_no_luck");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDetectVictoryCancel(WinConditionsCheckEvent event) {
        if(this.lover == null) return;
        if (this.lover.isState(StatePlayer.ALIVE) && getPlayerWW().isState(StatePlayer.ALIVE)){
            event.setVictoryTeam("");
        }
    }

    @EventHandler
    public void onPlayerDeath(FinalDeathEvent e){
        if(e.getPlayerWW().getRole().isKey(RoleBase.WITCH)){
            canUse = false;
            if(e.getTarget() != null) getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.soso_killed");
            else getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.soso_killed_pve");
            if(sosoFound && e.getTarget() != null){
                CursedLover cursedLover = new CursedLover(game, e.getPlayerWW(), e.getTarget());
                game.getLoversManager().addLover(cursedLover);
                setLover(e.getTarget());
                ArrayList<IPlayerWW> suspect = new ArrayList<>();
                suspect.add(getLover());
                if(LGMSMain.getInstance().getPlayerAlive(game).size() == 3){
                    IPlayerWW randomPlayer1 = (IPlayerWW) LGMSMain.getInstance().getPlayerAlive(game).toArray()[new Random().nextInt(LGMSMain.getInstance().getPlayerAlive(game).size())];
                    while (randomPlayer1.equals(getLover()) || randomPlayer1.equals(getPlayerWW())){
                        randomPlayer1 = (IPlayerWW) LGMSMain.getInstance().getPlayerAlive(game).toArray()[new Random().nextInt(LGMSMain.getInstance().getPlayerAlive(game).size())];
                    }
                    suspect.add(randomPlayer1);
                } else if(LGMSMain.getInstance().getPlayerAlive(game).size() > 3){
                    IPlayerWW randomPlayer1 = (IPlayerWW) LGMSMain.getInstance().getPlayerAlive(game).toArray()[new Random().nextInt(LGMSMain.getInstance().getPlayerAlive(game).size())];
                    while (randomPlayer1.equals(getLover()) || randomPlayer1.equals(getPlayerWW())){
                        randomPlayer1 = (IPlayerWW) LGMSMain.getInstance().getPlayerAlive(game).toArray()[new Random().nextInt(LGMSMain.getInstance().getPlayerAlive(game).size())];
                    }
                    IPlayerWW randomPlayer2 = (IPlayerWW) LGMSMain.getInstance().getPlayerAlive(game).toArray()[new Random().nextInt(LGMSMain.getInstance().getPlayerAlive(game).size())];
                    while (randomPlayer2.equals(getLover()) || randomPlayer2.equals(randomPlayer1) || randomPlayer2.equals(getPlayerWW())){
                        randomPlayer2 = (IPlayerWW) LGMSMain.getInstance().getPlayerAlive(game).toArray()[new Random().nextInt(LGMSMain.getInstance().getPlayerAlive(game).size())];
                    }
                    suspect.add(randomPlayer1);
                    suspect.add(randomPlayer2);
                }
                Collections.shuffle(suspect);
                StringBuilder suspectList = new StringBuilder();
                for(IPlayerWW playerWW : suspect){
                    suspectList.append("§c" + playerWW.getName()).append("§f, ");
                }
                this.suspectList = suspectList.substring(0, suspectList.length() - 2);
                Bukkit.getScheduler().runTaskLaterAsynchronously(LGMSMain.getInstance(), () -> {
                    setHargneux(true);
                    removeAuraModifier(this.getKey());
                    addAuraModifier(new AuraModifier(this.getKey(), Aura.DARK, 1, true));
                    setCanRevenge(true);
                    getLover().sendMessageWithKey(Prefix.RED, KEY + ".message.tough_cat_alert_killer");
                    getPlayerWW().sendMessageWithKey(Prefix.RED, KEY + ".message.form_tough_cat");
                    String gameMessage = game.translate(KEY + ".message.suspect_list");
                    getPlayerWW().sendMessage(new TextComponent(gameMessage.replace("&list&", this.suspectList)));
                }, 20 * 60);
            }
        }
        if(canRevenge){
            if(e.getPlayerWW() == getLover()){
                if(e.getTarget() == null || e.getTarget() != getPlayerWW()){
                    getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.tough_cat_use_false");
                    setCanRevenge(false);
                }
                if(e.getLastStrikers().contains(getPlayerWW()) && canRevenge && isHargneux){
                    getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.tough_cat_use_true");
                    setCanRevenge(false);
                    setRevenge(true);
                }
            } else {
                if(e.getTarget() == getPlayerWW() && isHargneux){
                    if(e.getPlayerWW().getRole().getCamp() == Camp.VILLAGER){
                        setCanRevenge(false);
                        getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.tough_cat_use_fail_village");
                    } else {
                        getPlayerWW().sendMessageWithKey(Prefix.ORANGE, KEY + ".message.tough_cat_use_fail_other");
                    }
                }
            }
        }
    }

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
        if(!isHargneux()){
            return new DescriptionBuilder(this.game, this)
                    .setDescription(game.translate(KEY + ".description"))
                    .setItems(game.translate(KEY + ".items"))
                    .build();
        } else {
            return new DescriptionBuilder(this.game, this)
                    .setDescription(game.translate(KEY + ".description_hargneux").replace("&list&", this.suspectList))
                    .setItems(game.translate(KEY + ".items"))
                    .build();
        }
    }

    public boolean isSosoFound() {
        return sosoFound;
    }

    public boolean isRevenge() {
        return isRevenge;
    }

    public void setRevenge(boolean revenge) {
        isRevenge = revenge;
    }

    public void setSosoFound(boolean sosoFound) {
        this.sosoFound = sosoFound;
    }

    public boolean canRevenge() {
        return canRevenge;
    }

    public void setCanRevenge(boolean canRevenge) {
        this.canRevenge = canRevenge;
    }

    @Override
    public Set<IPlayerWW> getPlayersMet() {
        return super.getPlayersMet();
    }
    @Override
    public void recoverPower() {

    }
    public void setLover(IPlayerWW couple) {
        this.lover = couple;
    }
    public IPlayerWW getLover() {
        return lover;
    }
    public boolean canUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public boolean isHargneux() {
        return isHargneux;
    }

    public void setHargneux(boolean hargneux) {
        isHargneux = hargneux;
    }
}
