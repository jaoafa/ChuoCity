package com.jaoafa.Bakushinchi.Command;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Cmd_Bakushinchi implements CommandExecutor {

    protected static ProtectedRegion getProtectedRegion(Region region, String id)
            throws IllegalArgumentException {

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "このコマンドはサーバ内から実行してください。");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("claim")) {
                String id = args[1];
                WorldEditPlugin we = getWorldEdit();
                WorldGuardPlugin wg = getWorldGuard();

                if (we == null) {
                    player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "WorldEditが利用できません。何かしらの問題が発生している可能性があります。");
                    return true;
                }
                if (wg == null) {
                    player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "WorldGuardが利用できません。何かしらの問題が発生している可能性があります。");
                    return true;
                }

                try {
                    Selection selection = we.getSelection(player);
                    if (selection == null) {
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "範囲が選択されていません。");
                        return true;
                    }
                    Region region = selection.getRegionSelector().getRegion();
                    ProtectedRegion protectedregion;
                    try {
                        protectedregion = getProtectedRegion(region, id);

                        if (protectedregion == null) {
                            player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。範囲タイプが非対応です。");
                            return true;
                        }
                    } catch (Exception e) {
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。" + e.getClass().getName() + " / " + e.getMessage());
                        return true;
                    }

                    RegionManager rm = wg.getRegionManager(player.getWorld());

                    if (rm.hasRegion(id)) {
                        player.sendMessage(
                                "[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。指定された範囲名は既に使用されています。");
                        return true;
                    }

                    ApplicableRegionSet regionlist = rm.getApplicableRegions(protectedregion);

                    if (regionlist.size() == 0) {
                        // 保護できない
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。このコマンドは爆新地内でのみ使用できます。");
                        return true;
                    }

                    List<ProtectedRegion> inheritance = new LinkedList<>();
                    ProtectedRegion lastregion = null;
                    for (ProtectedRegion r : regionlist) {
                        if (lastregion == null) {
                            lastregion = r;
                        }
                        inheritance.add(r);
                    }
                    if (lastregion == null) {
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。lastregion == null.");
                        return true;
                    }
                    Collections.reverse(inheritance);
                    ProtectedRegion firstregion = inheritance.get(0);

                    if (!firstregion.getId().equalsIgnoreCase("Bakushinchi")) {
                        // 爆新地じゃない
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。このコマンドは爆新地内でのみ使用できます。("
                                + firstregion.getId() + ")");
                        return true;
                    }

                    if (!firstregion.getId().equals(lastregion.getId())) {
                        // ラストが爆新地じゃない
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。次の範囲と被っています: "
                                + lastregion.getId());
                        return true;
                    }

                    //protectedregion.setParent(firstregion);
                    DefaultDomain owners = new DefaultDomain();
                    owners.addPlayer(player.getUniqueId());
                    protectedregion.setOwners(owners);
                    if (region.getWorld() == null) {
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。region.getWorld() == null.");
                        return true;
                    }

                    RegionManager regionManager = wg.getRegionContainer().get(Bukkit.getWorld(region.getWorld().getName()));

                    if (regionManager == null) {
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。regionManager == null.");
                        return true;
                    }
                    regionManager.addRegion(protectedregion);
                    player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "次の名前で保護を設定しました: " + protectedregion.getId() + "\n"
                            + "保護設定編集には/rgコマンドをご利用ください。");
                    if (!((protectedregion.getMinimumPoint().getBlockY() == 0
                            && protectedregion.getMaximumPoint().getBlockY() == 255)
                            || (protectedregion.getMinimumPoint().getBlockY() == 255
                            && protectedregion.getMaximumPoint().getBlockY() == 0))) {
                        player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN
                                + "保護範囲のY値が0～255ではありません。//expand vertを実行して0～255を選択してから保護することをお勧めします。");
                    }
                    return true;
                } catch (IncompleteRegionException e) {
                    player.sendMessage("[BAKUSHINCHI] " + ChatColor.GREEN + "範囲が選択されていません。");
                    return true;
                } /* catch (CircularInheritanceException e) {
					player.sendMessage(
							"[BAKUSHINCHI] " + ChatColor.GREEN + "指定された範囲を保護できません。" + e.getMessage());
					return true;
					}*/
            }
        }
        player.sendMessage(
                "[BAKUSHINCHI] " + ChatColor.GREEN + command.getUsage());
        return true;
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (!(plugin instanceof WorldGuardPlugin)) {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }

    private WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if (!(plugin instanceof WorldEditPlugin)) {
            return null;
        }

        return (WorldEditPlugin) plugin;
    }
}
