package me.isaiah.mods.permissions.commands;

import java.util.UUID;

import com.mojang.brigadier.context.CommandContext;

import me.isaiah.mods.permissions.Config;
import me.isaiah.mods.permissions.CyberPermissionsMod;
import net.minecraft.server.command.ServerCommandSource;

public class UserCommand extends Command {

	private static final String CMD_USER_HELP = "&4Usage: /perms user <user> <info|permissions|group|has>";
	
	/**
	 * "/perms user" Command
	 */
	public static int run(CommandContext<ServerCommandSource> context, ServerCommandSource cs, String[] args) {
		String[] argz0 = context.getInput().split(args[0] + " " + args[1] + " ");
		if (argz0.length <= 1) {
			message(cs, INVALID_ARGUMENTS);
			return 0;
		}
		String[] argz = argz0[1].split(" ");
		
		/*
		if (argz.length > 1 && argz[1].equalsIgnoreCase("getTest")) {
			// if (argz[2].equalsIgnoreCase("getTest")) {
				if (argz.length == 3) {
					message(cs, "&4Usage: /perms user " + argz[0] + " getTest <permission>");
					return 0;
				}

				String perm = argz[2]; 
				boolean has = CyberPermissionsMod.getOfflineUser(UUID.fromString(argz[0])).hasPermissionLucko(perm);
				
				message(cs, "Value of hasPermission for User \"" + argz[0] + "\" is = " + has);
				return 0;
			// }
		}
		*/

		String userKey = argz[0];
		Config e = CyberPermissionsMod.getUserFromNameOrUuid(cs.getServer(), argz[0]);

		if (argz.length <= 0) {
			message(cs, CMD_USER_HELP);
			return 0;
		}
 
		if (argz.length == 1 || argz[1].equalsIgnoreCase("info")) {
			
			if (!checkPerm(cs, "permissions.user.info")) {
        		return 1;
        	}
			
			message(cs, "&aInfo for user \"" + e.getName() + "\":");
			message(cs, "&aUUID: " + e.uuid);
			message(cs, "&2parentGroups:");

			for (String s : e.parentGroups) {
				message(cs, "&a- " + s);
			}

			message(cs, "&2User permissions:");
			for (String s : e.permissions) {
				message(cs, "&a- " + s);
			}
			for (String s : e.negPermissions) {
				message(cs, "&a- -" + s);
			}
			return 0;
		}
            
		if (argz[1].equalsIgnoreCase("group")) {
			
			if (argz.length <= 2) {
				message(cs, "&4Usage: /perms user " + userKey + " group <add/remove> <group>");
				return 0;
			}
			
			if (argz[2].equalsIgnoreCase("add")) {
				
				if (!checkPerm(cs, "permissions.user.group.add", "permissions.user.group.set")) {
	        		return 1;
	        	}
				
				if (argz.length == 3) {
					message(cs, "&4Usage: /perms user " + userKey + " group add <group>");
					return 0;
				}
				
				e.parentGroups.add(argz[3]);
				message(cs, "Group added!");
			}
			if (argz[2].equalsIgnoreCase("remove")) {
				
				
				if (!checkPerm(cs, "permissions.user.group.remove", "permissions.user.group.set")) {
	        		return 1;
	        	}
				
				if (argz.length == 3) {
					message(cs, "&4Usage: /perms user " + userKey + " group remove <group>");
					return 0;
				}
				
				e.parentGroups.remove(argz[3]);
				message(cs, "Group removed!");
			}
			e.save();
		}
		if (argz[1].equalsIgnoreCase("permissions")) {
			if (argz.length <= 2) {
				message(cs, "&4Usage: /perms user " + userKey + " permissions <set/get> <permission> [true|false]");
				return 0;
			}
			
			if (argz[2].equalsIgnoreCase("set")) {
				
				if (!checkPerm(cs, "permissions.user.permission.set", "permissions.user.permissions")) {
	        		return 1;
	        	}
				
				if (argz.length == 3) {
					message(cs, "&4Usage: /perms user " + userKey + " permissions set <permission> <true|false>");
					return 0;
				}

				String perm = argz[3];
				if (argz.length == 4) {
					message(cs, "&4Usage: /perms user " + userKey + " permissions set " + perm + " <true|false>");
					return 0;
				}

				String value = argz[4];
				e.setPermission(perm, Boolean.valueOf(value));
				message(cs, "&aPermission \"" + perm + "\" set to " + value + " for user " + argz[0]);
			}
			
			if (argz[2].equalsIgnoreCase("get")) {
				
				if (!checkPerm(cs, "permissions.user.permission.get", "permissions.user.permissions")) {
	        		return 1;
	        	}
				
				if (argz.length == 3) {
					message(cs, "&4Usage: /perms user " + userKey + " permissions get <permission>");
					return 0;
				}

				String perm = argz[3]; 
				boolean has = e.hasPermission(perm, true);
				
				message(cs, "Value of hasPermission for User \"" + userKey + "\" is = " + has);
				
			}
			
			if (argz[2].equalsIgnoreCase("getTest")) {
				
				if (!checkPerm(cs, "permissions.user.get")) {
	        		return 1;
	        	}
				
				if (argz.length == 3) {
					message(cs, "&4Usage: /perms user " + userKey + " permissions get <permission>");
					return 0;
				}

				String perm = argz[3]; 
				boolean has = e.hasPermissionLucko(perm);
				
				message(cs, "Value of hasPermission for User \"" + userKey + "\" is = " + has);
				
			}
		}
		return 0;
	}
	
}
