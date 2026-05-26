import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
    id("com.gradleup.shadow") version("9.4.0")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.Bytephoria.byte-clans:bukkit-api:1.12.1")

    compileOnly("org.spongepowered:configurate-yaml:4.2.0")
    compileOnly("com.github.bytephoria.data-container:api:v1.2.0")
    compileOnly("com.github.bytephoria.data-container:binary:v1.2.0")
    compileOnly("com.github.bytephoria.data-container:serializers:v1.2.0")
    compileOnly("com.github.bytephoria.data-container:bukkit-serializers:v1.2.0")

    compileOnly("org.incendo:cloud-paper:2.0.0-beta.10")
    compileOnly("org.incendo:cloud-annotations:2.0.0")
    compileOnly("org.incendo:cloud-minecraft-extras:2.0.0-beta.10")
}

paper {
    name = getProjectName(rootProject.name)
    main = "${rootProject.group}.${rootProject.name.replace("-", "")}.PaperPlugin"
    description = rootProject.description
    version = rootProject.version.toString()
    apiVersion = "1.19"

    authors = listOf("Bytephoria", "iAmForyy_")
    website = "https://bytephoria.team"
    generateLibrariesJson = true
    foliaSupported = true

    serverDependencies {
        register("ByteClans") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("ByteClansMenu") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

    }

}

tasks {

    jar {
        enabled = false
    }

    generatePaperPluginDescription {
        useGoogleMavenCentralProxy()
    }

    shadowJar {
        archiveBaseName.set(getProjectName(rootProject.name))
        archiveVersion.set(rootProject.version.toString())
        archiveClassifier.set("")

    }

}

/**
 * Converts a hyphen-separated project name into PascalCase.
 */
fun getProjectName(baseName: String): String {
    return baseName.split("-")
        .joinToString("") {
                part -> part.replaceFirstChar {
            it.uppercase()
        }
        }
}