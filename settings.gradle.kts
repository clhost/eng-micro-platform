rootProject.name = "eng-micro-platform"

pluginManagement {
    repositories {
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}

// libraries

include(":html-extensions")
project(":html-extensions").projectDir = File("libraries/html-extensions")

include(":ktor-extensions")
project(":ktor-extensions").projectDir = File("libraries/ktor-extensions")

include(":test-extensions")
project(":test-extensions").projectDir = File("libraries/test-extensions")

include(":stdlib-extensions")
project(":stdlib-extensions").projectDir = File("libraries/stdlib-extensions")

include(":jackson-extensions")
project(":jackson-extensions").projectDir = File("libraries/jackson-extensions")

include(":spring-kafka-extensions")
project(":spring-kafka-extensions").projectDir = File("libraries/spring-kafka-extensions")

include(":spring-web-extensions")
project(":spring-web-extensions").projectDir = File("libraries/spring-web-extensions")

include(":faults-language")
project(":faults-language").projectDir = File("libraries/faults-language")

// platform

include(":eng")
project(":eng").projectDir = File("platform/eng")

include(":eng-cli")
project(":eng-cli").projectDir = File("platform/eng-cli")
