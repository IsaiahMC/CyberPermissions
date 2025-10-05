package me.isaiah.mods.permissions.commands;

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

		Config e = CyberPermissionsMod.getUserFromNameOrUuid(cs.getServer(), argz[0]);

		if (argz.length <= 0) {
			message(cs, CMD_USER_HELP);
			return 0;
		}
            
		if (argz.length == 1 || argz[1].equalsIgnoreCase("info")) {
			message(cs, "&aInfo for user \"" + e.name + "\":");
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
			if (argz[2].equalsIgnoreCase("add")) {
				e.parentGroups.add(argz[3]);
				message(cs, "Group added!");
			}
			if (argz[2].equalsIgnoreCase("remove")) {
				e.parentGroups.remove(argz[3]);
				message(cs, "Group removed!");
			}
			e.save();
		}
		if (argz[1].equalsIgnoreCase("permissions")) {
			if (argz[2].equalsIgnoreCase("set")) {
				String perm = argz[3];
				String value = argz[4];
				e.setPermission(perm, Boolean.valueOf(value));
				message(cs, "Permission \"" + perm + "\" set to " + value + " for user " + argz[0]);
			}
		}
		return 0;
	}
	
}
