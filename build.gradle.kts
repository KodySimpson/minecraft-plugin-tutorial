plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "dev.kodysimpson"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.151-beta")
    testImplementation("io.papermc.paper:paper-api:26.3.build.151-beta")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    test { useJUnitPlatform() }
    runServer {
        minecraftVersion("26.3")
        build(151)
    }
}
