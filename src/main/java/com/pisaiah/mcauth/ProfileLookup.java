package com.pisaiah.mcauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Coppr (C) 2025 Isaiah.
 */
public class ProfileLookup {

	private static final String MOJANG_API =  "https://api.mojang.com/users/profiles/minecraft/";
	private static final String MOJANG_SESSION = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private static final String PLAYERDB_API = "https://playerdb.co/api/player/minecraft/";
	
	// TODO: Cache.
	// private static HashMap<String, String> UUID_CACHE = new HashMap<String, String>();
	
	/**
	 */
	public static UUID getUUIDIfOnline(MinecraftServer server, String playerName) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        return (player != null) ? player.getUuid() : null;
    }
	
	/**
	 */
	public static String getUUIDFromName(MinecraftServer server, String playerName) {
		
		UUID id = getUUIDIfOnline(server, playerName);
		if (null != id) {
			return id.toString();
		}

        String uuid = fetchFromMojang(playerName);
        if (uuid != null) return uuid;

        uuid = fetchFromPlayerDB(playerName);
        return uuid;
    }
	
	/**
	 */
	public static String getNameForUUID(String uuid) {
		/*
		UUID id = getUUIDIfOnline(server, playerName);
		if (null != id) {
			return id.toString();
		}
		*/
		
        String name = fetchNameFromMojang(uuid);
        
        // System.out.println("NAME: " + name);
        
        if (name != null) return name;

        name = fetchNameFromPlayerDB(name);
        return name;
	}

	/**
	 */
    private static String fetchFromMojang(String name) {
        try {
            URL url = new URL(MOJANG_API + name);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String json = response.toString();

            String ret= json.split("\"id\"")[1].split(",")[0]
            		.replaceAll(Pattern.quote("\""), "")
            		.replaceAll(Pattern.quote(":"), "")
            		.trim();
            
            if (ret.indexOf('-') == -1) {
    			// UUID without dashes
    			ret = ret.replaceFirst( 
    				        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5" 
    				    );
    		}
            return ret;
            
            /*
            int idStart = json.indexOf("\"id\":\"") + 6;
            int idEnd = json.indexOf("\"", idStart);
            return json.substring(idStart, idEnd);
            */
        } catch (Exception e) {
        	// e.printStackTrace();
            return null;
        }
    }

    /**
     */
    private static String fetchFromPlayerDB(String name) {
        try {
            URL url = new URL(PLAYERDB_API + name);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String json = response.toString();
            
            return json.split("\"id\"")[1].split(",")[0]
            		.replaceAll(Pattern.quote("\""), "")
            		.replaceAll(Pattern.quote(":"), "")
            		.trim();
            
            /*
            int idStart = json.indexOf("\"id\":\"") + 6;
            int idEnd = json.indexOf("\"", idStart);
            return json.substring(idStart, idEnd);
            */
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
	 */
    private static String fetchNameFromMojang(String uuid) {
        try {
            URL url = new URL(MOJANG_SESSION + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            System.out.println("RESP: " + conn.getResponseCode());
            
            if (conn.getResponseCode() != 200) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = "";
            String line;
            while ((line = in.readLine()) != null) response += line;
            in.close();

            return response.split("\"name\"")[1].split(",")[0]
            		.replaceAll(Pattern.quote("\""), "")
            		.replaceAll(Pattern.quote(":"), "")
            		.trim();
            
            /*
            int idStart = response.indexOf("\"name\":\"") + 6;
            int idEnd = response.indexOf("\"", idStart);
            return response.substring(idStart, idEnd);
            */
            
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }

    /**
     */
    private static String fetchNameFromPlayerDB(String uuid) {
        try {
            URL url = new URL(PLAYERDB_API + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String json = response.toString();
            
            return json.split("\"username\"")[1].split(",")[0]
            		.replaceAll(Pattern.quote("\""), "")
            		.replaceAll(Pattern.quote(":"), "")
            		.trim();
            
            /*
            int idStart = json.indexOf("\"username\":\"") + 11;//+ 6;
            int idEnd = json.indexOf("\"", idStart);
            return json.substring(idStart, idEnd);
            */
        } catch (Exception e) {
            return null;
        }
    }
	
}
