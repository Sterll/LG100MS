package fr.yanis.lg100ms.statistic;

import fr.yanis.lg100ms.LGMSMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class PlayerStatsUtils {

    private HashMap<Player, HashMap<String, Object>> playersStats;

    public PlayerStatsUtils(){
        playersStats = new HashMap<>();
    }

    public int getWinRate(Player player){
        int win = LGMSMain.getInstance().getStatsUtils().getWin(player);
        int lose = LGMSMain.getInstance().getStatsUtils().getLose(player);
        if(win == 0 && lose == 0) return 0;
        float calcul = ((float) win / (win + lose)) * 100;
        return (int) calcul;
    }

    public int getWinRateWithRole(Player player, String role){
        int win = LGMSMain.getInstance().getStatsUtils().getWinWithRole(player, role);
        int lose = LGMSMain.getInstance().getStatsUtils().getLoseWithRole(player, role);
        if(win == 0 && lose == 0) return 0;
        float calcul = ((float)win / (win + lose)) * 100;
        return (int) calcul;
    }

    public int getWinRateWithCamp(Player player, String camp){
        int win = LGMSMain.getInstance().getStatsUtils().getWinWithCamp(player, camp);
        int lose = LGMSMain.getInstance().getStatsUtils().getLoseWithCamp(player, camp);
        if(win == 0 && lose == 0) return 0;
        float calcul = ((float)win / (win + lose)) * 100;
        return (int) calcul;
    }

    public int getWin(Player player){
        return (int) playersStats.get(player).get("win");
    }

    public int getLose(Player player){
        return (int) playersStats.get(player).get("lose");
    }

    public String getDeathTimeAverage(Player player){
        int deathTime = (int) playersStats.get(player).get("deathTime");
        if(deathTime > 3600) {
            int hour = deathTime / 3600;
            int minute = (deathTime - (hour * 3600)) / 60;
            int second = deathTime - (hour * 3600) - (minute * 60);
            return hour + "h " + minute + "m " + second + "s";
        } else if(deathTime > 60){
            int minute = deathTime / 60;
            int second = deathTime - (minute * 60);
            return minute + "m " + second + "s";
        } else {
            return deathTime + "s";
        }
    }

    public int getWinWithRole(Player player, String role){
        if(playersStats.get(player).get("roleWin") == null) return 0;
        HashMap<String, Integer> roleWin = (HashMap<String, Integer>) playersStats.get(player).get("roleWin");
        if(!roleWin.containsKey(role)) return 0;
        return roleWin.get(role);
    }

    public int getLoseWithRole(Player player, String role){
        if(playersStats.get(player).get("roleLose") == null) return 0;
        HashMap<String, Integer> roleLose = (HashMap<String, Integer>) playersStats.get(player).get("roleLose");
        if(!roleLose.containsKey(role)) return 0;
        return roleLose.get(role);
    }

    public int getWinWithCamp(Player player, String camp){
        if(playersStats.get(player).get("campWin") == null) return 0;
        HashMap<String, Integer> campWin = (HashMap<String, Integer>) playersStats.get(player).get("campWin");
        if(!campWin.containsKey(camp)) return 0;
        return campWin.get(camp);
    }

    public int getLoseWithCamp(Player player, String camp){
        if(playersStats.get(player).get("campLose") == null) return 0;
        HashMap<String, Integer> campLose = (HashMap<String, Integer>) playersStats.get(player).get("campLose");
        if(!campLose.containsKey(camp)) return 0;
        return campLose.get(camp);
    }

    public void addPlayer(Player player, HashMap<String, Object> stats){
        playersStats.put(player, stats);
    }

    public HashMap<String, Object> getPlayerStats(Player player){
        return playersStats.get(player);
    }

    public void removePlayer(Player player){
        playersStats.remove(player);
    }
}
