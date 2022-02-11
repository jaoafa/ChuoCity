package com.jaoafa.CentralCity.Event;

import com.jaoafa.CentralCity.Main;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class Event_Beehive implements Listener {
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (!Main.isCentralCity(event.getLocation())) {
            return;
        }
        if (event.getEntity().getType() != EntityType.BEE) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BEEHIVE) {
            return;
        }
        event.setCancelled(true);
    }
}
