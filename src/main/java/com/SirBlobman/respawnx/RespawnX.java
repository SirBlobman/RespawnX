package com.SirBlobman.respawnx;

import com.SirBlobman.respawnx.command.CommandRespawnX;
import com.SirBlobman.respawnx.listener.ListenerDeath;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RespawnX extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ListenerDeath(this), this);
        getCommand("respawnx").setExecutor(new CommandRespawnX(this));
    }
}