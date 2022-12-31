package com.jaoafa.ChuoCity.Command;

import com.jaoafa.ChuoCity.Event.Event_Inspect;
import com.jaoafa.ChuoCity.Main;
import com.jaoafa.ChuoCity.PermissionsManager;
import com.jaoafa.ChuoCity.Tasks.Task_ChuoCityFlat;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Cmd_ChuoCity implements CommandExecutor {

    protected static ProtectedRegion getProtectedRegion(Region region, String id)
            throws IllegalArgumentException {

        // Detect the type of region from WorldEdit
        if (region instanceof Polygonal2DRegion polySel) {
            int minY = polySel.getMinimumPoint().getBlockY();
            int maxY = polySel.getMaximumPoint().getBlockY();
            return new ProtectedPolygonalRegion(id, polySel.getPoints(), minY, maxY);
        } else if (region instanceof CuboidRegion) {
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            return new ProtectedCuboidRegion(id, min, max);
        } else {
            return null;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmd, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[ChuoCity] " + ChatColor.GREEN + "このコマンドはサーバ内から実行してください。");
            return true;
        }

        if (args.length == 2) {
            if ("claim".startsWith(args[0])) {
                String id = args[1];
                WorldEditPlugin we = getWorldEdit();
                WorldGuardPlugin wg = getWorldGuard();

                if (we == null) {
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "WorldEditが利用できません。何かしらの問題が発生している可能性があります。");
                    return true;
                }
                if (wg == null) {
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "WorldGuardが利用できません。何かしらの問題が発生している可能性があります。");
                    return true;
                }

                try {
                    World selectionWorld = we.getSession(player).getSelectionWorld();
                    Region region = we.getSession(player).getSelection(selectionWorld);
                    ProtectedRegion protectedregion;
                    try {
                        protectedregion = getProtectedRegion(region, id);

                        if (protectedregion == null) {
                            player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。範囲タイプが非対応です。");
                            return true;
                        }
                    } catch (Exception e) {
                        player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。" + e.getClass().getName() + " / " + e.getMessage());
                        return true;
                    }

                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager rm = container.get(selectionWorld);
                    if (rm == null) {
                        player.sendMessage(
                                "[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。ワールドの取得に失敗しました。");
                        return true;
                    }

                    if (rm.hasRegion(id)) {
                        player.sendMessage(
                                "[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。指定された範囲名は既に使用されています。");
                        return true;
                    }

                    ApplicableRegionSet regionlist = rm.getApplicableRegions(protectedregion);

                    if (regionlist.size() == 0) {
                        // 保護できない
                        player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。このコマンドは中央市内でのみ使用できます。");
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
                        player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。lastregion == null.");
                        return true;
                    }
                    Collections.reverse(inheritance);
                    ProtectedRegion firstregion = inheritance.get(0);

                    if (!firstregion.getId().startsWith("chuocity_")) {
                        // 中央市じゃない
                        player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。このコマンドは中央市内でのみ使用できます。("
                                + firstregion.getId() + ")");
                        return true;
                    }

                    if (!firstregion.getId().equals(lastregion.getId())) {
                        // ラストが中央市じゃない
                        player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。次の範囲と被っています: "
                                + lastregion.getId());
                        return true;
                    }

                    //protectedregion.setParent(firstregion);
                    DefaultDomain owners = new DefaultDomain();
                    owners.addPlayer(player.getUniqueId());
                    protectedregion.setOwners(owners);
                    if (region.getWorld() == null) {
                        player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "指定された範囲を保護できません。region.getWorld() == null.");
                        return true;
                    }

                    rm.addRegion(protectedregion);
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "次の名前で保護を設定しました: " + protectedregion.getId() + "\n"
                            + "保護設定編集には/rgコマンドをご利用ください。");
                    if (!((protectedregion.getMinimumPoint().getBlockY() == -64
                            && protectedregion.getMaximumPoint().getBlockY() == 319)
                            || (protectedregion.getMinimumPoint().getBlockY() == 319
                            && protectedregion.getMaximumPoint().getBlockY() == -64))) {
                        player.sendMessage("[ChuoCity] " + ChatColor.GREEN
                                + "保護範囲のY値が0～255ではありません。//expand vertを実行して-64～319を選択してから保護することをお勧めします。");
                    }
                    return true;
                } catch (IncompleteRegionException e) {
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "範囲が選択されていません。");
                    return true;
                }
            }
        } else if (args.length == 1) {
            if ("flat".startsWith(args[0])) {
                String group = PermissionsManager.getPermissionMainGroup(player);
                if (!group.equalsIgnoreCase("Admin") && !group.equalsIgnoreCase("Moderator")) {
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "あなたはこのコマンドを使用できません。");
                    return true;
                }

                WorldEditPlugin we = getWorldEdit();

                if (we == null) {
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "WorldEditが利用できません。何かしらの問題が発生している可能性があります。");
                    return true;
                }

                try {
                    World selectionWorld = we.getSession(player).getSelectionWorld();
                    Region region = we.getSession(player).getSelection(selectionWorld);

                    new Task_ChuoCityFlat(player, region, 0).runTaskLater(Main.getJavaPlugin(), 0L);
                    return true;
                } catch (IncompleteRegionException e) {
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "範囲が選択されていません。");
                    return true;
                }
            } else if ("inspect".startsWith(args[0])) {
                boolean isInspect = Event_Inspect.inspects.contains(player.getUniqueId());
                if (isInspect) {
                    Event_Inspect.inspects.remove(player.getUniqueId());
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "インスペクタモードを終了しました。");
                } else {
                    Event_Inspect.inspects.add(player.getUniqueId());
                    player.sendMessage("[ChuoCity] " + ChatColor.GREEN + "インスペクタモードを開始しました。棒で対象ブロックを叩くことで情報を表示します。");
                }
                return true;
            }
        }
        player.sendMessage(
                "[ChuoCity] " + ChatColor.GREEN + command.getUsage());
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
