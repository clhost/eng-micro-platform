rootProject.name = "eng-micro-platform"

// libraries

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

include(":outbox")
project(":outbox").projectDir = File("libraries/outbox")

// platform

include(":eng")
project(":eng").projectDir = File("platform/eng")
