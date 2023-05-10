package com.github.sirblobman.respawn.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.respawn.RespawnPlugin;

public final class RespawnTask extends EntityTaskDetails<Player> {
    private final RespawnPlugin plugin;

    public RespawnTask(@NotNull RespawnPlugin plugin, @NotNull Player entity) {
        super(plugin, entity);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Player player = getEntity();
        if (player == null) {
            return;
        }

        respawn(player);
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
