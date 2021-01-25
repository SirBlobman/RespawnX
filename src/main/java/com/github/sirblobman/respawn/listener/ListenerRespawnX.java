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
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.respawn.RespawnPlugin;

public class ListenerRespawnX implements Listener {
    private final RespawnPlugin plugin;
    private final Map<UUID, Location> lastDeathLocationMap;
    public ListenerRespawnX(RespawnPlugin plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
        this.lastDeathLocationMap = new HashMap<>();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if(shouldNotRespawn(player)) return;

        UUID uuid = player.getUniqueId();
        Location lastDeathLocation = player.getLocation();
        this.lastDeathLocationMap.put(uuid, lastDeathLocation);

        player.setCanPickupItems(false);
        autoRespawn(player);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(shouldNotRespawn(player)) return;

        fixRespawnLocation(player, e);
        player.setCanPickupItems(true);

        Runnable task = () -> runRespawnCommands(player);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, task, 5L);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        fixHealth(player);
    }

    private boolean hasPermission(Player player) {
        FileConfiguration configuration = this.plugin.getConfig();
        if(!configuration.getBoolean("require-permission")) return true;

        String permissionName = configuration.getString("permission");
        if(permissionName == null || permissionName.isEmpty()) return true;

        Permission respawnPermission = new Permission(permissionName, "RespawnX Auto-Respawn Permission", PermissionDefault.FALSE);
        return player.hasPermission(respawnPermission);
    }

    private boolean checkWorld(Player player) {
        FileConfiguration configuration = this.plugin.getConfig();
        List<String> disabledWorldNameList = configuration.getStringList("disabled-world-list");

        World world = player.getWorld();
        String worldName = world.getName();
        return !disabledWorldNameList.contains(worldName);
    }

    private boolean shouldNotRespawn(Player player) {
        return (!hasPermission(player) || !checkWorld(player));
    }

    private void autoRespawn(Player player) {
        FileConfiguration configuration = this.plugin.getConfig();
        long delay = configuration.getLong("delay");

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(this.plugin, () -> respawn(player), delay);
    }

    private void respawn(Player player) {
        if(!player.isDead()) return;
        MultiVersionHandler multiVersionHandler = this.plugin.getMultiVersionHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();
        playerHandler.forceRespawn(player);
    }

    private void fixHealth(Player player) {
        double health = player.getHealth();
        if(Double.isNaN(health)) player.setHealth(0.0D);
        if(Double.isInfinite(health)) player.setHealth(0.0D);
        if(health < 0.0D) player.setHealth(0.0D);
    }

    private void fixRespawnLocation(Player player, PlayerRespawnEvent e) {
        FileConfiguration configuration = this.plugin.getConfig();
        if(!configuration.getBoolean("respawn-near-death.enabled")) return;

        UUID uuid = player.getUniqueId();
        if(!this.lastDeathLocationMap.containsKey(uuid)) return;
        Location lastDeathLocation = this.lastDeathLocationMap.get(uuid);

        double radius = configuration.getDouble("respawn-near-death.radius");
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double randomValue = rng.nextDouble(-radius, radius);

        double newX = (lastDeathLocation.getX() + randomValue);
        double newY = lastDeathLocation.getY();
        double newZ = (lastDeathLocation.getZ() + randomValue);
        World world = lastDeathLocation.getWorld();

        Location newLocation = new Location(world, newX, newY, newZ);
        e.setRespawnLocation(newLocation);
        player.teleport(newLocation);
    }

    private void runRespawnCommands(Player player) {
        FileConfiguration configuration = this.plugin.getConfig();
        List<String> commandList = configuration.getStringList("respawn-commands");

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
            Logger logger = this.plugin.getLogger();
            logger.log(Level.WARNING, "An error occurred while running the '/" + command + "' command in console:", ex);
        }
    }
}