import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.net.URI
import java.util.*

plugins {
    id("fabric-loom")
    id("io.github.juuxel.loom-vineflower").version("1.11.0")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
    id("pl.allegro.tech.build.axion-release").version("1.15.4")
}

base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}

/*
    * calculating revision number for the version
 */
fun isWindows() = System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")
fun getCommitsSinceLastTag(): String {
    val result = ByteArrayOutputStream()

    // 获取最后的tag
    val lastTag = ByteArrayOutputStream()
    val errorResult = ByteArrayOutputStream()
    exec {
        if (isWindows()) {
            commandLine("powershell", " –noprofile", "/c", "git describe --tags --abbrev=0")
        } else {
            commandLine("sh", "-c", "git describe --tags --abbrev=0")
        }
        standardOutput = lastTag
        errorOutput = errorResult
        isIgnoreExitValue = true
    }

    val lastTagName = lastTag.toString().trim()
    if (lastTagName.isEmpty()) {
        return "0"
    }

    // 计算commits
    exec {
        if (isWindows()) {
            commandLine(
                "powershell",
                " –noprofile",
                "/c",
                "git rev-list --count HEAD ^$(git describe --tags --abbrev=0)"
            )
        } else {
            commandLine("sh", "-c", "git rev-list --count HEAD ^$(git describe --tags --abbrev=0)")
        }
        standardOutput = result
    }

    return result.toString().trim()
}

scmVersion {

    repository {
        type.set("git")
    }

    localOnly.set(true)
    ignoreUncommittedChanges.set(false)
    useHighestVersion.set(true)
    sanitizeVersion.set(true)

    tag {
        prefix.set("v")
        versionSeparator.set("")
    }

    nextVersion {
        suffix.set("alpha")
        separator.set("-")
    }

    versionIncrementer { version ->
        version.currentVersion
        // Don't increment the version, just use the current version
    }

    versionCreator { version, position ->
        val revision = getCommitsSinceLastTag()
        if (revision == "0") {
            return@versionCreator "${version}-b${position.branch}"
        } else {
            return@versionCreator "${version}-b${position.branch}-r${revision}"
        }
    }

    snapshotCreator { _, _ -> "" }

    checks {
        uncommittedChanges.set(true)
        aheadOfRemote.set(false)
    }
}

project.version = scmVersion.version
val mavenGroup: String by project
group = mavenGroup

repositories {
    maven { url = URI("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") }
}

dependencies {
    val minecraftVersion: String by project
    minecraft("com.mojang", "minecraft", minecraftVersion)
    val yarnMappings: String by project
    mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")
    val loaderVersion: String by project
    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion)
    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc", "fabric-language-kotlin", fabricKotlinVersion)

    val devAuthVersion: String by project
    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${devAuthVersion}")

}


tasks {

    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions { jvmTarget = javaVersion.toString() }
    }

    jar { from("LICENSE") }

    processResources {
        val minecraftVersion: String by project
        val loaderVersion: String by project
        val fabricKotlinVersion: String by project

        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version,
                    "minecraftVersion" to minecraftVersion,
                    "loaderVersion" to loaderVersion,
                    "fabricKotlinVersion" to fabricKotlinVersion,
                )
            )
        }
    }

    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }

    release {
        scmVersion {
            versionIncrementer("incrementPatch")
        }
    }
}