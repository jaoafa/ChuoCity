package com.jaoafa.Bakushinchi.Event;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

import com.jaoafa.Bakushinchi.Main;

public class Event_BakushinchiDispenseDisable implements Listener {
	@EventHandler
	public void onBlockDispenseEvent(BlockDispenseEvent event) {
		ItemStack item = event.getItem();
		if (item.getType() != Material.TNT) {
			return;
		}

		if (!Main.isBakushinchi(event.getBlock().getLocation())) {
			return;
		}

		event.setCancelled(true);
	}
}
