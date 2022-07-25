plugins {
    idea
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
    paperDevBundle("${Dependency.Paper.Version}-R0.1-SNAPSHOT")
}

extra.apply {
    set("pluginName", project.name.split('-').joinToString("") { it.capitalize() })
    set("packageName", project.name.replace("-", ""))
    set("kotlinVersion", Dependency.Kotlin.Version)
    set("paperVersion", Dependency.Paper.Version)
}

fun TaskContainerScope.registerUpdateTask(name: String, suffix: String, source: Any) = register<Copy>(name) {
    val prefix = project.name
    val plugins = file(".server/plugins-$suffix")
    val update = File(plugins, "update")
    val regex = Regex("($prefix).*(.jar)")

    from(source)
    into(if (plugins.listFiles { _, it -> it.matches(regex) }?.isNotEmpty() == true) update else plugins)

    doFirst { update.deleteRecursively() }
    doLast {
        update.mkdirs()
        File(update, "UPDATE").createNewFile()
    }
}

tasks {
    // generate plugin.yml
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    val dev = registerUpdateTask("testDevJar", "dev", jar)
    val reobf = registerUpdateTask("testReobfJar", "reobf", reobfJar)

    register("testJar") {
        dependsOn(dev, reobf)
    }
}

idea {
    module {
        excludeDirs.add(file(".server"))
    }
}
