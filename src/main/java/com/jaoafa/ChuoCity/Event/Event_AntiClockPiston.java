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
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Event_AntiClockPiston implements Listener {
    private record PistonData(long ms, int times) {
    }

    final Map<Location, PistonData> pistonClocks = new HashMap<>();

    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (!Main.isChuoCity(loc)) return;

        long milliSec = System.currentTimeMillis();

        // ADD NASA
        if (!pistonClocks.containsKey(loc)) {
            pistonClocks.put(loc, new PistonData(milliSec, 1));
            return;
        }

        // 1秒間に1回未満のクロック回路で3回それが繰り返された場合。
        long milliSecOld = pistonClocks.get(loc).ms;
        long subtraction = milliSec - milliSecOld;

        if (subtraction > 1000) {
            // 1s (20tick, 1000ms) 以上
            pistonClocks.remove(loc);
            return;
        }
        if (pistonClocks.containsKey(loc) && pistonClocks.get(loc).times <= 3) {
            // あって、3回未満
            pistonClocks.put(loc, new PistonData(milliSec, pistonClocks.get(loc).times + 1));
            return;
        } else if (!pistonClocks.containsKey(loc)) {
            // ない
            pistonClocks.put(loc, new PistonData(milliSec, 1));
            return;
        }
        // あって、5回より上(6回以上)

        pistonClocks.remove(loc);
        event.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(Material.OAK_SIGN);
                Sign sign = (Sign) block.getState();
                sign.line(0, Component.text("[AntiPiston]", NamedTextColor.DARK_RED, TextDecoration.BOLD));
                sign.line(1, Component.text("早すぎるクロック"));
                sign.line(2, Component.text("ピストンの利用は"));
                sign.line(3, Component.text("避けてください。"));
                sign.update();
                cancel();
            }
        }.runTaskLater(Main.getJavaPlugin(), 1);

        String locationText = "%s %s %s".formatted(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        TextComponent.Builder text = Component.text().append(
                Component.text("[AntiClockPiston] "),
                Component.text("中央市内の ", NamedTextColor.RED),
                Component.text(
                        locationText,
                        Style.style()
                             .decorate(TextDecoration.UNDERLINED)
                             .clickEvent(ClickEvent.runCommand("/tp " + locationText))
                             .hoverEvent(HoverEvent.showText(Component.text(locationText + " にテレポート")))
                             .build()
                ),
                Component.text("にあったクロックピストンを停止しました。", NamedTextColor.RED)
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
