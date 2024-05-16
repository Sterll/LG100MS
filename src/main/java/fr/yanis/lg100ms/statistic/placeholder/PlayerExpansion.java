package fr.yanis.lg100ms.statistic.placeholder;

import fr.yanis.lg100ms.LGMSMain;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "100msplayer";
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
        if(player == null) return null;
        if(LGMSMain.getInstance().getStatsUtils().getPlayerStats(player.getPlayer()) == null) return null;
        if(identifier.contains("winrole_")){
            String role = identifier.replace("winrole_", "");
            return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinWithRole(player.getPlayer(), role + ".display"));
        }
        if(identifier.contains("loserole_")){
            String role = identifier.replace("loserole_", "");
            return String.valueOf(LGMSMain.getInstance().getStatsUtils().getLoseWithRole(player.getPlayer(), role + ".display"));
        }
        if(identifier.contains("winraterole_")){
            String role = identifier.replace("winraterole_", "");
            return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinRateWithRole(player.getPlayer(), role + ".display"));
        }
        if(identifier.contains("wincamp_")){
            String camp = identifier.replace("wincamp_", "");
            return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinWithCamp(player.getPlayer(), camp));
        }
        if(identifier.contains("losecamp_")){
            String camp = identifier.replace("losecamp_", "");
            return String.valueOf(LGMSMain.getInstance().getStatsUtils().getLoseWithCamp(player.getPlayer(), camp));
        }
        switch (identifier){
            case "gameplayed":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWin(player.getPlayer()) + LGMSMain.getInstance().getStatsUtils().getLose(player.getPlayer()));
            case "death_time_average":
                return LGMSMain.getInstance().getStatsUtils().getDeathTimeAverage(player.getPlayer());
            case "win":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWin(player.getPlayer()));
            case "lose":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getLose(player.getPlayer()));
            case "winrate":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinRate(player.getPlayer()));
            case "win_werewolf":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinWithCamp(player.getPlayer(), "werewolf.categories.werewolf"));
            case "lose_werewolf":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getLoseWithCamp(player.getPlayer(), "werewolf.categories.werewolf"));
            case "winrate_werewolf":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinRateWithCamp(player.getPlayer(), "werewolf.categories.werewolf"));
            case "win_villager":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinWithCamp(player.getPlayer(), "werewolf.categories.villager"));
            case "lose_villager":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getLoseWithCamp(player.getPlayer(), "werewolf.categories.villager"));
            case "winrate_villager":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinRateWithCamp(player.getPlayer(), "werewolf.categories.villager"));
            case "win_neutral":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinWithCamp(player.getPlayer(), "werewolf.categories.neutral"));
            case "lose_neutral":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getLoseWithCamp(player.getPlayer(), "werewolf.categories.neutral"));
            case "winrate_neutral":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinRateWithCamp(player.getPlayer(), "werewolf.categories.neutral"));
            case "win_lover":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinWithCamp(player.getPlayer(), "werewolf.categories.lover"));
            case "lose_lover":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getLoseWithCamp(player.getPlayer(), "werewolf.categories.lover"));
            case "winrate_lover":
                return String.valueOf(LGMSMain.getInstance().getStatsUtils().getWinRateWithCamp(player.getPlayer(), "werewolf.categories.lover"));
        }
        return null;
    }
}
