import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val baseVersion = fetchProperty("version.base", "invalid")
val betaString = fetchProperty("version.beta", "false")
val jenkinsBuildNumber = fetchEnv("BUILD_NUMBER", null, "Unofficial")

val betaBoolean = betaString.toBoolean()
val betaVersion = if (betaBoolean) "Beta-" else ""
version = "$baseVersion.$betaVersion$jenkinsBuildNumber"

fun fetchProperty(propertyName: String, defaultValue: String): String {
    val found = findProperty(propertyName)
    if (found != null) {
        return found.toString()
    }

    return defaultValue
}

fun fetchEnv(envName: String, propertyName: String?, defaultValue: String): String {
    val found = System.getenv(envName)
    if (found != null) {
        return found
    }

    if (propertyName != null) {
        return fetchProperty(propertyName, defaultValue)
    }

    return defaultValue
}

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://nexus.sirblobman.xyz/public/")
}

dependencies {
    // Java Dependencies
    compileOnly("org.jetbrains:annotations:26.0.1") // JetBrains Annotations
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT") // Spigot API

    // Plugin Dependencies
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT") // BlueSlimeCore
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("RespawnX")
        archiveClassifier.set(null as String?)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    processResources {
        val pluginName = fetchProperty("bukkit.plugin.name", "")
        val pluginPrefix = fetchProperty("bukkit.plugin.prefix", "")
        val pluginDescription = fetchProperty("bukkit.plugin.description", "")
        val pluginWebsite = fetchProperty("bukkit.plugin.website", "")
        val pluginMainClass = fetchProperty("bukkit.plugin.main", "")

        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "pluginName" to pluginName,
                    "pluginPrefix" to pluginPrefix,
                    "pluginDescription" to pluginDescription,
                    "pluginWebsite" to pluginWebsite,
                    "pluginMainClass" to pluginMainClass,
                    "pluginVersion" to version
                )
            )
        }

        filesMatching("config.yml") {
            expand(
                mapOf(
                    "pluginVersion" to version
                )
            )
        }
    }

    named("build") {
        dependsOn("shadowJar")
    }
}
