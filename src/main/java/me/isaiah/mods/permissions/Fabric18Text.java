package me.isaiah.mods.permissions;

import net.minecraft.util.Formatting;
import net.minecraft.text.*;

import net.minecraft.util.Identifier;

import me.isaiah.lib.IText;

/**
 * Ensures compatiblity with colored chat in 1.18.2
 */
public class Fabric18Text {
    
	public static Text colored_literal(String txt, Formatting color) {
		return IText.colored_literal(txt, color);
	}

}