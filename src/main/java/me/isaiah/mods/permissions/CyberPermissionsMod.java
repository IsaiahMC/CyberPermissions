package me.isaiah.mods.permissions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import cyber.permissions.v1.CyberPermissions;
import cyber.permissions.v1.Permissible;
import cyber.permissions.v1.Permission;
import cyber.permissions.v1.PermissionDefaults;
import me.isaiah.mods.permissions.commands.PermsCommand;
import me.lucko.fabric.api.permissions.v0.PermissionCheckEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CyberPermissionsMod implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger("CyberPermissions");
    public static File storage;

    public static HashMap<String, Config> groups = new HashMap<>();
    public static HashMap<String, Config> users = new HashMap<>();

    public static GameProfile findGameProfile(ServerCommandSource cs, String name) {
        if (name.length() >= 32) // Name length max is 16, UUID minimum is 32 
            return cs.getServer().getUserCache().getByUuid(UUID.fromString(name)).get();
        return cs.getServer().getUserCache().findByName(name).get();
    }

    public static Config getUser(GameProfile profile) {
        String uuid = profile.getId().toString();
        if (users.containsKey(uuid))
            return users.get(uuid);

        try {
            Config conf = new Config(profile);
            users.put(conf.uuid, conf);
            return conf;
        } catch (IOException ex) {
            LOGGER.error("Unable to load configuration for a user!");
            ex.printStackTrace();
            return null;
        }
    }

    public static Config getUser(ServerPlayerEntity e) {
        String uuid = e.getUuid().toString();
        if (users.containsKey(uuid))
            return users.get(uuid);

        try {
            Config conf = new Config(e);
            users.put(conf.uuid, conf);
            return conf;
        } catch (IOException ex) {
            LOGGER.error("Unable to load configuration for a user!");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading CyberPermissions...");
        storage = new File(FabricLoader.getInstance().getConfigDir().toFile(), "permissions");
        storage.mkdirs();

        File groupsDir = new File(storage, "groups");
        groupsDir.mkdir();
        for (File f : groupsDir.listFiles()) {
            try {
                Config conf = new Config(f);
                groups.put(conf.name, conf);
            } catch (IOException e) {
                LOGGER.error("Unable to load configuration for a group!");
                e.printStackTrace();
            }
        }
        if (!groups.containsKey("default")) {
            try {
                Config conf = new Config("default");
                groups.put(conf.name, conf);
            } catch (IOException e) {
                LOGGER.error("Unable to load configuration for a group!");
                e.printStackTrace();
            }
        }

        File usersDir = new File(storage, "users");
        usersDir.mkdir();
        for (File f : usersDir.listFiles()) {
            try {
                Config conf = new Config(f);
                users.put(conf.uuid, conf);
            } catch (IOException e) {
                LOGGER.error("Unable to load configuration for a user!");
                e.printStackTrace();
            }
        }

        PermsCommand cmd = new PermsCommand();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            cmd.register(dispatcher, "perms");
            cmd.register(dispatcher, "cyberperms");
            /*String[] lables = {"perms", "cyberperms"};
            for (String label : lables)
                dispatcher.register(literal(label)
                    .then( argument("group", StringArgumentType.string())
                            .then( argument("name", StringArgumentType.word()).executes(new GroupSubcommand())
                                    .then( argument("info", StringArgumentType.word()).executes(new GroupSubcommand()) )
                                    .then( argument("permissions", StringArgumentType.word()).executes(new GroupSubcommand()) )
                                    .then( argument("parentgroups", StringArgumentType.word()).executes(new GroupSubcommand()) ) )
                            .executes(new GroupSubcommand()) )
                    .then( argument("user",  StringArgumentType.word())
                            .then( argument("name", StringArgumentType.word()).executes(new UserGroupSubcommand())
                                .then( argument("group", StringArgumentType.word()).executes(new UserGroupSubcommand())
                                        .then( argument("add", StringArgumentType.word()).executes(new UserGroupSubcommand())
                                                .then( argument("group name", StringArgumentType.word()).executes(new UserGroupSubcommand()) ))
                                        .then( argument("remove", StringArgumentType.word()).executes(new UserGroupSubcommand())
                                                .then( argument("group name", StringArgumentType.word()).executes(new UserGroupSubcommand()) ))
                                )
                                .then( argument("info", StringArgumentType.word()).executes(new UserSubcommand()) )
                                .then( argument("permissions", StringArgumentType.word()).executes(new UserSubcommand()) )
                             )
                            .executes(new UserSubcommand())  )
                    .executes(cmd));*/
        });

        PermissionCheckEvent.EVENT.register((source, permission) -> {
            if (source instanceof ServerCommandSource) {
                ServerCommandSource ss = (ServerCommandSource) source;
                try {
                    ServerPlayerEntity plr = ss.getPlayer();
                    Permissible p = CyberPermissions.getPlayerPermissible(plr);
                    Permission perm = new Permission(permission, "LuckPerms API provided permission", PermissionDefaults.OPERATOR);
                    boolean hass = p.hasPermission(perm);
					if (hass) {
						return TriState.TRUE;
					}
                } catch (Exception e) {
                    // Not Player
                }
            }
            
            return TriState.DEFAULT;
        });
        
        LOGGER.info("CyberPermissions fully loaded...");
    }

    public LiteralArgumentBuilder<ServerCommandSource> literal(String name) {
        return LiteralArgumentBuilder.<ServerCommandSource>literal(name);
    }

    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.<ServerCommandSource, T>argument(name, type);
    }

}