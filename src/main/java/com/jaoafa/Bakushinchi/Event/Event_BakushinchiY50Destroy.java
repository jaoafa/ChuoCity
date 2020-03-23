package com.jaoafa.Bakushinchi.Event;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import com.jaoafa.Bakushinchi.PermissionsManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Event_BakushinchiY50Destroy implements Listener {
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAntiBlockUnderDestroy(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location loc = block.getLocation();
		World world = loc.getWorld();
		int y = loc.getBlockY();

		if (!world.getName().equalsIgnoreCase("Jao_Afa")) {
			return; // Jao_Afa以外では適用しない
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

		if (y > 50) {
			return;
		}

		String group = PermissionsManager.getPermissionMainGroup(player);
		if (!group.equalsIgnoreCase("Default") && !group.equalsIgnoreCase("Verified")) {
			return; // QD以外は特に規制設けない
		}
		player.sendMessage("[BlockDestroy] " + ChatColor.RED
				+ "直下掘り制限を回避したりするプレイヤーの影響により、Default・Verified権限によるY50未満の採掘はできなくなりました。");
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
