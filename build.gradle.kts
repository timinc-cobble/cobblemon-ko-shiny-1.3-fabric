configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("us.timinc.mc.cobblemon.counter:cobblemon-counter:1.0.1")).using(module("curse.maven:cobblemon-counter-900238:4690076"))
    }
}

plugins {
    id("java")
    id("dev.architectury.loom") version ("0.12.0-SNAPSHOT")
    id("architectury-plugin") version ("3.4-SNAPSHOT")
    kotlin("jvm") version ("1.8.10")
}

group = "us.timinc.mc.cobblemon.koshiny"
version = "1.1.1"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven(url = "https://cursemaven.com")
    maven(url = "https://maven.draylar.dev/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.2")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.14.14")

    modImplementation("net.fabricmc:fabric-language-kotlin:1.9.3+kotlin.1.8.20")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.75.1+1.19.2")
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.75.1+1.19.2"))
    modImplementation("dev.architectury", "architectury-fabric", "6.5.69")
    modCompileOnly("com.cobblemon:mod:1.3.1+1.19.2-SNAPSHOT")
    modRuntimeOnly("com.cobblemon:fabric:1.3.1+1.19.2-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    modImplementation("us.timinc.mc.cobblemon.counter:cobblemon-counter:1.0.1")
    include("dev.draylar.omega-config:omega-config-base:1.3.0+1.19.2")
    modImplementation("dev.draylar.omega-config:omega-config-base:1.3.0+1.19.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}