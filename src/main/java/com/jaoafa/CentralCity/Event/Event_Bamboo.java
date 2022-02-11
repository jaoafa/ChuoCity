package com.jaoafa.CentralCity.Event;

import com.jaoafa.CentralCity.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class Event_Bamboo implements Listener {
    @EventHandler
    public void onBlockSpreadEvent(BlockSpreadEvent event) {
        if (!Main.isCentralCity(event.getBlock().getLocation())) {
            return;
        }
        if (event.getSource().getType() != Material.BAMBOO && event.getSource().getType() != Material.BAMBOO_SAPLING) {
            return;
        }
        event.setCancelled(true);
    }
}
