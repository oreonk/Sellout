package com.oreonk.sqlite;

import com.oreonk.Sellout;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends DatabaseCommand {
    String dbname;
    public SQLite(Sellout instance) {
        super(instance);
        dbname = "sellout";
    }
    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS sellout_private (" +
            "`PLAYER` VARCHAR NOT NULL," +
            "`UUID` VARCHAR NOT NULL," +
            "`TIME` VARCHAR NOT NULL," +
            "`ITEM_ONE` VARCHAR NOT NULL," +
            "`AMOUNT_ONE` INT NOT NULL," +
            "`ITEM_TWO` VARCHAR NOT NULL," +
            "`AMOUNT_TWO` INT NOT NULL," +
            "`ITEM_THREE` VARCHAR NOT NULL," +
            "`AMOUNT_THREE` INT NOT NULL," +
            "PRIMARY KEY (`UUID`)" +
            ");";
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}

