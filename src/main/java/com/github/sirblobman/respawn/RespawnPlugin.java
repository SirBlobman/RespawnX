package com.github.sirblobman.respawn;

import org.jetbrains.annotations.NotNull;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.SpigotUpdateManager;
import com.github.sirblobman.respawn.command.CommandRespawnX;
import com.github.sirblobman.respawn.configuration.RespawnConfiguration;
import com.github.sirblobman.respawn.listener.ListenerRespawnX;
import com.github.sirblobman.api.shaded.bstats.bukkit.Metrics;

public final class RespawnPlugin extends ConfigurablePlugin {
    private final RespawnConfiguration configuration;

    public RespawnPlugin() {
        this.configuration = new RespawnConfiguration();
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        reloadConfiguration();
        registerCommands();
        registerListeners();
        registerUpdateChecker();
        register_bStats();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    protected void reloadConfiguration() {
        ConfigurationManager configurationManager = this.getConfigurationManager();
        configurationManager.reload("config.yml");
        getConfiguration().load(configurationManager.get("config.yml"));
    }

    public @NotNull RespawnConfiguration getConfiguration() {
        return this.configuration;
    }

    private void registerCommands() {
        new CommandRespawnX(this).register();
    }

    private void registerListeners() {
        new ListenerRespawnX(this).register();
    }

    private void registerUpdateChecker() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        SpigotUpdateManager updateManager = corePlugin.getSpigotUpdateManager();
        updateManager.addResource(this, 47058L);
    }

    private void register_bStats() {
        new Metrics(this, 16217);
    }
}
