plugins {
    val kotlinVersion = "1.8.20"
    id("com.dorongold.task-tree") version "1.5"
    kotlin("jvm") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.0"
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
    bootJar {
        enabled = false
    }
}

dependencies {
    implementation(project(":jackson-extensions"))

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("ch.qos.logback:logback-core:1.4.7")
    implementation("ch.qos.logback:logback-classic:1.4.7")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(project(":test-extensions"))

    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("io.kotest:kotest-property:5.5.5")
}
