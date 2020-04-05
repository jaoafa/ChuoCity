package com.jaoafa.Bakushinchi;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.Bakushinchi.Command.Cmd_Bakushinchi;
import com.jaoafa.Bakushinchi.Event.Event_AntiBlockUnderDestroy;
import com.jaoafa.Bakushinchi.Event.Event_AntiClockRedstone;
import com.jaoafa.Bakushinchi.Event.Event_AntiSnowMan;
import com.jaoafa.Bakushinchi.Event.Event_BakushinchiRailChecker;
import com.jaoafa.Bakushinchi.Event.Event_BakushinchiY50Destroy;
import com.jaoafa.Bakushinchi.Event.Event_PlaceTNT;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main extends JavaPlugin {
	private static Main Main = null;

	/**
	 * プラグインが起動したときに呼び出し
	 * @author mine_book000
	 * @since 2019/10/07
	 */
	@Override
	public void onEnable() {
		setMain(this);

		getCommand("bakushinchi").setExecutor(new Cmd_Bakushinchi());
		getServer().getPluginManager().registerEvents(new Event_BakushinchiRailChecker(), this);
		getServer().getPluginManager().registerEvents(new Event_AntiBlockUnderDestroy(), this);
		getServer().getPluginManager().registerEvents(new Event_BakushinchiY50Destroy(), this);
		getServer().getPluginManager().registerEvents(new Event_AntiClockRedstone(), this);
		getServer().getPluginManager().registerEvents(new Event_PlaceTNT(), this);
		getServer().getPluginManager().registerEvents(new Event_AntiSnowMan(), this);
	}

	public static JavaPlugin getJavaPlugin() {
		return Main;
	}

	public static Main getMain() {
		return Main;
	}

	public static void setMain(Main main) {
		Main = main;
	}

	public static boolean isBakushinchi(Location loc) {
		if (!loc.getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
			return false; // Jao_Afa以外では適用しない
		}

		WorldGuardPlugin wg = getWorldGuard();
		if (wg == null) {
			return false;
		}
		RegionManager rm = wg.getRegionManager(loc.getWorld());
		ApplicableRegionSet regions = rm.getApplicableRegions(loc);
		if (regions.size() == 0) {
			return false;
		}
		List<ProtectedRegion> inheritance = new LinkedList<ProtectedRegion>();
		Iterator<ProtectedRegion> iterator = regions.iterator();
		while (iterator.hasNext()) {
			inheritance.add(iterator.next());
		}
		Collections.reverse(inheritance);
		ProtectedRegion firstregion = inheritance.get(0);
		if (!firstregion.getId().equalsIgnoreCase("Bakushinchi")) {
			return false;
		}
		return true;
	}

	private static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}
}
