package me.isaiah.mods.permissions.mixin;

import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;

import cyber.permissions.v1.Permissible;
import cyber.permissions.v1.Permission;
import me.isaiah.mods.permissions.Config;
import me.isaiah.mods.permissions.CyberPermissionsMod;
import me.isaiah.mods.permissions.Utils;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements Permissible {

    private Config cyberPermConfig;

    @Override
    public boolean hasPermission(Permission id) {
        if (null == cyberPermConfig) {
            cyberPermConfig = CyberPermissionsMod.getUser((ServerPlayerEntity)(Object)this);
        }

        return cyberPermConfig.hasPermission(id.getPermissionAsString());
    }

    @Override
    public boolean isHighLevelOperator() {
        ServerPlayerEntity e = (ServerPlayerEntity)(Object)this;
        return Utils.isOp(e);
    }

    @Override
    public void setPermission(Permission id, boolean value) {
        cyberPermConfig.setPermission(id, value);
    }

}