package me.isaiah.mods.permissions.commands;

import com.mojang.brigadier.context.CommandContext;

import me.isaiah.mods.permissions.Config;
import me.isaiah.mods.permissions.CyberPermissionsMod;
import net.minecraft.server.command.ServerCommandSource;

public class GroupCommand extends Command {

	private static final String CMD_GROUP_HELP = "&4Usage: /perms group <group> <info|permissions|parentgroups>";
	
	/**
	 * "/perms group" Command
	 */
	public static int run(CommandContext<ServerCommandSource> context, ServerCommandSource cs, String[] args) {
		String[] argz0 = context.getInput().split(args[0] + " " + args[1] + " ");
        if (argz0.length <= 1) {
            message(cs, INVALID_ARGUMENTS);
            return 0;
        }
        String[] argz = argz0[1].split(" ");
        Config e = CyberPermissionsMod.groups.get(argz[0]);
        if (argz.length <= 0) {
        	message(cs, CMD_GROUP_HELP);
            return 0;
        }
        if (argz[1].equalsIgnoreCase("info")) {
        	message(cs, "&aInfo for group \"" + e.name + "\":");
        	message(cs, "&aName: " + e.name);
        	message(cs, "&2parentGroups:");
            for (String s : e.parentGroups)
            	message(cs, "&a- " + s);

            message(cs, "&2Group permissions:");
            for (String s : e.permissions)
            	message(cs, "&a- " + s);
            for (String s : e.negPermissions)
                message(cs, "&a- -" + s);
            return 0;
        }
        if (argz[1].equalsIgnoreCase("permissions")) {
            if (argz[2].equalsIgnoreCase("set")) {
                String perm = argz[3];
                String value = argz[4];
                e.setPermission(perm, Boolean.valueOf(value));
                message(cs, "&aPermission \"" + perm + "\" set to " + value + " for group " + argz[0]);
            }
        }

        if (argz[1].equalsIgnoreCase("parentgroups")) {
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
		return 0;
	}
	
}
