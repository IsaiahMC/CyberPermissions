package me.isaiah.mods.permissions;

import net.minecraft.server.network.ServerPlayerEntity;

public class Utils {

	public static boolean isOp(ServerPlayerEntity e) {
		return e.isCreativeLevelTwoOp();
		
		// return e.getPermissionLevel() >= 1;
	        // return e.getServer().getPermissionLevel(e.getGameProfile()) >= 1;
	}

}
