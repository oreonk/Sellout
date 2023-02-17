package com.oreonk.events;

import com.oreonk.ColorUtil;
import com.oreonk.Msg;
import com.oreonk.Sellout;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MenuInteract implements Listener {
    @EventHandler
    public void clickEvent(InventoryClickEvent event){
        if (event.getView().getTitle().equalsIgnoreCase(Sellout.getInstance().getConfig().getString("Util.Menu.name"))){
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getCurrentItem()!=null){
                Player player = (Player) event.getWhoClicked();
                if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equals(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("Util.Menu.update_name")))){
                    donateUpdate(player);
                }
                if (event.getCurrentItem().hasItemMeta() && !event.getCurrentItem().getItemMeta().hasDisplayName() && !event.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)){
                    if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Общее")) {
                        ItemStack testItemStack = new ItemStack(event.getCurrentItem().getType());
                        testItemStack.setAmount(64);
                        if (player.getInventory().contains(testItemStack)) {
                            if (Sellout.getInstance().publicItems.containsKey(event.getCurrentItem().getType().toString())) {
                                if (Sellout.getInstance().publicItems.get(event.getCurrentItem().getType().toString()) > 0) {
                                    player.getInventory().removeItem(testItemStack);
                                    Sellout.getInstance().getEconomy().depositPlayer(player, Integer.parseInt(Sellout.getInstance().getConfig().getString("Items.Public." + testItemStack.getType().toString())));
                                    int amount = Sellout.getInstance().publicItems.get(event.getCurrentItem().getType().toString());
                                    Sellout.getInstance().publicItems.replace(event.getCurrentItem().getType().toString(), amount-1);
                                } else {
                                    Msg.send(player, ChatColor.RED + "Скупщик больше не скупает этот предмет!");
                                }
                            } else {
                                Msg.send(player, ChatColor.RED + "Этого предмета больше нет в предложениях!");
                                player.closeInventory();
                            }
                        } else {
                            Msg.send(player, ChatColor.RED + "У вас нет подходящего для продажи предмета!");
                            player.closeInventory();
                        }
                    }
                    if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Личное")){
                        ItemStack testItemStack = new ItemStack(event.getCurrentItem().getType());
                        testItemStack.setAmount(64);
                        if (player.getInventory().contains(testItemStack)) {
                            ArrayList<ItemStack> itemsArray = Sellout.getInstance().privateItems.get(player);
                            int amount = -1;
                            for (ItemStack stack:itemsArray){
                                if (stack.getType().equals(testItemStack.getType())){
                                    amount = stack.getAmount();
                                }
                            }
                            if (amount == -1){
                                Msg.send(player, ChatColor.RED + "Этого предмета больше нет в предложениях!");
                                player.closeInventory();
                            } else {
                                if (amount > 0){
                                    player.getInventory().removeItem(testItemStack);
                                    Sellout.getInstance().getEconomy().depositPlayer(player, Integer.parseInt(Sellout.getInstance().getConfig().getString("Items.Public." + testItemStack.getType().toString())));
                                    for (ItemStack stack:itemsArray){
                                        if (stack.getType().equals(testItemStack.getType())){
                                            amount = stack.getAmount();
                                            stack.setAmount(amount-1);
                                        }
                                    }
                                    Sellout.getInstance().privateItems.replace(player, itemsArray);
                                } else {
                                    Msg.send(player, ChatColor.RED + "Скупщик больше не скупает этот предмет!");
                                }
                            }
                        } else {
                            Msg.send(player, ChatColor.RED + "У вас нет подходящего для продажи предмета!");
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }
    public void donateUpdate(Player player) {
        //if (Хватает лаймов){
        //    Sellout.getInstance().privateSelloutReset(player);
        //    player.closeInventory();
        //} else {
        //    player.sendMessage(ColorUtil.translateColorCodes("&#123123Вам не хватает лаймов!"));
        //    player.closeInventory();
        //}
    }
}
