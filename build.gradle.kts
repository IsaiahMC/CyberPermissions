import net.fabricmc.loom.task.RemapJarTask

plugins {
    id ("fabric-loom") version "1.11-SNAPSHOT"
    id ("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = "CyberPermissions"
    version = "1.5.4"
    group = "com.javazilla.mods"
}

repositories {
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {

	/*
	minecraft ("com.mojang:minecraft:1.18.2")
    mappings ("net.fabricmc:yarn:1.18.2+build.4:v2")
    modImplementation ("net.fabricmc:fabric-loader:0.14.17")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:0.75.1+1.18.2")
	*/

	//minecraft ("com.mojang:minecraft:1.19.2")
    //mappings ("net.fabricmc:yarn:1.19.2+build.28:v2")
    //modImplementation ("net.fabricmc:fabric-loader:0.14.17")
    //modImplementation ("net.fabricmc.fabric-api:fabric-api:0.75.1+1.19.2")
	//modImplementation ("net.fabricmc.fabric-api:fabric-api-deprecated:0.75.1+1.19.2")

	//minecraft ("com.mojang:minecraft:1.19.4")
    //mappings ("net.fabricmc:yarn:1.19.4+build.1:v2")
   // modImplementation ("net.fabricmc:fabric-loader:0.14.17")
    //modImplementation ("net.fabricmc.fabric-api:fabric-api:0.75.3+1.19.4")
	//modImplementation ("net.fabricmc.fabric-api:fabric-api-deprecated:0.75.3+1.19.4")	

	/*
	minecraft ("com.mojang:minecraft:1.20.1")
	mappings ("net.fabricmc:yarn:1.20.1+build.9")
	modImplementation ("net.fabricmc:fabric-loader:0.17.2")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:0.92.6+1.20.1")
	*/
	
	/*
	minecraft ("com.mojang:minecraft:1.21.1")
	mappings ("net.fabricmc:yarn:1.21.1+build.3")
	modImplementation ("net.fabricmc:fabric-loader:0.17.2")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:0.116.6+1.21.1")
	*/
	
	/*
	minecraft ("com.mojang:minecraft:1.21.8")
	mappings ("net.fabricmc:yarn:1.21.8+build.1")
	modImplementation ("net.fabricmc:fabric-loader:0.17.2")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:0.134.0+1.21.8")	
	*/
	
	minecraft ("com.mojang:minecraft:1.21.9")
	mappings ("net.fabricmc:yarn:1.21.9+build.1")
	modImplementation ("net.fabricmc:fabric-loader:0.17.2")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:0.134.0+1.21.9")
	
	// modImplementation(fileTree("dir" to "libs", "include" to "text-backwards-support-lib-0.1.jar"))
	// include(fileTree("dir" to "libs", "include" to "text-backwards-support-lib-0.1.jar"))

    // LuckPerms API
    // modImplementation("me.lucko:fabric-permissions-api:0.3.3")
    // include("me.lucko:fabric-permissions-api:0.3.3")
	
	// modImplementation("me.lucko:fabric-permissions-api:0.5.0")
    // include("me.lucko:fabric-permissions-api:0.5.0")
	
	// modImplementation("me.lucko:fabric-permissions-api:0.4.1")
    // include("me.lucko:fabric-permissions-api:0.4.1")
	
	modImplementation("me.lucko:fabric-permissions-api:0.5.0")
    // include("me.lucko:fabric-permissions-api:0.5.0")
	
	/*
	modImplementation(fileTree("dir" to "libs", "include" to "fabric-permissions-api-0.3.3.jar"))
	include(fileTree("dir" to "libs", "include" to "fabric-permissions-api-0.3.3.jar"))
	
	modImplementation(fileTree("dir" to "libs", "include" to "fabric-permissions-api-0.4.1.jar"))
	include(fileTree("dir" to "libs", "include" to "fabric-permissions-api-0.4.1.jar"))
	
	modImplementation(fileTree("dir" to "libs", "include" to "fabric-permissions-api-0.5.0.jar"))
	include(fileTree("dir" to "libs", "include" to "fabric-permissions-api-0.5.0.jar"))
	*/
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "version" to "1.5.4"
            )
        )
    }
}

val remapJar = tasks.getByName<RemapJarTask>("remapJar")

publishing {
    publications {
        create("main", MavenPublication::class.java) {
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()
            artifact(remapJar)
        }
    }

    repositories {
        val mavenUsername: String? by project
        val mavenPassword: String? by project
        mavenPassword?.let {
            maven(url = "https://repo.codemc.io/repository/maven-releases/") {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}
