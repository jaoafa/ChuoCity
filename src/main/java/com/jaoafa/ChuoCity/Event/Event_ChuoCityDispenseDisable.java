package com.jaoafa.ChuoCity.Event;

import com.jaoafa.ChuoCity.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

public class Event_ChuoCityDispenseDisable implements Listener {
    @EventHandler
    public void onBlockDispenseEvent(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() != Material.TNT) {
            return;
        }

        if (!Main.isChuoCity(event.getBlock().getLocation())) {
            return;
        }

        event.setCancelled(true);
    }
}
