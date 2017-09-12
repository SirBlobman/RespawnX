package com.SirBlobman.respawnx;

import com.SirBlobman.respawnx.config.ConfigSettings;
import com.SirBlobman.respawnx.nms.HandleRespawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RespawnX extends JavaPlugin implements Listener {
    public static RespawnX INSTANCE;
    public static File FOLDER;
    public static HandleRespawn HANDLER;
    
    @Override
    public void onEnable() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        ConfigSettings.load();
        HANDLER = HandleRespawn.getRespawnHandler();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("respawnx").setExecutor(this);
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        String cmd = c.getName().toLowerCase();
        if(cmd.equals("respawnx")) {
            ConfigSettings.load();
            cs.sendMessage("\u00a7c[\u00a74RespawnX\u00a7c] \u00a7fReloaded Config. New Values:");
            cs.sendMessage("\u00a7f\u00a7ldelay in ticks: \u00a7a" + ConfigSettings.DELAY_IN_TICKS);
            cs.sendMessage("\u00a7f\u00a7lrequire permission: \u00a7a" + ConfigSettings.REQUIRE_PERMISSION);
            cs.sendMessage("\u00a7f\u00a7lpermission: \u00a7a" + ConfigSettings.PERMISSION);
            return true;
        } else return false;
    }
    
    @Override
    public void onDisable() {}
    
    private static Map<Player, Location> LAST_DEATH = new HashMap<>();
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if(hasPermission(p)) {
            BukkitRunnable br = new BukkitRunnable() {
                @Override
                public void run() {
                    p.setCanPickupItems(false);
                    HANDLER.handleDeath(p);
                    LAST_DEATH.put(p, p.getLocation());
                }
            };
            br.runTaskLater(this, ConfigSettings.DELAY_IN_TICKS);
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Location l = LAST_DEATH.containsKey(p) ? LAST_DEATH.get(p) : e.getRespawnLocation();
        HANDLER.handleRespawn(p, l);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(p.getHealth() == Double.NaN) p.setHealth(0.0D);
        if(p.getHealth() == 0) {
            if(hasPermission(p)) {
                BukkitRunnable br = new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.setCanPickupItems(false);
                        HANDLER.handleDeath(p);
                    }
                };
                br.runTaskLater(this, ConfigSettings.DELAY_IN_TICKS);
            }
        }
    }
    
    private boolean hasPermission(Player p) {
        if(ConfigSettings.REQUIRE_PERMISSION) {
            String permission = ConfigSettings.PERMISSION;
            return p.hasPermission(permission);
        } else return true;
    }
}