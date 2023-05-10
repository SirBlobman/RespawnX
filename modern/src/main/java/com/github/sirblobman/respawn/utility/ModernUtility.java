package com.github.sirblobman.respawn.utility;

import org.jetbrains.annotations.NotNull;

import org.bukkit.World;

public final class ModernUtility {
    public static int getMinWorldHeight(@NotNull World world) {
        return world.getMinHeight();
    }
}
