allprojects {
    repositories {
        mavenCentral()
    }
}

plugins {
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
}
