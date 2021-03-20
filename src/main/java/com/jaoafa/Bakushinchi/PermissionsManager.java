package com.jaoafa.Bakushinchi;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionsManager implements Listener {
    /**
     * PermissionsExのステータス
     */
    private static boolean PermissionsExStatus;
    /**
     * LuckPermsのステータス
     */
    private static boolean LuckPermsStatus;

    private static PermissionsPlugin SelectPermissionsPlugin = null;

    public static void first() {
        try {
            checkPermissionsPluginAutoSelecter();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("権限管理プラグインが見つからないため、プラグインを停止します。");
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getJavaPlugin());
        }
    }

    /**
     * 権限管理プラグインの動作状態を確認し、使用するプラグインを自動選択する
     *
     * @throws UnsupportedOperationException 権限管理プラグインが見つからない場合に発生
     */
    public static void checkPermissionsPluginAutoSelecter() throws UnsupportedOperationException {
        Bukkit.getLogger().info("権限管理プラグインの自動選択を行います。");

        Plugin PermissionsEx = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
        Plugin LuckPerms = Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");

        if (PermissionsEx != null && PermissionsEx.isEnabled()) {
            PermissionsExStatus = true;
            Bukkit.getLogger().info("PermissionsExが動作しています。");
        }
        if (LuckPerms != null && LuckPerms.isEnabled()) {
            LuckPermsStatus = true;
            Bukkit.getLogger().info("LuckPermsが動作しています。");
        }

        if (PermissionsExStatus) {
            // PermissionsExが動作している
            SelectPermissionsPlugin = PermissionsPlugin.PermissionsEx; // PermissionsExを使う。
            Bukkit.getLogger().info("PermissionsExを自動選択しました！");
        } else if (LuckPermsStatus) {
            // LuckPermsが動作している
            SelectPermissionsPlugin = PermissionsPlugin.LuckPerms; // PermissionsExを使う。
            Bukkit.getLogger().info("LuckPermsを自動選択しました！");
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが見つからないため、自動選択ができません！");
        }
    }

    /**
     * 権限管理プラグインの動作状態を確認する
     */
    public static boolean checkPermissionsPlugin() throws UnsupportedOperationException {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        Plugin PermissionsEx = Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
        Plugin LuckPerms = Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");
        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            return (PermissionsEx != null && PermissionsEx.isEnabled());
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            return (LuckPerms != null && LuckPerms.isEnabled());
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
        }
    }

    /**
     * 指定されたプレイヤーの権限グループリストを取得します。
     *
     * @param player プレイヤー名
     * @return プレイヤーが居る権限グループリスト
     * @throws UnsupportedOperationException 権限管理プラグインが見つからないときに発生
     * @throws IllegalArgumentException      プレイヤーが見つからないときに発生
     */
    public static List<String> getPermissionGroupList(OfflinePlayer player)
            throws UnsupportedOperationException, IllegalArgumentException {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
			/*List<String> list = new ArrayList<>();
			Collection<String> groups = PermissionsEx.getPermissionManager().getGroupNames();
			for(String group : groups){
				if(PermissionsEx.getUser(player).inGroup(group)){
					list.add(group);
				}
			}
			return list;*/
            throw new IllegalStateException("PermissionsEx非対応");
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            LuckPerms LPApi = LuckPermsProvider.get();
            User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
            if (LPplayer == null) {
                throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
            }
            List<String> list = new ArrayList<>();
            String groupname = LPplayer.getPrimaryGroup();
            Group group = LPApi.getGroupManager().getGroup(groupname);
            if (group == null)
                throw new InternalError("Groupがnullです。");
            list.add(group.getFriendlyName());
            return list;
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが選択されていません！");
        }
    }

    /**
     * 指定されたプレイヤーの権限グループリストを取得します。
     *
     * @param player プレイヤー
     * @return プレイヤーが居る権限グループリスト
     * @throws UnsupportedOperationException 権限管理プラグインが見つからないときに発生
     * @throws IllegalArgumentException      プレイヤーが見つからないときに発生
     */
    public static List<String> getPermissionGroupList(Player player)
            throws UnsupportedOperationException, IllegalArgumentException {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();
        if (player == null)
            throw new InternalError("オンラインプレイヤーがnullです。");

        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
			/*
			List<String> list = new ArrayList<>();
			Collection<String> groups = PermissionsEx.getPermissionManager().getGroupNames();
			for (String group : groups) {
				if (PermissionsEx.getUser(player).inGroup(group)) {
					list.add(group);
				}
			}
			return list;*/
            throw new IllegalStateException("PermissionsEx非対応");
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            LuckPerms LPApi = LuckPermsProvider.get();
            UUID uuid = player.getUniqueId();
            if (uuid == null)
                throw new InternalError("オンラインプレイヤーのUUIDがnullです。");
            User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
            if (LPplayer == null) {
                throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
            }
            List<String> list = new ArrayList<>();
            String groupname = LPplayer.getPrimaryGroup();
            Group group = LPApi.getGroupManager().getGroup(groupname);
            if (group == null)
                throw new InternalError("Groupがnullです。");
            list.add(group.getFriendlyName());
            return list;
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが選択されていません！");
        }
    }

    public static String getPermissionMainGroup(String player)
            throws UnsupportedOperationException, IllegalArgumentException {
        @SuppressWarnings("deprecation")
        OfflinePlayer offplayer = Bukkit.getOfflinePlayer(player);
        if (offplayer == null)
            throw new IllegalArgumentException("Player not found.");
        return getPermissionMainGroup(offplayer);
    }

    public static String getPermissionMainGroup(UUID uuid)
            throws UnsupportedOperationException, IllegalArgumentException {
        OfflinePlayer offplayer = Bukkit.getOfflinePlayer(uuid);
        if (offplayer == null)
            throw new IllegalArgumentException("Player not found.");
        return getPermissionMainGroup(offplayer);
    }

    /**
     * 指定されたプレイヤーのメイン権限グループを取得します。
     *
     * @param player プレイヤー名
     * @return メイン権限グループ名
     * @throws UnsupportedOperationException 権限管理プラグインが見つからないときに発生
     * @throws IllegalArgumentException      プレイヤーが見つからないときに発生
     */
    public static String getPermissionMainGroup(OfflinePlayer player)
            throws UnsupportedOperationException, IllegalArgumentException {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
			/*
			Collection<String> groups = PermissionsEx.getPermissionManager().getGroupNames();
			String MaxGroup = null; // ?
			for (String group : groups) {
				if (PermissionsEx.getUser(player).inGroup(group)) {
					MaxGroup = group;
				}
			}
			return MaxGroup;
			*/
            throw new IllegalStateException("PermissionsEx非対応");
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            LuckPerms LPApi = LuckPermsProvider.get();
            UUID uuid = player.getUniqueId();
            if (uuid == null)
                throw new InternalError("オンラインプレイヤーのUUIDがnullです。");
            User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
            if (LPplayer == null) {
                throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
            }
            String groupname = LPplayer.getPrimaryGroup();
            Group group = LPApi.getGroupManager().getGroup(groupname);
            if (group == null)
                throw new InternalError("Groupがnullです。");
            return group.getFriendlyName();
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが選択されていません！");
        }
    }

    /**
     * 指定されたプレイヤーのメイン権限グループを取得します。
     *
     * @param player プレイヤー
     * @return メイン権限グループ名
     * @throws UnsupportedOperationException UnsupportedOperationException
     * @throws IllegalArgumentException      IllegalArgumentException
     * @throws InternalError                 InternalError
     */
    public static String getPermissionMainGroup(Player player)
            throws UnsupportedOperationException, IllegalArgumentException, InternalError {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        if (player == null)
            throw new InternalError("オンラインプレイヤーがnullです。");

        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            throw new IllegalStateException("PermissionsEx非対応");
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            LuckPerms LPApi = LuckPermsProvider.get();
            UUID uuid = player.getUniqueId();
            if (uuid == null)
                throw new InternalError("オンラインプレイヤーのUUIDがnullです。");
            User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
            if (LPplayer == null) {
                throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
            }
            String groupname = LPplayer.getPrimaryGroup();
            Group group = LPApi.getGroupManager().getGroup(groupname);
            if (group == null)
                throw new InternalError("Groupがnullです。");
            return group.getFriendlyName();
        } else {
            throw new UnsupportedOperationException("権限管理プラグインが選択されていません！");
        }
    }

    public static void setPermissionsGroup(Player player, String groupname) {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        if (player == null)
            throw new InternalError("オンラインプレイヤーがnullです。");
        if (groupname == null)
            throw new InternalError("グループ名がnullです。");

        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            throw new IllegalStateException("PermissionsEx非対応");
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            LuckPerms LPApi = LuckPermsProvider.get();
            UUID uuid = player.getUniqueId();
            if (uuid == null)
                throw new InternalError("オンラインプレイヤーのUUIDがnullです。");
            User LPplayer = LPApi.getUserManager().getUser(player.getUniqueId());
            if (LPplayer == null) {
                throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
            }
            Group group = LPApi.getGroupManager().getGroup(groupname);
            if (group == null) {
                throw new InternalError("Groupがnullです。");
            }
            LPplayer.setPrimaryGroup(groupname);
        }
    }

    public static void setPermissionsGroup(String uuidstr, String group) {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        if (uuidstr == null)
            throw new InternalError("uuidstrがnullです。");
        if (group == null)
            throw new InternalError("グループ名がnullです。");

        if (isSelectPermissionsPlugin(PermissionsPlugin.PermissionsEx)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            throw new IllegalStateException("PermissionsEx非対応");
        } else if (isSelectPermissionsPlugin(PermissionsPlugin.LuckPerms)) {
            if (!checkPermissionsPlugin()) {
                // プラグインが動作していない
                throw new UnsupportedOperationException("権限管理プラグインが見つかりません！");
            }
            LuckPerms LPApi = LuckPermsProvider.get();
            User LPplayer = null;
            try {
                UUID uuid = UUID.fromString(uuidstr);
                LPplayer = LPApi.getUserManager().getUser(uuid);
            } catch (IllegalArgumentException ignored) {
            }
            if (LPplayer == null) {
                LPplayer = LPApi.getUserManager().getUser(uuidstr);
                if (LPplayer == null) {
                    throw new IllegalArgumentException("指定されたプレイヤーは見つかりません。");
                }
            }
            LPplayer.setPrimaryGroup(group);
        }
    }

    public static PermissionsPlugin getSelectPermissionsPlugin() {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        return SelectPermissionsPlugin;
    }

    public static boolean isSelectPermissionsPlugin(PermissionsPlugin plugin) {
        // 権限管理プラグインが自動選択されてなかったら、自動選択する。
        if (SelectPermissionsPlugin == null)
            checkPermissionsPluginAutoSelecter();

        return plugin == SelectPermissionsPlugin;
    }

    @EventHandler
    public void PluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        if (plugin == null)
            return;
        if (!plugin.getName().equalsIgnoreCase("PermissionsEx") || !plugin.getName().equalsIgnoreCase("LuckPerms")) {
            return;
        }
        try {
            checkPermissionsPluginAutoSelecter();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("権限管理プラグインが見つからないため、プラグインを停止します。");
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getJavaPlugin());
        }
    }

    @EventHandler
    public void PluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if (plugin == null)
            return;
        if (!plugin.getName().equalsIgnoreCase("PermissionsEx") || !plugin.getName().equalsIgnoreCase("LuckPerms")) {
            return;
        }
        try {
            checkPermissionsPluginAutoSelecter();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("権限管理プラグインが見つからないため、プラグインを停止します。");
            Bukkit.getServer().getPluginManager().disablePlugin(Main.getJavaPlugin());
        }
    }

    public enum PermissionsPlugin {
        PermissionsEx(), LuckPerms();

        PermissionsPlugin() {
        }
    }
}
