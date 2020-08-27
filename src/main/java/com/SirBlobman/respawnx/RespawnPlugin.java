package com.SirBlobman.respawnx;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.core.CorePlugin;
import com.SirBlobman.respawnx.command.CommandRespawnX;
import com.SirBlobman.respawnx.listener.ListenerRespawnX;

public class RespawnPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ListenerRespawnX(this), this);

        CommandRespawnX command = new CommandRespawnX(this);
        command.register();
    }

    public MultiVersionHandler getMultiVersionHandler() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        return corePlugin.getMultiVersionHandler();
    }
}