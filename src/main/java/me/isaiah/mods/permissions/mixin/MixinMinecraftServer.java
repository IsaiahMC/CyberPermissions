package me.isaiah.mods.permissions.mixin;

import org.spongepowered.asm.mixin.Mixin;

import cyber.permissions.v1.Permissible;
import cyber.permissions.v1.Permission;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer implements Permissible {

    @Override
    public boolean hasPermission(Permission id) {
        return true; // Console has all permissions
    }

    @Override
    public boolean isHighLevelOperator() {
        return true;
    }

    @Override
    public void setPermission(Permission id, boolean value) {
        // Can not set permissions for the dedicated server
    }

}