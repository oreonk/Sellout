package com.oreonk.events;

import com.oreonk.ColorUtil;
import com.oreonk.Sellout;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class NpcInteract implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event){
        if (event.getHand().equals(EquipmentSlot.HAND) && event.getRightClicked().getCustomName().equalsIgnoreCase(ColorUtil.translateColorCodes(Sellout.getInstance().getConfig().getString("NPC")))){
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "sellout open " + event.getPlayer().getName());
        }
    }
}
