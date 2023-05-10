package com.github.sirblobman.respawn.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class RespawnConfiguration implements IConfigurable {
    private final List<String> disabledWorldList;
    private final List<String> respawnCommandList;
    private final RespawnNearDeathConfiguration respawnNearDeath;

    private boolean debugMode;
    private long delay;
    private boolean requirePermission;
    private String permissionName;
    private boolean disabledWorldListInverted;

    private transient Permission permission;

    public RespawnConfiguration() {
        this.debugMode = false;
        this.delay = 1L;
        this.requirePermission = false;
        this.permissionName = "respawnx.respawn.automatic";
        this.disabledWorldList = new ArrayList<>();
        this.disabledWorldListInverted = false;
        this.respawnCommandList = new ArrayList<>();
        this.respawnNearDeath = new RespawnNearDeathConfiguration();

        this.permission = null;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setDebugMode(config.getBoolean("debug-mode", false));
        setDelay(config.getLong("delay", 1L));
        setRequirePermission(config.getBoolean("require-permission", false));
        setPermissionName(config.getString("permission", "respawnx.respawn.automatic"));
        setDisabledWorlds(config.getStringList("disabled-world-list"));
        setDisabledWorldListInverted(config.getBoolean("disabled-world-list-inverted", false));
        setRespawnCommands(config.getStringList("respawn-commands"));

        RespawnNearDeathConfiguration nearDeath = getRespawnNearDeath();
        nearDeath.load(getOrCreateSection(config, "respawn-near-death"));
    }

    public long getDelay() {
        return this.delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isRequirePermission() {
        return this.requirePermission;
    }

    public void setRequirePermission(boolean requirePermission) {
        this.requirePermission = requirePermission;
        this.permission = null;
    }

    public @NotNull String getPermissionName() {
        return this.permissionName;
    }

    public void setPermissionName(@NotNull String permissionName) {
        this.permissionName = permissionName;
        this.permission = null;
    }

    public @Nullable Permission getPermission() {
        if (this.permission != null) {
            return this.permission;
        }

        if (isRequirePermission()) {
            String permissionName = getPermissionName();
            String description = "Allow a player to respawn automatically.";
            this.permission = new Permission(permissionName, description, PermissionDefault.FALSE);
            return this.permission;
        }

        return null;
    }

    public @NotNull List<String> getDisabledWorlds() {
        return Collections.unmodifiableList(this.disabledWorldList);
    }

    public void setDisabledWorlds(Collection<String> worlds) {
        this.disabledWorldList.clear();
        this.disabledWorldList.addAll(worlds);
    }

    public boolean isDisabledWorldListInverted() {
        return this.disabledWorldListInverted;
    }

    public void setDisabledWorldListInverted(boolean disabledWorldListInverted) {
        this.disabledWorldListInverted = disabledWorldListInverted;
    }

    public boolean isDisabled(@NotNull World world) {
        String worldName = world.getName();
        List<String> disabledWorldList = getDisabledWorlds();
        boolean contains = disabledWorldList.contains(worldName);
        boolean inverted = isDisabledWorldListInverted();
        return (inverted != contains);
    }

    public @NotNull List<String> getRespawnCommands() {
        return Collections.unmodifiableList(this.respawnCommandList);
    }

    public void setRespawnCommands(@NotNull Collection<String> commands) {
        this.respawnCommandList.clear();
        this.respawnCommandList.addAll(commands);
    }

    public @NotNull RespawnNearDeathConfiguration getRespawnNearDeath() {
        return this.respawnNearDeath;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
