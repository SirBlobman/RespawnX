package com.github.sirblobman.respawn;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.bstats.bukkit.Metrics;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.respawn.command.CommandRespawnX;
import com.github.sirblobman.respawn.listener.ListenerRespawnX;

public final class RespawnPlugin extends ConfigurablePlugin {
    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        reloadConfig();

        registerCommands();
        registerListeners();
        registerUpdateChecker();
        register_bStats();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    private void registerCommands() {
        new CommandRespawnX(this).register();
    }

    private void registerListeners() {
        new ListenerRespawnX(this).register();
    }

    private void registerUpdateChecker() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        updateManager.addResource(this, 47058L);
    }

    private void register_bStats() {
        new Metrics(this, 16217);
    }
}
