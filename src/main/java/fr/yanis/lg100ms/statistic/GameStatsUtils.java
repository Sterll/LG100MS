package fr.yanis.lg100ms.statistic;

import fr.yanis.lg100ms.LGMSMain;

import java.util.HashMap;

public class GameStatsUtils {

    public HashMap<String, Object> gameStats = new HashMap<>();

    public GameStatsUtils(HashMap<String, Object> gameStats) {
        this.gameStats = gameStats;
    }

    public int getWinWithRole(String role){
        if(gameStats.get("roleWin") == null) return 0;
        HashMap<String, Integer> roleWin = (HashMap<String, Integer>) gameStats.get("roleWin");
        if(!roleWin.containsKey(role)) return 0;
        return roleWin.get(role);
    }

    public int getWinRateWithRole(String role){
        int win = LGMSMain.getInstance().getGameStatsUtils().getWinWithRole(role);
        if(win == 0 && (int) gameStats.get("nbOfGame") == 0) return 0;
        float calcul = ((float)win / (win + (int) gameStats.get("nbOfGame"))) * 100;
        return (int) calcul;
    }

    public int getWinWithCamp(String camp){
        if(gameStats.get("campWin") == null) return 0;
        HashMap<String, Integer> campWin = (HashMap<String, Integer>) gameStats.get("campWin");
        if(!campWin.containsKey(camp)) return 0;
        return campWin.get(camp);
    }

    public int getWinRateWithCamp(String camp){
        int win = LGMSMain.getInstance().getGameStatsUtils().getWinWithCamp(camp);
        if(win == 0 && (int) gameStats.get("nbOfGame") == 0) return 0;
        float calcul = ((float)win / (win + (int) gameStats.get("nbOfGame"))) * 100;
        return (int) calcul;
    }

    public int getDureeMoyennePartie(){
        if(gameStats.get("duree_partie_average") == null) return 0;
        return (int) gameStats.get("duree_partie_average");
    }

    public String getLastCampWin(){
        if(gameStats.get("lastCamp") == null) return "Aucun";
        return (String) gameStats.get("lastCamp");
    }

    public int getNbOfGame(){
        return (int) gameStats.get("totalOfGame");
    }

}
