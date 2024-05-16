package fr.yanis.lg100ms.database;

import com.sun.org.apache.xpath.internal.operations.Bool;
import fr.ph1lou.werewolfapi.enums.Camp;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class DBUtils {

    //===================================
    // Base De Données - Create
    //===================================

    public void addPlayerStat(IPlayerWW player, boolean isWin, int dureePartie, int deathTime) {
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Player (id, id_player, pseudo, role, camp, a_gagner_la_game, duree_partie, death_time) VALUES (?,?,?,?,?,?,?,?)");

            preparedStatement.setInt(1, getMaxIntOfColumn("Player", "id") + 1);
            preparedStatement.setString(2, player.getUUID().toString());
            preparedStatement.setString(3, player.getName());
            preparedStatement.setString(4, player.getRole().getKey());
            preparedStatement.setString(5, player.getRole().getCamp().getKey());
            preparedStatement.setBoolean(6, isWin);
            preparedStatement.setInt(7, dureePartie);
            preparedStatement.setInt(8, deathTime);

            preparedStatement.executeUpdate();

            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void createNewGameStat(int playerCount, int timer, Set<IPlayerWW> players, String camp){
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Game (id_game, nombre_de_joueurs, role_gagnant, camp_gagnant, duree_partie) VALUES (?,?,?,?,?)");

            preparedStatement.setInt(1, getMaxIntOfColumn("Game", "id_game") + 1);
            preparedStatement.setInt(2, playerCount);
            StringBuilder roleGagnant = new StringBuilder();
            players.forEach(player -> roleGagnant.append(player.getRole().getKey()).append(","));
            preparedStatement.setString(3, roleGagnant.substring(0, roleGagnant.toString().length() - 1));
            preparedStatement.setString(4, camp);
            preparedStatement.setInt(5, timer);

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //===================================
    // Base De Données - Vérification
    //===================================

    public boolean ifHaveAccount(Player player) {
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM player_stats WHERE uuid = ?");

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            if(!resultSet.next()) {
                connection.close();
                return false;
            }
            connection.close();
            return true;
        } catch (SQLException event) {
            event.printStackTrace();
            return false;
        }
    }

    public boolean gameStatAlreadyExist(){
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM game_stats");

            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            if(!resultSet.next()) {
                connection.close();
                return false;
            }
            connection.close();
            return true;
        } catch (SQLException event) {
            event.printStackTrace();
            return false;
        }
    }

    //===================================
    // Base De Données - Set
    //===================================

    public void DBSetInfo(String setting, String settingsvalue, String table_name, String where, String whereFinding) {
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + table_name + " SET " + setting + "='" + settingsvalue + "' WHERE " + where + " = ?");

            preparedStatement.setString(1, whereFinding);
            preparedStatement.executeUpdate();

            connection.close();
        } catch (SQLException event) {
            event.printStackTrace();
        }
    }

    //===================================
    // Base De Données - Update
    //===================================

    //===================================
    // Base De Données - Get
    //===================================

    public HashMap<String, Object> getGameStats(){
        HashMap<String, Integer> roleWin = new HashMap<>();
        HashMap<String, Integer> campWin = new HashMap<>();
        try {
            int nbOfGame = 0;
            int time = 0;
            String lastCamp = "";
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Game");

            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            if(resultSet == null) return
                    new HashMap<String, Object>(){{
                        put("roleWin", roleWin);
                        put("campWin", campWin);
                        put("nbOfGame", 0);
                        put("duree_partie_average", 0);
                        put("lastCamp", null);
                        put("totalOfGame", 0);
                    }};

            while (resultSet.next()){
                nbOfGame++;
                time += resultSet.getInt("duree_partie");
                lastCamp = resultSet.getString("camp_gagnant");
                String[] roles = resultSet.getString("role_gagnant").split(",");
                for (String role : roles){
                    if (roleWin.containsKey(role)){
                        roleWin.replace(role, roleWin.get(role) + 1);
                    } else {
                        roleWin.put(role, 1);
                    }
                }
                if (campWin.containsKey(resultSet.getString("camp_gagnant"))){
                    campWin.replace(resultSet.getString("camp_gagnant"), campWin.get(resultSet.getString("camp_gagnant")) + 1);
                } else {
                    campWin.put(resultSet.getString("camp_gagnant"), 1);
                }
            }

            connection.close();
            int finalNbOfGame = nbOfGame;
            int finalTime = time;
            String finalLastCamp = lastCamp;
            return new HashMap<String, Object>(){{
                put("roleWin", roleWin);
                put("campWin", campWin);
                put("nbOfGame", finalNbOfGame);
                put("duree_partie_average", finalTime / finalNbOfGame);
                put("lastCamp", finalLastCamp);
                put("totalOfGame", finalNbOfGame);
            }};

        } catch (SQLException event) {
            event.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(event.getMessage());
            return new HashMap<String, Object>(){{
                put("roleWin", roleWin);
                put("campWin", campWin);
                put("nbOfGame", 0);
                put("duree_partie_average", 0);
                put("lastCamp", null);
                put("totalOfGame", 0);
            }};
        }
    }

    public HashMap<Integer, String> getTop3WinRate(){
        HashMap<Integer, String> players = new HashMap<>();
        players.put(1, "null:0.0");
        players.put(2, "null:0.0");
        players.put(3, "null:0.0");
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Player");

            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()){
                HashMap<String, Object> playerData = getPlayerStats(Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("id_player"))));
                int win = (int) playerData.get("win");
                int lose = (int) playerData.get("lose");
                if (win + lose == 0) continue;
                float winRate = (float) win / (win + lose);
                float first = Float.parseFloat(players.get(1).split(":")[1]);
                float second = Float.parseFloat(players.get(2).split(":")[1]);
                float third = Float.parseFloat(players.get(3).split(":")[1]);
                if (winRate > first){
                    players.replace(3, players.get(2));
                    players.replace(2, players.get(1));
                    players.replace(1, resultSet.getString("pseudo") + ":" + winRate);
                } else if (winRate > second){
                    players.replace(3, players.get(2));
                    players.replace(2, resultSet.getString("pseudo") + ":" + winRate);
                } else if (winRate > third){
                    players.replace(3, resultSet.getString("pseudo") + ":" + winRate);
                }
            }

            connection.close();
            return players;
        } catch (SQLException event) {
            event.printStackTrace();
            return null;
        }
    }

    public HashMap<String, Object> getPlayerStats(Player player){
        HashMap<String, Integer> roleWin = new HashMap<>();
        HashMap<String, Integer> roleLose = new HashMap<>();
        HashMap<String, Integer> campWin = new HashMap<>();
        HashMap<String, Integer> campLose = new HashMap<>();
        int win = 0;
        int lose = 0;
        int deathTime = 0;
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Player WHERE id_player = ?");

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeQuery();
            final ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()){
                if (resultSet.getBoolean("a_gagner_la_game")){
                    if(roleWin.containsKey(resultSet.getString("role"))){
                        roleWin.replace(resultSet.getString("role"), roleWin.get(resultSet.getString("role")) + 1);
                    } else {
                        roleWin.put(resultSet.getString("role"), 1);
                    }
                    if(campWin.containsKey(resultSet.getString("camp"))){
                        campWin.replace(resultSet.getString("camp"), campWin.get(resultSet.getString("camp")) + 1);
                    } else {
                        campWin.put(resultSet.getString("camp"), 1);
                    }
                    win++;
                } else {
                    if(roleLose.containsKey(resultSet.getString("role"))){
                        roleLose.replace(resultSet.getString("role"), roleLose.get(resultSet.getString("role")) + 1);
                    } else {
                        roleLose.put(resultSet.getString("role"), 1);
                    }
                    if(campLose.containsKey(resultSet.getString("camp"))){
                        campLose.replace(resultSet.getString("camp"), campLose.get(resultSet.getString("camp")) + 1);
                    } else {
                        campLose.put(resultSet.getString("camp"), 1);
                    }
                    deathTime += resultSet.getInt("death_time");
                    lose++;
                }
            }

            connection.close();
            int finalWin = win;
            int finalLose = lose;
            int finalDeathTime = deathTime;
            return new HashMap<String, Object>(){{
                put("win", finalWin);
                put("lose", finalLose);
                put("roleWin", roleWin);
                put("roleLose", roleLose);
                put("campWin", campWin);
                put("campLose", campLose);
                if(finalWin == 0 && finalLose == 0){
                    put("deathTime", 0);
                } else {
                    put("deathTime", (finalDeathTime / (finalWin + finalLose)));
                }
            }};
        } catch (SQLException event) {
            event.printStackTrace();
            return null;
        }
    }

    public HashMap<String, Object> getPlayerStats(OfflinePlayer player){
        HashMap<String, Integer> roleWin = new HashMap<>();
        HashMap<String, Integer> roleLose = new HashMap<>();
        HashMap<String, Integer> campWin = new HashMap<>();
        HashMap<String, Integer> campLose = new HashMap<>();
        int win = 0;
        int lose = 0;
        int deathTime = 0;
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Player WHERE id_player = ?");

            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.executeQuery();
            final ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()){
                if (resultSet.getBoolean("a_gagner_la_game")){
                    if(roleWin.containsKey(resultSet.getString("role"))){
                        roleWin.replace(resultSet.getString("role"), roleWin.get(resultSet.getString("role")) + 1);
                    } else {
                        roleWin.put(resultSet.getString("role"), 1);
                    }
                    if(campWin.containsKey(resultSet.getString("camp"))){
                        campWin.replace(resultSet.getString("camp"), campWin.get(resultSet.getString("camp")) + 1);
                    } else {
                        campWin.put(resultSet.getString("camp"), 1);
                    }
                    win++;
                } else {
                    if(roleLose.containsKey(resultSet.getString("role"))){
                        roleLose.replace(resultSet.getString("role"), roleLose.get(resultSet.getString("role")) + 1);
                    } else {
                        roleLose.put(resultSet.getString("role"), 1);
                    }
                    if(campLose.containsKey(resultSet.getString("camp"))){
                        campLose.replace(resultSet.getString("camp"), campLose.get(resultSet.getString("camp")) + 1);
                    } else {
                        campLose.put(resultSet.getString("camp"), 1);
                    }
                    deathTime += resultSet.getInt("death_time");
                    lose++;
                }
            }

            connection.close();
            int finalWin = win;
            int finalLose = lose;
            int finalDeathTime = deathTime;
            return new HashMap<String, Object>(){{
                put("win", finalWin);
                put("lose", finalLose);
                put("roleWin", roleWin);
                put("roleLose", roleLose);
                put("campWin", campWin);
                put("campLose", campLose);
                if(finalWin == 0 && finalLose == 0){
                    put("deathTime", 0);
                } else {
                    put("deathTime", (finalDeathTime / (finalWin + finalLose)));
                }
            }};
        } catch (SQLException event) {
            event.printStackTrace();
            return null;
        }
    }

    public String GetStringInfos(String geting, String table_name, String where, String whereFinding) {
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table_name + " WHERE " + where + " = ?");

            preparedStatement.setString(1, whereFinding);
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                String getted = resultSet.getString(geting);
                connection.close();
                return getted;
            }
            connection.close();
        } catch (SQLException event) {
            event.printStackTrace();
            return null;
        }
        return null;
    }

    public boolean GetBooleanInfos(String geting, String table_name, String where, String whereFinding) {
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table_name + " WHERE " + where + " = ?");

            preparedStatement.setString(1, whereFinding);
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                boolean getted = resultSet.getBoolean(geting);
                connection.close();
                return getted;
            }
            connection.close();
        } catch (SQLException event) {
            event.printStackTrace();
            return false;
        }
        return false;
    }

    public int GetIntInfos(String geting, String table_name, String where, String whereFinding) {
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table_name + " WHERE " + where + " = ?");

            preparedStatement.setString(1, whereFinding);
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                int getted = resultSet.getInt(geting);
                connection.close();
                return getted;
            }
            connection.close();
        } catch (SQLException event) {
            event.printStackTrace();
            return 0;
        }
        return 0;
    }

    public float GetFloatInfos(String geting, String table_name, String where, String whereFinding) {
        try {
            final Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table_name + " WHERE " + where + " = ?");

            preparedStatement.setString(1, whereFinding);
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                float getted = resultSet.getFloat(geting);
                connection.close();
                return getted;
            }
            connection.close();
        } catch (SQLException event) {
            event.printStackTrace();
            return 0.0F;
        }
        return 0.0F;
    }

    public static int getMaxIntOfColumn(String table_name, String column){
        try {
            Connection connection = DatabaseManager.WereWolfBDD.getDatabaseAccess().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT MAX(" + column +") FROM " + table_name);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()){
                int result = rs.getInt(1);
                connection.close();
                return result;
            }
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
}
