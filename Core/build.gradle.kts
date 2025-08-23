plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "me.adamix.mercury.core"
version = "0.0.1"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
    implementation("org.tomlj:tomlj:1.1.1")
    implementation("com.github.AdamBurdik.MercuryConfiguration:api:02dc08376f")
    implementation("com.github.AdamBurdik.MercuryConfiguration:toml:02dc08376f")
}

tasks.processResources {
    // Replaces ${version} from plugin.yml to a project version, defined above.
    filesMatching("plugin.yml") {
        expand(
            mapOf(
                "version" to project.version
            )
        )
    }
}

tasks.jar {
    archiveBaseName = "MercuryCore"
    archiveVersion = ""
}

tasks.shadowJar {
    archiveBaseName = "MercuryCore"
    archiveClassifier = ""
    archiveVersion.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}