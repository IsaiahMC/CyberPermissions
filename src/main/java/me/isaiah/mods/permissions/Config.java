package me.isaiah.mods.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.GameProfile;

import cyber.permissions.v1.Permission;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * TODO: Better Configuration
 */
public class Config {

    private File file;

    public boolean isGroup;
    public String name;
    public String uuid;
    public List<String> parentGroups;

    public List<String> permissions;
    public List<String> negPermissions;

    public Config(File f) throws IOException {
        this.parentGroups = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.negPermissions = new ArrayList<>();
        this.file = f;

        if (f.exists()) {
            List<String> list = Files.readAllLines(file.toPath());
            String last = "";
            for (String s : list) {
                if (s.contains(":")) {
                    String start = s.split(":")[0];
                    if (start.equalsIgnoreCase("permissions"))
                        last = start;

                    if (s.split(":").length <= 1)
                        continue;
                    String value = s.split(":")[1].trim();
                    if (!last.equalsIgnoreCase(start))
                        last = "";

                    if (start.equalsIgnoreCase("name"))
                        name = value;
                    if (start.equalsIgnoreCase("uuid"))
                        uuid = value;
                    if (start.equalsIgnoreCase("isGroup"))
                        isGroup = Boolean.valueOf(value);
                    if (start.equalsIgnoreCase("parentGroups")) {
                        String[] txt = value.split(",");
                        for (String group : txt) {
                            parentGroups.add(group.trim());
                        }
                    }
                }
                if (last.equalsIgnoreCase("permissions") && s.startsWith("- ")) {
                    String perm = s.substring(1).trim();
                    if (perm.startsWith("-")) {
                        negPermissions.add(perm.substring(1).trim());
                    } else {
                        permissions.add(perm);
                    }
                }
            }
        }
    }

    public void setPermission(Permission id, boolean value) {
        String str = id.getPermissionAsString();
        setPermission(str, value);
    }

    public void setPermission(String str, boolean value) {
        if (!value) {
            if (permissions.contains(str))
                permissions.remove(str);
            else negPermissions.add(str);
        } else {
            permissions.add(str);
        }
    }

    public Config(ServerPlayerEntity plr) throws IOException {
        this( new File(new File(PermbricMod.storage, "users"), plr.getUuid().toString() + ".yml") );

        this.name = plr.getName().asString();
        this.uuid = plr.getUuid().toString();
        this.isGroup = false;
        this.parentGroups.add("default");
        this.permissions.add("example.permission");
        this.write();
    }

    public Config(String group) throws IOException {
        this( new File(new File(PermbricMod.storage, "groups"), group + ".yml") );

        if (this.file.exists())
            return;

        this.name = group;
        this.uuid = null;
        this.isGroup = true;
        if (!group.equalsIgnoreCase("default"))
            this.parentGroups.add("default");
        this.permissions.add("example.permission");
        this.write();
    }

    public Config(GameProfile profile) throws IOException {
        this( new File(new File(PermbricMod.storage, "users"), profile.getId().toString() + ".yml") );

        this.name = profile.getName();
        this.uuid = profile.getId().toString();
        this.isGroup = false;
        this.parentGroups.add("default");
        this.permissions.add("example.permission");
        this.write();
    }

    public boolean hasPermission(String permission) {
        if (negPermissions.contains(permission))
            return false;
        if (permissions.contains(permission))
            return true;

        for (String s : parentGroups)
            if (PermbricMod.groups.get(s).hasPermission(permission))
                return true;

        return false;
    }

    public void write() throws IOException {
        String groups = "parentGroups: ";
        for (String s : parentGroups)
            groups += s + ", ";

        String text = (null != uuid ? ("uuid: " + uuid + "\n") : "") + "name: " + name + "\nisGroup: " + isGroup + (parentGroups.size() > 0 ? ("\n" + groups) : "") + "\npermissions:\n";
        for (String s : negPermissions)
            text += "- -" + s;
        for (String s : permissions)
            text += "- " + s;

        Files.write(file.toPath(), text.getBytes());
    }

}