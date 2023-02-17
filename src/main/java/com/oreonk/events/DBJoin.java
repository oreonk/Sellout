package com.oreonk.events;

import com.oreonk.ColorUtil;
import com.oreonk.Sellout;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DBJoin implements Listener {
    @EventHandler
    public void update(PlayerJoinEvent event){
        //Чек если игрок заходит в первый раз
        if (Sellout.getInstance().getDatabase().infoInsertedLocal(event.getPlayer())) {
            Sellout.getInstance().privateSelloutInsert(event.getPlayer());
        } else {
            Sellout.getInstance().privateSelloutReset(event.getPlayer());
        }
        LocalDateTime now = LocalDateTime.now();
        if (ChronoUnit.MINUTES.between(Sellout.getInstance().privateTimers.get(event.getPlayer()), now) >= Integer.parseInt(Sellout.getInstance().getConfig().getString("Timings.Private"))) {
            Sellout.getInstance().privateSelloutReset(event.getPlayer());
        }
    }
}
