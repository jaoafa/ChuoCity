package com.jaoafa.ChuoCity.Event;

import com.jaoafa.ChuoCity.Main;
import com.jaoafa.ChuoCity.PermissionsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Event_AntiClockRedstone implements Listener {
    private record RSData(long ms, int times) {
    }

    final Map<Location, RSData> redStoneClocks = new HashMap<>();

    List<Material> forbiddenBlocks = List.of(
            Material.REDSTONE_WIRE,
            Material.REPEATER,
            Material.COMPARATOR
    );

    @EventHandler
    public void OnRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (!Main.isChuoCity(loc)) return;

        if (!forbiddenBlocks.contains(block.getType())) return;

        int oldC = event.getOldCurrent();
        int newC = event.getNewCurrent();

        if ((oldC == newC) || (oldC != 0) && (newC != 0)) return;

        long milliSec = System.currentTimeMillis();

        boolean nearEntryExists = redStoneClocks.entrySet().stream().anyMatch(entry -> {
            Location dataLoc = entry.getKey();
            return (dataLoc.distance(loc) == 1);
        });

        // ADD NASA
        if (!redStoneClocks.containsKey(loc) && !nearEntryExists) {
            redStoneClocks.put(loc, new RSData(milliSec, 1));
            return;
        }
        
        RSData clockData = redStoneClocks.get(loc);

        if (clockData == null) return;

        long milliSecOld = clockData.ms;
        long subtraction = milliSec - milliSecOld;

        // 1秒間に1回未満のクロック回路で5回それが繰り返された場合。
        if (subtraction > 1000) {
            // 1s (20tick, 1000ms) 以上
            redStoneClocks.remove(loc);
            return;
        }
        if (redStoneClocks.containsKey(loc) && clockData.times < 5) {
            // あって、5回未満
            redStoneClocks.put(loc, new RSData(milliSec, clockData.times + 1));
            return;
        }
        // あって、5回より上(6回以上)
        redStoneClocks.remove(loc);

        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(Material.OAK_SIGN);
                Sign sign = (Sign) block.getState();
                sign.line(0, Component.text("[AntiClock]", NamedTextColor.DARK_RED, TextDecoration.BOLD));
                sign.line(1, Component.text("早すぎるクロック"));
                sign.line(2, Component.text("回路の利用は"));
                sign.line(3, Component.text("避けてください。"));
                sign.update();

                cancel();
            }
        }.runTaskLater(Main.getJavaPlugin(), 1);

        String locationText = "%s %s %s".formatted(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        TextComponent.Builder text = Component.text().append(
                Component.text("[AntiClock] "),
                Component.text("中央市内の ", NamedTextColor.RED),
                Component.text(
                        locationText,
                        Style.style()
                             .decorate(TextDecoration.UNDERLINED)
                             .clickEvent(ClickEvent.runCommand("/tp " + locationText))
                             .hoverEvent(HoverEvent.showText(Component.text(locationText + " にテレポート")))
                             .build()
                ),
                Component.text(" にあったクロック回路を停止しました。", NamedTextColor.RED)
        );

        //AMに送信
        Bukkit.getOnlinePlayers().stream().filter(player -> {
            String group = PermissionsManager.getPermissionMainGroup(player);
            return group.equalsIgnoreCase("Moderator") || group.equalsIgnoreCase("Admin");
        }).forEach(player -> player.sendMessage(text));
        //Loggerに送信
        Main.getMain().getLogger().info(text.content());
    }
}
