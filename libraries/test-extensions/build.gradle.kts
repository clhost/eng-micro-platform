plugins {
    val kotlinVersion = "1.8.20"
    id("com.dorongold.task-tree") version "1.5"
    kotlin("jvm") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
            allWarningsAsErrors = true
        }
    }
    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
            allWarningsAsErrors = true
        }
    }
    jar {
        enabled = true
    }
}

dependencies {
    api("io.mockk:mockk:1.13.4")
    api("org.hamcrest:hamcrest:2.2")
    api("com.jayway.jsonpath:json-path:2.7.0")
    api("com.jayway.jsonpath:json-path-assert:2.7.0")
    api("org.awaitility:awaitility:4.2.0")
    api("org.awaitility:awaitility-kotlin:4.2.0")

    compileOnly("org.apache.kafka:kafka-clients:3.4.0")
    compileOnly("com.github.tomakehurst:wiremock-jre8-standalone:2.35.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
