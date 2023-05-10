package com.github.sirblobman.respawn.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.respawn.RespawnPlugin;
import com.github.sirblobman.respawn.configuration.RespawnConfiguration;
import com.github.sirblobman.respawn.configuration.RespawnNearDeathConfiguration;
import com.github.sirblobman.respawn.task.CommandsTask;
import com.github.sirblobman.respawn.task.RespawnTask;
import com.github.sirblobman.respawn.utility.ModernUtility;
import com.github.sirblobman.api.shaded.xseries.XBlock;

public final class ListenerRespawnX extends PluginListener<RespawnPlugin> {
    private final Map<UUID, Location> lastDeathLocationMap;

    public ListenerRespawnX(@NotNull RespawnPlugin plugin) {
        super(plugin);
        this.lastDeathLocationMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (shouldNotRespawn(player)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        Location lastDeathLocation = player.getLocation();
        this.lastDeathLocationMap.put(uuid, lastDeathLocation);

        player.setCanPickupItems(false);
        autoRespawn(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (shouldNotRespawn(player)) {
            return;
        }

        fixRespawnLocation(player, e);
        player.setCanPickupItems(true);

        RespawnPlugin plugin = getPlugin();
        FoliaHelper foliaHelper = plugin.getFoliaHelper();
        TaskScheduler scheduler = foliaHelper.getScheduler();

        CommandsTask task = new CommandsTask(plugin, player);
        scheduler.scheduleEntityTask(task);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        fixHealth(player);
    }

    private @NotNull RespawnConfiguration getConfiguration() {
        RespawnPlugin plugin = getPlugin();
        return plugin.getConfiguration();
    }

    private @NotNull RespawnNearDeathConfiguration getRespawnNearDeath() {
        RespawnConfiguration configuration = getConfiguration();
        return configuration.getRespawnNearDeath();
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
        return !configuration.isDisabled(world);
    }

    private boolean shouldNotRespawn(Player player) {
        return (isWorldDisabled(player) || !hasPermission(player));
    }

    private void autoRespawn(@NotNull Player player) {
        RespawnPlugin plugin = getPlugin();
        FoliaHelper foliaHelper = plugin.getFoliaHelper();
        TaskScheduler scheduler = foliaHelper.getScheduler();

        RespawnConfiguration configuration = getConfiguration();
        long delay = configuration.getDelay();

        RespawnTask task = new RespawnTask(plugin, player);
        task.setDelay(delay);
        scheduler.scheduleTask(task);
    }

    private void fixHealth(Player player) {
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
        double radius = respawnNearDeath.getRadius();

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double randomValue = rng.nextDouble(-radius, radius);

        double newX = (lastDeathLocation.getX() + randomValue);
        double newY = lastDeathLocation.getY();
        double newZ = (lastDeathLocation.getZ() + randomValue);
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
        if (locationY < getMinHeight(world)) {
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
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 18) {
            return 0;
        }

        return ModernUtility.getMinWorldHeight(world);
    }
}
