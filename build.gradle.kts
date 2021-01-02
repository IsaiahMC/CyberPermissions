import net.fabricmc.loom.task.RemapJarTask

plugins {
    id ("fabric-loom") version "0.5-SNAPSHOT"
    id ("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = "CyberPermissions"
    version = "1.1"
    group = "com.javazilla.mods"
}

dependencies {
    minecraft ("com.mojang:minecraft:1.16.4")
    mappings ("net.fabricmc:yarn:1.16.4+build.7:v2")
    modImplementation ("net.fabricmc:fabric-loader:0.9.1+build.205")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:0.20.2+build.402-1.16")
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "version" to "1.1"
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
