import java.net.URI

plugins {
    kotlin("jvm") version "2.2.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = URI("https://m2.dv8tion.net/releases")
    }
    maven {
        url = URI("https://jitpack.io")
    }
    maven {
        name = "TarsosDSP repository"
        url = URI("https://mvn.0110.be/releases")
    }
    maven(url = "https://maven.lavalink.dev/releases")
}

tasks.withType<Tar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Zip>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation(project(mapOf("path" to ":network")))
    implementation(project(mapOf("path" to ":music")))

    implementation("com.github.devoxin.lavaplayer:lavaplayer:1.10.0")
    implementation("net.dv8tion:JDA:6.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("dev.lavalink.youtube:common:1.18.0")

    implementation("be.tarsos.dsp:core:2.5")
    implementation("be.tarsos.dsp:jvm:2.5")

    implementation("ai.picovoice:porcupine-java:4.0.0")

    implementation("moe.kyokobot.libdave:adapter-jda:0.1.2")
// Реализация libdave-jvm через JNI
    implementation("moe.kyokobot.libdave:impl-jni:0.1.2")
// Нативные библиотеки под вашу ОС и архитектуру (выберите одну)
// Для Windows x64:
    runtimeOnly("moe.kyokobot.libdave:natives-win-x86-64:0.1.3")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("io.mockk:mockk:1.13.10")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("core.MainKt")
}