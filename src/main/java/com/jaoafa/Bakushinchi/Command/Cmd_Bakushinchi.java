package com.jaoafa.Bakushinchi.Command;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

public class Cmd_Bakushinchi implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("claim")) {
				String id = args[1];
				WorldEditPlugin we = getWorldEdit();
				WorldGuardPlugin wg = getWorldGuard();

				try {
					Region region = we.getSelection(player).getRegionSelector().getRegion();
					ProtectedRegion protectedregion = getProtectedRegion(region, id);

					if (protectedregion == null) {
						player.sendMessage(
								"[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。範囲タイプが非対応です。");
						return true;
					}

					RegionManager rm = wg.getRegionManager(player.getWorld());
					ApplicableRegionSet regionlist = rm.getApplicableRegions(protectedregion);

					if (regionlist.size() == 0) {
						// 保護できない
						player.sendMessage(
								"[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。このコマンドは爆新地内でのみ使用できます。");
						return true;
					}

					List<ProtectedRegion> inheritance = new LinkedList<ProtectedRegion>();
					ProtectedRegion lastregion = null;
					Iterator<ProtectedRegion> iterator = regionlist.iterator();
					while (iterator.hasNext()) {
						ProtectedRegion r = iterator.next();
						if (lastregion == null) {
							lastregion = r;
						}
						inheritance.add(r);
					}
					Collections.reverse(inheritance);
					ProtectedRegion firstregion = inheritance.get(0);

					if (!firstregion.getId().equalsIgnoreCase("Bakushinchi")) {
						// 爆新地じゃない
						player.sendMessage(
								"[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。このコマンドは爆新地内でのみ使用できます。("
										+ firstregion.getId() + ")");
						return true;
					}

					if (!firstregion.getId().equals(lastregion.getId())) {
						// ラストが爆新地じゃない
						player.sendMessage(
								"[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。次の範囲と被っています: ("
										+ lastregion.getId() + ")");
						return true;
					}

					protectedregion.setParent(firstregion);
					DefaultDomain owners = new DefaultDomain();
					owners.addPlayer(player.getUniqueId());
					protectedregion.setOwners(owners);
					wg.getRegionContainer().get(Bukkit.getWorld(region.getWorld().getName()))
							.addRegion(protectedregion);
				} catch (IncompleteRegionException e) {
					player.sendMessage(
							"[BAKUSHINCHI] " + ChatColor.GREEN + "範囲が選択されていません。");
					return true;
				} catch (CircularInheritanceException e) {
					player.sendMessage(
							"[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。" + e.getMessage());
					return true;
				}
			}
		}

		return true;
	}

	protected static ProtectedRegion getProtectedRegion(Region region, String id)
			throws IncompleteRegionException {

		// Detect the type of region from WorldEdit
		if (region instanceof Polygonal2DRegion) {
			Polygonal2DRegion polySel = (Polygonal2DRegion) region;
			int minY = polySel.getMinimumPoint().getBlockY();
			int maxY = polySel.getMaximumPoint().getBlockY();
			return new ProtectedPolygonalRegion(id, polySel.getPoints(), minY, maxY);
		} else if (region instanceof CuboidRegion) {
			BlockVector min = region.getMinimumPoint().toBlockVector();
			BlockVector max = region.getMaximumPoint().toBlockVector();
			return new ProtectedCuboidRegion(id, min, max);
		} else {
			return null;
		}
	}

	/**
	 * Get a WorldEdit selection for a player, or emit an exception if there is none
	 * available.
	 *
	 * @param player the player
	 * @return the selection
	 * @throws CommandException thrown on an error
	 */
	protected static Region checkSelection(WorldEditPlugin we, Player player) throws IncompleteRegionException {
		return we.getSelection(player).getRegionSelector()
				.getRegion();
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}

	private WorldEditPlugin getWorldEdit() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			return null;
		}

		return (WorldEditPlugin) plugin;
	}
}
