package com.jaoafa.Bakushinchi.Event;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Event_BakushinchiRailChecker implements Listener {
	@EventHandler
	public void OnEvent_RailPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!event.canBuild()) {
			return;
		}
		Block block = event.getBlock();
		if (block.getType() != Material.RAILS && block.getType() != Material.ACTIVATOR_RAIL
				&& block.getType() != Material.DETECTOR_RAIL && block.getType() != Material.POWERED_RAIL) {
			return;
		}
		if (!block.getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
			return;
		}
		WorldGuardPlugin wg = getWorldGuard();
		if (wg == null) {
			return;
		}
		RegionManager rm = wg.getRegionManager(player.getWorld());
		ApplicableRegionSet regions = rm.getApplicableRegions(block.getLocation());
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
		player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "爆新地での鉄道の敷設は禁止しています。見つかった場合、処罰の対象となりますのでご注意ください。");
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}
}
