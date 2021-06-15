package com.github.sirblobman.respawn;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.respawn.command.CommandRespawnX;
import com.github.sirblobman.respawn.listener.ListenerRespawnX;

public final class RespawnPlugin extends JavaPlugin {
    private final MultiVersionHandler multiVersionHandler;

    public RespawnPlugin() {
        this.multiVersionHandler = new MultiVersionHandler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ListenerRespawnX(this), this);

        CommandRespawnX command = new CommandRespawnX(this);
        command.register();

        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        updateManager.addResource(this, 47058L);
    }

    public MultiVersionHandler getMultiVersionHandler() {
        return this.multiVersionHandler;
    }
}
