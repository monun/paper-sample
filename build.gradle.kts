plugins {
    kotlin("jvm") version Dependency.Kotlin.Version
    id("io.papermc.paperweight.userdev") version "1.3.8"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    paperDevBundle("1.19-R0.1-SNAPSHOT")
}

val pluginName = rootProject.name.split('-').joinToString("") { it.capitalize() }

extra.apply {
    set("pluginName", pluginName)
    set("packageName", rootProject.name.replace("-", ""))
    set("kotlinVersion", Dependency.Kotlin.Version)
}

tasks {
    // generate plugin.yml
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    register<Copy>("debugJar") {
        from(jar)

        val baseName = jar.get().archiveBaseName.get()
        val pluginsDirectory = File(".debug-server/plugins")
        val plugins = pluginsDirectory.listFiles { file: File -> file.isFile && file.name.endsWith(".jar") }
            ?: emptyArray()

        if (plugins.none { it.name.startsWith(baseName) }) into(plugins)
        else {
            val updateDirectory = File(pluginsDirectory, "update")
            into(updateDirectory)
            doLast {
                File(updateDirectory, "UPDATE").createNewFile()
            }
        }
    }
}

