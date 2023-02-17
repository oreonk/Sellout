package com.oreonk.commands;

import com.oreonk.ColorUtil;
import com.oreonk.Msg;
import com.oreonk.Sellout;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (Sellout.getInstance().getConfig().getString("Util.Perms.admin") == null) {
                    Msg.send(player, ChatColor.RED + "Ошибка в ведённом перме Util.Perms.admin");
                    return true;
                }
                if (!player.hasPermission(Sellout.getInstance().getConfig().getString("Util.Perms.admin"))) {
                    return true;
                }
                if (Sellout.getInstance().getConfig().getString("Util.Commands.all") == null) {
                    Msg.send(player, ChatColor.RED + "Ошибка в введённом начале команды Util.Commands.all");
                    return true;
                }
                if (arguments.length == 1 && arguments[0].equals("help")) {
                    Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + label + " help" + ChatColor.WHITE + " - список команд");
                    Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + label + ChatColor.WHITE + " - открыть меню скупщика");
                    Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + label + " reload" + ChatColor.WHITE + " - перезагрузка конфигурации плагина");
                    Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + label + " restart" + ChatColor.WHITE + " - обновить все скупки");
                    Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + label + " restart local *ник*" + ChatColor.WHITE + " - обновить локальные скупки игрока");
                    Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + label + " restart global" + ChatColor.WHITE + " - обновить глобальную скупку");
                    Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + label + " open *ник*" + ChatColor.WHITE + " - открыть игроку меню скупщика");
                    return true;
                }else if (arguments.length == 0) {
                    String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                    if (guiName == null) {
                        guiName = "Скупщик";
                    }
                    Inventory gui = Bukkit.createInventory(player, 54, guiName);
                    ArrayList<String> public_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("public_items");
                    new BukkitRunnable() {
                        public void run() {
                            String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                            if (guiName == null) {
                                guiName = "Скупщик";
                            }
                            if (!player.getOpenInventory().getTitle().equals(guiName)) {
                                this.cancel();
                            } else {
                                int i = 0;
                                for (Map.Entry<String, Integer> entry : Sellout.getInstance().publicItems.entrySet()) {
                                    ItemStack itemStack = new ItemStack(Material.valueOf(entry.getKey()));
                                    itemStack.setAmount(entry.getValue());
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    ArrayList<String> itemLore = new ArrayList<>();
                                    itemLore.add("");
                                    for (String addString : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("local_info")) {
                                        addString = addString.replace("%amount%", String.valueOf(entry.getValue()));
                                        addString = addString.replace("%price%", Sellout.getInstance().getConfig().getString("Items.Public." + itemStack.getType().toString()));
                                        LocalDateTime now = LocalDateTime.now();
                                        LocalDateTime timerPublic = Sellout.getInstance().publicTimer;
                                        timerPublic = timerPublic.plusMinutes(Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Public")));
                                        addString = addString.replace("%public_reset_time%", String.valueOf(ChronoUnit.MINUTES.between(now, timerPublic)));
                                        itemLore.add(ColorUtil.translateColorCodes(addString));
                                    }
                                    itemMeta.setLore(itemLore);
                                    itemStack.setItemMeta(itemMeta);
                                    gui.setItem(Integer.parseInt(public_item_positions.get(i)), itemStack);
                                    i ++;
                                    if (i == 8){
                                        i = 0;
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Sellout.getInstance(), 0, 20);
                    //31 32 33
                    ItemStack placeholder = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    ItemMeta meta = placeholder.getItemMeta();
                    meta.setDisplayName(" ");
                    placeholder.setItemMeta(meta);
                    ArrayList<String> placeholder_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("placeholder_items");
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(0)), placeholder);
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(1)), placeholder);
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(2)), placeholder);
                    //40 41 42
                    ArrayList<String> private_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("private_items");
                    new BukkitRunnable() {
                        public void run() {
                            String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                            if (guiName == null) {
                                guiName = "Скупщик";
                            }
                            if (!player.getOpenInventory().getTitle().equals(guiName)) {
                                this.cancel();
                            } else {
                                int i = 0;
                                for (ItemStack stack : Sellout.getInstance().privateItems.get(player)) {
                                    ItemMeta stack_meta = stack.getItemMeta();
                                    ArrayList<String> stack_lore = new ArrayList<>();
                                    stack_lore.add("");
                                    for (String addString : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("private_info")) {
                                        addString = addString.replace("%amount%", String.valueOf(stack.getAmount()));
                                        addString = addString.replace("%price%", Sellout.getInstance().getConfig().getString("Items.Public." + stack.getType().toString()));
                                        LocalDateTime now = LocalDateTime.now();
                                        LocalDateTime timerPrivate = Sellout.getInstance().privateTimers.get(player);
                                        timerPrivate = timerPrivate.plusMinutes(Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Private")));
                                        addString = addString.replace("%private_reset_time%", String.valueOf(ChronoUnit.MINUTES.between(now, timerPrivate)));
                                        stack_lore.add(ColorUtil.translateColorCodes(addString));
                                    }
                                    stack_meta.setLore(stack_lore);
                                    stack.setItemMeta(stack_meta);
                                    gui.setItem(Integer.parseInt(private_item_positions.get(i)), stack);
                                    i ++;
                                    if (i == 3){
                                        i = 0;
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Sellout.getInstance(), 0, 20);
                    int position = Integer.parseInt(Sellout.getInstance().getConfig().getString("Util.Menu.info_position"));
                    ItemStack info = new ItemStack(Material.BOOK);
                    ItemMeta info_meta = info.getItemMeta();
                    ArrayList<String> info_lore = new ArrayList<>();
                    info_meta.setDisplayName(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.info_name")));
                    info_lore.add("");
                    List<String> info_lore_config;
                    info_lore_config = Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("info");
                    for (String string : info_lore_config) {
                        string = string.replace("%sender%", player.getName());
                        info_lore.add(ColorUtil.translateColorCodes(string));
                        info_meta.setLore(info_lore);
                        info.setItemMeta(info_meta);
                        gui.setItem(position, info);
                    }

                    ItemStack updatePrivate = new ItemStack(Material.REDSTONE_TORCH);
                    ItemMeta updateMeta = updatePrivate.getItemMeta();
                    ArrayList<String> updateLore = new ArrayList<>();
                    updateLore.add("");
                    for (String path : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("update_info")) {
                        updateLore.add(ColorUtil.translateColorCodes(path));
                    }
                    updateMeta.setDisplayName(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.update_name")));
                    updateMeta.setLore(updateLore);
                    updatePrivate.setItemMeta(updateMeta);
                    gui.setItem(Integer.parseInt(Sellout.getInstance().getConfig().getString("Util.Menu.update_position")), updatePrivate);
                    player.openInventory(gui);
                    return true;
                } else if (arguments.length == 2 && arguments[0].equals("open") && Bukkit.getPlayer(arguments[1]) != null) {
                    String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                    if (guiName == null) {
                        guiName = "Скупщик";
                    }
                    Inventory gui = Bukkit.createInventory(Bukkit.getPlayer(arguments[1]), 54, guiName);
                    ArrayList<String> public_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("public_items");
                    new BukkitRunnable() {
                        public void run() {
                            String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                            if (guiName == null) {
                                guiName = "Скупщик";
                            }
                            if (!Bukkit.getPlayer(arguments[1]).getOpenInventory().getTitle().equals(guiName)) {
                                this.cancel();
                            } else {
                                int i = 0;
                                for (Map.Entry<String, Integer> entry : Sellout.getInstance().publicItems.entrySet()) {
                                    ItemStack itemStack = new ItemStack(Material.valueOf(entry.getKey()));
                                    itemStack.setAmount(entry.getValue());
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    ArrayList<String> itemLore = new ArrayList<>();
                                    itemLore.add("");
                                    for (String addString : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("local_info")) {
                                        addString = addString.replace("%amount%", String.valueOf(entry.getValue()));
                                        addString = addString.replace("%price%", Sellout.getInstance().getConfig().getString("Items.Public." + itemStack.getType().toString()));
                                        LocalDateTime now = LocalDateTime.now();
                                        LocalDateTime timerPublic = Sellout.getInstance().publicTimer;
                                        timerPublic = timerPublic.plusMinutes(Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Public")));
                                        addString = addString.replace("%public_reset_time%", String.valueOf(ChronoUnit.MINUTES.between(now, timerPublic)));
                                        itemLore.add(ColorUtil.translateColorCodes(addString));
                                    }
                                    itemMeta.setLore(itemLore);
                                    itemStack.setItemMeta(itemMeta);
                                    gui.setItem(Integer.parseInt(public_item_positions.get(i)), itemStack);
                                    i ++;
                                    if (i == 8){
                                        i = 0;
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Sellout.getInstance(), 0, 20);
                    //31 32 33
                    ItemStack placeholder = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    ItemMeta meta = placeholder.getItemMeta();
                    meta.setDisplayName(" ");
                    placeholder.setItemMeta(meta);
                    ArrayList<String> placeholder_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("placeholder_items");
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(0)), placeholder);
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(1)), placeholder);
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(2)), placeholder);
                    //40 41 42
                    ArrayList<String> private_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("private_items");
                    new BukkitRunnable() {
                        public void run() {
                            String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                            if (guiName == null) {
                                guiName = "Скупщик";
                            }
                            if (!Bukkit.getPlayer(arguments[1]).getOpenInventory().getTitle().equals(guiName)) {
                                this.cancel();
                            } else {
                                int i = 0;
                                for (ItemStack stack : Sellout.getInstance().privateItems.get(Bukkit.getPlayer(arguments[1]))) {
                                    ItemMeta stack_meta = stack.getItemMeta();
                                    ArrayList<String> stack_lore = new ArrayList<>();
                                    stack_lore.add("");
                                    for (String addString : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("private_info")) {
                                        addString = addString.replace("%amount%", String.valueOf(stack.getAmount()));
                                        addString = addString.replace("%price%", Sellout.getInstance().getConfig().getString("Items.Public." + stack.getType().toString()));
                                        LocalDateTime now = LocalDateTime.now();
                                        LocalDateTime timerPrivate = Sellout.getInstance().privateTimers.get(Bukkit.getPlayer(arguments[1]));
                                        timerPrivate = timerPrivate.plusMinutes(Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Private")));
                                        addString = addString.replace("%private_reset_time%", String.valueOf(ChronoUnit.MINUTES.between(now, timerPrivate)));
                                        stack_lore.add(ColorUtil.translateColorCodes(addString));
                                    }
                                    stack_meta.setLore(stack_lore);
                                    stack.setItemMeta(stack_meta);
                                    gui.setItem(Integer.parseInt(private_item_positions.get(i)), stack);
                                    i ++;
                                    if (i == 3){
                                        i = 0;
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Sellout.getInstance(), 0, 20);
                    int position = Integer.parseInt(Sellout.getInstance().getConfig().getString("Util.Menu.info_position"));
                    ItemStack info = new ItemStack(Material.BOOK);
                    ItemMeta info_meta = info.getItemMeta();
                    ArrayList<String> info_lore = new ArrayList<>();
                    info_meta.setDisplayName(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.info_name")));
                    info_lore.add("");
                    List<String> info_lore_config;
                    info_lore_config = Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("info");
                    for (String string : info_lore_config) {
                        string = string.replace("%sender%", Bukkit.getPlayer(arguments[1]).getName());
                        info_lore.add(ColorUtil.translateColorCodes(string));
                        info_meta.setLore(info_lore);
                        info.setItemMeta(info_meta);
                        gui.setItem(position, info);
                    }

                    ItemStack updatePrivate = new ItemStack(Material.REDSTONE_TORCH);
                    ItemMeta updateMeta = updatePrivate.getItemMeta();
                    ArrayList<String> updateLore = new ArrayList<>();
                    updateLore.add("");
                    for (String path : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("update_info")) {
                        updateLore.add(ColorUtil.translateColorCodes(path));
                    }
                    updateMeta.setDisplayName(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.update_name")));
                    updateMeta.setLore(updateLore);
                    updatePrivate.setItemMeta(updateMeta);
                    gui.setItem(Integer.parseInt(Sellout.getInstance().getConfig().getString("Util.Menu.update_position")), updatePrivate);
                    Bukkit.getPlayer(arguments[1]).openInventory(gui);
                    return true;
                } else if (arguments.length == 2 && arguments[0].equals("restart") && arguments[1].equals("global")) {
                    Sellout.getInstance().publicSelloutHandle();
                    return true;
                } else if (arguments.length == 1 && arguments[0].equals("restart")) {
                    for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
                        Sellout.getInstance().privateSelloutReset(player1);
                    }
                    Sellout.getInstance().publicSelloutHandle();
                    return true;
                } else if (arguments.length == 3 && arguments[0].equals("restart") && arguments[1].equals("local") && Bukkit.getPlayer(arguments[2]) == null) {
                    Msg.send(player, ChatColor.RED + "Игрока с таким ником нет!");
                    return true;
                } else if (arguments.length == 3 && arguments[0].equals("restart") && arguments[1].equals("local") && Bukkit.getPlayer(arguments[2]) != null) {
                    Sellout.getInstance().privateSelloutReset(Bukkit.getPlayer(arguments[2]));
                    return true;
                } else if (arguments.length == 1 && arguments[0].equals("reload")) {
                    Sellout.getInstance().reloadConfig();
                    return true;
                }
            } else {
                if (Sellout.getInstance().getConfig().getString("Util.Perms.admin") == null) {
                    Bukkit.getConsoleSender().sendMessage("Ошибка в ведённом перме Util.Perms.admin");
                    return true;
                }
                if (Sellout.getInstance().getConfig().getString("Util.Commands.all") == null) {
                    Bukkit.getConsoleSender().sendMessage("Ошибка в введённом начале команды Util.Commands.all");
                    return true;
                }
                if (arguments.length == 1 && arguments[0].equals("help")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " help" + ChatColor.WHITE + " - список команд");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + ChatColor.WHITE + " - открыть меню скупщика");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " reload" + ChatColor.WHITE + " - перезагрузка конфигурации плагина");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " restart" + ChatColor.WHITE + " - обновить все скупки");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " restart local *ник*" + ChatColor.WHITE + " - обновить локальные скупки игрока");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " restart global" + ChatColor.WHITE + " - обновить глобальную скупку");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " open *ник*" + ChatColor.WHITE + " - открыть игроку меню скупщика");
                    return true;
                }else if (arguments.length == 0) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " help" + ChatColor.WHITE + " - список команд");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + ChatColor.WHITE + " - открыть меню скупщика");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " reload" + ChatColor.WHITE + " - перезагрузка конфигурации плагина");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " restart" + ChatColor.WHITE + " - обновить все скупки");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " restart local *ник*" + ChatColor.WHITE + " - обновить локальные скупки игрока");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " restart global" + ChatColor.WHITE + " - обновить глобальную скупку");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "/" + label + " open *ник*" + ChatColor.WHITE + " - открыть игроку меню скупщика");
                    return true;
                } else if (arguments.length == 2 && arguments[0].equals("open") && Bukkit.getPlayer(arguments[1]) != null) {
                    String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                    if (guiName == null) {
                        guiName = "Скупщик";
                    }
                    Inventory gui = Bukkit.createInventory(Bukkit.getPlayer(arguments[1]), 54, guiName);
                    ArrayList<String> public_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("public_items");
                    new BukkitRunnable() {
                        public void run() {
                            String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                            if (guiName == null) {
                                guiName = "Скупщик";
                            }
                            if (!Bukkit.getPlayer(arguments[1]).getOpenInventory().getTitle().equals(guiName)) {
                                this.cancel();
                            } else {
                                int i = 0;
                                for (Map.Entry<String, Integer> entry : Sellout.getInstance().publicItems.entrySet()) {
                                    ItemStack itemStack = new ItemStack(Material.valueOf(entry.getKey()));
                                    itemStack.setAmount(entry.getValue());
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    ArrayList<String> itemLore = new ArrayList<>();
                                    itemLore.add("");
                                    for (String addString : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("local_info")) {
                                        addString = addString.replace("%amount%", String.valueOf(entry.getValue()));
                                        addString = addString.replace("%price%", Sellout.getInstance().getConfig().getString("Items.Public." + itemStack.getType().toString()));
                                        LocalDateTime now = LocalDateTime.now();
                                        LocalDateTime timerPublic = Sellout.getInstance().publicTimer;
                                        timerPublic = timerPublic.plusMinutes(Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Public")));
                                        addString = addString.replace("%public_reset_time%", String.valueOf(ChronoUnit.MINUTES.between(now, timerPublic)));
                                        itemLore.add(ColorUtil.translateColorCodes(addString));
                                    }
                                    itemMeta.setLore(itemLore);
                                    itemStack.setItemMeta(itemMeta);
                                    gui.setItem(Integer.parseInt(public_item_positions.get(i)), itemStack);
                                    i ++;
                                    if (i == 8){
                                        i = 0;
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Sellout.getInstance(), 0, 20);
                    //31 32 33
                    ItemStack placeholder = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    ItemMeta meta = placeholder.getItemMeta();
                    meta.setDisplayName(" ");
                    placeholder.setItemMeta(meta);
                    ArrayList<String> placeholder_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("placeholder_items");
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(0)), placeholder);
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(1)), placeholder);
                    gui.setItem(Integer.parseInt(placeholder_item_positions.get(2)), placeholder);
                    //40 41 42
                    ArrayList<String> private_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("private_items");
                    new BukkitRunnable() {
                        public void run() {
                            String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                            if (guiName == null) {
                                guiName = "Скупщик";
                            }
                            if (!Bukkit.getPlayer(arguments[1]).getOpenInventory().getTitle().equals(guiName)) {
                                this.cancel();
                            } else {
                                int i = 0;
                                for (ItemStack stack : Sellout.getInstance().privateItems.get(Bukkit.getPlayer(arguments[1]))) {
                                    ItemMeta stack_meta = stack.getItemMeta();
                                    ArrayList<String> stack_lore = new ArrayList<>();
                                    stack_lore.add("");
                                    for (String addString : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("private_info")) {
                                        addString = addString.replace("%amount%", String.valueOf(stack.getAmount()));
                                        addString = addString.replace("%price%", Sellout.getInstance().getConfig().getString("Items.Public." + stack.getType().toString()));
                                        LocalDateTime now = LocalDateTime.now();
                                        LocalDateTime timerPrivate = Sellout.getInstance().privateTimers.get(Bukkit.getPlayer(arguments[1]));
                                        timerPrivate = timerPrivate.plusMinutes(Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Private")));
                                        addString = addString.replace("%private_reset_time%", String.valueOf(ChronoUnit.MINUTES.between(now, timerPrivate)));
                                        stack_lore.add(ColorUtil.translateColorCodes(addString));
                                    }
                                    stack_meta.setLore(stack_lore);
                                    stack.setItemMeta(stack_meta);
                                    gui.setItem(Integer.parseInt(private_item_positions.get(i)), stack);
                                    i ++;
                                    if (i == 3){
                                        i = 0;
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Sellout.getInstance(), 0, 20);
                    int position = Integer.parseInt(Sellout.getInstance().getConfig().getString("Util.Menu.info_position"));
                    ItemStack info = new ItemStack(Material.BOOK);
                    ItemMeta info_meta = info.getItemMeta();
                    ArrayList<String> info_lore = new ArrayList<>();
                    info_meta.setDisplayName(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.info_name")));
                    info_lore.add("");
                    List<String> info_lore_config;
                    info_lore_config = Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("info");
                    for (String string : info_lore_config) {
                        string = string.replace("%sender%", Bukkit.getPlayer(arguments[1]).getName());
                        info_lore.add(ColorUtil.translateColorCodes(string));
                        info_meta.setLore(info_lore);
                        info.setItemMeta(info_meta);
                        gui.setItem(position, info);
                    }

                    ItemStack updatePrivate = new ItemStack(Material.REDSTONE_TORCH);
                    ItemMeta updateMeta = updatePrivate.getItemMeta();
                    ArrayList<String> updateLore = new ArrayList<>();
                    updateLore.add("");
                    for (String path : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("update_info")) {
                        updateLore.add(ColorUtil.translateColorCodes(path));
                    }
                    updateMeta.setDisplayName(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.update_name")));
                    updateMeta.setLore(updateLore);
                    updatePrivate.setItemMeta(updateMeta);
                    gui.setItem(Integer.parseInt(Sellout.getInstance().getConfig().getString("Util.Menu.update_position")), updatePrivate);
                    Bukkit.getPlayer(arguments[1]).openInventory(gui);
                    Bukkit.getConsoleSender().sendMessage("Вы открыли меню игроку" + Bukkit.getPlayer(arguments[1]).getName());
                    return true;
                } else if (arguments.length == 2 && arguments[0].equals("restart") && arguments[1].equals("global")) {
                    Sellout.getInstance().publicSelloutHandle();
                    return true;
                } else if (arguments.length == 1 && arguments[0].equals("restart")) {
                    for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
                        Sellout.getInstance().privateSelloutReset(player1);
                    }
                    Sellout.getInstance().publicSelloutHandle();
                    return true;
                } else if (arguments.length == 3 && arguments[0].equals("restart") && arguments[1].equals("local") && Bukkit.getPlayer(arguments[2]) == null) {
                    Bukkit.getConsoleSender().sendMessage("Игрока с таким ником нет!");
                    return true;
                } else if (arguments.length == 3 && arguments[0].equals("restart") && arguments[1].equals("local") && Bukkit.getPlayer(arguments[2]) != null) {
                    Sellout.getInstance().privateSelloutReset(Bukkit.getPlayer(arguments[2]));
                    return true;
                } else if (arguments.length == 1 && arguments[0].equals("reload")) {
                    Sellout.getInstance().reloadConfig();
                    return true;
                }
            }
        return true;
    }
}
