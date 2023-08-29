import org.codehaus.groovy.runtime.ProcessGroovyMethods

plugins {
    application
    `maven-publish`
    val kotlinVersion = "1.8.20"
    id("com.dorongold.task-tree") version "1.5"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("org.mikeneck.graalvm-native-image") version "v1.4.0"
}

dependencies {
    implementation(project(":eng-language"))

    implementation("com.github.ajalt.clikt:clikt:3.5.2")
    implementation("com.github.tomas-langer:chalk:1.0.2")
    implementation("com.charleskorn.kaml:kaml:0.53.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

application {
    applicationName = "eng"
    mainClass.set("io.clhost.tooling.eng.EngKt")
}

nativeImage {
    graalVmHome = System.getenv("GRAALVM_HOME") ?: System.getenv("JAVA_HOME")
    executableName = "eng"
    buildType { build -> build.executable(main = "io.clhost.tooling.eng.EngKt") }
    arguments {
        add("--no-fallback")
        add("--allow-incomplete-classpath")
        add("-H:ReflectionConfigurationFiles=reflection.json")
    }
    outputDirectory = file("$buildDir/native")
    finalizedBy("nativeExecutablePackaging")
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
            jvmTarget = "17"
            allWarningsAsErrors = true
        }
        finalizedBy("sourcePackaging")
    }
    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
            jvmTarget = "17"
            allWarningsAsErrors = true
        }
    }
    jar {
        enabled = true
    }
    register<Zip>("sourcePackaging") {
        archiveFileName.set("sources.zip")
        destinationDirectory.set(layout.buildDirectory.dir("sources"))
        from(layout.buildDirectory.dir("resources/main"))
    }
    register<Zip>("nativeExecutablePackaging") {
        archiveFileName.set("eng.zip")
        destinationDirectory.set(layout.buildDirectory.dir("artifact"))
        from(layout.buildDirectory.dir("native"))
    }
}

val nativeImageFile = layout.buildDirectory.file("artifact/eng.zip")
val nativeImageArtifact = artifacts.add("archives", nativeImageFile.get().asFile) {
    type = "binary"
}

val sourcesDirectory = layout.buildDirectory.file("sources/sources.zip")
val sourcesArtifact = artifacts.add("archives", sourcesDirectory.get()) {
    type = "raw-sources"
    classifier = "sources"
}

fun platform(): String {
    fun String.execute(): Process = ProcessGroovyMethods.execute(this)
    fun Process.text(): String = ProcessGroovyMethods.getText(this)
    val platform: String = "uname".execute().text().trim()
    return if ("darwin".equals(platform, ignoreCase = true)) "macos" else "linux"
}

publishing {
    repositories {
        mavenCentral()
    }
    publications {
        register("maven", MavenPublication::class) {
            artifact(nativeImageArtifact)
            artifact(sourcesArtifact)
            artifactId = "$artifactId-${platform()}"
        }
    }
}
