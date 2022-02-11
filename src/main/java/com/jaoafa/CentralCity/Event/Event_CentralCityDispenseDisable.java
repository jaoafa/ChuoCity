package com.jaoafa.CentralCity.Event;

import com.jaoafa.CentralCity.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

public class Event_CentralCityDispenseDisable implements Listener {
    @EventHandler
    public void onBlockDispenseEvent(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() != Material.TNT) {
            return;
        }

        if (!Main.isCentralCity(event.getBlock().getLocation())) {
            return;
        }

        event.setCancelled(true);
    }
}
