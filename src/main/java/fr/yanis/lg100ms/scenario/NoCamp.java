package fr.yanis.lg100ms.scenario;

import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Scenario;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.enums.Sound;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import fr.ph1lou.werewolfapi.events.UpdatePlayerNameTagEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import fr.ph1lou.werewolfapi.events.game.game_cycle.WinEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.listeners.impl.ListenerWerewolf;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.yanis.lg100ms.JsonMessageBuilder;
import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.commands.admin.Command100ms;
import fr.yanis.lg100ms.task.GroupTeleport;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Scenario(key = NoCamp.KEY + ".display",
    configValues = {
            @IntValue(key = NoCamp.KEY + ".config.rayon", defaultValue = 50, meetUpValue = 50, step = 1, item = UniversalMaterial.COMPASS),
            @IntValue(key = NoCamp.KEY + ".config.rayon_attacked", defaultValue = 15, meetUpValue = 15, step = 1, item = UniversalMaterial.COMPASS),
            @IntValue(key = NoCamp.KEY + ".config.sendtitle", defaultValue = 660, meetUpValue = 660, step = 30, item = UniversalMaterial.CLOCK),
            @IntValue(key = NoCamp.KEY + ".config.timemin", defaultValue = 60, meetUpValue = 60, step = 10, item = UniversalMaterial.ENDER_PEARL),
            @IntValue(key = NoCamp.KEY + ".config.intervalle", defaultValue = 120, meetUpValue = 120, step = 5, item = UniversalMaterial.ENDER_PEARL)
    })
public class NoCamp extends ListenerWerewolf {
    public static final String KEY = LGMSMain.KEY + ".scenario.nocamp";
    public static HashMap<IPlayerWW, Integer> compteur = new HashMap<>();
    private HashMap<IPlayerWW, ArrayList<IPlayerWW>> group = new HashMap<>();
    private HashMap<IPlayerWW, Boolean> timer = new HashMap<>();
    private ArrayList<GroupTeleport> tasks = new ArrayList<>();
    private HashMap<IPlayerWW, Integer> playerTeleported = new HashMap<>();
    private HashMap<IPlayerWW, Integer> playerTitled = new HashMap<>();
    private BukkitTask taskSecond;
    private static boolean activate = true;
    private List<Entity> moreThanMidle = new ArrayList<>();
    public NoCamp(WereWolfAPI game) {
        super(game);
    }

    @EventHandler
    public void onGameStart(StartEvent e){
        NoCamp.compteur.clear();
        timer.clear();
        group.clear();
        getGame().getPlayersWW().forEach(playerWW -> {
            addToCompteur(playerWW, 0);
            timer.put(playerWW, false);
        });
        taskSecond = Bukkit.getScheduler().runTaskTimerAsynchronously(LGMSMain.getInstance(), () -> {
            if(!activate) return;
            LGMSMain.getInstance().getPlayerAlive(getGame()).forEach(playerWW -> {
                if (playerWW == null) return;
                if(Bukkit.getPlayer(playerWW.getUUID()) == null) return;
                if(!playerWW.isState(StatePlayer.ALIVE)) return;
                if((!Bukkit.getPlayer(playerWW.getUUID()).isSneaking() || !checkBlocksAbovePlayer(Bukkit.getPlayer(playerWW.getUUID()))) && !Bukkit.getPlayer(playerWW.getUUID()).hasPotionEffect(PotionEffectType.INVISIBILITY)){
                    createGroupOfPlayer(playerWW, getGame().getConfig().getValue(NoCamp.KEY + ".config.rayon"));
                    ArrayList<IPlayerWW> group = this.group.get(playerWW);
                    if(group.size() + 1 > getGame().getGroup()) addToCompteur(playerWW, 1);
                    if(group.size() + 1 <= getGame().getGroup()) removeFromCompteur(playerWW, 2);

                    if(NoCamp.compteur.get(playerWW) >= getGame().getConfig().getValue(NoCamp.KEY + ".config.sendtitle")){
                        if(!timer.get(playerWW)){
                            Bukkit.getPlayer(playerWW.getUUID()).sendTitle("§cGroupe trop grand !", "§6Veuillez vous éloigner des autres joueurs");
                            Sound.ANVIL_USE.play(playerWW);
                            TextComponent text = new JsonMessageBuilder("§9Le joueur §b" + playerWW.getName() + " §9a reçu un avertissement du scénario NoCamp !")
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§9Cliquez pour téléporter le joueur")}))
                                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + playerWW.getName()))
                                    .build();
                            getGame().getModerationManager().getModerators().forEach(uuid -> {
                                Bukkit.getPlayer(uuid).spigot().sendMessage(text);
                            });
                            if(playerTitled.containsKey(playerWW)){
                                playerTitled.replace(playerWW, playerTitled.get(playerWW) + 1);
                            } else {
                                playerTitled.put(playerWW, 1);
                            }
                            Random random = new Random();
                            int intervalle = getGame().getConfig().getValue(NoCamp.KEY + ".config.intervalle");
                            int timeMin = getGame().getConfig().getValue(NoCamp.KEY + ".config.timemin");
                            int timeMax = intervalle + timeMin;
                            GroupTeleport task = null;
                            if(intervalle == 0){
                                task = new GroupTeleport(timeMin, playerWW, this);
                            } else {
                                task = new GroupTeleport(random.nextInt(timeMax - timeMin) + timeMin, playerWW, this);
                            }
                            task.runTaskTimer(LGMSMain.getInstance(), 0, 20);
                            tasks.add(task);
                            timer.replace(playerWW, true);
                        }
                    }
                }
                if(Command100ms.devMode){
                    Bukkit.broadcastMessage("§9" + playerWW.getName() + " §f: §b" + NoCamp.compteur.get(playerWW) + " §f| §b" + timer.get(playerWW) + " §f| §b" + group.get(playerWW).size());
                }
            });
        }, 0, 20);
    }

    @EventHandler
    public void onGameStop(WinEvent e){
        taskSecond.cancel();
        tasks.forEach(GroupTeleport::cancel);
        if(getGame().getModerationManager().getModerators() != null){
            for (UUID moderator : getGame().getModerationManager().getModerators()) {
                sendListOfPlayer(Bukkit.getPlayer(moderator));
            }
        }
        if(getGame().getModerationManager().getHosts() != null){
            for (UUID host : getGame().getModerationManager().getHosts()) {
                sendListOfPlayer(Bukkit.getPlayer(host));
            }
        }
        sendListToConsole();
    }

    public void sendListOfPlayer(Player player){
        player.sendMessage("§6Liste des joueurs ayant eu un title");
        playerTitled.forEach((playerWW1, integer) -> {
            player.sendMessage("§9" + playerWW1.getName() + " §f: §b" + integer + " fois");
        });
        player.sendMessage("§6Liste des joueurs ayant été téléporté");
        playerTeleported.forEach((playerWW1, integer) -> {
            player.sendMessage("§9" + playerWW1.getName() + " §f: §b" + integer + " fois");
        });
    }

    public void sendListToConsole(){
        Bukkit.getConsoleSender().sendMessage("Liste des joueurs ayant eu un title");
        playerTitled.forEach((playerWW1, integer) -> {
            Bukkit.getConsoleSender().sendMessage( playerWW1.getName() + " : " + integer + " fois");
        });
        Bukkit.getConsoleSender().sendMessage("Liste des joueurs ayant été téléporté");
        playerTeleported.forEach((playerWW1, integer) -> {
            Bukkit.getConsoleSender().sendMessage(playerWW1.getName() + " : " + integer + " fois");
        });
    }

    @EventHandler
    public void onGameStop(StopEvent e){
        taskSecond.cancel();
        tasks.forEach(GroupTeleport::cancel);
        if(getGame().getModerationManager().getModerators() != null) {
            for (UUID moderator : getGame().getModerationManager().getModerators()) {
                sendListOfPlayer(Bukkit.getPlayer(moderator));
            }
        }
        if(getGame().getModerationManager().getHosts() != null){
            for (UUID host : getGame().getModerationManager().getHosts()) {
                sendListOfPlayer(Bukkit.getPlayer(host));
            }
        }
        sendListToConsole();
    }

    @EventHandler
    public void onAttacked(EntityDamageByEntityEvent e){
        if(!activate) return;
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
            Material weapon = ((Player) e.getDamager()).getItemInHand().getType();
            if(weapon == Material.IRON_SWORD || weapon == Material.DIAMOND_SWORD || weapon == Material.IRON_AXE || weapon == Material.DIAMOND_AXE){
                createGroupOfPlayer(getGame().getPlayerWW(e.getEntity().getUniqueId()).get(), getGame().getConfig().getValue(NoCamp.KEY + ".config.rayonAttacked"));
                if(this.group.get(getGame().getPlayerWW(e.getEntity().getUniqueId()).get()) == null) return;
                this.group.get(getGame().getPlayerWW(e.getEntity().getUniqueId()).get()).forEach(playerWW -> {
                    setCompteur(playerWW, 0);
                });
                setCompteur(getGame().getPlayerWW(e.getEntity().getUniqueId()).get(), 0);
            }
        }
        if(e.getDamager() instanceof Projectile){
            if(!activate) return;
            Projectile projectile = (Projectile) e.getDamager();
            if(projectile.getShooter() instanceof Player){
                if(projectile instanceof Arrow){
                    if(getGame().getPlayersWW() == null) return;
                    if(!getGame().getPlayerWW(e.getEntity().getUniqueId()).isPresent()) return;
                    if(!moreThanMidle.contains(projectile)) return;
                    setCompteur(getGame().getPlayerWW(e.getEntity().getUniqueId()).get(), 0);
                    createGroupOfPlayer(getGame().getPlayerWW(e.getEntity().getUniqueId()).get(), getGame().getConfig().getValue(NoCamp.KEY + ".config.rayonAttacked"));
                    if(this.group.get(getGame().getPlayerWW(e.getEntity().getUniqueId()).get()) == null) return;
                    if(this.group.get(getGame().getPlayerWW(e.getEntity().getUniqueId()).get()).isEmpty()) return;
                    this.group.get(getGame().getPlayerWW(e.getEntity().getUniqueId()).get()).forEach(playerWW -> {
                        setCompteur(playerWW, 0);
                    });
                }
            }
        }
    }

    public void createGroupOfPlayer(IPlayerWW playerWW, int rayon){
        ArrayList<IPlayerWW> group = new ArrayList<>();
        Player bukkitPlayer = Bukkit.getPlayer(playerWW.getUUID());
        if(bukkitPlayer == null) return;
        if(!bukkitPlayer.getNearbyEntities(rayon, rayon,rayon).isEmpty()){
            bukkitPlayer.getNearbyEntities(rayon, rayon,rayon).forEach(entity -> {
                if(entity == null) return;
                if(entity instanceof Player){
                    if(getGame().getPlayerWW(entity.getUniqueId()).get() == null) return;
                    if(getGame().getPlayerWW(entity.getUniqueId()).get().isState(StatePlayer.JUDGEMENT)) return;
                    if((!((Player) entity).isSneaking() || !checkBlocksAbovePlayer((Player) entity)) && !(((Player) entity).hasPotionEffect(PotionEffectType.INVISIBILITY)) && ((Player) entity).getGameMode() == GameMode.SURVIVAL) {
                        getGame().getPlayerWW(entity.getUniqueId()).get();
                        IPlayerWW target = getGame().getPlayerWW(entity.getUniqueId()).get();
                        group.add(target);
                    }
                }
            });
        }
        if(this.group.containsKey(playerWW)){
            this.group.replace(playerWW, group);
        } else {
            this.group.put(playerWW, group);
        }
    }

    @EventHandler
    public void EntityShootWithBow(EntityShootBowEvent e){
        if(!activate) return;
        if(e.getEntity() instanceof Player){
            if(e.getProjectile() instanceof Arrow){
                if(e.getForce() > 0.5F){
                    moreThanMidle.add(e.getProjectile());
                }
            }
        }
    }

    private boolean checkBlocksAbovePlayer(Player player) {
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        for (int currentY = y; currentY < 256; currentY++) {
            Block block = player.getWorld().getBlockAt(x, currentY, z);
            if (block.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    private void addToCompteur(IPlayerWW playerWW, int amount){
        if(NoCamp.compteur.containsKey(playerWW)){
            NoCamp.compteur.replace(playerWW, NoCamp.compteur.get(playerWW) + amount);
        }else{
            NoCamp.compteur.put(playerWW, amount);
        }
    }

    private void removeFromCompteur(IPlayerWW playerWW, int amount){
        if(NoCamp.compteur.containsKey(playerWW)){
            if(NoCamp.compteur.get(playerWW) > amount){
                NoCamp.compteur.replace(playerWW, NoCamp.compteur.get(playerWW) - amount);
            } else {
                NoCamp.compteur.replace(playerWW, 0);
            }
        }
    }

    private void setCompteur(IPlayerWW playerWW, int amount) {
        if (NoCamp.compteur.containsKey(playerWW)) {
            NoCamp.compteur.replace(playerWW, amount);
        } else {
            NoCamp.compteur.put(playerWW, amount);
        }
    }

    public HashMap<IPlayerWW, Integer> getPlayerTeleported() {
        return playerTeleported;
    }

    public static HashMap<IPlayerWW, Integer> getCompteur() {
        return NoCamp.compteur;
    }
    public HashMap<IPlayerWW, Boolean> getTimer() {
        return timer;
    }

    public HashMap<IPlayerWW, ArrayList<IPlayerWW>> getGroup() {
        return group;
    }

    public static void setActivate(boolean activate) {
        NoCamp.activate = activate;
    }

    public static boolean isActivate() {
        return activate;
    }
}
