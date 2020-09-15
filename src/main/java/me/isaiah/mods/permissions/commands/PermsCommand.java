package me.isaiah.mods.permissions.commands;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import com.mojang.authlib.GameProfile;
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
import cyber.permissions.v1.Permission;
import me.isaiah.mods.permissions.Config;
import me.isaiah.mods.permissions.PermbricMod;
import net.minecraft.SharedConstants;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
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
                cs.sendFeedback(new LiteralText("This command requires operator permission.").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                return 0;
            }
            String[] args = context.getInput().split(" ");
            if (args.length <= 1) {
                sendMessage(cs, null, "Invalid arguments!");
                return 0;
            }

            if (args[1].equalsIgnoreCase("user")) {
                String[] argz0 = context.getInput().split(args[0] + " " + args[1] + " ");
                if (argz0.length <= 1) {
                    sendMessage(cs, null, "Invalid arguments!");
                    return 0;
                }
                String[] argz = argz0[1].split(" ");
                Config e = PermbricMod.getUser(PermbricMod.findGameProfile(cs, argz[0]));
                if (argz.length <= 0) {
                    sendMessage(cs, null, "Usage: /perms user <user> <info|permissions|group|has>");
                    return 0;
                }
                if (argz[1].equalsIgnoreCase("info")) {
                    sendMessage(cs, Formatting.GREEN, "Info for user \"" + e.name + "\":");
                    sendMessage(cs, Formatting.GREEN, "UUID: " + e.uuid);
                    sendMessage(cs, Formatting.DARK_GREEN, "parentGroups:");
                    for (String s : e.parentGroups)
                        sendMessage(cs, Formatting.GREEN, "- " + s);

                    sendMessage(cs, Formatting.DARK_GREEN, "User permissions:");
                    for (String s : e.permissions)
                        sendMessage(cs, Formatting.GREEN, "- " + s);
                    for (String s : e.negPermissions)
                        sendMessage(cs, Formatting.GREEN, "- -" + s);
                    return 0;
                }
                if (argz[1].equalsIgnoreCase("permissions")) {
                    if (argz[2].equalsIgnoreCase("set")) {
                        String perm = argz[3];
                        String value = argz[4];
                        e.setPermission(perm, Boolean.valueOf(value));
                        sendMessage(cs, null, "Permission \"" + perm + "\" set to " + value + " for user");
                    }
                }

                if (argz[1].equalsIgnoreCase("group")) {
                    if (argz[2].equalsIgnoreCase("add")) {
                        e.parentGroups.add(argz[3]);
                        sendMessage(cs, null, "Group added!");
                    }
                    if (argz[2].equalsIgnoreCase("remove")) {
                        e.parentGroups.remove(argz[3]);
                        sendMessage(cs, null, "Group removed!");
                    }
                }

                sendMessage(cs, null, argz[1]);
            }

            if (args[1].equalsIgnoreCase("group")) {
                String[] argz0 = context.getInput().split(args[0] + " " + args[1] + " ");
                if (argz0.length <= 1) {
                    sendMessage(cs, null, "Invalid arguments!");
                    return 0;
                }
                String[] argz = argz0[1].split(" ");
                Config e = PermbricMod.groups.get(argz[0]);
                if (argz.length <= 0) {
                    sendMessage(cs, null, "Usage: /perms group <group> <info|permissions|parentgroups>");
                    return 0;
                }
                if (argz[1].equalsIgnoreCase("info")) {
                    sendMessage(cs, Formatting.GREEN, "Info for group \"" + e.name + "\":");
                    sendMessage(cs, Formatting.GREEN, "Name: " + e.name);
                    sendMessage(cs, Formatting.DARK_GREEN, "parentGroups:");
                    for (String s : e.parentGroups)
                        sendMessage(cs, Formatting.GREEN, "- " + s);

                    sendMessage(cs, Formatting.DARK_GREEN, "Group permissions:");
                    for (String s : e.permissions)
                        sendMessage(cs, Formatting.GREEN, "- " + s);
                    for (String s : e.negPermissions)
                        sendMessage(cs, Formatting.GREEN, "- -" + s);
                    return 0;
                }
                if (argz[1].equalsIgnoreCase("permissions")) {
                    if (argz[2].equalsIgnoreCase("set")) {
                        String perm = argz[3];
                        String value = argz[4];
                        e.setPermission(perm, Boolean.valueOf(value));
                        sendMessage(cs, null, "Permission \"" + perm + "\" set to " + value + " for group");
                    }
                }

                if (argz[1].equalsIgnoreCase("parentgroups")) {
                    if (argz[2].equalsIgnoreCase("add")) {
                        e.parentGroups.add(argz[3]);
                        sendMessage(cs, null, "Group added!");
                    }
                    if (argz[2].equalsIgnoreCase("remove")) {
                        e.parentGroups.remove(argz[3]);
                        sendMessage(cs, null, "Group removed!");
                    }
                }

                sendMessage(cs, null, argz[1]);
            }

            if (args[1].equalsIgnoreCase("creategroup")) {
                if (!PermbricMod.groups.containsKey(args[2])) {
                    try {
                        Config conf = new Config(args[2]);
                        PermbricMod.groups.put(conf.name, conf);
                        sendMessage(cs, null, "Created group: \"" + args[2] + "\"!");
                    } catch (IOException e) {
                        PermbricMod.LOGGER.error("Unable to load configuration for a group!");
                        e.printStackTrace();
                    }
                }
            }

            if (args[1].equalsIgnoreCase("listgroups")) {
                for (String group : PermbricMod.groups.keySet())
                    sendMessage(cs, null, group);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void sendMessage(ServerCommandSource cs, Formatting color, String message) {
        LiteralText txt = new LiteralText(message);
        cs.sendFeedback(color != null ? txt.setStyle(Style.EMPTY.withColor(color)) : txt, false);
    }

}