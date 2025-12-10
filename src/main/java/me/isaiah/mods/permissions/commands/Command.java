package me.isaiah.mods.permissions.commands;

import me.isaiah.mods.permissions.CyberPermissionsMod;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Command {

	public static final String INVALID_ARGUMENTS = "&4Invalid arguments!";
	
	public static Text text(String message) {
		try {
			return Text.of(translate_alternate_color_codes('&', message));
		} catch (Exception e) {
			e.printStackTrace();
			return Text.of(message);
		}
	}

	public static void message(ServerCommandSource cs, String message) {
		try {
			if (null == cs.getPlayer()) {
	        	CyberPermissionsMod.LOGGER.info(message);
	        } else {
	        	cs.getPlayer().sendMessage(Text.of(translate_alternate_color_codes('&', message)), false);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private static final char COLOR_CHAR = '\u00A7';
    private static String translate_alternate_color_codes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }
    
    public static boolean checkPerm(ServerCommandSource cs, String permission) {
    	return checkPerm(cs, permission, true);
    }
    
    public static boolean checkPerm(ServerCommandSource cs, boolean print, String... perms) {
    	boolean has = Permissions.check(cs, "permissions.admin", 4);
    	for (String permission : perms ) {
    		if (checkPerm(cs, permission, false)) {
    			has = true;
    			break;
    		}
    	}
    	if (!has) {
			if (print) {
				message(cs, "&4This command requires one of the following permissions: " + String.join(", ", perms) + ", permissions.admin");
			}
		}
    	return has;
    }
    
    public static boolean checkPerm(ServerCommandSource cs, String... perms) {
    	return checkPerm(cs, true, perms);
    }

    public static boolean checkPerm(ServerCommandSource cs, String permission, boolean print) {
    	boolean hasPerm = Permissions.check(cs, permission, 4) || Permissions.check(cs, "permissions.admin", 4);
    	
    	if (!hasPerm) {
    		if (print) {
    			message(cs, "&4This command requires permission: " + permission);
    		}
    		return false;
    	}
    	return hasPerm;
    }

}
