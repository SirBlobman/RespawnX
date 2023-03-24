package com.github.sirblobman.respawn.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.plugin.listener.PluginListener;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.xseries.XBlock;
import com.github.sirblobman.respawn.utility.ModernUtility;
import com.github.sirblobman.respawn.RespawnPlugin;

public final class ListenerRespawnX extends PluginListener<RespawnPlugin> {
    private final Map<UUID, Location> lastDeathLocationMap;

    public ListenerRespawnX(RespawnPlugin plugin) {
        super(plugin);
        this.lastDeathLocationMap = new HashMap<>();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if(shouldNotRespawn(player)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Location lastDeathLocation = player.getLocation();
        this.lastDeathLocationMap.put(uuid, lastDeathLocation);

        player.setCanPickupItems(false);
        autoRespawn(player);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(shouldNotRespawn(player)) {
            return;
        }

        fixRespawnLocation(player, e);
        player.setCanPickupItems(true);

        RespawnPlugin plugin = getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, () -> runRespawnCommands(player), 5L);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        fixHealth(player);
    }

    private YamlConfiguration getConfiguration() {
        RespawnPlugin plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private boolean hasPermission(Player player) {
        YamlConfiguration configuration = getConfiguration();
        if(!configuration.getBoolean("require-permission")) {
            return true;
        }

        String permissionName = configuration.getString("permission");
        if(permissionName == null || permissionName.isEmpty()) {
            return true;
        }

        Permission respawnPermission = new Permission(permissionName, "RespawnX Auto-Respawn Permission",
                PermissionDefault.FALSE);
        return player.hasPermission(respawnPermission);
    }

    private boolean isWorldEnabled(Player player) {
        YamlConfiguration configuration = getConfiguration();
        List<String> disabledWorldNameList = configuration.getStringList("disabled-world-list");
        boolean inverted = configuration.getBoolean("disabled-world-list-inverted", false);

        World world = player.getWorld();
        String worldName = world.getName();
        boolean contains = disabledWorldNameList.contains(worldName);

        return (inverted == contains);
    }

    private boolean shouldNotRespawn(Player player) {
        return (!hasPermission(player) || !isWorldEnabled(player));
    }

    private void autoRespawn(Player player) {
        YamlConfiguration configuration = getConfiguration();
        long delay = configuration.getLong("delay");

        RespawnPlugin plugin = getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, () -> respawn(player), delay);
    }

    private void respawn(Player player) {
        if(!player.isDead()) {
            return;
        }

        RespawnPlugin plugin = getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();
        playerHandler.forceRespawn(player);
    }

    private void fixHealth(Player player) {
        double health = player.getHealth();
        if(Double.isNaN(health) || Double.isInfinite(health) || health < 0.0D) {
            player.setHealth(0.0D);
        }
    }

    private void fixRespawnLocation(Player player, PlayerRespawnEvent e) {
        YamlConfiguration configuration = getConfiguration();
        if(!configuration.getBoolean("respawn-near-death.enabled")) {
            return;
        }

        UUID uuid = player.getUniqueId();
        if(!this.lastDeathLocationMap.containsKey(uuid)) {
            return;
        }

        Location lastDeathLocation = this.lastDeathLocationMap.get(uuid);
        double radius = configuration.getDouble("respawn-near-death.radius");
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double randomValue = rng.nextDouble(-radius, radius);

        double newX = (lastDeathLocation.getX() + randomValue);
        double newY = lastDeathLocation.getY();
        double newZ = (lastDeathLocation.getZ() + randomValue);
        World world = lastDeathLocation.getWorld();

        Location newLocation = new Location(world, newX, newY, newZ);
        if(shouldPreventUnsafeRespawn() && isUnsafe(newLocation)) {
            newLocation = world.getSpawnLocation();
        }

        e.setRespawnLocation(newLocation);
        player.teleport(newLocation);
    }

    private void runRespawnCommands(Player player) {
        YamlConfiguration configuration = getConfiguration();
        List<String> commandList = configuration.getStringList("respawn-commands");
        if(commandList.isEmpty()) {
            return;
        }

        String playerName = player.getName();
        for(String command : commandList) {
            String realCommand = command.replace("{player}", playerName);
            runAsConsole(realCommand);
        }
    }

    private void runAsConsole(String command) {
        try {
            CommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, command);
        } catch(Exception ex) {
            Logger logger = getPlugin().getLogger();
            logger.log(Level.WARNING, "An error occurred while running the '/" + command
                    + "' command in console:", ex);
        }
    }

    private boolean shouldPreventUnsafeRespawn() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("respawn-near-death.prevent-unsafe-respawn", true);
    }

    private boolean isUnsafe(Location location) {
        if(location == null) {
            return true;
        }

        World world = location.getWorld();
        if(world == null) {
            return true;
        }

        int locationY = location.getBlockY();
        if (locationY < getMinHeight(world)) {
            return true;
        }

        int x = location.getBlockX();
        int z = location.getBlockZ();
        for(int y = locationY; y >= 0; y--) {
            Block block = world.getBlockAt(x, y, z);
            Material bukkitMaterial = block.getType();
            if(XBlock.isLava(bukkitMaterial)) {
                return true;
            }
        }

        return false;
    }

    private int getMinHeight(World world) {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 18) {
            return 0;
        }

        return ModernUtility.getMinWorldHeight(world);
    }
}
