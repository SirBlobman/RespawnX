package com.github.sirblobman.respawn.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.TaskDetails;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.respawn.RespawnPlugin;

public final class RespawnTask extends TaskDetails {
    private final RespawnPlugin plugin;
    private final Player player;

    public RespawnTask(@NotNull RespawnPlugin plugin, @NotNull Player player) {
        super(plugin);
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        Player player = getPlayer();
        respawn(player);
    }

    private @NotNull Player getPlayer() {
        return this.player;
    }

    private @NotNull RespawnPlugin getRespawnPlugin() {
        return this.plugin;
    }

    private void respawn(@NotNull Player player) {
        RespawnPlugin plugin = getRespawnPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();
        playerHandler.forceRespawn(player);
    }
}
