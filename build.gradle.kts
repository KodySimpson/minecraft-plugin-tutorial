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
    compileOnly("io.papermc.paper:paper-api:26.3.build.135-beta")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    runServer {
        minecraftVersion("26.3")
        build(135)
    }
}
