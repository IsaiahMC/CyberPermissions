package me.isaiah.mods.permissions.commands;

import me.isaiah.mods.permissions.CyberPermissionsMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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

}
