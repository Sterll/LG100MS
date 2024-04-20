package fr.yanis.lg100ms.database;

import fr.yanis.lg100ms.LGMSMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DatabaseManager {

    WereWolfBDD(new DatabaseCredentials(LGMSMain.getInstance().getConfig().getString("mysql.host"), LGMSMain.getInstance().getConfig().getString("mysql.user"), LGMSMain.getInstance().getConfig().getString("mysql.password"), LGMSMain.getInstance().getConfig().getString("mysql.dbName"), LGMSMain.getInstance().getConfig().getInt("mysql.port")));

    private DatabaseAccess databaseAccess;
    DatabaseManager(DatabaseCredentials credentials){
        this.databaseAccess = new DatabaseAccess(credentials);
    }

    public DatabaseAccess getDatabaseAccess(){
        return databaseAccess;
    }

    public static void initAllDatabaseConnection(){
        for(DatabaseManager databaseManager : values()){
            databaseManager.databaseAccess.initPool();
        }
    }
    public static void closeAllDatabaseConnections(){
        for(DatabaseManager databaseManager : values()){
            databaseManager.databaseAccess.closePool();
        }
    }

}
