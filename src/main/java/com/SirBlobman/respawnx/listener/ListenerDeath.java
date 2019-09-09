package com.SirBlobman.respawnx.listener;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.utility.Util;
import com.SirBlobman.respawnx.RespawnX;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListenerDeath implements Listener {
    private static final Map<UUID, Location> lastDeathLocation = Util.newMap();
    private final RespawnX plugin;
    public ListenerDeath(RespawnX plugin) {
        this.plugin = plugin;
    }

    private boolean checkNoRespawn(Player player) {
        FileConfiguration config = this.plugin.getConfig();
        boolean usePermissions = config.getBoolean("require permission");
        if(usePermissions) {
            String permission = config.getString("permission");
            if(!player.hasPermission(permission)) return true;
        }

        World world = player.getWorld();
        String worldName = world.getName();
        List<String> disabledWorldList = config.getStringList("disabled worlds");
        return disabledWorldList.contains(worldName);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if(checkNoRespawn(player)) return;

        Location location = player.getLocation();
        UUID uuid = player.getUniqueId();
        lastDeathLocation.put(uuid, location);

        player.setCanPickupItems(false);
        respawn(player);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(checkNoRespawn(player)) return;

        fixRespawnLocation(player, e);
        player.setCanPickupItems(true);
        runRespawnCommands(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        fixHealth(player);
    }

    private void respawn(Player player) {
        FileConfiguration config = this.plugin.getConfig();
        long delay = config.getLong("delay");

        Runnable task = () -> {
            if(!player.isDead()) return;

            NMS_Handler nmsHandler = NMS_Handler.getHandler();
            nmsHandler.forceRespawn(player);
        };
        Bukkit.getScheduler().runTaskLater(this.plugin, task, delay);
    }

    private void fixHealth(Player player) {
        double health = player.getHealth();
        if(Double.isNaN(health)) player.setHealth(0.0D);
        if(Double.isInfinite(health)) player.setHealth(0.0D);
    }

    private void fixRespawnLocation(Player player, PlayerRespawnEvent e) {
        FileConfiguration config = this.plugin.getConfig();
        boolean respawnNearDeath = config.getBoolean("respawn near death.enabled");
        if(!respawnNearDeath) return;

        UUID uuid = player.getUniqueId();
        Location respawnLocation = lastDeathLocation.getOrDefault(uuid, e.getRespawnLocation());
        lastDeathLocation.remove(uuid);

        int radius = config.getInt("respawn near death.radius");
        if(radius < 0) radius = 0;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int randomVar = random.nextInt(-radius, radius);
        int newX = respawnLocation.getBlockX() + randomVar;
        int newY = respawnLocation.getBlockY() + randomVar;
        int newZ = respawnLocation.getBlockZ() + randomVar;
        Location newLocation = new Location(respawnLocation.getWorld(), newX, newY, newZ);
        e.setRespawnLocation(newLocation);
        player.teleport(newLocation);
    }

    private void runRespawnCommands(Player player) {
        FileConfiguration config = this.plugin.getConfig();
        List<String> commandList = config.getStringList("respawn commands");

        Runnable task = () -> {
            String username = player.getName();
            CommandSender console = Bukkit.getConsoleSender();
            for(String command : commandList) {
                command = command.replace("{player}", username);
                try {
                    Bukkit.dispatchCommand(console, command);
                } catch(Exception ex) {
                    Logger logger = this.plugin.getLogger();
                    logger.log(Level.WARNING, "An error occurred while executing a respawn command.", ex);
                }
            }
        };
        Bukkit.getScheduler().runTaskLater(this.plugin, task, 5L);
    }
}