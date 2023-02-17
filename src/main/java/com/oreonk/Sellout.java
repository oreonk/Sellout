package com.oreonk;

import com.oreonk.commands.Main;
import com.oreonk.events.DBJoin;
import com.oreonk.events.LeaveEvent;
import com.oreonk.events.MenuInteract;
import com.oreonk.sqlite.DatabaseCommand;
import com.oreonk.sqlite.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Sellout extends JavaPlugin{
    private static Sellout instance;
    private static Economy econ = null;
    private DatabaseCommand db;
    public HashMap<String, Integer> publicItems = new HashMap<>() {};
    public HashMap<Player, ArrayList<ItemStack>> privateItems = new HashMap<>() {};
    public HashMap<Player, LocalDateTime> privateTimers = new HashMap<>() {};
    public LocalDateTime publicTimer;
    @Override
    public void onEnable() {
        instance = this;
        System.out.println("[Sellout] Оно живое..");
        saveDefaultConfig();
        this.db = new SQLite(this);
        this.db.load();
        this.db.createTable();
        if (!this.db.infoInsertedGlobal()){
            publicSelloutHandle();
        }
        setupEconomy();
        this.getCommand("sellout").setExecutor(new Main());
        getServer().getPluginManager().registerEvents(new DBJoin(), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new MenuInteract(), this);
        publicItems = this.db.publicItemsHashmap();
        publicTimer = this.db.publicItemsTime();
        new BukkitRunnable() {
            public void run() {
                for (Map.Entry<Player,LocalDateTime> entry : privateTimers.entrySet()){
                    LocalDateTime now = LocalDateTime.now();
                    if(ChronoUnit.SECONDS.between(entry.getValue(), now) >= Integer.parseInt(getConfig().getString("Timings.Private"))){
                        privateSelloutReset(entry.getKey());
                    }
                }
            }
        }.runTaskTimer(this, 100, 100);
        new BukkitRunnable() {
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                if(ChronoUnit.SECONDS.between(publicTimer, now) >= Integer.parseInt(getConfig().getString("Timings.Public"))){
                    publicSelloutHandle();
                }
            }
        }.runTaskTimer(this, 40, 40);
    }
    public void publicSelloutHandle(){
        ArrayList<String> types = new ArrayList<>(getConfig().getConfigurationSection("Items.Public").getKeys(false));
        ArrayList<ItemStack> items = new ArrayList<>();
        while (items.size() < 8){
            int random = ThreadLocalRandom.current().nextInt(0, types.size());
            String type = types.get(random);
            ItemStack item = new ItemStack(Material.valueOf(type));
            int random1 = ThreadLocalRandom.current().nextInt(0, 3);
            if (random1 == 0) {
                item.setAmount(64);
            } else if (random1 == 1){
                item.setAmount(128);
            } else if (random1 == 2){
                item.setAmount(256);
            }
            items.add(item);
            types.remove(type);
        }
        LocalDateTime now = LocalDateTime.now();
        this.db.updateGlobalSellout(items, now);
        publicItems.clear();
        for (ItemStack item : items){
            publicItems.put(item.getType().toString(), item.getAmount());
        }
        publicTimer = now;
        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            player.sendMessage(ColorUtil.translateColorCodes(getConfig().getString("Util.Messages.Public_update")));
            String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
        }
    }

    public void privateSelloutReset(Player player){
        ArrayList<String> types = new ArrayList<>(this.getConfig().getConfigurationSection("Items.Public").getKeys(false));
        ArrayList<ItemStack> items = new ArrayList<>();
        while (items.size() < 3){
            int random = ThreadLocalRandom.current().nextInt(0, types.size());
            String type = types.get(random);
            ItemStack item = new ItemStack(Material.valueOf(type));
            int random1 = ThreadLocalRandom.current().nextInt(0, 3);
            if (random1 == 0) {
                item.setAmount(64);
            } else if (random1 == 1){
                item.setAmount(128);
            } else if (random1 == 2){
                item.setAmount(256);
            }
            items.add(item);
            types.remove(type);
        }
        LocalDateTime before = LocalDateTime.now();
        this.db.updateLocalSellout(items, player, before);
        if (privateItems.containsKey(player)){
            privateItems.remove(player);
        }
        privateItems.put(player, items);
        if (privateTimers.containsKey(player)){
            privateTimers.remove(player);
        }
        privateTimers.put(player, before);
        player.sendMessage(ColorUtil.translateColorCodes(getConfig().getString("Util.Messages.Private_update")));
        String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
        if (guiName == null){
            guiName = "Скупщик";
        }
    }

    public void privateSelloutInsert(Player player){
        if(!privateItems.containsKey(player)){
            privateItems.putAll(this.db.privateItemsHashmap(player));
        }
        if (!privateTimers.containsKey(player)){
            privateTimers.putAll(this.db.privateTimersHashmap(player));
        }
    }

    public void publicSelloutDisableHandle(){
        ArrayList<ItemStack> items = new ArrayList<>();
        for (Map.Entry<String , Integer> entry : publicItems.entrySet()){
            ItemStack item = new ItemStack(Material.valueOf(entry.getKey()));
            item.setAmount(entry.getValue());
            items.add(item);
        }
        this.db.updateGlobalSellout(items, publicTimer);
    }

    public void privateSelloutDisableHandle(){
        for (Map.Entry<Player, ArrayList<ItemStack>> entry : privateItems.entrySet()) {
            this.db.updateLocalSellout(entry.getValue(), entry.getKey(), privateTimers.get(entry.getKey()));
        }
    }

    public static Economy getEconomy() {return econ;}
    public DatabaseCommand getDatabase(){ return this.db; }
    @Override
    public void onDisable() {
        System.out.println("[Sellout] The world is going dark...goodbye");
        publicSelloutDisableHandle();
        privateSelloutDisableHandle();
    }
    public static Sellout getInstance() { return instance; }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
