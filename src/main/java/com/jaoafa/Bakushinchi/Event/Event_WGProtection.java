package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Event_WGProtection {
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
        if (player.hasPermission("worldguard.region.bypass")) {
            return;
        }
        event.setExtent(new AbstractDelegateExtent(event.getExtent()) {
            private boolean canBuild(int x, int y, int z) {
                if (!Main.isBakushinchi(new Location(player.getWorld(), x, y, z))) {
                    return true;
                }
                ProtectedRegion region = Main.getTopRegion(new Location(player.getWorld(), x, y, z));
                if (region == null) {
                    return true;
                }
                if (region.getId().equalsIgnoreCase("Bakushinchi")) {
                    return true;
                }
                if (region.getOwners().contains(player.getUniqueId())) {
                    return true;
                }

                return region.getMembers().contains(player.getUniqueId());
            }

            @Override
            public boolean setBlock(Vector coord, BaseBlock block) throws WorldEditException {
                return canBuild(coord.getBlockX(), coord.getBlockY(), coord.getBlockZ()) && super.setBlock(coord, block);
            }
        });

    }
}