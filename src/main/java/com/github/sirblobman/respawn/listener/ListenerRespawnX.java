package com.github.sirblobman.respawn.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.api.folia.FoliaHelper;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.plugin.listener.PluginListener;
import com.github.sirblobman.respawn.RespawnPlugin;
import com.github.sirblobman.respawn.configuration.RespawnConfiguration;
import com.github.sirblobman.respawn.configuration.RespawnNearDeathConfiguration;
import com.github.sirblobman.respawn.task.CommandsTask;
import com.github.sirblobman.respawn.task.RespawnTask;
import com.github.sirblobman.api.shaded.xseries.XBlock;

public final class ListenerRespawnX extends PluginListener<RespawnPlugin> {
    private final Map<UUID, Location> lastDeathLocationMap;

    public ListenerRespawnX(@NotNull RespawnPlugin plugin) {
        super(plugin);
        this.lastDeathLocationMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        printDebug("Detected PlayerDeathEvent...");

        Player player = e.getEntity();
        if (shouldNotRespawn(player)) {
            printDebug("Player world is disabled or is missing permission.");
            return;
        }

        UUID playerId = player.getUniqueId();
        Location lastDeathLocation = player.getLocation();
        this.lastDeathLocationMap.put(playerId, lastDeathLocation);

        printDebug("Disabled player item pickup.");
        player.setCanPickupItems(false);
        autoRespawn(player);
        printDebug("Triggered automatic respawn for player.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent e) {
        printDebug("Detected PlayerRespawnEvent.");
        Player player = e.getPlayer();
        if (shouldNotRespawn(player)) {
            return;
        }

        fixRespawnLocation(player, e);
        printDebug("Fixed respawn location.");

        player.setCanPickupItems(true);
        printDebug("Re-enabled player item pickup.");

        RespawnPlugin plugin = getPlugin();
        FoliaHelper foliaHelper = plugin.getFoliaHelper();
        TaskScheduler scheduler = foliaHelper.getScheduler();

        CommandsTask task = new CommandsTask(plugin, player);
        scheduler.scheduleEntityTask(task);
        printDebug("Scheduled respawn commands task for player.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        printDebug("Detected PlayerJoinEvent...");
        Player player = e.getPlayer();
        fixHealth(player);
        printDebug("Fixed player health.");
    }

    private @NotNull RespawnConfiguration getConfiguration() {
        RespawnPlugin plugin = getPlugin();
        return plugin.getConfiguration();
    }

    private @NotNull RespawnNearDeathConfiguration getRespawnNearDeath() {
        RespawnConfiguration configuration = getConfiguration();
        return configuration.getRespawnNearDeath();
    }

    private void printDebug(@NotNull String message) {
        RespawnConfiguration configuration = getConfiguration();
        if (!configuration.isDebugMode()) {
            return;
        }

        Logger logger = getLogger();
        logger.info("[Debug] " + message);
    }

    private boolean hasPermission(@NotNull Player player) {
        RespawnConfiguration configuration = getConfiguration();
        Permission permission = configuration.getPermission();
        if (permission == null) {
            return true;
        }

        return player.hasPermission(permission);
    }

    private boolean isWorldDisabled(@NotNull Player player) {
        World world = player.getWorld();
        RespawnConfiguration configuration = getConfiguration();
        return configuration.isDisabled(world);
    }

    private boolean shouldNotRespawn(@NotNull Player player) {
        printDebug("Detected check for 'should not respawn player'.");
        printDebug("Player: " + player.getName());

        if (isWorldDisabled(player)) {
            printDebug("World is disabled for player.");
            return true;
        }

        if (!hasPermission(player)) {
            printDebug("Player is missing automatic respawn permission.");
            return true;
        }

        printDebug("Player should respawn.");
        return false;
    }

    private void autoRespawn(@NotNull Player player) {
        RespawnPlugin plugin = getPlugin();
        FoliaHelper foliaHelper = plugin.getFoliaHelper();
        TaskScheduler scheduler = foliaHelper.getScheduler();

        RespawnConfiguration configuration = getConfiguration();
        long delay = configuration.getDelay();

        RespawnTask task = new RespawnTask(plugin, player);
        task.setDelay(delay);
        scheduler.scheduleEntityTask(task);
    }

    private void fixHealth(@NotNull Player player) {
        double health = player.getHealth();
        if (Double.isNaN(health) || Double.isInfinite(health) || health < 0.0D) {
            player.setHealth(0.0D);
        }
    }

    private void fixRespawnLocation(@NotNull Player player, @NotNull PlayerRespawnEvent e) {
        RespawnNearDeathConfiguration respawnNearDeath = getRespawnNearDeath();
        if (!respawnNearDeath.isEnabled()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        if (!this.lastDeathLocationMap.containsKey(playerId)) {
            return;
        }

        Location lastDeathLocation = this.lastDeathLocationMap.get(playerId);
        double radiusX = respawnNearDeath.getRadiusX();
        double radiusY = respawnNearDeath.getRadiusY();
        double radiusZ = respawnNearDeath.getRadiusZ();

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double randomValueX = rng.nextDouble(-radiusX, radiusX);
        double randomValueY = rng.nextDouble(-radiusY, radiusY);
        double randomValueZ = rng.nextDouble(-radiusZ, radiusZ);

        double newX = (lastDeathLocation.getX() + randomValueX);
        double newY = (lastDeathLocation.getY() + randomValueY);
        double newZ = (lastDeathLocation.getZ() + randomValueZ);
        World world = lastDeathLocation.getWorld();

        Location newLocation = new Location(world, newX, newY, newZ);
        if (respawnNearDeath.isPreventUnsafeRespawn() && isUnsafe(newLocation)) {
            newLocation = world.getSpawnLocation();
        }

        e.setRespawnLocation(newLocation);
        player.teleport(newLocation);
    }

    private boolean isUnsafe(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) {
            return true;
        }

        int locationY = location.getBlockY();
        if (locationY <= getMinHeight(world)) {
            return true;
        }

        int x = location.getBlockX();
        int z = location.getBlockZ();
        for (int y = locationY; y >= 0; y--) {
            Block block = world.getBlockAt(x, y, z);
            Material bukkitMaterial = block.getType();
            if (XBlock.isLava(bukkitMaterial)) {
                return true;
            }
        }

        return false;
    }

    private int getMinHeight(@NotNull World world) {
        return world.getMinHeight();
    }
}
