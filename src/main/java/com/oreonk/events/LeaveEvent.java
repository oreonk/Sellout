package com.oreonk.events;

import com.oreonk.Sellout;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {
    @EventHandler
    public void leaveEvent(PlayerQuitEvent event){
        Sellout.getInstance().privateItems.remove(event.getPlayer());
        Sellout.getInstance().privateTimers.remove(event.getPlayer());
    }
}
