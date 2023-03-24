plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Java Dependencies
    compileOnly("org.jetbrains:annotations:24.0.1") // JetBrains Annotations
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT") // Spigot API
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
