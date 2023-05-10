package com.github.sirblobman.respawn.task;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.respawn.RespawnPlugin;
import com.github.sirblobman.respawn.configuration.RespawnConfiguration;

public final class CommandsTask extends EntityTaskDetails<Player> {
    private final RespawnPlugin plugin;

    public CommandsTask(@NotNull RespawnPlugin plugin, @NotNull Player entity) {
        super(plugin, entity);
        this.plugin = plugin;
        setDelay(5L);
    }

    @Override
    public void run() {
        Player player = getEntity();
        if (player != null) {
            runCommands(player);
        }
    }

    private @NotNull RespawnPlugin getRespawnPlugin() {
        return this.plugin;
    }

    private @NotNull Logger getLogger() {
        RespawnPlugin plugin = getRespawnPlugin();
        return plugin.getLogger();
    }

    private @NotNull RespawnConfiguration getConfiguration() {
        RespawnPlugin plugin = getRespawnPlugin();
        return plugin.getConfiguration();
    }

    private @NotNull List<String> getCommands() {
        RespawnConfiguration configuration = getConfiguration();
        return configuration.getRespawnCommands();
    }

    private void runCommands(@NotNull Player player) {
        List<String> commandList = getCommands();
        if (commandList.isEmpty()) {
            return;
        }

        String playerName = player.getName();
        for (String command : commandList) {
            String realCommand = command.replace("{player}", playerName);
            runAsConsole(realCommand);
        }
    }

    private void runAsConsole(@NotNull String command) {
        try {
            CommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, command);
        } catch (Exception ex) {
            Logger logger = getLogger();
            String messageFormat = "Failed to execute the command '/%s' in the server console:";
            logger.log(Level.WARNING, String.format(Locale.US, messageFormat, command), ex);
        }
    }
}
