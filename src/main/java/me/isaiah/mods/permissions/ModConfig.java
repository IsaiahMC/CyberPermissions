package me.isaiah.mods.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

public class ModConfig {

	private File configFile = null;
	
	// Values
	public static boolean HIDE_CMD = false;
	
	// config instance
	private static ModConfig INSTANCE;
	
	public ModConfig() {
		INSTANCE = this;
	}
	
	public ModConfig get() {
		return INSTANCE;
	}
	
	public void init(File storage) {
		File configFile = new File(storage, "config.yml");
		this.configFile = configFile;
		
		
		if (!this.configFile.exists()) {
			try {
				this.configFile.createNewFile();
				this.write();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			List<String> lines = Files.readAllLines(configFile.toPath());
			
			for (String line : lines) {
				if (line.startsWith("hidePermsCommandIfPlayerNoPermission: ")) {
					String[] spl = line.split(Pattern.quote(":"));
					ModConfig.HIDE_CMD = Boolean.valueOf( spl[1].trim() );
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void write() throws IOException {
		
		if (null == configFile) {
			// Not init yet
		}
		
        String content = "# Configuration file for Permissions Mod (aka CyberPerms)\n";

        content += "hidePermsCommandIfPlayerNoPermission: false\n";
        
        Files.write(configFile.toPath(), content.getBytes());
    }
	
}
