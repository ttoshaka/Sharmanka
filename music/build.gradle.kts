import java.net.URI

plugins {
    kotlin("jvm") version "2.2.0"
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
    maven(url = "https://maven.lavalink.dev/releases")
}

dependencies {
    implementation("com.github.devoxin.lavaplayer:lavaplayer:1.10.0")
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("io.mockk:mockk:1.13.10")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}