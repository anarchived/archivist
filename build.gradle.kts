plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("java")
    `kotlin-dsl`
    kotlin("jvm") version embeddedKotlinVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    named("build") {
        dependsOn(named("shadowJar"))
    }
    shadowJar {
        relocate("io.papermc.lib", "com.dfsek.terra.lib.paperlib")
        relocate("com.google.common", "com.dfsek.terra.lib.google.common")
        relocate("org.apache.logging.slf4j", "com.dfsek.terra.lib.slf4j-over-log4j")
        exclude("org/slf4j/**")
        exclude("org/checkerframework/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/intellij/**")
        exclude("com/google/errorprone/**")
        exclude("com/google/j2objc/**")
        exclude("javax/**")
    }
}

group = "org.crayne.archivist"
version = "1.0-SNAPSHOT"

tasks.withType<Jar> {
    destinationDirectory.set(file("/home/crayne/archivist/server/plugins"))
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
}