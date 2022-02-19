package com.jaoafa.ChuoCity;

import com.jaoafa.ChuoCity.Command.Cmd_ChuoCity;
import com.jaoafa.ChuoCity.Event.*;
import com.jaoafa.ChuoCity.Tasks.Task_NewStep;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {
    private static Main Main = null;

    public static JavaPlugin getJavaPlugin() {
        return Main;
    }

    public static Main getMain() {
        return Main;
    }

    public static void setMain(Main main) {
        Main = main;
    }

    public static boolean isChuoCity(Location loc) {
        if (!loc.getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
            return false; // Jao_Afa以外では適用しない
        }

        WorldGuardPlugin wg = getWorldGuard();
        if (wg == null) {
            return false;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(loc.getWorld().getName());
        RegionManager rm = container.get(world);
        if (rm == null) {
            return false;
        }
        ApplicableRegionSet regions = rm.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
        if (regions.size() == 0) {
            return false;
        }
        List<ProtectedRegion> inheritance = new LinkedList<>();
        for (ProtectedRegion region : regions) {
            inheritance.add(region);
        }
        Collections.reverse(inheritance);
        ProtectedRegion firstregion = inheritance.get(0);
        return firstregion.getId().startsWith("chuocity_");
    }

    public static ProtectedRegion getTopRegion(Location loc) {
        if (!loc.getWorld().getName().equalsIgnoreCase("Jao_Afa")) {
            return null; // Jao_Afa以外では適用しない
        }

        WorldGuardPlugin wg = getWorldGuard();
        if (wg == null) {
            return null;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(loc.getWorld().getName());
        RegionManager rm = container.get(world);
        if (rm == null) {
            return null;
        }
        ApplicableRegionSet regions = rm.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
        if (regions.size() == 0) {
            return null;
        }
        List<ProtectedRegion> inheritance = new LinkedList<>();
        for (ProtectedRegion region : regions) {
            inheritance.add(region);
        }
        return inheritance.get(0);
    }

    private static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin instanceof WorldGuardPlugin) {
            return (WorldGuardPlugin) plugin;
        }
        return null;

    }

    /**
     * プラグインが起動したときに呼び出し
     *
     * @author mine_book000
     * @since 2019/10/07
     */
    @Override
    public void onEnable() {
        setMain(this);

        Objects.requireNonNull(getCommand("chuocity")).setExecutor(new Cmd_ChuoCity());
        getServer().getPluginManager().registerEvents(new Event_ChuoCityRailChecker(), this);
        getServer().getPluginManager().registerEvents(new Event_AntiBlockUnderDestroy(), this);
        getServer().getPluginManager().registerEvents(new Event_ChuoCityY50Destroy(), this);
        getServer().getPluginManager().registerEvents(new Event_AntiClockRedstone(), this);
        getServer().getPluginManager().registerEvents(new Event_PlaceTNT(), this);
        getServer().getPluginManager().registerEvents(new Event_AntiCreatureSpawn(), this);
        getServer().getPluginManager().registerEvents(new Event_AntiDiffusionWaterLava(), this);
        getServer().getPluginManager().registerEvents(new Event_AntiInteract(), this);
        getServer().getPluginManager().registerEvents(new Event_AntiTooManyArmorStand(), this);
        getServer().getPluginManager().registerEvents(new Event_RegionCommand(), this);
        getServer().getPluginManager().registerEvents(new Event_Bamboo(), this);
        getServer().getPluginManager().registerEvents(new Event_Beehive(), this);

        WorldEdit.getInstance().getEventBus().register(new Event_WGNonProtection());

        new Task_NewStep().runTaskTimerAsynchronously(this, 0L, 1200L); // per 1 minute
    }
}
