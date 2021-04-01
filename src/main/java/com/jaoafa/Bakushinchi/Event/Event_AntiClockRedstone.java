package com.jaoafa.Bakushinchi.Event;

import com.jaoafa.Bakushinchi.Main;
import com.jaoafa.Bakushinchi.PermissionsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.HashMap;
import java.util.Map;

public class Event_AntiClockRedstone implements Listener {
    Map<Location, Long> redstoneclocks = new HashMap<>();
    Map<Location, Integer> rcs_count = new HashMap<>();

    // ピストンは必要に応じて対応。

    @EventHandler
    public void OnRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        if (block.getType() != Material.REDSTONE_WIRE) {
            return;
        }

        if (event.getOldCurrent() != 0 && event.getNewCurrent() != 15) {
            return; // 0から15になる状態、つまりクロック回路
        }

        if (!Main.isBakushinchi(loc)) {
            return;
        }

        long milliSec = System.currentTimeMillis();

        if (!redstoneclocks.containsKey(block.getLocation())) {
            // NASA
            redstoneclocks.put(block.getLocation(), milliSec);
            return;
        }

        // 1秒間に1回未満のクロック回路で3回それが繰り返された場合。

        long milliSec_old = redstoneclocks.get(block.getLocation());
        long sa = milliSec - milliSec_old;
        if (sa > 1000) {
            // 1s (20tick, 1000ms) 以上
            rcs_count.remove(block.getLocation());
            redstoneclocks.remove(block.getLocation());
            return;
        }
        if (rcs_count.containsKey(block.getLocation()) && rcs_count.get(block.getLocation()) <= 3) {
            // あって、3回未満
            rcs_count.put(block.getLocation(), rcs_count.get(block.getLocation()) + 1);
            redstoneclocks.put(block.getLocation(), milliSec);
            return;
        } else if (!rcs_count.containsKey(block.getLocation())) {
            // ない
            rcs_count.put(block.getLocation(), 1);
            redstoneclocks.put(block.getLocation(), milliSec);
            return;
        }
        // あって、5回より上(6回以上)

        rcs_count.remove(block.getLocation());
        redstoneclocks.remove(block.getLocation());

        block.setType(Material.OAK_SIGN);
        Sign sign = (Sign) block.getState();
        sign.line(0, Component.text("[AntiClock]", NamedTextColor.DARK_RED, TextDecoration.BOLD));
        sign.line(1, Component.text("早すぎるクロック"));
        sign.line(2, Component.text("回路の利用は"));
        sign.line(3, Component.text("避けてください。"));
        sign.update();
        for (Player p : Bukkit.getOnlinePlayers()) {
            String _group = PermissionsManager.getPermissionMainGroup(p);
            if (_group.equalsIgnoreCase("Moderator") || _group.equalsIgnoreCase("Admin")) {
                p.sendMessage("[AntiClock] " + ChatColor.RED + "爆新地内の" + loc.getBlockX() + " " + loc.getBlockY() + " "
                    + loc.getBlockZ() + "にあったクロック回路を停止しました。");
            }
            System.out.println("[AntiClock] 爆新地内の" + loc.getBlockX() + " " + loc.getBlockY() + " "
                + loc.getBlockZ() + "にあったクロック回路を停止しました。");
        }
    }
}
