package me.isaiah.mods.permissions.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Pattern;

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
import me.isaiah.mods.permissions.ModConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
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

       
        try {
        	if (!ModConfig.HIDE_CMD) {
	        	/*
        		boolean has = Permissions.require("permissions.admin", 4).or(Permissions.require("permissions.cmd")).test(context.getSource());
	        	if (!has) {
	        		return builder.buildFuture();
	        	}
	        	*/
        		if (!canRunAnyCommand( context.getSource() )) {
        			return builder.buildFuture();
        		}
        	}
        } catch (Exception e) {
        	
        }
        
        
        List<String> results = new ArrayList<>();

        ServerCommandSource cs = context.getSource();
        
        String input = builder.getInput();
        int spaces = input.length() - input.replaceAll(" ", "").length();
        String[] cmds = input.split(" ");
        if (cmds.length <= 1) {
        	if (checkPerm(cs, "permissions.creategroup")) results.add("creategroup");
        	if (checkPerm(cs, "permissions.listgroups"))  results.add("listgroups");
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

    	if (ModConfig.HIDE_CMD) {
    		return t.hasPermissionLevel(3);
    	} else {
    		return true;
    	}

    	// return t.hasPermissionLevel(3); // Player is operator
    }
    
    /*
    public boolean checkPerm(ServerCommandSource cs, String permission) {
    	return checkPerm(cs, permission, true);
    }
    */

    public boolean checkPerm(ServerCommandSource cs, String permission, boolean print) {
    	return Command.checkPerm(cs, permission, print);
    }
    
    public boolean checkPerm(ServerCommandSource cs, boolean print, String... permissions) {
    	return Command.checkPerm(cs, print, permissions);
    }
    
    public boolean checkPerm(ServerCommandSource cs, String... permissions) {
    	return Command.checkPerm(cs, false, permissions);
    }
    
    public String[] stuff = {
			"permissions.creategroup=/perms creategroup <group>",
			"permissions.listgroups=/perms listgroups",
			"permissions.user.info=/perms user <user> info",
			"permissions.user.group.add,permissions.user.group.set=/perms user <user> group add",
			"permissions.user.group.remove,permissions.user.group.set=/perms user <user> group remove",
			"permissions.user.permission.set,permissions.user.permissions=/perms user <user> permissions set <perm> <true|false>",
			"permissions.user.permission.get,permissions.user.permissions=/perms user <user> permissions get <perm>",
			"permissions.group.info=/perms group <group> info",
			"permissions.group.permission.set,permissions.group.permissions=/perms group <group> permissions set <perm> <true|false>",
			"permissions.group.permission.get,permissions.group.permissions=/perms group <group> permissions get <perm>",
			"permissions.group.parent.set=/perms group <group> parentgroups <add/remove> <parentGroup>"
	};
    
    public ArrayList<String> cmdPerms = new ArrayList<>();

    public boolean canRunAnyCommand(ServerCommandSource cs) {
    	for (String perm : cmdPerms) {
    		if (checkPerm(cs, perm)) {
    			return true;
    		}
    	}
    	
    	for (String s : stuff) {
    		String[] spl = s.split(Pattern.quote("="));
    		String perm = spl[0];

    		if (perm.indexOf(',') != -1) {
    			String[] perms = perm.split(Pattern.quote(","));
    			for (String perm1 : perms) {
    				cmdPerms.add(perm1);
    			}
    			
    			if (checkPerm(cs, perms)) {
    				return true;
    			}
    		} else {
    			cmdPerms.add(perm);
    			
        		if (checkPerm(cs, perm)) {
            		return true;
            	}
    		}
    	}
    	
    	return Permissions.check(cs, "permissions.admin", 4) || Permissions.check(cs, "permissions.cmd", 4);
    }
    
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource cs = context.getSource();

        try {
        	
        	boolean hasPerm = Permissions.check(cs, "permissions.admin", 4) || canRunAnyCommand(cs);
        	
            Permissible permissible = CyberPermissions.getPermissible(cs);
            if (!hasPerm) {
            	hasPerm = permissible.isHighLevelOperator();
            }
            
            if (!hasPerm && ModConfig.HIDE_CMD) {
            	sendMessage(cs, Formatting.RED, "This command requires operator permission.");
                return 0;
            }
            String[] args = context.getInput().split(" ");

            if (args.length <= 1) {
            	sendMessage(cs, Formatting.AQUA, "CyberPerms/SimplePerms Mod.");

                String[] usageMsg = {
                		"Command Usage:",
                		// "/perms user Player - Player Permissions Info",
                		"/perms user <user> <info|permissions|group|has>",
                		"/perms group <group> <info|permissions|users|has>",
                		"/perms creategroup <group>",
                		"/perms listgroups",
                		"See \"/perms help\" for more details"
                };

                sendMessages(cs, null, usageMsg);
                
                
                return 0;
            }
            
            if (args[1].equalsIgnoreCase("help")) {
            	sendMessage(cs, Formatting.AQUA, "Subcommands You have Permission for:");

            	for (String s : stuff) {
            		String[] spl = s.split(Pattern.quote("="));
            		String perm = spl[0];
            		
            		if (perm.indexOf(',') != -1) {
            			String[] perms = perm.split(Pattern.quote(","));
            			boolean has = checkPerm(cs, perms);
            			if (has) {
            				sendMessage(cs, Formatting.GREEN, spl[1]);
            			} else {
            				sendMessage(cs, Formatting.RED, "(No Permission: " + perms[0] + ")");
            			}
            		} else {
	            		if (checkPerm(cs, perm)) {
	                		sendMessage(cs, Formatting.GREEN, spl[1]);
	                	} else {
            				sendMessage(cs, Formatting.RED, "(No Permission: " + perm + ")");
            			}
            		}
            	}
            	
            }

            if (args[1].equalsIgnoreCase("creategroup")) {
            	
            	if (!checkPerm(cs, "permissions.creategroup")) {
            		return 1;
            	}
            	
                if (!CyberPermissionsMod.groups.containsKey(args[2])) {
                    try {
                        Config conf = new Config(args[2]);
                        CyberPermissionsMod.groups.put(conf.name, conf);
                        sendMessage(cs, null, "Created group: \"" + args[2] + "\"!");
                        conf.save();
                    } catch (IOException e) {
                        CyberPermissionsMod.LOGGER.error("Unable to load configuration for a group!");
                        e.printStackTrace();
                    }
                }
            }

            if (args[1].equalsIgnoreCase("listgroups")) {
            	
            	if (!checkPerm(cs, "permissions.listgroups")) {
            		return 1;
            	}
            	
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
    
    public void sendMessages(ServerCommandSource cs, Formatting color, String... messages) {
        try {
	    	if (null == cs.getPlayer()) {
	    		for (String message : messages) { CyberPermissionsMod.LOGGER.info(message); }
	        } else {
	        	for (String message : messages) {
	        		cs.getPlayer().sendMessage(colored_literal(message, color), false);
	        	}
	        }
        } catch (Exception e) {
        	// 1.18.2
        	for (String message : messages) { CyberPermissionsMod.LOGGER.info(message); }
        }
    }

}