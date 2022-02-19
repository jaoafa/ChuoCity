package com.jaoafa.ChuoCity.Event;

import com.jaoafa.ChuoCity.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class Event_WGNonProtection implements Listener {
    @Subscribe
    public void onWEEdit(EditSessionEvent event) {
        if (event.getStage() == EditSession.Stage.BEFORE_REORDER) {
            return;
        }

        Actor actor = event.getActor();
        if (actor == null || !actor.isPlayer()) {
            return;
        }

        Player player = Bukkit.getPlayer(actor.getUniqueId());
        if (player == null) {
            return;
        }

        if (player.hasPermission("worldguard.region.bypass")) {
            return;
        }
        event.setExtent(new AbstractDelegateExtent(event.getExtent()) {
            private boolean canBuild(int x, int y, int z) {
                if (Main.isChuoCity(new Location(player.getWorld(), x, y, z))) {
                    return true;
                }
                if (!player.getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
                    return true; // Jao_Afa以外では適用しない
                }
                ProtectedRegion region = Main.getTopRegion(new Location(player.getWorld(), x, y, z));
                if (region == null) {
                    return false; // 誰も保護していない
                }
                if (region.getOwners().contains(player.getUniqueId())) {
                    return true; // オーナー
                }

                return region.getMembers().contains(player.getUniqueId()); // メンバー
            }

            @Override
            public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
                return canBuild(location.getBlockX(), location.getBlockY(), location.getBlockZ()) && super.setBlock(location, block);
            }
        });

    }

    @EventHandler
    public void OnBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        if (!Main.isChuoCity(loc)) {
            return;
        }
        ProtectedRegion region = Main.getTopRegion(loc);
        if (region == null) {
            return;
        }
        if (region.getId().length() == 12 && region.getId().startsWith("chuocity_")) {
            return;
        }
        if (region.getOwners().contains(player.getUniqueId())) {
            return;
        }
        if (region.getMembers().contains(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage("[WGProtection] " + ChatColor.RED + "あなたがブロックを設置しようとした場所は保護されているため編集できません。他の開いている土地を探しましょう！");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        if (!Main.isChuoCity(loc)) {
            return;
        }
        ProtectedRegion region = Main.getTopRegion(loc);
        if (region == null) {
            return;
        }
        if (region.getId().equalsIgnoreCase("ChuoCity")) {
            return;
        }
        if (region.getOwners().contains(player.getUniqueId())) {
            return;
        }
        if (region.getMembers().contains(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage("[WGProtection] " + ChatColor.RED + "あなたがブロックを破壊しようとした場所は保護されているため編集できません。他の開いている土地を探しましょう！");
    }
}