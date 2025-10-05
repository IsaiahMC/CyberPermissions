package me.isaiah.mods.permissions.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import cyber.permissions.v1.CyberPermissions;
import cyber.permissions.v1.Permissible;
import me.isaiah.mods.permissions.Config;
import me.isaiah.mods.permissions.CyberPermissionsMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PermsCommand implements com.mojang.brigadier.Command<ServerCommandSource>, Predicate<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {

    public LiteralCommandNode<ServerCommandSource> register(CommandDispatcher<ServerCommandSource> dispatcher, String label) {
        return dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal(label).requires(this).executes(this)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("args", StringArgumentType.greedyString()).suggests(this).executes(this))
        );
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        builder = builder.createOffset(builder.getInput().lastIndexOf(' ') + 1);

        List<String> results = new ArrayList<>();

        String input = builder.getInput();
        int spaces = input.length() - input.replaceAll(" ", "").length();
        String[] cmds = input.split(" ");
        if (cmds.length <= 1) {
            results.add("creategroup");
            results.add("listgroups");
            results.add("user");
            results.add("group");
        } else {
            if (cmds[1].equalsIgnoreCase("user")) {
                if (spaces == 2) {
                    for (String plr : context.getSource().getServer().getPlayerManager().getPlayerNames())
                        results.add(plr);
                } else if (spaces == 3){
                    results.add("info");
                    results.add("group");
                    results.add("permissions");
                } else if (cmds.length >= 4) {
                    if (cmds[3].equalsIgnoreCase("group")) {
                        if (spaces == 4) {
                            results.add("add");
                            results.add("remove");
                        } else if (cmds.length <= 5) {
                            for (String group : CyberPermissionsMod.groups.keySet())
                                results.add(group);
                        }
                    } else if (cmds[3].equalsIgnoreCase("permissions")) {
                        if (spaces == 4) {
                            results.add("set");
                        } else {
                            if (spaces == 5) {
                                results.add("PERMISSION");
                            } else {
                                results.add("true");
                                results.add("false");
                            }
                        }
                    }
                }
            } else if (cmds[1].equalsIgnoreCase("group")) {
                if (spaces == 2) {
                    for (String group : CyberPermissionsMod.groups.keySet())
                        results.add(group);
                } else if (spaces == 3){
                    results.add("info");
                    results.add("parentgroups");
                    results.add("permissions");
                } else if (cmds.length >= 4){
                    if (cmds[3].equalsIgnoreCase("parentgroups")) {
                        if (cmds.length <= 4) {
                            results.add("add");
                            results.add("remove");
                        } else if (cmds.length <= 5) {
                            for (String group : CyberPermissionsMod.groups.keySet())
                                results.add(group);
                        }
                    } else if (cmds[3].equalsIgnoreCase("permissions")) {
                        if (cmds.length <= 4) {
                            results.add("set");
                        } else {
                            if (cmds.length <= 5) {
                                results.add("PERMISSION");
                            } else {
                                results.add("true");
                                results.add("false");
                            }
                        }
                    }
                }
            }
        }

        boolean should = true;
        for (String s : results) if (input.endsWith(" " + s)) should = false;
        if (should) for (String s : results)
            builder.suggest(s);

        return builder.buildFuture();
    }

    @Override
    public boolean test(ServerCommandSource t) {
        return t.hasPermissionLevel(3); // Player is operator
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource cs = context.getSource();

        try {
            Permissible permissible = CyberPermissions.getPermissible(cs);
            if (!permissible.isHighLevelOperator()) {
            	sendMessage(cs, Formatting.RED, "This command requires operator permission.");
                return 0;
            }
            String[] args = context.getInput().split(" ");

            if (args.length <= 1) {
            	sendMessage(cs, null, "CyberPerms/SimplePerms Mod.");
                sendMessage(cs, null, "Invalid arguments!");
                return 0;
            }

            if (args[1].equalsIgnoreCase("creategroup")) {
                if (!CyberPermissionsMod.groups.containsKey(args[2])) {
                    try {
                        Config conf = new Config(args[2]);
                        CyberPermissionsMod.groups.put(conf.name, conf);
                        sendMessage(cs, null, "Created group: \"" + args[2] + "\"!");
                    } catch (IOException e) {
                        CyberPermissionsMod.LOGGER.error("Unable to load configuration for a group!");
                        e.printStackTrace();
                    }
                }
            }

            if (args[1].equalsIgnoreCase("listgroups")) {
            	sendMessage(cs, Formatting.BLUE, "List of Groups:");
                for (String group : CyberPermissionsMod.groups.keySet())
                    sendMessage(cs, null, group);
            }

            if (args[1].equalsIgnoreCase("user")) {
            	return UserCommand.run(context, cs, args);
            }

            if (args[1].equalsIgnoreCase("group")) {
                return GroupCommand.run(context, cs, args);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
	
	public Text colored_literal(String txt, Formatting color) {
		try {
			if (null == color) {
				return Text.of(txt);
			}
			return Text.of(txt).copy().formatted(color);
		} catch (Exception | IncompatibleClassChangeError e) {
			return Text.of(txt);
		}
	}

    public void sendMessage(ServerCommandSource cs, Formatting color, String message) {
        try {
	    	if (null == cs.getPlayer()) {
	        	CyberPermissionsMod.LOGGER.info(message);
	        } else {
	        	cs.getPlayer().sendMessage(colored_literal(message, color), false);
	        }
        } catch (Exception e) {
        	// 1.18.2
        	CyberPermissionsMod.LOGGER.info(message);
        }
    }

}