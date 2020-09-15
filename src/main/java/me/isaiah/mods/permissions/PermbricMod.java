package me.isaiah.mods.permissions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;

import me.isaiah.mods.permissions.commands.PermsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermbricMod implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger("CyberPermissions");
    public static File storage;

    public static HashMap<String, Config> groups = new HashMap<>();
    public static HashMap<String, Config> users = new HashMap<>();

    public static GameProfile findGameProfile(ServerCommandSource cs, String name) {
        if (name.length() > 30) // Name length max is 16, UUID minimum is 32 
            return cs.getMinecraftServer().getUserCache().getByUuid(UUID.fromString(name));
        return cs.getMinecraftServer().getUserCache().findByName(name);
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
        });
        LOGGER.info("CyberPermissions fully loaded...");
    }

}