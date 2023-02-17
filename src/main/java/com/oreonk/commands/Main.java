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
            if (Sellout.getInstance().getConfig().getString("Util.Perms.admin") == null){
                Msg.send(player, ChatColor.RED +  "Ошибка в ведённом перме Util.Perms.admin");
                return true;
            }
            if (!player.hasPermission(Sellout.getInstance().getConfig().getString("Util.Perms.admin"))) {
                return true;
            }
            if (arguments.length == 0){
                if (Sellout.getInstance().getConfig().getString("Util.Commands.all") == null){
                    Msg.send(player, ChatColor.RED + "Ошибка в введённом начале команды Util.Commands.all");
                    return true;
                }
                String commandName = Sellout.getInstance().getConfig().getString("Util.Commands.all");
                Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + commandName + " reload" + ChatColor.WHITE + " - перезагрузка конфигурации плагина");
                Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + commandName + " restart" + ChatColor.WHITE + " - обновить все скупки");
                Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + commandName + " restart local *ник*" + ChatColor.WHITE + " - обновить локальные скупки игрока");
                Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + commandName + " restart global" + ChatColor.WHITE + " - обновить глобальную скупку");
                Msg.send(player, ChatColor.LIGHT_PURPLE + "/" + commandName + " open *ник*" + ChatColor.WHITE + " - открыть игроку меню скупщика");
                return true;
            } else if (arguments.length == 2 && arguments[0].equals("open") && Bukkit.getPlayer(arguments[1]) != null){
                String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                if (guiName == null){
                    guiName = "Скупщик";
                }
                Inventory gui = Bukkit.createInventory(player, 54, guiName);
                ArrayList<String> public_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("public_items");
                for (Map.Entry<String , Integer> entry : Sellout.getInstance().publicItems.entrySet()){
                    ItemStack itemStack = new ItemStack(Material.valueOf(entry.getKey()));
                    itemStack.setAmount(entry.getValue());
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    ArrayList<String> itemLore = new ArrayList<>();
                    itemLore.add("");
                    String addString = ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.amount"));
                    addString = addString.replace("%amount%",String.valueOf(entry.getValue()));
                    addString = addString.replace("%price%", Sellout.getInstance().getConfig().getString("Items.Public." + itemStack.getType().toString()));
                    itemLore.add(addString);
                    itemLore.add("");
                    itemLore.add(ChatColor.GRAY + "Общее");
                    itemMeta.setLore(itemLore);
                    itemStack.setItemMeta(itemMeta);
                    if (gui.getItem(Integer.parseInt(public_item_positions.get(0))) == null){
                        gui.setItem(Integer.parseInt(public_item_positions.get(0)), itemStack);
                    } else if (gui.getItem(Integer.parseInt(public_item_positions.get(1))) == null){
                        gui.setItem(Integer.parseInt(public_item_positions.get(1)), itemStack);
                    } else if (gui.getItem(Integer.parseInt(public_item_positions.get(2))) == null){
                        gui.setItem(Integer.parseInt(public_item_positions.get(2)), itemStack);
                    } else if (gui.getItem(Integer.parseInt(public_item_positions.get(3))) == null){
                        gui.setItem(Integer.parseInt(public_item_positions.get(3)), itemStack);
                    } else if (gui.getItem(Integer.parseInt(public_item_positions.get(4))) == null){
                        gui.setItem(Integer.parseInt(public_item_positions.get(4)), itemStack);
                    } else if (gui.getItem(Integer.parseInt(public_item_positions.get(5))) == null){
                        gui.setItem(Integer.parseInt(public_item_positions.get(5)), itemStack);
                    } else if (gui.getItem(Integer.parseInt(public_item_positions.get(6))) == null){
                        gui.setItem(Integer.parseInt(public_item_positions.get(6)), itemStack);
                    } else {
                        gui.setItem(Integer.parseInt(public_item_positions.get(7)), itemStack);
                    }
                }

                //31 32 33
                ItemStack placeholder = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta meta = placeholder.getItemMeta();
                meta.setDisplayName(" ");
                placeholder.setItemMeta(meta);
                ArrayList<String> placeholder_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("placeholder_items");
                gui.setItem(Integer.parseInt(placeholder_item_positions.get(0)),placeholder);
                gui.setItem(Integer.parseInt(placeholder_item_positions.get(1)),placeholder);
                gui.setItem(Integer.parseInt(placeholder_item_positions.get(2)),placeholder);
                //40 41 42
                ArrayList<String> private_item_positions = (ArrayList<String>) Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("private_items");
                for (ItemStack stack : Sellout.getInstance().privateItems.get(player)){
                    ItemMeta stack_meta = stack.getItemMeta();
                    ArrayList<String> stack_lore = new ArrayList<>();
                    stack_lore.add("");
                    String addString = ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.amount"));
                    addString = addString.replace("%amount%",String.valueOf(stack.getAmount()));
                    addString = addString.replace("%price%", Sellout.getInstance().getConfig().getString("Items.Public." + stack.getType().toString()));
                    stack_lore.add(addString);
                    stack_lore.add("");
                    stack_lore.add(ChatColor.GRAY + "Личное");
                    stack_meta.setLore(stack_lore);
                    stack.setItemMeta(stack_meta);
                    if (gui.getItem(Integer.parseInt(private_item_positions.get(0))) == null){
                        gui.setItem(Integer.parseInt(private_item_positions.get(0)), stack);
                    } else if (gui.getItem(Integer.parseInt(private_item_positions.get(1))) == null){
                        gui.setItem(Integer.parseInt(private_item_positions.get(1)), stack);
                    } else {
                        gui.setItem(Integer.parseInt(private_item_positions.get(2)), stack);
                    }
                }
                int position = Integer.parseInt(Sellout.getInstance().getConfig().getString("Util.Menu.info_position"));
                new BukkitRunnable() {
                    public void run() {
                        String guiName = Sellout.getInstance().getConfig().getString("Util.Menu.name");
                        if (guiName == null){
                            guiName = "Скупщик";
                        }
                        if (!player.getOpenInventory().getTitle().equals(guiName)){
                            this.cancel();
                        } else {
                            ItemStack info = new ItemStack(Material.BOOK);
                            ItemMeta info_meta = info.getItemMeta();
                            ArrayList<String> info_lore = new ArrayList<>();
                            info_meta.setDisplayName(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.info_name")));
                            info_lore.add("");
                            List<String> info_lore_config;
                            info_lore_config = Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("info");
                            for (String string : info_lore_config) {
                                string = string.replace("%sender%", player.getName());
                                LocalDateTime now = LocalDateTime.now();
                                LocalDateTime timerPublic = Sellout.getInstance().publicTimer;
                                timerPublic = timerPublic.plusSeconds(Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Public")));
                                string = string.replace("%public_reset_time%", String.valueOf(ChronoUnit.SECONDS.between(now, timerPublic)));
                                LocalDateTime timerPrivate = Sellout.getInstance().privateTimers.get(player);
                                timerPrivate = timerPrivate.plusSeconds(Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Private")));
                                string = string.replace("%private_reset_time%", String.valueOf(ChronoUnit.SECONDS.between(now, timerPrivate)));
                                info_lore.add(ColorUtil.translateColorCodes(string));
                                info_meta.setLore(info_lore);
                                info.setItemMeta(info_meta);
                                gui.setItem(position, info);
                            }
                        }
                    }
                }.runTaskTimer(Sellout.getInstance(), 0, 20);
                ItemStack updatePrivate = new ItemStack(Material.REDSTONE_TORCH);
                ItemMeta updateMeta = updatePrivate.getItemMeta();
                ArrayList<String> updateLore = new ArrayList<>();
                updateLore.add("");
                for (String path : Sellout.getInstance().getConfig().getConfigurationSection("Util.Menu").getStringList("update_info")){
                    updateLore.add(ColorUtil.translateColorCodes(path));
                }
                updateMeta.setDisplayName(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.update_name")));
                updateMeta.setLore(updateLore);
                updatePrivate.setItemMeta(updateMeta);
                gui.setItem(Integer.parseInt(Sellout.getInstance().getConfig().getString("Util.Menu.update_position")), updatePrivate);
                player.openInventory(gui);
                return true;
            } else if (arguments.length == 2 && arguments[0].equals("restart") && arguments[1].equals("global")){
                Sellout.getInstance().publicSelloutHandle();
                return true;
            } else if (arguments.length == 1 && arguments[0].equals("restart")){
                for (Player player1: Bukkit.getServer().getOnlinePlayers()) {
                    Sellout.getInstance().privateSelloutReset(player1);
                }
                Sellout.getInstance().publicSelloutHandle();
                return true;
            } else if (arguments.length == 3 && arguments[0].equals("restart") && arguments[1].equals("local") && Bukkit.getPlayer(arguments[2]) == null){
                Msg.send(player, ChatColor.RED + "Игрока с таким ником нет!");
                return true;
            } else if (arguments.length == 3 && arguments[0].equals("restart") && arguments[1].equals("local") && Bukkit.getPlayer(arguments[2]) != null){
                Sellout.getInstance().privateSelloutReset(Bukkit.getPlayer(arguments[2]));
                return true;
            } else if (arguments.length == 1 && arguments[0].equals("reload")){
                Sellout.getInstance().saveConfig();
                return true;
            }
        }
        return true;
    }
}
