package com.jaoafa.Bakushinchi.Event;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Event_BakushinchiDispenseDisable implements Listener {
	@EventHandler
	public void onBlockDispenseEvent(BlockDispenseEvent event) {
		ItemStack item = event.getItem();
		if (item.getType() != Material.TNT) {
			return;
		}
		if (!event.getBlock().getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
			return;
		}
		WorldGuardPlugin wg = getWorldGuard();
		if (wg == null) {
			return;
		}
		RegionManager rm = wg.getRegionManager(event.getBlock().getWorld());
		ApplicableRegionSet regions = rm.getApplicableRegions(event.getBlock().getLocation());
		if (regions.size() == 0) {
			return;
		}
		List<ProtectedRegion> inheritance = new LinkedList<ProtectedRegion>();
		Iterator<ProtectedRegion> iterator = regions.iterator();
		while (iterator.hasNext()) {
			inheritance.add(iterator.next());
		}
		Collections.reverse(inheritance);
		ProtectedRegion firstregion = inheritance.get(0);
		if (!firstregion.getId().equalsIgnoreCase("Bakushinchi")) {
			return;
		}
		event.setCancelled(true);
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}
}
