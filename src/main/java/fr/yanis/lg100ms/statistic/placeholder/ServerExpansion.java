package fr.yanis.lg100ms.statistic.placeholder;

import fr.yanis.lg100ms.LGMSMain;
import fr.yanis.lg100ms.statistic.GameStatsUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "server";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Sterll";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if(identifier.contains("winrole_")){
            String role = identifier.replace("winrole_", "");
            return String.valueOf(LGMSMain.getInstance().getGameStatsUtils().getWinWithRole(role));
        }
        if(identifier.contains("wincamp_")){
            String camp = identifier.replace("wincamp_", "");
            return String.valueOf(LGMSMain.getInstance().getGameStatsUtils().getWinWithCamp(camp));
        }
        if(identifier.contains("winraterole_")){
            String role = identifier.replace("winraterole_", "");
            return String.valueOf(LGMSMain.getInstance().getGameStatsUtils().getWinRateWithRole(role));
        }
        if(identifier.contains("winratecamp_")){
            String camp = identifier.replace("winratecamp_", "");
            return String.valueOf(LGMSMain.getInstance().getGameStatsUtils().getWinRateWithCamp(camp));
        }
        switch (identifier){
            case "duree_partie_average":
                return String.valueOf(LGMSMain.getInstance().getGameStatsUtils().getDureeMoyennePartie());
            case "last_camp":
                return LGMSMain.getInstance().getGameStatsUtils().getLastCampWin();
            case "nb_of_game":
                return String.valueOf(LGMSMain.getInstance().getGameStatsUtils().getNbOfGame());
            case "top3_winrate_number":
                return LGMSMain.getInstance().getDBUtils().getTop3WinRate().get(3).split(":")[1];
            case "top3_winrate_name":
                return LGMSMain.getInstance().getDBUtils().getTop3WinRate().get(3).split(":")[0];
            case "top2_winrate_number":
                return LGMSMain.getInstance().getDBUtils().getTop3WinRate().get(2).split(":")[1];
            case "top2_winrate_name":
                return LGMSMain.getInstance().getDBUtils().getTop3WinRate().get(2).split(":")[0];
            case "top1_winrate_number":
                return LGMSMain.getInstance().getDBUtils().getTop3WinRate().get(1).split(":")[1];
            case "top1_winrate_name":
                return LGMSMain.getInstance().getDBUtils().getTop3WinRate().get(1).split(":")[0];
        }
        return null;
    }
}
