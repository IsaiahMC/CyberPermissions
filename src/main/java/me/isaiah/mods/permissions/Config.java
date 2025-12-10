package me.isaiah.mods.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.pisaiah.mcauth.ProfileLookup;

import cyber.permissions.v1.Permission;
//import net.minecraft.server.network.ServerPlayerEntity;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.util.TriState;

/**
 * TODO: Better Configuration
 * 
 * This is a horrible YAML parser.
 */
public class Config {

    private File file;

    public boolean isGroup;
    public String name;
    public String uuid;
    public List<String> parentGroups;

    public List<String> permissions;
    public List<String> negPermissions;
    
    private static final String OFFLINE_NAME = "?OFFLINE_PLAYER?";

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

                    if (start.equalsIgnoreCase("name")) {
                    	if (value.indexOf(OFFLINE_NAME) != -1) {

                    		// String name = ProfileLookup.getNameForUUID(this.uuid);
                    		// this.name = name;
                    		
                    		continue;
                    	}
                        name = value;
                        
                    }
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
    
    /**
     */
    public String getName() {
    	if (null == this.name) {
    		String name = ProfileLookup.getNameForUUID(this.uuid);
    		this.name = name;
    	}
    	return this.name;
    }
    
    public boolean hasName() {
    	return null != this.name;
    }
    
    /**
     */
    public String getNameIfGroup() {
    	
    	if (!this.isGroup) {
    		return getName();
    	}
    	
    	return this.name;
    }

    public void setPermission(Permission id, boolean value) {
        String str = id.getPermissionAsString();
        this.setPermission(str, value);
    }

    public void setPermission(String str, boolean value) {
        if (!value) {
            if (permissions.contains(str))
                permissions.remove(str);
            else negPermissions.add(str);
        } else {
            permissions.add(str);
        }
        this.save();
    }

    /*
    public Config(ServerPlayerEntity plr) throws IOException {
        this( new File(new File(CyberPermissionsMod.storage, "users"), plr.getUuid().toString() + ".yml") );

        this.name = plr.getName().getString();
        this.uuid = plr.getUuid().toString();
        this.isGroup = false;
        this.parentGroups.add("default");
        this.permissions.add("example.permission");
        this.write();
    }
    */

    public Config(String group) throws IOException {
        this( new File(new File(CyberPermissionsMod.storage, "groups"), group + ".yml") );

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

    /*
    @Deprecated
    public Config(GameProfile profile) throws IOException {
        this( new File(new File(CyberPermissionsMod.storage, "users"), profile.id().toString() + ".yml") );

        this.name = profile.name();
        this.uuid = profile.id().toString();
        this.isGroup = false;
        this.parentGroups.add("default");
        this.permissions.add("example.permission");
        this.write();
    }
    */
    
    public Config(String name, UUID uuid) throws IOException {
        this( new File(new File(CyberPermissionsMod.storage, "users"), uuid.toString() + ".yml") );
        
        this.name = name;
        this.uuid = uuid.toString();
        this.isGroup = false;
        this.parentGroups.add("default");
        this.permissions.add("example.permission");
        this.write();
        
    }
    
    public Config(UUID uuid) throws IOException {
        this( new File(new File(CyberPermissionsMod.storage, "users"), uuid.toString() + ".yml") );
        
        // this.name = name;
        this.uuid = uuid.toString();
        this.isGroup = false;
        this.parentGroups.add("default");
        this.permissions.add("example.permission");
        // this.write();
        
        this.writeNoName();
    }
    
    /**
     */
    public boolean hasNegativePermission(String permission) {
    	if (negPermissions.contains(permission)) {
            return true;
        }
    	
    	if (isGroup) {
        	return false;
        }

    	return false;
    }

    public boolean hasPermission(String permission) {
    	return hasPermission(permission, false);
    }
    
    public boolean hasPermission(String permission, boolean checkLucko) {
        if (negPermissions.contains(permission)) {
            return false;
        }
        if (permissions.contains(permission)) {
            return true;
        }

        for (String s : parentGroups) {
        	if (CyberPermissionsMod.groups.get(s).hasNegativePermission(permission)) {
        		return false;
        	}

            if (CyberPermissionsMod.groups.get(s).hasPermission(permission)) {
                return true;
            }
        }
        
        if (isGroup) {
        	return false;
        }
        
        // Check Fabric Permissions API
        if (checkLucko) {
	        try {
				CompletableFuture<Boolean> state = Permissions.check(UUID.fromString(this.uuid), permission);
				if (state.get()) {
					return true;
				}
			} catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
				e.printStackTrace();
			}
        }

        return false;
    }
    
    public boolean hasPermissionLucko(String permission) {
        // Check Fabric Permissions API
	    try {
	    	CompletableFuture<Boolean> state = Permissions.check(UUID.fromString(this.uuid), permission);
			if (state.get()) {
				return true;
			}
		} catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
			e.printStackTrace();
		}
        return false;
    }
    
    public void save() {
        try {
            write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeNoName() throws IOException {
        write(OFFLINE_NAME);
    }
    
    public void write() throws IOException {
    	write( getName() );
    }
    
    private void write(String saveName) throws IOException {
        String groups = "parentGroups: ";
        for (String s : parentGroups) {
            groups += s + ", ";
        }

        String text = (null != uuid ? ("uuid: " + uuid + "\n") : "") + "name: " + saveName + "\nisGroup: " + isGroup + (parentGroups.size() > 0 ? ("\n" + groups) : "") + "\npermissions:\n";
        for (String s : negPermissions) {
            text += "- -" + s + "\n";
        }
        for (String s : permissions) {
            text += "- " + s + "\n";
        }

        Files.write(file.toPath(), text.getBytes());
    }

}