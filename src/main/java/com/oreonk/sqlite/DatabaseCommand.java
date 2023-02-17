package com.oreonk.sqlite;

import com.oreonk.Sellout;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public abstract class DatabaseCommand {
    Sellout plugin;
    Connection connection;
    String table = "sellout_private"; //Имя таблицы

    public DatabaseCommand(Sellout instance) {
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void createTable() {
        PreparedStatement ps;
        Connection connection;
        try {
            connection = getSQLConnection();
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS sellout_public "
                    + "(ID INT(10), ITEM_ONE VARCHAR(100), AMOUNT_ONE INT(10), ITEM_TWO VARCHAR(100), AMOUNT_TWO INT(10), ITEM_THREE VARCHAR(100), AMOUNT_THREE INT(10), ITEM_FOUR VARCHAR(100), AMOUNT_FOUR INT(10), ITEM_FIVE VARCHAR(100), AMOUNT_FIVE INT(10), ITEM_SIX VARCHAR(100), AMOUNT_SIX INT(10), ITEM_SEVEN VARCHAR(100), AMOUNT_SEVEN INT(10), ITEM_EIGHT VARCHAR(100), AMOUNT_EIGHT INT(10), TIME VARCHAR(100), PRIMARY KEY(ID))");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public boolean infoInsertedGlobal(){
        Connection connection;
        try {
            connection = getSQLConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM sellout_public WHERE ID=?");
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                rs.close();
                ps.close();
                return true;
            }
            rs.close();
            ps.close();
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    //Полное обновление (в том числе таймера)
    public void updateGlobalSellout(ArrayList<ItemStack> arrayList, LocalDateTime time){
        Connection connection;
        try {
            connection = getSQLConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM sellout_public WHERE ID=?");
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if(infoInsertedGlobal()){
                PreparedStatement ps2 = connection.prepareStatement("REPLACE INTO sellout_public (ID,ITEM_ONE,AMOUNT_ONE,ITEM_TWO,AMOUNT_TWO,ITEM_THREE,AMOUNT_THREE,ITEM_FOUR,AMOUNT_FOUR,ITEM_FIVE,AMOUNT_FIVE,ITEM_SIX,AMOUNT_SIX,ITEM_SEVEN,AMOUNT_SEVEN,ITEM_EIGHT,AMOUNT_EIGHT,TIME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ps2.setInt(1,1);
                ps2.setString(2, arrayList.get(0).getType().toString());
                ps2.setInt(3, arrayList.get(0).getAmount());
                ps2.setString(4, arrayList.get(1).getType().toString());
                ps2.setInt(5, arrayList.get(1).getAmount());
                ps2.setString(6, arrayList.get(2).getType().toString());
                ps2.setInt(7, arrayList.get(2).getAmount());
                ps2.setString(8, arrayList.get(3).getType().toString());
                ps2.setInt(9, arrayList.get(3).getAmount());
                ps2.setString(10, arrayList.get(4).getType().toString());
                ps2.setInt(11, arrayList.get(4).getAmount());
                ps2.setString(12, arrayList.get(5).getType().toString());
                ps2.setInt(13, arrayList.get(5).getAmount());
                ps2.setString(14, arrayList.get(6).getType().toString());
                ps2.setInt(15, arrayList.get(6).getAmount());
                ps2.setString(16, arrayList.get(7).getType().toString());
                ps2.setInt(17, arrayList.get(7).getAmount());
                ps2.setString(18, time.toString());
                ps2.executeUpdate();
                ps2.close();
            } else {
                PreparedStatement ps2 = connection.prepareStatement("INSERT INTO sellout_public (ID,ITEM_ONE,AMOUNT_ONE,ITEM_TWO,AMOUNT_TWO,ITEM_THREE,AMOUNT_THREE,ITEM_FOUR,AMOUNT_FOUR,ITEM_FIVE,AMOUNT_FIVE,ITEM_SIX,AMOUNT_SIX,ITEM_SEVEN,AMOUNT_SEVEN,ITEM_EIGHT,AMOUNT_EIGHT,TIME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ps2.setInt(1,1);
                ps2.setString(2, arrayList.get(0).getType().toString());
                ps2.setInt(3, arrayList.get(0).getAmount());
                ps2.setString(4, arrayList.get(1).getType().toString());
                ps2.setInt(5, arrayList.get(1).getAmount());
                ps2.setString(6, arrayList.get(2).getType().toString());
                ps2.setInt(7, arrayList.get(2).getAmount());
                ps2.setString(8, arrayList.get(3).getType().toString());
                ps2.setInt(9, arrayList.get(3).getAmount());
                ps2.setString(10, arrayList.get(4).getType().toString());
                ps2.setInt(11, arrayList.get(4).getAmount());
                ps2.setString(12, arrayList.get(5).getType().toString());
                ps2.setInt(13, arrayList.get(5).getAmount());
                ps2.setString(14, arrayList.get(6).getType().toString());
                ps2.setInt(15, arrayList.get(6).getAmount());
                ps2.setString(16, arrayList.get(7).getType().toString());
                ps2.setInt(17, arrayList.get(7).getAmount());
                ps2.setString(18, time.toString());
                ps2.executeUpdate();
                ps2.close();
            }
            rs.close();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public HashMap<String, Integer> publicItemsHashmap(){
        Connection connection;
            try {
                connection = getSQLConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM sellout_public WHERE ID=?");
                ps.setInt(1, 1);
                ResultSet rs = ps.executeQuery();
                HashMap<String, Integer> hashMap = new HashMap<>();
                if (rs.next()) {
                    hashMap.put(rs.getString("ITEM_ONE"), rs.getInt("AMOUNT_ONE"));
                    hashMap.put(rs.getString("ITEM_TWO"), rs.getInt("AMOUNT_TWO"));
                    hashMap.put(rs.getString("ITEM_THREE"), rs.getInt("AMOUNT_THREE"));
                    hashMap.put(rs.getString("ITEM_FOUR"), rs.getInt("AMOUNT_FOUR"));
                    hashMap.put(rs.getString("ITEM_FIVE"), rs.getInt("AMOUNT_FIVE"));
                    hashMap.put(rs.getString("ITEM_SIX"), rs.getInt("AMOUNT_SIX"));
                    hashMap.put(rs.getString("ITEM_SEVEN"), rs.getInt("AMOUNT_SEVEN"));
                    hashMap.put(rs.getString("ITEM_EIGHT"), rs.getInt("AMOUNT_EIGHT"));
                    rs.close();
                    ps.close();
                    return hashMap;
                }
                ps.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return new HashMap<>();
    }

    public LocalDateTime publicItemsTime(){
        Connection connection;
            try {
                connection = getSQLConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM sellout_public WHERE ID=?");
                ps.setInt(1, 1);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    LocalDateTime time = LocalDateTime.parse(rs.getString("TIME"));
                    rs.close();
                    ps.close();
                    return time;
                }
                ps.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return LocalDateTime.parse("0");
    }

    public boolean infoInsertedLocal(Player player){
        Connection connection;
        try {
            connection = getSQLConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                rs.close();
                ps.close();
                return true;
            }
            rs.close();
            ps.close();
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    //Полное обновление (в том числе таймера)
    public void updateLocalSellout(ArrayList<ItemStack> arrayList, Player player, LocalDateTime localDateTime){
        Connection connection;
        try {
            connection = getSQLConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if(infoInsertedLocal(player)){
                PreparedStatement ps2 = connection.prepareStatement("REPLACE INTO " + table + " (PLAYER,UUID,ITEM_ONE,AMOUNT_ONE,ITEM_TWO,AMOUNT_TWO,ITEM_THREE,AMOUNT_THREE,TIME) VALUES(?,?,?,?,?,?,?,?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2,player.getUniqueId().toString());
                ps2.setString(3, arrayList.get(0).getType().toString());
                ps2.setInt(4, arrayList.get(0).getAmount());
                ps2.setString(5, arrayList.get(1).getType().toString());
                ps2.setInt(6, arrayList.get(1).getAmount());
                ps2.setString(7, arrayList.get(2).getType().toString());
                ps2.setInt(8, arrayList.get(2).getAmount());
                ps2.setString(9, localDateTime.toString());
                ps2.executeUpdate();
                ps2.close();
            } else {
                PreparedStatement ps2 = connection.prepareStatement("INSERT INTO " + table + " (PLAYER,UUID,ITEM_ONE,AMOUNT_ONE,ITEM_TWO,AMOUNT_TWO,ITEM_THREE,AMOUNT_THREE,TIME) VALUES(?,?,?,?,?,?,?,?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2,player.getUniqueId().toString());
                ps2.setString(3, arrayList.get(0).getType().toString());
                ps2.setInt(4, arrayList.get(0).getAmount());
                ps2.setString(5, arrayList.get(1).getType().toString());
                ps2.setInt(6, arrayList.get(1).getAmount());
                ps2.setString(7, arrayList.get(2).getType().toString());
                ps2.setInt(8, arrayList.get(2).getAmount());
                ps2.setString(9, localDateTime.toString());
                ps2.executeUpdate();
                ps2.close();
            }
            rs.close();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public HashMap<Player, ArrayList<ItemStack>> privateItemsHashmap(Player player){
        Connection connection;
            try {
                connection = getSQLConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
                ps.setString(1, player.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                HashMap<Player, ArrayList<ItemStack>> hashMap = new HashMap<>();
                if (rs.next()) {
                    ItemStack itemStack1 = new ItemStack(Material.valueOf(rs.getString("ITEM_ONE")));
                    itemStack1.setAmount(rs.getInt("AMOUNT_ONE"));
                    ItemStack itemStack2 = new ItemStack(Material.valueOf(rs.getString("ITEM_TWO")));
                    itemStack2.setAmount(rs.getInt("AMOUNT_TWO"));
                    ItemStack itemStack3 = new ItemStack(Material.valueOf(rs.getString("ITEM_THREE")));
                    itemStack3.setAmount(rs.getInt("AMOUNT_THREE"));
                    hashMap.put(player, new ArrayList<>());
                    hashMap.get(player).add(itemStack1);
                    hashMap.get(player).add(itemStack2);
                    hashMap.get(player).add(itemStack3);
                    rs.close();
                    ps.close();
                    return hashMap;
                }
                ps.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return new HashMap<>();
    }

    public HashMap<Player, LocalDateTime> privateTimersHashmap(Player player){
        Connection connection;
            try {
                connection = getSQLConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
                ps.setString(1, player.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();
                HashMap<Player, LocalDateTime> hashMap = new HashMap<>();
                if (rs.next()) {
                    LocalDateTime localDateTime = LocalDateTime.parse(rs.getString("TIME"));
                    hashMap.put(player, localDateTime);
                    rs.close();
                    ps.close();
                    return hashMap;
                }
                ps.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return new HashMap<>();
    }

    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}
