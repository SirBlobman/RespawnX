package com.github.sirblobman.respawn.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class RespawnNearDeathConfiguration implements IConfigurable {
    private boolean enabled;
    private double radius;
    private boolean preventUnsafeRespawn;

    public RespawnNearDeathConfiguration() {
        this.enabled = false;
        this.radius = 1.0D;
        this.preventUnsafeRespawn = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setEnabled(section.getBoolean("enabled", false));
        setRadius(section.getDouble("radius", 1.0D));
        setPreventUnsafeRespawn(section.getBoolean("prevent-unsafe-respawn", true));
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isPreventUnsafeRespawn() {
        return this.preventUnsafeRespawn;
    }

    public void setPreventUnsafeRespawn(boolean preventUnsafeRespawn) {
        this.preventUnsafeRespawn = preventUnsafeRespawn;
    }
}
