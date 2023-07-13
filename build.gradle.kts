import net.fabricmc.loom.task.RemapJarTask

plugins {
    id ("fabric-loom") version "1.1-SNAPSHOT"
    id ("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = "CyberPermissions"
    version = "1.5"
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

	minecraft ("com.mojang:minecraft:1.20.1")
	mappings ("net.fabricmc:yarn:1.20.1+build.9")
	modImplementation ("net.fabricmc:fabric-loader:0.14.21")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:0.85.0+1.20.1")
	modImplementation ("net.fabricmc.fabric-api:fabric-api-deprecated:0.85.0+1.20.1")
	
	// modImplementation(fileTree("dir" to "libs", "include" to "text-backwards-support-lib-0.1.jar"))
	// include(fileTree("dir" to "libs", "include" to "text-backwards-support-lib-0.1.jar"))

    // LuckPerms API
    modImplementation("me.lucko:fabric-permissions-api:0.1-SNAPSHOT")
    include("me.lucko:fabric-permissions-api:0.1-SNAPSHOT")
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "version" to "1.4"
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
