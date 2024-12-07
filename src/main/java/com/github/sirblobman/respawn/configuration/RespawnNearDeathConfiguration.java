package com.github.sirblobman.respawn.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class RespawnNearDeathConfiguration implements IConfigurable {
    private boolean enabled;
    private boolean preventUnsafeRespawn;

    private double radiusX;
    private double radiusY;
    private double radiusZ;

    public RespawnNearDeathConfiguration() {
        this.enabled = false;
        this.radiusX = 5.0D;
        this.radiusY = 10.0D;
        this.radiusZ = 5.0D;
        this.preventUnsafeRespawn = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setEnabled(section.getBoolean("enabled", false));
        setPreventUnsafeRespawn(section.getBoolean("prevent-unsafe-respawn", true));

        // Old configurations may have only 'radius'.
        double radius = section.getDouble("radius", 5.0D);
        setRadiusX(section.getDouble("radius-x", radius));
        setRadiusY(section.getDouble("radius-y", radius));
        setRadiusZ(section.getDouble("radius-z", radius));
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPreventUnsafeRespawn() {
        return this.preventUnsafeRespawn;
    }

    public void setPreventUnsafeRespawn(boolean preventUnsafeRespawn) {
        this.preventUnsafeRespawn = preventUnsafeRespawn;
    }

    public double getRadiusX() {
        return this.radiusX;
    }

    public void setRadiusX(double radiusX) {
        this.radiusX = radiusX;
    }

    public double getRadiusY() {
        return this.radiusY;
    }

    public void setRadiusY(double radiusY) {
        this.radiusY = radiusY;
    }

    public double getRadiusZ() {
        return this.radiusZ;
    }

    public void setRadiusZ(double radiusZ) {
        this.radiusZ = radiusZ;
    }
}
